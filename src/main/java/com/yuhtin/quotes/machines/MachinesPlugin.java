package com.yuhtin.quotes.machines;

import com.henryfabio.minecraft.inventoryapi.manager.InventoryManager;
import com.henryfabio.sqlprovider.connector.SQLConnector;
import com.henryfabio.sqlprovider.executor.SQLExecutor;
import com.yuhtin.quotes.machines.cache.FuelCache;
import com.yuhtin.quotes.machines.cache.MachineCache;
import com.yuhtin.quotes.machines.cache.MachineDataCache;
import com.yuhtin.quotes.machines.command.MachineCommand;
import com.yuhtin.quotes.machines.listener.MachinePlaceListener;
import com.yuhtin.quotes.machines.repository.SQLProvider;
import com.yuhtin.quotes.machines.repository.repository.MachineRepository;
import com.yuhtin.quotes.machines.util.VaultAPI;
import me.lucko.helper.plugin.ExtendedJavaPlugin;

public class MachinesPlugin extends ExtendedJavaPlugin {

    @Override
    protected void load() {
        saveDefaultConfig();
    }

    @Override
    protected void enable() {
        InventoryManager.enable(this);
        VaultAPI.instance().setupEconomy();

        loadFileData();
        loadDatabase();
        loadMachineData();

        bindModule(new MachineCommand());
        bindModule(new MachinePlaceListener());

        MachineDataCache.instance().setWorlds(getConfig().getStringList("machine-worlds"));

        getLogger().info("Plugin ligado com sucesso!");
    }

    private void loadFileData() {
        FuelCache.instance().reload();
        MachineDataCache.instance().reload();
    }

    private void loadMachineData() {
        MachineCache.instance().enableTicking();

        MachineRepository.instance()
                .findAll()
                .thenAcceptAsync(machines -> machines.forEach(MachineCache.instance()::addMachine));
    }

    private void loadDatabase() {
        SQLConnector connector = SQLProvider.of(this).setup(null);
        MachineRepository.instance().init(new SQLExecutor(connector));
    }

    public static MachinesPlugin getInstance() {
        return getPlugin(MachinesPlugin.class);
    }

}
