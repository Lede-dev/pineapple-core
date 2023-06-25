package me.lede.level.objects;

import com.google.common.collect.Lists;
import me.lede.utils.color.Color;
import me.lede.utils.format.Formatter;
import me.lede.utils.message.Prefix;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ExperienceBook {

    private static final String TAG_EXP = "exp_book_value";

    private final String display;
    private final List<String> lore;
    private final double exp;

    public ExperienceBook(@NotNull ItemStack item) {
        if (!isBook(item)) {
            throw new RuntimeException("경험치 아이템이 아닙니다.");
        }

        net.minecraft.server.v1_12_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = nmsItem.getTag() == null ? new NBTTagCompound() : nmsItem.getTag();
        this.exp = tag.getDouble(TAG_EXP);

        if (this.exp <= 0) {

        }

        ItemMeta meta = item.getItemMeta();
        this.display = meta.getDisplayName();
        this.lore = meta.getLore();
    }

    public ExperienceBook(double exp) {
        this.display = null;
        this.lore = null;
        this.exp = exp;
    }

    public ExperienceBook(String display, List<String> lore, double exp) {
        this.display = display;
        this.lore = lore;
        this.exp = exp;
    }

    public static boolean isBook(@Nullable ItemStack item) {
        if (item == null) {
            return false;
        }

        net.minecraft.server.v1_12_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = nmsItem.getTag() == null ? new NBTTagCompound() : nmsItem.getTag();
        return tag.hasKey(TAG_EXP);
    }

    public ItemStack getItemStack() {
        ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = item.getItemMeta();

        if (display == null) {
            meta.setDisplayName(Color.colored(String.format(
                    "&e경험치 [ &f%s &e]",
                    Formatter.format(exp)
            )));
        } else {
            meta.setDisplayName(display);
        }

        List<String> lore;
        if (this.lore == null) {
            lore = Lists.newArrayList(
                    Color.colored("&f"),
                    Color.colored("&f우클릭 시 사용가능"),
                    Color.colored("&f")
            );
        } else {
            lore = Lists.newArrayList(this.lore);
        }

        meta.setLore(lore);
        item.setItemMeta(meta);

        net.minecraft.server.v1_12_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = nmsItem.getTag() == null ? new NBTTagCompound() : nmsItem.getTag();
        tag.setDouble(TAG_EXP, this.exp);
        nmsItem.setTag(tag);
        return CraftItemStack.asBukkitCopy(nmsItem);
    }

    public double getExp() {
        return exp;
    }

}
