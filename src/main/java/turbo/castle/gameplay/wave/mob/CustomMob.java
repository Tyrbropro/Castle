package turbo.castle.gameplay.wave.mob;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static org.bukkit.attribute.Attribute.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public abstract class CustomMob {
    final double health;
    final double damage;
    final double speed;
    final String name;
    PotionEffectType effectType;
    int effectDuration;
    int effectAmplifier;

    public CustomMob(double health, double damage, double speed, String name, PotionEffectType effectType, int effectDuration, int effectAmplifier) {
        this.health = health;
        this.damage = damage;
        this.speed = speed;
        this.name = name;
        this.effectType = effectType;
        this.effectDuration = effectDuration;
        this.effectAmplifier = effectAmplifier;
    }

    public CustomMob(double health, double damage, double speed, String name) {
        this.health = health;
        this.damage = damage;
        this.speed = speed;
        this.name = name;
    }

    public abstract void onSpawn(LivingEntity entity);

    public abstract void onDeath(EntityDeathEvent event);

    public abstract void onTarget(EntityTargetEvent event);

    protected void setupEntity(LivingEntity entity) {
        entity.setCustomName(name);
        entity.setCustomNameVisible(true);
        entity.getAttribute(GENERIC_MAX_HEALTH).setBaseValue(health);
        entity.setHealth(health);
        entity.getAttribute(GENERIC_ATTACK_DAMAGE).setBaseValue(damage);
        entity.getAttribute(GENERIC_MOVEMENT_SPEED).setBaseValue(speed);
    }

    protected void applyEffect(LivingEntity target) {
        if (effectType != null && target instanceof Player) {
            target.addPotionEffect(new PotionEffect(effectType, effectDuration, effectAmplifier));
        }
    }

    public void handleHit(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof LivingEntity target) {
            applyEffect(target);
        }
    }
}