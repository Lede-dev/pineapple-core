package me.lede.utils.format;

import org.jetbrains.annotations.Nullable;

public class Parser {

    public static int parseInteger(@Nullable String str) {
        if (str == null) {
            return 0;
        }

        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static double parseDouble(@Nullable String str) {
        if (str == null) {
            return 0.0d;
        }

        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return 0.0d;
        }
    }

}
