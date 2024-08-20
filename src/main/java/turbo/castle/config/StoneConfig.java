package turbo.castle.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.springframework.stereotype.Component;
import turbo.castle.gameplay.stone.Stone;
import turbo.castle.util.MapService;

import java.util.*;

@Getter
@Setter
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StoneConfig {
    Map<Stone, Integer> stoneHits = new HashMap<>();
    Map<Block, Integer> stoneSizes = new HashMap<>();
    Map<Location, Stone> blockStone = new HashMap<>();
    Set<Location> minedStone = new HashSet<>();
    Set<Stone> stones = new HashSet<>();

    public StoneConfig() {
        stones.add(new Stone(new Location(MapService.getWorld(), 0, 69, 378)));
        stones.add(new Stone(new Location(MapService.getWorld(), 6, 69, 380)));
        stones.add(new Stone(new Location(MapService.getWorld(), 15, 69, 379)));
        stones.add(new Stone(new Location(MapService.getWorld(), 13, 69, 387)));
        stones.add(new Stone(new Location(MapService.getWorld(), 14, 69, 394)));
        stones.add(new Stone(new Location(MapService.getWorld(), 5, 69, 391)));
        stones.add(new Stone(new Location(MapService.getWorld(), -1, 69, 392)));
    }
}
