package me.lede.level.api;

import me.lede.level.Level;
import me.lede.level.config.Config;
import me.lede.level.objects.LevelManager;
import me.lede.level.objects.PlayerLevel;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public class LevelAPI {

    public static Plugin getPlugin() {
        return Level.getInstance();
    }

    @Range(from = 0, to = Integer.MAX_VALUE)
    public static int getStatByLevel() {
        return Config.getConfig().getStatByLevel();
    }

    @Range(from = 0, to = Integer.MAX_VALUE)
    public static int getMaxLevel() {
        return Config.getConfig().getMaxLevel();
    }

    public static double getRequiredExp(@Range(from = 0, to = Integer.MAX_VALUE) int level) {
        return Config.getConfig().getRequiredExp(level);
    }

    public static double getRemoveExpPercentage() {
        return Config.getConfig().getRemoveExpPercentage();
    }

    public static void reloadPlayerLevel() {
        LevelManager.reloadPlayerLevel();
    }

    public static PlayerLevel getPlayerLevel(@NotNull OfflinePlayer player) {
        return LevelManager.getPlayerLevel(player);
    }

    @Range(from = 0, to = Integer.MAX_VALUE)
    public static int getLevel(@NotNull OfflinePlayer player) {
        PlayerLevel playerLevel = LevelManager.getPlayerLevel(player);
        return playerLevel.getExperience().getLevel();
    }

    public static void setLevel(@NotNull OfflinePlayer player, @Range(from = 0, to = Integer.MAX_VALUE) int value) {
        PlayerLevel playerLevel = LevelManager.getPlayerLevel(player);
        playerLevel.getExperience().setLevel(value);
        playerLevel.save();
        syncPlayerExpToExpBar(player);
    }

    public static void addLevel(@NotNull OfflinePlayer player, @Range(from = 0, to = Integer.MAX_VALUE) int value) {
        PlayerLevel playerLevel = LevelManager.getPlayerLevel(player);
        playerLevel.getExperience().addLevel(value);
        playerLevel.save();
        syncPlayerExpToExpBar(player);
    }

    public static void removeLevel(@NotNull OfflinePlayer player, @Range(from = 0, to = Integer.MAX_VALUE) int value) {
        PlayerLevel playerLevel = LevelManager.getPlayerLevel(player);
        playerLevel.getExperience().removeLevel(value);
        playerLevel.save();
        syncPlayerExpToExpBar(player);
    }

    public static double getExp(@NotNull OfflinePlayer player) {
        PlayerLevel playerLevel = LevelManager.getPlayerLevel(player);
        return playerLevel.getExperience().getExp();
    }

    public static void setExp(@NotNull OfflinePlayer player, double value) {
        PlayerLevel playerLevel = LevelManager.getPlayerLevel(player);
        playerLevel.getExperience().setExp(value);
        playerLevel.save();
        syncPlayerExpToExpBar(player);
    }

    public static void addExp(@NotNull OfflinePlayer player, double value) {
        PlayerLevel playerLevel = LevelManager.getPlayerLevel(player);
        playerLevel.getExperience().addExp(value);
        playerLevel.save();
        syncPlayerExpToExpBar(player);
    }

    public static void removeExp(@NotNull OfflinePlayer player, double value) {
        PlayerLevel playerLevel = LevelManager.getPlayerLevel(player);
        playerLevel.getExperience().removeExp(value);
        playerLevel.save();
        syncPlayerExpToExpBar(player);
    }

    public static void syncPlayerExpToExpBar(@NotNull OfflinePlayer player) {
        Player online = player.getPlayer();
        if (online != null) {
            PlayerLevel playerLevel = LevelManager.getPlayerLevel(online);
            playerLevel.getExperience().syncExpBar(online);
        }
    }

}
