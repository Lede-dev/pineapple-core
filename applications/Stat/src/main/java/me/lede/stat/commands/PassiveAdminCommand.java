package me.lede.stat.commands;

import com.google.common.collect.Lists;
import me.lede.stat.objects.PassiveBook;
import me.lede.stat.objects.PlayerStat;
import me.lede.stat.objects.StatManager;
import me.lede.utils.color.Color;
import me.lede.utils.commands.AbstractCommand;
import me.lede.utils.format.Formatter;
import me.lede.utils.format.Parser;
import me.lede.utils.message.Comparator;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class PassiveAdminCommand extends AbstractCommand {

    public PassiveAdminCommand() {
        super("adminpassive", "core");
        setDescription("패시브 관리 커맨드");
        setUsage("/패시브관리");
        setAliases(Lists.newArrayList("패시브관리", "adp"));
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!sender.isOp()) {
            return false;
        }

        int len = args.length;
        if (len == 0) {
            sendHelpMessage(sender);
            return false;
        }

        String arg1 = args[0];

        if (Comparator.containsIgnoreCase(arg1, "확인", "check")) {
            if (len < 2) {
                sendHelpMessage(sender);
                return false;
            }

            OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
            PlayerStat stat = StatManager.getPlayerStat(target);
            sender.sendMessage(Color.colored(String.format(
                    "&b[ McAge3 ] &f%s 패시브 &7( 데미지 감소 %s%% )",
                    args[1],
                    Formatter.format(stat.getPassiveDamageDecreasePercentage())
            )));
            return false;
        }

        if (Comparator.containsIgnoreCase(arg1, "목록", "list")) {
            int page = 1;
            if (len >= 2) {
                page = Math.max(1, Parser.parseInteger(args[1]));
            }

            List<PlayerStat> stats = StatManager.getPlayerStatList();
            int start = (page - 1) * 20;
            if (start >= stats.size()) {
                sender.sendMessage(Color.colored("&b[ McAge3 ] &f해당 페이지에 출력할 플레이어 정보가 존재하지 않습니다."));
                return false;
            }

            int end = Math.min(stats.size(), page * 20);

            for (int i = start; i < end; i++) {
                PlayerStat stat = stats.get(i);
                Player target = stat.getPlayer();
                String name = target == null ? stat.getUniqueId() : target.getName();
                sender.sendMessage(Color.colored(String.format(
                        "&b[ McAge3 ] &f%s 패시브 &7( 데미지 감소 %s%% )",
                        name,
                        Formatter.format(stat.getPassiveDamageDecreasePercentage())
                )));
            }
            sender.sendMessage(Color.colored("&b[ McAge3 ] &f페이지 &e" + page + " &f출력"));
            return false;
        }

        if (Comparator.containsIgnoreCase(arg1, "초기화", "reset")) {
            if (len < 2) {
                sendHelpMessage(sender);
                return false;
            }

            OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
            PlayerStat stat = StatManager.getPlayerStat(target);
            stat.setPassiveLevel(0);
            sender.sendMessage(Color.colored(String.format(
                    "&b[ McAge3 ] &f%s 플레이어의 패시브를 초기화 합니다.",
                    args[1]
            )));
            return false;
        }

        if (Comparator.containsIgnoreCase(arg1, "전체초기화", "resetall")) {
            StatManager.resetAllPassive();
            StatManager.applyAllStat();
            sender.sendMessage(Color.colored("&b[ McAge3 ] &f모든 플레이어의 패시브를 초기화 합니다."));
            return false;
        }

        if (Comparator.containsIgnoreCase(arg1, "책", "book")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Color.colored("&c해당 명령어는 플레이어만 사용할 수 있습니다."));
                return false;
            }

            Player player = (Player) sender;

            if (len < 2) {
                sendHelpMessage(sender);
                return false;
            }

            int amount = 1;
            int level = Parser.parseInteger(args[1]);
            if (len >= 3) {
                amount = Math.max(1, Parser.parseInteger(args[2]));
            }

            PassiveBook book = new PassiveBook(level);
            ItemStack item = book.getItemStack();
            item.setAmount(amount);
            player.getInventory().addItem(item);
            player.sendMessage(Color.colored("&b[ McAge3 ] &f패시브책이 지급되었습니다."));
            return false;
        }

        
        return false;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            return Lists.newArrayList(
                    "확인", "목록", "초기화", "전체초기화", "책",
                    "check", "list", "reset", "resetall", "book"
            );
        }

        if (args.length == 2) {
            String arg = args[0];
            if (Comparator.containsIgnoreCase(arg, "확인", "check")) {
                return super.tabComplete(sender, alias, args);
            }

            if (Comparator.containsIgnoreCase(arg, "목록", "list")) {
                int size = Bukkit.getOnlinePlayers().size();
                int maxPage = (size / 20) + ((size % 20 > 0) ? 1 : 0);
                List<String> pages = Lists.newArrayListWithCapacity(maxPage);
                for (int i = 1; i <= maxPage; i++) {
                    pages.add(Integer.toString(i));
                }
                return pages;
            }

            if (Comparator.containsIgnoreCase(arg, "초기화", "reset")) {
                return super.tabComplete(sender, alias, args);
            }

            if (Comparator.containsIgnoreCase(arg, "책", "book")) {
                return Lists.newArrayList("1", "10", "15", "20");
            }
        }

        return Lists.newArrayList();
    }

    private void sendHelpMessage(@NotNull CommandSender sender) {
        sender.sendMessage(Color.colored("&b[ McAge3 ] &f/패시브관리 확인 <닉네임>"));
        sender.sendMessage(Color.colored("&b[ McAge3 ] &f/패시브관리 목록 <페이지>"));
        sender.sendMessage(Color.colored("&b[ McAge3 ] &f/패시브관리 초기화 <닉네임>"));
        sender.sendMessage(Color.colored("&b[ McAge3 ] &f/패시브관리 전체초기화"));
        sender.sendMessage(Color.colored("&b[ McAge3 ] &f/패시브관리 책 <레벨>"));
    }

}
