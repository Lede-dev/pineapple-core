package me.lede.stat.objects;

import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public enum StatType {

    STRENGTH("근력", "&c"),
    MAGIC("마법", "&b"),
    AGILITY("민첩", "&e"),
    DEFTNESS("손재주", "&2"),
    NETHER_STAR("네더의 별", "&f"),
    ABILITY("어빌리티", "&6")
    ;

    private final String name;
    private final String color;

    StatType(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public static List<String> getNameList() {
        List<String> names = Lists.newArrayListWithCapacity(values().length);
        for (StatType value : values()) {
            names.add(value.getName());
        }
        return names;
    }

    @Nullable
    public static StatType getFromName(@NotNull String name) {
        for (StatType type : values()) {
            if (type.name.equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }

}
