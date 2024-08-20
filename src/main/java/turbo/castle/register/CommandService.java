package turbo.castle.register;

import org.bukkit.command.CommandExecutor;

public interface CommandService {
    void scanPackage(String packageName);

    void registerCommand(CommandExecutor command);
}
