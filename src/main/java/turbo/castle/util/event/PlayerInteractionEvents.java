package turbo.castle.util.event;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import turbo.castle.gameplay.stone.MiningStone;
import turbo.castle.gameplay.tree.TreeFelling;

import java.util.HashMap;
import java.util.Map;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PlayerInteractionEvents implements Listener {

    TreeFelling treeFelling;
    MiningStone miningStone;
    Map<Player, Long> cooldown = new HashMap<>();


    @Autowired
    public PlayerInteractionEvents(TreeFelling treeFelling, MiningStone miningStone) {
        this.treeFelling = treeFelling;
        this.miningStone = miningStone;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        event.setCancelled(false);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        Player player = event.getPlayer();
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            fellingCooldown(player, block);

        }
    }

    private void fellingCooldown(Player player, Block block) {
        long currentTime = System.currentTimeMillis();
        if (cooldown.containsKey(player)) {
            long lastUse = cooldown.get(player);

            long cooldownTime = 1000;
            if (currentTime - lastUse < cooldownTime) return;
        }
        cooldown.put(player, currentTime);
        mining(player, block);
    }

    private void mining(Player player, Block block) {
        if (block != null && block.getType() == Material.COBBLESTONE) {
            miningStone.mining(player, block);
            player.playSound(player.getLocation(), Sound.BLOCK_STONE_BREAK, 5.0f, 1.0f);
            player.spawnParticle(Particle.CRIT, block.getLocation().add(0.5, 0.5, 0.5), 20);
        } else if (block != null && block.getType() == Material.LOG) {
            treeFelling.felling(player, block);
            player.playSound(player.getLocation(), Sound.BLOCK_WOOD_BREAK, 5.0f, 1.0f);
            player.spawnParticle(Particle.CRIT, block.getLocation().add(0.5, 0.5, 0.5), 20);
        }
    }
}
