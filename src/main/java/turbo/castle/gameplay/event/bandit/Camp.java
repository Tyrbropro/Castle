package turbo.castle.gameplay.event.bandit;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import turbo.castle.Castle;
import turbo.castle.util.BlockUtil;
import turbo.castle.util.MapService;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Camp extends AbstractBanditEvent {
    Location sourceMin = new Location(MapService.getWorld(), -40, 64, 319);
    Location sourceMax = new Location(MapService.getWorld(), -26, 68, 311);
    Location target = new Location(MapService.getWorld(), -40, 64, 434);

    public Camp(BlockUtil blockUtil, Player player) {
        super(blockUtil, player);
        spawnCamp();
    }

    private void spawnCamp() {
        blockUtil.copyPaste(sourceMin, sourceMax, target);
        Bukkit.getScheduler().runTaskTimer(Castle.getPlugin(), this::campInteract, 0, MOVE_INTERVAL * CHECK_INTERVAL);
    }

    private void campInteract() {
        if (isCompleted) {
            Bukkit.getScheduler().cancelTasks(Castle.getPlugin());
            return;
        }
        checkPlayerProximity(target, new Location(MapService.getWorld(), -36.5, 64, 431.5));
    }

    @Override
    protected void cleanup() {
        blockUtil.iterateBlocks(target, target.clone().add(14, 5, -8), (block, blockLoc) -> block.setType(Material.AIR));
    }
}
