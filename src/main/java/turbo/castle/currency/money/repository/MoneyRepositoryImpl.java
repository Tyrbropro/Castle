package turbo.castle.currency.money.repository;

import turbo.castle.currency.money.MoneyInfo;

public class MoneyRepositoryImpl implements MoneyRepository {

    MoneyInfo moneyInfo = new MoneyInfo();

    @Override
    public void setMoney(int money) {
        moneyInfo.setMoney(money);
    }

    @Override
    public void addMoney(int money) {
        moneyInfo.setMoney(moneyInfo.getMoney() + money);
    }

    @Override
    public void delMoney(int money) {
        moneyInfo.setMoney(moneyInfo.getMoney() - money);
    }

    @Override
    public int getMoney() {
        return moneyInfo.getMoney();
    }
}
