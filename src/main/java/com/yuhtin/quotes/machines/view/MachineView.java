package com.yuhtin.quotes.machines.view;


import com.henryfabio.minecraft.inventoryapi.editor.InventoryEditor;
import com.henryfabio.minecraft.inventoryapi.inventory.impl.simple.SimpleInventory;
import com.henryfabio.minecraft.inventoryapi.item.InventoryItem;
import com.henryfabio.minecraft.inventoryapi.viewer.Viewer;
import com.henryfabio.minecraft.inventoryapi.viewer.impl.simple.SimpleViewer;
import com.yuhtin.quotes.machines.cache.MachineDataCache;
import com.yuhtin.quotes.machines.model.Machine;
import com.yuhtin.quotes.machines.model.MachineData;
import com.yuhtin.quotes.machines.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static me.lucko.helper.text3.Text.colorize;

public class MachineView extends SimpleInventory {

    public MachineView() {
        super("machine.view", "Mineradora {type}", 3 * 9);
        getConfiguration().secondUpdate(1);
    }

    @Override
    protected void configureViewer(@NotNull SimpleViewer viewer) {
        Machine machine = viewer.getPropertyMap().get("machine");
        viewer.getConfiguration().titleInventory(machine.getCustomName());
    }

    @Override
    protected void configureInventory(@NotNull Viewer viewer, @NotNull InventoryEditor editor) {
        Machine machine = viewer.getPropertyMap().get("machine");

        activateItem(editor, machine);
        storageItem(editor, machine);
    }

    private void activateItem(InventoryEditor editor, Machine machine) {
        if (machine.isActive()) {

            int timeInSeconds = machine.getCycles() + (machine.getFuelAmount() * machine.getFuelConsumeInterval());
            String time = formatTime(timeInSeconds);

            editor.setItem(13, InventoryItem.of(new ItemBuilder(Material.GREEN_DYE)
                    .name("&aMineradora ligada...")
                    .setLore(
                            "&7A mineradora está ligada, fique de olho para não",
                            "&7acabar seu combustível!",
                            "",
                            "&7Tempo restante: &e" + time
                    ).wrap()
            ));
        } else {
            editor.setItem(13, InventoryItem.of(new ItemBuilder(Material.GRAY_DYE)
                    .name("&cSem combustível...")
                    .setLore(
                            "",
                            "&eAdicione combustível para ligar a mineradora!"
                    ).wrap()
            ));
        }
    }

    private void storageItem(InventoryEditor editor, Machine machine) {
        editor.setItem(15, InventoryItem.of(new ItemBuilder(Material.CHEST)
                .name("&eArmazenamento")
                .setLore(
                        "&7Drops armazenados: &e" + machine.getDrops(),
                        "",
                        "&aClique para vender os drops!"
                ).wrap()
        ).defaultCallback(callback -> {
            Player player = callback.getPlayer();
            player.closeInventory();

            MachineData data = MachineDataCache.instance().get(machine.getMachineDataId());
            if (data == null) {
                player.sendMessage(colorize("&cOcorreu um erro ao tentar vender os drops!"));
                return;
            }

            double dropPrice = machine.getDrops() * data.getSellDropPrice();
            player.sendMessage(colorize("&6&lMINERADORA &fVocê vendeu &a" + machine.getDrops() + " &fdrops por &a$ " + dropPrice + "!"));

            machine.setDrops(0);
        }));
    }

    private String formatTime(int timeInSeconds) {
        int minutes = timeInSeconds / 60;
        int seconds = timeInSeconds % 60;

        return String.format("%02d:%02d", minutes, seconds);
    }
}
