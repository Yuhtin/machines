package com.yuhtin.quotes.machines.util;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Objects;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SimpleLocation {

    private final String world;
    private final int x;
    private final int y;
    private final int z;

    public static SimpleLocation encode(Location location) {
        return new SimpleLocation(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public static SimpleLocation of(String stringed) {
        String[] split = stringed.split(";");
        return new SimpleLocation(split[0], Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]));
    }

    public Location decode() {
        return new Location(Bukkit.getWorld(world), x, y, z);
    }

    @Override
    public String toString() {
        return world + ";" + x + ";" + y + ";" + z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleLocation that = (SimpleLocation) o;
        return Double.compare(that.x, x) == 0 &&
                Double.compare(that.y, y) == 0 &&
                Double.compare(that.z, z) == 0 &&
                Objects.equals(world, that.world);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z, world);
    }
}
