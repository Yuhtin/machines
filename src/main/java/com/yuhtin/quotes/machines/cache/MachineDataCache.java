package com.yuhtin.quotes.machines.cache;

import com.yuhtin.quotes.machines.MachinesPlugin;
import com.yuhtin.quotes.machines.model.MachineData;
import com.yuhtin.quotes.machines.schematic.Schematic;
import com.yuhtin.quotes.machines.util.ItemBuilder;
import com.yuhtin.quotes.machines.util.ItemParser;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;

public class MachineDataCache {

    private static MachineDataCache instance;

    private final HashMap<Integer, MachineData> data = new HashMap<>();

    public void register(MachineData machineData) {
        data.put(machineData.getId(), machineData);
    }

    public MachineData get(int id) {
        return data.get(id);
    }

    public Collection<Integer> getIds() {
        return data.keySet();
    }

    public Collection<MachineData> values() {
        return data.values();
    }

    @Nullable
    public MachineData getByItem(ItemStack machine) {
        NBTItem nbtItem = new NBTItem(machine);
        if (nbtItem.hasTag("machine-data")) {
            return get(nbtItem.getInteger("machine-data"));
        } else {
            return null;
        }
    }

    public static MachineDataCache instance() {
        if (instance == null) instance = new MachineDataCache();
        return instance;
    }

    public void reload() {
        data.clear();

        MachinesPlugin plugin = MachinesPlugin.getInstance();
        ConfigurationSection config = plugin.getConfig().getConfigurationSection("machines");
        for (String key : config.getKeys(false)) {
            ConfigurationSection section = config.getConfigurationSection(key);

            Schematic schematic = createSchematic(section.getString("schematicFile"));
            if (schematic == null) continue;

            MachineData machineData = MachineData.builder()
                    .id(Integer.parseInt(key))
                    .item(ItemParser.of(section.getConfigurationSection("item")).parseItem())
                    .schematic(schematic)
                    .customName(section.getString("customName"))
                    .lore(section.getStringList("lore"))
                    .requiredFuelId(section.getInt("requiredFuelId"))
                    .spendFuelInterval(section.getInt("spendFuelInterval"))
                    .price(section.getDouble("price"))
                    .sellDropPrice(section.getDouble("sellDropPrice"))
                    .maxDrops(section.getDouble("maxDrops"))
                    .build();

            register(machineData);
            plugin.getLogger().info("Mineradora " + machineData.getCustomName() + " (" + key + ") carregada com sucesso!");
        }
    }

    public Schematic createSchematic(String schematicName) {
        try {
            return new Schematic(schematicName);
        } catch (Exception exception) {
            MachinesPlugin.getInstance().getLogger().warning("Erro ao carregar a schematic " + schematicName + "!");
            MachinesPlugin.getInstance().getLogger().severe(exception.getMessage());
            return null;
        }
    }
}
