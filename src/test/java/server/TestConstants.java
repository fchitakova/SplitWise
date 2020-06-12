package server;

public class TestConstants{
    public static final String TEST_USERNAME="testUsername";
    public static final char[] TEST_PASSWORD1 = "testPassword".toCharArray();
    public static final char[] TEST_PASSWORD2 = "dummyPassword".toCharArray();
    public static final String LOGIN_COMMAND = "login "+TEST_USERNAME+ " testPassword";
    public static String REGISTER_COMMAND = "register "+ TEST_USERNAME+" testPassword";
}