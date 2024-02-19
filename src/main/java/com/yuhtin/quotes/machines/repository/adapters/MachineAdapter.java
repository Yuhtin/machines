package com.yuhtin.quotes.machines.repository.adapters;

import com.henryfabio.sqlprovider.executor.adapter.SQLResultAdapter;
import com.henryfabio.sqlprovider.executor.result.SimpleResultSet;
import com.yuhtin.quotes.machines.cache.MachineDataCache;
import com.yuhtin.quotes.machines.model.Machine;
import com.yuhtin.quotes.machines.model.MachineData;

public class MachineAdapter implements SQLResultAdapter<Machine> {

    @Override
    public Machine adaptResult(SimpleResultSet resultSet) {
        boolean active = resultSet.get("active");
        int machineDataId = resultSet.get("machine_data_id");

        MachineDataCache cache = MachineDataCache.instance();
        MachineData data = cache.get(machineDataId);

        return Machine.builder()
                .owner(resultSet.get("username"))
                .customName(data.getCustomName())
                .encodedLocation(resultSet.get("encodedLocation"))
                .machineDataId(machineDataId)
                .fuelConsumeInterval(data.getSpendFuelInterval())
                .maxDrops(data.getMaxDrops())
                .active(active)
                .cycles(resultSet.get("cycles"))
                .fuelAmount(resultSet.get("fuel_amount"))
                .drops(resultSet.get("drops"))
                .build();
    }
}
