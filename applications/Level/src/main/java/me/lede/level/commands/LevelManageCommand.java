package me.lede.level.commands;

import com.google.common.collect.Lists;
import me.lede.level.api.LevelAPI;
import me.lede.level.config.Config;
import me.lede.level.events.PlayerLevelUpEvent;
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
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class LevelManageCommand extends AbstractCommand {

    public LevelManageCommand() {
        super("leveladmin", "core");
        setDescription("레벨 관리 커맨드");
        setUsage("/레벨관리");
        setAliases(Lists.newArrayList("레벨관리"));
        setPermission("op.command.level");
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
        if (Comparator.containsIgnoreCase(arg1, "show", "확인")) {
            if (len < 2) {
                sendHelpMessage(sender);
                return false;
            }

            String name = args[1];
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);
            if (offlinePlayer == null) {
                sender.sendMessage(Color.colored(String.format(
                        Prefix.LEVEL + "&6%s &f플레이어는 서버에 접속한 기록이 없는 플레이어 입니다.",
                        name
                )));
                return false;
            }

            PlayerLevel level = LevelManager.getPlayerLevel(offlinePlayer);
            sender.sendMessage(Color.colored(String.format(
                    Prefix.LEVEL + "&6%s &f플레이어의 레벨 정보.",
                    name
            )));
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

        if (Comparator.containsIgnoreCase(arg1, "set", "설정")) {
            if (len < 3) {
                sendHelpMessage(sender);
                return false;
            }

            OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
            int level = Parser.parseInteger(args[2]);
            LevelAPI.setLevel(player, level);
            LevelAPI.syncPlayerExpToExpBar(player);
            sender.sendMessage(Color.colored(String.format(
                    Prefix.LEVEL + "&6%s &f플레이어의 레벨을 &e%s &f(으)로 설정합니다.",
                    args[1], Formatter.format(level)
            )));
            return false;
        }

        if (Comparator.containsIgnoreCase(arg1, "exp", "경험치")) {
            if (len < 3) {
                sendHelpMessage(sender);
                return false;
            }

            int level = Parser.parseInteger(args[1]);
            double exp = Parser.parseDouble(args[2]);

            Config.getConfig().setRequiredExp(level, exp);
            sender.sendMessage(Color.colored(String.format(
                    Prefix.LEVEL + "&e%s &f-> &a%s &f레벨업 요구 경험치를 &6%s &f로 설정하였습니다.",
                    Formatter.format(level - 1), Formatter.format(level), Formatter.format(Config.getConfig().getRequiredExp(level)))
            ));
            return false;
        }

        if (Comparator.containsIgnoreCase(arg1, "removeExp", "경험치감소")) {
            if (len < 2) {
                sendHelpMessage(sender);
                return false;
            }

            double removeValue = Math.min(Parser.parseDouble(args[1]), 100);
            removeValue = Math.max(removeValue, 0);
            Config.getConfig().setRemoveExpPercentage(removeValue);
            sender.sendMessage(Color.colored(String.format(
                    Prefix.LEVEL + "사망 시 감소시킬 경험치를 &e%s%% &f로 설정합니다.",
                    Formatter.format(removeValue)
            )));
            return false;
        }

        if (Comparator.containsIgnoreCase(arg1, "maxLevel", "최대레벨설정")) {
            if (len < 2) {
                sendHelpMessage(sender);
                return false;
            }

            int maxLevel = Math.max(Parser.parseInteger(args[1]), 0);
            Config.getConfig().setMaxLevel(maxLevel);
            sender.sendMessage(Color.colored(String.format(
                    Prefix.LEVEL + "최대 레벨을 경험치를 &e%s &f(으)로 설정합니다.",
                    Formatter.format(maxLevel)
            )));
            return false;
        }

        if (Comparator.containsIgnoreCase(arg1, "list", "레벨목록")) {
            if (len < 2) {
                sendHelpMessage(sender);
                return false;
            }

            int page = Math.max(Parser.parseInteger(args[1]), 1);
            int start = ((page - 1) * 20) + 1;
            int end = page * 20;
            sender.sendMessage(Color.colored(String.format(
                    Prefix.LEVEL + "&e%s &f~ &6%s &f레벨 요구 경험치 출력",
                    Formatter.format(start), Formatter.format(end)
            )));

            Config config = Config.getConfig();
            Map<Integer, String> exps = config.getRequiredExpMap();
            for (int i = start; i <= end; i++) {
                String value = exps.get(i);
                if (value != null) {
                    sender.sendMessage(Color.colored(String.format(
                            Prefix.LEVEL + "&e%s &f-> &6%s &f요구 경험치: &a%s",
                            i - 1, i, Formatter.format(Parser.parseDouble(value))
                    )));
                }
            }
            return false;
        }

        if (Comparator.containsIgnoreCase(arg1, "reload", "리로드")) {
            Config.reloadConfig();
            sender.sendMessage(Color.colored(Prefix.LEVEL + "레벨 콘피그를 리로드 합니다."));
            return false;
        }

        if (Comparator.containsIgnoreCase(arg1, "add", "추가")) {
            if (!Bukkit.getPluginManager().isPluginEnabled("Stat")) {
                sender.sendMessage(Color.colored(
                        Prefix.LEVEL + "스텟 플러그인이 로드되지 않아 해당 기능을 사용할 수 없습니다."
                ));
                return false;
            }

            if (len < 3) {
                sendHelpMessage(sender);
                return false;
            }

            OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
            int value = Parser.parseInteger(args[2]);
            PlayerLevel level = LevelManager.getPlayerLevel(target);

            for (int i = 0; i < value; i++) {
                level.getExperience().addLevel(1);
                PlayerLevelUpEvent event = new PlayerLevelUpEvent(target, level);
                Bukkit.getPluginManager().callEvent(event);
            }

            level.save();

            sender.sendMessage(Color.colored(String.format(
                    Prefix.LEVEL + "%s 플레이어에게 %s 레벨을 추가합니다.",
                    args[1], Formatter.format(value)
            )));
            return false;
        }

        if (Comparator.containsIgnoreCase(arg1, "msg", "message", "메시지", "메세지")) {
            Config config = Config.getConfig();
            boolean state = config.isLevelMessageEnabled();
            config.setLevelMessage(!state);

            String msg = config.isLevelMessageEnabled() ? "&a활성화" : "&c비활성화";
            sender.sendMessage(Color.colored(String.format(
                    Prefix.LEVEL + "경험치 획득 메시지 출력을 %s &f합니다.",
                    msg
            )));
            return false;
        }

        sendHelpMessage(sender);
        return false;
    }

    private void sendHelpMessage(@NotNull CommandSender sender) {
        sender.sendMessage(Color.colored(Prefix.LEVEL + "&e/레벨관리 확인 &6<플레이어> &f- 플레이어의 레벨과 경험치를 확인합니다."));
        sender.sendMessage(Color.colored(Prefix.LEVEL + "&e/레벨관리 설정 &6<플레이어> &a<레벨> &f- 플레이어의 레벨을 설정합니다."));
        sender.sendMessage(Color.colored(Prefix.LEVEL + "&e/레벨관리 추가 &6<플레이어> &a<레벨> &f- 플레이어의 레벨을 추가합니다. [스텟 지급]"));
        sender.sendMessage(Color.colored(Prefix.LEVEL + "&e/레벨관리 경험치 &6<레벨> &a<수량> &f- 레벨업에 필요한 경험치를 설정합니다."));
        sender.sendMessage(Color.colored(Prefix.LEVEL + "&e/레벨관리 경험치감소 &a<퍼센트> &f- 사망 시 잃는 경험치를 설정합니다."));
        sender.sendMessage(Color.colored(Prefix.LEVEL + "&e/레벨관리 최대레벨설정 &a<레벨> &f- 최대레벨을 설정합니다."));
        sender.sendMessage(Color.colored(Prefix.LEVEL + "&e/레벨관리 레벨목록 &a<페이지> &f- 레벨업에 필요한 경험치 목록을 확인합니다."));
        sender.sendMessage(Color.colored(Prefix.LEVEL + "&e/레벨관리 리로드 &f- 콘피그를 직접 수정할 경우, 콘피그를 리로드 합니다."));
        sender.sendMessage(Color.colored(Prefix.LEVEL + "&e/레벨관리 메시지 &f- 경험치 획득 메시지 출력을 활성화/비활성화 합니다."));
    }



}
