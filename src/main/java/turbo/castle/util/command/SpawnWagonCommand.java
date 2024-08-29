package turbo.castle.util.command;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.springframework.stereotype.Component;
import turbo.castle.gameplay.event.bandit.Wagon;
import turbo.castle.register.SubCommand;
import turbo.castle.util.BlockUtil;

@Component
public class SpawnWagonCommand implements CommandExecutor {

    @Override
    @SubCommand("wagon")
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("wagon")) {
            if (commandSender instanceof Player player) {
                new Wagon(new BlockUtil(), player);
            }
            return true;
        }
        return false;
    }
}
