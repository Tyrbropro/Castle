package turbo.castle.gameplay.wave.mob;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.springframework.stereotype.Component;
import turbo.castle.currency.stone.repository.StoneRepositoryImpl;
import turbo.castle.currency.wood.repository.WoodRepositoryImpl;
import turbo.castle.data.PlayerData;
import turbo.castle.gameplay.wave.event.EventManager;
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
    public void endGame(){
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
        if (waveManager.isWoodPVP()) {
            int wood = (int) ((500 * waveManager.getCurrentWave()) * 0.5);
            woodRepository.addWood(wood);
            currentPlayer.sendMessage("Получили " + wood + " дерева");

        } else if (waveManager.isStonePVP()) {
            int stone = (int) ((500 * waveManager.getCurrentWave()) * 0.5);
            stoneRepository.addStone(stone);
            currentPlayer.sendMessage("Получили " + stone + " дерева");
        }
    }
}
