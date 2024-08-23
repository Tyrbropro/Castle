package turbo.castle.util.event;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import turbo.castle.gameplay.stone.MiningStone;
import turbo.castle.gameplay.tree.TreeFelling;
import turbo.castle.gameplay.village.Building;
import turbo.castle.gameplay.village.BuildingManager;
import turbo.castle.gameplay.village.types.AlchemyStore;
import turbo.castle.gameplay.village.types.FarmerStore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PlayerInteractionEvents implements Listener {

    TreeFelling treeFelling;
    MiningStone miningStone;
    Map<Player, Long> cooldown = new HashMap<>();


    @Autowired
    public PlayerInteractionEvents(TreeFelling treeFelling, MiningStone miningStone) {
        this.treeFelling = treeFelling;
        this.miningStone = miningStone;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        event.setCancelled(false);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        ItemStack item = player.getInventory().getItemInMainHand();


        long currentTime = System.currentTimeMillis();
        if (cooldown.containsKey(player)) {
            long lastUse = cooldown.get(player);

            long cooldownTime = 1000;
            if (currentTime - lastUse < cooldownTime) return;
        }
        healFeed(event, player, item);
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            mining(player, block);
            cooldown.put(player, currentTime);
        } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            BuildingManager buildingManager = new BuildingManager(uuid);
            buildingManager.interactWithBuilding(player, block.getLocation());
            cooldown.put(player, currentTime);
        }
    }

    private void healFeed(PlayerInteractEvent event, Player player, ItemStack item) {
        if (item != null && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta.hasLore()) {
                for (String lore : meta.getLore()) {
                    if (lore.startsWith("Восстанавливает: ")) {
                        int healing = Integer.parseInt(lore.split(" ")[1]);

                        double newHealth = Math.min(player.getHealth() + healing, player.getMaxHealth());
                        player.setHealth(newHealth);
                        player.sendMessage("Вы восстановили " + healing + " HP.");
                        item.setAmount(item.getAmount() - 1);
                        event.setCancelled(true);
                        break;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        Player player = (Player) event.getWhoClicked();
        UUID uuid = player.getUniqueId();
        BuildingManager buildingManager = new BuildingManager(uuid);
        if (inventory.getName().equals("Town Hall")) {
            Building building = buildingManager.getBuildingByName("Town Hall");
            int slot = event.getRawSlot();
            building.interactInventory(player, slot);
        } else if (inventory.getName().equals("Blacksmith")) {
            Building building = buildingManager.getBuildingByName("Blacksmith");
            int slot = event.getRawSlot();
            building.interactInventory(player, slot);
        } else if (inventory.getName().equals("Storage")) {
            Building building = buildingManager.getBuildingByName("Storage");
            int slot = event.getRawSlot();
            building.interactInventory(player, slot);
        } else if (inventory.getName().equals("FarmerStore")) {
            Building building = buildingManager.getBuildingByName("FarmerStore");
            int slot = event.getRawSlot();
            building.interactInventory(player, slot);
        } else if (inventory.getName().equals("FarmerShop")) {
            FarmerStore building = (FarmerStore) buildingManager.getBuildingByName("FarmerStore");
            int slot = event.getRawSlot();
            building.interactShop(player, slot);
        }else if (inventory.getName().equals("AlchemyStore")) {
            Building building = buildingManager.getBuildingByName("AlchemyStore");
            int slot = event.getRawSlot();
            building.interactInventory(player, slot);
        } else if (inventory.getName().equals("AlchemyShop")) {
            AlchemyStore building = (AlchemyStore) buildingManager.getBuildingByName("AlchemyStore");
            int slot = event.getRawSlot();
            building.interactShop(player, slot);
        }
        event.setCancelled(true);
    }


    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player player) {

            player.setFoodLevel(20);
            player.setSaturation(20.0f);

            event.setCancelled(true);
        }
    }

    private void mining(Player player, Block block) {
        if (block != null && block.getType() == Material.COBBLESTONE) {
            miningStone.mining(player, block);
            player.playSound(player.getLocation(), Sound.BLOCK_STONE_BREAK, 5.0f, 1.0f);
            player.spawnParticle(Particle.CRIT, block.getLocation().add(0.5, 0.5, 0.5), 20);
        } else if (block != null && block.getType() == Material.LOG) {
            treeFelling.felling(player, block);
            player.playSound(player.getLocation(), Sound.BLOCK_WOOD_BREAK, 5.0f, 1.0f);
            player.spawnParticle(Particle.CRIT, block.getLocation().add(0.5, 0.5, 0.5), 20);
        }
    }
}
