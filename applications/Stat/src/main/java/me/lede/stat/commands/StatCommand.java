package me.lede.stat.commands;

import com.google.common.collect.Lists;
import me.lede.stat.objects.PlayerStatMenu;
import me.lede.stat.objects.StatEditorMode;
import me.lede.stat.objects.StatManager;
import me.lede.utils.color.Color;
import me.lede.utils.commands.AbstractCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatCommand extends AbstractCommand {

    public StatCommand() {
        super("stat", "core");
        setDescription("스탯 확인 커맨드");
        setUsage("/스탯");
        setAliases(Lists.newArrayList("스탯", "스텟", "tmxot", "tmxpt"));
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Color.colored("&c해당 명령어는 플레이어만 사용할 수 있습니다."));
            return false;
        }

        Player player = (Player) sender;
        StatManager.setStatEditorMode(player, StatEditorMode.NORMAL);
        PlayerStatMenu.openStatMenu(player, player);
        return false;
    }

}
