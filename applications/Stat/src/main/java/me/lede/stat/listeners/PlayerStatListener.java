package me.lede.stat.listeners;

import me.lede.level.events.PlayerLevelUpEvent;
import me.lede.level.objects.PlayerLevel;
import me.lede.stat.Stat;
import me.lede.stat.config.Config;
import me.lede.stat.objects.StatEditorMode;
import me.lede.stat.objects.StatManager;
import me.lede.stat.objects.StatType;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerStatListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        StatManager.loadPlayerStat(player);
        StatManager.setStatEditorMode(player, StatEditorMode.NORMAL);

        me.lede.stat.objects.PlayerStat stat = StatManager.getPlayerStat(player);
        Bukkit.getScheduler().scheduleSyncDelayedTask(Stat.getInstance(), stat::applyStat, 10);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        StatManager.removePlayerStat(player);
        StatManager.removeStatEditorMode(player);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        me.lede.stat.objects.PlayerStat stat = StatManager.getPlayerStat(player);
        Bukkit.getScheduler().scheduleSyncDelayedTask(Stat.getInstance(), stat::applyStat, 10);
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        me.lede.stat.objects.PlayerStat stat = StatManager.getPlayerStat(player);
        Bukkit.getScheduler().scheduleSyncDelayedTask(Stat.getInstance(), stat::applyStat, 10);
    }

    @EventHandler
    public void onLevelUp(PlayerLevelUpEvent event) {
        OfflinePlayer player = event.getPlayer();
        PlayerLevel level = event.getPlayerLevel();
        me.lede.stat.objects.PlayerStat stat = StatManager.getPlayerStat(player);
        stat.addStat(StatType.ABILITY, Config.getConfig().getStatPoint(level.getExperience().getLevel()));
        stat.save();
    }

}
