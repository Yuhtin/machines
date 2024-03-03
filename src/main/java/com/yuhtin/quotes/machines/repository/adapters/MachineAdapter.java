package com.yuhtin.quotes.machines.repository.adapters;

import com.henryfabio.sqlprovider.executor.adapter.SQLResultAdapter;
import com.henryfabio.sqlprovider.executor.result.SimpleResultSet;
import com.yuhtin.quotes.machines.cache.MachineDataCache;
import com.yuhtin.quotes.machines.model.Machine;
import com.yuhtin.quotes.machines.model.MachineData;
import com.yuhtin.quotes.machines.util.CardinalDirection;
import com.yuhtin.quotes.machines.util.SimpleLocation;

public class MachineAdapter implements SQLResultAdapter<Machine> {

    @Override
    public Machine adaptResult(SimpleResultSet resultSet) {
        boolean active;
        try {
            active = resultSet.get("active");
        } catch (Exception ignored) {
            active = (int) resultSet.get("active") == 1;
        }

        int machineDataId = resultSet.get("machine_data_id");

        MachineDataCache cache = MachineDataCache.instance();
        MachineData data = cache.get(machineDataId);

        SimpleLocation encodedLocation = SimpleLocation.of(resultSet.get("location"));

        double yawPlaced = resultSet.get("yawPlaced");

        CardinalDirection direction = CardinalDirection.valueOf(resultSet.get("cardinalDirection"));

        return Machine.builder()
                .owner(resultSet.get("username"))
                .customName(data.getCustomName())
                .simpleLocation(encodedLocation)
                .direction(direction)
                .machineDataId(machineDataId)
                .yawPlaced(yawPlaced)
                .fuelConsumeInterval(data.getSpendFuelInterval())
                .maxDrops(data.getMaxDrops())
                .active(active)
                .cycles(resultSet.get("cycles"))
                .fuelAmount(resultSet.get("fuel_amount"))
                .drops(resultSet.get("drops"))
                .build();
    }
}
