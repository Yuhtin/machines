package com.yuhtin.quotes.machines.util;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultAPI {

    private static VaultAPI instance;
    private Economy economy;

    public boolean setupEconomy() {
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;

        this.economy = rsp.getProvider();
        return this.economy != null;
    }

    public boolean has(Player player, double amount) {
        return this.economy.has(player, amount);
    }

    public boolean withdraw(Player player, double amount) {
        return this.economy.withdrawPlayer(player, amount).transactionSuccess();
    }

    public boolean deposit(Player player, double amount) {
        return this.economy.depositPlayer(player, amount).transactionSuccess();
    }

    public static VaultAPI instance() {
        if (instance == null) instance = new VaultAPI();
        return instance;
    }

}
