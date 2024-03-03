package com.yuhtin.quotes.machines.cache;

import com.yuhtin.quotes.machines.model.Machine;
import com.yuhtin.quotes.machines.repository.repository.MachineRepository;
import com.yuhtin.quotes.machines.util.SimpleLocation;
import lombok.Data;
import me.lucko.helper.Schedulers;
import me.lucko.helper.bucket.Bucket;
import me.lucko.helper.bucket.factory.BucketFactory;
import me.lucko.helper.bucket.partitioning.PartitioningStrategies;
import me.lucko.helper.scheduler.Task;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;


@Data
public class MachineCache {

    private static MachineCache instance;
    private MachineRepository repository = MachineRepository.instance();

    private final Map<SimpleLocation, Machine> machines = new LinkedHashMap<>();
    private final Bucket<Machine> machineBucket = BucketFactory.newHashSetBucket(20, PartitioningStrategies.nextInCycle());

    private Task task;


    @Nullable
    public Machine getMachine(SimpleLocation encodedLocation) {
        return machines.get(encodedLocation);
    }

    @Nullable
    public Machine getMachine(Location location) {
        return getMachine(SimpleLocation.encode(location));
    }

    public void addMachine(Machine machine) {
        machines.put(machine.getSimpleLocation(), machine);
        machineBucket.add(machine);
    }

    public void removeMachine(SimpleLocation location) {
        Machine removed = machines.remove(location);
        if (removed != null) machineBucket.remove(removed);
    }

    public void enableTicking() {
        if (task != null) {
            task.stop();
        }

        machineBucket.clear();
        machineBucket.addAll(machines.values());

        task = Schedulers.async().runRepeating(
                () -> machineBucket.asCycle().next().forEach(this::updateMachine),
                1, 1
        );
    }

    private void updateMachine(Machine machine) {
        machine.tick();
        repository.insert(machine);
    }

    public static MachineCache instance() {
        if (instance == null) instance = new MachineCache();
        return instance;
    }

}