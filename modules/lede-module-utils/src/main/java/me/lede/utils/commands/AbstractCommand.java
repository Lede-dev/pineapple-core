package me.lede.utils.commands;

import org.bukkit.command.defaults.BukkitCommand;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractCommand extends BukkitCommand {

    private final String uniqueTag;

    protected AbstractCommand(@NotNull String name, @NotNull String uniqueTag) {
        super(name);
        this.uniqueTag = uniqueTag;
    }

    public String getUniqueTag() {
        return uniqueTag;
    }

}
