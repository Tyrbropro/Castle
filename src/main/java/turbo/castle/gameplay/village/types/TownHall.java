package turbo.castle.gameplay.village.types;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.springframework.stereotype.Component;
import turbo.castle.gameplay.village.AbstractBuilding;
import turbo.castle.gameplay.village.Building;
import turbo.castle.gameplay.village.BuildingManager;
import turbo.castle.util.MapService;

import java.util.Arrays;
import java.util.Collections;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component
public class TownHall extends AbstractBuilding {

    BlackSmith blackSmith;
    Storage storage;
    FarmerStore farmerStore;
    AlchemyStore alchemyStore;

    public TownHall(BuildingManager buildingManager, BlackSmith blackSmith, Storage storage,
                    FarmerStore farmerStore, AlchemyStore alchemyStore) {
        super("Town Hall",
                new Location(MapService.getWorld(), -74, 64, 424),
                500,
                500,
                0);
        buildingManager.addBuilding(this);
        this.blackSmith = blackSmith;
        this.storage = storage;
        this.farmerStore = farmerStore;
        this.alchemyStore = alchemyStore;
    }

    @Override
    public void upgrade(Player player) {
        super.upgrade(player);
        BuildingManager buildingManager1 = new BuildingManager(player.getUniqueId());
        if (getLevel() == 1) {
            buildingManager1.unlockBuilding(BlackSmith.class);
            buildingManager1.addBuilding(blackSmith);
            player.sendMessage("Кузница открыта");
        } else if (getLevel() == 2) {
            buildingManager1.unlockBuilding(Storage.class);
            buildingManager1.addBuilding(storage);
            player.sendMessage("Склад открыт");
        } else if (getLevel() == 3) {
            buildingManager1.unlockBuilding(FarmerStore.class);
            buildingManager1.addBuilding(farmerStore);
            player.sendMessage("Фермерская лавка открыта");
        } else if (getLevel() == 4) {
            buildingManager1.unlockBuilding(AlchemyStore.class);
            buildingManager1.addBuilding(alchemyStore);
            player.sendMessage("Алхимическая лавка открыта");
        }
    }

    @Override
    public void onInteract(Player player) {
        setPriceStone(500);
        setPriceWood(500);
        for (int i = 0; i < getLevel(); i++) {
            setPriceStone((int) (getPriceStone() * 1.8));
            setPriceWood((int) (getPriceWood() * 1.8));
        }
        switch (getLevel()) {
            case 0 -> infoNextLvl = "Кузница";
            case 1 -> infoNextLvl = "Склад";
            case 2 -> infoNextLvl = "Фермерская лавка";
            case 3 -> infoNextLvl = "Алхимическая лавка";
            default -> infoNextLvl = "Нихуя";
        }
        setInfoNextLvl(infoNextLvl);
        openInventory(player);
    }

    @Override
    public void openInventory(Player player) {
        Inventory townHallInventory = Bukkit.createInventory(null, 9, "Town Hall");

        ItemStack infoItem = new ItemStack(Material.PAPER);
        ItemMeta infoMeta = infoItem.getItemMeta();
        infoMeta.setDisplayName("Информация");
        infoMeta.setLore(info());
        infoItem.setItemMeta(infoMeta);
        townHallInventory.setItem(0, infoItem);

        ItemStack upgradeItem = new ItemStack(Material.ANVIL);
        ItemMeta upgradeMeta = upgradeItem.getItemMeta();
        upgradeMeta.setDisplayName("Прокачать");
        upgradeItem.setItemMeta(upgradeMeta);
        townHallInventory.setItem(1, upgradeItem);

        player.openInventory(townHallInventory);
    }

    @Override
    public void interactInventory(Player player, int slot) {
        switch (slot) {
            case 0 -> player.sendMessage("Это Ратуша.");
            case 1 -> upgrade(player);
            default -> {
            }
        }
        openInventory(player);
    }
}
