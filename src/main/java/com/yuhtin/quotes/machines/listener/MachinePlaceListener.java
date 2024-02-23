package com.yuhtin.quotes.machines.listener;

import com.yuhtin.quotes.machines.cache.FuelCache;
import com.yuhtin.quotes.machines.cache.MachineCache;
import com.yuhtin.quotes.machines.cache.MachineDataCache;
import com.yuhtin.quotes.machines.model.Fuel;
import com.yuhtin.quotes.machines.model.Machine;
import com.yuhtin.quotes.machines.model.MachineData;
import com.yuhtin.quotes.machines.util.EncodedLocation;
import com.yuhtin.quotes.machines.view.ViewCache;
import lombok.AllArgsConstructor;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static me.lucko.helper.text3.Text.colorize;

@AllArgsConstructor
public class MachinePlaceListener implements TerminableModule {

    @Override
    public void setup(@NotNull TerminableConsumer consumer) {

        MachineCache cache = MachineCache.instance();
        MachineDataCache dataCache = MachineDataCache.instance();

        Events.subscribe(BlockPlaceEvent.class, EventPriority.HIGHEST)
                .handler(event -> {
                    if (!dataCache.getWorlds().contains(event.getBlock().getWorld().getName())) return;

                    ItemStack itemInHand = event.getItemInHand();
                    if (itemInHand == null) return;

                    Block block = event.getBlock();
                    Location location = block.getLocation();

                    Player player = event.getPlayer();

                    if (!dataCache.getValidMaterials().contains(block.getType())) return;

                    MachineData machineData = dataCache.getByItem(player.getItemInHand());
                    if (machineData == null) return;

                    event.setCancelled(true);

                    Machine machine = Machine.builder()
                            .owner(player.getName())
                            .customName(machineData.getCustomName())
                            .machineDataId(machineData.getId())
                            .encodedLocation(EncodedLocation.encode(location).hashCode())
                            .fuelConsumeInterval(machineData.getSpendFuelInterval())
                            .build();

                    if (!machine.pasteSchematic(player, location.getWorld())) {
                        player.sendMessage(colorize("&cNão é possível colocar a máquina aqui pois não há espaço suficiente."));
                        return;
                    }

                    Schedulers.sync().runLater(() -> event.getBlock().setType(machineData.getItem().getType()), 10);
                    cache.addMachine(machine);

                    player.setItemInHand(itemInHand.getAmount() > 1
                            ? new ItemStack(itemInHand.getType(), itemInHand.getAmount() - 1)
                            : null);
                }).bindWith(consumer);

        Events.subscribe(BlockBreakEvent.class, EventPriority.HIGHEST)
                .handler(event -> {
                    if (!dataCache.getWorlds().contains(event.getBlock().getWorld().getName())) return;

                    Player player = event.getPlayer();
                    Block block = event.getBlock();

                    if (!dataCache.getValidMaterials().contains(block.getType())) return;

                    Machine machine = cache.getMachine(block.getLocation());
                    if (machine == null) return;

                    event.setCancelled(true);

                    if (!machine.getOwner().equalsIgnoreCase(player.getName()) && !player.isOp()) return;

                    if (!machine.getOwner().equals(event.getPlayer().getName()) && !event.getPlayer().hasPermission("machine.admin")) {
                        event.getPlayer().sendMessage(colorize("&cVocê não pode interagir com a máquina de outro jogador."));
                        return;
                    }

                    if (machine.isBuildingSchematic()) {
                        player.sendMessage(colorize("&cA máquina ainda está sendo construída."));
                        return;
                    }

                    if (machine.isActive()) {
                        player.sendMessage(colorize("&cVocê não pode quebrar a máquina enquanto ela está ativa."));
                        return;
                    }

                    if (machine.getDrops() > 0) {
                        player.sendMessage(colorize("&cVocê precisa vender os itens da máquina antes de quebrá-la."));
                        return;
                    }

                    block.setType(Material.AIR);

                    machine.cleanUpSchematic(block.getWorld());
                    cache.removeMachine(machine.getEncodedLocation());

                    player.getInventory().addItem(dataCache.get(machine.getMachineDataId()).getItem())
                            .forEach((index, item) -> player.getWorld().dropItemNaturally(block.getLocation(), item));

                }).bindWith(consumer);

        Events.subscribe(PlayerInteractEvent.class, EventPriority.HIGHEST)
                .handler(event -> {
                    if (event.getAction() != Action.RIGHT_CLICK_BLOCK
                            || !dataCache.getWorlds().contains(event.getPlayer().getWorld().getName()))
                        return;

                    Block block = event.getClickedBlock();
                    if (!dataCache.getValidMaterials().contains(block.getType())) return;

                    Machine machine = cache.getMachine(block.getLocation());
                    if (machine == null) return;

                    event.setCancelled(true);

                    if (!machine.getOwner().equals(event.getPlayer().getName()) && !event.getPlayer().hasPermission("machine.admin")) {
                        event.getPlayer().sendMessage(colorize("&cVocê não pode interagir com a máquina de outro jogador."));
                        return;
                    }

                    if (machine.isBuildingSchematic()) {
                        event.getPlayer().sendMessage(colorize("&cA máquina ainda está sendo construída."));
                        return;
                    }

                    Player player = event.getPlayer();
                    ItemStack itemInHand = player.getItemInHand();
                    if (itemInHand != null) {
                        Fuel fuel = FuelCache.instance().getByItem(itemInHand);
                        if (fuel != null) {
                            int requiredFuelId = MachineDataCache.instance().get(machine.getMachineDataId()).getRequiredFuelId();
                            if (fuel.getId() == requiredFuelId) {
                                machine.fuelUp(itemInHand.getAmount());
                                player.setItemInHand(null);
                            } else {
                                player.sendMessage(colorize("&cEste combustível não é válido para esta máquina."));
                            }

                            return;
                        }
                    }

                    ViewCache.instance().openMachineView(player, machine);
                }).bindWith(consumer);

    }

}
