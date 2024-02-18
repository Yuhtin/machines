package com.yuhtin.quotes.machines.cache;

import com.yuhtin.quotes.machines.model.Machine;
import com.yuhtin.quotes.machines.util.EncodedLocation;
import lombok.Data;
import me.lucko.helper.Schedulers;
import me.lucko.helper.bucket.Bucket;
import me.lucko.helper.bucket.BucketPartition;
import me.lucko.helper.bucket.factory.BucketFactory;
import me.lucko.helper.bucket.partitioning.PartitioningStrategies;
import me.lucko.helper.scheduler.Task;
import org.bukkit.Location;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Data
public class MachineCache {

    private static MachineCache instance;

    private String world;
    private final List<Material> validMaterials = new ArrayList<>();

    private final Map<Integer, Machine> machines = new LinkedHashMap<>();
    private final Bucket<Machine> machineBucket = BucketFactory.newHashSetBucket(20, PartitioningStrategies.nextInCycle());

    private Task task;


    @Nullable
    public Machine getMachine(int encodedLocation) {
        return machines.get(encodedLocation);
    }

    @Nullable
    public Machine getMachine(Location location) {
        return getMachine(EncodedLocation.encode(location).hashCode());
    }

    public void addMachine(int key, Machine machine) {
        machines.put(key, machine);
        machineBucket.add(machine);
    }

    public void removeMachine(int encodedLocation) {
        Machine removed = machines.remove(encodedLocation);
        if (removed != null) machineBucket.remove(removed);
    }

    public void removeMachine(Location location) {
        removeMachine(EncodedLocation.encode(location).hashCode());
    }

    public void enableTicking() {
        if (task != null) {
            task.stop();
        }

        machineBucket.clear();
        machineBucket.addAll(machines.values());

        task = Schedulers.async().runRepeating(() -> {
            BucketPartition<Machine> part = machineBucket.asCycle().next();
            if (part.isEmpty()) return;

            for (Machine machine : part) {
                if (!machine.isActive() || !machine.tick()) {
                    machineBucket.remove(machine);
                }
            }
        }, 1, 1);
    }

    public static MachineCache instance() {
        if (instance == null) instance = new MachineCache();
        return instance;
    }

}