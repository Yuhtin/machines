package com.yuhtin.quotes.machines.model;

import com.yuhtin.quotes.machines.schematic.Schematic;
import com.yuhtin.quotes.machines.util.CardinalDirection;
import com.yuhtin.quotes.machines.util.ItemBuilder;
import de.tr7zw.nbtapi.NBTItem;
import lombok.Builder;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.List;

@Getter
@Builder
public class MachineData {

    private final int id;
    private final ItemStack item;
    private final Schematic schematic;
    private final String customName;
    private final List<String> lore;
    private final int requiredFuelId, spendFuelInterval;
    private final double price, sellDropPrice, maxDrops;

    public void cleanUpSchematic(Location location, double yawPlaced) {
        schematic.cleanUpSchematic(location, yawPlaced);
    }

    public boolean placeSchematic(Player player, Location decode, Runnable completed) {
        Collection<Location> locations = schematic.pasteSchematic(decode, player, completed, Schematic.Options.REALISTIC);
        return locations != null && !locations.isEmpty();
    }

    public ItemStack generateItem() {
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setInteger("machine-data", id);
        nbtItem.setString("machine-unique", String.valueOf(System.currentTimeMillis()));

        return nbtItem.getItem();
    }
}
