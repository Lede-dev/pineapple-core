package me.lede.utils.message;

import org.jetbrains.annotations.NotNull;

public class Comparator {

    public static boolean contains(@NotNull String criteria, @NotNull String... strs) {
        for (String str : strs) {
            if (criteria.equals(str)) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsIgnoreCase(@NotNull String criteria, @NotNull String... strs) {
        for (String str : strs) {
            if (criteria.equalsIgnoreCase(str)) {
                return true;
            }
        }
        return false;
    }

}
