package turbo.castle.currency.wood.repository;

public interface WoodRepository {
    void setWood(int wood);

    void addWood(int wood);
    void delWood(int wood);

    int getWood();
}
