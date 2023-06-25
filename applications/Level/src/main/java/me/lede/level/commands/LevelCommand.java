package me.lede.level.commands;

import com.google.common.collect.Lists;
import me.lede.level.objects.LevelManager;
import me.lede.level.objects.PlayerLevel;
import me.lede.utils.color.Color;
import me.lede.utils.commands.AbstractCommand;
import me.lede.utils.format.Formatter;
import me.lede.utils.message.Prefix;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LevelCommand extends AbstractCommand {

    public LevelCommand() {
        super("level", "core");
        setDescription("레벨 확인 커맨드");
        setUsage("/레벨");
        setAliases(Lists.newArrayList("레벨", "fpqpf"));
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Color.colored(Prefix.LEVEL + "해당 명령어는 플레이어만 사용할 수 있습니다."));
            return false;
        }

        Player player = (Player) sender;
        PlayerLevel level = LevelManager.getPlayerLevel(player);
        sender.sendMessage(Color.colored(String.format(
                Prefix.LEVEL + "레벨: &a%s",
                Formatter.format(level.getExperience().getLevel())
        )));
        sender.sendMessage(Color.colored(String.format(
                Prefix.LEVEL + "경험치: &e%s",
                Formatter.format(level.getExperience().getExp())
        )));
        return false;
    }
}
