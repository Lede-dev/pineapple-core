package me.lede.skill.listeners;

import me.lede.skill.config.Config;
import me.lede.skill.objects.PlayerSkill;
import me.lede.skill.objects.SkillManager;
import me.lede.stat.api.event.StatResetEvent;
import me.lede.stat.objects.PlayerStat;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public class StatResetListener implements Listener {

    @EventHandler
    public void onResetStat(StatResetEvent event) {
        if (Config.getConfig().isResetSkill()) {
            PlayerStat stat = event.getStat();
            PlayerSkill skill = SkillManager.getPlayerSkill(Bukkit.getOfflinePlayer(UUID.fromString(stat.getUniqueId())));
            skill.reset();
        }
    }

}
