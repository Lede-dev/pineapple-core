package me.lede.skill.listeners;

import me.lede.skill.objects.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PlayerSkillMainMenuListener implements Listener {

    @EventHandler
    public void onInvClick(InventoryClickEvent event) {
        InventoryView view = event.getView();
        if (isMainView(view)) {
            event.setCancelled(true);
            int clicked = event.getRawSlot();
            Player player = (Player) event.getWhoClicked();

            if (isStrengthSlot(clicked)) {
                if (isDebugMode(player)) {
                    OfflinePlayer target = getTargetPlayer(player);
                    PlayerStrengthSkillMenu.openMenu(player, target);
                    return;
                }

                if (isEditorMode(player)) {
                    OfflinePlayer target = getEditTargetPlayer(player);
                    PlayerStrengthSkillMenu.openMenu(player, target);
                    return;
                }

                PlayerStrengthSkillMenu.openMenu(player, player);
                return;
            }

            if (isMagicSlot(clicked)) {
                if (isDebugMode(player)) {
                    OfflinePlayer target = getTargetPlayer(player);
                    PlayerMagicSkillMenu.openMenu(player, target);
                    return;
                }

                if (isEditorMode(player)) {
                    OfflinePlayer target = getEditTargetPlayer(player);
                    PlayerMagicSkillMenu.openMenu(player, target);
                    return;
                }

                PlayerMagicSkillMenu.openMenu(player, player);
                return;
            }

            if (isAgilitySlot(clicked)) {
                if (isDebugMode(player)) {
                    OfflinePlayer target = getTargetPlayer(player);
                    PlayerAgilitySkillMenu.openMenu(player, target);
                    return;
                }

                if (isEditorMode(player)) {
                    OfflinePlayer target = getEditTargetPlayer(player);
                    PlayerAgilitySkillMenu.openMenu(player, target);
                    return;
                }

                PlayerAgilitySkillMenu.openMenu(player, player);
                return;
            }

            if (isDeftnessSlot(clicked)) {
                if (isDebugMode(player)) {
                    OfflinePlayer target = getTargetPlayer(player);
                    PlayerDeftnessSkillMenu.openMenu(player, target);
                    return;
                }

                if (isEditorMode(player)) {
                    OfflinePlayer target = getEditTargetPlayer(player);
                    PlayerDeftnessSkillMenu.openMenu(player, target);
                    return;
                }

                PlayerDeftnessSkillMenu.openMenu(player, player);
            }
        }
    }

    private boolean isMainView(@NotNull InventoryView view) {
        return view.getTitle().equals(PlayerSkillMainMenu.TITLE);
    }

    private boolean isStrengthSlot(int clicked) {
        return clicked == PlayerSkillMainMenu.SLOT_STRENGTH;
    }

    private boolean isMagicSlot(int clicked) {
        return clicked == PlayerSkillMainMenu.SLOT_MAGIC;
    }

    private boolean isAgilitySlot(int clicked) {
        return clicked == PlayerSkillMainMenu.SLOT_AGILITY;
    }

    private boolean isDeftnessSlot(int clicked) {
        return clicked == PlayerSkillMainMenu.SLOT_DEFTNESS;
    }

    private boolean isDebugMode(@NotNull Player player) {
//        if (!player.hasMetadata("debug")) {
//            return false;
//        }
//
//        String targetUniqueId = player.getMetadata("debug").get(0).asString();
//        return !player.getUniqueId().toString().equals(targetUniqueId);

        return player.hasMetadata("debug");
    }

    private OfflinePlayer getTargetPlayer(@NotNull Player player) {
        String targetUniqueId = player.getMetadata("debug").get(0).asString();
        return Bukkit.getOfflinePlayer(UUID.fromString(targetUniqueId));
    }

    private boolean isEditorMode(@NotNull Player player) {
//        if (!player.hasMetadata("editor")) {
//            return false;
//        }
//
//        String targetUniqueId = player.getMetadata("editor").get(0).asString();
//        return !player.getUniqueId().toString().equals(targetUniqueId);
        return player.hasMetadata("editor");
    }

    private OfflinePlayer getEditTargetPlayer(@NotNull Player player) {
        String targetUniqueId = player.getMetadata("editor").get(0).asString();
        return Bukkit.getOfflinePlayer(UUID.fromString(targetUniqueId));
    }

}
