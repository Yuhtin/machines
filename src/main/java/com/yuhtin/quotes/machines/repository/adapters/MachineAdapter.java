package com.yuhtin.quotes.machines.repository.adapters;

import com.henryfabio.sqlprovider.executor.adapter.SQLResultAdapter;
import com.henryfabio.sqlprovider.executor.result.SimpleResultSet;
import com.yuhtin.quotes.machines.model.Machine;

public class MachineAdapter implements SQLResultAdapter<Machine> {

    @Override
    public Machine adaptResult(SimpleResultSet resultSet) {
        boolean active = resultSet.get("active");

        return Machine.builder()
                .owner(resultSet.get("username"))
                .encodedLocation(resultSet.get("encodedLocation"))
                .machineDataId(resultSet.get("machine_data_id"))
                .fuelConsumeInterval(resultSet.get("fuel_consume_interval"))
                .active(active)
                .cycles(resultSet.get("cycles"))
                .fuelAmount(resultSet.get("fuel_amount"))
                .drops(resultSet.get("drops"))
                .build();
    }
}
