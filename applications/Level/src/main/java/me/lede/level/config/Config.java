package me.lede.level.config;

import com.google.common.collect.Maps;
import me.lede.level.Level;
import me.lede.utils.format.Parser;
import me.lede.utils.mson.Mson;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class Config {

    private static Config config;

    public static void reloadConfig() {
        String filepath = getConfigPath();
        File file = new File(filepath);
        if (!file.exists()) {
            Level.getInstance().saveResource("config.json", false);
        }

        config = new Mson<>(filepath, Config.class).read().getData();
    }

    public static void saveConfig() {
        new Mson<>(getConfigPath(), config).write();
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
        return Level.getInstance().getDataFolder().getAbsolutePath();
    }

    private int statByLevel;
    private final Map<Integer, Integer> bonusStatLevel;
    private boolean applyBuffToExpBook;
    private double expRemovePercentage;
    private int maxLevel;
    private boolean levelMessage;
    private final Map<Integer, String> requiredExp;

    public Config(int statByLevel, double expRemovePercentage, int maxLevel, Map<Integer, String> requiredExp) {
        this.statByLevel = statByLevel;
        this.bonusStatLevel = Maps.newHashMap();
        this.applyBuffToExpBook = false;
        this.expRemovePercentage = expRemovePercentage;
        this.maxLevel = maxLevel;
        this.levelMessage = true;
        this.requiredExp = requiredExp;
    }

    public int getStatPoint(int level) {
        return bonusStatLevel.getOrDefault(level, statByLevel);
    }

    public int getStatByLevel() {
        return statByLevel;
    }

    public int getBonusStatLevel(int level) {
        return bonusStatLevel.getOrDefault(level, statByLevel);
    }

    public boolean isApplyBuffToExpBook() {
        return applyBuffToExpBook;
    }

    public double getRemoveExpPercentage() {
        return expRemovePercentage;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public double getRequiredExp(int level) {
        String value = requiredExp.getOrDefault(level, "1000");
        return Parser.parseDouble(value);
    }

    public boolean isLevelMessageEnabled() {
        return levelMessage;
    }

    public Map<Integer, String> getRequiredExpMap() {
        return requiredExp;
    }

    public void setStatByLevel(int value) {
        this.statByLevel = value;
        saveConfig();
    }

    public void setApplyBuffToExpBook(boolean applyBuffToExpBook) {
        this.applyBuffToExpBook = applyBuffToExpBook;
    }

    public void setRemoveExpPercentage(double value) {
        this.expRemovePercentage = value;
        saveConfig();
    }

    public void setMaxLevel(int value) {
        this.maxLevel = value;
        saveConfig();
    }

    public void setRequiredExp(int level, double value) {
        this.requiredExp.put(level, new BigDecimal(value).toString());
        saveConfig();
    }

    public void setLevelMessage(boolean state) {
        this.levelMessage = state;
        saveConfig();
    }

}
