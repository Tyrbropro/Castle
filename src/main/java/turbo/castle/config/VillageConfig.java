package turbo.castle.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bson.Document;
import turbo.castle.gameplay.village.Building;
import turbo.castle.gameplay.village.BuildingManager;
import turbo.castle.gameplay.village.types.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VillageConfig {

    @Getter
    static Map<UUID, List<Building>> buildings = new HashMap<>();

    @Getter
    static Map<UUID, List<Class<? extends Building>>> unlockedBuildings = new HashMap<>();

    public static List<Document> toDocumentList(UUID uuid) {
        List<Document> documents = new ArrayList<>();
        List<Building> buildingList = buildings.get(uuid);
        if (buildingList != null) {
            for (Building building : buildingList) {
                Document doc = new Document("name", building.getClass().getSimpleName())
                        .append("level", building.getLevel());
                documents.add(doc);
            }
        }
        return documents;
    }

    public static void loadFromDocumentList(UUID uuid, List<Document> documents) {
        List<Building> buildingList = new ArrayList<>();
        for (Document doc : documents) {
            String buildingName = doc.getString("name");
            int level = doc.getInteger("level");

            Building building = createBuildingFromName(buildingName, uuid);
            if (building != null) {
                building.setLevel(level);
                buildingList.add(building);
            }
        }
        buildings.put(uuid, buildingList);
    }

    private static Building createBuildingFromName(String name, UUID uuid) {
        return switch (name) {
            case "TownHall" ->
                    new TownHall(new BuildingManager(uuid), new BlackSmith(), new Storage(), new FarmerStore(), new AlchemyStore());
            case "BlackSmith" -> new BlackSmith();
            case "Storage" -> new Storage();
            case "FarmerStore" -> new FarmerStore();
            case "AlchemyStore" -> new AlchemyStore();
            default -> null;
        };
    }
}
