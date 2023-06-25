package me.lede.utils.registry;

import me.lede.utils.commands.AbstractCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

public class CommandRegistry {

    public static void register(@NotNull AbstractCommand command) {
        CommandMap commandMap = getCommandMap();
        if (commandMap == null) {
            throw new NullPointerException("Cannot find CommandMap.");
        }

        commandMap.register(command.getUniqueTag(), command);
    }

    @Nullable
    public static CommandMap getCommandMap() {
        try {
            final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            bukkitCommandMap.setAccessible(true);
            return (CommandMap) bukkitCommandMap.get(Bukkit.getServer());
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
