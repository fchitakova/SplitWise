package splitwise.server.model;


import java.util.List;

public interface Friendship {
     Double NEUTRAL_DEBT = 0.0;
     String SHOULD_TAKE_MONEY = "You owe";
     String SHOULD_GIVE_MONEY = "Owes you";

     void split(Double amount);

     void payOff(String username, Double amount);

     String getName();

     String getStatus();

     List<String> getMembersUsernames();
}