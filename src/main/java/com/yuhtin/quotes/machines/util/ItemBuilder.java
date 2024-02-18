package com.yuhtin.quotes.machines.util;

import me.lucko.helper.text3.Text;
import net.minecraft.server.v1_8_R3.NBTBase;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagList;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static me.lucko.helper.text3.Text.colorize;

public class ItemBuilder {

    private final ItemStack item;

    public ItemBuilder(ItemStack item) {
        this.item = item;
    }

    public ItemBuilder(Material type) {
        this(new ItemStack(type));
    }

    public ItemBuilder(Material type, int data) {
        this(new ItemStack(type, 1, (short) data));
    }

    public ItemBuilder(String name) {
        item = new ItemStack(Material.SKULL_ITEM);

        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setOwner(name);

        item.setItemMeta(meta);
    }

    public ItemBuilder(Material type, Color color) {
        item = new ItemStack(type);

        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
        meta.setColor(color);
        item.setItemMeta(meta);
    }

    public ItemBuilder changeItemMeta(Consumer<ItemMeta> consumer) {
        ItemMeta itemMeta = item.getItemMeta();
        consumer.accept(itemMeta);
        item.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder name(String name) {
        return changeItemMeta(it -> it.setDisplayName(colorize(name)));
    }

    public ItemBuilder setLore(String... lore) {
        return changeItemMeta(it -> it.setLore(Arrays.stream(lore).map(Text::colorize).collect(Collectors.toList())));
    }

    public ItemBuilder setLore(List<String> lore) {
        return changeItemMeta(it -> it.setLore(lore));
    }

    private NBTTagCompound getNBTCompound() {
        net.minecraft.server.v1_8_R3.ItemStack nmsCopy = CraftItemStack.asNMSCopy(item);
        return (nmsCopy.hasTag()) ? nmsCopy.getTag() : new NBTTagCompound();
    }

    private <T> T getNBTCompound(Function<NBTTagCompound, T> function) {
        net.minecraft.server.v1_8_R3.ItemStack nmsCopy = CraftItemStack.asNMSCopy(item);
        if (nmsCopy == null) {
            function.apply(null);
            return null;
        }

        NBTTagCompound compound = (nmsCopy.hasTag()) ? nmsCopy.getTag() : new NBTTagCompound();
        return function.apply(compound);
    }

    private void modifyNBTCompound(Consumer<NBTTagCompound> consumer) {
        net.minecraft.server.v1_8_R3.ItemStack nmsCopy = CraftItemStack.asNMSCopy(item);
        NBTTagCompound compound = (nmsCopy.hasTag()) ? nmsCopy.getTag() : new NBTTagCompound();
        consumer.accept(compound);
        nmsCopy.setTag(compound);

        ItemMeta meta = CraftItemStack.asBukkitCopy(nmsCopy).getItemMeta();
        item.setItemMeta(meta);
    }

    public ItemBuilder removeNBTTag(String key) {
        modifyNBTCompound((NBTTagCompound compound) -> {
            compound.remove(key);
        });

        return this;
    }

    public ItemBuilder setNBTTag(String tag, NBTBase value) {
        modifyNBTCompound((NBTTagCompound compound) -> {
            compound.set(tag, value);
        });

        return this;
    }

    public ItemBuilder setNBTByte(String key, byte value) {
        modifyNBTCompound((NBTTagCompound compound) -> {
            compound.setByte(key, value);
        });
        return this;
    }

    public ItemBuilder setNBTShort(String key, short value) {
        modifyNBTCompound((NBTTagCompound compound) -> {
            compound.setShort(key, value);
        });
        return this;
    }

    public ItemBuilder setNBTInt(String key, int value) {
        modifyNBTCompound((NBTTagCompound compound) -> {
            compound.setInt(key, value);
        });
        return this;
    }

    public ItemBuilder setNBTLong(String key, long value) {
        modifyNBTCompound((NBTTagCompound compound) -> {
            compound.setLong(key, value);
        });
        return this;
    }

    public ItemBuilder setNBTFloat(String key, float value) {
        modifyNBTCompound((NBTTagCompound compound) -> {
            compound.setFloat(key, value);
        });
        return this;
    }

    public ItemBuilder setNBTDouble(String key, double value) {
        modifyNBTCompound((NBTTagCompound compound) -> {
            compound.setDouble(key, value);
        });
        return this;
    }

    public ItemBuilder setNBTString(String key, String value) {
        modifyNBTCompound((NBTTagCompound compound) -> {
            compound.setString(key, value);
        });
        return this;
    }

    public ItemBuilder setNBTByteArray(String key, byte[] value) {
        modifyNBTCompound((NBTTagCompound compound) -> {
            compound.setByteArray(key, value);
        });
        return this;
    }

    public ItemBuilder setNBTIntArray(String key, int[] value) {
        modifyNBTCompound((NBTTagCompound compound) -> {
            compound.setIntArray(key, value);
        });
        return this;
    }

    public ItemBuilder setNBTBoolean(String key, boolean value) {
        setNBTByte(key, (byte) (value ? 1 : 0));
        return this;
    }

    public <T> NBTBase getNBTTag(String key) {
        return getNBTCompound((NBTTagCompound compound) -> compound == null ? null : compound.get(key));
    }

    public boolean hasNBTKey(String key) {
        return getNBTTag(key) != null;
    }

    public String getNBTString(String tag) {
        return getNBTCompound(compound -> {
            return compound.hasKey(tag) ? compound.getString(tag) : null;
        });
    }

    public Integer getNBTInt(String tag) {
        return getNBTCompound(compound -> {
            return compound.hasKey(tag) ? compound.getInt(tag) : null;
        });
    }

    public Long getNBTLong(String tag) {
        return getNBTCompound(compound -> {
            return compound.hasKey(tag) ? compound.getLong(tag) : null;
        });
    }

    public Double getNBTDouble(String tag) {
        return getNBTCompound(compound -> {
            return compound.hasKey(tag) ? compound.getDouble(tag) : null;
        });
    }

    public Boolean getNBTBoolean(String tag) {
        return getNBTCompound(compound -> {
            return compound.hasKey(tag) ? compound.getBoolean(tag) : null;
        });
    }

    public NBTTagList getNBTList(String tag) {
        return getNBTCompound(compound -> {
            NBTBase base = compound.get(tag);
            return base instanceof NBTTagList ? (NBTTagList) base : new NBTTagList();
        });
    }


    public ItemStack wrap() {
        return item;
    }

}