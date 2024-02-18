package com.yuhtin.quotes.machines.listener;

import com.yuhtin.quotes.machines.cache.FuelCache;
import com.yuhtin.quotes.machines.cache.MachineCache;
import com.yuhtin.quotes.machines.cache.MachineDataCache;
import com.yuhtin.quotes.machines.model.Fuel;
import com.yuhtin.quotes.machines.model.Machine;
import com.yuhtin.quotes.machines.model.MachineData;
import com.yuhtin.quotes.machines.util.ItemBuilder;
import lombok.AllArgsConstructor;
import me.lucko.helper.Events;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public class MachinePlaceListener implements TerminableModule {

    private final MachineCache cache;

    @Override
    public void setup(@NotNull TerminableConsumer consumer) {

        Events.subscribe(PlayerInteractEvent.class, EventPriority.HIGHEST)
            .handler(event -> {
                if (event.getAction() != Action.RIGHT_CLICK_BLOCK
                        || !event.getPlayer().getWorld().getName().equalsIgnoreCase(cache.getWorld()))
                    return;

                Block block = event.getClickedBlock();
                if (!cache.getValidMaterials().contains(block.getType())) return;

                Machine machine = cache.getMachine(block.getLocation());
                if (machine == null) return;

                event.setCancelled(true);

                Player player = event.getPlayer();
                ItemStack itemInHand = player.getItemInHand();
                if (itemInHand != null) {
                    Fuel fuel = FuelCache.instance().getByItem(itemInHand);
                    if (fuel != null) {
                        machine.fuelUp(itemInHand.getAmount());
                        player.setItemInHand(null);

                        return;
                    }
                }

                // TODO: open machine view
            }).bindWith(consumer);

    }

}
