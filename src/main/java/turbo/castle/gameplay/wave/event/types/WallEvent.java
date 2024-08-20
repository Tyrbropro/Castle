package turbo.castle.gameplay.wave.event.types;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.springframework.stereotype.Component;
import turbo.castle.Castle;
import turbo.castle.gameplay.wave.event.WaveEvent;
import turbo.castle.util.BlockUtil;
import turbo.castle.util.MapService;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WallEvent extends WaveEvent {
    BlockUtil blockUtil = new BlockUtil();
    Location sourceMin = new Location(MapService.getWorld(), -157, 63, 461);
    Location sourceMax = new Location(MapService.getWorld(), -126, 63, 430);
    List<Location> blockLocations = findAreaBlock(sourceMin, sourceMax);

    public WallEvent(Player player) {
        super(player);
    }

    @Override
    public void trigger() {
        for (int i = 0; i < 5; i++) {
            if (!blockLocations.isEmpty()) {
                Random random = new Random();
                Location randomLocation = blockLocations.get(random.nextInt(blockLocations.size()));

                buildWall(randomLocation);

                Bukkit.getScheduler().runTaskLater(Castle.getPlugin(), () -> removeWall(randomLocation), 20L * 60);
            }
        }
    }

    private List<Location> findAreaBlock(Location sourceMin, Location sourceMax) {
        List<Location> blockLocations = new ArrayList<>();

        blockUtil.iterateBlocks(sourceMin, sourceMax, (block, blockLoc) -> {
            if (block.getType() == Material.NETHERRACK) {
                blockLocations.add(block.getLocation());
            }
        });

        return blockLocations;
    }

    private void buildWall(Location baseLocation) {
        World world = baseLocation.getWorld();
        for (int i = 1; i < 10; i++) {
            Block block = world.getBlockAt(baseLocation.getBlockX(), baseLocation.getBlockY() + i, baseLocation.getBlockZ());
            block.setType(Material.STONE);
        }
    }

    private void removeWall(Location baseLocation) {
        World world = baseLocation.getWorld();
        for (int i = 1; i < 10; i++) {
            Block block = world.getBlockAt(baseLocation.getBlockX(), baseLocation.getBlockY() + i, baseLocation.getBlockZ());
            block.setType(Material.AIR);
        }
    }
}
