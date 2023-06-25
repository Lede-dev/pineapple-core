package me.lede.utils.color;

import com.google.common.collect.Lists;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Color {

    @NotNull
    public static List<String> colored(@NotNull List<String> strs) {
        List<String> list = Lists.newArrayListWithCapacity(strs.size());
        for (String str : strs) {
            list.add(colored(str));
        }
        return list;
    }

    @NotNull
    public static String[] colored(@NotNull String... strs) {
        String[] arr = new String[strs.length];
        int index = 0;
        for (String str : strs) {
            arr[index++] = colored(str);
        }
        return arr;
    }

    @NotNull
    public static String colored(@NotNull String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    @NotNull
    public static List<String> uncolored(@NotNull List<String> strs) {
        List<String> list = Lists.newArrayListWithCapacity(strs.size());
        for (String str : strs) {
            list.add(uncolored(str));
        }
        return list;
    }

    @NotNull
    public static String[] uncolored(@NotNull String... strs) {
        String[] arr = new String[strs.length];
        int index = 0;
        for (String str : strs) {
            arr[index++] = uncolored(str);
        }
        return arr;
    }

    @NotNull
    public static String uncolored(@NotNull String str) {
        return ChatColor.stripColor(str);
    }
}
