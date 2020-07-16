package splitwise.server.model;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class Friend implements Friendship, Serializable {
    public static final double NEUTRAL_ACCOUNT_AMOUNT = 0.0;
    private String name;
    private Double account;

    public Friend(String name){
        this.name = name;
        this.account = NEUTRAL_ACCOUNT_AMOUNT;
    }


    @Override
    public void split(Double amount) {
        account = account + amount / 2;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getStatus() {
        StringBuilder userStatus = new StringBuilder(getName() + ": ");
        if (account > 0.0) {
            userStatus.append(SHOULD_GIVE_MONEY + account + '\n');
        }
        if (account < 0.0) {
            userStatus.append(SHOULD_TAKE_MONEY + account + '\n');
        }
        if (account == 0.0) {
            userStatus.append(NEUTRAL_DEBT + '\n');
        }
        return userStatus.toString();
    }

    @Override
    public List<String> getFriendshipMembersUsernames() {
        return List.of(name);
    }

    public Double getAccount() {
        return account;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Friend)) {
            return false;
        }
        Friend friend = (Friend) o;
        return name.equals(friend.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
