package turbo.castle.util.command;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import turbo.castle.gameplay.stone.SpawnStone;
import turbo.castle.register.SubCommand;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SpawnStoneCommand implements CommandExecutor {

    SpawnStone spawnStone;

    @Autowired
    public SpawnStoneCommand(SpawnStone spawnStone) {
        this.spawnStone = spawnStone;
    }

    @Override
    @SubCommand("spawnStone")
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("spawnStone")) {
            if (commandSender instanceof Player) {
                spawnStone.spawn();
            }
            return true;
        }
        return false;
    }
}
