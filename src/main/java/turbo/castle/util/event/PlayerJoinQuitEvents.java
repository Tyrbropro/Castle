package turbo.castle.util.event;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import turbo.castle.data.PlayerData;
import turbo.castle.gameplay.stone.SpawnStone;
import turbo.castle.gameplay.tree.TreeGrowth;

import java.util.UUID;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PlayerJoinQuitEvents implements Listener {
    TreeGrowth treeGrowth;
    SpawnStone spawnStone;

    @Autowired
    public PlayerJoinQuitEvents(TreeGrowth treeGrowth, SpawnStone spawnStone) {
        this.treeGrowth = treeGrowth;
        this.spawnStone = spawnStone;
    }

    @EventHandler
    public void asyncPlayerLogin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();
        PlayerData.getUsers().putIfAbsent(uuid, new PlayerData(uuid));
    }

    @EventHandler
    public void joinPlayer(PlayerJoinEvent event) {
        treeGrowth.grow();
        spawnStone.spawn();
    }
}
