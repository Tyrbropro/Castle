package turbo.castle.util.command;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.Location;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.springframework.stereotype.Component;
import turbo.castle.gameplay.wave.SpawnWave;
import turbo.castle.gameplay.wave.WaveManager;
import turbo.castle.register.SubCommand;
import turbo.castle.util.MapService;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component
public class StonePVPCommand implements CommandExecutor {

    Location start = new Location(MapService.getWorld(), -147.5, 64, 445.5);
    SpawnWave spawnWave;
    WaveManager waveManager;

    public StonePVPCommand(SpawnWave spawnWave, WaveManager waveManager) {
        this.waveManager = waveManager;
        this.spawnWave = spawnWave;
    }

    @Override
    @SubCommand("stonePVP")
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("stonePVP")) {
            if (commandSender instanceof Player player) {
                if (waveManager.isWoodPVP() || waveManager.isStonePVP()) {
                    player.sendMessage("Ждите идет битва");
                } else {
                    waveManager.setStonePVP(true);
                    player.teleport(start);

                    spawnWave.setCurrentPlayer(player);
                    spawnWave.startNextWave();
                }
            }
            return true;
        }
        return false;
    }
}
