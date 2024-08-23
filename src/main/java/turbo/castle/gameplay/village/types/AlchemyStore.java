package turbo.castle.gameplay.village.types;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;
import turbo.castle.currency.stone.repository.StoneRepositoryImpl;
import turbo.castle.currency.wood.repository.WoodRepositoryImpl;
import turbo.castle.data.PlayerData;
import turbo.castle.gameplay.village.AbstractBuilding;
import turbo.castle.util.MapService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AlchemyStore extends AbstractBuilding {


    public AlchemyStore() {
        super("AlchemyStore",
                new Location(MapService.getWorld(), -82 , 64 , 460),
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
        if (getLevel() == 0) {
            infoNextLvl = "Открывается магазин с зельями";
        } else {
            infoNextLvl = "Больше выбора зелий";
        }
        setInfoNextLvl(infoNextLvl);
        openInventory(player);
    }

    @Override
    public void openInventory(Player player) {
        Inventory storageInventory = Bukkit.createInventory(null, 9, "AlchemyStore");

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

        if (getLevel() != 0) {
            ItemStack shopItem = new ItemStack(Material.POTION);
            ItemMeta shopMeta = shopItem.getItemMeta();
            shopMeta.setDisplayName("Магазин");
            shopItem.setItemMeta(shopMeta);
            storageInventory.setItem(2, shopItem);
        }

        player.openInventory(storageInventory);
    }


    @Override
    public void interactInventory(Player player, int slot) {
        switch (slot) {
            case 0 -> player.sendMessage("Это Алхимическая Лавка. Здесь можно покупать Зелья.");
            case 1 -> {
                upgrade(player);
                openInventory(player);
            }
            case 2 -> openShop(player);

            default -> {
            }
        }
    }

    private void openShop(Player player) {
        if (getLevel() == 0) return;

        Inventory shopInventory = Bukkit.createInventory(null, 9, "AlchemyShop");

        PotionType[] potionTypes = new PotionType[]{
                PotionType.SPEED, PotionType.STRENGTH, PotionType.FIRE_RESISTANCE
        };

        int[][] potionDurations = new int[][]{
                {600, 600, 600},
                {1800, 1800, 1800},
                {1800, 1800, 1800}
        };

        int[][] potionLevels = new int[][]{
                {1, 1, 1},
                {1, 1, 1},
                {2, 2, 2}
        };

        int[] prices = new int[]{100, 200, 300};

        int level = getLevel();

        for (int i = 0; i < potionTypes.length; i++) {
            ItemStack potionItem = new ItemStack(Material.POTION);
            PotionMeta meta = (PotionMeta) potionItem.getItemMeta();

            meta.setBasePotionData(new PotionData(potionTypes[i], false, potionLevels[level - 1][i] > 1));

            PotionEffect potionEffect = new PotionEffect(potionTypes[i].getEffectType(), potionDurations[level - 1][i], potionLevels[level - 1][i] - 1);
            meta.addCustomEffect(potionEffect, true);

            meta.setDisplayName(potionTypes[i].name().replace("_", " ").toLowerCase());

            meta.setLore(Arrays.asList(
                    "Цена: " + prices[i] + " монет",
                    "Длительность: " + (potionDurations[level - 1][i] / 20) + " секунд",
                    "Уровень эффекта: " + potionLevels[level - 1][i],
                    "Зелье выдается когда волна начинается , после проигрыша зелья проподает"
            ));

            potionItem.setItemMeta(meta);
            shopInventory.addItem(potionItem);
        }

        player.openInventory(shopInventory);
    }



    public void interactShop(Player player, int slot) {
        Inventory inventory = player.getOpenInventory().getTopInventory();
        ItemStack clickedItem = inventory.getItem(slot);
        if (clickedItem == null || !clickedItem.hasItemMeta()) return;

        ItemMeta meta = clickedItem.getItemMeta();
        List<String> lore = meta.getLore();
        if (lore == null || lore.size() < 2) return;

        int price = Integer.parseInt(lore.get(0).split(" ")[1]);

        PlayerData playerData = PlayerData.getUsers().get(player.getUniqueId());
        WoodRepositoryImpl woodRepository = playerData.getWoodRepository();
        StoneRepositoryImpl stoneRepository = playerData.getStoneRepository();
        if (woodRepository.getWood() >= price && stoneRepository.getStone() >= price) {
            woodRepository.delWood(price);
            stoneRepository.delStone(price);

            playerData.addPurchasedPotion(new ItemStack(clickedItem));
            player.sendMessage("Вы купили " + clickedItem.getItemMeta().getDisplayName() + " за " + price);
        } else {
            player.sendMessage("У вас недостаточно ресурсов для покупки.");
        }
    }
}
