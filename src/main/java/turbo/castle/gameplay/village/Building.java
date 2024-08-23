package turbo.castle.gameplay.village;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public interface Building {

    String getName();

    String getInfoNextLvl();

    void setInfoNextLvl(String infoNextLvl);

    Location getLocation();

    int getLevel();

    void setLevel(int level);

    void upgrade(Player player);

    int getPriceStone();

    void setPriceStone(int priceStone);

    int getPriceWood();

    void openInventory(Player player);
    void interactInventory(Player player, int slot);

    void setPriceWood(int priceWood);

    String info();

    void onInteract(Player player);

}
