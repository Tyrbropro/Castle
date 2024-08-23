package turbo.castle;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import turbo.castle.config.Config;
import turbo.castle.data.DataBase;
import turbo.castle.data.PlayerData;
import turbo.castle.gameplay.village.BuildingManager;
import turbo.castle.gameplay.village.types.BlackSmith;
import turbo.castle.register.CommandService;
import turbo.castle.register.CommandServiceImpl;
import turbo.castle.register.ListenerService;
import turbo.castle.register.ListenerServiceImpl;
import turbo.castle.util.MapService;

@FieldDefaults(level = AccessLevel.PRIVATE)
public final class Castle extends JavaPlugin {

    static Castle plugin;
    static ConfigurableApplicationContext context;

    @Override
    public void onEnable() {
        plugin = this;

        World world = MapService.getWorld();
        if (world != null) {
            context = new AnnotationConfigApplicationContext(Config.class);

            DataBase.loadDatabaseConfig();

            ListenerService listenerService = context.getBean(ListenerServiceImpl.class);
            listenerService.scanPackages(this.getClass().getPackage().getName());

            CommandService commandService = context.getBean(CommandServiceImpl.class);
            commandService.scanPackage(this.getClass().getPackage().getName());

        } else Bukkit.shutdown();
    }

    @Override
    public void onDisable() {
        for (PlayerData playerData : PlayerData.getUsers().values()) {
            BuildingManager buildingManager = playerData.getBuildingManager();
            BlackSmith blackSmith = (BlackSmith) buildingManager.getBuildingByName("Blacksmith");
            if (blackSmith == null) playerData.saveToMongoDB();
            else {
                playerData.setUpgradedItems(blackSmith.getUpgradedItems());
                playerData.setUpgradeLevels(blackSmith.getUpgradeLevels());

                playerData.saveToMongoDB();
            }
        }
        if (context != null) {
            context.close();
        }
    }

    public static Castle getPlugin() {
        return plugin;
    }

    public static ConfigurableApplicationContext getContext() {
        return context;
    }
}
