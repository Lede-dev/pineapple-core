package me.lede.utils.log;

import org.bukkit.Bukkit;

public class SimpleLog {

    public static void sendBar() {
        Bukkit.getLogger().info("ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ");
    }

    public static void sendBlank() {
        Bukkit.getLogger().info(" ");
    }

}
