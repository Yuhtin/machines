package com.yuhtin.quotes.machines.schematic;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class that stores scheduler information.
 * @author SamB440
 */

@Data
public class Scheduler {

    private int task;
    private int endAfter;
    private int ticks = 0;
    private Runnable run;
    private final List<Object> data = new ArrayList<>();

    public Scheduler() { }

    public Scheduler(final Runnable run) {
        this.run = run;
    }

    public Scheduler endAfter(final int ticks) {
        this.endAfter = ticks;
        return this;
    }

    public int getCurrentTicks() {
        return this.ticks;
    }

    public Scheduler incrementTicks(final int amount) {
        this.ticks = this.ticks + amount;
        if (this.ticks >= this.endAfter) {
            if (run != null) run.run();
            this.cancel();
        }
        return this;
    }

    public void cancel() {
        if (run != null) run.run();
        Bukkit.getScheduler().cancelTask(task);
        this.data.clear();
    }
}