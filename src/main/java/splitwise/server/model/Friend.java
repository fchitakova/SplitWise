package splitwise.server.model;

import java.io.Serializable;
import java.util.Objects;

public class Friend extends Friendship implements Serializable {
    public static final String SHOULD_TAKE_MONEY = "You owe %s %s LV ";
    public static final String SHOULD_GIVE_MONEY = "%s owes you %s LV";
    
    private Double account;
    
    public Friend(String name) {
	this.name = name;
	this.account = NEUTRAL_ACCOUNT_AMOUNT;
    }
    
    public Double getAccount() {
	return account;
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
	
	if(account>NEUTRAL_ACCOUNT_AMOUNT) {
	    userStatus.append(String.format(SHOULD_GIVE_MONEY, name, account));
	}
	if(account<NEUTRAL_ACCOUNT_AMOUNT) {
	    userStatus.append(String.format(SHOULD_TAKE_MONEY, name, -account));
	}
	
	return userStatus.toString();
    }
    
    @Override
    public boolean equals(Object o) {
	if(this==o) {
	    return true;
	}
	if(!(o instanceof Friend) || !super.equals(o)) {
	    return false;
	}
	Friend friend = (Friend) o;
	return Objects.equals(account, friend.account);
    }
    
    @Override
    public int hashCode() {
	return Objects.hash(super.hashCode(), account);
    }
}
