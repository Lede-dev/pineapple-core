package me.lede.level;

import me.lede.level.commands.ExpBuffCommand;
import me.lede.level.commands.ExpManageCommand;
import me.lede.level.commands.LevelCommand;
import me.lede.level.commands.LevelManageCommand;
import me.lede.level.config.Config;
import me.lede.level.listeners.ExpOrbListener;
import me.lede.level.listeners.ExperienceItemListener;
import me.lede.level.listeners.PlayerLevelListener;
import me.lede.level.listeners.XPCommandWrapper;
import me.lede.level.objects.LevelManager;
import me.lede.level.schedulers.ExperienceBuffScheduler;
import me.lede.utils.log.SimpleLog;
import me.lede.utils.registry.CommandRegistry;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class Level extends JavaPlugin {

    private static Level instance;

    public static Level getInstance() {
        return instance;
    }

    private final Logger logger = Bukkit.getLogger();

    @Override
    public void onEnable() {
        instance = this;

        Config.reloadConfig();
        LevelManager.reloadPlayerLevel();

        registerCommands();
        registerListeners();

        ExperienceBuffScheduler.start();
    }

    @Override
    public void onDisable() {
        ExperienceBuffScheduler.stop();
        LevelManager.savePlayerLevel();
    }

    private void registerCommands() {
        CommandRegistry.register(new ExpManageCommand());
        CommandRegistry.register(new LevelManageCommand());
        CommandRegistry.register(new ExpBuffCommand());
        CommandRegistry.register(new LevelCommand());
    }

    private void registerListeners() {
        PluginManager manager = Bukkit.getPluginManager();
        manager.registerEvents(new XPCommandWrapper(), this);
        manager.registerEvents(new PlayerLevelListener(), this);
        manager.registerEvents(new ExpOrbListener(), this);
        manager.registerEvents(new ExperienceItemListener(), this);
    }

}
