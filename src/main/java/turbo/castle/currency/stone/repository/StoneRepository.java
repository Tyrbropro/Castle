package turbo.castle.currency.stone.repository;

public interface StoneRepository {
    void setStone(int stone);

    void addStone(int stone);
    void delStone(int stone);

    int getStone();
}
