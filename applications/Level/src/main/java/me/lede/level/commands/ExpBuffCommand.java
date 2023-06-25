package me.lede.level.commands;

import com.google.common.collect.Lists;
import me.lede.level.api.LevelAPI;
import me.lede.level.objects.ExperienceBuff;
import me.lede.level.objects.PlayerLevel;
import me.lede.utils.color.Color;
import me.lede.utils.commands.AbstractCommand;
import me.lede.utils.format.Formatter;
import me.lede.utils.message.Comparator;
import me.lede.utils.message.Prefix;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ExpBuffCommand extends AbstractCommand {

    public ExpBuffCommand() {
        super("expbuff", "core");
        setDescription("경험치 배수버프 관리 커맨드");
        setUsage("/남은시간");
        setAliases(Lists.newArrayList("남은시간"));
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Color.colored(Prefix.EXP + "해당 명령어는 플레이어만 사용할 수 있습니다."));
            return false;
        }

        int len = args.length;
        if (len == 0) {
            sendHelpMessage(sender);
            return false;
        }

        Player player = (Player) sender;
        String arg1 = args[0];
        if (Comparator.containsIgnoreCase(arg1, "show", "확인")) {
            PlayerLevel level = LevelAPI.getPlayerLevel(player);
            ExperienceBuff buff = level.getExperienceBuff();
            double value = buff.getBuff();
            long duration = buff.getRemainSeconds();

            if (duration < 0) {
                player.sendMessage(Color.colored(Prefix.EXP + "배수버프가 적용중이지 않습니다."));
                return false;
            }

            player.sendMessage(Color.colored(String.format(
                    Prefix.EXP + "&6%s &f배 배수버프가 적용중 입니다. 남은시간 [%s ]",
                    Formatter.format(value), Formatter.formatSeconds(duration)
            )));
            return false;
        }

        if (Comparator.containsIgnoreCase(arg1, "remove", "제거")) {
            PlayerLevel level = LevelAPI.getPlayerLevel(player);
            ExperienceBuff buff = level.getExperienceBuff();
            long duration = buff.getRemainSeconds();

            if (duration < 0) {
                player.sendMessage(Color.colored(Prefix.EXP + "배수버프가 적용중이지 않습니다."));
                return false;
            }

            buff.setRemainSeconds(-1L);
            player.sendMessage(Color.colored(Prefix.EXP + "적용중인 배수버프를 제거하였습니다."));
            return false;
        }

        sendHelpMessage(sender);
        return false;
    }

    private void sendHelpMessage(@NotNull CommandSender sender) {
        sender.sendMessage(Color.colored(Prefix.EXP + "&e/배수버프 &b확인 &f- 적용중인 배수버프를 확인합니다."));
        sender.sendMessage(Color.colored(Prefix.EXP + "&e/배수버프 &b제거 &f- 적용중인 배수버프를 제거합니다."));
    }


}
