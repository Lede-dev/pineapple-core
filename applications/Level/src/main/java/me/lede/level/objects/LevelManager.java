package me.lede.level.objects;

import com.google.common.collect.Maps;
import me.lede.utils.format.Formatter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class LevelManager {

    private static final Map<String, PlayerLevel> levelMap = Maps.newHashMap();

    public static void reloadPlayerLevel() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            loadPlayerLevel(player);
        }
    }

    public static void savePlayerLevel() {
        for (PlayerLevel level : levelMap.values()) {
            level.save();
        }
    }
    
    public static void loadPlayerLevel(@NotNull OfflinePlayer player) {
        PlayerLevel level = new PlayerLevel(player.getUniqueId().toString());
        levelMap.put(level.getUniqueId(), level);

        Player online = player.getPlayer();
        if (online != null) {
            level.getExperience().syncExpBar(online);
        }
    }

    public static void removePlayerLevel(@NotNull OfflinePlayer player) {
        levelMap.remove(player.getUniqueId().toString());
    }

    @NotNull
    public static PlayerLevel getPlayerLevel(@NotNull OfflinePlayer player) {
        String uuid = player.getUniqueId().toString();
        PlayerLevel level = levelMap.get(uuid);
        if (level == null) {
            level = new PlayerLevel(uuid);
        }
        return level;
    }

    public static Map<String, PlayerLevel> getPlayerLevelMap() {
        return Maps.newHashMap(levelMap);
    }

}
