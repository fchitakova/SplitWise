package splitwise.server.model;


import java.util.Objects;

public abstract class Friendship {
     public static final double NEUTRAL_ACCOUNT_AMOUNT = 0.0;

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

     @Override
     public boolean equals(Object o) {
         if (this == o) {
             return true;
         }
         if (!(o instanceof Friendship)) {
             return false;
         }
         Friendship that = (Friendship) o;
         return Objects.equals(getName(), that.getName());
     }

     @Override
     public int hashCode() {
          return Objects.hash(getName());
     }
}