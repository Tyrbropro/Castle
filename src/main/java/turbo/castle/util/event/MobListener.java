package turbo.castle.util.event;


import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.springframework.stereotype.Component;
import turbo.castle.gameplay.wave.mob.SpawnWave;

@Component
public class MobListener implements Listener {
    SpawnWave spawnWave;

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
        if (event.getEntity() != null) {
            event.getDrops().clear();
        }
        if (spawnWave.getCustomMobFactory().getCustomMobs().containsKey(event.getEntity())) {
            spawnWave.onMobDeath();
        }
    }
}
