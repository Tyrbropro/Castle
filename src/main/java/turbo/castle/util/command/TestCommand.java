package turbo.castle.util.command;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import turbo.castle.gameplay.wave.event.WaveEvent;
import turbo.castle.gameplay.wave.event.types.WallEvent;
import turbo.castle.register.SubCommand;

public class TestCommand implements CommandExecutor {

    @Override
    @SubCommand("test")
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("test")) {
            if (commandSender instanceof Player player) {
                WaveEvent event = new WallEvent(player);
                event.trigger();
            }
            return true;
        }
        return false;
    }
}
