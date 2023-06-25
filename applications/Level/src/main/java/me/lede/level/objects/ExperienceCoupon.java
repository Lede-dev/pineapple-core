package me.lede.level.objects;

import com.google.common.collect.Lists;
import me.lede.utils.color.Color;
import me.lede.utils.format.Formatter;
import me.lede.utils.message.Prefix;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ExperienceCoupon {

    private static final String TAG_BUFF_VALUE = "tag:exp_buff_value";
    private static final String TAG_BUFF_DURATION = "tag:exp_buff_duration";

    private final double buff;
    private final long durationSeconds;

    public ExperienceCoupon(@NotNull ItemStack item) {
        if (!isCoupon(item)) {
            throw new RuntimeException("쿠폰 아이템이 아닙니다.");
        }

        net.minecraft.server.v1_12_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = nmsItem.getTag() == null ? new NBTTagCompound() : nmsItem.getTag();
        this.buff = tag.getDouble(TAG_BUFF_VALUE);
        this.durationSeconds = tag.getLong(TAG_BUFF_DURATION);
    }

    public ExperienceCoupon(double buff, long durationSeconds) {
        this.buff = buff;
        this.durationSeconds = durationSeconds;
    }

    public static boolean isCoupon(@Nullable ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }

        net.minecraft.server.v1_12_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound tag = nmsItem.getTag() == null ? new NBTTagCompound() : nmsItem.getTag();
        return tag.hasKey(TAG_BUFF_VALUE);
    }

    public ItemStack getItemStack() {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Color.colored(String.format(
                "&e경험치 %s배 쿠폰",
                Formatter.format(buff)
        )));

        List<String> lore = Lists.newArrayList(
                Color.colored(String.format(
                        "&f시간 : &6%s분",
                        Formatter.format(durationSeconds / 60)
                )),
                Color.colored(String.format(
                        "&f경험치 획득량 : &6%s배",
                        Formatter.format(buff)
                ))
        );
        meta.setLore(lore);
        item.setItemMeta(meta);

        net.minecraft.server.v1_12_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = nmsItem.getTag() == null ? new NBTTagCompound() : nmsItem.getTag();
        tag.setDouble(TAG_BUFF_VALUE, this.buff);
        tag.setLong(TAG_BUFF_DURATION, this.durationSeconds);
        nmsItem.setTag(tag);
        return CraftItemStack.asBukkitCopy(nmsItem);
    }

    public double getBuff() {
        return buff;
    }

    public long getDurationSeconds() {
        return durationSeconds;
    }

}
