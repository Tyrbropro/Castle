package turbo.castle.gameplay.stone;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.springframework.stereotype.Component;
import turbo.castle.config.StoneConfig;
import turbo.castle.currency.stone.repository.StoneRepositoryImpl;
import turbo.castle.data.PlayerData;
import turbo.castle.util.BlockUtil;

import java.util.UUID;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MiningStone {
    StoneConfig stoneConfig;
    BlockUtil blockUtil;

    public MiningStone(StoneConfig stoneConfig, BlockUtil blockUtil) {
        this.stoneConfig = stoneConfig;
        this.blockUtil = blockUtil;
    }

    public void mining(Player player, Block block) {
        UUID uuid = player.getUniqueId();
        PlayerData data = PlayerData.getUsers().get(uuid);
        StoneRepositoryImpl stoneRepository = data.getStoneRepository();

        Stone stone = stoneConfig.getBlockStone().get(block.getLocation());
        if (stone == null) return;

        int stoneSize = blockUtil.countTreeBlocks(block);
        stoneConfig.getStoneSizes().putIfAbsent(block, stoneSize);

        int hits = stoneConfig.getStoneHits().merge(stone, 1, Integer::sum);

        if (hits >= stoneSize) {
            removeStone(stone);

            stoneRepository.addStone((stoneSize * 50) * data.getMultiplierStone());
            if(stoneRepository.getStone() >= data.getMaxStone()){
                stoneRepository.setStone(data.getMaxStone());
                player.sendMessage("У вас максимум камня");
            }
            player.sendMessage("Вы разрушили камни!");
        } else {
            player.sendMessage(String.format("Вы ударили по камням %d раз(а). Всего блоков: %d", hits, stoneSize));
        }
    }

    private void removeStone(Stone stone) {
        stoneConfig.getMinedStone().remove(stone.location());
        stoneConfig.getStoneHits().remove(stone);

        final Location sourceMin = stone.location().clone().add(0, -5, 0);
        final Location sourceMax = stone.location().clone().add(-4, -4, -4);
        blockUtil.iterateBlocks(sourceMin, sourceMax, (block, blockLoc) -> block.setType(Material.AIR));
    }
}
