package me.lede.skill.listeners;

import me.lede.skill.config.Config;
import me.lede.skill.config.SkillConfig;
import me.lede.skill.objects.PlayerSkill;
import me.lede.skill.objects.SkillBook;
import me.lede.skill.objects.SkillManager;
import me.lede.skill.objects.SkillResetTicket;
import me.lede.stat.objects.StatType;
import me.lede.utils.color.Color;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class SkillTicketListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = player.getEquipment().getItemInMainHand();
        String prefix = Config.getConfig().getPrefix();

        if (SkillResetTicket.isResetTicket(item)) {
            PlayerSkill playerSkill = SkillManager.getPlayerSkill(player);
            playerSkill.reset();

            int amount = item.getAmount();
            item.setAmount(amount - 1);

            player.sendMessage(Color.colored(prefix + "&c스킬 초기화권&f을 사용하였습니다."));
            return;
        }

        if (SkillBook.isSkillBook(item)) {
            SkillBook book = new SkillBook(item);
            String skill = Objects.requireNonNull(book.getSkill());

            PlayerSkill playerSkill = SkillManager.getPlayerSkill(player);
            if (playerSkill.hasSkill(skill)) {
                player.sendMessage(Color.colored(prefix + "&f이미 배운 스킬입니다."));
                return;
            }

            StatType statType = Objects.requireNonNull(SkillConfig.getConfig().getStatTypeByName(skill));
            SkillConfig.Type skillType = Objects.requireNonNull(SkillConfig.getConfig().getSkillTypeByName(skill));
            playerSkill.addSkill(skillType, statType);
            playerSkill.save();

            int amount = item.getAmount();
            item.setAmount(amount - 1);

            player.sendMessage(Color.colored(String.format(
                    prefix + "&f%s 스킬을 배웠습니다.", skill
            )));
        }
    }

}
