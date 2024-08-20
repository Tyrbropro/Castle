package turbo.castle.gameplay.wave.mob.types;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.springframework.stereotype.Component;
import turbo.castle.gameplay.wave.mob.CustomMob;
import turbo.castle.gameplay.wave.mob.WaveManager;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FireZombie extends CustomMob {

    public FireZombie(WaveManager waveManager) {
        super(
                waveManager.calculateHealth(20.0),
                waveManager.calculateDamage(5.0),
                waveManager.calculateSpeed(0.23),
                "FireZombie"
        );
    }

    @Override
    public void onSpawn(LivingEntity entity) {
        setupEntity(entity);
    }
    @Override
    public void onDeath(EntityDeathEvent event) {
    }

    @Override
    public void onTarget(EntityTargetEvent event) {

    }

    public void aggroOnPlayer(Creature entity, Player target) {
        entity.setTarget(target);
    }

    @Override
    public void handleHit(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player player) {
            player.setFireTicks(100);
        }
    }
}
