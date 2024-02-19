package com.yuhtin.quotes.machines.util;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static me.lucko.helper.text3.Text.colorize;

/**
 * @author Yuhtin
 * Github: <a href="https://github.com/Yuhtin">https://github.com/Yuhtin</a>
 */
@RequiredArgsConstructor(staticName = "of")
public class ItemParser {

    private final ConfigurationSection configurationSection;

    public List<ItemStack> parseList() {
        List<ItemStack> items = new ArrayList<>();
        for (String key : this.configurationSection.getKeys(false)) {
            ConfigurationSection section = this.configurationSection.getConfigurationSection(key);
            if (section == null) continue;

            items.add(parseItem(section));
        }

        return items;
    }

    @Nullable
    public ItemStack parseItem() {
        return parseItem(this.configurationSection);
    }

    @Nullable
    public ItemStack parseItem(ConfigurationSection section) {
        if (section == null) return null;

        ItemBuilder itemBuilder = null;
        try {
            if (section.contains("head")) itemBuilder = new ItemBuilder(section.getString("head"));
            else if (section.contains("material")) {
                String material = section.getString("material");
                itemBuilder = new ItemBuilder(
                        Material.valueOf(material),
                        section.contains("data") ? (short) section.getInt("data") : 0
                );
            }

            if (itemBuilder == null) return null;

            if (section.contains("amount")) itemBuilder.amount(section.getInt("amount"));
            if (section.getBoolean("glow")) itemBuilder.glowing();
            if (section.getBoolean("hideFlags")) itemBuilder.hideFlags();
            if (section.contains("customName")) itemBuilder.name(section.getString("customName"));
            if (section.contains("lore")) {
                final List<String> lore = new ArrayList<>();
                for (String description : section.getStringList("lore")) {
                    lore.add(colorize(description));
                }

                itemBuilder.setLore(lore);
            }

            return itemBuilder.wrap();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        }
    }

    private java.awt.Color getColorByHex(String hex) {
        return java.awt.Color.decode(hex);
    }

    private Color getBukkitColorByHex(String hex) {
        val decode = getColorByHex(hex);
        return Color.fromRGB(decode.getRed(), decode.getGreen(), decode.getBlue());
    }
}