package turbo.castle.currency.money.repository;

public interface MoneyRepository {
    void setMoney(int money);

    void addMoney(int money);
    void delMoney(int money);

    int getMoney();
}
