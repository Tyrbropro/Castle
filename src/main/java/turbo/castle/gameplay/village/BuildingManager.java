package turbo.castle.gameplay.village;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import turbo.castle.config.VillageConfig;
import turbo.castle.gameplay.village.types.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BuildingManager {

    UUID uuid;

    public BuildingManager(UUID uuid) {
        this.uuid = uuid;

        List<Class<? extends Building>> unlockedBuildings = VillageConfig.getUnlockedBuildings().computeIfAbsent(uuid, k -> new ArrayList<>());
        List<Building> buildingsList = VillageConfig.getBuildings().computeIfAbsent(uuid, k -> new ArrayList<>());

        if (unlockedBuildings.isEmpty()) {
            unlockedBuildings.add(TownHall.class);
        }
        if (buildingsList.isEmpty()) {
            buildingsList.add(new TownHall(this, new BlackSmith(), new Storage(),new FarmerStore(), new AlchemyStore()));
        }

    }

    public void addBuilding(Building building) {
        VillageConfig.getBuildings().get(uuid).add(building);
    }


    public Building getBuildingByLocation(Location location) {
        List<Building> buildings = VillageConfig.getBuildings().get(uuid);
        if (buildings == null || buildings.isEmpty()) {
            assert buildings != null;
            buildings.add(new TownHall(this, new BlackSmith(), new Storage(),new FarmerStore(), new AlchemyStore()));
        }
        for (Building building : buildings) {
            if (building.getLocation().equals(location)) {
                return building;
            }
        }
        return null;
    }

    public Building getBuildingByName(String name) {
        List<Building> buildings = VillageConfig.getBuildings().get(uuid);
        if (buildings == null || buildings.isEmpty()) {
            assert buildings != null;
            buildings.add(new TownHall(this, new BlackSmith(), new Storage(),new FarmerStore(), new AlchemyStore()));
        }
        for (Building building : buildings) {
            if (building.getName().equals(name)) {
                return building;
            }
        }
        return null;
    }

    public void interactWithBuilding(Player player, Location location) {
        Building building = getBuildingByLocation(location);
        if (building != null) {
            building.onInteract(player);
        } else {
            player.sendMessage("Здесь нет здания.");
        }
    }

    public void unlockBuilding(Class<? extends Building> buildingClass) {
        List<Class<? extends Building>> unlockedBuildings = VillageConfig.getUnlockedBuildings().get(uuid);
        if (!unlockedBuildings.contains(buildingClass)) {
            unlockedBuildings.add(buildingClass);
        }
    }

    public boolean isBuildingUnlocked(Class<? extends Building> buildingClass) {
        List<Class<? extends Building>> unlockedBuildings = VillageConfig.getUnlockedBuildings().get(uuid);
        return unlockedBuildings.contains(buildingClass);
    }
}
