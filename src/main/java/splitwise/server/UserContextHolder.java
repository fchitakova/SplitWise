package splitwise.server;


public class UserContextHolder {
    public static final String INITIAL_VALUE= "initial";

    public static ThreadLocal<String> usernameHolder = ThreadLocal.withInitial(() -> INITIAL_VALUE);


}
