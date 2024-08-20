package turbo.castle.gameplay.wave.mob;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.springframework.stereotype.Component;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MobHitListener implements Listener {

    CustomMobFactory customMobFactory;

    public MobHitListener(CustomMobFactory customMobFactory) {
        this.customMobFactory = customMobFactory;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof LivingEntity entity) {
            CustomMob customMob = customMobFactory.getCustomMob(entity);
            if (customMob != null) {
                customMob.handleHit(event);
            }
        }
    }
}