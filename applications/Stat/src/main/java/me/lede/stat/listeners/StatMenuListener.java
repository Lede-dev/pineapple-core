package me.lede.stat.listeners;

import me.lede.stat.config.Config;
import me.lede.stat.objects.*;
import me.lede.utils.color.Color;
import me.lede.utils.format.Formatter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class StatMenuListener implements Listener {

    @EventHandler
    public void onInvClick(InventoryClickEvent event) {
        InventoryView view = event.getView();
        if (isStatMenuView(view)) {
            event.setCancelled(true);
            Inventory inv = view.getTopInventory();
            Player player = (Player) event.getWhoClicked();
            int clicked = event.getRawSlot();
            ClickType click = event.getClick();

            String prefix = Config.getConfig().getPrefix();
            StatEditorMode mode = StatManager.getStatEditorMode(player);
            OfflinePlayer target = PlayerStatMenu.getTargetOfflinePlayer(inv);
            PlayerStat stat = StatManager.getPlayerStat(target);
            
            // 디버그 모드
            if (mode == StatEditorMode.DEBUG) {
                player.sendMessage(Color.colored(prefix + "&c당신의 스텟창이 아닙니다."));
                return;
            }

            if (isStrengthIcon(clicked)) {
                
                // 일반 모드
                if (mode == StatEditorMode.NORMAL) {
                    addStat(stat, StatType.STRENGTH);
                    playClickSound(player);
                    updateInventory(player, inv, stat);
                    return;
                }

                // 에디터 모드
                if (click.isLeftClick()) {
                    forceAddStat(player, stat, StatType.STRENGTH);
                    playClickSound(player);
                    updateInventory(player, inv, stat);
                    return;
                }

                if (click.isRightClick()) {
                    forceRemoveStat(player, stat, StatType.STRENGTH);
                    playClickSound(player);
                    updateInventory(player, inv, stat);
                }
                return;
            }

            if (isMagicIcon(clicked)) {
                
                // 일반 모드
                if (mode == StatEditorMode.NORMAL) {
                    addStat(stat, StatType.MAGIC);
                    playClickSound(player);
                    updateInventory(player, inv, stat);
                    return;
                }
                
                // 에디터 모드
                if (click.isLeftClick()) {
                    forceAddStat(player, stat, StatType.MAGIC);
                    playClickSound(player);
                    updateInventory(player, inv, stat);
                    return;
                }

                if (click.isRightClick()) {
                    forceRemoveStat(player, stat, StatType.MAGIC);
                    playClickSound(player);
                    updateInventory(player, inv, stat);
                }
                return;
            }

            if (isAgilityIcon(clicked)) {
                
                // 일반 모드
                if (mode == StatEditorMode.NORMAL) {
                    addStat(stat, StatType.AGILITY);
                    playClickSound(player);
                    updateInventory(player, inv, stat);
                    return;
                }
                
                // 에디터 모드
                if (click.isLeftClick()) {
                    forceAddStat(player, stat, StatType.AGILITY);
                    playClickSound(player);
                    updateInventory(player, inv, stat);
                    return;
                }

                if (click.isRightClick()) {
                    forceRemoveStat(player, stat, StatType.AGILITY);
                    playClickSound(player);
                    updateInventory(player, inv, stat);
                }
                return;
            }

            if (isDeftnessIcon(clicked)) {
                
                // 일반 모드
                if (mode == StatEditorMode.NORMAL) {
                    addStat(stat, StatType.DEFTNESS);
                    playClickSound(player);
                    updateInventory(player, inv, stat);
                    return;
                }
                
                // 에디터 모드
                if (click.isLeftClick()) {
                    forceAddStat(player, stat, StatType.DEFTNESS);
                    playClickSound(player);
                    updateInventory(player, inv, stat);
                    return;
                }

                if (click.isRightClick()) {
                    forceRemoveStat(player, stat, StatType.DEFTNESS);
                    playClickSound(player);
                    updateInventory(player, inv, stat);
                }
                return;
            }

            if (isAbilityIcon(clicked)) {
                if (mode != StatEditorMode.EDITOR) {
                    return;
                }

                // 에디터 모드
                if (click.isLeftClick()) {
                    forceAddAbility(player, stat);
                    playClickSound(player);
                    updateInventory(player, inv, stat);
                    return;
                }

                if (click.isRightClick()) {
                    forceRemoveAbility(player, stat);
                    playClickSound(player);
                    updateInventory(player, inv, stat);
                }
                return;
            }

            if (!Config.getConfig().isNetherStarStatEnabled()) {
                return;
            }

            if (isNetherStarIcon(clicked)) {
                
                // 일반 모드
                if (mode == StatEditorMode.NORMAL) {
                    buyNetherStarStat(player, stat);
                    playClickSound(player);
                    updateInventory(player, inv, stat);
                    return;
                }
                
                // 에디터 모드
                if (click.isLeftClick()) {
                    forceAddNetherStar(player, stat);
                    playClickSound(player);
                    updateInventory(player, inv, stat);
                    return;
                }

                if (click.isRightClick()) {
                    forceRemoveNetherStar(player, stat);
                    playClickSound(player);
                    updateInventory(player, inv, stat);
                }
            }
        }
    }

    private void addStat(@NotNull PlayerStat stat, @NotNull StatType type) {
        if (stat.isMaxValue(type)) {
            Player player = stat.getPlayer();
            if (player != null) {
                player.sendMessage( "&f현재 최대 스텟 포인트입니다.");
            }
            return;
        }

        if (!stat.hasStat(StatType.ABILITY, 1)) {
            return;
        }

        stat.addStat(type, 1);
        stat.removeStat(StatType.ABILITY, 1);
        stat.applyStat();
        stat.save();
    }

    private void forceAddStat(@NotNull Player moderator, @NotNull PlayerStat stat, @NotNull StatType type) {
        String prefix = Config.getConfig().getPrefix();
        if (stat.isMaxValue(type)) {
            moderator.sendMessage(Color.colored(prefix + "&c더 이상 스텟을 올릴 수 없습니다."));
            return;
        }

        stat.addStat(type, 1);
        stat.applyStat();
        stat.save();

        OfflinePlayer target = Bukkit.getOfflinePlayer(UUID.fromString(stat.getUniqueId()));
        moderator.sendMessage(Color.colored(String.format(
                prefix + "권한으로 %s 유저의 %s 스텟을 1개 추가하였습니다.",
                target.getName(),
                type.getName()
        )));

        Player targetOnline = target.getPlayer();
        if (targetOnline != null) {
            targetOnline.sendMessage(Color.colored(String.format(
                    prefix + "관리자 권한으로 %s 스텟이 1개 추가되었습니다.",
                    type.getName()
            )));
        }
    }

    private void forceRemoveStat(@NotNull Player moderator, @NotNull PlayerStat stat, @NotNull StatType type) {
        String prefix = Config.getConfig().getPrefix();
        if (stat.getStat(type) < 1) {
            moderator.sendMessage(Color.colored(prefix + "&c더 이상 스텟을 내릴 수 없습니다."));
            return;
        }

        stat.removeStat(type, 1);
        stat.applyStat();
        stat.save();

        OfflinePlayer target = Bukkit.getOfflinePlayer(UUID.fromString(stat.getUniqueId()));
        moderator.sendMessage(Color.colored(String.format(
                prefix + "권한으로 %s 유저의 %s 스텟을 1개 회수하였습니다.",
                target.getName(),
                type.getName()
        )));

        Player targetOnline = target.getPlayer();
        if (targetOnline != null) {
            targetOnline.sendMessage(Color.colored(String.format(
                    prefix + "관리자 권한으로 %s 스텟이 1개 회수되었습니다.",
                    type.getName()
            )));
        }
    }

    private void forceAddAbility(@NotNull Player moderator, @NotNull PlayerStat stat) {
        String prefix = Config.getConfig().getPrefix();
        stat.addStat(StatType.ABILITY, 1);
        stat.applyStat();
        stat.save();

        OfflinePlayer target = Bukkit.getOfflinePlayer(UUID.fromString(stat.getUniqueId()));
        moderator.sendMessage(Color.colored(String.format(
                prefix + "권한으로 %s 유저의 어빌리티가 1개 추가되었습니다.",
                target.getName()
        )));

        Player targetOnline = target.getPlayer();
        if (targetOnline != null) {
            targetOnline.sendMessage(Color.colored(
                    prefix + "관리자 권한으로 어빌리티가 1개 추가되었습니다."
            ));
        }
    }

    private void forceRemoveAbility(@NotNull Player moderator, @NotNull PlayerStat stat) {
        String prefix = Config.getConfig().getPrefix();
        if (!stat.hasStat(StatType.ABILITY, 1)) {
            moderator.sendMessage(Color.colored(prefix + "&c더 이상 어빌리티를 내릴 수 없습니다."));
            return;
        }

        stat.removeStat(StatType.ABILITY, 1);
        stat.applyStat();
        stat.save();

        OfflinePlayer target = Bukkit.getOfflinePlayer(UUID.fromString(stat.getUniqueId()));
        moderator.sendMessage(Color.colored(String.format(
                prefix + "권한으로 %s 유저의 어빌리티가 1개 회수되었습니다.",
                target.getName()
        )));

        Player targetOnline = target.getPlayer();
        if (targetOnline != null) {
            targetOnline.sendMessage(Color.colored(
                    prefix + "관리자 권한으로 어빌리티가 1개 회수되었습니다."
            ));
        }
    }

    private void buyNetherStarStat(@NotNull Player moderator, @NotNull PlayerStat stat) {
        String prefix = Config.getConfig().getPrefix();

        if (stat.isMaxValue(StatType.NETHER_STAR)) {
            Player player = stat.getPlayer();
            if (player != null) {
                player.sendMessage( "&f현재 최대 스텟 포인트입니다.");
            }
            return;
        }

        int level = stat.getStat(StatType.NETHER_STAR);
        int required = ((level + 1) / 10) + 1;

        int hasAmount = 0;
        for (ItemStack content : moderator.getInventory().getContents()) {
            if (content != null && content.getType() == Material.NETHER_STAR) {
                hasAmount += content.getAmount();
            }
        }

        if (required > hasAmount) {
            moderator.sendMessage(Color.colored(prefix + "보유중인 네더의 별이 부족하여 스탯을 구매할 수 없습니다."));
            return;
        }

        for (ItemStack content : moderator.getInventory().getContents()) {
            if (content != null && content.getType() == Material.NETHER_STAR) {
                int amount = content.getAmount();
                if (required >= amount) {
                    required -= amount;
                    content.setAmount(0);

                    if (required <= 0) {
                        break;
                    }

                } else {
                    content.setAmount(amount - required);
                    break;
                }
            }
        }

        stat.addStat(StatType.NETHER_STAR, 1);
        stat.applyStat();
        stat.save();

        moderator.sendMessage(Color.colored(String.format(
                prefix + "네더의 별 %s개를 사용하여 스탯을 구매하였습니다.",
                Formatter.format(((level + 1) / 10) + 1)
        )));
    }
    
    private void forceAddNetherStar(@NotNull Player moderator, @NotNull PlayerStat stat) {
        String prefix = Config.getConfig().getPrefix();

        if (stat.isMaxValue(StatType.NETHER_STAR)) {
            moderator.sendMessage(Color.colored(prefix + "&c더 이상 스텟을 올릴 수 없습니다."));
            return;
        }

        stat.addStat(StatType.NETHER_STAR, 1);
        stat.applyStat();
        stat.save();

        OfflinePlayer target = Bukkit.getOfflinePlayer(UUID.fromString(stat.getUniqueId()));
        moderator.sendMessage(Color.colored(String.format(
                prefix + "권한으로 %s 유저의 네더의 별 스탯이 1개 추가되었습니다.",
                target.getName()
        )));

        Player targetOnline = target.getPlayer();
        if (targetOnline != null) {
            targetOnline.sendMessage(Color.colored(
                    prefix + "관리자 권한으로 네더의 별 스탯이 1개 추가되었습니다."
            ));
        }
    }
    
    private void forceRemoveNetherStar(@NotNull Player moderator, @NotNull PlayerStat stat) {
        String prefix = Config.getConfig().getPrefix();

        if (!stat.hasStat(StatType.NETHER_STAR, 1)) {
            moderator.sendMessage(Color.colored(prefix + "&c더 이상 네더의 별 스탯을 내릴 수 없습니다."));
            return;
        }
        
        stat.removeStat(StatType.NETHER_STAR, 1);
        stat.applyStat();
        stat.save();

        OfflinePlayer target = Bukkit.getOfflinePlayer(UUID.fromString(stat.getUniqueId()));
        moderator.sendMessage(Color.colored(String.format(
                prefix + "권한으로 %s 유저의 네더의 별 스탯이 1개 회수되었습니다.",
                target.getName()
        )));

        Player targetOnline = target.getPlayer();
        if (targetOnline != null) {
            targetOnline.sendMessage(Color.colored(
                    prefix + "관리자 권한으로 네더의 별 스탯이 1개 회수되었습니다."
            ));
        }
    }

    private void updateInventory(@NotNull Player player, @NotNull Inventory inv, @NotNull PlayerStat stat) {
        PlayerStatMenu.updateStatMenu(inv, stat);
        player.updateInventory();
    }

    private void playClickSound(@NotNull Player player) {
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 2);
    }

    private boolean isStatMenuView(@NotNull InventoryView view) {
        return view.getTitle().equalsIgnoreCase(PlayerStatMenu.TITLE);
    }

    private boolean isStrengthIcon(int clicked) {
        return clicked == PlayerStatMenu.SLOT_STRENGTH;
    }

    private boolean isMagicIcon(int clicked) {
        return clicked == PlayerStatMenu.SLOT_MAGIC;
    }

    private boolean isAgilityIcon(int clicked) {
        return clicked == PlayerStatMenu.SLOT_AGILITY;
    }

    private boolean isDeftnessIcon(int clicked) {
        return clicked == PlayerStatMenu.SLOT_DEFTNESS;
    }

    private boolean isAbilityIcon(int clicked) {
        return clicked == PlayerStatMenu.SLOT_ABILITY;
    }

    private boolean isNetherStarIcon(int clicked) {
        return clicked == PlayerStatMenu.SLOT_NETHER_STAR;
    }


}
