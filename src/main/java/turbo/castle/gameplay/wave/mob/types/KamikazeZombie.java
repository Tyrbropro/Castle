package turbo.castle.gameplay.wave.mob.types;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import turbo.castle.Castle;
import turbo.castle.gameplay.wave.mob.CustomMob;
import turbo.castle.gameplay.wave.mob.WaveManager;

public class KamikazeZombie extends CustomMob {

    public KamikazeZombie(WaveManager waveManager) {
        super(
                waveManager.calculateHealth(10.0),
                waveManager.calculateDamage(1.0),
                waveManager.calculateSpeed(0.35),
                "Kamikaze Zombie"
        );
    }

    @Override
    public void onSpawn(LivingEntity entity) {
        setupEntity(entity);

        ItemStack tntHelmet = new ItemStack(Material.TNT);
        entity.getEquipment().setHelmet(tntHelmet);
    }

    @Override
    public void handleHit(EntityDamageByEntityEvent event) {
        explode(event.getEntity().getLocation());
        event.getEntity().remove();
    }

    @Override
    public void onDeath(EntityDeathEvent event) {
        explode(event.getEntity().getLocation());
    }

    @Override
    public void onTarget(EntityTargetEvent event) {
        if (event.getTarget() instanceof Player targetPlayer) {
            LivingEntity entity = (LivingEntity) event.getEntity();
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (entity.isDead() || !targetPlayer.isOnline()) {
                        this.cancel();
                        return;
                    }

                    double distance = entity.getLocation().distance(targetPlayer.getLocation());
                    if (distance <= 1.5) {
                        explode(entity.getLocation());
                        entity.remove();
                        this.cancel();
                    }
                }
            }.runTaskTimer(Castle.getPlugin(), 0L, 1L);
        }
    }

    private void explode(Location location) {
        location.getWorld().createExplosion(location.getX(), location.getY(), location.getZ(), (float) getDamage(), false, false);
    }
}
