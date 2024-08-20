package turbo.castle.gameplay.tree;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import turbo.castle.Castle;
import turbo.castle.config.TreeConfig;
import turbo.castle.util.BlockUtil;
import turbo.castle.util.MapService;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TreeGrowth {
    TreeConfig treeConfig;
    BlockUtil blockUtil;

    @Autowired
    public TreeGrowth(TreeConfig treeConfig, BlockUtil blockUtil) {
        this.treeConfig = treeConfig;
        this.blockUtil = blockUtil;
    }

    public void grow() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Tree tree : treeConfig.getTrees()) {
                    if (tree.isBeingBroken()) return;
                    int growLvl = tree.getGrowthLevel();
                    int x = growLvl * 6;

                    Location sourceMin = new Location(MapService.getWorld(), -8 + x, 64, 490);
                    Location sourceMax = new Location(MapService.getWorld(), -12 + x, 69, 486);

                    if (growLvl < 3) tree.setGrowthLevel(growLvl + 1);
                    cloneTree(sourceMin, sourceMax, tree.getLocation(), tree);
                }
            }
        }.runTaskTimer(Castle.getPlugin(), 0L, 10 * 20L);
    }

    private void cloneTree(Location sourceMin, Location sourceMax, Location target, Tree tree) {
        blockUtil.iterateBlocks(sourceMin, sourceMax, (sourceBlock, blockLoc) -> {
            Block targetBlock = target.getWorld().getBlockAt(
                    blockLoc.getBlockX() - sourceMin.getBlockX() + target.getBlockX(),
                    blockLoc.getBlockY() - sourceMin.getBlockY() + target.getBlockY(),
                    blockLoc.getBlockZ() - sourceMin.getBlockZ() + target.getBlockZ()
            );
            targetBlock.setType(sourceBlock.getType());
            if (sourceBlock.getType() == Material.LOG) {
                treeConfig.getBlockTree().put(targetBlock.getLocation(), tree);
            }
        });
    }
}
