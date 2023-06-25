package me.lede.level.commands;

import com.google.common.collect.Lists;
import me.lede.level.api.LevelAPI;
import me.lede.level.objects.ExperienceBook;
import me.lede.level.objects.ExperienceCoupon;
import me.lede.level.objects.LevelManager;
import me.lede.level.objects.PlayerLevel;
import me.lede.utils.color.Color;
import me.lede.utils.commands.AbstractCommand;
import me.lede.utils.format.Formatter;
import me.lede.utils.format.Parser;
import me.lede.utils.message.Comparator;
import me.lede.utils.message.Prefix;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ExpManageCommand extends AbstractCommand {

    public ExpManageCommand() {
        super("expadmin", "core");
        setDescription("경험치 관리 커맨드");
        setUsage("/경험치관리");
        setAliases(Lists.newArrayList("경험치관리"));
        setPermission("op.command.exp");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (sender instanceof Player && !sender.isOp()) {
            return false;
        }

        int len = args.length;

        if (len == 0) {
            sendHelpMessage(sender);
            return false;
        }

        String arg1 = args[0];

        if (Comparator.containsIgnoreCase(arg1, "set", "설정")) {
            if (len < 3) {
                sendHelpMessage(sender);
                return false;
            }

            OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
            double exp = Parser.parseDouble(args[2]);

            LevelAPI.setExp(player, exp);
            sender.sendMessage(Color.colored(String.format(
                    Prefix.EXP + "&6%s &f플레이어의 경험치를 &a%s &f(으)로 설정합니다.",
                    args[1], Formatter.format(exp)
            )));
            return false;
        }

        if (Comparator.containsIgnoreCase(arg1, "coupon", "배수쿠폰")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Color.colored(Prefix.EXP + "해당 명령어는 플레이어만 사용할 수 있습니다."));
                return false;
            }

            if (len < 4) {
                sendHelpMessage(sender);
                return false;
            }

            double buff = Parser.parseDouble(args[1]);
            int minute = Parser.parseInteger(args[2]);
            int amount = Parser.parseInteger(args[3]);

            ExperienceCoupon coupon = new ExperienceCoupon(buff, minute * 60L);
            ItemStack item = coupon.getItemStack();
            item.setAmount(amount);

            ((Player) sender).getInventory().addItem(item);
            sender.sendMessage(Color.colored(String.format(
                    Prefix.EXP + "&6%s &f배, &a%s &f분 배수쿠폰 &e%s &f개를 생성하였습니다.",
                    Formatter.format(buff), Formatter.format(minute), Formatter.format(amount)
            )));
            return false;
        }

        if (Comparator.containsIgnoreCase(arg1, "removeCoupon", "배수삭제")) {
            if (len < 2) {
                sendHelpMessage(sender);
                return false;
            }

            OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
            PlayerLevel level = LevelAPI.getPlayerLevel(player);
            level.getExperienceBuff().setRemainSeconds(-1L);

            sender.sendMessage(Color.colored(String.format(
                    Prefix.EXP + "&6%s &f플레이어의 배수 버프를 제거하였습니다.",
                    args[1]
            )));
            return false;
        }

        if (Comparator.containsIgnoreCase(arg1, "removeCouponAll", "전체배수삭제")) {
            for (Map.Entry<String, PlayerLevel> entry : LevelManager.getPlayerLevelMap().entrySet()) {
                PlayerLevel level = entry.getValue();
                if (level != null) {
                    level.getExperienceBuff().setRemainSeconds(-1L);
                }
            }

            sender.sendMessage(Color.colored(Prefix.EXP + "모든 플레이어의 배수 버프를 제거하였습니다."));
            return false;
        }

        if (Comparator.containsIgnoreCase(arg1, "add", "지급")) {
            if (len < 3) {
                sendHelpMessage(sender);
                return false;
            }

            OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
            double exp = Parser.parseDouble(args[2]);

            LevelAPI.addExp(player, exp);
            sender.sendMessage(Color.colored(String.format(
                    Prefix.EXP + "&6%s &f플레이어에게 &a%s &f경험치를 지급하였습니다.",
                    args[1], Formatter.format(exp)
            )));
            return false;
        }

        if (Comparator.containsIgnoreCase(arg1, "remove", "차감")) {
            if (len < 3) {
                sendHelpMessage(sender);
                return false;
            }

            OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
            double exp = Parser.parseDouble(args[2]);

            LevelAPI.removeExp(player, exp);
            sender.sendMessage(Color.colored(String.format(
                    Prefix.EXP + "&6%s &f플레이어로부터 &a%s &f경험치를 차감하였습니다.",
                    args[1], Formatter.format(exp)
            )));
            return false;
        }

        if (Comparator.containsIgnoreCase(arg1, "book", "책발급")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Color.colored(Prefix.EXP + "해당 명령어는 플레이어만 사용할 수 있습니다."));
                return false;
            }

            if (len < 2) {
                sendHelpMessage(sender);
                return false;
            }

            int amount = 1;
            if (len >= 3) {
                amount = Math.max(Parser.parseInteger(args[2]), 1);
            }

            double exp = Parser.parseDouble(args[1]);
            ExperienceBook book = new ExperienceBook(exp);
            ItemStack item = book.getItemStack();
            item.setAmount(amount);

            ((Player) sender).getInventory().addItem(item);
            sender.sendMessage(Color.colored(String.format(
                    Prefix.EXP + "경험치 &a%s &f책을 발급하였습니다.",
                    Formatter.format(exp)
            )));
            return false;
        }

        if (Comparator.containsIgnoreCase(arg1, "tear", "눈물")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Color.colored(Prefix.EXP + "해당 명령어는 플레이어만 사용할 수 있습니다."));
                return false;
            }

            if (len < 2) {
                sendHelpMessage(sender);
                return false;
            }

            int amount = 1;
            if (len >= 3) {
                amount = Math.max(Parser.parseInteger(args[2]), 1);
            }

            double exp = Parser.parseDouble(args[1]);
            ExperienceBook book = new ExperienceBook(
                    Color.colored(String.format("&f눈물 서린 책 [ %s ]", Formatter.format(exp))),
                    Lists.newArrayList(
                            Color.colored("&f"),
                            Color.colored(String.format("&7우클릭 시 경험치 %s 획득", Formatter.format(exp))),
                            Color.colored("&f")
                    ),
                    exp
            );
            ItemStack item = book.getItemStack();
            item.setAmount(amount);

            ((Player) sender).getInventory().addItem(item);
            sender.sendMessage(Color.colored(String.format(
                    Prefix.EXP + "경험치 &a%s &f눈물을 발급하였습니다.",
                    Formatter.format(exp)
            )));
            return false;
        }

        sendHelpMessage(sender);
        return false;
    }
    
    private void sendHelpMessage(@NotNull CommandSender sender) {
        sender.sendMessage(Color.colored(Prefix.EXP + "&e/경험치관리 &b설정 &d<플레이어> &a<경험치> &f- 플레이어의 경험치를 설정합니다."));
        sender.sendMessage(Color.colored(Prefix.EXP + "&e/경험치관리 &b배수쿠폰 &d<배수> &a<분> &e<수량> &f- 배수쿠폰 지급"));
        sender.sendMessage(Color.colored(Prefix.EXP + "&e/경험치관리 &b배수삭제 &d<플레이어> &f- 플레이어의 배수버프를 제거합니다."));
        sender.sendMessage(Color.colored(Prefix.EXP + "&e/경험치관리 &b전체배수삭제&f- 모든 플레이어의 배수버프를 제거합니다."));
        sender.sendMessage(Color.colored(Prefix.EXP + "&e/경험치관리 &b지급 &d<플레이어> &a<경험치> &f- 플레이어에게 경험치를 지급합니다."));
        sender.sendMessage(Color.colored(Prefix.EXP + "&e/경험치관리 &b차감 &d<플레이어> &a<경험치> &f- 플레이어의 경험치를 차감합니다."));
        sender.sendMessage(Color.colored(Prefix.EXP + "&e/경험치관리 &b책발급 &d<경험치> &a<개수> &f- 경험치 책을 발급합니다."));
        sender.sendMessage(Color.colored(Prefix.EXP + "&e/경험치관리 &b눈물 &d<경험치> &a<개수> &f- 경험치 책을 발급합니다."));
    }

}
