package me.lede.stat.api;

import me.lede.stat.objects.PlayerStat;
import me.lede.stat.objects.StatManager;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class StatAPI {

    @NotNull
    public static PlayerStat getPlayerStat(@NotNull OfflinePlayer player) {
        return StatManager.getPlayerStat(player);
    }



}
