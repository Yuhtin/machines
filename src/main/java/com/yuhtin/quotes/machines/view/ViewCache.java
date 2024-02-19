package com.yuhtin.quotes.machines.view;

import com.yuhtin.quotes.machines.model.Machine;
import lombok.Getter;
import org.bukkit.entity.Player;

public class ViewCache {

    private static ViewCache instance;

    private final MachineView machineView;
    private final ShopView shopView;

    public ViewCache() {
        this.machineView = new MachineView().init();
        this.shopView = new ShopView();
    }

    public void openMachineView(Player player, Machine machine) {
        machineView.openInventory(player, viewer -> viewer.getPropertyMap().set("machine", machine));
    }

    public void openShopView(Player player) {
        shopView.openInventory(player);
    }

    public static ViewCache instance() {
        if (instance == null) instance = new ViewCache();
        return instance;
    }

}
