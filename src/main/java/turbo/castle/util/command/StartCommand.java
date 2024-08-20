package turbo.castle.util.command;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.Location;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.springframework.stereotype.Component;
import turbo.castle.register.SubCommand;
import turbo.castle.util.MapService;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StartCommand implements CommandExecutor {

    Location start = new Location(MapService.getWorld(), -77.5, 64, 420.5);

    @Override
    @SubCommand("start")
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("start")) {
            if (commandSender instanceof Player player) {
                player.teleport(start);
            }
            return true;
        }
        return false;
    }
}
