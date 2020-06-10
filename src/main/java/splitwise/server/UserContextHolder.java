package splitwise.server;


public class UserContextHolder {
    public static ThreadLocal<String> usernameHolder = new ThreadLocal();
}
