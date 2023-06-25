package me.lede.stat.objects;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.lede.stat.config.Config;
import me.lede.utils.mson.Mson;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;
import java.util.Map;

public class StatManager {

    private static boolean DEBUG = false;

    public static final String PATH_DB = Config.getDataPath() + "/saves";

    private static final Map<String, PlayerStat> playerStatMap = Maps.newHashMap();
    private static final Map<String, StatEditorMode> modes = Maps.newHashMap();

    public static boolean isDebug() {
        return DEBUG;
    }

    public static void setDebug(boolean state) {
        DEBUG = state;
    }

    public static List<PlayerStat> getPlayerStatList() {
        return Lists.newArrayList(playerStatMap.values());
    }

    public static void applyAllStat() {
        for (PlayerStat stat : playerStatMap.values()) {
            stat.applyStat();
        }
    }

    public static void resetStatAll() {
        for (PlayerStat stat : getAllStat()) {
            stat.resetAll();
            stat.save();
        }

        for (PlayerStat stat : playerStatMap.values()) {
            stat.load();
        }
    }

    public static void applyStatAll() {
        for (PlayerStat stat : getAllStat()) {
            stat.applyStat();
        }
    }

    public static void setEnabledAll(boolean enabled) {
        for (PlayerStat stat : getAllStat()) {
            stat.setEnabled(enabled);
            stat.applyStat();
            stat.save();
        }

        for (PlayerStat stat : playerStatMap.values()) {
            stat.load();
            stat.applyStat();
        }
    }
    
    public static void resetAllPassive() {
        for (PlayerStat stat : getAllStat()) {
            stat.setPassiveLevel(0);
            stat.applyStat();
            stat.save();
        }

        for (PlayerStat stat : playerStatMap.values()) {
            stat.load();
            stat.applyStat();
        }
    }
    
    public static List<PlayerStat> getAllStat() {
        List<PlayerStat> stats = Lists.newArrayList();
        File dir = new File(PATH_DB);
        String[] files = dir.list();
        if (files == null) {
            return stats;
        }

        for (String name : files) {
            try {
                PlayerStat stat = new Mson<>(PATH_DB + "/" + name, PlayerStat.class).read().getData();
                stats.add(stat);
            } catch (Exception ignored) {}
        }
        return stats;
    }

    public static void loadPlayerStat(@NotNull OfflinePlayer player) {
        String uuid = player.getUniqueId().toString();
        playerStatMap.put(uuid, new PlayerStat(uuid));
    }

    public static void removePlayerStat(@NotNull OfflinePlayer player) {
        String uuid = player.getUniqueId().toString();
        playerStatMap.remove(uuid);
    }

    @NotNull
    public static PlayerStat getPlayerStat(@NotNull OfflinePlayer player) {
        String uuid = player.getUniqueId().toString();
        PlayerStat stat = playerStatMap.get(uuid);
        return (stat == null) ? new PlayerStat(uuid) : stat;
    }

    public static void setStatEditorMode(@NotNull Player player, @NotNull StatEditorMode mode) {
        String uuid = player.getUniqueId().toString();
        modes.put(uuid, mode);
    }

    public static void removeStatEditorMode(@NotNull Player player) {
        String uuid = player.getUniqueId().toString();
        modes.remove(uuid);
    }

    @NotNull
    public static StatEditorMode getStatEditorMode(@NotNull Player player) {
        String uuid = player.getUniqueId().toString();
        return modes.computeIfAbsent(uuid, k -> StatEditorMode.NORMAL);
    }

}
