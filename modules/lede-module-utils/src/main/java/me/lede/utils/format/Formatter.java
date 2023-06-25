package me.lede.utils.format;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;

public class Formatter {

    private static final DecimalFormat formatter = new DecimalFormat("###,###.###");

    @NotNull
    public static String format(@Nullable Object obj) {
        if (obj == null) {
            return "";
        }

        return formatter.format(obj);
    }

    public static String formatSeconds(long sec) {
        int day = (int) (sec / 86400);
        int hour = (int) ((sec % 86400) / 3600);
        int minute = (int) (((sec % 86400) % 3600) / 60);
        int second = (int) (((sec % 86400) % 3600) % 60);

        String dayStr = (day > 0) ? " &a" + day + "&f일" : "";
        String hourStr = (hour > 0) ? " &a" + hour + "&f시간" : "";
        String minuteStr = (minute > 0) ? " &a" + minute + "&f분" : "";
        String secondStr = (second > 0) ? " &a" + second + "&f초" : "";

        return dayStr + hourStr + minuteStr + secondStr;
    }

}
