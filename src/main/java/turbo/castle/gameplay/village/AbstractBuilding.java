package turbo.castle.gameplay.village;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import turbo.castle.currency.money.repository.MoneyRepositoryImpl;
import turbo.castle.currency.stone.repository.StoneRepositoryImpl;
import turbo.castle.currency.wood.repository.WoodRepositoryImpl;
import turbo.castle.data.PlayerData;
import turbo.castle.util.BlockUtil;
import turbo.castle.util.MapService;

import java.util.Arrays;
import java.util.List;

@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class AbstractBuilding implements Building {

    String name;
    String infoNextLvl;
    Location location;
    int level;
    int priceStone;
    int priceWood;
    int clearAreaPrice;
    boolean isClearArea;

    public AbstractBuilding(String name, Location location, int priceStone, int priceWood, int clearAreaPrice) {
        this.name = name;
        this.location = location;
        this.level = 0;
        this.clearAreaPrice = clearAreaPrice;
        this.isClearArea = false;
        this.priceStone = priceStone;
        this.priceWood = priceWood;
    }

    @Override
    public List<String> info() {
        return Arrays.asList(
                String.format("Информация о %s:", name),
                String.format("Уровень: %d", level),
                String.format("Цена улучшения (камень): %d", priceStone),
                String.format("Цена улучшения (дерево): %d", priceWood),
                "На следующем уровне будет доступно:",
                infoNextLvl
        );
    }

    @Override
    public String getInfoNextLvl() {
        return infoNextLvl;
    }

    @Override
    public void openInventory(Player player) {

    }

    @Override
    public boolean isClearArea() {
        return isClearArea;
    }

    @Override
    public void setClearArea(boolean clearArea) {
        isClearArea = clearArea;
    }

    @Override
    public void setClearAreaPrice(int clearAreaPrice) {
        this.clearAreaPrice = clearAreaPrice;
    }

    @Override
    public int getClearAreaPrice() {
        return clearAreaPrice;
    }

    @Override
    public void setInfoNextLvl(String infoNextLvl) {
        this.infoNextLvl = infoNextLvl;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public int getPriceStone() {
        return priceStone;
    }

    @Override
    public void setPriceStone(int priceStone) {
        this.priceStone = priceStone;
    }

    @Override
    public int getPriceWood() {
        return priceWood;
    }

    @Override
    public void setPriceWood(int priceWood) {
        this.priceWood = priceWood;
    }

    @Override
    public void interactInventory(Player player, int slot) {

    }

    @Override
    public void upgrade(Player player) {
        PlayerData playerData = PlayerData.getUsers().get(player.getUniqueId());
        WoodRepositoryImpl woodRepository = playerData.getWoodRepository();
        StoneRepositoryImpl stoneRepository = playerData.getStoneRepository();
        if (woodRepository.getWood() >= priceWood && stoneRepository.getStone() >= priceStone) {
            woodRepository.delWood(priceWood);
            stoneRepository.delStone(priceStone);
            priceWood *= 1.8;
            priceStone *= 1.8;
            level++;
        } else player.sendMessage("Не хватает ресурсов для апгрейда здания");
    }

    @Override
    public void interactClearArea(Player player, int slot) {
        PlayerData data = PlayerData.getUsers().get(player.getUniqueId());
        MoneyRepositoryImpl moneyRepository = data.getMoneyRepository();
        switch (slot) {
            case 3 -> {
                if (moneyRepository.getMoney() >= clearAreaPrice) {
                    moneyRepository.delMoney(clearAreaPrice);
                    setClearArea(true);
                    player.sendMessage("Вы успешно расчистили территорию.");
                    onInteract(player);
                } else {
                    player.sendMessage("У вас недостаточно монет для расчистки территории.");
                }
            }
            case 5 -> {
                player.sendMessage("Вы отменили расчистку территории.");
                openInventory(player);
            }
            default -> {
            }
        }
    }

    @Override
    public abstract void onInteract(Player player);

    @Override
    public void spawnObstacles(Location target) {
        Location sourceMin = new Location(MapService.getWorld(), -66, 63, 483);
        Location sourceMax = new Location(MapService.getWorld(), -76, 68, 493);
        new BlockUtil().copyPaste(sourceMin, sourceMax, target);
    }

    @Override
    public void removeObstacles(Location target) {
        Location sourceMin = new Location(MapService.getWorld(), -78, 63, 483);
        Location sourceMax = new Location(MapService.getWorld(), -88, 68, 493);
        new BlockUtil().copyPaste(sourceMin, sourceMax, target);
    }
}
