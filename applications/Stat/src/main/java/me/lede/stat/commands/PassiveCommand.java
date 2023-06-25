package me.lede.stat.commands;

import com.google.common.collect.Lists;
import me.lede.stat.config.Config;
import me.lede.stat.objects.PlayerStat;
import me.lede.stat.objects.StatManager;
import me.lede.utils.color.Color;
import me.lede.utils.commands.AbstractCommand;
import me.lede.utils.format.Formatter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PassiveCommand extends AbstractCommand {

    public PassiveCommand() {
        super("passive", "core");
        setDescription("패시브 확인 커맨드");
        setUsage("/패시브");
        setAliases(Lists.newArrayList("패시브"));
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;
        PlayerStat stat = StatManager.getPlayerStat(player);
        int level = stat.getPassiveLevel();
        double percentage = Config.getConfig().getPassivePercentage();
        player.sendMessage(Color.colored(String.format(
                "&b[ McAge3 ] &f패시브 &7( 데미지 감소 %s%% )",
                Formatter.format(level * percentage)
        )));
        return false;
    }

}
