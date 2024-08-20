package turbo.castle.currency.stone.repository;

import turbo.castle.currency.stone.StoneInfo;

public class StoneRepositoryImpl implements StoneRepository {
    StoneInfo stoneInfo = new StoneInfo();

    @Override
    public void setStone(int stone) {
        stoneInfo.setStone(stone);
    }

    @Override
    public void addStone(int stone) {
        stoneInfo.setStone(stoneInfo.getStone() + stone);
    }

    @Override
    public void delStone(int stone) {
        stoneInfo.setStone(stoneInfo.getStone() - stone);
    }

    @Override
    public int getStone() {
        return stoneInfo.getStone();
    }
}
