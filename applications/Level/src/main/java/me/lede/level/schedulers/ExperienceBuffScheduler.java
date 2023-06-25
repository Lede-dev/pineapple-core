package me.lede.level.schedulers;

import me.lede.level.Level;
import me.lede.level.objects.ExperienceBuff;
import me.lede.level.objects.LevelManager;
import me.lede.level.objects.PlayerLevel;
import me.lede.utils.color.Color;
import me.lede.utils.format.Formatter;
import me.lede.utils.message.Prefix;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class ExperienceBuffScheduler {

    private static int task = -1;

    public static void start() {
        if (task != -1) {
            Bukkit.getLogger().warning("이미 실행중인 경험치 버프 스케쥴러가 실행중 입니다.");
            return;
        }

        task = Bukkit.getScheduler().scheduleSyncRepeatingTask(Level.getInstance(), () -> {
            for (Map.Entry<String, PlayerLevel> entry : LevelManager.getPlayerLevelMap().entrySet()) {
                PlayerLevel playerLevel = entry.getValue();
                if (playerLevel != null) {
                    ExperienceBuff buff = playerLevel.getExperienceBuff();
                    if (buff != null && buff.isEnabled()) {
                        buff.decreaseSecond();

                        if (!buff.isEnabled()) {
                            Player player = Bukkit.getPlayer(UUID.fromString(playerLevel.getUniqueId()));
                            if (player != null) {
                                player.sendMessage(Color.colored(String.format(
                                        Prefix.EXP + "경험치 &6%s &f배 쿠폰이 끝났습니다.",
                                        Formatter.format(buff.getBuff())
                                )));
                            }
                        }
                    }
                }
            }
        }, 0, 20);
    }

    public static void stop() {
        if (task == -1) {
            Bukkit.getLogger().warning("경험치 버프 스케쥴러가 실행중이지 않습니다.");
            return;
        }

        Bukkit.getScheduler().cancelTask(task);
        task = -1;
    }

    public static boolean isStarted() {
        return task != -1;
    }

}
