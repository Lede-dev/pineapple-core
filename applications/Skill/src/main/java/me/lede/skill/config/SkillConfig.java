package me.lede.skill.config;

import com.google.common.collect.Lists;
import me.lede.skill.Skill;
import me.lede.skill.objects.PlayerSkill;
import me.lede.stat.objects.PlayerStat;
import me.lede.stat.objects.StatType;
import me.lede.utils.color.Color;
import me.lede.utils.mson.Mson;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;
import java.util.Map;

public class SkillConfig {

    public enum Type {
        TIER_1,
        TIER_2,
        TIER_3,
        TIER_4,
        TIER_5,
        TIER_6,
        QUEST
        ;
    }

    public static class StatSkillContent {
        private Map<String, Integer> required;
        private List<String> skill;

        public int getRequiredLevel() {
            return required.getOrDefault("level", 0);
        }

        public int getRequiredStrength() {
            return required.getOrDefault("strength", 0);
        }

        public int getRequiredMagic() {
            return required.getOrDefault("magic", 0);
        }

        public int getRequiredAgility() {
            return required.getOrDefault("agility", 0);
        }

        public int getRequiredDeftness() {
            return required.getOrDefault("deftness", 0);
        }

        public int getRequiredNetherStar() {
            return required.getOrDefault("nether", 0);
        }

        public List<String> getSkill() {
            return skill;
        }

    }

    public static class StatSkill {

        private Map<Type, StatSkillContent> spells;

        public StatSkillContent getSpell(@NotNull Type type) {
            return spells.get(type);
        }

        public Map<Type, StatSkillContent> getSpells() {
            return spells;
        }

        @Nullable
        public Type getTypeByName(@NotNull String name) {
            for (Map.Entry<Type, StatSkillContent> entry : spells.entrySet()) {
                if (entry.getValue().getSkill().contains(name)) {
                    return entry.getKey();
                }
            }
            return null;
        }

        public boolean isSkillName(@NotNull String name) {
            if (spells == null) {
                // Bukkit.broadcastMessage("null spells");
                return false;
            }

            for (StatSkillContent value : spells.values()) {
                if (value == null) {
                    // Bukkit.broadcastMessage("null value");
                    continue;
                }

                if (value.getSkill().contains(name)) {
                    return true;
                }
            }
            return false;
        }

        public boolean isQuestSkillName(@NotNull String name) {
            return spells.get(Type.QUEST).getSkill().contains(name);
        }

    }

    private static SkillConfig config;

    public static void reloadConfig() {
        String filepath = getConfigPath();
        File file = new File(filepath);
        if (!file.exists()) {
            Skill.getInstance().saveResource("skill.json", false);
        }

        config = new Mson<>(filepath, SkillConfig.class).read().getData();
    }

    public static SkillConfig getConfig() {
        if (config == null) {
            reloadConfig();
        }
        return config;
    }

    public static String getConfigPath() {
        return getDataPath() + "/skill.json";
    }

    public static String getDataPath() {
        return Skill.getInstance().getDataFolder().getAbsolutePath();
    }

    private StatSkill strengthSkill;
    private StatSkill magicSkill;
    private StatSkill agilitySkill;
    private StatSkill deftnessSkill;

    public List<String> getSkillNames() {
        List<String> names = Lists.newArrayList();
        for (StatType type : StatType.values()) {
            StatSkill statSkill = getSkill(type);
            if (statSkill != null) {
                statSkill.getSpells().values().forEach(content ->
                        names.add(content.getSkill().get(0)));
            }
        }
        return names;
    }

    public void sendRequireMessage(CommandSender player, PlayerStat stat, @NotNull Type type, @NotNull StatType statType) {
        if (player == null) {
            return;
        }

        StatSkill statSkill = getStatSkill(statType);
        if (statSkill == null) {
            return;
        }

        String prefix = Config.getConfig().getPrefix();
        StatSkillContent content = statSkill.getSpell(type);
        if (content.getRequiredLevel() - stat.getStat(statType) > 0) {
            player.sendMessage(Color.colored(String.format(
                    prefix + "%s%s&f스텟이 &c%d&f개 부족합니다.",
                    statType.getColor(), statType.getName(),
                    content.getRequiredLevel() - stat.getStat(statType)
            )));
        }

        if (content.getRequiredStrength() - stat.getStat(StatType.STRENGTH) > 0) {
            player.sendMessage(Color.colored(String.format(
                    prefix + "%s%s&f스텟이 &c%d&f개 부족합니다.",
                    StatType.STRENGTH.getColor(), StatType.STRENGTH.getName(),
                    content.getRequiredStrength() - stat.getStat(StatType.STRENGTH)
            )));
        }

        if (content.getRequiredMagic() - stat.getStat(StatType.MAGIC) > 0) {
            player.sendMessage(Color.colored(String.format(
                    prefix + "%s%s&f스텟이 &c%d&f개 부족합니다.",
                    StatType.MAGIC.getColor(), StatType.MAGIC.getName(),
                    content.getRequiredMagic() - stat.getStat(StatType.MAGIC)
            )));
        }

        if (content.getRequiredAgility() - stat.getStat(StatType.AGILITY) > 0) {
            player.sendMessage(Color.colored(String.format(
                    prefix + "%s%s&f스텟이 &c%d&f개 부족합니다.",
                    StatType.AGILITY.getColor(), StatType.AGILITY.getName(),
                    content.getRequiredAgility() - stat.getStat(StatType.AGILITY)
            )));
        }

        if (content.getRequiredDeftness() - stat.getStat(StatType.DEFTNESS) > 0) {
            player.sendMessage(Color.colored(String.format(
                    prefix + "%s%s&f스텟이 &c%d&f개 부족합니다.",
                    StatType.DEFTNESS.getColor(), StatType.DEFTNESS.getName(),
                    content.getRequiredDeftness() - stat.getStat(StatType.DEFTNESS)
            )));
        }

        if (content.getRequiredNetherStar() - stat.getStat(StatType.NETHER_STAR) > 0) {
            player.sendMessage(Color.colored(String.format(
                    prefix + "%s%s&f스텟이 &c%d&f개 부족합니다.",
                    StatType.NETHER_STAR.getColor(), StatType.NETHER_STAR.getName(),
                    content.getRequiredNetherStar() - stat.getStat(StatType.NETHER_STAR)
            )));
        }
    }

    public boolean checkLearnable(PlayerStat stat, @NotNull Type type, @NotNull StatType statType) {
        StatSkill statSkill = getStatSkill(statType);
        if (statSkill == null) {
            return false;
        }

        StatSkillContent content = statSkill.getSpell(type);
        if (content.getRequiredLevel() > stat.getStat(statType)) {
            return false;
        }

        if (content.getRequiredStrength() > stat.getStat(StatType.STRENGTH)) {
            return false;
        }

        if (content.getRequiredMagic() > stat.getStat(StatType.MAGIC)) {
            return false;
        }

        if (content.getRequiredAgility() > stat.getStat(StatType.AGILITY)) {
            return false;
        }

        if (content.getRequiredDeftness() > stat.getStat(StatType.DEFTNESS)) {
            return false;
        }

        if (content.getRequiredNetherStar() > stat.getStat(StatType.NETHER_STAR)) {
            return false;
        }

        return true;
    }

    public StatSkill getSkill(@NotNull StatType type) {
        if (type == StatType.STRENGTH) {
            return strengthSkill;
        }

        if (type == StatType.MAGIC) {
            return magicSkill;
        }

        if (type == StatType.AGILITY) {
            return agilitySkill;
        }

        if (type == StatType.DEFTNESS) {
            return deftnessSkill;
        }

        return null;
    }

    public StatSkill getStrengthSkill() {
        return strengthSkill;
    }

    public StatSkill getMagicSkill() {
        return magicSkill;
    }

    public StatSkill getAgilitySkill() {
        return agilitySkill;
    }

    public StatSkill getDeftnessSkill() {
        return deftnessSkill;
    }

    @Nullable
    public StatType getStatTypeByName(@NotNull String name) {
        if (strengthSkill.isSkillName(name)) {
            return StatType.STRENGTH;
        }

        if (magicSkill.isSkillName(name)) {
            return StatType.MAGIC;
        }

        if (agilitySkill.isSkillName(name)) {
            return StatType.AGILITY;
        }

        if (deftnessSkill.isSkillName(name)) {
            return StatType.DEFTNESS;
        }

        return null;
    }

    @Nullable
    public Type getSkillTypeByName(@NotNull String name) {
        Type type = null;
        type = strengthSkill.getTypeByName(name);
        if (type != null) {
            return type;
        }

        type = magicSkill.getTypeByName(name);
        if (type != null) {
            return type;
        }

        type = agilitySkill.getTypeByName(name);
        if (type != null) {
            return type;
        }

        type = deftnessSkill.getTypeByName(name);
        return type;
    }

    @Nullable
    public StatSkill getStatSkill(@NotNull StatType statType) {
        if (statType == StatType.STRENGTH) {
            return strengthSkill;
        }

        if (statType == StatType.MAGIC) {
            return magicSkill;
        }

        if (statType == StatType.AGILITY) {
            return agilitySkill;
        }

        if (statType == StatType.DEFTNESS) {
            return deftnessSkill;
        }

        return null;
    }
    
    public boolean isSkillName(@NotNull String name) {
        return strengthSkill.isSkillName(name) || magicSkill.isSkillName(name)
                || agilitySkill.isSkillName(name) || deftnessSkill.isSkillName(name);
    }

    public boolean isQuestSkillName(@NotNull String name) {
        return strengthSkill.isQuestSkillName(name) || magicSkill.isQuestSkillName(name)
                || agilitySkill.isQuestSkillName(name) || deftnessSkill.isQuestSkillName(name);
    }

    public String getStrengthSkillName(@NotNull Type type) {
        return strengthSkill.getSpell(type).getSkill().get(0);
    }

    public String getMagicSkillName(@NotNull Type type) {
        return magicSkill.getSpell(type).getSkill().get(0);
    }

    public String getAgilitySkillName(@NotNull Type type) {
        return agilitySkill.getSpell(type).getSkill().get(0);
    }

    public String getDeftnessSkillName(@NotNull Type type) {
        return deftnessSkill.getSpell(type).getSkill().get(0);
    }

}
