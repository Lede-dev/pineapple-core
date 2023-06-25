package me.lede.skill.commands;

import com.google.common.collect.Lists;
import me.lede.skill.Skill;
import me.lede.skill.config.Config;
import me.lede.skill.objects.PlayerSkillMainMenu;
import me.lede.stat.objects.StatEditorMode;
import me.lede.stat.objects.StatManager;
import me.lede.utils.color.Color;
import me.lede.utils.commands.AbstractCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class SkillCommand extends AbstractCommand {

    public SkillCommand() {
        super("skill", "core");
        setDescription("스킬 커맨드");
        setUsage("/스킬");
        setAliases(Lists.newArrayList("스킬", "tmzlf"));
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        String prefix = Config.getConfig().getPrefix();
        if (!(sender instanceof Player)) {
            sender.sendMessage(Color.colored(prefix + "&c해당 명령어는 플레이어만 사용할 수 있습니다."));
            return false;
        }

        Player player = (Player) sender;
        player.removeMetadata("editor", Skill.getInstance());
        player.removeMetadata("debug", Skill.getInstance());
        PlayerSkillMainMenu.openMenu(player);
        return false;
    }

}
