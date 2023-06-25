package me.lede.level.objects;

import me.lede.level.config.Config;
import me.lede.level.events.PlayerLevelUpEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Experience {

    private transient OfflinePlayer player;
    private transient PlayerLevel playerLevel;

    private int level;
    private double exp;

    public Experience() {
        this.level = 1;
        this.exp = 0.0d;
    }

    public void addExp(double value) {
        if (isMaxLevel()) {
            level = getMaxLevel();
            exp = 0.0d;
            return;
        }

        exp += value;
        if (isLevelUpAvailable()) {
            level += 1;
            exp = Math.max(exp - getNextLevelRequiredExp(), 0);

            // 레벨업 이벤트 실행
            PlayerLevelUpEvent event = new PlayerLevelUpEvent(player, playerLevel);
            Bukkit.getPluginManager().callEvent(event);

            addExp(0.0d);
        }
    }

    public void removeExp(double value) {
        exp = Math.max(exp - value, 0);
    }

    public void setExp(double value) {
        exp = Math.max(value, 0);
        addExp(0.0d);
    }

    public double getExp() {
        return exp;
    }

    public void addLevel(int value) {
        level = Math.min(value, getMaxLevel());
    }

    public void removeLevel(int value) {
        level = Math.max(level - value, 0);
    }

    public void setLevel(int value) {
        level = Math.min(value, getMaxLevel());
    }

    public int getLevel() {
        return level;
    }

    public void syncExpBar(@NotNull Player player) {
        player.setLevel(level);

        if (isMaxLevel()) {
            player.setExp(0.0f);
        } else {
            double requiredExp = Config.getConfig().getRequiredExp(level + 1);
            float percentage = Math.min((float) (exp / requiredExp), 1.0f);
            player.setExp(percentage);
        }
    }

    public boolean isLevelUpAvailable() {
        double required = getNextLevelRequiredExp();
        return exp >= required;
    }

    public double getNextLevelRequiredExp() {
        if (isMaxLevel()) {
            return 0.0d;
        }

        return Config.getConfig().getRequiredExp(level + 1);
    }

    public boolean isMaxLevel() {
        return level >= getMaxLevel();
    }

    public int getMaxLevel() {
        return Config.getConfig().getMaxLevel();
    }

    public OfflinePlayer getPlayer() {
        return player;
    }

    public void setPlayer(OfflinePlayer player) {
        this.player = player;
    }

    public PlayerLevel getPlayerLevel() {
        return playerLevel;
    }

    public void setPlayerLevel(PlayerLevel playerLevel) {
        this.playerLevel = playerLevel;
    }

}
