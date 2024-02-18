package com.yuhtin.quotes.machines.cache;

import com.yuhtin.quotes.machines.model.MachineData;
import com.yuhtin.quotes.machines.util.ItemBuilder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class MachineDataCache {

    private static MachineDataCache instance;

    private final HashMap<Integer, MachineData> machines = new HashMap<>();

    public void register(MachineData machineData) {
        machines.put(machineData.getId(), machineData);
    }

    public MachineData get(int id) {
        return machines.get(id);
    }

    @Nullable
    public MachineData getByItem(ItemStack machine) {
        ItemBuilder builder = new ItemBuilder(machine);
        if (builder.hasNBTKey("machine-data")) {
            return get(builder.getNBTInt("machine-data"));
        } else {
            return null;
        }
    }

    public static MachineDataCache instance() {
        if (instance == null) instance = new MachineDataCache();
        return instance;
    }

}
