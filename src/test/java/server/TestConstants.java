package server;

public class TestConstants{
    public static final String TEST_USERNAME="testUsername";
    public static final char[] TEST_PASSWORD1 = "testPassword".toCharArray();
    public static final char[] TEST_PASSWORD2 = "dummyPassword".toCharArray();
    public static final String LOGIN_COMMAND = "login "+TEST_USERNAME+ " testPassword";
    public static String REGISTER_COMMAND = "register "+ TEST_USERNAME+" testPassword";
    public static String ADD_FRIEND_COMMAND = "add-friend "+TEST_USERNAME;
    public static final String LOGIN_OR_REGISTER="""
           This command can be invoked only by logged in users. Please first login or register.""";
    public static final String ALREADY_LOGGED_IN = "Registration is not allowed when already logged in.";
}