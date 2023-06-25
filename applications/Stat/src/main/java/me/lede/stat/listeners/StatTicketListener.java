package me.lede.stat.listeners;

import me.lede.level.api.LevelAPI;
import me.lede.stat.config.Config;
import me.lede.stat.objects.PassiveBook;
import me.lede.stat.objects.PlayerStat;
import me.lede.stat.objects.StatManager;
import me.lede.stat.objects.StatType;
import me.lede.stat.utils.Items;
import me.lede.utils.color.Color;
import me.lede.utils.format.Formatter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class StatTicketListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack tool = player.getEquipment().getItemInMainHand();

        if (Items.isAdditionalAbilityTicket(tool)) {
            Config config = Config.getConfig();
            PlayerStat stat = StatManager.getPlayerStat(player);
            stat.addStat(StatType.ABILITY, config.getAdditionalStatPoint());
            stat.save();
            tool.setAmount(tool.getAmount() - 1);
            player.sendMessage(Color.colored(config.getPrefix() + " + 1"));
            return;
        }

        if (Items.isResetAbilityTicket(tool)) {
            Config config = Config.getConfig();
            PlayerStat stat = StatManager.getPlayerStat(player);
            stat.reset();
            stat.save();
            tool.setAmount(tool.getAmount() - 1);
            player.sendMessage(Color.colored(config.getPrefix() + " 스텟초기화권을 사용하셨습니다."));
            return;
        }

        if (Items.isStatRandomTicket(tool)) {
            Config config = Config.getConfig();
            Inventory inv = player.getInventory();
            if (inv.firstEmpty() == -1) {
                player.sendMessage(Color.colored(config.getPrefix() + " 인벤토리 공간이 부족하여 사용할 수 없습니다."));
                return;
            }

            tool.setAmount(tool.getAmount() - 1);

            int amount = (int) ((Math.random() * (config.getStatRandomTicketMaxValue() - 1)) + 1);
            ItemStack item = Items.getAdditionalAbilityTicket();
            item.setAmount(amount);
            inv.addItem(item);
            player.sendMessage(Color.colored(config.getPrefix() + " 스텟 + " + amount));
            return;
        }

        if (PassiveBook.isPassiveBook(tool)) {
            Config config = Config.getConfig();
            PlayerStat stat = StatManager.getPlayerStat(player);

            PassiveBook book = new PassiveBook(tool);
            int level = book.getLevel();

            if (LevelAPI.getLevel(player) < level) {
                player.sendMessage(Color.colored("&b[ McAge3 ] &f레벨이 부족하여 사용할 수 없습니다."));
                return;
            }

            stat.setPassiveLevel(stat.getPassiveLevel() + 1);
            stat.applyStat();
            stat.save();
            tool.setAmount(tool.getAmount() - 1);
            player.sendMessage(Color.colored(String.format(
                    "&b[ McAge3 ] &fLv.%s 패시브 획득 &7( 데미지 감소 %s%% )",
                    Formatter.format(level),
                    Formatter.format(stat.getPassiveLevel() * config.getPassivePercentage())
            )));
        }
    }

}
