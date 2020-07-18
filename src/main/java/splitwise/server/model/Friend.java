package splitwise.server.model;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class Friend extends Friendship implements Serializable {
    public static final String SHOULD_TAKE_MONEY = "You owe %s %s LV ";
    public static final String SHOULD_GIVE_MONEY = "%s owes you %s LV";
    public static final double NEUTRAL_ACCOUNT_AMOUNT = 0.0;

    private Double account;

    public Friend(String name) {
        this.name = name;
        this.account = NEUTRAL_ACCOUNT_AMOUNT;
    }


    @Override
    public void split(Double amount) {
        account = account + amount;
    }

    @Override
    public void payOff(String username, Double amount) {
        account = account - amount;
    }


    @Override
    public String getStatus() {
        StringBuilder userStatus = new StringBuilder();

        if (account > 0.0) {
            userStatus.append(String.format(SHOULD_GIVE_MONEY, name, Double.toString(account)));
        }
        if (account < 0.0) {
            userStatus.append(String.format(SHOULD_TAKE_MONEY, name, Double.toString(-account)));
        }

        return userStatus.toString();
    }


    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Friend)) {
            return false;
        }
        Friend friend = (Friend) other;
        return name.equals(friend.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
