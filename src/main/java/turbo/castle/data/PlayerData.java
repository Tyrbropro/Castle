package turbo.castle.data;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.bukkit.inventory.ItemStack;
import turbo.castle.currency.stone.repository.StoneRepositoryImpl;
import turbo.castle.currency.wood.repository.WoodRepositoryImpl;
import turbo.castle.gameplay.village.BuildingManager;

import org.bson.Document;

import java.util.*;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlayerData {


    private List<ItemStack> purchasedFood = new ArrayList<>();

    private List<ItemStack> purchasedPotions = new ArrayList<>();


    @Getter
    static HashMap<UUID, PlayerData> users = new HashMap<>();

    final UUID uuid;
    final WoodRepositoryImpl woodRepository;
    final StoneRepositoryImpl stoneRepository;

    @Getter
    @Setter
    int multiplierWood = 0;
    @Getter
    @Setter
    int multiplierStone = 0;
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
        this.buildingManager = new BuildingManager(uuid);
        this.upgradeLevels = new HashMap<>();
        this.upgradedItems = new HashMap<>();
        users.put(uuid, this);
    }

    public PlayerData(UUID uuid, WoodRepositoryImpl woodRepository, StoneRepositoryImpl stoneRepository,
                      BuildingManager buildingManager, Map<String, Integer> upgradeLevels,
                      Map<String, ItemStack> upgradedItems) {
        this.uuid = uuid;
        this.woodRepository = woodRepository;
        this.stoneRepository = stoneRepository;
        this.buildingManager = buildingManager;
        this.upgradeLevels = upgradeLevels;
        this.upgradedItems = upgradedItems;

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
