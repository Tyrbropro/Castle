package turbo.castle.data;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import turbo.castle.currency.stone.repository.StoneRepositoryImpl;
import turbo.castle.currency.wood.repository.WoodRepositoryImpl;

import java.util.HashMap;
import java.util.UUID;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PlayerData {

    @Getter
    static HashMap<UUID, PlayerData> users = new HashMap<>();

    UUID uuid;
    WoodRepositoryImpl woodRepository;
    StoneRepositoryImpl stoneRepository;

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
        this.woodRepository = new WoodRepositoryImpl();
        this.stoneRepository = new StoneRepositoryImpl();

        users.put(uuid, this);
    }
}
