package turbo.castle.util.event;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import turbo.castle.config.VillageLevelConfig;
import turbo.castle.data.PlayerData;
import turbo.castle.gameplay.stone.SpawnStone;
import turbo.castle.gameplay.tree.TreeGrowth;
import turbo.castle.gameplay.village.BuildingManager;
import turbo.castle.gameplay.village.types.BlackSmith;

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

        PlayerData playerData = PlayerData.loadFromMongoDB(uuid);
        PlayerData.getUsers().put(uuid, playerData);

        BuildingManager buildingManager = new BuildingManager(uuid);
        BlackSmith blackSmith = (BlackSmith) buildingManager.getBuildingByName("Blacksmith");
        if (blackSmith != null) {
            blackSmith.setUpgradeLevels(playerData.getUpgradeLevels());
            blackSmith.setUpgradedItems(playerData.getUpgradedItems());
        }
    }

    @EventHandler
    public void joinPlayer(PlayerJoinEvent event) {
        treeGrowth.grow();
        spawnStone.spawn();

        Player player = event.getPlayer();
        PlayerData data = PlayerData.getUsers().get(player.getUniqueId());
        VillageLevelConfig villageLevelConfig = data.getVillageLevelConfig();

        player.setExp(0f);
        player.setLevel(0);

        player.giveExp(villageLevelConfig.getXp());
    }

    @EventHandler
    public void quitPlayer(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        PlayerData playerData = PlayerData.getUsers().get(uuid);

        BuildingManager buildingManager = new BuildingManager(uuid);
        BlackSmith blackSmith = (BlackSmith) buildingManager.getBuildingByName("Blacksmith");
        if (blackSmith == null) playerData.saveToMongoDB();
        else {
            playerData.setUpgradedItems(blackSmith.getUpgradedItems());
            playerData.setUpgradeLevels(blackSmith.getUpgradeLevels());

            playerData.saveToMongoDB();
        }
    }
}
