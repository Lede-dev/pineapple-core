package me.lede.stat.commands;

import com.google.common.collect.Lists;
import me.lede.stat.Stat;
import me.lede.stat.api.event.StatResetEvent;
import me.lede.stat.config.Config;
import me.lede.stat.objects.*;
import me.lede.stat.utils.Items;
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
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.Executors;

public class StatAdminCommand extends AbstractCommand {

    public StatAdminCommand() {
        super("statadmin", "core");
        setDescription("스텟 관리 커맨드");
        setUsage("/스텟관리");
        setAliases(Lists.newArrayList("스탯관리", "스텟관리", "tmxptrhksfl", "tmxorghksfl"));
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {

        if (!sender.isOp()) {
            return false;
        }

        int len = args.length;
        Player player;
        String prefix = Config.getConfig().getPrefix();

        if (len == 0) {
            sendHelpMessage(sender);
            return false;
        }

        String arg1 = args[0];
        if (Comparator.containsIgnoreCase(arg1, "view", "확인")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Color.colored("&c해당 명령어는 플레이어만 사용할 수 있습니다."));
                return false;
            }

            player = (Player) sender;

            if (len < 2) {
                sendHelpMessage(sender);
                return false;
            }


            OfflinePlayer target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(Color.colored(String.format(prefix + "%s 유저가 오프라인 입니다.", args[1])));
                return false;
            }

            StatManager.setStatEditorMode(player, StatEditorMode.DEBUG);
            PlayerStatMenu.openStatMenu(player, target);
            return false;
        }

        if (Comparator.containsIgnoreCase(arg1, "addticket", "추가권")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Color.colored("&c해당 명령어는 플레이어만 사용할 수 있습니다."));
                return false;
            }

            player = (Player) sender;

            int amount = 1;
            if (len >= 2) {
                amount = Math.max(Parser.parseInteger(args[1]), 1);
            }

            ItemStack ticket = Items.getAdditionalAbilityTicket();
            ticket.setAmount(amount);
            player.getInventory().addItem(ticket);
            player.sendMessage(Color.colored(String.format(
                    prefix + "&f추가 스텟권 %s개를 생성하였습니다.",
                    Formatter.format(amount)
            )));
            return false;
        }

        if (Comparator.containsIgnoreCase(arg1, "resetticket", "초기화권")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Color.colored("&c해당 명령어는 플레이어만 사용할 수 있습니다."));
                return false;
            }

            player = (Player) sender;

            int amount = 1;
            if (len>= 2) {
                amount = Math.max(Parser.parseInteger(args[1]), 1);
            }

            ItemStack ticket = Items.getResetAbilityTicket();
            ticket.setAmount(amount);
            player.getInventory().addItem(ticket);
            player.sendMessage(Color.colored(String.format(
                    prefix + "&f스텟 초기화권 %s개를 생성하였습니다.",
                    Formatter.format(amount)
            )));
            return false;
        }

        if (Comparator.containsIgnoreCase(arg1, "randomticket", "스텟뽑기권")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Color.colored("&c해당 명령어는 플레이어만 사용할 수 있습니다."));
                return false;
            }

            player = (Player) sender;

            int amount = 1;
            if (len >= 2) {
                amount = Math.max(1, Parser.parseInteger(args[1]));
            }

            ItemStack item = Items.getStatRandomTicket();
            item.setAmount(amount);
            player.getInventory().addItem(item);

            Config config = Config.getConfig();
            sender.sendMessage(Color.colored(config.getPrefix() + "스텟 뽑기권 " + amount + "개를 생성하였습니다."));
            return false;
        }

        if (Comparator.containsIgnoreCase(arg1, "reset", "초기화")) {
            if (len < 2) {
                sendHelpMessage(sender);
                return false;
            }

            OfflinePlayer target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(Color.colored(String.format(prefix + "%s 유저가 오프라인 입니다.", args[1])));
                return false;
            }

            PlayerStat stat = StatManager.getPlayerStat(target);
            stat.resetAll();
            stat.applyStat();
            stat.save();

            sender.sendMessage(Color.colored(String.format(
                    prefix + "권한으로 %s 유저를 초기화 했습니다.",
                    args[1]
            )));

            Player targetOnline = target.getPlayer();
            if (targetOnline != null) {
                targetOnline.sendMessage(Color.colored(prefix + "관리자가 스텟을 초기화 시켰습니다."));
            }
            return false;
        }

        if (Comparator.containsIgnoreCase(arg1, "resetall", "전체초기화")) {
            Executors.newSingleThreadExecutor().execute(() ->  {
                StatManager.resetStatAll();

                List<PlayerStat> stats = StatManager.getAllStat();
                Bukkit.getScheduler().runTask(Stat.getInstance(), () -> {
                    PluginManager manager = Bukkit.getPluginManager();
                    stats.forEach(playerStat -> {
                        playerStat.applyStat();

                        StatResetEvent event = new StatResetEvent(playerStat);
                        manager.callEvent(event);
                    });
                });

                sender.sendMessage(Color.colored(prefix + "권한으로 모든 유저가 스텟이 초기화 되었습니다."));
                Bukkit.broadcastMessage(Color.colored(prefix + "관리자가 스텟을 초기화 시켰습니다."));
            });
            return false;
        }

        if (Comparator.containsIgnoreCase(arg1, "add", "추가")) {
            if (len < 4) {
                sendHelpMessage(sender);
                return false;
            }

            OfflinePlayer target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(Color.colored(String.format(prefix + "%s 유저가 오프라인 입니다.", args[1])));
                return false;
            }

            String statType = args[2];
            int value = Parser.parseInteger(args[3]);
            if (Comparator.containsIgnoreCase(statType, "strength", "근력")) {
                addStat(sender, target, StatType.STRENGTH, value);
                return false;
            }

            if (Comparator.containsIgnoreCase(statType, "magic", "마법")) {
                addStat(sender, target, StatType.MAGIC, value);
                return false;
            }

            if (Comparator.containsIgnoreCase(statType, "agility", "민첩")) {
                addStat(sender, target, StatType.AGILITY, value);
                return false;
            }

            if (Comparator.containsIgnoreCase(statType, "deftness", "손재주")) {
                addStat(sender, target, StatType.DEFTNESS, value);
                return false;
            }

            if (Comparator.containsIgnoreCase(statType, "ability", "어빌리티")) {
                addStat(sender, target, StatType.ABILITY, value);
                return false;
            }

            if (Comparator.containsIgnoreCase(statType, "nether", "네더의별")) {
                if (Config.getConfig().isNetherStarStatEnabled()) {
                    addStat(sender, target, StatType.NETHER_STAR, value);
                } else {
                    sender.sendMessage(Color.colored(prefix + "네더의별 스텟이 비활성화 상태입니다."));
                }
                return false;
            }

            sendHelpMessage(sender);
            return false;
        }

        if (Comparator.containsIgnoreCase(arg1, "remove", "회수")) {
            if (len < 4) {
                sendHelpMessage(sender);
                return false;
            }

            OfflinePlayer target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(Color.colored(String.format(prefix + "%s 유저가 오프라인 입니다.", args[1])));
                return false;
            }

            String statType = args[2];
            int value = Parser.parseInteger(args[3]);
            if (Comparator.containsIgnoreCase(statType, "strength", "근력")) {
                removeStat(sender, target, StatType.STRENGTH, value);
                return false;
            }

            if (Comparator.containsIgnoreCase(statType, "magic", "마법")) {
                removeStat(sender, target, StatType.MAGIC, value);
                return false;
            }

            if (Comparator.containsIgnoreCase(statType, "agility", "민첩")) {
                removeStat(sender, target, StatType.AGILITY, value);
                return false;
            }

            if (Comparator.containsIgnoreCase(statType, "deftness", "손재주")) {
                removeStat(sender, target, StatType.DEFTNESS, value);
                return false;
            }

            if (Comparator.containsIgnoreCase(statType, "ability", "어빌리티")) {
                removeStat(sender, target, StatType.ABILITY, value);
                return false;
            }

            if (Comparator.containsIgnoreCase(statType, "nether", "네더의별")) {
                if (Config.getConfig().isNetherStarStatEnabled()) {
                    removeStat(sender, target, StatType.NETHER_STAR, value);
                } else {
                    sender.sendMessage(Color.colored(prefix + "네더의별 스텟이 비활성화 상태입니다."));
                }
                return false;
            }

            sendHelpMessage(sender);
            return false;
        }

        if (Comparator.containsIgnoreCase(arg1, "edit", "수정")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Color.colored("&c해당 명령어는 플레이어만 사용할 수 있습니다."));
                return false;
            }

            player = (Player) sender;

            if (len < 2) {
                sendHelpMessage(sender);
                return false;
            }

            OfflinePlayer target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(Color.colored(String.format(prefix + "%s 유저가 오프라인 입니다.", args[1])));
                return false;
            }

            StatManager.setStatEditorMode(player, StatEditorMode.EDITOR);
            PlayerStatMenu.openStatMenu(player, target);
            return false;
        }

        if (Comparator.containsIgnoreCase(arg1, "enable", "켜기")) {
            if (len < 2) {
                sendHelpMessage(sender);
                return false;
            }

            OfflinePlayer target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(Color.colored(String.format(prefix + "%s 유저가 오프라인 입니다.", args[1])));
                return false;
            }

            PlayerStat stat = StatManager.getPlayerStat(target);
            stat.setEnabled(true);
            stat.applyStat();
            stat.save();

            sender.sendMessage(Color.colored(String.format(prefix + "권한으로 %s 유저의 스텟을 켰습니다.", args[1])));
            Player targetOnline = target.getPlayer();
            if (targetOnline != null) {
                targetOnline.sendMessage(Color.colored(prefix + "관리자가 당신의 스텟을 켰습니다."));
            }
            return false;
        }

        if (Comparator.containsIgnoreCase(arg1, "disable", "끄기")) {
            if (len < 2) {
                sendHelpMessage(sender);
                return false;
            }

            OfflinePlayer target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(Color.colored(String.format(prefix + "%s 유저가 오프라인 입니다.", args[1])));
                return false;
            }

            PlayerStat stat = StatManager.getPlayerStat(target);
            stat.setEnabled(false);
            stat.applyStat();
            stat.save();

            sender.sendMessage(Color.colored(String.format(prefix + "권한으로 %s 유저의 스텟을 껐습니다.", args[1])));
            Player targetOnline = target.getPlayer();
            if (targetOnline != null) {
                targetOnline.sendMessage(Color.colored(prefix + "관리자가 당신의 스텟을 껐습니다."));
            }
            return false;
        }

        if (Comparator.containsIgnoreCase(arg1, "enableall", "전체켜기")) {
            StatManager.setEnabledAll(true);
            StatManager.applyAllStat();
            sender.sendMessage(Color.colored(prefix + "권한으로 모든 유저의 스텟을 켰습니다."));
            Bukkit.broadcastMessage(Color.colored(prefix + "관리자가 당신의 스텟을 켰습니다."));
            return false;
        }

        if (Comparator.containsIgnoreCase(arg1, "disableall", "전체끄기")) {
            StatManager.setEnabledAll(false);
            StatManager.applyAllStat();
            sender.sendMessage(Color.colored(prefix + "권한으로 모든 유저의 스텟을 껐습니다."));
            Bukkit.broadcastMessage(Color.colored(prefix + "관리자가 당신의 스텟을 껐습니다."));
            return false;
        }

        if (Comparator.containsIgnoreCase(arg1, "reload", "리로드")) {
            Config.reloadConfig();
            StatManager.applyAllStat();
            Bukkit.broadcastMessage(Color.colored(prefix + "Stat config reload."));
            return false;
        }

        if (Comparator.containsIgnoreCase(arg1, "nether", "네별")) {
            if (len < 2) {
                sendHelpMessage(sender);
                return false;
            }

            String state = args[1];
            Config config = Config.getConfig();
            if (Comparator.containsIgnoreCase(state, "off", "끄기")) {
                config.setEnableNetherStarStat(false);
                config.save();
                Config.reloadConfig();
                StatManager.applyAllStat();
                sender.sendMessage(Color.colored(config.getPrefix() + "네더의 별 스텟을 끕니다."));
                return false;
            }

            if (Comparator.containsIgnoreCase(state, "on", "켜기")) {
                config.setEnableNetherStarStat(true);
                config.save();
                Config.reloadConfig();
                StatManager.applyAllStat();
                sender.sendMessage(Color.colored(config.getPrefix() + "네더의 별 스텟을 켭니다."));
                return false;
            }

            sendHelpMessage(sender);
            return false;
        }

        if (Comparator.containsIgnoreCase(arg1, "debug", "디버그")) {
            boolean state = StatManager.isDebug();
            StatManager.setDebug(!state);
            sender.sendMessage("디버그 모드 전환: " + StatManager.isDebug());
            return false;
        }

        sendHelpMessage(sender);
        return false;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {

        if (args.length == 1) {
            return Lists.newArrayList(
                    "확인", "추가권", "초기화권", "스텟뽑기권", "초기화", "전체초기화",
                    "추가", "회수", "수정", "켜기", "끄기", "전체켜기", "전체끄기",
                    "네별", "디버그", "리로드",
                    "view", "addticket", "resetticket", "randomticket", "reset", "resetall",
                    "add", "remove", "edit", "enable", "disable", "enableall", "disableall",
                    "nether", "debug", "reload"
            );
        }

        if (args.length == 2) {
            String arg = args[0];
            if (Comparator.containsIgnoreCase(arg, "확인", "view")) {
                return super.tabComplete(sender, alias, args);
            }

            if (Comparator.containsIgnoreCase(arg, "추가권", "초기화권", "스텟뽑기권", "addticket", "resetticket", "randomticket")) {
                return Lists.newArrayList("1", "3", "5", "10");
            }

            if (Comparator.containsIgnoreCase(arg, "초기화", "reset")) {
                return super.tabComplete(sender, alias, args);
            }

            if (Comparator.containsIgnoreCase(arg, "추가", "회수", "add", "remove")) {
                return super.tabComplete(sender, alias, args);
            }

            if (Comparator.containsIgnoreCase(arg, "수정", "켜기", "끄기", "edit", "enable", "disable")) {
                return super.tabComplete(sender, alias, args);
            }

            if (Comparator.containsIgnoreCase(arg, "네별", "nether")) {
                return Lists.newArrayList("켜기", "끄기", "on", "off");
            }
        }

        if (args.length == 3) {
            String arg = args[0];
            if (Comparator.containsIgnoreCase(arg, "추가", "회수", "add", "remove")) {
                return StatType.getNameList();
            }
        }

        if (args.length == 4) {
            String arg = args[0];
            if (Comparator.containsIgnoreCase(arg, "추가", "회수", "add", "remove")) {
                return Lists.newArrayList("1", "3", "5", "10");
            }
        }

        return Lists.newArrayList();
    }

    private void sendHelpMessage(@NotNull CommandSender sender) {
        String prefix = Config.getConfig().getPrefix();
        sender.sendMessage(Color.colored(prefix + "/스텟관리 확인 <닉네임>"));
        sender.sendMessage(Color.colored(prefix + "/스텟관리 추가권 <갯수>"));
        sender.sendMessage(Color.colored(prefix + "/스텟관리 초기화권 <갯수>"));
        sender.sendMessage(Color.colored(prefix + "/스텟관리 스텟뽑기권 <갯수>"));
        sender.sendMessage(Color.colored(prefix + "/스텟관리 초기화 <닉네임>"));
        sender.sendMessage(Color.colored(prefix + "/스텟관리 전체초기화"));
        sender.sendMessage(Color.colored(prefix + "/스텟관리 추가 <닉네임> <스텟> <숫자>"));
        sender.sendMessage(Color.colored(prefix + "/스텟관리 회수 <닉네임> <스텟> <숫자>"));
        sender.sendMessage(Color.colored(prefix + "/스텟관리 수정 <닉네임>"));
        sender.sendMessage(Color.colored(prefix + "/스텟관리 켜기 <닉네임>"));
        sender.sendMessage(Color.colored(prefix + "/스텟관리 끄기 <닉네임>"));
        sender.sendMessage(Color.colored(prefix + "/스텟관리 전체켜기"));
        sender.sendMessage(Color.colored(prefix + "/스텟관리 전체끄기"));
        sender.sendMessage(Color.colored(prefix + "/스텟관리 네별 끄기"));
        sender.sendMessage(Color.colored(prefix + "/스텟관리 네별 켜기"));
        sender.sendMessage(Color.colored(prefix + "/스텟관리 디버그"));
        sender.sendMessage(Color.colored(prefix + "/스텟관리 리로드"));
    }

    private void addStat(@NotNull CommandSender sender, @NotNull OfflinePlayer target, @NotNull StatType type, int value) {
        String prefix = Config.getConfig().getPrefix();
        PlayerStat stat = StatManager.getPlayerStat(target);
        if (stat.isMaxValue(type)) {
            sender.sendMessage(Color.colored(String.format(
                    prefix + "&c더 이상 올릴 수 있는 %s 스텟이 없습니다.",
                    type.getName()
            )));
            return;
        }

        int increase = value;
        int max = Config.getConfig().getMaxStat(type);
        int has = stat.getStat(type);
        if (has + value > max) {
            increase = max - has;
        }
        stat.addStat(type, value);
        stat.applyStat();
        stat.save();

        sender.sendMessage(Color.colored(String.format(
                prefix + "권한으로 %s 스텟이 %s개 추가되었습니다.",
                type.getName(), Formatter.format(increase)
        )));

        Player targetOnline = target.getPlayer();
        if (targetOnline != null) {
            targetOnline.sendMessage(Color.colored(String.format(
                    prefix + "관리자 권한으로 %s 스텟이 %s개 추가되었습니다.",
                    type.getName(), Formatter.format(increase)
            )));
        }
    }

    private void removeStat(@NotNull CommandSender sender, @NotNull OfflinePlayer target, @NotNull StatType type, int value) {
        String prefix = Config.getConfig().getPrefix();
        PlayerStat stat = StatManager.getPlayerStat(target);
        int has = stat.getStat(type);
        if (has < 1) {
            sender.sendMessage(Color.colored(String.format(
                    prefix + "&c더 이상 내릴 수 있는 %s 스텟이 없습니다.",
                    type.getName()
            )));
            return;
        }

        int decrease = value;
        if (has - value < 0) {
            decrease = value - has;
        }
        stat.removeStat(type, value);
        stat.applyStat();
        stat.save();

        sender.sendMessage(Color.colored(String.format(
                prefix + "권한으로 %s 스텟이 %s개 회수되었습니다.",
                type.getName(), Formatter.format(decrease)
        )));

        Player targetOnline = target.getPlayer();
        if (targetOnline != null) {
            targetOnline.sendMessage(Color.colored(String.format(
                    prefix + "관리자 권한으로 %s 스텟이 %s개 회수되었습니다.",
                    type.getName(), Formatter.format(decrease)
            )));
        }
    }

}
