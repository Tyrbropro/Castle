package turbo.castle.gameplay.village;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public interface Building {

    String getName();

    String getInfoNextLvl();

    void setInfoNextLvl(String infoNextLvl);

    Location getLocation();

    int getLevel();

    boolean isClearArea();

    void setClearArea(boolean clearArea);

    int getClearAreaPrice();

    void setClearAreaPrice(int clearAreaPrice);

    void setLevel(int level);

    void upgrade(Player player);

    int getPriceStone();

    void setPriceStone(int priceStone);

    int getPriceWood();

    void openInventory(Player player);

    void interactClearArea(Player player, int slot);

    void interactInventory(Player player, int slot);

    void setPriceWood(int priceWood);

    List<String> info();

    void onInteract(Player player);

    void spawnObstacles(Location target);

    void removeObstacles(Location target);

}
