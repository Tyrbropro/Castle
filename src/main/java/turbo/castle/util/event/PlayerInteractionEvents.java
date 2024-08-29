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
import turbo.castle.gameplay.village.types.BlackSmith;
import turbo.castle.gameplay.village.types.FarmerStore;
import turbo.castle.gameplay.village.types.Storage;

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
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        Block block = event.getClickedBlock();

        if (isCooldownActive(player)) return;

        healFeed(event, player, item);

        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            mining(player, block);
        } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            BuildingManager buildingManager = new BuildingManager(player.getUniqueId());
            buildingManager.interactWithBuilding(player, block.getLocation());
        }

        updateCooldown(player);
    }

    private boolean isCooldownActive(Player player) {
        long currentTime = System.currentTimeMillis();
        return cooldown.containsKey(player) && currentTime - cooldown.get(player) < 1000;
    }

    private void updateCooldown(Player player) {
        cooldown.put(player, System.currentTimeMillis());
    }

    private void healFeed(PlayerInteractEvent event, Player player, ItemStack item) {
        if (item != null && item.hasItemMeta() && item.getItemMeta().hasLore()) {
            for (String lore : item.getItemMeta().getLore()) {
                if (lore.startsWith("Восстанавливает: ")) {
                    int healing = Integer.parseInt(lore.split(" ")[1]);
                    player.setHealth(Math.min(player.getHealth() + healing, player.getMaxHealth()));
                    player.sendMessage("Вы восстановили " + healing + " HP.");
                    item.setAmount(item.getAmount() - 1);
                    event.setCancelled(true);
                    break;
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

        String inventoryName = inventory.getName();
        Building building = buildingManager.getBuildingByName(inventoryName);
        int slot = event.getRawSlot();
        if (building != null) {
            if (inventoryName.equals("FarmerShop")) {
                ((FarmerStore) building).interactShop(player, slot);
            } else if (inventoryName.equals("AlchemyShop")) {
                ((AlchemyStore) building).interactShop(player, slot);
            } else {
                building.interactInventory(player, slot);
            }
            event.setCancelled(true);
        } else if (inventory.getName().equals("FarmerShop")) {
            FarmerStore farmerStore = (FarmerStore) buildingManager.getBuildingByName("FarmerStore");
            farmerStore.interactShop(player, slot);
            event.setCancelled(true);
        } else if (inventory.getName().equals("AlchemyShop")) {
            AlchemyStore alchemyStore = (AlchemyStore) buildingManager.getBuildingByName("AlchemyStore");
            alchemyStore.interactShop(player, slot);
            event.setCancelled(true);
        } else if (inventory.getName().equals("Подтвердить расчистку территории под Алхимический Магазин")) {
            AlchemyStore alchemyStore = (AlchemyStore) buildingManager.getBuildingByName("AlchemyStore");
            alchemyStore.interactClearArea(player, slot);
            event.setCancelled(true);
        } else if (inventory.getName().equals("Подтвердить расчистку территории под кузницу")) {
            BlackSmith blackSmith = (BlackSmith) buildingManager.getBuildingByName("Blacksmith");
            blackSmith.interactClearArea(player, slot);
            event.setCancelled(true);
        } else if (inventory.getName().equals("Подтвердить расчистку территории под Фермерский магазин")) {
            FarmerStore farmerStore = (FarmerStore) buildingManager.getBuildingByName("FarmerStore");
            farmerStore.interactClearArea(player, slot);
            event.setCancelled(true);
        } else if (inventory.getName().equals("Подтвердить расчистку территории под склад")) {
            Storage storage = (Storage) buildingManager.getBuildingByName("Storage");
            storage.interactClearArea(player, slot);
            event.setCancelled(true);
        }
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
        if (block != null) {
            if (block.getType() == Material.COBBLESTONE) {
                miningStone.mining(player, block);
                playMiningEffects(player, block, Sound.BLOCK_STONE_BREAK);
            } else if (block.getType() == Material.LOG) {
                treeFelling.felling(player, block);
                playMiningEffects(player, block, Sound.BLOCK_WOOD_BREAK);
            }
        }
    }

    private void playMiningEffects(Player player, Block block, Sound sound) {
        player.getWorld().playSound(block.getLocation(), sound, 1.0f, 1.0f);
        player.getWorld().spawnParticle(Particle.BLOCK_CRACK, block.getLocation(), 10);
    }
}
