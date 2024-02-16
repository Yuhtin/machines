package com.yuhtin.quotes.machines.model;

import lombok.Builder;

@Builder
public class Machine {

    private final String owner;
    private final int x, y, z;
    private final int machineDataId;

    private int cycleInSeconds;
    private int fuelAmount;

    private double drops;

}
