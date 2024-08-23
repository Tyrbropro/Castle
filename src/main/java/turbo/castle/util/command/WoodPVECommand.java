package turbo.castle.util.command;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.springframework.stereotype.Component;
import turbo.castle.data.PlayerData;
import turbo.castle.gameplay.village.BuildingManager;
import turbo.castle.gameplay.village.types.BlackSmith;
import turbo.castle.register.SubCommand;
import turbo.castle.util.MapService;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WoodPVECommand implements CommandExecutor {

    Location start = new Location(MapService.getWorld(), -2.5, 64, 461.5);

    @Override
    @SubCommand("woodPVE")
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("woodPVE")) {
            if (commandSender instanceof Player player) {
                giveAxe(player);
                player.teleport(start);
            }
            return true;
        }
        return false;
    }

    private void giveAxe(Player player) {
        player.getInventory().clear();

        BuildingManager buildingManager = new BuildingManager(player.getUniqueId());
        BlackSmith building = (BlackSmith) buildingManager.getBuildingByName("Blacksmith");
        if (building == null) {
            player.getInventory().addItem(new ItemStack(Material.WOOD_AXE));
        } else {
            int multiplier = building.getUpgradeLevels().get("axe") + 1;
            PlayerData data = PlayerData.getUsers().get(player.getUniqueId());
            data.setMultiplierWood(multiplier);
            player.getInventory().addItem(building.getAxe());
        }
    }
}
