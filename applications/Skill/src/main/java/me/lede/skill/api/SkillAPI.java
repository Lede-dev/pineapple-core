package me.lede.skill.api;

import me.lede.skill.objects.PlayerSkill;
import me.lede.skill.objects.SkillManager;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class SkillAPI {

    public static void resetSkill(@NotNull OfflinePlayer offlinePlayer) {
        PlayerSkill skill = SkillManager.getPlayerSkill(offlinePlayer);
        skill.reset();
    }

}
