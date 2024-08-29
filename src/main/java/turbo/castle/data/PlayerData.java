package turbo.castle.data;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.bukkit.inventory.ItemStack;
import turbo.castle.config.VillageLevelConfig;
import turbo.castle.currency.money.repository.MoneyRepositoryImpl;
import turbo.castle.currency.stone.repository.StoneRepositoryImpl;
import turbo.castle.currency.wood.repository.WoodRepositoryImpl;
import turbo.castle.gameplay.village.BuildingManager;

import java.util.*;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlayerData {


    List<ItemStack> purchasedFood = new ArrayList<>();

    List<ItemStack> purchasedPotions = new ArrayList<>();


    @Getter
    static HashMap<UUID, PlayerData> users = new HashMap<>();

    final UUID uuid;
    final WoodRepositoryImpl woodRepository;
    final StoneRepositoryImpl stoneRepository;
    final MoneyRepositoryImpl moneyRepository;
    final VillageLevelConfig villageLevelConfig;

    @Getter
    @Setter
    int multiplierWood = 1;
    @Getter
    @Setter
    int multiplierStone = 1;
    @Getter
    @Setter
    int maxStone = 2000;

    @Getter
    @Setter
    int maxWood = 2000;

    @Setter
    Map<String, Integer> upgradeLevels;
    @Setter
    Map<String, ItemStack> upgradedItems;

    @Setter
    BuildingManager buildingManager;

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
        this.woodRepository = new WoodRepositoryImpl();
        this.stoneRepository = new StoneRepositoryImpl();
        this.moneyRepository = new MoneyRepositoryImpl();
        this.buildingManager = new BuildingManager(uuid);
        this.upgradeLevels = new HashMap<>();
        this.upgradedItems = new HashMap<>();
        this.villageLevelConfig = new VillageLevelConfig();
        users.put(uuid, this);
    }

    public PlayerData(UUID uuid, WoodRepositoryImpl woodRepository, StoneRepositoryImpl stoneRepository,
                      MoneyRepositoryImpl moneyRepository,
                      BuildingManager buildingManager, Map<String, Integer> upgradeLevels,
                      Map<String, ItemStack> upgradedItems, VillageLevelConfig villageLevelConfig) {
        this.uuid = uuid;
        this.woodRepository = woodRepository;
        this.stoneRepository = stoneRepository;
        this.moneyRepository = moneyRepository;
        this.buildingManager = buildingManager;
        this.upgradeLevels = upgradeLevels;
        this.upgradedItems = upgradedItems;
        this.villageLevelConfig = villageLevelConfig;

        users.put(uuid, this);
    }

    public List<ItemStack> getPurchasedFood() {
        return purchasedFood;
    }

    public void addPurchasedFood(ItemStack food) {
        purchasedFood.add(food);
    }

    public void clearPurchasedFood() {
        purchasedFood.clear();
    }

    public List<ItemStack> getPurchasedPotions() {
        return purchasedPotions;
    }

    public void addPurchasedPotion(ItemStack potion) {
        purchasedPotions.add(potion);
    }

    public void clearPurchasedPotions() {
        purchasedPotions.clear();
    }

    public void saveToMongoDB() {
        DataBase.saveToMongoDB(this);
    }

    public static PlayerData loadFromMongoDB(UUID uuid) {
        return DataBase.loadFromMongoDB(uuid);
    }
}
