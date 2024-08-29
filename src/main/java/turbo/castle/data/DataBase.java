package turbo.castle.data;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import turbo.castle.config.VillageConfig;
import turbo.castle.config.VillageLevelConfig;
import turbo.castle.currency.money.repository.MoneyRepositoryImpl;
import turbo.castle.currency.stone.repository.StoneRepositoryImpl;
import turbo.castle.currency.wood.repository.WoodRepositoryImpl;
import turbo.castle.gameplay.village.BuildingManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class DataBase {

    static String MONGO_URI;
    static String DATABASE_NAME;
    static String COLLECTION_NAME;

    public static void loadDatabaseConfig() {
        try (BufferedReader reader = new BufferedReader(
                new FileReader("database_config_castle.txt"))) {
            MONGO_URI = reader.readLine();
            DATABASE_NAME = reader.readLine();
            COLLECTION_NAME = reader.readLine();

        } catch (IOException e) {
            throw new RuntimeException("Failed to load database config", e);
        }
    }

    public static void saveToMongoDB(PlayerData playerData) {
        try (MongoClient mongoClient = MongoClients.create(MONGO_URI)) {
            MongoDatabase database = mongoClient.getDatabase(DATABASE_NAME);
            MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);

            Document upgradeLevelsDoc = new Document();
            for (Map.Entry<String, Integer> entry : playerData.getUpgradeLevels().entrySet()) {
                upgradeLevelsDoc.append(entry.getKey(), entry.getValue());
            }

            Document upgradedItemsDoc = new Document();
            for (Map.Entry<String, ItemStack> entry : playerData.getUpgradedItems().entrySet()) {
                upgradedItemsDoc.append(entry.getKey(), itemStackToDocument(entry.getValue()));
            }

            List<Document> purchasedFoodDocs = new ArrayList<>();
            for (ItemStack item : playerData.getPurchasedFood()) {
                purchasedFoodDocs.add(itemStackToDocument(item));
            }

            List<Document> purchasedPotionsDocs = new ArrayList<>();
            for (ItemStack item : playerData.getPurchasedPotions()) {
                purchasedPotionsDocs.add(itemStackToDocument(item));
            }

            Document doc = new Document("uuid", playerData.getUuid().toString())
                    .append("xp", playerData.getVillageLevelConfig().getXp())
                    .append("wood", playerData.getWoodRepository().getWood())
                    .append("stone", playerData.getStoneRepository().getStone())
                    .append("money", playerData.getMoneyRepository().getMoney())
                    .append("xp", playerData.getVillageLevelConfig().getXp())
                    .append("buildings", VillageConfig.toDocumentList(playerData.getUuid()))
                    .append("upgradeLevels", upgradeLevelsDoc)
                    .append("upgradedItems", upgradedItemsDoc)
                    .append("purchasedFood", purchasedFoodDocs)
                    .append("purchasedPotions", purchasedPotionsDocs);

            Document query = new Document("uuid", playerData.getUuid().toString());
            collection.replaceOne(query, doc, new ReplaceOptions().upsert(true));
        }
    }

    private static Document itemStackToDocument(ItemStack itemStack) {
        if (itemStack == null) return null;
        Document doc = new Document();
        doc.append("material", itemStack.getType().name());
        doc.append("amount", itemStack.getAmount());

        return doc;
    }

    public static PlayerData loadFromMongoDB(UUID uuid) {
        try (MongoClient mongoClient = MongoClients.create(MONGO_URI)) {
            MongoDatabase database = mongoClient.getDatabase(DATABASE_NAME);
            MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);

            Document query = new Document("uuid", uuid.toString());
            Document result = collection.find(query).first();

            if (result != null) {
                WoodRepositoryImpl woodRepo = new WoodRepositoryImpl();
                StoneRepositoryImpl stoneRepo = new StoneRepositoryImpl();
                MoneyRepositoryImpl moneyRepo = new MoneyRepositoryImpl();
                VillageLevelConfig villageLevelConfig = new VillageLevelConfig();

                woodRepo.setWood(result.getInteger("wood"));
                stoneRepo.setStone(result.getInteger("stone"));
                moneyRepo.setMoney(result.getInteger("money"));
                villageLevelConfig.setXp(result.getInteger("xp"));

                List<Document> buildingDocs = (List<Document>) result.get("buildings");
                VillageConfig.loadFromDocumentList(uuid, buildingDocs);

                Map<String, Integer> upgradeLevels = new HashMap<>();
                Document upgradeLevelsDoc = (Document) result.get("upgradeLevels");
                for (String key : upgradeLevelsDoc.keySet()) {
                    upgradeLevels.put(key, upgradeLevelsDoc.getInteger(key));
                }

                Map<String, ItemStack> upgradedItems = new HashMap<>();
                Document upgradedItemsDoc = (Document) result.get("upgradedItems");
                for (String key : upgradedItemsDoc.keySet()) {
                    upgradedItems.put(key, documentToItemStack((Document) upgradedItemsDoc.get(key)));
                }

                List<Document> purchasedFoodDocs = (List<Document>) result.get("purchasedFood");
                List<ItemStack> purchasedFood = new ArrayList<>();
                for (Document foodDoc : purchasedFoodDocs) {
                    purchasedFood.add(documentToItemStack(foodDoc));
                }

                List<Document> purchasedPotionsDocs = (List<Document>) result.get("purchasedPotions");
                List<ItemStack> purchasedPotions = new ArrayList<>();
                for (Document potionDoc : purchasedPotionsDocs) {
                    purchasedPotions.add(documentToItemStack(potionDoc));
                }

                PlayerData playerData = new PlayerData(uuid, woodRepo, stoneRepo, moneyRepo, new BuildingManager(uuid), upgradeLevels, upgradedItems, villageLevelConfig);
                playerData.getPurchasedFood().addAll(purchasedFood);
                playerData.getPurchasedPotions().addAll(purchasedPotions);

                return playerData;
            } else {
                return new PlayerData(uuid);
            }
        }
    }

    private static ItemStack documentToItemStack(Document doc) {
        if (doc == null) return null;
        Material material = Material.valueOf(doc.getString("material"));
        int amount = doc.getInteger("amount");

        return new ItemStack(material, amount);
    }
}
