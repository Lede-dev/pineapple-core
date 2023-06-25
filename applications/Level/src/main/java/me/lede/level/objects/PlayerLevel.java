package me.lede.level.objects;

import me.lede.level.config.Config;
import me.lede.utils.mson.Mson;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.UUID;

public class PlayerLevel {

    private final String uuid;
    private Experience experience;
    private ExperienceBuff experienceBuff;

    public PlayerLevel(@NotNull String uuid) {
        this.uuid = uuid;
        if (exists()) {
            load();
        } else {
            this.experience = new Experience();
            this.experienceBuff = new ExperienceBuff();

            this.experience.setPlayer(Bukkit.getOfflinePlayer(UUID.fromString(uuid)));
            this.experience.setPlayerLevel(this);
            save();
        }
    }

    public String getUniqueId() {
        return uuid;
    }

    public Experience getExperience() {
        return experience;
    }

    public ExperienceBuff getExperienceBuff() {
        return experienceBuff;
    }

    public void save() {
        new Mson<>(getSavePath(), this).write();
    }

    public void load() {
        PlayerLevel loadLevel = new Mson<>(getSavePath(), PlayerLevel.class).read().getData();
        this.experience = loadLevel.getExperience();
        this.experienceBuff = loadLevel.getExperienceBuff();

        this.experience.setPlayer(Bukkit.getOfflinePlayer(UUID.fromString(uuid)));
        this.experience.setPlayerLevel(this);
    }

    public boolean exists() {
        return new File(getSavePath()).exists();
    }

    public String getSavePath() {
        return Config.getDataPath() + "/saves/" + uuid + ".json";
    }

}
