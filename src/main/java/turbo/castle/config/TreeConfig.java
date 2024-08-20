package turbo.castle.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.springframework.stereotype.Component;
import turbo.castle.gameplay.tree.Tree;
import turbo.castle.util.MapService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TreeConfig {
    Map<Tree, Integer> treeHits = new HashMap<>();
    Map<Block, Integer> treeSizes = new HashMap<>();
    Map<Location, Tree> blockTree = new HashMap<>();
    List<Tree> trees = new ArrayList<>();

    public TreeConfig() {
        for (int i = 0; i < 5; i++) {
            int z = i * 6;
            for (int b = 0; b < 3; b++) {
                int x = b * 6;
                trees.add(new Tree(new Location(MapService.getWorld(), 14 - x, 64, 478 - z)));
            }
        }
    }
}
