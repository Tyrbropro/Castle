package turbo.castle.gameplay.wave.event;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.springframework.stereotype.Component;
import turbo.castle.Castle;
import turbo.castle.gameplay.wave.event.types.MeteorShowerEvent;
import turbo.castle.gameplay.wave.event.types.WallEvent;
import turbo.castle.gameplay.wave.WaveManager;

import java.util.*;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EventManager {

    Set<WaveEvent> events = new HashSet<>();
    Random random = new Random();

    WaveManager waveManager;

    public EventManager(WaveManager waveManager) {
        registerEvent(new MeteorShowerEvent(null));
        registerEvent(new WallEvent(null));
        this.waveManager = waveManager;
    }

    public void registerEvent(WaveEvent event) {
        events.add(event);
    }

    public void startEventScheduler(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                int chance = calculateChance(waveManager.getCurrentWave());
                if (random.nextInt(30) < chance) {
                    WaveEvent event = getRandomEvent(player);
                    if (event != null) {
                        event.trigger();
                    }
                }
            }
        }.runTaskTimer(Castle.getPlugin(), 0L, 20L);
    }

    private int calculateChance(int currentWave) {
        if (currentWave >= 5) {
            return Math.min(29, (currentWave / 5) * 2);
        }
        return 0;
    }

    private WaveEvent getRandomEvent(Player player) {
        List<WaveEvent> eventList = new ArrayList<>(events);
        if (eventList.isEmpty()) return null;

        WaveEvent event = eventList.get(random.nextInt(eventList.size()));
        event.setPlayer(player);
        return event;
    }
}
