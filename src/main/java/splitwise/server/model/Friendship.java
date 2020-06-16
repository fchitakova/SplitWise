package splitwise.server.model;


public interface Friendship {
     Double NEUTRAL_DEBT = 0.0;
     String SHOULD_TAKE_MONEY = "You owe";
     String SHOULD_GIVE_MONEY = "Owes you";

     void addToAccount(Double amount);

     String getName();

     String getStatus();

}