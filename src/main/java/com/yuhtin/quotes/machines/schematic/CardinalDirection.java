package com.yuhtin.quotes.machines.schematic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;


@Getter
@AllArgsConstructor
public enum CardinalDirection {

    NORTH(0), SOUTH(180), EAST(270), WEST(90);

    private final int rotation;

    public static CardinalDirection getCardinalDirection(Player player) {
        float yaw = player.getLocation().getYaw();

        if (yaw < 0) {
            yaw += 360;
        }

        if (yaw >= 315 || yaw < 45) {
            return SOUTH;
        } else if (yaw < 135) {
            return WEST;
        } else if (yaw < 225) {
            return NORTH;
        } else if (yaw < 315) {
            return EAST;
        }

        return NORTH;
    }

}