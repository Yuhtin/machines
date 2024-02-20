package com.yuhtin.quotes.machines.util;

import me.lucko.helper.text3.Text;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_19_R2.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
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
        item = new ItemStack(Material.PLAYER_HEAD);

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
        net.minecraft.world.item.ItemStack nmsCopy = CraftItemStack.asNMSCopy(item);
        return (nmsCopy.hasTag()) ? nmsCopy.getTag() : new NBTTagCompound();
    }

    private <T> T getNBTCompound(Function<NBTTagCompound, T> function) {
        net.minecraft.world.item.ItemStack nmsCopy = CraftItemStack.asNMSCopy(item);
        if (nmsCopy == null) {
            function.apply(null);
            return null;
        }

        NBTTagCompound compound = (nmsCopy.hasTag()) ? nmsCopy.getTag() : new NBTTagCompound();
        return function.apply(compound);
    }

    private void modifyNBTCompound(Consumer<NBTTagCompound> consumer) {
        net.minecraft.world.item.ItemStack nmsCopy = CraftItemStack.asNMSCopy(item);
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

    public ItemBuilder setNBTByte(String key, byte value) {
        modifyNBTCompound((NBTTagCompound compound) -> {
            compound.putByte(key, value);
        });
        return this;
    }

    public ItemBuilder setNBTShort(String key, short value) {
        modifyNBTCompound((NBTTagCompound compound) -> {
            compound.putShort(key, value);
        });
        return this;
    }

    public ItemBuilder setNBTInt(String key, int value) {
        modifyNBTCompound((NBTTagCompound compound) -> {
            compound.putInt(key, value);
        });
        return this;
    }

    public ItemBuilder setNBTLong(String key, long value) {
        modifyNBTCompound((NBTTagCompound compound) -> {
            compound.putLong(key, value);
        });
        return this;
    }

    public ItemBuilder setNBTFloat(String key, float value) {
        modifyNBTCompound((NBTTagCompound compound) -> {
            compound.putFloat(key, value);
        });
        return this;
    }

    public ItemBuilder setNBTDouble(String key, double value) {
        modifyNBTCompound((NBTTagCompound compound) -> {
            compound.putDouble(key, value);
        });
        return this;
    }

    public ItemBuilder setNBTString(String key, String value) {
        modifyNBTCompound((NBTTagCompound compound) -> {
            compound.putString(key, value);
        });
        return this;
    }

    public ItemBuilder setNBTByteArray(String key, byte[] value) {
        modifyNBTCompound((NBTTagCompound compound) -> {
            compound.putByteArray(key, value);
        });
        return this;
    }

    public ItemBuilder setNBTIntArray(String key, int[] value) {
        modifyNBTCompound((NBTTagCompound compound) -> {
            compound.putIntArray(key, value);
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
            return compound.contains(tag) ? compound.getString(tag) : null;
        });
    }

    public Integer getNBTInt(String tag) {
        return getNBTCompound(compound -> {
            return compound.contains(tag) ? compound.getInt(tag) : null;
        });
    }

    public Long getNBTLong(String tag) {
        return getNBTCompound(compound -> compound.contains(tag) ? compound.getLong(tag) : null);
    }

    public Double getNBTDouble(String tag) {
        return getNBTCompound(compound -> compound.contains(tag) ? compound.getDouble(tag) : null);
    }

    public Boolean getNBTBoolean(String tag) {
        return getNBTCompound(compound -> compound.contains(tag) ? compound.getBoolean(tag) : null);
    }

    public NBTTagList getNBTList(String tag) {
        return getNBTCompound(compound -> {
            NBTBase base = compound.get(tag);
            return base instanceof NBTTagList ? (NBTTagList) base : new NBTTagList();
        });
    }


    public void amount(int amount) {
        item.setAmount(amount);
    }

    public void glowing() {
        item.addEnchantment(Enchantment.DURABILITY, 1);
        hideFlags();
    }

    public void hideFlags() {
        changeItemMeta(it -> it.addItemFlags(ItemFlag.values()));
    }

    public ItemStack wrap() {
        return item;
    }
}