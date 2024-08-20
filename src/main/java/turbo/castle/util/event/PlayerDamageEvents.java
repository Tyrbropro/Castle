package turbo.castle.util.event;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.springframework.stereotype.Component;
import turbo.castle.currency.wood.repository.WoodRepositoryImpl;
import turbo.castle.data.PlayerData;
import turbo.castle.gameplay.wave.mob.SpawnWave;

import java.util.UUID;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PlayerDamageEvents implements Listener {
    private final SpawnWave spawnWave; // Зависимость от SpawnWave

    public PlayerDamageEvents(SpawnWave spawnWave) {
        this.spawnWave = spawnWave;
    }

    @EventHandler
    public void deathEvent(EntityDeathEvent event) {
        event.setDroppedExp(0);

        LivingEntity death = event.getEntity();
        if (death.getKiller() != null) {
            Player player = death.getKiller();
            UUID uuid = player.getUniqueId();
            PlayerData data = PlayerData.getUsers().get(uuid);

            WoodRepositoryImpl woodRepository = data.getWoodRepository();
            woodRepository.addWood(50);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        spawnWave.endGame();
        player.sendMessage("Конец игры! Вы погибли.");
    }
}
