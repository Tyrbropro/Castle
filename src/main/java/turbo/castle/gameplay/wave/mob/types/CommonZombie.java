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
import turbo.castle.gameplay.wave.WaveManager;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommonZombie extends CustomMob {

    public CommonZombie(WaveManager waveManager) {
        super(
                waveManager.calculateHealth(10.0),
                waveManager.calculateDamage(2.0),
                waveManager.calculateSpeed(0.11),
                "Zombie"
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

    }
}