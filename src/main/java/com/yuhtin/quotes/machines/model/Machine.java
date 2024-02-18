package com.yuhtin.quotes.machines.model;

import com.yuhtin.quotes.machines.util.EncodedLocation;
import lombok.Builder;
import lombok.Data;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Data
@Builder
public class Machine {

    private final String owner;
    private final int encodedLocation;
    private final int machineDataId, fuelConsumeInterval;

    private boolean active;

    private int cycles, fuelAmount;

    private double quantity, drops;

    public boolean tick() {
        if (!active) return false;

        if (cycles < 0) {
            cycles = fuelConsumeInterval;
            fuelAmount--;
        } else {
            cycles--;
            drops += quantity;
        }

        if (fuelAmount < 0) {
            active = false;
            cycles = -1;
            fuelAmount = 0;
        }

        return active;
    }

    public void fuelUp(int amount) {
        fuelAmount += amount;
    }

    public void activate() {
        active = true;
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
