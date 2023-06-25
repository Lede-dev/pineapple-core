package me.lede.level.listeners;

import me.lede.level.api.LevelAPI;
import me.lede.level.config.Config;
import me.lede.level.objects.Experience;
import me.lede.level.objects.ExperienceBuff;
import me.lede.level.objects.PlayerLevel;
import me.lede.utils.color.Color;
import me.lede.utils.format.Formatter;
import me.lede.utils.message.Prefix;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;

public class ExpOrbListener implements Listener {

    @EventHandler
    public void onExpChange(PlayerExpChangeEvent event) {
        Player player = event.getPlayer();
        int amount = event.getAmount();

        event.setAmount(0);

        PlayerLevel playerLevel = LevelAPI.getPlayerLevel(player);
        ExperienceBuff buff = playerLevel.getExperienceBuff();
        if (buff.isEnabled()) {
            amount *= buff.getBuff();
        }

        LevelAPI.addExp(player, amount);

        Experience exp = playerLevel.getExperience();

        if (Config.getConfig().isLevelMessageEnabled()) {
            player.sendMessage(Color.colored(String.format(
                    Prefix.EXP + "경험치를 &a%s &f획득하였습니다. [ &e%s &f/ &6%s &f]",
                    Formatter.format(amount), Formatter.format(exp.getExp()), Formatter.format(exp.getNextLevelRequiredExp())
            )));
        }
    }

}
