package turbo.castle.gameplay.village.types;

import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import turbo.castle.data.PlayerData;
import turbo.castle.gameplay.village.AbstractBuilding;
import turbo.castle.gameplay.village.SavableBuilding;
import turbo.castle.util.MapService;

public class Storage extends AbstractBuilding implements SavableBuilding {

    final Location loc = new Location(MapService.getWorld(), -84, 63, 419);
    private int storage = 2000;

    public Storage() {
        super("Storage",
                new Location(MapService.getWorld(), -82, 64, 424),
                1500,
                1500,
                100);
    }

    @Override
    public void onInteract(Player player) {
        if (!isClearArea) {
            openClearAreaConfirmation(player);
            spawnObstacles(loc);
        } else {
            removeObstacles(loc);
            setPriceStone(1500);
            setPriceWood(1500);
            for (int i = 0; i < getLevel(); i++) {
                setPriceStone((int) (getPriceStone() * 1.8));
                setPriceWood((int) (getPriceWood() * 1.8));
            }
            PlayerData data = PlayerData.getUsers().get(player.getUniqueId());
            switch (getLevel()) {
                case 0 -> {
                    storage = 2000;
                    infoNextLvl = "Вместимость 4000";
                }
                case 1 -> {
                    storage = 4000;
                    infoNextLvl = "Вместимость 6000";
                }
                case 2 -> {
                    storage = 6000;
                    infoNextLvl = "Вместимость 10000";
                }
                case 3 -> {
                    storage = 10000;
                    infoNextLvl = "Вместимость 15000";
                }
                case 4 -> storage = 15000;
                default -> infoNextLvl = "не придумал";
            }
            data.setMaxStone(storage);
            data.setMaxWood(storage);
            setInfoNextLvl(infoNextLvl);
            openInventory(player);
        }
    }

    public void openClearAreaConfirmation(Player player) {
        Inventory confirmInventory = Bukkit.createInventory(null, 9, "Подтвердить расчистку территории под склад");

        ItemStack confirmItem = new ItemStack(Material.EMERALD_BLOCK);
        ItemMeta confirmMeta = confirmItem.getItemMeta();
        confirmMeta.setDisplayName("Подтвердить расчистку за " + clearAreaPrice + " монет");
        confirmItem.setItemMeta(confirmMeta);
        confirmInventory.setItem(3, confirmItem);

        ItemStack cancelItem = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta cancelMeta = cancelItem.getItemMeta();
        cancelMeta.setDisplayName("Отмена");
        cancelItem.setItemMeta(cancelMeta);
        confirmInventory.setItem(5, cancelItem);

        player.openInventory(confirmInventory);
    }

    @Override
    public void openInventory(Player player) {
        Inventory storageInventory = Bukkit.createInventory(null, 9, "Storage");

        ItemStack infoItem = new ItemStack(Material.PAPER);
        ItemMeta infoMeta = infoItem.getItemMeta();
        infoMeta.setDisplayName("Информация");
        infoMeta.setLore(info());
        infoItem.setItemMeta(infoMeta);
        storageInventory.setItem(0, infoItem);

        ItemStack upgradeItem = new ItemStack(Material.ANVIL);
        ItemMeta upgradeMeta = upgradeItem.getItemMeta();
        upgradeMeta.setDisplayName("Прокачать");
        upgradeItem.setItemMeta(upgradeMeta);
        storageInventory.setItem(1, upgradeItem);

        player.openInventory(storageInventory);
    }


    @Override
    public void interactInventory(Player player, int slot) {
        switch (slot) {
            case 0 -> player.sendMessage("Это Склад. Здесь храняться богатсва деревни.");
            case 1 -> {
                upgrade(player);
                onInteract(player);
            }
            default -> {
            }
        }
        openInventory(player);
    }

    @Override
    public Document saveData() {
        return new Document("isClearArea", isClearArea)
                .append("level", getLevel())
                .append("storage", storage);

    }

    @Override
    public void loadData(Document document) {
        setClearArea(document.getBoolean("isClearArea"));
        setLevel(document.getInteger("level"));
        storage = document.getInteger("storage");

        if (!isClearArea) spawnObstacles(loc);
        else removeObstacles(loc);
    }
}
