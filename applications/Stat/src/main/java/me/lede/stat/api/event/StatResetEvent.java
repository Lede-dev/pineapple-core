package me.lede.stat.api.event;

import me.lede.stat.objects.PlayerStat;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class StatResetEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final PlayerStat stat;

    public StatResetEvent(PlayerStat stat) {
        this.stat = stat;
    }

    public PlayerStat getStat() {
        return stat;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
