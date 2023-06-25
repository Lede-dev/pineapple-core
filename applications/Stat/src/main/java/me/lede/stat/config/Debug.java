package me.lede.stat.config;

import me.lede.utils.color.Color;
import me.lede.utils.format.Formatter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Debug {

    public static void sendMagicDamageDebugMessage(
            double origin, double damage, double damagePercentage,
            double finalDamage, double additionDamage, double additionDamagePercentage,
            double finalPercentage, boolean enableStar, double starPercentage
    ) {
        Config config = Config.getConfig();
        String prefix = config.getPrefix();
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.isOp()) {
                online.sendMessage(" ");
                online.sendMessage(Color.colored(String.format(
                        prefix + "마법 대미지: %s",
                        Formatter.format(origin)
                )));
                online.sendMessage(Color.colored(String.format(
                        prefix + "스텟 대미지: %s",
                        Formatter.format(damage)
                )));
                online.sendMessage(Color.colored(String.format(
                        prefix + "스텟 대미지 증가 비율: %s%%",
                        Formatter.format(damagePercentage)
                )));
                online.sendMessage(Color.colored(String.format(
                        prefix + "네더의별 스탯 증가 비율: %s%% [ %s ]",
                        Formatter.format(starPercentage), enableStar
                )));
                online.sendMessage(Color.colored(String.format(
                        prefix + "최종 대미지 증가 비율: %s",
                        Formatter.format(finalPercentage)
                )));
                online.sendMessage(Color.colored(String.format(
                        prefix + "최종 마법 대미지 - 계산된 값: %s (( %s + %s ) * %s )",
                        Formatter.format(finalDamage),
                        Formatter.format(origin), Formatter.format(additionDamage),
                        Formatter.format(additionDamagePercentage)
                )));
                online.sendMessage(Color.colored(String.format(
                        prefix + "최종 마법 대미지 - 실적용: %s ( %s * %s )",
                        Formatter.format(origin * finalPercentage),
                        Formatter.format(origin),
                        Formatter.format(finalPercentage)
                )));
                online.sendMessage(" ");
            }
        }
    }

    public static void sendAttackDamageDebugMessage(
            double origin, double damage, double finalDamage,
            boolean enableStar, double starPercentage
    ) {
        Config config = Config.getConfig();
        String prefix = config.getPrefix();
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.isOp()) {
                online.sendMessage(" ");
                online.sendMessage(Color.colored(String.format(
                        prefix + "기본 대미지: %s",
                        Formatter.format(origin)
                )));
                online.sendMessage(Color.colored(String.format(
                        prefix + "스탯 대미지: %s",
                        Formatter.format(damage)
                )));
                online.sendMessage(Color.colored(String.format(
                        prefix + "네더의별 스탯 증가 비율: %s%% [ %s ]",
                        Formatter.format(starPercentage), enableStar
                )));
                online.sendMessage(Color.colored(String.format(
                        prefix + "최종 대미지: %s ( %s + %s )",
                        Formatter.format(origin + finalDamage), Formatter.format(origin),
                        Formatter.format(finalDamage)
                )));
                online.sendMessage(" ");
            }
        }
    }

    public static void sendArrowDamageDebugMessage(
            double damage, float power, double starDamage,
            double finalDamage, boolean enableStar, double starPercentage
    ) {
        Config config = Config.getConfig();
        String prefix = config.getPrefix();
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.isOp()) {
                online.sendMessage(" ");
                online.sendMessage(Color.colored(String.format(
                        prefix + "스탯 대미지: %s",
                        Formatter.format(damage)
                )));
                online.sendMessage(Color.colored(String.format(
                        prefix + "네더의별 스탯 증가 비율: %s%% [ %s ]",
                        Formatter.format(starPercentage), enableStar
                )));
                online.sendMessage(Color.colored(String.format(
                        prefix + "활 차지: %s%%",
                        Formatter.format(power * 100)
                )));
                online.sendMessage(Color.colored(String.format(
                        prefix + "화살 추가 대미지: %s ( %s * %s )",
                        Formatter.format(finalDamage),
                        Formatter.format(starDamage), Formatter.format(power)
                )));
                online.sendMessage(" ");
            }
        }
    }
    
    public static void sendArrowHitDebugDamageMessage(double origin, double addition) {
        Config config = Config.getConfig();
        String prefix = config.getPrefix();
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.isOp()) {
                online.sendMessage(Color.colored(String.format(
                        prefix + "활 기본 대미지: %s",
                        Formatter.format(origin)
                )));
                online.sendMessage(Color.colored(String.format(
                        prefix + "활 최종 대미지: %s ( %s + %s )",
                        Formatter.format(origin + addition),
                        Formatter.format(origin), Formatter.format(addition)
                )));
            }
        }
    }

}
