package me.lede.stat.objects;

import com.google.common.collect.Maps;
import me.lede.level.api.LevelAPI;
import me.lede.stat.api.event.StatResetEvent;
import me.lede.stat.config.Config;
import me.lede.stat.config.Debug;
import me.lede.utils.mson.Mson;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Map;
import java.util.UUID;

public class PlayerStat {

    private final String uuid;
    private boolean enabled;
    private Map<StatType, Integer> status;
    private int passiveLevel;

    public PlayerStat(@NotNull String uuid) {
        this.uuid = uuid;
        if (exists()) {
            load();
        } else {
            this.enabled = true;
            this.status = Maps.newHashMap();
            this.passiveLevel = 0;

            OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
            int level = LevelAPI.getLevel(player);
            if (level > 0) {
                this.status.put(StatType.ABILITY, level * Config.getConfig().getStatPoint());
            }
            save();
        }
    }

    public double getArrowDamage(float power) {
        if (!enabled) {
            return 0d;
        }

        Config config = Config.getConfig();
        boolean enableStar = config.isNetherStarStatEnabled() && config.isArrowNetherStatDamageEnabled();

        double strength = getStat(StatType.STRENGTH) * config.getStrengthDamage();
        double agility = getStat(StatType.AGILITY) * config.getAgilityDamage();
        double deftness = getStat(StatType.DEFTNESS) * config.getDeftnessDamage();

        double damage = strength + agility + deftness;
        double starDamage = enableStar ? damage + Math.max(0, damage / 100 * getNetherStarStatPercentage()) : damage;
        double finalDamage = starDamage * power;

        if (StatManager.isDebug()) {
            Debug.sendArrowDamageDebugMessage(
                    damage, power, starDamage,
                    finalDamage, enableStar, getNetherStarStatPercentage()
            );
        }

        return finalDamage;
    }

    public double getAttackDamage(double origin) {
        if (!enabled) {
            return origin;
        }

        Config config = Config.getConfig();
        boolean enableStar = config.isNetherStarStatEnabled();

        double strength = getStat(StatType.STRENGTH) * config.getStrengthDamage();
        double agility = getStat(StatType.AGILITY) * config.getAgilityDamage();
        double deftness = getStat(StatType.DEFTNESS) * config.getDeftnessDamage();

        double damage = strength + agility + deftness;
        double finalDamage = config.isNetherStarStatEnabled() ?
                damage + Math.max(0, damage / 100 * getNetherStarStatPercentage()) : damage;

        if (StatManager.isDebug()) {
            Debug.sendAttackDamageDebugMessage(origin, damage, finalDamage, enableStar, getNetherStarStatPercentage());
        }

        return origin + finalDamage;
    }

    public float getMagicModifier(double origin) {
        if (!enabled) {
            return 1.0f;
        }

        Config config = Config.getConfig();
        boolean enableStar = config.isNetherStarStatEnabled();

        int magic = getStat(StatType.MAGIC);
        int level = magic / 10;

        double damage = magic * config.getMagicDamage();
        double damagePercentage = level * config.getMagicSkill();

        double additionDamage = enableStar ? damage * (1 + (getNetherStarStatPercentage() / 100)) : damage;
        double additionDamagePercentage = enableStar ? damagePercentage * (1 + (getNetherStarStatPercentage() / 100)) : damagePercentage;

        double finalDamage = (origin + additionDamage) * (1 + (additionDamagePercentage / 100));

        float finalPercentage = (float) (finalDamage / origin);

        if (StatManager.isDebug()) {
            Debug.sendMagicDamageDebugMessage(
                    origin, damage, damagePercentage, finalDamage, additionDamage,
                    additionDamagePercentage, finalPercentage, enableStar, getNetherStarStatPercentage()
            );
        }

        return finalPercentage;
    }

    public boolean hasStat(@NotNull StatType type, int value) {
        return getStat(type) >= value;
    }

    public void addStat(@NotNull StatType type, int value) {
        int hasValue = getStat(type);
        setStat(type, hasValue + value);
    }

    public void removeStat(@NotNull StatType type, int value) {
        int hasValue = getStat(type);
        setStat(type, hasValue - value);
    }

    public int getStat(@NotNull StatType type) {
        return status.computeIfAbsent(type, k -> 0);
    }

    public void setStat(@NotNull StatType type, int value) {
        value = Math.max(value, 0);
        value = Math.min(value, Config.getConfig().getMaxStat(type));
        status.put(type, value);
    }

    public boolean isMaxValue(@NotNull StatType type) {
        return getStat(type) >= Config.getConfig().getMaxStat(type);
    }

    public void reset() {
        int strength = getStat(StatType.STRENGTH);
        int magic = getStat(StatType.MAGIC);
        int agility = getStat(StatType.AGILITY);
        int deftness = getStat(StatType.DEFTNESS);

        int ability = strength + magic + agility + deftness;
        this.status.put(StatType.STRENGTH, 0);
        this.status.put(StatType.MAGIC, 0);
        this.status.put(StatType.AGILITY, 0);
        this.status.put(StatType.DEFTNESS, 0);
        this.status.put(StatType.ABILITY, this.status.getOrDefault(StatType.ABILITY, 0) + ability);

        this.passiveLevel = 0;
        applyStat();

        StatResetEvent event = new StatResetEvent(this);
        Bukkit.getPluginManager().callEvent(event);
    }

    public void resetNetherStar() {
        this.status.put(StatType.NETHER_STAR, 0);
        applyStat();
    }

    public void resetAll() {
        this.status = Maps.newHashMap();
        this.passiveLevel = 0;
    }

    public void load() {
        PlayerStat stat = new Mson<>(getSavePath(), PlayerStat.class).read().getData();
        this.enabled = stat.isEnabled();
        this.status = stat.getStatus();
        this.passiveLevel = stat.getPassiveLevel();
    }

    public void save() {
        new Mson<>(getSavePath(), this).write();
    }

    public void applyStat() {
        Player player = Bukkit.getPlayer(UUID.fromString(uuid));
        if (player != null) {
            if (enabled) {
                applyHealth(player);
                applySpeed(player);
                applyAttackDamage(player);
            } else {
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20.0d);
                player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(1.0d);
                player.setWalkSpeed(0.2f);
            }
        }
    }

    private void applyHealth(@NotNull Player player) {
        double health = 20 + getAdditionalHealth();
        player.setHealthScaled(false);
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
    }

    private void applySpeed(@NotNull Player player) {
        float speed = 0.2f + getAdditionalSpeed();
        player.setWalkSpeed(speed);
    }

    private void applyAttackDamage(@NotNull Player player) {
        double damage = getAttackDamage(1.0d);
        player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(damage);
    }

    public double getAdditionalHealth() {
        Config config = Config.getConfig();
        boolean enableStar = config.isNetherStarStatEnabled();

        double strength = config.getStrengthHealth() * getStat(StatType.STRENGTH);
        double magic = config.getMagicHealth() * getStat(StatType.MAGIC);
        double agility = config.getAgilityHealth() * getStat(StatType.AGILITY);
        double deftness = config.getDeftnessHealth() * getStat(StatType.DEFTNESS);

        double health = strength + magic + agility + deftness;
        return enableStar ? Math.max(0, health * (1 + (getNetherStarStatPercentage() / 100))) : health;
    }

    public float getAdditionalSpeed() {
        Config config = Config.getConfig();
        boolean enableStar = config.isNetherStarStatEnabled() && config.isNetherStarSpeedEnabled();

        double agility = getStat(StatType.AGILITY);
        double speed = 0.2 / 100 * (agility * config.getAgilitySpeed() * config.getSpeedIncreasePercentage());
        return (float) (enableStar ? Math.max(0, speed * (1 + (getNetherStarStatPercentage() / 100))) : speed);
    }

    public double getNetherStarStatPercentage() {
        return getStat(StatType.NETHER_STAR) * Config.getConfig().getNetherStarPercentage();
    }

    public double getPassiveDamageDecreasePercentage() {
        return passiveLevel * Config.getConfig().getPassivePercentage();
    }

    public boolean exists() {
        return new File(getSavePath()).exists();
    }

    public String getSavePath() {
        return Config.getDataPath() + "/saves/" + uuid + ".json";
    }

    public String getUniqueId() {
        return uuid;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enable) {
        this.enabled = enable;
    }

    public int getPassiveLevel() {
        return passiveLevel;
    }

    public void setPassiveLevel(int passiveLevel) {
        this.passiveLevel = passiveLevel;
    }

    public Map<StatType, Integer> getStatus() {
        return status;
    }

    @Nullable
    public Player getPlayer() {
        return Bukkit.getPlayer(UUID.fromString(uuid));
    }
}
