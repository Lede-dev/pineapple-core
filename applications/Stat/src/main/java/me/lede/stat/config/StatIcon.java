package me.lede.stat.config;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class StatIcon {

    private int type;
    private int model;

    public int getType() {
        return type;
    }

    public int getModel() {
        return model;
    }

    @NotNull
    public ItemStack toItemStack() {
        if (model <= 0) {
            return new ItemStack(type, 1);
        }

        return new ItemStack(type, 1, (short) model);
    }

}
