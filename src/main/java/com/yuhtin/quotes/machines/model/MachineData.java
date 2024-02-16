package com.yuhtin.quotes.machines.model;

import lombok.Builder;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

@Getter
@Builder
public class MachineData {

    private final int id;
    private final String customName, schematicFileName;
    private final int requiredFuelId, spendFuelInterval;
    private final double price, sellDropPrice;

    private final ItemStack drop;

}
