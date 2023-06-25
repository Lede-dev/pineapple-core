package me.lede.skill.objects;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.lede.skill.config.Config;
import me.lede.utils.mson.Mson;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;
import java.util.Map;

public class SkillManager {

    public static final String PATH_DB = Config.getDataPath() + "/saves";

    private static final Map<String, PlayerSkill> playerSkillMap = Maps.newHashMap();

    public static List<PlayerSkill> getPlayerSkillList() {
        return Lists.newArrayList(playerSkillMap.values());
    }

    public static void loadPlayerSkill(@NotNull OfflinePlayer player) {
        String uuid = player.getUniqueId().toString();
        playerSkillMap.put(uuid, new PlayerSkill(uuid));
    }

    public static void removePlayerSkill(@NotNull OfflinePlayer player) {
        String uuid = player.getUniqueId().toString();
        playerSkillMap.remove(uuid);
    }

    @NotNull
    public static PlayerSkill getPlayerSkill(@NotNull OfflinePlayer player) {
        String uuid = player.getUniqueId().toString();
        return playerSkillMap.computeIfAbsent(uuid, PlayerSkill::new);
    }

    public static void resetAll() {
        for (PlayerSkill playerSkill : playerSkillMap.values()) {
            playerSkill.reset();
        }

        for (PlayerSkill playerSkill : getAllSkill()) {
            playerSkill.reset();
        }
    }

    public static List<PlayerSkill> getAllSkill() {
        List<PlayerSkill> skills = Lists.newArrayList();
        File dir = new File(PATH_DB);
        String[] files = dir.list();
        if (files == null) {
            return skills;
        }

        for (String name : files) {
            try {
                PlayerSkill stat = new Mson<>(PATH_DB + "/" + name, PlayerSkill.class).read().getData();
                skills.add(stat);
            } catch (Exception ignored) {}
        }
        return skills;
    }

}
