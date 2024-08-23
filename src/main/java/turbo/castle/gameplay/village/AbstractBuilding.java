package turbo.castle.gameplay.village;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import turbo.castle.currency.stone.repository.StoneRepositoryImpl;
import turbo.castle.currency.wood.repository.WoodRepositoryImpl;
import turbo.castle.data.PlayerData;

@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class AbstractBuilding implements Building {

    String name;
    String infoNextLvl;
    Location location;
    int level;
    int priceStone;
    int priceWood;

    public AbstractBuilding(String name, Location location, int priceStone, int priceWood) {
        this.name = name;
        this.location = location;
        this.level = 0;
        this.priceStone = priceStone;
        this.priceWood = priceWood;
    }

    @Override
    public String info() {
        return String.format(
                """
                        Информация о %s:
                        Уровень: %d
                        Цена улучшения (камень): %d
                        Цена улучшения (дерево): %d
                        На следущей уровне будет доступно:\s
                        %s""",

                name,
                level,
                priceStone,
                priceWood,
                infoNextLvl
        );
    }

    @Override
    public String getInfoNextLvl() {
        return infoNextLvl;
    }
    @Override
    public void openInventory(Player player){

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
    public void interactInventory(Player player, int slot){

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
    public abstract void onInteract(Player player);
}
