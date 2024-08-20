package turbo.castle.gameplay.wave.mob.types;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.potion.PotionEffectType;
import org.springframework.stereotype.Component;
import turbo.castle.gameplay.wave.mob.CustomMob;
import turbo.castle.gameplay.wave.mob.WaveManager;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SlowZombie extends CustomMob {

    public SlowZombie(WaveManager waveManager) {
        super(
                waveManager.calculateHealth(20.0),
                waveManager.calculateDamage(5.0),
                waveManager.calculateSpeed(0.23),
                "SlowZombie",
                PotionEffectType.SLOW,
                100,
                1
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
        super.handleHit(event);
    }
}
