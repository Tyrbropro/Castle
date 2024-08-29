package turbo.castle.util.event;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import turbo.castle.data.PlayerData;
import turbo.castle.gameplay.wave.SpawnWave;
import turbo.castle.util.MapService;

import java.util.UUID;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PlayerDamageEvents implements Listener {
    SpawnWave spawnWave;

    @Autowired
    public PlayerDamageEvents(SpawnWave spawnWave) {
        this.spawnWave = spawnWave;
    }

    @EventHandler
    public void deathEvent(EntityDeathEvent event) {
        event.setDroppedExp(0);

        LivingEntity death = event.getEntity();
        Player killer = death.getKiller();
        if (killer != null) {
            PlayerData data = PlayerData.getUsers().get(killer.getUniqueId());
            data.getWoodRepository().addWood(50);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        player.getInventory().clear();
        if (spawnWave == null) return;
        spawnWave.endGame();
        player.teleport(new Location(MapService.getWorld(), -77.5, 64, 420.5));
        player.sendMessage("Конец игры! Вы погибли.");
    }
}
