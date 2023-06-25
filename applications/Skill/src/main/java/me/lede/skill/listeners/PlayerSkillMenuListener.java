package me.lede.skill.listeners;

import me.lede.skill.Skill;
import me.lede.skill.config.Config;
import me.lede.skill.config.SkillConfig;
import me.lede.skill.objects.*;
import me.lede.stat.objects.StatType;
import me.lede.utils.color.Color;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class PlayerSkillMenuListener implements Listener {

    @EventHandler
    public void onInvClick(@NotNull InventoryClickEvent event) {
        InventoryView view = event.getView();
        Player player = (Player) event.getWhoClicked();
        int clicked = event.getRawSlot();

        StatType stat = getStatType(view);
        if (stat == null) {
            return;
        }

        event.setCancelled(true);
        if (isReturnSlot(clicked)) {
            PlayerSkillMainMenu.openMenu(player);
            return;
        }

        if (isDebugMode(player)) {
            sendDebugModeMessage(player);
            return;
        }

        SkillConfig.Type type = getTypeFromClickedSlot(clicked);
        if (type == null) {
            return;
        }

        if (isEditorMode(player)) {
            OfflinePlayer target = getEditTargetPlayer(player);
            PlayerSkill skill = SkillManager.getPlayerSkill(target);

            if (event.isLeftClick()) {
                if (Config.getConfig().isForceLearn()) {
                    skill.addSkill(type, stat);
                } else {
                    skill.learnSkill(type, stat);
                }
            }

            if (event.isRightClick()) {
                skill.removeSkill(type, stat);
            }

            skill.save();
            openStatMenu(player, target, stat);
            return;
        }

        PlayerSkill skill = SkillManager.getPlayerSkill(player);
        skill.learnSkill(type, stat);
        openStatMenu(player, player, stat);
    }

    private boolean isStrengthMenu(@NotNull InventoryView view) {
        return view.getTitle().equals(PlayerStrengthSkillMenu.TITLE);
    }

    private boolean isMagicMenu(@NotNull InventoryView view) {
        return view.getTitle().equals(PlayerMagicSkillMenu.TITLE);
    }

    private boolean isAgilityMenu(@NotNull InventoryView view) {
        return view.getTitle().equals(PlayerAgilitySkillMenu.TITLE);
    }

    private boolean isDeftnessMenu(@NotNull InventoryView view) {
        return view.getTitle().equals(PlayerDeftnessSkillMenu.TITLE);
    }

    private StatType getStatType(@NotNull InventoryView view) {
        if (isStrengthMenu(view)) {
            return StatType.STRENGTH;
        }

        if (isMagicMenu(view)) {
            return StatType.MAGIC;
        }

        if (isAgilityMenu(view)) {
            return StatType.AGILITY;
        }

        if (isDeftnessMenu(view)) {
            return StatType.DEFTNESS;
        }

        return null;
    }

    private void openStatMenu(@NotNull Player player, @NotNull OfflinePlayer target, @NotNull StatType stat) {
        switch (stat) {
            case STRENGTH: PlayerStrengthSkillMenu.openMenu(player, target); break;
            case MAGIC: PlayerMagicSkillMenu.openMenu(player, target); break;
            case AGILITY: PlayerAgilitySkillMenu.openMenu(player, target); break;
            case DEFTNESS: PlayerDeftnessSkillMenu.openMenu(player, target); break;
        }
    }

    @Nullable
    private SkillConfig.Type getTypeFromClickedSlot(int clicked) {
        int index = getSlotIndex(clicked);
        if (index == -1) {
            return null;
        }

        return SkillConfig.Type.values()[index];
    }

    private int getSlotIndex(int clicked) {
        return PlayerStrengthSkillMenu.SLOTS.indexOf(clicked);
    }

    private boolean isReturnSlot(int clicked) {
        return PlayerStrengthSkillMenu.SLOT_RETURN == clicked;
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

    private void sendDebugModeMessage(@NotNull Player player) {
        String prefix = Config.getConfig().getPrefix();
        player.sendMessage(Color.colored(prefix + "&f당신의 스킬창이 아닙니다."));
    }

}
