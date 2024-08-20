package turbo.castle.util.command;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.springframework.stereotype.Component;
import turbo.castle.currency.stone.repository.StoneRepositoryImpl;
import turbo.castle.data.PlayerData;
import turbo.castle.register.SubCommand;

import java.util.UUID;

@Component
public class StoneInfoCommand implements CommandExecutor {

    @Override
    @SubCommand("stone")
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("stone")) {
            if (commandSender instanceof Player player) {
                UUID uuid = player.getUniqueId();
                PlayerData data = PlayerData.getUsers().get(uuid);

                StoneRepositoryImpl stoneRepository = data.getStoneRepository();
                player.sendMessage(String.valueOf(stoneRepository.getStone()));
            }
            return true;
        }
        return false;
    }
}
