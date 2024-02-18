package com.yuhtin.quotes.machines.repository.repository;

import com.henryfabio.sqlprovider.executor.SQLExecutor;
import com.yuhtin.quotes.machines.model.Machine;
import com.yuhtin.quotes.machines.repository.adapters.MachineAdapter;
import lombok.Getter;
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
                "encodedLocation INT NOT NULL PRIMARY KEY," +
                "username CHAR(36) NOT NULL," +
                "machine_data_id INT NOT NULL," +
                "fuel_consume_interval INT NOT NULL DEFAULT 10," +
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
        sqlExecutor.updateQuery(
                "INSERT INTO " + TABLE + " VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                statement -> {
                    statement.set(1, machine.getEncodedLocation());
                    statement.set(2, machine.getOwner());
                    statement.set(3, machine.getMachineDataId());
                    statement.set(4, machine.getFuelConsumeInterval());
                    statement.set(5, machine.isActive());
                    statement.set(6, machine.getCycles());
                    statement.set(7, machine.getFuelAmount());
                    statement.set(8, machine.getDrops());
                }
        );
    }

    public static MachineRepository getInstance() {
        return INSTANCE;
    }
}
