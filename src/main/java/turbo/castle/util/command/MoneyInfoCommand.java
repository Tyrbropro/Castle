package turbo.castle.util.command;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.springframework.stereotype.Component;
import turbo.castle.currency.money.repository.MoneyRepositoryImpl;
import turbo.castle.currency.wood.repository.WoodRepositoryImpl;
import turbo.castle.data.PlayerData;
import turbo.castle.register.SubCommand;

import java.util.UUID;

@Component
public class MoneyInfoCommand implements CommandExecutor {

    @Override
    @SubCommand("money")
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("money")) {
            if (commandSender instanceof Player player) {
                UUID uuid = player.getUniqueId();
                PlayerData data = PlayerData.getUsers().get(uuid);

                MoneyRepositoryImpl moneyRepository = data.getMoneyRepository();
                player.sendMessage(String.valueOf(moneyRepository.getMoney()));
            }
            return true;
        }
        return false;
    }
}
