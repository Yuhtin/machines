package com.yuhtin.quotes.machines.model;

import com.yuhtin.quotes.machines.cache.MachineCache;
import com.yuhtin.quotes.machines.cache.MachineDataCache;
import com.yuhtin.quotes.machines.util.EncodedLocation;
import lombok.Builder;
import lombok.Data;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

@Data
@Builder
public class Machine {

    private final String owner, customName;
    private final int encodedLocation;
    private final double yawPlaced, maxDrops;
    private final int machineDataId, fuelConsumeInterval;

    private boolean buildingSchematic, active;

    private int cycles, fuelAmount;

    private double drops;

    public void tick() {
        if (!active) return;

        if (cycles < 0) {
            cycles = fuelConsumeInterval;
            fuelAmount--;
        } else {
            cycles--;
            drops++;

            if (drops > maxDrops) {
                desactivate();
            }
        }

        if (fuelAmount < 0) {
            desactivate();
            fuelAmount = 0;
        }
    }

    public void fuelUp(int amount) {
        fuelAmount += amount;
        activate();
    }

    public void activate() {
        active = true;
    }

    private void desactivate() {
        active = false;
        cycles = -1;
    }

    public boolean pasteSchematic(Player player, World world) {
        MachineDataCache cache = MachineDataCache.instance();
        MachineData data = cache.get(machineDataId);
        if (data == null) return false;

        buildingSchematic = true;
        return data.placeSchematic(player, EncodedLocation.of(encodedLocation).decode(world), () -> buildingSchematic = false);
    }

    public void cleanUpSchematic(World world) {
        MachineDataCache cache = MachineDataCache.instance();
        MachineData data = cache.get(machineDataId);
        if (data == null) return;

        data.cleanUpSchematic(EncodedLocation.of(encodedLocation).decode(world), yawPlaced);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Machine machine = (Machine) obj;
        return encodedLocation == machine.encodedLocation;
    }

    public boolean equals(Location location) {
        return encodedLocation == EncodedLocation.encode(location).hashCode();
    }

}
