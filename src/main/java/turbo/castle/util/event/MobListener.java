package turbo.castle.util.event;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import turbo.castle.gameplay.wave.SpawnWave;

@Component
public class MobListener implements Listener {
    final SpawnWave spawnWave;

    @Autowired
    public MobListener(SpawnWave spawnWave) {
        this.spawnWave = spawnWave;
    }

    @EventHandler
    public void onEntityCombust(EntityCombustEvent event) {
        if (event.getEntity() instanceof LivingEntity) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        event.getDrops().clear();

        if (spawnWave.getCustomMobFactory().getCustomMobs().containsKey(event.getEntity())) {
            spawnWave.onMobDeath();
        }
    }
}
