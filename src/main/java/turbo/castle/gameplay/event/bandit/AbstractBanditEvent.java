package turbo.castle.gameplay.event.bandit;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import turbo.castle.Castle;
import turbo.castle.config.VillageLevelConfig;
import turbo.castle.currency.money.repository.MoneyRepositoryImpl;
import turbo.castle.data.PlayerData;
import turbo.castle.gameplay.village.BuildingManager;
import turbo.castle.gameplay.village.types.BlackSmith;
import turbo.castle.util.BlockUtil;

import java.util.ArrayList;
import java.util.List;

@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class AbstractBanditEvent {
    final BlockUtil blockUtil;
    final Player player;
    final List<LivingEntity> spawnedMobs = new ArrayList<>();
    final BanditSpawner banditSpawner;
    boolean proximityChecked = false;
    boolean isCompleted = false;
    final int CHECK_INTERVAL = 20;
    final int MOVE_INTERVAL = 5;
    final int PROXIMITY_RADIUS = 30;
    final int MOB_COUNT = 3;
    final int REWARD_AMOUNT = 100;
    final int REWARD_XP = 5;

    protected AbstractBanditEvent(BlockUtil blockUtil, Player player) {
        this.blockUtil = blockUtil;
        this.player = player;
        this.banditSpawner = new BanditSpawner(player, spawnedMobs);
    }

    protected void checkPlayerProximity(Location targetLocation, Location mobSpawnLocation) {
        if (!proximityChecked && player.getLocation().distance(targetLocation) <= PROXIMITY_RADIUS) {
            player.sendMessage("Вы около бандитского лагеря");
            spawnMobs(mobSpawnLocation, MOB_COUNT);
            equipPlayer();
            startMobMonitoring();
            proximityChecked = true;
        }
    }

    protected void equipPlayer() {
        giveSword(player);
        giveArmor(player);
    }

    protected void startMobMonitoring() {
        Bukkit.getScheduler().runTaskTimer(Castle.getPlugin(), this::monitorMobs, 0, CHECK_INTERVAL);
    }

    protected void clearMobs() {
        spawnedMobs.forEach(LivingEntity::remove);
        spawnedMobs.clear();
    }

    protected void monitorMobs() {
        spawnedMobs.removeIf(LivingEntity::isDead);
        if (spawnedMobs.isEmpty()) {
            rewardPlayer();
            cleanup();
            isCompleted = true;
            Bukkit.getScheduler().cancelTasks(Castle.getPlugin());
        }
    }

    protected void rewardPlayer() {
        PlayerData data = PlayerData.getUsers().get(player.getUniqueId());
        MoneyRepositoryImpl moneyRepository = data.getMoneyRepository();
        VillageLevelConfig villageLevelConfig = data.getVillageLevelConfig();

        villageLevelConfig.addXp(REWARD_XP);
        moneyRepository.addMoney(REWARD_AMOUNT);

        player.setExp(0f);
        player.setLevel(0);

        player.giveExp(villageLevelConfig.getXp());
        player.sendMessage("Вы уничтожили всех бандитов и забрали у них " + REWARD_AMOUNT + " монет");

        player.getInventory().clear();
    }

    protected abstract void cleanup();

    protected void spawnMobs(Location location, int count) {
        banditSpawner.spawnMobs(location, count);
    }

    protected void giveSword(Player player) {
        player.getInventory().clear();
        BlackSmith building = getBlacksmithBuilding();
        player.getInventory().addItem(building == null ? new ItemStack(Material.WOOD_SWORD) : building.getSword());
    }

    protected void giveArmor(Player player) {
        BlackSmith building = getBlacksmithBuilding();
        if (building != null) {
            PlayerInventory inventory = player.getInventory();
            inventory.setHelmet(building.getHelmet());
            inventory.setChestplate(building.getChestplate());
            inventory.setLeggings(building.getLeggings());
            inventory.setBoots(building.getBoots());
            player.updateInventory();
        }
    }

    private BlackSmith getBlacksmithBuilding() {
        BuildingManager buildingManager = new BuildingManager(player.getUniqueId());
        return (BlackSmith) buildingManager.getBuildingByName("Blacksmith");
    }
}
