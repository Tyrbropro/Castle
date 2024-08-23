package turbo.castle.gameplay.village.types;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import turbo.castle.data.PlayerData;
import turbo.castle.gameplay.village.AbstractBuilding;
import turbo.castle.util.MapService;

import java.util.Collections;

public class Storage extends AbstractBuilding {


    public Storage() {
        super("Storage",
                new Location(MapService.getWorld(), -82, 64, 424),
                1500,
                1500);
    }

    @Override
    public void onInteract(Player player) {
        setPriceStone(1500);
        setPriceWood(1500);
        for (int i = 0; i < getLevel(); i++) {
            setPriceStone((int) (getPriceStone() * 1.8));
            setPriceWood((int) (getPriceWood() * 1.8));
        }
        PlayerData data = PlayerData.getUsers().get(player.getUniqueId());
        int storage = 2000;
        switch (getLevel()) {
            case 0 -> {
                storage = 2000 ;
                infoNextLvl = "Вместимость 4000";
            }
            case 1 -> {
                storage = 4000 ;
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

    @Override
    public void openInventory(Player player) {
        Inventory storageInventory = Bukkit.createInventory(null, 9, "Storage");

        ItemStack infoItem = new ItemStack(Material.PAPER);
        ItemMeta infoMeta = infoItem.getItemMeta();
        infoMeta.setDisplayName("Информация");
        infoMeta.setLore(Collections.singletonList(info()));
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
}
