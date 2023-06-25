package me.lede.skill.objects;

import me.lede.skill.config.MenuConfig;
import me.lede.utils.color.Color;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PlayerSkillMainMenu {

    public static final String TITLE = Color.colored("&8스텟 스킬");
    public static final int SIZE = 27;

    public static final int SLOT_STRENGTH = 10;
    public static final int SLOT_MAGIC = 12;
    public static final int SLOT_AGILITY = 14;
    public static final int SLOT_DEFTNESS = 16;

    public static void openMenu(@NotNull Player player) {
        Inventory inv = createMenu();
        player.openInventory(inv);
    }

    private static Inventory createMenu() {
        Inventory inv = Bukkit.createInventory(null, SIZE, TITLE);
        setupBackground(inv);
        setupIcons(inv);
        return inv;
    }

    private static void setupBackground(@NotNull Inventory inv) {
        ItemStack icon = MenuConfig.getBackground();
        for (int i = 0; i < SIZE; i++) {
            inv.setItem(i, icon);
        }
    }

    private static void setupIcons(@NotNull Inventory inv) {
        MenuConfig.MainMenuIconGroup group = MenuConfig.getMainConfig();
        inv.setItem(SLOT_STRENGTH, group.getStrength().toItemStack());
        inv.setItem(SLOT_MAGIC, group.getMagic().toItemStack());
        inv.setItem(SLOT_AGILITY, group.getAgility().toItemStack());
        inv.setItem(SLOT_DEFTNESS, group.getDeftness().toItemStack());
    }

}
