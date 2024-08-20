package turbo.castle.gameplay.wave.event;

import lombok.Setter;
import org.bukkit.entity.Player;

public abstract class WaveEvent {
    @Setter
    protected Player player;

    public WaveEvent(Player player) {
        this.player = player;
    }

    public abstract void trigger();

    public void sendEventMessage(String message) {
        if (player != null && player.isOnline()) {
            player.sendMessage(message);
        }
    }
}