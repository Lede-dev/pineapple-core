package me.lede.level.listeners;

import me.lede.level.Level;
import me.lede.level.api.LevelAPI;
import me.lede.level.config.Config;
import me.lede.level.events.PlayerLevelUpEvent;
import me.lede.level.objects.Experience;
import me.lede.level.objects.LevelManager;
import me.lede.level.objects.PlayerLevel;
import me.lede.utils.color.Color;
import me.lede.utils.format.Formatter;
import me.lede.utils.message.Prefix;
import net.minecraft.server.v1_12_R1.*;
import net.minecraft.server.v1_12_R1.SoundCategory;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.jetbrains.annotations.NotNull;

public class PlayerLevelListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        LevelManager.loadPlayerLevel(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        LevelManager.removePlayerLevel(player);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        event.setDroppedExp(0);

        Player player = event.getEntity();
        PlayerLevel level = LevelAPI.getPlayerLevel(player);

        Experience experience = level.getExperience();
        double hasExp = experience.getExp();
        double removePercentage = Config.getConfig().getRemoveExpPercentage();
        double removeExp = hasExp / 100 * removePercentage;
        experience.setExp(hasExp - removeExp);
        experience.syncExpBar(player);
        level.save();

        if (Config.getConfig().isLevelMessageEnabled()) {
            player.sendMessage(Color.colored(String.format(
                    Prefix.EXP + "사망하여 경험치 &a%s &f(을)를 잃었습니다.",
                    Formatter.format(removeExp)
            )));
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(Level.getInstance(), () -> {
            LevelAPI.syncPlayerExpToExpBar(event.getPlayer());
        }, 1);
    }

    @EventHandler
    public void onLevelUp(PlayerLevelUpEvent event) {
        OfflinePlayer player = event.getPlayer();
        Player online = player.getPlayer();
        if (online != null) {
            sendLevelUpEffect(online, event.getPlayerLevel().getExperience().getLevel());
        }
    }

    private void sendLevelUpEffect(@NotNull Player player, int level) {
        spawnFirework(player);
        player.sendTitle(
                Color.colored("&a&l&o[ Level Up ]"),
                Color.colored(String.format(
                        "&f%s레벨이 되었습니다!",
                        Formatter.format(level)
                )),
                0, 10, 20
        );
    }

    private void spawnFirework(@NotNull Player player) {
        PlayerConnection con = ((CraftPlayer) player).getHandle().playerConnection;

        ItemStack stackFirework = new ItemStack(Material.FIREWORK);
        FireworkMeta fireworkMeta = (FireworkMeta) stackFirework.getItemMeta();
        FireworkEffect effect = FireworkEffect.builder().flicker(false).withColor(org.bukkit.Color.GREEN).with(FireworkEffect.Type.BURST).build();
        fireworkMeta.addEffect(effect);
        fireworkMeta.setPower(2);
        stackFirework.setItemMeta(fireworkMeta);

        Location loc = player.getLocation();
        EntityFireworks firework = new EntityFireworks(((CraftWorld) loc.getWorld()).getHandle(), loc.getX(), loc.getY(), loc.getZ(), CraftItemStack.asNMSCopy(stackFirework));

        con.sendPacket(new PacketPlayOutSpawnEntity(firework, 76));
        con.sendPacket(new PacketPlayOutNamedSoundEffect(SoundEffects.bI, SoundCategory.AMBIENT, loc.getX(), loc.getY(), loc.getZ(), 3.0F, 1.0F));

        firework.expectedLifespan = 0;
        con.sendPacket(new PacketPlayOutEntityMetadata(firework.getId(), firework.getDataWatcher(), true));
        con.sendPacket(new PacketPlayOutEntityStatus(firework, (byte) 17));
        con.sendPacket(new PacketPlayOutEntityDestroy(firework.getId()));
    }

}
