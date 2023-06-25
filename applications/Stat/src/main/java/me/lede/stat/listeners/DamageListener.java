package me.lede.stat.listeners;

import com.nisovin.magicspells.Spell;
import com.nisovin.magicspells.events.SpellApplyDamageEvent;
import com.nisovin.magicspells.spells.targeted.DrainlifeSpell;
import com.nisovin.magicspells.spells.targeted.PainSpell;
import me.lede.stat.Stat;
import me.lede.stat.config.Config;
import me.lede.stat.config.Debug;
import me.lede.stat.objects.StatManager;
import me.lede.utils.color.Color;
import me.lede.utils.format.Formatter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.metadata.FixedMetadataValue;

import java.lang.reflect.Field;

public class DamageListener implements Listener {

    private static final String ARROW_DAMAGE_TAG = "tag:arrow-custom-damage";

    @EventHandler
    public void onShootBow(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        if (!(event.getProjectile() instanceof Arrow)) {
            return;
        }

        Config config = Config.getConfig();
        if (!config.isArrowStatDamageEnabled()) {
            return;
        }

        Player player = (Player) event.getEntity();
        me.lede.stat.objects.PlayerStat stat = StatManager.getPlayerStat(player);
        Arrow arrow = (Arrow) event.getProjectile();
        float force = event.getForce();
        double damage = stat.getArrowDamage(force);
        arrow.setMetadata(ARROW_DAMAGE_TAG, new FixedMetadataValue(Stat.getInstance(), damage));
    }

    @EventHandler
    public void onSpellApplyDamage(SpellApplyDamageEvent event) {
        Player player = event.getCaster();
        me.lede.stat.objects.PlayerStat stat = StatManager.getPlayerStat(player);
        double damage = event.getDamage();

        Spell spell = event.getSpell();
        if (spell instanceof DrainlifeSpell) {
            sendMagicDamageLog(damage);
            return;
        }

        if (spell instanceof PainSpell) {
            PainSpell pain = (PainSpell) spell;
            try {
                Field field = PainSpell.class.getDeclaredField("ignoreArmor");
                field.setAccessible(true);
                boolean ignoreArmor = field.getBoolean(pain);
                if (ignoreArmor) {
                    sendFixedDamageLog(damage);
                    return;
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        if (!stat.isEnabled()) {
            sendMagicDamageLog(damage);
            return;
        }

        // apply magic modifier
        float modifier = stat.getMagicModifier(damage);
        event.applyDamageModifier(modifier);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onProjectileHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Arrow)) {
            return;
        }

        Arrow arrow = (Arrow) event.getDamager();
        if (!arrow.hasMetadata(ARROW_DAMAGE_TAG)) {
            return;
        }

        double origin = event.getDamage();
        double addition = arrow.getMetadata(ARROW_DAMAGE_TAG).get(0).asDouble();
        double finalDamage = origin + addition;
        event.setDamage(finalDamage);

        if (StatManager.isDebug()) {
            Debug.sendArrowHitDebugDamageMessage(origin, addition);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player)) {
            return;
        }

        Player player = (Player) entity;
        me.lede.stat.objects.PlayerStat stat = StatManager.getPlayerStat(player);
        if (!stat.isEnabled()) {
            return;
        }

        double damage = event.getDamage();
        double decrease = stat.getPassiveDamageDecreasePercentage();
        double finalDamage = damage / 100 * (100 - decrease);
        event.setDamage(finalDamage);

        if (!StatManager.isDebug()) {
            return;
        }

        String prefix = Config.getConfig().getPrefix();
        if (event instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent damageByEvent = (EntityDamageByEntityEvent) event;
            Entity atkEntity = damageByEvent.getDamager();
            if (atkEntity instanceof Player) {
                Player attacker = (Player) atkEntity;
                for (Player online : Bukkit.getOnlinePlayers()) {
                    if (online.isOp()) {
                        online.sendMessage(Color.colored(String.format(
                                "%s&6%s&f 플레이어가 &6%s &f 플레이어에게 대미지를 &e%s&f 입었습니다.",
                                prefix, attacker.getName(), player.getName(), Formatter.format(finalDamage)
                        )));
                    }
                }
                return;
            }
        }

        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.isOp()) {
                online.sendMessage(Color.colored(String.format(
                        "%s&6%s&f 플레이어가 &e%s &f대미지를 &e%s&f 입었습니다.",
                        prefix, player.getName(), event.getCause(), Formatter.format(finalDamage)
                )));
            }
        }
    }

    private void sendFixedDamageLog(double damage) {
        if (StatManager.isDebug()) {
            for (Player online : Bukkit.getOnlinePlayers()) {
                if (online.isOp()) {
                    online.sendMessage("");
                    online.sendMessage(
                            Color.colored(Config.getConfig().getPrefix())
                                    + "고정 대미지: " + Formatter.format(damage)
                    );
                    online.sendMessage("");
                }
            }
        }
    }
    
    private void sendMagicDamageLog(double damage) {
        if (StatManager.isDebug()) {
            for (Player online : Bukkit.getOnlinePlayers()) {
                if (online.isOp()) {
                    online.sendMessage("");
                    online.sendMessage(
                            Color.colored(Config.getConfig().getPrefix())
                                    + "마법 대미지: " + Formatter.format(damage)
                    );
                    online.sendMessage("");
                }
            }
        }
    }

}
