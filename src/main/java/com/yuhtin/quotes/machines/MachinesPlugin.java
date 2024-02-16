package com.yuhtin.quotes.machines;

import me.lucko.helper.plugin.ExtendedJavaPlugin;

public class MachinesPlugin extends ExtendedJavaPlugin {

    @Override
    protected void load() {
        saveDefaultConfig();
    }

    @Override
    protected void enable() {

    }

    public static MachinesPlugin getInstance() {
        return getPlugin(MachinesPlugin.class);
    }

}
