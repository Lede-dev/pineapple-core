package me.lede.skill.config;

import com.google.common.collect.Lists;
import com.nisovin.magicspells.spells.targeted.PainSpell;
import me.lede.skill.Skill;
import me.lede.utils.color.Color;
import me.lede.utils.mson.Mson;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MenuConfig {

    private static MenuIconGroup strength;
    private static MenuIconGroup magic;
    private static MenuIconGroup agility;
    private static MenuIconGroup deftness;

    private static MainMenuIconGroup main;

    public static void reloadConfig() {
        reloadStrengthConfig();
        reloadMagicConfig();
        reloadAgilityConfig();
        reloadDeftnessConfig();
        reloadMainMenuConfig();
    }

    private static void reloadStrengthConfig() {
        String filepath = getConfigPath("strength");
        File file = new File(filepath);
        if (!file.exists()) {
            Skill.getInstance().saveResource("menus/strength.json", false);
        }

        strength = new Mson<>(filepath, MenuIconGroup.class).read().getData();
    }

    private static void reloadMagicConfig() {
        String filepath = getConfigPath("magic");
        File file = new File(filepath);
        if (!file.exists()) {
            Skill.getInstance().saveResource("menus/magic.json", false);
        }

        magic = new Mson<>(filepath, MenuIconGroup.class).read().getData();
    }

    private static void reloadAgilityConfig() {
        String filepath = getConfigPath("agility");
        File file = new File(filepath);
        if (!file.exists()) {
            Skill.getInstance().saveResource("menus/agility.json", false);
        }

        agility = new Mson<>(filepath, MenuIconGroup.class).read().getData();
    }

    private static void reloadDeftnessConfig() {
        String filepath = getConfigPath("deftness");
        File file = new File(filepath);
        if (!file.exists()) {
            Skill.getInstance().saveResource("menus/deftness.json", false);
        }

        deftness = new Mson<>(filepath, MenuIconGroup.class).read().getData();
    }

    private static void reloadMainMenuConfig() {
        String filepath = getConfigPath("main");
        File file = new File(filepath);
        if (!file.exists()) {
            Skill.getInstance().saveResource("menus/main.json", false);
        }

        main = new Mson<>(filepath, MainMenuIconGroup.class).read().getData();
    }

    public static MenuIconGroup getStrengthConfig() {
        if (strength == null) {
            reloadStrengthConfig();
        }
        return strength;
    }

    public static MenuIconGroup getMagicConfig() {
        if (magic == null) {
            reloadMagicConfig();
        }
        return magic;
    }

    public static MenuIconGroup getAgilityConfig() {
        if (agility == null) {
            reloadAgilityConfig();
        }
        return agility;
    }

    public static MenuIconGroup getDeftnessConfig() {
        if (deftness == null) {
            reloadDeftnessConfig();
        }
        return deftness;
    }

    public static MainMenuIconGroup getMainConfig() {
        if (main == null) {
            reloadMainMenuConfig();
        }
        return main;
    }

    public static ItemStack getBackground() {
        ItemStack item = new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.WHITE.getWoolData());
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(" ");
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack getReturn() {
//        ItemStack item = new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.LIME.getWoolData());
//        ItemMeta meta = item.getItemMeta();
//        meta.setDisplayName(Color.colored("&f돌아가기"));
//        item.setItemMeta(meta);
//        return item;

        return main.getPrev().toItemStack();
    }

    public static String getConfigPath(@NotNull String filename) {
        return getDataPath() + "/menus/" + filename + ".json";
    }

    public static String getDataPath() {
        return Skill.getInstance().getDataFolder().getAbsolutePath();
    }

    public static class MenuIconGroup {

        private TreeMap<SkillConfig.Type, MenuIcon> icons;

        public MenuIcon getMenuIcon(@NotNull SkillConfig.Type type) {
            return icons.get(type);
        }

        public Map<SkillConfig.Type, MenuIcon> getIcons() {
            return icons;
        }

    }

    public static class MenuIcon {

        private int type;
        private short model;
        private String name;
        private List<String> lore;

        private int typeEnabled;
        private short modelEnabled;
        private String nameEnabled;
        private List<String> loreEnabled;

        public String getName() {
            return name;
        }

        public List<String> getLore() {
            return lore;
        }

        public ItemStack getClosedIcon() {
            ItemStack item = new ItemStack(type, 1, model);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(Color.colored(name));
            meta.setLore(Color.colored(lore == null ? Lists.newArrayList() : lore));
            item.setItemMeta(meta);
            return item;
        }

        public ItemStack getOpenedIcon() {
            ItemStack item = new ItemStack(typeEnabled, 1, modelEnabled);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(Color.colored(nameEnabled));
            meta.setLore(Color.colored(loreEnabled == null ? Lists.newArrayList() : loreEnabled));
            item.setItemMeta(meta);
            return item;
        }

        public ItemStack getQuestClosedIcon() {
            ItemStack item = new ItemStack(Material.BARRIER);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(Color.colored("&c&lX"));
            item.setItemMeta(meta);
            return item;
        }

    }

    public static class MainMenuIconGroup {
        private MainMenuIcon strength;
        private MainMenuIcon magic;
        private MainMenuIcon agility;
        private MainMenuIcon deftness;
        private MainMenuIcon prev;

        public MainMenuIcon getStrength() {
            return strength;
        }

        public MainMenuIcon getMagic() {
            return magic;
        }

        public MainMenuIcon getAgility() {
            return agility;
        }

        public MainMenuIcon getDeftness() {
            return deftness;
        }

        public MainMenuIcon getPrev() {
            return prev;
        }
    }

    public static class MainMenuIcon {

        private int type;
        private int model;
        private String name;
        private List<String> lore;

        public int getType() {
            return type;
        }

        public int getModel() {
            return model;
        }

        @NotNull
        public ItemStack toItemStack() {
            if (model <= 0) {
                return new ItemStack(type, 1);
            }

            ItemStack item = new ItemStack(type, 1, (short) model);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(Color.colored(name));
            meta.setUnbreakable(true);
            meta.addItemFlags(ItemFlag.values());
            meta.setLore(lore == null ? Lists.newArrayList() : lore);
            item.setItemMeta(meta);
            return item;
        }

    }

}
