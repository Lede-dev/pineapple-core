package me.lede.stat.objects;

import me.lede.utils.color.Color;
import me.lede.utils.format.Formatter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PassiveBook {

    private static final String NAME_START = "[ Lv.";
    private static final String NAME_END = " 패시브 ]";

    private final int level;

    public PassiveBook(int level) {
        this.level = level;
    }

    public PassiveBook(@Nullable ItemStack item) {
        if (!isPassiveBook(item)) {
            this.level = 0;
        } else {
            this.level = getPassiveLevelFromItem(item);
        }
    }

    public int getLevel() {
        return level;
    }

    public static boolean isPassiveBook(@Nullable ItemStack item) {
        if (item == null) {
            return false;
        }

        Material type = item.getType();
        if (type != Material.ENCHANTED_BOOK) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }

        String colored = meta.getDisplayName();
        if (colored == null) {
            return false;
        }

        String name = Color.uncolored(colored);
        return name.startsWith(NAME_START) && name.endsWith(NAME_END);
    }

    public int getPassiveLevelFromItem(@NotNull ItemStack item) {
        String name = Color.uncolored(item.getItemMeta().getDisplayName());
        String value = name.replace(NAME_START, "").replace(NAME_END, "");
        return Integer.parseInt(value);
    }

    public ItemStack getItemStack() {
        ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Color.colored(String.format(
                "&f[ Lv.%s 패시브 ]",
                Formatter.format(level)
        )));

        item.setItemMeta(meta);
        return item;
    }
}
