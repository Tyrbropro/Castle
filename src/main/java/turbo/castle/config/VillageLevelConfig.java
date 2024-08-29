package turbo.castle.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VillageLevelConfig {

    private int xp = 0;

    public void addXp(int amount) {
        this.xp += amount;
    }
}
