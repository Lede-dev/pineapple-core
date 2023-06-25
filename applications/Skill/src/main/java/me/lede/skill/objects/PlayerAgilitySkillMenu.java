package me.lede.skill.objects;

import com.google.common.collect.Lists;
import me.lede.skill.config.MenuConfig;
import me.lede.skill.config.SkillConfig;
import me.lede.utils.color.Color;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class PlayerAgilitySkillMenu {

    public static final String TITLE = Color.colored("&8민첩스킬");
    public static final int SIZE = 45;

    public static final List<Integer> SLOTS = Lists.newArrayList(
            11, 13, 15, 28, 30, 32, 34
    );

    public static final int SLOT_RETURN = 40;

    public static void openMenu(@NotNull Player player, @NotNull OfflinePlayer target) {
        Inventory inv = createMenu(target);
        player.openInventory(inv);
    }

    public static Inventory createMenu(@NotNull OfflinePlayer target) {
        Inventory inv = Bukkit.createInventory(null, SIZE, TITLE);
        setupBackground(inv);
        setupIcons(inv, target);
        return inv;
    }

    private static void setupBackground(@NotNull Inventory inv) {
        ItemStack icon = MenuConfig.getBackground();
        for (int i = 0; i < SIZE; i++) {
            inv.setItem(i, icon);
        }

        inv.setItem(SLOT_RETURN, MenuConfig.getReturn());
    }

    private static void setupIcons(@NotNull Inventory inv, @NotNull OfflinePlayer target) {
        PlayerSkill skill = SkillManager.getPlayerSkill(target);
        MenuConfig.MenuIconGroup config = MenuConfig.getAgilityConfig();
        Map<SkillConfig.Type, MenuConfig.MenuIcon> icons = config.getIcons();

        int index = 0;
        for (Map.Entry<SkillConfig.Type, MenuConfig.MenuIcon> entry : icons.entrySet()) {
            SkillConfig.Type type = entry.getKey();
            MenuConfig.MenuIcon icon = entry.getValue();
            int slot = SLOTS.get(index++);
            if (skill.hasAgilitySkill(type)) {
                inv.setItem(slot, icon.getOpenedIcon());
            } else {
                inv.setItem(slot, icon.getClosedIcon());
            }
        }
    }
    
}
