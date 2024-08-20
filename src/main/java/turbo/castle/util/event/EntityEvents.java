package turbo.castle.util.event;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.springframework.stereotype.Component;
import turbo.castle.gameplay.wave.mob.CustomMob;
import turbo.castle.gameplay.wave.mob.CustomMobFactory;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EntityEvents implements Listener {
    CustomMobFactory customMobFactory;

    public EntityEvents(CustomMobFactory customMobFactory) {
        this.customMobFactory = customMobFactory;
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().clear();
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof LivingEntity && !(event.getEntity() instanceof Player)) {
            if (!(event.getDamager() instanceof Player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityTarget(EntityTargetEvent event) {
        if (event.getEntity() instanceof LivingEntity entity) {
            CustomMob customMob = customMobFactory.getCustomMob(entity);
            if (customMob != null) {
                customMob.onTarget(event);
            }
        }
    }
}
