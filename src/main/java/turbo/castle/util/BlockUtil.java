package turbo.castle.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;

@Component
public class BlockUtil {
    public void iterateBlocks(Location minLoc, Location maxLoc, BiConsumer<Block, Location> blockAction) {
        int minX = Math.min(minLoc.getBlockX(), maxLoc.getBlockX());
        int maxX = Math.max(minLoc.getBlockX(), maxLoc.getBlockX());
        int minY = Math.min(minLoc.getBlockY(), maxLoc.getBlockY());
        int maxY = Math.max(minLoc.getBlockY(), maxLoc.getBlockY());
        int minZ = Math.min(minLoc.getBlockZ(), maxLoc.getBlockZ());
        int maxZ = Math.max(minLoc.getBlockZ(), maxLoc.getBlockZ());

        World world = MapService.getWorld();

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = world.getBlockAt(x, y, z);
                    Location blockLoc = new Location(world, x, y, z);
                    blockAction.accept(block, blockLoc);
                }
            }
        }
    }

    public void copyPaste(Location sourceMin, Location sourceMax, Location target) {
        iterateBlocks(sourceMin, sourceMax, (sourceBlock, blockLoc) -> {
            Block targetBlock = target.getWorld().getBlockAt(
                    blockLoc.getBlockX() - sourceMin.getBlockX() + target.getBlockX(),
                    blockLoc.getBlockY() - sourceMin.getBlockY() + target.getBlockY(),
                    blockLoc.getBlockZ() - sourceMin.getBlockZ() + target.getBlockZ()
            );
            targetBlock.setType(sourceBlock.getType());
        });
    }

    public int countTreeBlocks(Block startingBlock) {
        int count = 0;
        Material logType = startingBlock.getType();

        Set<Block> visited = new HashSet<>();
        Deque<Block> stack = new ArrayDeque<>();
        stack.push(startingBlock);

        while (!stack.isEmpty()) {
            Block block = stack.pop();
            if (visited.contains(block) || block.getType() != logType) continue;
            visited.add(block);
            count++;

            for (BlockFace face : BlockFace.values()) {
                Block adjacent = block.getRelative(face);
                if (!visited.contains(adjacent) && adjacent.getType() == logType) {
                    stack.push(adjacent);
                }
            }
        }
        return count;
    }
}
