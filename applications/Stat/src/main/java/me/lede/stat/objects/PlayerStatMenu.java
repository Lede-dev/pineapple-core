package me.lede.stat.objects;

import com.google.common.collect.Lists;
import me.lede.stat.config.Config;
import me.lede.stat.config.StatIcon;
import me.lede.stat.utils.Items;
import me.lede.utils.color.Color;
import me.lede.utils.format.Formatter;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PlayerStatMenu {

    private static final String TAG_STAT_OWNER = "tag:stat_owner";

    public static final String TITLE = Color.colored("&8스텟창");

    private static final int SLOT_TAG = 0;
    public static final int SLOT_NETHER_STAR = 4;
    public static final int SLOT_STRENGTH = 10;
    public static final int SLOT_MAGIC = 12;
    public static final int SLOT_AGILITY = 14;
    public static final int SLOT_DEFTNESS = 16;
    public static final int SLOT_ABILITY = 22;

    public static void openStatMenu(@NotNull Player viewer, @NotNull OfflinePlayer statOwner) {
        Inventory inv = createStatMenu(statOwner);
        viewer.openInventory(inv);
    }

    private static Inventory createStatMenu(@NotNull OfflinePlayer statOwner) {
        Inventory inv = Bukkit.createInventory(null, 27, TITLE);
        PlayerStat stat = StatManager.getPlayerStat(statOwner);
        setupBackground(inv);
        updateStatMenu(inv, stat);
        return inv;
    }

    public static void setupBackground(@NotNull Inventory inv) {
        ItemStack icon = Items.getBackgroundIcon();
        for (int i = 1; i < 27; i++) {
            inv.setItem(i, icon);
        }
    }

    public static void updateStatMenu(@NotNull Inventory inv, @NotNull PlayerStat stat) {
        setTagIcon(inv, stat);
        inv.setItem(SLOT_STRENGTH, getStrengthIcon(stat));
        inv.setItem(SLOT_MAGIC, getMagicIcon(stat));
        inv.setItem(SLOT_AGILITY, getAgilityIcon(stat));
        inv.setItem(SLOT_DEFTNESS, getDeftnessIcon(stat));
        inv.setItem(SLOT_ABILITY, getAbilityIcon(stat));

        if (Config.getConfig().isNetherStarStatEnabled()) {
            inv.setItem(SLOT_NETHER_STAR, getNetherStarIcon(stat));
        }
    }

    @NotNull
    public static OfflinePlayer getTargetOfflinePlayer(@NotNull Inventory inv) {
        return Bukkit.getOfflinePlayer(UUID.fromString(getTagFromIcon(inv)));
    }

    private static void setTagIcon(@NotNull Inventory inv, @NotNull PlayerStat stat) {
        ItemStack icon = Items.getBackgroundIcon();
        net.minecraft.server.v1_12_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(icon);
        NBTTagCompound tag = nmsItem.getTag() == null ? new NBTTagCompound() : nmsItem.getTag();
        tag.setString(TAG_STAT_OWNER, stat.getUniqueId());
        nmsItem.setTag(tag);
        ItemStack newIcon = CraftItemStack.asBukkitCopy(nmsItem);
        inv.setItem(SLOT_TAG, newIcon);
    }

    @NotNull
    private static String getTagFromIcon(@NotNull Inventory inv) {
        ItemStack icon = inv.getItem(SLOT_TAG);
        if (icon == null) {
            return "";
        }

        net.minecraft.server.v1_12_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(icon);
        NBTTagCompound tag = nmsItem.getTag() == null ? new NBTTagCompound() : nmsItem.getTag();
        return tag.getString(TAG_STAT_OWNER);
    }

    public static ItemStack getStrengthIcon(@NotNull PlayerStat stat) {
        Config config = Config.getConfig();
        StatIcon icon = config.getStrengthIcon();
        ItemStack item = icon.toItemStack();
        ItemMeta meta = item.getItemMeta();

        StatType type = StatType.STRENGTH;
        meta.setDisplayName(Color.colored("&c&l근력"));
        meta.setLore(Lists.newArrayList(
                Color.colored(String.format(
                        "&7 %s / %s ",
                        Formatter.format(stat.getStat(type)),
                        Formatter.format(config.getMaxStat(type))
                )),
                Color.colored(String.format(
                        "&7근력 1당 공격력 : %s / 체력 : %s",
                        Formatter.format(config.getStrengthDamage()),
                        Formatter.format(config.getStrengthHealth())
                ))
        ));

        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.values());

        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack getMagicIcon(@NotNull PlayerStat stat) {
        Config config = Config.getConfig();
        StatIcon icon = config.getMagicIcon();
        ItemStack item = icon.toItemStack();
        ItemMeta meta = item.getItemMeta();

        StatType type = StatType.MAGIC;
        meta.setDisplayName(Color.colored("&b&l마법"));
        meta.setLore(Lists.newArrayList(
                Color.colored(String.format(
                        "&7 %s / %s ",
                        Formatter.format(stat.getStat(type)),
                        Formatter.format(config.getMaxStat(type))
                )),
                Color.colored(String.format(
                        "&7마법 1당 공격력 : %s / 체력 : %s",
                        Formatter.format(config.getMagicDamage()),
                        Formatter.format(config.getMagicHealth())
                )),
                Color.colored(String.format(
                        "&7마법 10당 스킬 공격력 : %s%%",
                        Formatter.format(config.getMagicSkill())
                ))
        ));

        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.values());

        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack getAgilityIcon(@NotNull PlayerStat stat) {
        Config config = Config.getConfig();
        StatIcon icon = config.getAgilityIcon();
        ItemStack item = icon.toItemStack();
        ItemMeta meta = item.getItemMeta();

        StatType type = StatType.AGILITY;
        meta.setDisplayName(Color.colored("&e&l민첩"));
        meta.setLore(Lists.newArrayList(
                Color.colored(String.format(
                        "&7 %s / %s ",
                        Formatter.format(stat.getStat(type)),
                        Formatter.format(config.getMaxStat(type))
                )),
                Color.colored(String.format(
                        "&7민첩 1당 공격력 : %s / 체력 : %s / 이속 : %s",
                        Formatter.format(config.getAgilityDamage()),
                        Formatter.format(config.getAgilityHealth()),
                        Formatter.format(config.getAgilitySpeed())
                ))
        ));

        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.values());

        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack getDeftnessIcon(@NotNull PlayerStat stat) {
        Config config = Config.getConfig();
        StatIcon icon = config.getDeftnessIcon();
        ItemStack item = icon.toItemStack();
        ItemMeta meta = item.getItemMeta();

        StatType type = StatType.DEFTNESS;
        meta.setDisplayName(Color.colored("&2&l손재주"));
        meta.setLore(Lists.newArrayList(
                Color.colored(String.format(
                        "&7 %s / %s ",
                        Formatter.format(stat.getStat(type)),
                        Formatter.format(config.getMaxStat(type))
                )),
                Color.colored(String.format(
                        "&7손재주 1당 공격력 : %s / 체력 : %s ",
                        Formatter.format(config.getDeftnessDamage()),
                        Formatter.format(config.getDeftnessHealth())
                ))
        ));

        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.values());

        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack getAbilityIcon(@NotNull PlayerStat stat) {
        Config config = Config.getConfig();
        StatIcon icon = config.getAbilityIcon();
        ItemStack item = icon.toItemStack();
        ItemMeta meta = item.getItemMeta();

        StatType type = StatType.ABILITY;
        meta.setDisplayName(Color.colored("&6&l어빌리티"));
        meta.setLore(Lists.newArrayList(
                Color.colored(String.format(
                        "&7 %s ",
                        Formatter.format(stat.getStat(type))
                ))
        ));

        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.values());

        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack getNetherStarIcon(@NotNull PlayerStat stat) {
        Config config = Config.getConfig();
        StatIcon icon = config.getNetherStarIcon();
        ItemStack item = icon.toItemStack();
        ItemMeta meta = item.getItemMeta();

        StatType type = StatType.NETHER_STAR;
        meta.setDisplayName(Color.colored("&f&l네더의 별 스텟"));
        meta.setLore(Lists.newArrayList(
                Color.colored(String.format(
                        "&7 %s / %s ",
                        Formatter.format(stat.getStat(type)),
                        Formatter.format(config.getMaxStat(type))
                )),
                Color.colored(String.format(
                        "&71당 전체스텟 %s%% 증가 / 현재 증가 퍼센트 %s%%",
                        Formatter.format(config.getNetherStarPercentage()),
                        Formatter.format(stat.getStat(StatType.NETHER_STAR) * config.getNetherStarPercentage())
                ))
        ));

        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.values());

        item.setItemMeta(meta);
        return item;
    }



}
