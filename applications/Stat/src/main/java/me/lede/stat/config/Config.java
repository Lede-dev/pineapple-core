package me.lede.stat.config;

import me.lede.stat.Stat;
import me.lede.stat.objects.StatType;
import me.lede.utils.mson.Mson;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Map;

public class Config {

    private static Config config;

    public static void reloadConfig() {
        String filepath = getConfigPath();
        File file = new File(filepath);
        if (!file.exists()) {
            Stat.getInstance().saveResource("config.json", false);
        }

        config = new Mson<>(filepath, Config.class).read().getData();
    }

    public static Config getConfig() {
        if (config == null) {
            reloadConfig();
        }
        return config;
    }

    public static String getConfigPath() {
        return getDataPath() + "/config.json";
    }

    public static String getDataPath() {
        return Stat.getInstance().getDataFolder().getAbsolutePath();
    }

    private StatIcon strengthIcon;
    private StatIcon magicIcon;
    private StatIcon agilityIcon;
    private StatIcon deftnessIcon;
    private StatIcon abilityIcon;
    private StatIcon netherStarIcon;

    private String prefix;

    private int statPoint;
    private Map<Integer, Integer> specialStatPoint;
    private int additionalStatPoint;

    private Map<StatType, Integer> maxStat;

    private double strengthDamage;
    private double strengthHealth;

    private double magicHealth;
    private double magicDamage;
    private double magicSkill;

    private double agilityDamage;
    private double agilityHealth;
    private double agilitySpeed;

    private double deftnessDamage;
    private double deftnessHealth;

    private double netherStarPercentage;

    private double passivePercentage;

    private boolean enableNetherStarStat;
    private boolean enableNetherStarSpeed;

    private double speedIncreasePercentage;

    private int statRandomTicketMaxValue;

    private boolean enableArrowStatDamage;
    private boolean enableArrowNetherStatDamage;

    public void save() {
        new Mson<>(getConfigPath(), this).write();
    }

    public StatIcon getStrengthIcon() {
        return strengthIcon;
    }

    public StatIcon getMagicIcon() {
        return magicIcon;
    }

    public StatIcon getAgilityIcon() {
        return agilityIcon;
    }

    public StatIcon getDeftnessIcon() {
        return deftnessIcon;
    }

    public StatIcon getAbilityIcon() {
        return abilityIcon;
    }

    public StatIcon getNetherStarIcon() {
        return netherStarIcon;
    }

    public String getPrefix() {
        return "&b[ 스텟 ]&f ";
    }

    public int getStatPoint(int level) {
        return specialStatPoint.getOrDefault(level, statPoint);
    }

    public int getStatPoint() {
        return statPoint;
    }

    public Map<Integer, Integer> getSpecialStatPoint() {
        return specialStatPoint;
    }

    public int getAdditionalStatPoint() {
        return additionalStatPoint;
    }

    public int getMaxStat(@NotNull StatType type) {
        return maxStat.computeIfAbsent(type, k -> Integer.MAX_VALUE);
    }

    public double getStrengthDamage() {
        return strengthDamage;
    }

    public double getStrengthHealth() {
        return strengthHealth;
    }

    public double getMagicHealth() {
        return magicHealth;
    }

    public double getMagicSkill() {
        return magicSkill;
    }

    public double getMagicDamage() {
        return magicDamage;
    }

    public double getAgilityDamage() {
        return agilityDamage;
    }

    public double getAgilityHealth() {
        return agilityHealth;
    }

    public double getAgilitySpeed() {
        return agilitySpeed;
    }

    public double getDeftnessDamage() {
        return deftnessDamage;
    }

    public double getDeftnessHealth() {
        return deftnessHealth;
    }

    public double getNetherStarPercentage() {
        return netherStarPercentage;
    }

    public double getPassivePercentage() {
        return passivePercentage;
    }

    public boolean isNetherStarStatEnabled() {
        return enableNetherStarStat;
    }

    public void setEnableNetherStarStat(boolean enableNetherStarStat) {
        this.enableNetherStarStat = enableNetherStarStat;
    }

    public boolean isNetherStarSpeedEnabled() {
        return enableNetherStarSpeed;
    }

    public double getSpeedIncreasePercentage() {
        return speedIncreasePercentage;
    }

    public int getStatRandomTicketMaxValue() {
        return Math.max(1, statRandomTicketMaxValue);
    }

    public boolean isArrowStatDamageEnabled() {
        return enableArrowStatDamage;
    }

    public boolean isArrowNetherStatDamageEnabled() {
        return enableArrowNetherStatDamage;
    }

}
