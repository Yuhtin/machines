package com.yuhtin.quotes.machines.cache;

import com.yuhtin.quotes.machines.MachinesPlugin;
import com.yuhtin.quotes.machines.model.Fuel;
import com.yuhtin.quotes.machines.util.ItemBuilder;
import com.yuhtin.quotes.machines.util.ItemParser;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;

public class FuelCache {

    private static FuelCache instance;

    private final HashMap<Integer, Fuel> fuels = new HashMap<>();

    public void register(Fuel data) {
        fuels.put(data.getId(), data);
    }

    public Fuel get(int id) {
        return fuels.get(id);
    }

    public Collection<Integer> getIds() {
        return fuels.keySet();
    }

    @Nullable
    public Fuel getByItem(ItemStack fuel) {
        NBTItem nbtItem = new NBTItem(fuel);
        if (nbtItem.hasTag("fuel-data")) {
            return get(nbtItem.getInteger("fuel-data"));
        } else {
            return null;
        }
    }

    public static FuelCache instance() {
        if (instance == null) instance = new FuelCache();
        return instance;
    }

    public void reload() {
        fuels.clear();

        MachinesPlugin plugin = MachinesPlugin.getInstance();
        ConfigurationSection config = plugin.getConfig().getConfigurationSection("fuels");
        for (String key : config.getKeys(false)) {
            ConfigurationSection section = config.getConfigurationSection(key);

            ItemStack item = ItemParser.of(section).parseItem();
            if (item == null) continue;

            NBTItem nbtItem = new NBTItem(item);

            int value = Integer.parseInt(key);
            nbtItem.setInteger("fuel-data", value);

            Fuel fuel = new Fuel(
                    value,
                    nbtItem.getItem()
            );

            register(fuel);
        }
    }
}
