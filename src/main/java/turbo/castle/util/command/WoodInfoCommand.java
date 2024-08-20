package turbo.castle.util.command;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.springframework.stereotype.Component;
import turbo.castle.currency.wood.repository.WoodRepositoryImpl;
import turbo.castle.data.PlayerData;
import turbo.castle.register.SubCommand;

import java.util.UUID;

@Component
public class WoodInfoCommand implements CommandExecutor {

    @Override
    @SubCommand("wood")
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("wood")) {
            if (commandSender instanceof Player player) {
                UUID uuid = player.getUniqueId();
                PlayerData data = PlayerData.getUsers().get(uuid);

                WoodRepositoryImpl woodRepository = data.getWoodRepository();
                player.sendMessage(String.valueOf(woodRepository.getWood()));
            }
            return true;
        }
        return false;
    }
}