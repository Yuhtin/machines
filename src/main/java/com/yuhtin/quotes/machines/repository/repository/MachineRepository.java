package com.yuhtin.quotes.machines.repository.repository;

import com.henryfabio.sqlprovider.executor.SQLExecutor;
import com.yuhtin.quotes.machines.model.Machine;
import com.yuhtin.quotes.machines.repository.adapters.MachineAdapter;
import lombok.RequiredArgsConstructor;
import me.lucko.helper.Schedulers;
import me.lucko.helper.promise.Promise;

import java.util.Set;

/**
 * @author <a href="https://github.com/Yuhtin">Yuhtin</a>
 */
@RequiredArgsConstructor
public final class MachineRepository {

    private static final MachineRepository INSTANCE = new MachineRepository();
    private static final String TABLE = "machine_data";

    private SQLExecutor sqlExecutor;

    public void init(SQLExecutor sqlExecutor) {
        this.sqlExecutor = sqlExecutor;
        createTable();
    }

    public void createTable() {
        sqlExecutor.updateQuery("CREATE TABLE IF NOT EXISTS " + TABLE + "(" +
                "location LONGTEXT NOT NULL PRIMARY KEY," +
                "username CHAR(36) NOT NULL," +
                "cardinalDirection CHAR(36) NOT NULL DEFAULT 'NORTH'," +
                "yawPlaced DOUBLE NOT NULL DEFAULT 0," +
                "machine_data_id INT NOT NULL," +
                "active BOOLEAN NOT NULL DEFAULT FALSE," +
                "cycles INT NOT NULL DEFAULT -1," +
                "fuel_amount INT NOT NULL DEFAULT 0," +
                "drops DOUBLE NOT NULL DEFAULT 0" +
                ");"
        );
    }

    public void recreateTable() {
        sqlExecutor.updateQuery("DROP TABLE IF EXISTS " + TABLE);
        createTable();
    }

    public Promise<Set<Machine>> findAll() {
        return Schedulers.async().call(() -> sqlExecutor.resultManyQuery(
                "SELECT * FROM " + TABLE,
                statement -> {},
                MachineAdapter.class
        ));
    }

    public void insert(Machine machine) {
        Schedulers.async().run(() -> sqlExecutor.updateQuery(
                "REPLACE INTO " + TABLE + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                statement -> {
                    statement.set(1, machine.getSimpleLocation());
                    statement.set(2, machine.getOwner());
                    statement.set(3, machine.getDirection().name());
                    statement.set(4, machine.getYawPlaced());
                    statement.set(5, machine.getMachineDataId());
                    statement.set(6, machine.isActive());
                    statement.set(7, machine.getCycles());
                    statement.set(8, machine.getFuelAmount());
                    statement.set(9, machine.getDrops());
                }
        ));
    }

    public static MachineRepository instance() {
        return INSTANCE;
    }
}
