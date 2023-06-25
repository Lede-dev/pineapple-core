package me.lede.stat.utils;

import com.google.common.collect.Lists;
import me.lede.utils.color.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

public class Items {

    public static ItemStack getAdditionalAbilityTicket() {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Color.colored("&b[ &f추가 스텟권 &b]"));
        meta.setLore(Lists.newArrayList(Color.colored("&f우클릭 시 스텟 포인트를 획득합니다.")));
        item.setItemMeta(meta);

        return item;
    }

    public static boolean isAdditionalAbilityTicket(@Nullable ItemStack item) {
        if (item == null) {
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
        return name.contains("추가 스텟권");
    }

    public static ItemStack getResetAbilityTicket() {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Color.colored("&b[ &f스텟 초기화권 &b]"));
        meta.setLore(Lists.newArrayList(Color.colored("&f우클릭 시 스텟 포인트를 초기화 합니다.")));
        item.setItemMeta(meta);

        return item;
    }

    public static boolean isResetAbilityTicket(@Nullable ItemStack item) {
        if (item == null) {
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
        return name.contains("스텟 초기화권");
    }
    
    public static ItemStack getStatRandomTicket() {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Color.colored("&b[ &f스텟 뽑기권 &b]"));
        item.setItemMeta(meta);

        return item;
    }

    public static boolean isStatRandomTicket(@Nullable ItemStack item) {
        if (item == null) {
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
        return name.contains("스텟 뽑기권");
    }

    public static ItemStack getBackgroundIcon() {
        ItemStack item = new ItemStack(Material.STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(" ");
        item.setItemMeta(meta);
        return item;
    }

}
