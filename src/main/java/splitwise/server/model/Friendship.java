package splitwise.server.model;


import java.util.List;

public interface Friendship{

     void split(Double amount);

     void payOff(String username, Double amount);

     String getName();

     String getStatus();

     List<String> getMembersUsernames();
}