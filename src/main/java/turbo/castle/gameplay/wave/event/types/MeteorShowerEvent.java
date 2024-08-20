package turbo.castle.gameplay.wave.event.types;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.springframework.stereotype.Component;
import turbo.castle.Castle;
import turbo.castle.gameplay.wave.event.WaveEvent;

import java.util.HashMap;
import java.util.Map;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MeteorShowerEvent extends WaveEvent {

    Map<Location, Material> originalBlocks = new HashMap<>();

    public MeteorShowerEvent(Player player) {
        super(player);
    }

    @Override
    public void trigger() {
        sendEventMessage("Начался метеоритный дождь! Берегись!");

        if (player != null && player.isOnline()) {
            Location playerLocation = player.getLocation();

            replaceBlocks(playerLocation, Material.WOOL, (byte) 14);
            new BukkitRunnable() {
                @Override
                public void run() {
                    spawnMeteor(playerLocation);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            restoreBlocks();
                        }
                    }.runTaskLater(Castle.getPlugin(), 100L);
                }
            }.runTaskLater(Castle.getPlugin(), 20L);
        }
    }

    private void replaceBlocks(Location center, Material newMaterial, byte data) {
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                Location loc = center.clone().add(x, -1, z);
                Block block = loc.getBlock();
                while (block.getType() == Material.AIR) {
                    block = loc.add(0, -1, 0).getBlock();
                }
                Block finalBlock = block;
                originalBlocks.computeIfAbsent(loc, key -> finalBlock.getType());
                block.setType(newMaterial);
                block.setData(data);
            }
        }
    }

    private void spawnMeteor(Location location) {
        location.setYaw(90);
        location.setPitch(90);

        Location meteorLocation = location.clone().add(0, 20, 0);
        Fireball fireball = (Fireball) player.getWorld().spawnEntity(meteorLocation, EntityType.FIREBALL);

        Vector direction = location.toVector().subtract(meteorLocation.toVector()).normalize();

        fireball.setVelocity(direction.multiply(2));
        fireball.setYield(7);
        fireball.setIsIncendiary(false);
    }

    private void restoreBlocks() {
        for (Map.Entry<Location, Material> entry : originalBlocks.entrySet()) {
            Block block = entry.getKey().getBlock();
            block.setType(entry.getValue());
        }
    }
}
