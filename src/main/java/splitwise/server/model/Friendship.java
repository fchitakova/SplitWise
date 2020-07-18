package splitwise.server.model;


public abstract class Friendship {
     protected String name;

     public abstract void split(Double amount);

     public abstract void payOff(String username, Double amount);

     public abstract String getStatus();

     public String getName() {
          return this.name;
     }

     public boolean hasName(String name) {
          return this.name.equalsIgnoreCase(name);
     }

}