package turbo.castle.currency.wood.repository;

import turbo.castle.currency.wood.WoodInfo;

public class WoodRepositoryImpl implements WoodRepository {
    WoodInfo woodInfo = new WoodInfo();

    @Override
    public void setWood(int wood) {
        woodInfo.setWood(wood);
    }

    @Override
    public void addWood(int wood) {
        woodInfo.setWood(woodInfo.getWood() + wood);
    }

    @Override
    public void delWood(int wood) {
        woodInfo.setWood(woodInfo.getWood() - wood);
    }

    @Override
    public int getWood() {
        return woodInfo.getWood();
    }
}
