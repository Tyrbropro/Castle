package turbo.castle.gameplay.wave.mob;

import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CustomMobFactory {
    @Getter
    private final Map<Entity, CustomMob> customMobs = new HashMap<>();

    public void registerCustomMob(LivingEntity entity, CustomMob customMob) {
        customMobs.put(entity, customMob);
    }

    public CustomMob getCustomMob(Entity entity) {
        return customMobs.get(entity);
    }

    public void unregisterCustomMob(Entity entity) {
        customMobs.remove(entity);
    }
}