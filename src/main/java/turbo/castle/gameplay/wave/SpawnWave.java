package turbo.castle.gameplay.wave;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.springframework.stereotype.Component;
import turbo.castle.currency.stone.repository.StoneRepositoryImpl;
import turbo.castle.currency.wood.repository.WoodRepositoryImpl;
import turbo.castle.data.PlayerData;
import turbo.castle.gameplay.village.Building;
import turbo.castle.gameplay.village.BuildingManager;
import turbo.castle.gameplay.village.types.BlackSmith;
import turbo.castle.gameplay.wave.event.EventManager;
import turbo.castle.gameplay.wave.mob.CustomMob;
import turbo.castle.gameplay.wave.mob.CustomMobFactory;
import turbo.castle.gameplay.wave.mob.types.*;
import turbo.castle.util.BlockUtil;
import turbo.castle.util.MapService;

import java.util.*;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SpawnWave {
    final Set<Class<? extends CustomMob>> zombieTypes = new HashSet<>();
    final WaveManager waveManager;
    final BlockUtil blockUtil;
    @Getter
    final CustomMobFactory customMobFactory;
    final EventManager eventManager;
    final Random random = new Random();
    final World world = MapService.getWorld();
    Player currentPlayer;
    int remainingMobs = 0;
    final Location[] locations = new Location[]{
            new Location(world, -141, 64, 462.5),
            new Location(world, -124.5, 64, 446),
            new Location(world, -141, 64, 429.5),
            new Location(world, -157.5, 64, 446)
    };
    final Location target = new Location(world, -145, 64, 442);

    public SpawnWave(CustomMobFactory customMobFactory, EventManager eventManager, WaveManager waveManager, BlockUtil blockUtil) {
        this.waveManager = waveManager;
        this.customMobFactory = customMobFactory;
        this.eventManager = eventManager;
        this.blockUtil = blockUtil;

        zombieTypes.add(FireZombie.class);
        zombieTypes.add(SlowZombie.class);
        zombieTypes.add(PoisonZombie.class);
        zombieTypes.add(CommonZombie.class);
        zombieTypes.add(KamikazeZombie.class);
    }

    public void setCurrentPlayer(Player player) {
        this.currentPlayer = player;
    }

    public void startNextWave() {
        checkError();

        waveManager.nextWave();
        if (waveManager.getCurrentWave() == 1) {
            giveSword(currentPlayer);
            giveArmor(currentPlayer);
            givePlayerFood(currentPlayer);
            givePlayerPotion(currentPlayer);
            eventManager.startEventScheduler(currentPlayer);
        }
        remainingMobs = 0;

        for (int i = 0; i < 5; i++) {
            Creature entity = (Creature) world.spawnEntity(randomLocation(), EntityType.ZOMBIE);
            CustomMob customMob = createRandomZombie();

            customMob.onSpawn(entity);
            customMobFactory.registerCustomMob(entity, customMob);
            remainingMobs++;

            if (customMob instanceof FireZombie fireZombie) {
                fireZombie.aggroOnPlayer(entity, currentPlayer);
            } else if (customMob instanceof SlowZombie slowZombie) {
                slowZombie.aggroOnPlayer(entity, currentPlayer);
            } else if (customMob instanceof PoisonZombie poisonZombie) {
                poisonZombie.aggroOnPlayer(entity, currentPlayer);
            } else if (customMob instanceof CommonZombie commonZombie) {
                commonZombie.aggroOnPlayer(entity, currentPlayer);
            }
        }
    }

    private void checkError() {
        if (currentPlayer == null) throw new IllegalStateException("Игрок не установлен.");
        if (waveManager.isStonePVP()) buildStone();
        else if (waveManager.isWoodPVP()) buildWood();
        else throw new IllegalStateException("Бой не установлен.");
    }

    private void giveSword(Player player) {
        player.getInventory().clear();

        BuildingManager buildingManager = new BuildingManager(player.getUniqueId());
        BlackSmith building = (BlackSmith) buildingManager.getBuildingByName("Blacksmith");
        if (building == null) {
            player.getInventory().addItem(new ItemStack(Material.WOOD_SWORD));
        } else player.getInventory().addItem(building.getSword());
    }

    private void giveArmor(Player player) {
        BuildingManager buildingManager = new BuildingManager(player.getUniqueId());
        BlackSmith building = (BlackSmith) buildingManager.getBuildingByName("Blacksmith");
        if (building == null) return;
        if (building.getHelmet() == null) return;
        PlayerInventory inventory = player.getInventory();

        inventory.setHelmet(building.getHelmet());
        inventory.setChestplate(building.getChestplate());
        inventory.setLeggings(building.getLeggings());
        inventory.setBoots(building.getBoots());

        player.updateInventory();
    }

    private void givePlayerFood(Player player) {
        PlayerData playerData = PlayerData.getUsers().get(player.getUniqueId());
        List<ItemStack> purchasedFood = playerData.getPurchasedFood();

        for (ItemStack food : purchasedFood) {
            player.getInventory().addItem(food);
        }

        playerData.clearPurchasedFood();
    }

    private void givePlayerPotion(Player player) {
        PlayerData playerData = PlayerData.getUsers().get(player.getUniqueId());
        List<ItemStack> potions = playerData.getPurchasedPotions();

        for (ItemStack potion : potions) {
            player.getInventory().addItem(potion);
        }

        playerData.clearPurchasedPotions();
    }

    private CustomMob createRandomZombie() {
        List<Class<? extends CustomMob>> zombieList = new ArrayList<>(zombieTypes);

        int randomIndex = random.nextInt(zombieList.size());
        Class<? extends CustomMob> zombieClass = zombieList.get(randomIndex);

        try {
            return zombieClass.getConstructor(WaveManager.class).newInstance(waveManager);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create zombie instance", e);
        }
    }

    private Location randomLocation() {
        int randomIndex = random.nextInt(locations.length);
        return locations[randomIndex];
    }

    public void onMobDeath() {
        remainingMobs--;
        if (remainingMobs <= 0) {
            endWave();
            startNextWave();
        }
    }

    private void buildStone() {
        Location sourceMin = new Location(world, -161, 64, 484);
        Location sourceMax = new Location(world, -154, 80, 491);
        blockUtil.copyPaste(sourceMin, sourceMax, target);
    }

    private void buildWood() {
        Location sourceMin = new Location(world, -152, 64, 484);
        Location sourceMax = new Location(world, -145, 80, 491);
        blockUtil.copyPaste(sourceMin, sourceMax, target);
    }

    private void endWave() {
        Set<Entity> entities = new HashSet<>(customMobFactory.getCustomMobs().keySet());
        for (Entity entity : entities) {
            customMobFactory.unregisterCustomMob(entity);
            entity.remove();
        }
    }

    public void endGame() {
        Set<Entity> entities = new HashSet<>(customMobFactory.getCustomMobs().keySet());
        for (Entity entity : entities) {
            customMobFactory.unregisterCustomMob(entity);
            entity.remove();
        }
        reward();
        waveManager.setCurrentWave(0);
        waveManager.setStonePVP(false);
        waveManager.setWoodPVP(false);
    }

    private void reward() {
        UUID uuid = currentPlayer.getUniqueId();
        PlayerData data = PlayerData.getUsers().get(uuid);
        WoodRepositoryImpl woodRepository = data.getWoodRepository();
        StoneRepositoryImpl stoneRepository = data.getStoneRepository();

        currentPlayer.sendMessage("Вы прошли " + waveManager.getCurrentWave() + " волн");

        int reward = 250 + (waveManager.getCurrentWave() - 1) * (waveManager.getCurrentWave() - 1) * 300;
        if (waveManager.isWoodPVP()) {
            woodRepository.addWood(reward);
            if (woodRepository.getWood() >= data.getMaxWood()) {
                woodRepository.setWood(data.getMaxWood());
                currentPlayer.sendMessage("У вас максимум дерева");
            }
            currentPlayer.sendMessage("Получили " + reward + " дерева");

        } else if (waveManager.isStonePVP()) {
            stoneRepository.addStone(reward);
            if (stoneRepository.getStone() >= data.getMaxStone()) {
                stoneRepository.setStone(data.getMaxStone());
                currentPlayer.sendMessage("У вас максимум камня");
            }
            currentPlayer.sendMessage("Получили " + reward + " камня");
        }
    }
}
