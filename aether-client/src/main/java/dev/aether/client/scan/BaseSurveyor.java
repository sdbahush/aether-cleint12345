package dev.aether.client.scan;

import dev.aether.client.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Heuristic survey of loaded terrain. It does not reveal unloaded chunks or server data. */
public final class BaseSurveyor extends Module {
    private static final int RADIUS = 32;
    private static final int VERTICAL = 16;
    private static final int POSITIONS_PER_TICK = 2600;
    private static final int CANDIDATE_THRESHOLD = 9;
    private final Map<Long, Integer> chunkScores = new HashMap<>();
    private final Map<Long, BlockPos> chunkCenters = new HashMap<>();
    private final List<ScanResult> candidates = new ArrayList<>();
    private BlockPos origin;
    private int cursor;
    private int volume;

    public BaseSurveyor() { super("Base Surveyor", "Scores suspicious block clusters in loaded terrain."); }

    @Override protected void onEnable() { begin(Minecraft.getInstance()); }

    @Override protected void onTick(Minecraft client) {
        if (client.level == null || client.player == null) return;
        if (origin == null) begin(client);
        int end = Math.min(volume, cursor + POSITIONS_PER_TICK);
        for (; cursor < end; cursor++) {
            BlockPos pos = positionAt(cursor);
            if (!client.level.hasChunkAt(pos)) continue;
            int weight = weight(client.level.getBlockState(pos));
            if (weight == 0) continue;
            int cx = pos.getX() >> 4;
            int cz = pos.getZ() >> 4;
            long key = (((long) cx) << 32) ^ (cz & 0xffffffffL);
            chunkScores.merge(key, weight, Integer::sum);
            chunkCenters.putIfAbsent(key, new BlockPos((cx << 4) + 8, pos.getY(), (cz << 4) + 8));
        }
        if (cursor >= volume) finishAndRestart(client);
    }

    private void begin(Minecraft client) {
        if (client.player == null) return;
        origin = client.player.blockPosition();
        cursor = 0;
        int side = RADIUS * 2 + 1;
        volume = side * side * (VERTICAL * 2 + 1);
        chunkScores.clear();
        chunkCenters.clear();
    }

    private void finishAndRestart(Minecraft client) {
        candidates.clear();
        chunkScores.forEach((key, score) -> {
            if (score >= CANDIDATE_THRESHOLD) {
                BlockPos pos = chunkCenters.get(key);
                candidates.add(new ScanResult(pos, "Suspicious chunk", score));
            }
        });
        candidates.sort(Comparator.comparingInt(ScanResult::score).reversed());
        begin(client);
    }

    private BlockPos positionAt(int index) {
        int side = RADIUS * 2 + 1;
        int x = index % side - RADIUS;
        int z = (index / side) % side - RADIUS;
        int y = index / (side * side) - VERTICAL;
        return origin.offset(x, y, z);
    }

    private int weight(BlockState state) {
        var b = state.getBlock();
        if (b == Blocks.CHEST || b == Blocks.TRAPPED_CHEST || b == Blocks.BARREL) return 3;
        if (b == Blocks.HOPPER || b == Blocks.ENDER_CHEST || b == Blocks.SHULKER_BOX) return 3;
        if (b == Blocks.SPAWNER) return 4;
        if (b == Blocks.OBSIDIAN || b == Blocks.CRYING_OBSIDIAN) return 1;
        if (b == Blocks.FURNACE || b == Blocks.BLAST_FURNACE || b == Blocks.SMOKER) return 1;
        if (b == Blocks.CRAFTING_TABLE || b == Blocks.ANVIL || b == Blocks.BREWING_STAND) return 1;
        return 0;
    }

    public List<ScanResult> candidates() { return Collections.unmodifiableList(candidates); }
    public int scannedPositions() { return cursor; }
    public int totalPositions() { return volume; }
}
