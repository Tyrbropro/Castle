package turbo.castle.gameplay.village.types;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.springframework.stereotype.Component;
import turbo.castle.currency.stone.repository.StoneRepositoryImpl;
import turbo.castle.currency.wood.repository.WoodRepositoryImpl;
import turbo.castle.data.PlayerData;
import turbo.castle.gameplay.village.AbstractBuilding;
import turbo.castle.util.MapService;

import java.util.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Component
@Setter
@Getter
public class BlackSmith extends AbstractBuilding {

    Map<String, Integer> upgradeLevels = new HashMap<>();
    Map<String, ItemStack> upgradedItems = new HashMap<>();

    public BlackSmith() {
        super("Blacksmith",
                new Location(MapService.getWorld(), -74, 64, 442),
                1000,
                1000);

        upgradeLevels.put("sword", 0);
        upgradeLevels.put("pickaxe", 0);
        upgradeLevels.put("axe", 0);
        upgradeLevels.put("armor", 0);

        upgradedItems.put("sword", new ItemStack(Material.WOOD_SWORD));
        upgradedItems.put("pickaxe", new ItemStack(Material.WOOD_PICKAXE));
        upgradedItems.put("axe", new ItemStack(Material.WOOD_AXE));
        upgradedItems.put("armor", null);

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
            case 0 -> infoNextLvl = "инструменты до камня";
            case 1 -> infoNextLvl = "инструменты до железа";
            case 2 -> infoNextLvl = "инструменты до алмаза";
            default -> infoNextLvl = "не придумал";
        }
        setInfoNextLvl(infoNextLvl);
        openInventory(player);
    }

    @Override
    public void openInventory(Player player) {
        Inventory blacksmithInventory = Bukkit.createInventory(null, 9, "Blacksmith");

        ItemStack infoItem = new ItemStack(Material.PAPER);
        ItemMeta infoMeta = infoItem.getItemMeta();
        infoMeta.setDisplayName("Информация");
        infoMeta.setLore(Collections.singletonList(info()));
        infoItem.setItemMeta(infoMeta);
        blacksmithInventory.setItem(0, infoItem);

        ItemStack upgradeItem = new ItemStack(Material.ANVIL);
        ItemMeta upgradeMeta = upgradeItem.getItemMeta();
        upgradeMeta.setDisplayName("Прокачать");
        upgradeItem.setItemMeta(upgradeMeta);
        blacksmithInventory.setItem(1, upgradeItem);

        blacksmithInventory.setItem(2, getItemStackForUpgrade("sword", Material.DIAMOND_SWORD, "Прокачать меч"));
        blacksmithInventory.setItem(3, getItemStackForUpgrade("pickaxe", Material.DIAMOND_PICKAXE, "Прокачать кирку"));
        blacksmithInventory.setItem(4, getItemStackForUpgrade("axe", Material.DIAMOND_AXE, "Прокачать топор"));
        blacksmithInventory.setItem(5, getItemStackForUpgrade("armor", Material.DIAMOND_CHESTPLATE, "Прокачать броню"));

        player.openInventory(blacksmithInventory);
    }

    private ItemStack getItemStackForUpgrade(String type, Material material, String displayName) {
        int level = upgradeLevels.get(type);
        int woodCost = getUpgradeCost(level);
        int stoneCost = getUpgradeCost(level);

        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(displayName + " (Уровень " + level + ")");
        itemMeta.setLore(Arrays.asList(
                "Текущая цена улучшения:",
                "Дерево: " + woodCost,
                "Камень: " + stoneCost
        ));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    private int getUpgradeCost(int level) {
        return switch (level) {
            case 0 -> 200;
            case 1 -> 400;
            case 2 -> 800;
            default -> 0;
        };
    }

    @Override
    public void interactInventory(Player player, int slot) {
        switch (slot) {
            case 0 -> player.sendMessage("Это кузница. Здесь вы можете улучшать свои инструменты и броню.");
            case 1 -> upgrade(player);
            case 2 -> upgradeItem("sword", player);
            case 3 -> upgradeItem("pickaxe", player);
            case 4 -> upgradeItem("axe", player);
            case 5 -> upgradeItem("armor", player);
            default -> {
            }
        }
        openInventory(player);
    }

    private void upgradeItem(String type, Player player) {
        int currentLevel = upgradeLevels.get(type);
        PlayerData data = PlayerData.getUsers().get(player.getUniqueId());
        WoodRepositoryImpl woodRepository = data.getWoodRepository();
        StoneRepositoryImpl stoneRepository = data.getStoneRepository();

        int woodCost = getUpgradeCost(currentLevel);
        int stoneCost = getUpgradeCost(currentLevel);

        if (woodRepository.getWood() >= woodCost && stoneRepository.getStone() >= stoneCost) {
            woodRepository.setWood(woodRepository.getWood() - woodCost);
            stoneRepository.setStone(stoneRepository.getStone() - stoneCost);

            Material upgradedMaterial = switch (currentLevel) {
                case 0 -> switch (type) {
                    case "sword" -> Material.STONE_SWORD;
                    case "pickaxe" -> Material.STONE_PICKAXE;
                    case "axe" -> Material.STONE_AXE;
                    case "armor" -> {
                        upgradedItems.put("helmet", new ItemStack(Material.LEATHER_HELMET));
                        upgradedItems.put("leggings", new ItemStack(Material.LEATHER_LEGGINGS));
                        upgradedItems.put("boots", new ItemStack(Material.LEATHER_BOOTS));
                        yield Material.LEATHER_CHESTPLATE;
                    }
                    default -> Material.AIR;
                };
                case 1 -> switch (type) {
                    case "sword" -> Material.IRON_SWORD;
                    case "pickaxe" -> Material.IRON_PICKAXE;
                    case "axe" -> Material.IRON_AXE;
                    case "armor" -> {
                        upgradedItems.put("helmet", new ItemStack(Material.IRON_HELMET));
                        upgradedItems.put("leggings", new ItemStack(Material.IRON_LEGGINGS));
                        upgradedItems.put("boots", new ItemStack(Material.IRON_BOOTS));
                        yield Material.IRON_CHESTPLATE;
                    }
                    default -> Material.AIR;
                };
                case 2 -> switch (type) {
                    case "sword" -> Material.DIAMOND_SWORD;
                    case "pickaxe" -> Material.DIAMOND_PICKAXE;
                    case "axe" -> Material.DIAMOND_AXE;
                    case "armor" -> {
                        upgradedItems.put("helmet", new ItemStack(Material.DIAMOND_HELMET));
                        upgradedItems.put("leggings", new ItemStack(Material.DIAMOND_LEGGINGS));
                        upgradedItems.put("boots", new ItemStack(Material.DIAMOND_BOOTS));
                        yield Material.DIAMOND_CHESTPLATE;
                    }
                    default -> Material.AIR;
                };
                default -> Material.AIR;
            };

            ItemStack upgradedItem = new ItemStack(upgradedMaterial);
            ItemMeta itemMeta = upgradedItem.getItemMeta();
            itemMeta.setDisplayName(type.substring(0, 1).toUpperCase() + type.substring(1) + " (Уровень " + (currentLevel + 1) + ")");
            itemMeta.setUnbreakable(true);
            upgradedItem.setItemMeta(itemMeta);

            upgradedItems.put(type, upgradedItem);
            upgradeLevels.put(type, currentLevel + 1);
            player.sendMessage(type.substring(0, 1).toUpperCase() + type.substring(1) + " был(а) успешно улучшен(а) до уровня " + (currentLevel + 1));
        } else {
            player.sendMessage("У вас недостаточно ресурсов для улучшения " + type + ".");
        }
    }


    public ItemStack getSword() {
        return upgradedItems.get("sword");
    }

    public ItemStack getPickaxe() {
        return upgradedItems.get("pickaxe");
    }

    public ItemStack getAxe() {
        return upgradedItems.get("axe");
    }

    public ItemStack getHelmet() {
        return upgradedItems.get("helmet");
    }

    public ItemStack getChestplate() {
        return upgradedItems.get("armor");
    }

    public ItemStack getLeggings() {
        return upgradedItems.get("leggings");
    }

    public ItemStack getBoots() {
        return upgradedItems.get("boots");
    }

    public ItemStack[] getArmorSet() {
        return new ItemStack[]{
                getHelmet(),
                getChestplate(),
                getLeggings(),
                getBoots()
        };
    }
}
