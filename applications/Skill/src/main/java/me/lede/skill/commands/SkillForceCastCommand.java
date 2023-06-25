package me.lede.skill.commands;

import com.google.common.collect.Lists;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.Spell;
import me.lede.skill.config.Config;
import me.lede.utils.color.Color;
import me.lede.utils.commands.AbstractCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SkillForceCastCommand extends AbstractCommand {

    public SkillForceCastCommand(@NotNull String name, @NotNull String uniqueTag) {
        super("forcecast", "core");
        setDescription("스킬 발동 커맨드");
        setUsage("/fc");
        setAliases(Lists.newArrayList("fc", "스킬강제발동"));
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Color.colored("&c해당 명령어는 플레이어만 사용 가능합니다."));
            return false;
        }

        String prefix = Config.getConfig().getPrefix();
        if (args.length == 0) {
            sender.sendMessage(Color.colored(prefix + "사용할 스킬을 입력해 주세요."));
            return false;
        }

        Player player = (Player) sender;

        String skill = args[0];
        Spell magicSpell = MagicSpells.getSpellByInGameName(skill);
        if (magicSpell == null) {
            player.sendMessage(Color.colored(prefix + skill + " 스킬의 매직스펠을 찾을 수 없습니다."));
            return false;
        }

        magicSpell.cast(player);
        return false;
    }

}
