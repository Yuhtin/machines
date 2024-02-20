package com.yuhtin.quotes.machines.view;

import com.henryfabio.minecraft.inventoryapi.editor.InventoryEditor;
import com.henryfabio.minecraft.inventoryapi.inventory.impl.paged.PagedInventory;
import com.henryfabio.minecraft.inventoryapi.item.InventoryItem;
import com.henryfabio.minecraft.inventoryapi.item.supplier.InventoryItemSupplier;
import com.henryfabio.minecraft.inventoryapi.viewer.Viewer;
import com.henryfabio.minecraft.inventoryapi.viewer.configuration.border.Border;
import com.henryfabio.minecraft.inventoryapi.viewer.configuration.impl.ViewerConfigurationImpl;
import com.henryfabio.minecraft.inventoryapi.viewer.impl.paged.PagedViewer;
import com.yuhtin.quotes.machines.cache.MachineDataCache;
import com.yuhtin.quotes.machines.model.MachineData;
import com.yuhtin.quotes.machines.util.ItemBuilder;
import com.yuhtin.quotes.machines.util.VaultAPI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static me.lucko.helper.text3.Text.colorize;

public class ShopView extends PagedInventory {
    public ShopView() {
        super("machine.shop", "Mineradoras", 6 * 9);
    }

    @Override
    protected void configureViewer(@NotNull PagedViewer viewer) {
        ViewerConfigurationImpl.Paged configuration = viewer.getConfiguration();
        configuration.previousPageSlot(19);
        configuration.nextPageSlot(25);
        configuration.border(Border.of(2, 2, 2, 2));
    }

    @Override
    protected void configureInventory(@NotNull Viewer viewer, @NotNull InventoryEditor editor) {
        editor.setItem(49, InventoryItem.of(new ItemBuilder(Material.BARRIER)
                .name("&cFechar loja")
                .wrap()
        ).defaultCallback(callback -> callback.getPlayer().closeInventory()));
    }

    @Override
    protected List<InventoryItemSupplier> createPageItems(@NotNull PagedViewer viewer) {
        List<InventoryItemSupplier> items = new ArrayList<>();

        for (MachineData data : MachineDataCache.instance().values()) {
            items.add(() -> InventoryItem.of(new ItemBuilder(data.getItem())
                    .name(data.getCustomName())
                    .setLore(data.getLore())
                    .wrap()
            ).defaultCallback(callback -> {
                Player player = callback.getPlayer();

                if (!VaultAPI.instance().withdraw(player, data.getPrice())) {
                    player.sendMessage(colorize("&cVocê não tem dinheiro suficiente para comprar esta mineradora."));
                    player.playSound(player.getLocation(), "entity.villager.no", 1, 1);
                    return;
                }

                player.getInventory().addItem(data.generateItem()).forEach((index, item) -> player.getWorld().dropItem(player.getLocation(), item));
                player.sendMessage(colorize("&aVocê comprou uma mineradora por $ " + data.getPrice() + "."));
                player.playSound(player.getLocation(), "entity.villager.yes", 1, 1);
            }));
        }

        return items;
    }
}
