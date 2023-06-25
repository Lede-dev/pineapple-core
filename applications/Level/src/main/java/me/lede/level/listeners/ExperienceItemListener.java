package me.lede.level.listeners;

import me.lede.level.api.LevelAPI;
import me.lede.level.config.Config;
import me.lede.level.objects.*;
import me.lede.utils.color.Color;
import me.lede.utils.format.Formatter;
import me.lede.utils.message.Prefix;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class ExperienceItemListener implements Listener {

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack tool = player.getEquipment().getItemInMainHand();

        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (ExperienceCoupon.isCoupon(tool)) {

            PlayerLevel level = LevelAPI.getPlayerLevel(player);
            ExperienceBuff buff = level.getExperienceBuff();

            if (buff.isEnabled()) {
                player.sendMessage(Color.colored(Prefix.EXP + "이미 배수버프 효과가 적용중 입니다."));
                return;
            }

            ExperienceCoupon coupon = new ExperienceCoupon(tool);
            buff.setBuff(coupon.getBuff());
            buff.setRemainSeconds(coupon.getDurationSeconds());
            level.save();

            int amount = tool.getAmount();
            tool.setAmount(--amount);

            player.sendMessage(Color.colored(String.format(
                    Prefix.EXP + "&a%s &f분 동안 경험치를 &6%s&f 배 획득합니다.",
                    Formatter.format(coupon.getDurationSeconds() / 60),
                    Formatter.format(coupon.getBuff())
            )));
            return;
        }

        if (ExperienceBook.isBook(tool)) {
            PlayerLevel level = LevelAPI.getPlayerLevel(player);
            Experience exp = level.getExperience();
            if (exp.isMaxLevel()) {
                player.sendMessage(Color.colored(
                        Prefix.LEVEL + "최고 레벨을 달성하여 더 이상 경험치책을 사용할 수 없습니다."
                ));
                return;
            }

            ExperienceBook book = new ExperienceBook(tool);
            double expAmount = book.getExp();

            if (Config.getConfig().isApplyBuffToExpBook()) {
                PlayerLevel playerLevel = LevelAPI.getPlayerLevel(player);
                ExperienceBuff buff = playerLevel.getExperienceBuff();
                if (buff.isEnabled()) {
                    expAmount *= buff.getBuff();
                }
            }

            LevelAPI.addExp(player, expAmount);

            int amount = tool.getAmount();
            tool.setAmount(--amount);

            if (Config.getConfig().isLevelMessageEnabled()) {
                player.sendMessage(Color.colored(String.format(
                        Prefix.EXP + "경험치책을 사용하여 경험치를 &a%s &f획득하였습니다. [ &e%s &f/ &6%s &f]",
                        Formatter.format(book.getExp()), Formatter.format(exp.getExp()), Formatter.format(exp.getNextLevelRequiredExp())
                )));
            }
        }
    }

}
