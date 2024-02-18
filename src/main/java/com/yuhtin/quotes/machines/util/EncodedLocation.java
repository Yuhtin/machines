package com.yuhtin.quotes.machines.util;

import org.bukkit.Location;
import org.bukkit.World;

public class EncodedLocation {

    private final int encoded;

    public Location decode(World world) {
        int x = encoded >> 6;
        int y = (encoded >> 26) & 0xFFF;
        int z = encoded << 6 >> 6;
        return new Location(world, x, y, z);
    }

    private EncodedLocation(int x, int y, int z) {
        this.encoded = ((x << 6) | (y << 26) | z);
    }

    public static EncodedLocation encode(Location location) {
        return new EncodedLocation(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    @Override
    public int hashCode() {
        return encoded;
    }

}
