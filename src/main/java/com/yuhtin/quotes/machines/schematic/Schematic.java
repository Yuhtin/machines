package com.yuhtin.quotes.machines.schematic;

import com.boydti.fawe.FaweAPI;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.yuhtin.quotes.machines.MachinesPlugin;
import lombok.Getter;
import me.lucko.helper.Schedulers;
import me.lucko.helper.bucket.Bucket;
import me.lucko.helper.bucket.BucketPartition;
import me.lucko.helper.bucket.factory.BucketFactory;
import me.lucko.helper.bucket.partitioning.PartitioningStrategies;
import me.lucko.helper.scheduler.Task;
import me.lucko.helper.scheduler.builder.TaskBuilder;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * A utility class that previews and pastes schematics block-by-block with asynchronous support.
 * <br></br>
 *
 * @author SamB440 - Schematic previews, centering and pasting block-by-block, class itself
 * @author brainsynder - 1.13+ Palette Schematic Reader
 * @author Math0424 - Rotation calculations
 * @author Jojodmo - Legacy (< 1.12) Schematic Reader
 * @version 2.0.6
 */
public class Schematic {

    private final Clipboard clipboard;


    public Schematic(String schematicFileName) throws IllegalArgumentException, IOException {
        File file = new File("plugins/WorldEdit/schematics/" + schematicFileName);
        if (!file.exists()) throw new IllegalArgumentException("Schematic file not found in plugins/WorldEdit/schematics/" + schematicFileName);

        clipboard = FaweAPI.load(file).getClipboard();
        if (clipboard == null) throw new IllegalArgumentException("Schematic file is not a valid schematic");
    }

    public void cleanUpSchematic(Location loc, double yaw) {
        ClipboardHolder clipboardHolder = new ClipboardHolder(clipboard, FaweAPI.getWorld(loc.getWorld().getName()).getWorldData());
        clipboardHolder.setTransform(new AffineTransform().rotateY(yaw));

        Clipboard transformedClipboard = clipboardHolder.getClipboard();

        Bucket<Location> bucket = BucketFactory.newHashSetBucket(60, PartitioningStrategies.random());
        Vector minimumPoint = transformedClipboard.getMinimumPoint();
        Vector maximumPoint = transformedClipboard.getMaximumPoint();
        int minX = minimumPoint.getBlockX();
        int maxX = maximumPoint.getBlockX();
        int minY = minimumPoint.getBlockY();
        int maxY = maximumPoint.getBlockY();
        int minZ = minimumPoint.getBlockZ();
        int maxZ = maximumPoint.getBlockZ();

        final int width = transformedClipboard.getRegion().getWidth();
        final int height = transformedClipboard.getRegion().getHeight();
        final int length = transformedClipboard.getRegion().getLength();
        final int widthCentre = width / 2;
        final int heightCentre = height / 2;
        final int lengthCentre = length / 2;

        int minBlockY = loc.getWorld().getMaxHeight();
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    BaseBlock block = clipboard.getLazyBlock(x, y, z);
                    if (block.getType() <= 0) continue;

                    double offsetX = Math.abs(maxX - x);
                    double offsetY = Math.abs(maxY - y);
                    double offsetZ = Math.abs(maxZ - z);

                    Location offsetLoc = loc.clone().subtract(offsetX - widthCentre, offsetY - heightCentre, offsetZ - lengthCentre);
                    if (offsetLoc.getBlockY() < minBlockY) minBlockY = offsetLoc.getBlockY();

                    bucket.add(offsetLoc);
                }
            }
        }

        Scheduler scheduler = new Scheduler();
        scheduler.setTask(Schedulers.sync().runRepeating(() -> {
            BucketPartition<Location> next = bucket.asCycle().next();
            if (next.isEmpty()) {
                scheduler.cancel();
                return;
            }

            next.forEach(location -> location.getBlock().setType(Material.AIR));
        }, 1, 20).getBukkitId());
    }

    /**
     * Pastes a schematic, with a specified time
     *
     * @param paster player pasting
     * @param time   time in ticks to paste blocks
     * @return collection of locations where schematic blocks will be pasted, null if schematic locations will replace blocks
     */
    @Nullable
    public Collection<Location> pasteSchematic(final Location loc, final Player paster, Runnable runnable, final int time, final Options... option) {
        final Map<Location, BaseBlock> pasteBlocks = new LinkedHashMap<>();
        final List<Options> options = Arrays.asList(option);
        try {
            final Data tracker = new Data();

            double yaw = roundHalfUp((int) paster.getEyeLocation().getYaw());

            ClipboardHolder clipboardHolder = new ClipboardHolder(clipboard, FaweAPI.getWorld(loc.getWorld().getName()).getWorldData());
            clipboardHolder.setTransform(new AffineTransform().rotateY(yaw));

            Clipboard transformedClipboard = clipboardHolder.getClipboard();

            Vector minimumPoint = transformedClipboard.getMinimumPoint();
            Vector maximumPoint = transformedClipboard.getMaximumPoint();
            int minX = minimumPoint.getBlockX();
            int maxX = maximumPoint.getBlockX();
            int minY = minimumPoint.getBlockY();
            int maxY = maximumPoint.getBlockY();
            int minZ = minimumPoint.getBlockZ();
            int maxZ = maximumPoint.getBlockZ();

            final int width = transformedClipboard.getRegion().getWidth();
            final int height = transformedClipboard.getRegion().getHeight();
            final int length = transformedClipboard.getRegion().getLength();
            final int widthCentre = width / 2;
            final int heightCentre = height / 2;
            final int lengthCentre = length / 2;

            int minBlockY = loc.getWorld().getMaxHeight();
            for (int x = minX; x <= maxX; x++) {
                for (int y = minY; y <= maxY; y++) {
                    for (int z = minZ; z <= maxZ; z++) {
                        BaseBlock block = clipboard.getLazyBlock(x, y, z);

                        if (block.getType() <= 0) continue;

                        final double offsetX = Math.abs(maxX - x);
                        final double offsetY = Math.abs(maxY - y);
                        final double offsetZ = Math.abs(maxZ - z);

                        final Location offsetLoc = loc.clone().subtract(offsetX - widthCentre, offsetY - heightCentre, offsetZ - lengthCentre);
                        if (offsetLoc.getBlockY() < minBlockY) minBlockY = offsetLoc.getBlockY();
                        pasteBlocks.put(offsetLoc, block);
                    }
                }
            }

            /*
             * Verify location of pasting
             */
            boolean validated = true;
            for (Location validate : pasteBlocks.keySet()) {
                BaseBlock baseBlock = pasteBlocks.get(validate);
                boolean isWater = validate.clone().subtract(0, 1, 0).getBlock().getType() == Material.WATER;
                boolean isAir = minBlockY == validate.getBlockY() && validate.clone().subtract(0, 1, 0).getBlock().getType() == Material.AIR;
                boolean isSolid = validate.getBlock().getType().isSolid();
                boolean isTransparent = options.contains(Options.IGNORE_TRANSPARENT) && validate.getBlock().getType().isTransparent() && validate.getBlock().getType() != Material.AIR;

                if (!options.contains(Options.PLACE_ANYWHERE) && (isWater || isAir || isSolid) && !isTransparent) {
                    if (options.contains(Options.USE_GAME_MARKER)) {
                        PacketSender.sendBlockHighlight(paster, validate, Color.RED, 51);
                    } else {
                        paster.sendBlockChange(validate, Material.STAINED_GLASS, (byte) 14);
                    }

                    validated = false;
                } else {
                    if (options.contains(Options.USE_FAKE_BLOCKS)) {
                        paster.sendBlockChange(validate, baseBlock.getId(), (byte) baseBlock.getData());
                    } else if (options.contains(Options.USE_GAME_MARKER)) {
                        PacketSender.sendBlockHighlight(paster, validate, Color.GREEN, 51);
                    } else {
                        paster.sendBlockChange(validate, Material.STAINED_GLASS, (byte) 5);
                    }
                }

                if (!options.contains(Options.PREVIEW) && !options.contains(Options.USE_GAME_MARKER)) {
                    Schedulers.async().runLater(() -> {
                        if (validate.getBlock().getType() == Material.AIR)
                            paster.sendBlockChange(validate.getBlock().getLocation(), Material.AIR, (byte) 0);
                    }, 60);
                }
            }

            if (options.contains(Options.PREVIEW)) return new ArrayList<>();
            if (!validated) return null;

            if (options.contains(Options.REALISTIC)) {
                Map<Location, BaseBlock> sorted
                        = pasteBlocks.entrySet()
                        .stream()
                        .sorted(Comparator.comparingInt(i -> i.getKey().getBlockY()))
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (e1, e2) -> e1, LinkedHashMap::new));
                pasteBlocks.clear();
                pasteBlocks.putAll(sorted);
            }

            // Start pasting each block every tick
            final AtomicReference<Task> task = new AtomicReference<>();

            tracker.trackCurrentBlock = 0;

            Runnable pasteTask = () -> {
                Location key = (Location) pasteBlocks.keySet().toArray()[tracker.trackCurrentBlock];
                BaseBlock baseBlock = pasteBlocks.get(key);
                Block block = key.getBlock();
                block.setType(Material.getMaterial(baseBlock.getId()), false);
                block.setData((byte) baseBlock.getData());

                block.getState().update(true, false);

                Location location = block.getLocation();
                World world = location.getWorld();
                world.playEffect(location, Effect.CLOUD, 6);
                world.playEffect(location, Effect.STEP_SOUND, block.getType());
                world.playEffect(location.clone().add(.5, 0, .5), Effect.HAPPY_VILLAGER, 2);

                tracker.trackCurrentBlock++;

                if (tracker.trackCurrentBlock >= pasteBlocks.size()) {
                    runnable.run();
                    task.get().stop();
                    tracker.trackCurrentBlock = 0;
                }
            };

            task.set(TaskBuilder.newBuilder().sync().every(time).run(pasteTask));
            return pasteBlocks.keySet();
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private int roundHalfUp(int value) {
        return (value + (value < 0 ? 90 / -2 : 90 / 2)) / 90 * 90;
    }

    /**
     * Pastes a schematic, with the time defaulting to 1 block per second
     *
     * @param location location to paste from
     * @param paster   player pasting
     * @param options  options to apply to this paste
     * @return list of locations where schematic blocks will be pasted, null if schematic locations will replace blocks
     */
    public Collection<Location> pasteSchematic(final Location location, final Player paster, Runnable runnable, final Options... options) {
        return pasteSchematic(location, paster, runnable, 20, options);
    }

    /**
     * Hacky method to avoid "final".
     */
    protected static class Data {
        int trackCurrentBlock;
    }

    /**
     * An enum of options to apply whilst previewing/pasting a schematic.
     */
    public enum Options {
        /**
         * Previews schematic
         */
        PREVIEW,
        /**
         * A realistic building method. Builds from the ground up, instead of in the default slices.
         */
        REALISTIC,
        /**
         * Bypasses the verification check and allows placing anywhere.
         */
        PLACE_ANYWHERE,
        /**
         * Ignores transparent blocks in the placement check
         */
        IGNORE_TRANSPARENT,
        /**
         * Instead of blocks, uses the game marker API by Mojang to show valid build areas.
         * <hr></hr>
         * Please note that in 1.17 Mojang unintentionally broke the RGB functionality, and anything that is not of the
         * {@link Color#GREEN} type will display as black. This can be fixed using a ResourcePack, described here:
         * <a href="https://bugs.mojang.com/browse/MC-234030">https://bugs.mojang.com/browse/MC-234030</a>
         */
        USE_GAME_MARKER,
        /**
         * Instead of game markers or glass blocks,
         * uses the actual block types of the schematic to show valid build areas.
         * <hr></hr>
         * Note that this will still use red glass for invalid build areas.
         * You can optionally provide the {@link Options#USE_GAME_MARKER} option to replace the red glass.
         */
        USE_FAKE_BLOCKS
    }
}