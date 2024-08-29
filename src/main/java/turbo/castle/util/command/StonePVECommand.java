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
public class StonePVECommand implements CommandExecutor {

    Location start = new Location(MapService.getWorld(), -4.5, 64, 384.5);

    @Override
    @SubCommand("stonePVE")
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("stonePVE")) {
            if (commandSender instanceof Player player) {
                givePickaxe(player);
                player.teleport(start);
            }
            return true;
        }
        return false;
    }

    private void givePickaxe(Player player) {
        player.getInventory().clear();

        BuildingManager buildingManager = new BuildingManager(player.getUniqueId());
        BlackSmith building = (BlackSmith) buildingManager.getBuildingByName("Blacksmith");
        if (building == null) {
            player.getInventory().addItem(new ItemStack(Material.WOOD_PICKAXE));
        } else {
            int multiplier = building.getUpgradeLevels().get("pickaxe") + 1;
            PlayerData data = PlayerData.getUsers().get(player.getUniqueId());
            data.setMultiplierStone(multiplier + 1);
            player.getInventory().addItem(building.getPickaxe());
        }
    }
}
