package turbo.castle.gameplay.tree;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import turbo.castle.config.TreeConfig;
import turbo.castle.currency.wood.repository.WoodRepositoryImpl;
import turbo.castle.data.PlayerData;
import turbo.castle.util.BlockUtil;
import turbo.castle.util.MapService;

import java.util.*;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TreeFelling {
    TreeConfig treeConfig;
    BlockUtil blockUtil;

    @Autowired
    public TreeFelling(TreeConfig treeConfig, BlockUtil blockUtil) {
        this.treeConfig = treeConfig;
        this.blockUtil = blockUtil;
    }

    public void felling(Player player, Block block) {
        UUID uuid = player.getUniqueId();
        PlayerData data = PlayerData.getUsers().get(uuid);
        WoodRepositoryImpl woodRepository = data.getWoodRepository();

        Tree tree = treeConfig.getBlockTree().get(block.getLocation());
        if (tree == null) return;
        tree.setBeingBroken(true);

        int treeSize = blockUtil.countTreeBlocks(block);
        treeConfig.getTreeSizes().putIfAbsent(block, treeSize);

        int hits = treeConfig.getTreeHits().getOrDefault(tree, 0) + 1;
        treeConfig.getTreeHits().put(tree, hits);

        if (hits >= treeSize) {
            removeTree(tree, block);
            treeConfig.getTreeHits().remove(tree);

            woodRepository.addWood(treeSize * 50);
            player.sendMessage("Вы разрушили дерево!");
        } else {
            player.sendMessage("Вы ударили по дереву " + hits + " раз(а). Всего блоков: " + treeSize);
        }
    }

    private void removeTree(Tree tree, Block block) {
        tree.setGrowthLevel(0);
        tree.setBeingBroken(false);
        treeConfig.getTreeSizes().remove(block);

        Location loc = tree.getLocation();
        int minX = loc.getBlockX() - 4;
        int maxX = loc.getBlockX();
        int minY = loc.getBlockY();
        int maxY = loc.getBlockY() + 5;
        int minZ = loc.getBlockZ() - 4;
        int maxZ = loc.getBlockZ();

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    MapService.getWorld().getBlockAt(x, y, z).setType(Material.AIR);
                }
            }
        }
    }
}
