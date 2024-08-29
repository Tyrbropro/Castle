package turbo.castle.gameplay.event.bandit;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import turbo.castle.Castle;
import turbo.castle.util.BlockUtil;
import turbo.castle.util.MapService;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class Wagon extends AbstractBanditEvent {
    final Location sourceMin = new Location(MapService.getWorld(), -45, 64, 342);
    final Location sourceMax = new Location(MapService.getWorld(), -31, 69, 336);
    final Location destinationLocation = new Location(MapService.getWorld(), -106, 64, 402);
    Location target = new Location(MapService.getWorld(), -72, 64, 402);

    public Wagon(BlockUtil blockUtil, Player player) {
        super(blockUtil, player);
        startMovingWagon();
    }

    private void startMovingWagon() {
        Bukkit.getScheduler().runTaskTimer(Castle.getPlugin(), this::moveWagon, 0, MOVE_INTERVAL * CHECK_INTERVAL);
    }

    private void moveWagon() {
        if (target == null || target.distance(destinationLocation) < 1) {
            cleanup();
            clearMobs();
            Bukkit.getScheduler().cancelTasks(Castle.getPlugin());
            return;
        }
        spawnedMobs.removeIf(Entity::isDead);

        Location nextLocation = target.clone().add(-1, 0, 0);
        blockUtil.copyPaste(sourceMin, sourceMax, nextLocation);
        checkPlayerProximity(nextLocation, nextLocation);
        target = nextLocation;
    }

    @Override
    protected void cleanup() {
        blockUtil.iterateBlocks(target, target.clone().add(14, 5, -6), (block, blockLoc) -> block.setType(Material.AIR));
    }
}
