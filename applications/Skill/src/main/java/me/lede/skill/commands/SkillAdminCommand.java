package me.lede.skill.commands;

import com.google.common.collect.Lists;
import com.nisovin.magicspells.MagicSpells;
import me.lede.skill.Skill;
import me.lede.skill.config.Config;
import me.lede.skill.config.MenuConfig;
import me.lede.skill.config.SkillConfig;
import me.lede.skill.objects.*;
import me.lede.stat.objects.StatEditorMode;
import me.lede.stat.objects.StatManager;
import me.lede.stat.objects.StatType;
import me.lede.utils.color.Color;
import me.lede.utils.commands.AbstractCommand;
import me.lede.utils.format.Parser;
import me.lede.utils.message.Comparator;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;

public class SkillAdminCommand extends AbstractCommand {

    public SkillAdminCommand() {
        super("skilladmin", "core");
        setDescription("스킬 관리 커맨드");
        setUsage("/스킬관리");
        setAliases(Lists.newArrayList("스킬관리", "tmzlfrhksfl"));
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        String prefix = Config.getConfig().getPrefix();
        if (!sender.isOp()) {
            sender.sendMessage(prefix + "&4당신은 이 명령어를 사용할 권한이 없습니다.");
            return false;
        }

        int len = args.length;
        if (len == 0) {
            sendHelpMessage(sender);
            return false;
        }

        String arg1 = args[0];
        if (Comparator.containsIgnoreCase(arg1, "show", "확인")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Color.colored(prefix + "해당 명령어는 플레이어만 사용할 수 있습니다."));
                return false;
            }

            if (len < 2) {
                sendHelpMessage(sender);
                return false;
            }

            String target = args[1];
            Player player = (Player) sender;
            OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(target);
            if (!targetPlayer.hasPlayedBefore()) {
                sender.sendMessage(Color.colored(String.format(prefix + "%s 플레이어는 한 번도 접속하지 않은 플레이어 입니다.", target)));
                return false;
            }

            player.removeMetadata("editor", Skill.getInstance());
            player.setMetadata("debug", new FixedMetadataValue(Skill.getInstance(), targetPlayer.getUniqueId().toString()));
            PlayerSkillMainMenu.openMenu(player);
            return false;
        }

        if (Comparator.containsIgnoreCase(arg1, "add", "지급")) {
            if (len < 3) {
                sendHelpMessage(sender);
                return false;
            }

            String name = args[1];
            String skill = args[2];

            OfflinePlayer target = Bukkit.getOfflinePlayer(name);
            if (!target.hasPlayedBefore()) {
                sender.sendMessage(Color.colored(String.format(prefix + "%s 플레이어는 한 번도 접속하지 않은 플레이어 입니다.", name)));
                return false;
            }

            PlayerSkill targetSkill = SkillManager.getPlayerSkill(target);
            try {
                SkillConfig config = SkillConfig.getConfig();
                StatType statType = Objects.requireNonNull(config.getStatTypeByName(skill));
                SkillConfig.StatSkill statSkill = Objects.requireNonNull(config.getStatSkill(statType));
                SkillConfig.Type type = Objects.requireNonNull(statSkill.getTypeByName(skill));
                targetSkill.learnSkillByAdmin(sender, type, statType);
            } catch (Exception e) {
                sender.sendMessage(Color.colored(prefix + "잘못된 스킬 이름을 입력하였습니다. 입력한 값: " + skill));
            }
            return false;
        }

        if (Comparator.containsIgnoreCase(arg1, "forceadd", "무시지급")) {
            if (len < 3) {
                sendHelpMessage(sender);
                return false;
            }

            String name = args[1];
            String skill = args[2];

            OfflinePlayer target = Bukkit.getOfflinePlayer(name);
            if (!target.hasPlayedBefore()) {
                sender.sendMessage(Color.colored(String.format(prefix + "%s 플레이어는 한 번도 접속하지 않은 플레이어 입니다.", name)));
                return false;
            }
            
            PlayerSkill targetSkill = SkillManager.getPlayerSkill(target);
            try {
                SkillConfig config = SkillConfig.getConfig();
                StatType statType = Objects.requireNonNull(config.getStatTypeByName(skill));
                SkillConfig.StatSkill statSkill = Objects.requireNonNull(config.getStatSkill(statType));
                SkillConfig.Type type = Objects.requireNonNull(statSkill.getTypeByName(skill));
                targetSkill.forceLearnSkillByAdmin(sender, type, statType);
            } catch (Exception e) {
                sender.sendMessage(Color.colored(prefix + "잘못된 스킬 이름을 입력하였습니다. 입력한 값: " + skill));
            }
            return false;
        }

        if (Comparator.containsIgnoreCase(arg1, "remove", "회수")) {
            if (len < 3) {
                sendHelpMessage(sender);
                return false;
            }

            String name = args[1];
            String skill = args[2];

            OfflinePlayer target = Bukkit.getOfflinePlayer(name);
            if (!target.hasPlayedBefore()) {
                sender.sendMessage(Color.colored(String.format(prefix + "%s 플레이어는 한 번도 접속하지 않은 플레이어 입니다.", name)));
                return false;
            }
            
            PlayerSkill targetSkill = SkillManager.getPlayerSkill(target);
            try {
                SkillConfig config = SkillConfig.getConfig();
                StatType statType = Objects.requireNonNull(config.getStatTypeByName(skill));
                SkillConfig.StatSkill statSkill = Objects.requireNonNull(config.getStatSkill(statType));
                SkillConfig.Type type = Objects.requireNonNull(statSkill.getTypeByName(skill));
                targetSkill.forceRemoveSkillByAdmin(sender, type, statType);
            } catch (Exception e) {
                sender.sendMessage(Color.colored(prefix + "잘못된 스킬 이름을 입력하였습니다. 입력한 값: " + skill));
            }
            return false;
        }

        if (Comparator.containsIgnoreCase(arg1, "book", "스킬북")) {
            if (len < 2) {
                sendHelpMessage(sender);
                return false;
            }

            int amount = 1;
            if (len >= 3) {
                try {
                    amount = Integer.parseInt(args[2]);
                } catch (Exception ignored) { }
            }

            String skill = args[1];
            Player target = (Player) sender;

            SkillBook book = new SkillBook(skill);
            ItemStack item = book.toItemStack();
            if (item == null) {
                sender.sendMessage(Color.colored(prefix + "스킬북으로 만들 수 없는 스킬입니다. 입력한 값: " + skill));
                return false;
            }

            item.setAmount(amount);
            target.getInventory().addItem(item);

            StatType statType = SkillConfig.getConfig().getStatTypeByName(skill);
            sender.sendMessage(Color.colored(String.format(
                    prefix + "%s%s &f스킬북을 지급하였습니다.",
                    statType.getColor(), skill
            )));
            return false;
        }

        if (Comparator.containsIgnoreCase(arg1, "resetticket", "초기화권")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Color.colored(prefix + "해당 명령어는 플레이어만 사용할 수 있습니다."));
                return false;
            }

            int amount = 1;
            if (len >= 2) {
                amount = Math.max(1, Parser.parseInteger(args[1]));
            }

            SkillResetTicket ticket = new SkillResetTicket();
            ItemStack item = ticket.toItemStack();
            item.setAmount(amount);

            Player player = (Player) sender;
            player.getInventory().addItem(item);
            sender.sendMessage(Color.colored(prefix + "&c스킬 초기화권&f을 지급하였습니다."));
            return false;
        }

        if (Comparator.containsIgnoreCase(arg1, "reset", "초기화")) {
            if (len < 2) {
                sendHelpMessage(sender);
                return false;
            }

            String target = args[1];
            OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(target);
            if (!targetPlayer.hasPlayedBefore()) {
                sender.sendMessage(Color.colored(String.format(prefix + "%s 플레이어는 한 번도 접속하지 않은 플레이어 입니다.", target)));
                return false;
            }
            
            PlayerSkill playerSkill = SkillManager.getPlayerSkill(targetPlayer);
            playerSkill.reset();
            sender.sendMessage(Color.colored(String.format(
                    prefix + "&6%s&f의 스킬을 초기화 했습니다.",
                    target
            )));
            return false;
        }

        if (Comparator.containsIgnoreCase(arg1, "resetall", "전체초기화")) {
            Executors.newSingleThreadExecutor().execute(() -> {
                long start = System.currentTimeMillis();
                SkillManager.resetAll();
                Bukkit.broadcastMessage(Color.colored(String.format(
                        prefix + "&fSkill system reset. ( %s 초 )",
                        (System.currentTimeMillis() - start) / 1000.0d
                )));
            });
            return false;
        }

        if (Comparator.containsIgnoreCase(arg1, "addall", "전체지급")) {
            if (len == 1) {
                for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
                    if (offlinePlayer.hasPlayedBefore()) {
                        PlayerSkill playerSkill = SkillManager.getPlayerSkill(offlinePlayer);
                        playerSkill.addAll();
                    }
                }

                sender.sendMessage(Color.colored(String.format(prefix + "모든 플레이어에게 모든 스킬을 지급하였습니다.")));
                return false;
            }

            if (len == 2) {
                String target = args[1];
                if (target == null || target.isEmpty() || target.equals("@")) {
                    for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
                        if (offlinePlayer.hasPlayedBefore()) {
                            PlayerSkill playerSkill = SkillManager.getPlayerSkill(offlinePlayer);
                            playerSkill.addAll();
                        }
                    }

                    sender.sendMessage(Color.colored(String.format(prefix + "모든 플레이어에게 모든 스킬을 지급하였습니다.")));
                    return false;
                }

                OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(target);
                if (targetPlayer == null) {
                    sender.sendMessage(Color.colored(String.format(prefix + "%s 플레이어는 없는 플레이어입니다.")));
                    return false;
                }

                if (!targetPlayer.hasPlayedBefore()) {
                    sender.sendMessage(Color.colored(String.format(prefix + "%s 플레이어는 한 번도 접속하지 않은 플레이어 입니다.", target)));
                    return false;
                }

                PlayerSkill playerSkill = SkillManager.getPlayerSkill(targetPlayer);
                playerSkill.addAll();
                sender.sendMessage(Color.colored(String.format(
                        prefix + "%s 플레이어에게 모든 스킬을 지급하였습니다.",
                        target
                )));
                return false;
            }

            if (len == 3) {
                String target = args[1];
                String skill = args[2];
                StatType type = StatType.getFromName(skill);
                if (type == null) {
                    sender.sendMessage(Color.colored(prefix + "근력, 마법, 민첩, 손재주 스텟을 입력해 주세요."));
                    return false;
                }

                if (target == null || target.isEmpty() || target.equals("@")) {
                    for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
                        if (offlinePlayer.hasPlayedBefore()) {
                            PlayerSkill playerSkill = SkillManager.getPlayerSkill(offlinePlayer);
                            playerSkill.addAll(type);
                        }
                    }
                    sender.sendMessage(Color.colored(String.format(prefix + "모든 플레이어에게 %s 스킬을 지급하였습니다", type.getName())));
                    return false;
                }

                OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(target);
                if (targetPlayer == null) {
                    sender.sendMessage(Color.colored(String.format(prefix + "%s 플레이어는 없는 플레이어입니다.")));
                    return false;
                }

                if (!targetPlayer.hasPlayedBefore()) {
                    sender.sendMessage(Color.colored(String.format(prefix + "%s 플레이어는 한 번도 접속하지 않은 플레이어 입니다.", target)));
                    return false;
                }

                PlayerSkill playerSkill = SkillManager.getPlayerSkill(targetPlayer);
                playerSkill.addAll(type);
                sender.sendMessage(Color.colored(String.format(
                        prefix + "%s 플레이어에게 %s 스킬을 지급하였습니다.",
                        target, type.getName()
                )));
                return false;
            }
            return false;
        }

        if (Comparator.containsIgnoreCase(arg1, "reload", "리로드")) {
            Config.reloadConfig();
            SkillConfig.reloadConfig();
            MenuConfig.reloadConfig();
            Bukkit.broadcastMessage(Color.colored(String.format(prefix + "&fSkill config reload.")));
            return false;
        }

        if (Comparator.containsIgnoreCase(arg1, "edit", "수정")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Color.colored("&c해당 명령어는 플레이어만 사용할 수 있습니다."));
                return false;
            }

            if (len < 2) {
                sendHelpMessage(sender);
                return false;
            }

            OfflinePlayer target = Bukkit.getPlayer(args[1]);
            if (target == null || !target.hasPlayedBefore()) {
                sender.sendMessage(Color.colored(String.format(prefix + "%s 플레이어는 없는 플레이어 입니다.", args[1])));
                return false;
            }

            Player player = (Player) sender;
            player.removeMetadata("debug", Skill.getInstance());
            player.setMetadata("editor", new FixedMetadataValue(Skill.getInstance(), target.getUniqueId().toString()));

            PlayerSkillMainMenu.openMenu(player);
            return false;
        }

        sendHelpMessage(sender);
        return false;
    }


    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (!sender.isOp()) {
            return Lists.newArrayList();
        }

        if (args.length == 0) {
            super.tabComplete(sender, alias, args);
        }

        if (args.length == 1) {
            return Lists.newArrayList(
                    "확인", "수정", "지급", "회수", "무시지급",
                    "전체지급", "스킬북", "초기화권", "초기화",
                    "전체초기화", "리로드"
            );
        }

        final String key = args[0];

        if (args.length == 2) {
            if (Comparator.containsIgnoreCase(key, "확인", "수정", "지급", "회수", "무시지급", "초기화")) {
                return super.tabComplete(sender, alias, args);
            }

            if (Comparator.containsIgnoreCase(key, "전체지급")) {
                List<String> value = super.tabComplete(sender, alias, args);
                value.add("@");
                return value;
            }

            if (Comparator.containsIgnoreCase(key, "초기화권")) {
                return Lists.newArrayList("1", "3", "5", "10", "32", "64");
            }

            if (Comparator.containsIgnoreCase(key, "스킬북")) {
                return SkillConfig.getConfig().getSkillNames();
            }
        }

        if (args.length == 3) {
            if (Comparator.containsIgnoreCase(key, "지급", "회수", "무시지급")) {
                return SkillConfig.getConfig().getSkillNames();
            }

            if (Comparator.containsIgnoreCase(key, "전체지급")) {
                return Lists.newArrayList("근력", "마법", "민첩", "손재주");
            }

            if (Comparator.containsIgnoreCase(key, "스킬북")) {
                return Lists.newArrayList("1", "3", "5", "10", "32", "64");
            }
        }

        // return super.tabComplete(sender, alias, args);
        return Lists.newArrayList();
    }

    private void sendHelpMessage(@NotNull CommandSender sender) {
        String prefix = Config.getConfig().getPrefix();
        sender.sendMessage(Color.colored(prefix + "/스킬관리 확인 <닉네임>"));
        sender.sendMessage(Color.colored(prefix + "/스킬관리 수정 <닉네임>"));
        sender.sendMessage(Color.colored(prefix + "/스킬관리 지급 <닉네임> <스킬>"));
        sender.sendMessage(Color.colored(prefix + "/스킬관리 회수 <닉네임> <스킬>"));
        sender.sendMessage(Color.colored(prefix + "/스킬관리 무시지급 <닉네임> <스킬>"));
        sender.sendMessage(Color.colored(prefix + "/스킬관리 전체지급 <닉네임|@> <스텟>"));
        sender.sendMessage(Color.colored(prefix + "/스킬관리 스킬북 <스킬> <갯수>"));
        sender.sendMessage(Color.colored(prefix + "/스킬관리 초기화권 <갯수>"));
        sender.sendMessage(Color.colored(prefix + "/스킬관리 초기화 <닉네임>"));
        sender.sendMessage(Color.colored(prefix + "/스킬관리 전체초기화"));
        sender.sendMessage(Color.colored(prefix + "/스킬관리 리로드"));
    }

}
