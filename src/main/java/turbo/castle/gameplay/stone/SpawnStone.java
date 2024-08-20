package turbo.castle.gameplay.stone;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import turbo.castle.Castle;
import turbo.castle.config.StoneConfig;
import turbo.castle.util.BlockUtil;
import turbo.castle.util.MapService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SpawnStone {
    StoneConfig stoneConfig;
    BlockUtil blockUtil;
    Random random = new Random();

    @Autowired
    public SpawnStone(StoneConfig stoneConfig, BlockUtil blockUtil) {
        this.stoneConfig = stoneConfig;
        this.blockUtil = blockUtil;
    }

    public void spawn() {
        clearArea();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (stoneConfig.getMinedStone().size() == 7) return;

                Stone stone = getRandomUnminedStone();
                if (stone == null) return;

                stoneConfig.getMinedStone().add(stone.location());

                int ran = random.nextInt(3);
                int x = (ran * 6) - 1;

                Location sourceMin = new Location(MapService.getWorld(), x, 64, 368);
                Location sourceMax = new Location(MapService.getWorld(), -4 + x, 65, 364);

                blockUtil.copyPaste(sourceMin, sourceMax, stone.location());
                gravelToCobblestone(stone.location(), ran, stone);
            }
        }.runTaskTimer(Castle.getPlugin(), 0L, 10 * 20L);
    }

    private Stone getRandomUnminedStone() {
        List<Stone> stones = new ArrayList<>(stoneConfig.getStones());
        Collections.shuffle(stones);
        for (Stone stone : stones) {
            if (!checkMined(stone.location())) {
                return stone;
            }
        }
        return null;
    }

    private void gravelToCobblestone(Location loc, int count, Stone stone) {
        final int finalCount = count * 2 + 4;
        final Location sourceMin = loc.clone().add(0, -5, 0);
        final Location sourceMax = loc.clone().add(-4, -4, -4);

        new BukkitRunnable() {
            @Override
            public void run() {
                final int[] blockGravel = {0};
                blockUtil.iterateBlocks(sourceMin, sourceMax, (block, blockLoc) -> {
                    if (block.getType() == Material.GRAVEL) {
                        blockGravel[0]++;
                    }
                });

                if (blockGravel[0] == finalCount) {
                    blockUtil.iterateBlocks(sourceMin, sourceMax, (block, blockLoc) -> {
                        if (block.getType() == Material.GRAVEL) {
                            block.setType(Material.COBBLESTONE);
                            stoneConfig.getBlockStone().put(blockLoc, stone);
                        }
                    });
                    this.cancel();
                }
            }
        }.runTaskTimer(Castle.getPlugin(), 0L, 1);
    }

    private boolean checkMined(Location target) {
        for (Location loc : stoneConfig.getMinedStone()) {
            if (loc.equals(target)) {
                return true;
            }
        }
        return false;
    }

    private void clearArea() {
        final Location sourceMin = new Location(MapService.getWorld(), -7, 64, 372);
        final Location sourceMax = new Location(MapService.getWorld(), 17, 68, 396);
        blockUtil.iterateBlocks(sourceMin, sourceMax, (block, blockLoc) -> block.setType(Material.AIR));
    }
}
