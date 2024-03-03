package com.yuhtin.quotes.machines.view;


import com.henryfabio.minecraft.inventoryapi.editor.InventoryEditor;
import com.henryfabio.minecraft.inventoryapi.inventory.impl.simple.SimpleInventory;
import com.henryfabio.minecraft.inventoryapi.item.InventoryItem;
import com.henryfabio.minecraft.inventoryapi.viewer.Viewer;
import com.henryfabio.minecraft.inventoryapi.viewer.impl.simple.SimpleViewer;
import com.yuhtin.quotes.machines.MachinesPlugin;
import com.yuhtin.quotes.machines.cache.MachineDataCache;
import com.yuhtin.quotes.machines.model.Machine;
import com.yuhtin.quotes.machines.model.MachineData;
import com.yuhtin.quotes.machines.util.ItemBuilder;
import com.yuhtin.quotes.machines.util.NumberUtils;
import com.yuhtin.quotes.machines.util.VaultAPI;
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

    @Override
    protected void update(@NotNull Viewer viewer, @NotNull InventoryEditor editor) {
        super.update(viewer, editor);
        configureInventory(viewer, editor);
    }

    private void activateItem(InventoryEditor editor, Machine machine) {
        if (machine.isActive()) {

            int timeInSeconds = machine.getCycles() + (machine.getFuelAmount() * machine.getFuelConsumeInterval());
            String time = formatTime(timeInSeconds);

            editor.setItem(12, InventoryItem.of(new ItemBuilder(Material.GREEN_DYE)
                    .name("&aMineradora ligada...")
                    .setLore(
                            "&7A mineradora está ligada, fique de olho para não",
                            "&7acabar seu combustível!",
                            "",
                            "&7Tempo restante: &e" + time
                    ).wrap()
            ));
        } else {
            editor.setItem(12, InventoryItem.of(new ItemBuilder(Material.GRAY_DYE)
                    .name("&cSem combustível...")
                    .setLore(
                            "",
                            "&eAdicione combustível para ligar a mineradora!"
                    ).wrap()
            ));
        }
    }

    private void storageItem(InventoryEditor editor, Machine machine) {
        MachineData data = MachineDataCache.instance().get(machine.getMachineDataId());
        if (data == null) return;

        double sellPriceActual = machine.getDrops() * data.getSellDropPrice();
        editor.setItem(14, InventoryItem.of(new ItemBuilder(Material.CHEST)
                .name("&eArmazenamento")
                .setLore(
                        "&7Drops armazenados: &e" + NumberUtils.format(machine.getDrops()),
                        "&7Preço de venda: &a$ " + NumberUtils.format(sellPriceActual),
                        "",
                        "&aClique para vender os drops!"
                ).wrap()
        ).defaultCallback(callback -> {
            Player player = callback.getPlayer();
            player.closeInventory();

            double drops = machine.getDrops();
            if (drops < 1) {
                player.sendMessage(colorize("&cVocê não possui drops para vender!"));
                return;
            }

            double sellPrice = drops * data.getSellDropPrice();
            player.sendMessage(colorize("&6&lMINERADORA &fVocê vendeu &a" + NumberUtils.format(drops) + " &fdrops por &a$ " + NumberUtils.format(sellPrice) + "!"));

            VaultAPI.instance().deposit(player, sellPrice);
            machine.setDrops(0);
        }));
    }

    private String formatTime(int timeInSeconds) {
        int minutes = timeInSeconds / 60;
        int seconds = timeInSeconds % 60;

        return String.format("%02d:%02d", minutes, seconds);
    }
}
