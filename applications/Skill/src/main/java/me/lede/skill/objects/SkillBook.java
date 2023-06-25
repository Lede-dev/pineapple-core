package me.lede.skill.objects;

import com.google.common.collect.Lists;
import me.lede.skill.config.Config;
import me.lede.skill.config.SkillConfig;
import me.lede.utils.color.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

public class SkillBook {

    private final String skill;

    public SkillBook(String skill) {
        if (Config.getConfig().isEnableSkillBook()) {
            String check = SkillConfig.getConfig().isSkillName(skill) ? skill : null;
            if (check != null) {
                this.skill = check;
                return;
            }
        }

        this.skill = SkillConfig.getConfig().isQuestSkillName(skill) ? skill : null;
    }

    public SkillBook(@Nullable ItemStack item) {
        skill = getSkillFromItem(item);
    }

    @Nullable
    public ItemStack toItemStack() {
        if (skill == null) {
            return null;
        }

        ItemStack item = new ItemStack(Material.BOOK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Color.colored(String.format("&6[ 스킬북 ] &f%s", skill)));
        meta.setLore(Color.colored(Lists.newArrayList(String.format("&f우클릭 시 %s 스킬을 배웁니다.", skill))));
        item.setItemMeta(meta);
        return item;
    }

    @Nullable
    public String getSkill() {
        return skill;
    }

    public static boolean isSkillBook(@Nullable ItemStack item) {
        return getSkillFromItem(item) != null;
    }

    @Nullable
    public static String getSkillFromItem(@Nullable ItemStack item) {
        if (item == null) {
            return null;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return null;
        }

        String name = meta.getDisplayName();
        if (name ==  null) {
            return null;
        }

        String display = Color.uncolored(name);
        String skill = display.replace("[ 스킬북 ] ", "");

        SkillConfig config = SkillConfig.getConfig();
        return config.isQuestSkillName(skill) | config.isSkillName(skill) ? skill : null;
    }
}
