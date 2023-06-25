package me.lede.level.listeners;

import me.lede.utils.color.Color;
import me.lede.utils.message.Prefix;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class XPCommandWrapper implements Listener {

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage();
        if (command.startsWith("/xp")) {
            event.setCancelled(true);
            Player player = event.getPlayer();
            if (player.isOp()) {
                player.sendMessage(Color.colored(Prefix.LEVEL + "&6/레벨관리 &f혹은 &6/경험치관리 &f명령어를 사용해 주세요."));
            }
        }
    }

}
