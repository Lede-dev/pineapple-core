package me.lede.skill.objects;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.lede.skill.config.Config;
import me.lede.skill.config.SkillConfig;
import me.lede.stat.objects.PlayerStat;
import me.lede.stat.objects.StatManager;
import me.lede.stat.objects.StatType;
import me.lede.utils.color.Color;
import me.lede.utils.mson.Mson;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

public class PlayerSkill {

    private final String uuid;
    private Set<SkillConfig.Type> strengthSkill = Sets.newHashSet();
    private Set<SkillConfig.Type> magicSkill = Sets.newHashSet();
    private Set<SkillConfig.Type> agilitySkill = Sets.newHashSet();
    private Set<SkillConfig.Type> deftnessSkill = Sets.newHashSet();

    public PlayerSkill(@NotNull String uuid) {
        this.uuid = uuid;

        if (exists()) {
            load();
        } else {
            strengthSkill = Sets.newHashSet();
            magicSkill = Sets.newHashSet();
            agilitySkill = Sets.newHashSet();
            deftnessSkill = Sets.newHashSet();
            save();
        }
    }
    
    public void addAll() {
        boolean learnQuest = Config.getConfig().isLearnQuestSkill();
        for (SkillConfig.Type type : SkillConfig.Type.values()) {
            if (type == SkillConfig.Type.QUEST && !learnQuest) {
                continue;
            }

            strengthSkill.add(type);
            magicSkill.add(type);
            agilitySkill.add(type);
            deftnessSkill.add(type);
        }
        save();
    }

    public void addAll(@NotNull StatType statType) {
        boolean learnQuest = Config.getConfig().isLearnQuestSkill();
        List<SkillConfig.Type> types = Lists.newArrayList(SkillConfig.Type.values());
        if (!learnQuest) {
            types.remove(SkillConfig.Type.QUEST);
        }

        switch (statType) {
            case STRENGTH: strengthSkill.addAll(types); break;
            case MAGIC: magicSkill.addAll(types); break;
            case AGILITY: agilitySkill.addAll(types); break;
            case DEFTNESS: deftnessSkill.addAll(types); break;
        }
    }

    public boolean hasSkill(@NotNull String name) {
        try {
            SkillConfig config = SkillConfig.getConfig();
            StatType statType = Objects.requireNonNull(config.getStatTypeByName(name));
            SkillConfig.StatSkill statSkill = Objects.requireNonNull(config.getStatSkill(statType));
            SkillConfig.Type type = Objects.requireNonNull(statSkill.getTypeByName(name));
            return hasSkill(type, statType);
        } catch (Exception ignored) {}
        return false;
    }

    public boolean hasSkill(@NotNull SkillConfig.Type type, @NotNull StatType statType) {
        boolean has = false;
        switch (statType) {
            case STRENGTH: has = hasStrengthSkill(type); break;
            case MAGIC: has = hasMagicSkill(type); break;
            case AGILITY: has = hasAgilitySkill(type); break;
            case DEFTNESS: has = hasDeftnessSkill(type); break;
        }
        return has;
    }

    public void addSkill(@NotNull SkillConfig.Type type, @NotNull StatType statType) {
        switch (statType) {
            case STRENGTH: addStrengthSkill(type); break;
            case MAGIC: addMagicSkill(type); break;
            case AGILITY: addAgilitySkill(type); break;
            case DEFTNESS: addDeftnessSkill(type); break;
        }
    }

    public void removeSkill(@NotNull SkillConfig.Type type, @NotNull StatType statType) {
        removeSkill(type, statType, false);
    }

    public void removeSkill(@NotNull SkillConfig.Type type, @NotNull StatType statType, boolean notice) {
        switch (statType) {
            case STRENGTH: removeStrengthSkill(type); break;
            case MAGIC: removeMagicSkill(type); break;
            case AGILITY: removeAgilitySkill(type); break;
            case DEFTNESS: removeDeftnessSkill(type); break;
        }

        if (notice) {
            SkillConfig config = SkillConfig.getConfig();
            String name = config.getStatSkill(statType).getSpell(type).getSkill().get(0);

            Player player = Bukkit.getPlayer(UUID.fromString(uuid));
            if (player != null && player.isOnline()) {
                player.getPlayer().sendMessage(Color.colored(String.format(
                        Config.getConfig().getPrefix() + "%s%s&f스킬을 회수했습니다.",
                        statType.getColor(), name
                )));
            }
        }
    }


    public boolean hasStrengthSkill(@NotNull SkillConfig.Type type) {
        return strengthSkill.contains(type);
    }

    public boolean hasMagicSkill(@NotNull SkillConfig.Type type) {
        return magicSkill.contains(type);
    }

    public boolean hasAgilitySkill(@NotNull SkillConfig.Type type) {
        return agilitySkill.contains(type);
    }

    public boolean hasDeftnessSkill(@NotNull SkillConfig.Type type) {
        return deftnessSkill.contains(type);
    }

    public void addStrengthSkill(@NotNull SkillConfig.Type type) {
        strengthSkill.add(type);
    }

    public void addMagicSkill(@NotNull SkillConfig.Type type) {
        magicSkill.add(type);
    }

    public void addAgilitySkill(@NotNull SkillConfig.Type type) {
        agilitySkill.add(type);
    }

    public void addDeftnessSkill(@NotNull SkillConfig.Type type) {
        deftnessSkill.add(type);
    }

    public void removeStrengthSkill(@NotNull SkillConfig.Type type) {
        strengthSkill.remove(type);
    }

    public void removeMagicSkill(@NotNull SkillConfig.Type type) {
        magicSkill.remove(type);
    }

    public void removeAgilitySkill(@NotNull SkillConfig.Type type) {
        agilitySkill.remove(type);
    }

    public void removeDeftnessSkill(@NotNull SkillConfig.Type type) {
        deftnessSkill.remove(type);
    }

    public void learnSkill(@NotNull SkillConfig.Type type, @NotNull StatType statType) {
        String prefix = Config.getConfig().getPrefix();
        OfflinePlayer player = getOfflinePlayer();
        if (hasSkill(type, statType)) {
            if (player.isOnline()) {
                player.getPlayer().sendMessage(Color.colored(prefix + "&f이미 스킬을 배웠습니다."));
            }
            return;
        }

        if (type == SkillConfig.Type.QUEST) {
            if (player.isOnline()) {
                player.getPlayer().sendMessage(Color.colored(prefix + "&f스킬북을 사용하여 스킬을 배울 수 있습니다."));
            }
            return;
        }

        SkillConfig config = SkillConfig.getConfig();
        String name = config.getStatSkill(statType).getSpell(type).getSkill().get(0);

        PlayerStat stat = StatManager.getPlayerStat(player);
        if (config.checkLearnable(stat, type, statType)) {
            addSkill(type, statType);
            if (player.isOnline()) {
                player.getPlayer().sendMessage(Color.colored(String.format(
                        prefix + "%s%s&f스킬을 배웠습니다.",
                        statType.getColor(), name
                )));
            }
            save();
            return;
        }

        if (player.isOnline()) {
            config.sendRequireMessage(player.getPlayer(), stat, type, statType);
        }
    }

    public void learnSkillByAdmin(@NotNull CommandSender admin, @NotNull SkillConfig.Type type, @NotNull StatType statType) {
        String prefix = Config.getConfig().getPrefix();
        OfflinePlayer player = getOfflinePlayer();
        if (hasSkill(type, statType)) {
            admin.sendMessage(Color.colored(prefix + "&f이미 스킬을 배웠습니다."));
            return;
        }

        SkillConfig config = SkillConfig.getConfig();
        String name = config.getStatSkill(statType).getSpell(type).getSkill().get(0);

        PlayerStat stat = StatManager.getPlayerStat(player);
        if (config.checkLearnable(stat, type, statType)) {
            addSkill(type, statType);
            if (player.isOnline()) {
                player.getPlayer().sendMessage(Color.colored(String.format(
                        prefix + "관리자가 %s%s&f스킬을 지급하였습니다.", statType.getColor(), name)));
            }
            admin.sendMessage(Color.colored(String.format(
                    prefix + "권한으로 %s%s&f스킬을 지급하였습니다.", statType.getColor(), name)));
            save();
            return;
        }

        config.sendRequireMessage(admin, stat, type, statType);

//        if (player.isOnline()) {
//            player.getPlayer().sendMessage(Color.colored(String.format(
//                    prefix + "&c%s&f스텟이 &c%d&f개 부족합니다.",
//                    statType.getName(),
//                    required - hasLevel
//            )));
//        }
    }

    public void forceLearnSkillByAdmin(@NotNull CommandSender admin, @NotNull SkillConfig.Type type, @NotNull StatType statType) {
        String prefix = Config.getConfig().getPrefix();
        OfflinePlayer player = getOfflinePlayer();
        if (hasSkill(type, statType)) {
            admin.sendMessage(Color.colored(prefix + "&f이미 스킬을 배웠습니다."));
            return;
        }

        SkillConfig config = SkillConfig.getConfig();
        String name = config.getStatSkill(statType).getSpell(type).getSkill().get(0);

        addSkill(type, statType);
        admin.sendMessage(Color.colored(String.format(
                prefix + "권한으로 %s%s&f스킬을 지급하였습니다.", statType.getColor(), name)));
        if (player.isOnline()) {
            player.getPlayer().sendMessage(Color.colored(String.format(
                    prefix + "관리자가 %s%s&f스킬을 지급하였습니다.", statType.getColor(), name)));
        }
        save();
    }

    public void forceRemoveSkillByAdmin(@NotNull CommandSender admin, @NotNull SkillConfig.Type type, @NotNull StatType statType) {
        String prefix = Config.getConfig().getPrefix();
        OfflinePlayer player = getOfflinePlayer();
        if (!hasSkill(type, statType)) {
            admin.sendMessage(Color.colored(prefix + "&f배우지 않은 스킬입니다."));
            return;
        }

        SkillConfig config = SkillConfig.getConfig();
        String name = config.getStatSkill(statType).getSpell(type).getSkill().get(0);

        removeSkill(type, statType);
        admin.sendMessage(Color.colored(String.format(
                prefix + "&f권한으로 %s%s&f스킬을 회수하였습니다.", statType.getColor(), name)));
        if (player.isOnline()) {
            player.getPlayer().sendMessage(Color.colored(
                    String.format(prefix + "&f관리자&f가 %s%s&f스킬을 회수하였습니다.", statType.getColor(), name)));
        }
        save();
    }


    public boolean findSkillFromName(@NotNull String name) {
        if (name.isEmpty()) {
            return false;
        }

        SkillConfig config = SkillConfig.getConfig();
        if (!config.isSkillName(name)) {
            return false;
        }

        for (SkillConfig.Type type : strengthSkill) {
            String skill = config.getStrengthSkillName(type);
            if (skill != null && skill.equalsIgnoreCase(name)) {
                return true;
            }
        }

        for (SkillConfig.Type type : magicSkill) {
            String skill = config.getMagicSkillName(type);
            if (skill != null && skill.equalsIgnoreCase(name)) {
                return true;
            }
        }

        for (SkillConfig.Type type : agilitySkill) {
            String skill = config.getAgilitySkillName(type);
            if (skill != null && skill.equalsIgnoreCase(name)) {
                return true;
            }
        }

        for (SkillConfig.Type type : deftnessSkill) {
            String skill = config.getDeftnessSkillName(type);
            if (skill != null && skill.equalsIgnoreCase(name)) {
                return true;
            }
        }

        return false;
    }

    public String getUniqueId() {
        return uuid;
    }

    public Set<SkillConfig.Type> getStrengthSkill() {
        return strengthSkill;
    }

    public Set<SkillConfig.Type> getMagicSkill() {
        return magicSkill;
    }

    public Set<SkillConfig.Type> getAgilitySkill() {
        return agilitySkill;
    }

    public Set<SkillConfig.Type> getDeftnessSkill() {
        return deftnessSkill;
    }

    public void reset() {
        strengthSkill = Sets.newHashSet();
        magicSkill = Sets.newHashSet();
        agilitySkill = Sets.newHashSet();
        deftnessSkill = Sets.newHashSet();
        save();
    }

    public void load() {
        PlayerSkill skill = new Mson<>(getSavePath(), PlayerSkill.class).read().getData();
        strengthSkill = skill.getStrengthSkill();
        magicSkill = skill.getMagicSkill();
        agilitySkill = skill.getAgilitySkill();
        deftnessSkill = skill.getDeftnessSkill();
    }

    public void save() {
        new Mson<>(getSavePath(), this).write();
    }


    public boolean exists() {
        return new File(getSavePath()).exists();
    }

    public String getSavePath() {
        return Config.getDataPath() + "/saves/" + uuid + ".json";
    }

    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(UUID.fromString(uuid));
    }

}
