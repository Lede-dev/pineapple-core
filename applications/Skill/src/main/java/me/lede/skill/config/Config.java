package me.lede.skill.config;

import me.lede.skill.Skill;
import me.lede.utils.mson.Mson;

import java.io.File;

public class Config {

    private static Config config;

    public static void reloadConfig() {
        String filepath = getConfigPath();
        File file = new File(filepath);
        if (!file.exists()) {
            Skill.getInstance().saveResource("config.json", false);
        }

        config = new Mson<>(filepath, Config.class).read().getData();
    }

    public static Config getConfig() {
        if (config == null) {
            reloadConfig();
        }
        return config;
    }

    private boolean resetSkill;
    private boolean enableSkillBook;
    private boolean forceLearn;
    private boolean learnQuestSkill;

    public static SkillConfig getSkillConfig() {
        return SkillConfig.getConfig();
    }

    public static String getConfigPath() {
        return getDataPath() + "/config.json";
    }

    public static String getDataPath() {
        return Skill.getInstance().getDataFolder().getAbsolutePath();
    }

    public boolean isResetSkill() {
        return resetSkill;
    }

    public boolean isEnableSkillBook() {
        return enableSkillBook;
    }

    public boolean isForceLearn() {
        return forceLearn;
    }

    public boolean isLearnQuestSkill() {
        return learnQuestSkill;
    }

    public String getPrefix() {
        return "&b&l[ 스킬 ]&f ";
    }



}
