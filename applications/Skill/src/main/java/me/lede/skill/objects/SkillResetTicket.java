package me.lede.skill.objects;

import me.lede.utils.color.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

public class SkillResetTicket {

    private final boolean isTicket;

    public SkillResetTicket() {
        isTicket = true;
    }

    public SkillResetTicket(@Nullable ItemStack item) {
        isTicket = isResetTicket(item);
    }

    public ItemStack toItemStack() {
        ItemStack item = new ItemStack(Material.BOOK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Color.colored("&b[ &f스킬 초기화권 &b]"));
        item.setItemMeta(meta);
        return item;
    }

    public static boolean isResetTicket(@Nullable ItemStack item) {
        if (item == null) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }
        
        String name = meta.getDisplayName();
        if (name == null) {
            return false;
        }

        return name.equals(Color.colored("&b[ &f스킬 초기화권 &b]"));
    }

    public boolean isTicket() {
        return isTicket;
    }

}
