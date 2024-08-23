package turbo.castle.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import turbo.castle.gameplay.wave.SpawnWave;
import turbo.castle.gameplay.wave.WaveManager;
import turbo.castle.gameplay.wave.event.EventManager;
import turbo.castle.gameplay.wave.mob.*;
import turbo.castle.gameplay.wave.mob.types.SlowZombie;
import turbo.castle.gameplay.stone.MiningStone;
import turbo.castle.gameplay.stone.SpawnStone;
import turbo.castle.gameplay.tree.TreeFelling;
import turbo.castle.gameplay.tree.TreeGrowth;
import turbo.castle.register.CommandServiceImpl;
import turbo.castle.register.ListenerServiceImpl;
import turbo.castle.util.BlockUtil;
import turbo.castle.util.MapService;
import turbo.castle.util.command.*;
import turbo.castle.util.event.*;

@Configuration
@AllArgsConstructor
public class Config {

    @Bean
    public BlockUtil blockUtil() {
        return new BlockUtil();
    }

    @Bean
    public MapService mapService() {
        return new MapService();
    }

    @Bean
    public StartCommand startCommand() {
        return new StartCommand();
    }

    @Bean
    public ListenerServiceImpl listenerService() {
        return new ListenerServiceImpl();
    }

    @Bean
    public CommandServiceImpl commandService() {
        return new CommandServiceImpl();
    }

    @Bean
    public WoodInfoCommand woodInfoCommand() {
        return new WoodInfoCommand();
    }

    @Bean
    public StoneConfig stoneConfig() {
        return new StoneConfig();
    }

    @Bean
    public TreeConfig treeConfig() {
        return new TreeConfig();
    }

    @Bean
    public TreeFelling treeFelling() {
        return new TreeFelling(treeConfig(), blockUtil());
    }

    @Bean
    public MiningStone miningStone() {
        return new MiningStone(stoneConfig(), blockUtil());
    }

    @Bean
    public TreeGrowth treeGrowth() {
        return new TreeGrowth(treeConfig(), blockUtil());
    }

    @Bean
    public SpawnStone spawnStone() {
        return new SpawnStone(stoneConfig(), blockUtil());
    }

    @Bean
    public PlayerJoinQuitEvents playerJoinQuitEvents() {
        return new PlayerJoinQuitEvents(treeGrowth(), spawnStone());
    }

    @Bean
    public PlayerInteractionEvents playerInteractionEvents() {
        return new PlayerInteractionEvents(treeFelling(), miningStone());
    }

    @Bean
    public StoneInfoCommand stoneInfoCommand() {
        return new StoneInfoCommand();
    }

    @Bean
    public WaveManager waveManager() {
        return new WaveManager();
    }

    @Bean
    public CustomMobFactory customMobFactory() {
        return new CustomMobFactory();
    }

    @Bean
    public SpawnWave spawnWave() {
        return new SpawnWave(customMobFactory(), eventManager(), waveManager(), blockUtil());
    }

    @Bean
    public PlayerDamageEvents playerDamageEvents() {
        return new PlayerDamageEvents(spawnWave());
    }

    @Bean
    public SlowZombie slowZombie() {
        return new SlowZombie(waveManager());
    }

    @Bean
    public MobHitListener mobHitListener() {
        return new MobHitListener(customMobFactory());
    }

    @Bean
    public StonePVPCommand stonePVPCommand() {
        return new StonePVPCommand(spawnWave(), waveManager());
    }

    @Bean
    public WoodPVPCommand woodPVPCommand() {
        return new WoodPVPCommand(spawnWave(), waveManager());
    }

    @Bean
    public MobListener mobListener() {
        return new MobListener(spawnWave());
    }

    @Bean
    public EventManager eventManager() {
        return new EventManager(waveManager());
    }

    @Bean
    public TestCommand testCommand() {
        return new TestCommand();
    }

    @Bean
    public EntityEvents entityEvents() {
        return new EntityEvents(customMobFactory());
    }

    @Bean
    public StonePVECommand stonePVECommand() {
        return new StonePVECommand();
    }

    @Bean
    public WoodPVECommand woodPVECommand() {
        return new WoodPVECommand();
    }
}