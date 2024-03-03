package com.yuhtin.quotes.machines.model;

import com.yuhtin.quotes.machines.cache.MachineDataCache;
import com.yuhtin.quotes.machines.util.CardinalDirection;
import com.yuhtin.quotes.machines.util.SimpleLocation;
import lombok.Builder;
import lombok.Data;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@Data
@Builder
public class Machine {

    private final String owner, customName;
    private final SimpleLocation simpleLocation;
    private final CardinalDirection direction;
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

    public boolean pasteSchematic(Player player) {
        MachineDataCache cache = MachineDataCache.instance();
        MachineData data = cache.get(machineDataId);
        if (data == null) return false;

        buildingSchematic = true;

        return data.placeSchematic(player, schematicLocation(), () -> buildingSchematic = false);
    }

    private Location schematicLocation() {
        Location location = simpleLocation.decode();

        if (direction == CardinalDirection.NORTH) {
            location = location.add(0, 0, -1);
        } else if (direction == CardinalDirection.SOUTH) {
            location = location.add(0, 0, 1);
        } else if (direction == CardinalDirection.EAST) {
            location = location.add(1, 0, 0);
        } else if (direction == CardinalDirection.WEST) {
            location = location.add(-1, 0, 0);
        }

        return location;
    }

    public void cleanUpSchematic() {
        MachineDataCache cache = MachineDataCache.instance();
        MachineData data = cache.get(machineDataId);
        if (data == null) return;

        data.cleanUpSchematic(schematicLocation(), yawPlaced);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Machine machine = (Machine) obj;
        return simpleLocation == machine.simpleLocation;
    }

    public boolean equals(Location location) {
        return simpleLocation.equals(SimpleLocation.encode(location));
    }

}
