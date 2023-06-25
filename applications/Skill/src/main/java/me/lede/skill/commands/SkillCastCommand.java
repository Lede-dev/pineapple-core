package me.lede.skill.commands;

import com.google.common.collect.Lists;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.Spell;
import me.lede.skill.config.Config;
import me.lede.skill.objects.PlayerSkill;
import me.lede.skill.objects.SkillManager;
import me.lede.utils.color.Color;
import me.lede.utils.commands.AbstractCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SkillCastCommand extends AbstractCommand {

    public SkillCastCommand() {
        super("skillcast", "core");
        setDescription("스킬 발동 커맨드");
        setUsage("/sc");
        setAliases(Lists.newArrayList("sc", "스킬발동"));
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Color.colored("&c해당 명령어는 플레이어만 사용 가능합니다."));
            return false;
        }

        String prefix = Config.getConfig().getPrefix();
        if (args.length == 0) {
            sender.sendMessage(Color.colored(prefix + "사용할 스킬을 입력해 주세요."));
            return false;
        }

        String skill = args[0];
        Player player = (Player) sender;
        if (!player.hasPermission("core.skill." + skill)) {
            player.sendMessage(Color.colored(prefix + skill + " 스킬을 사용하려면 core.skill." + skill + " 퍼미션이 필요합니다."));
            return false;
        }

        PlayerSkill playerSkill = SkillManager.getPlayerSkill(player);
        boolean find = playerSkill.findSkillFromName(skill);
        if (!find) {
            player.sendMessage(Color.colored(prefix + skill + " 스킬은 사용할 수 없는 스킬입니다."));
            return false;
        }

        Spell magicSpell = MagicSpells.getSpellByInGameName(skill);
        if (magicSpell == null) {
            player.sendMessage(Color.colored(prefix + skill + " 스킬의 매직스펠을 찾을 수 없습니다."));
            return false;
        }

        magicSpell.cast(player);
        return false;
    }
}
