package me.lede.stat;

import me.lede.stat.commands.PassiveAdminCommand;
import me.lede.stat.commands.PassiveCommand;
import me.lede.stat.commands.StatAdminCommand;
import me.lede.stat.commands.StatCommand;
import me.lede.stat.config.Config;
import me.lede.stat.listeners.DamageListener;
import me.lede.stat.listeners.PlayerStatListener;
import me.lede.stat.listeners.StatMenuListener;
import me.lede.stat.listeners.StatTicketListener;
import me.lede.stat.objects.StatEditorMode;
import me.lede.stat.objects.StatManager;
import me.lede.utils.registry.CommandRegistry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Stat extends JavaPlugin {

    private static Stat instance;

    public static Stat getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        Config.reloadConfig();

        registerListener();
        registerCommands();

        loadOnlinePlayerStat();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void registerListener() {
        PluginManager manager = Bukkit.getPluginManager();
        manager.registerEvents(new PlayerStatListener(), this);
        manager.registerEvents(new DamageListener(), this);
        manager.registerEvents(new StatTicketListener(), this);
        manager.registerEvents(new StatMenuListener(), this);
    }

    private void registerCommands() {
        CommandRegistry.register(new StatCommand());
        CommandRegistry.register(new StatAdminCommand());
        CommandRegistry.register(new PassiveAdminCommand());
        CommandRegistry.register(new PassiveCommand());
    }

    private void loadOnlinePlayerStat() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            StatManager.loadPlayerStat(player);
            StatManager.setStatEditorMode(player, StatEditorMode.NORMAL);

            me.lede.stat.objects.PlayerStat stat = StatManager.getPlayerStat(player);
            Bukkit.getScheduler().scheduleSyncDelayedTask(Stat.getInstance(), stat::applyStat, 10);
        }
    }

}
