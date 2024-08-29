package turbo.castle.gameplay.event.bandit;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BanditSpawner {

    Player player;
    List<LivingEntity> spawnedMobs;

    public BanditSpawner(Player player, List<LivingEntity> spawnedMobs) {
        this.player = player;
        this.spawnedMobs = spawnedMobs;
    }

    public void spawnMobs(Location location, int count) {
        int playerLevel = player.getLevel();
        for (int i = 0; i < count; i++) {
            Skeleton skeleton = (Skeleton) location.getWorld().spawnEntity(location, EntityType.SKELETON);
            skeleton.setCustomName("Бандит");

            ItemStack sword = new ItemStack(Material.IRON_SWORD);
            ItemMeta meta = sword.getItemMeta();
            meta.setDisplayName("Меч бандита");
            sword.setItemMeta(meta);
            skeleton.getEquipment().setItemInMainHand(sword);

            double health = 20 + playerLevel * 2;
            double damage = 5 + playerLevel * 0.5;

            AttributeInstance healthAttribute = skeleton.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            if (healthAttribute != null) {
                healthAttribute.setBaseValue(health);
            }
            skeleton.setHealth(health);

            AttributeInstance damageAttribute = skeleton.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
            if (damageAttribute != null) {
                damageAttribute.setBaseValue(damage);
            }
            skeleton.setTarget(player);
            spawnedMobs.add(skeleton);
        }
    }
}
