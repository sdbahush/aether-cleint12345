package dev.aether.client.scan;

import dev.aether.client.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Finds storage-related blocks in chunks already present on the client. */
public final class StashRadar extends Module {
    private static final int RADIUS = 24;
    private static final int VERTICAL = 12;
    private static final int POSITIONS_PER_TICK = 1800;
    private final List<ScanResult> results = new ArrayList<>();
    private final Set<Long> seen = new HashSet<>();
    private BlockPos origin;
    private int cursor;
    private int volume;

    public StashRadar() { super("Stash Radar", "Locates visible storage blocks in loaded chunks."); }

    @Override protected void onEnable() { reset(Minecraft.getInstance()); }

    @Override protected void onTick(Minecraft client) {
        if (client.level == null || client.player == null) return;
        if (origin == null) reset(client);
        int end = Math.min(volume, cursor + POSITIONS_PER_TICK);
        for (; cursor < end; cursor++) {
            BlockPos pos = positionAt(cursor);
            if (!client.level.hasChunkAt(pos)) continue;
            BlockState state = client.level.getBlockState(pos);
            String kind = classify(state);
            if (kind == null) continue;
            long key = pos.asLong();
            if (seen.add(key)) results.add(new ScanResult(pos.immutable(), kind, 1));
        }
        if (cursor >= volume) reset(client);
    }

    private void reset(Minecraft client) {
        if (client.player == null) return;
        origin = client.player.blockPosition();
        cursor = 0;
        int side = RADIUS * 2 + 1;
        volume = side * side * (VERTICAL * 2 + 1);
    }

    private BlockPos positionAt(int index) {
        int side = RADIUS * 2 + 1;
        int height = VERTICAL * 2 + 1;
        int x = index % side - RADIUS;
        int z = (index / side) % side - RADIUS;
        int y = index / (side * side) - VERTICAL;
        return origin.offset(x, y, z);
    }

    private String classify(BlockState state) {
        var block = state.getBlock();
        if (block == Blocks.CHEST) return "Chest";
        if (block == Blocks.TRAPPED_CHEST) return "Trapped chest";
        if (block == Blocks.BARREL) return "Barrel";
        if (block == Blocks.HOPPER) return "Hopper";
        if (block == Blocks.ENDER_CHEST) return "Ender chest";
        if (block == Blocks.SHULKER_BOX) return "Shulker box";
        if (block == Blocks.FURNACE || block == Blocks.BLAST_FURNACE || block == Blocks.SMOKER) return "Furnace";
        return null;
    }

    public List<ScanResult> results() { return Collections.unmodifiableList(results); }
    public int scannedPositions() { return cursor; }
    public int totalPositions() { return volume; }
    public void clear() { results.clear(); seen.clear(); }
}
