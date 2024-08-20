package turbo.castle.gameplay.tree;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.bukkit.Location;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Tree {

    int growthLevel = 0;
    boolean isBeingBroken;
    final Location location;

    public Tree(Location location) {
        this.location = location;
    }
}
