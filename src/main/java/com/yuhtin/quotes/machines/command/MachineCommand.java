package com.yuhtin.quotes.machines.command;

import com.yuhtin.quotes.machines.MachinesPlugin;
import com.yuhtin.quotes.machines.cache.FuelCache;
import com.yuhtin.quotes.machines.cache.MachineDataCache;
import com.yuhtin.quotes.machines.model.Fuel;
import com.yuhtin.quotes.machines.model.MachineData;
import com.yuhtin.quotes.machines.view.ViewCache;
import me.lucko.helper.Commands;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class MachineCommand implements TerminableModule {

    @Override
    public void setup(@NotNull TerminableConsumer consumer) {
        Commands.create().handler(context -> {

            if (context.arg(0).isPresent() && context.sender().hasPermission("machines.admin")) {
                String arg = context.arg(0).parse(String.class).orElse("help");

                if (arg.equalsIgnoreCase("fuel")) {
                    Player player = context.arg(1).parseOrFail(Player.class);
                    int id = context.arg(2).parseOrFail(Integer.class);
                    int amount = context.arg(3).parseOrFail(Integer.class);

                    Fuel data = FuelCache.instance().get(id);
                    if (data == null) {
                        context.reply("&cCombustível não encontrado");
                        context.reply("&cCombutívels disponíveis: " + FuelCache.instance().getIds());
                        return;
                    }

                    ItemStack item = data.getItem().clone();
                    item.setAmount(amount);

                    player.getInventory().addItem(item).forEach((index, itemStack) -> player.getWorld().dropItem(player.getLocation(), itemStack));
                    context.reply("&aCombustível entregue com sucesso");

                    return;
                }

                if (arg.equalsIgnoreCase("give")) {
                    Player player = context.arg(1).parseOrFail(Player.class);
                    int id = context.arg(2).parseOrFail(Integer.class);
                    int amount = context.arg(3).parseOrFail(Integer.class);

                    MachineData data = MachineDataCache.instance().get(id);
                    if (data == null) {
                        context.reply("&cMáquina não encontrada");
                        context.reply("&cMáquinas disponíveis: " + MachineDataCache.instance().getIds());
                        return;
                    }

                    for (int i = 0; i < Math.min(2304, amount); i++) {
                        ItemStack item = data.generateItem();
                        player.getInventory().addItem(item).forEach((index, itemStack) -> player.getWorld().dropItem(player.getLocation(), itemStack));
                    }

                    context.reply("&aMáquina entregue com sucesso");

                    return;
                }

                if (arg.equalsIgnoreCase("reload")) {
                    MachinesPlugin.getInstance().reloadConfig();
                    FuelCache.instance().reload();
                    MachineDataCache.instance().reload();

                    context.reply("&aSistema recarregado com sucesso");
                    return;
                }

                context.reply("&eComandos disponíveis:");
                context.reply("&e/mineradoras fuel <player> <id> <quantidade> - &7Giva combustíveis para um player");
                context.reply("&e/mineradoras give <player> <id> <quantidade> - &7Giva mineradoras para um player");
                context.reply("&e/mineradoras reload - &7Recarrega os dados do sistema");

                return;
            }

            if (context.sender() instanceof Player) {
                ViewCache.instance().openShopView((Player) context.sender());
            }
        }).registerAndBind(consumer, "mineradoras", "maquinas", "mineradora");
    }
}
