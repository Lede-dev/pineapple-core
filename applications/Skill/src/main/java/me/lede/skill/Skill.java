package me.lede.skill;

import me.lede.skill.commands.SkillAdminCommand;
import me.lede.skill.commands.SkillCastCommand;
import me.lede.skill.commands.SkillCommand;
import me.lede.skill.config.Config;
import me.lede.skill.config.MenuConfig;
import me.lede.skill.config.SkillConfig;
import me.lede.skill.listeners.*;
import me.lede.skill.objects.SkillManager;
import me.lede.utils.registry.CommandRegistry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Skill extends JavaPlugin {

    private static Skill instance;

    public static Skill getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        Config.reloadConfig();
        SkillConfig.reloadConfig();
        MenuConfig.reloadConfig();

        registerCommands();
        registerListeners();

        loadOnlinePlayerSkill();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void registerCommands() {
        CommandRegistry.register(new SkillCommand());
        CommandRegistry.register(new SkillAdminCommand());
        CommandRegistry.register(new SkillCastCommand());
    }

    private void registerListeners() {
        PluginManager manager = Bukkit.getPluginManager();
        manager.registerEvents(new PlayerSkillListener(), this);
        manager.registerEvents(new PlayerSkillMainMenuListener(), this);
        manager.registerEvents(new PlayerSkillMenuListener(), this);
        manager.registerEvents(new SkillTicketListener(), this);
        manager.registerEvents(new StatResetListener(), this);
    }

    private void loadOnlinePlayerSkill() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            SkillManager.loadPlayerSkill(player);
        }
    }

}
