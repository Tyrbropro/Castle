package turbo.castle.gameplay.village;

import org.bson.Document;

public interface SavableBuilding {
    Document saveData();

    void loadData(Document document);
}
