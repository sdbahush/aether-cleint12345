package dev.aether.client.scan;

import net.minecraft.core.BlockPos;

public record ScanResult(BlockPos position, String label, int score) {}
