package me.lede.level.events;

import me.lede.level.objects.PlayerLevel;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerLevelUpEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final OfflinePlayer player;
    private final PlayerLevel playerLevel;

    public PlayerLevelUpEvent(OfflinePlayer player, PlayerLevel playerLevel) {
        this.player = player;
        this.playerLevel = playerLevel;
    }

    public OfflinePlayer getPlayer() {
        return player;
    }

    public PlayerLevel getPlayerLevel() {
        return playerLevel;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
