package server.commands;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import splitwise.server.UserContextHolder;
import splitwise.server.services.UserService;
import splitwise.server.commands.LoginCommand;

import java.util.ArrayDeque;
import java.util.Deque;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static server.TestConstants.TEST_USERNAME;
import static server.TestConstants.TEST_PASSWORD1;
import static server.TestConstants.LOGIN_COMMAND;


public class LoginCommandTest {
    private static String INVALID_CREDENTIALS = "Invalid username or password!";
    String SUCCESSFUL_LOGIN_MESSAGE = "Successful login!";
    String ANSI_RED = "\u001B[31m";
    String ANSI_RESET = "\u001B[0m";
    private String RED_STAR_SYMBOL = ANSI_RED + '*' + ANSI_RESET;
    private String NOTIFICATIONS_TITLE = RED_STAR_SYMBOL+RED_STAR_SYMBOL+RED_STAR_SYMBOL+" Notifications " +
            RED_STAR_SYMBOL+RED_STAR_SYMBOL+RED_STAR_SYMBOL;


    private static UserService userService;
    private static LoginCommand command;

    @BeforeClass
    public static void setUp(){
        userService = Mockito.mock(UserService.class);
        command = new LoginCommand(LOGIN_COMMAND,userService);
    }

    @Test
    public void testInvalidCredentialsMessageIsReturnedIfInvalidCredentialsAreProvided(){
        when(userService.checkCredentialsValidity(TEST_USERNAME,TEST_PASSWORD1)).thenReturn(false);

        String assertMessage = "Login attempt with invalid credentials did not return right invalid credentials message.";
        String expectedInvalidCredentialsResponse = INVALID_CREDENTIALS;
        String actualResponse = command.execute();

        assertEquals(assertMessage,expectedInvalidCredentialsResponse,actualResponse);
    }


    @Test
    public void testLoginWithValidCredentialsChangeUserContext(){
        when(userService.checkCredentialsValidity(TEST_USERNAME,TEST_PASSWORD1)).thenReturn(true);
        when(userService.getUserNotifications(TEST_USERNAME)).thenReturn(new ArrayDeque<>());

        String assertMessage = "Missed UserContextHolder update: Successful login did not set username in UserContextHolder to logged in user's username!";
        command.execute();
        String userContextAfterInvalidLoginAttempt = UserContextHolder.usernameHolder.get();

        assertEquals(assertMessage,TEST_USERNAME,userContextAfterInvalidLoginAttempt);
    }


    @Test
    public void testThatNoNotificationsMessageIsReturnedWhenThereAreNotAnyNotifications(){
        when(userService.checkCredentialsValidity(TEST_USERNAME,TEST_PASSWORD1)).thenReturn(true);
        when(userService.getUserNotifications(TEST_USERNAME)).thenReturn(new ArrayDeque<>());

        String assertMessage = "Not right login response is returned when there are not any notifications.";
        String expectedLoginResponse = SUCCESSFUL_LOGIN_MESSAGE +"\nNo notifications to show.";
        String response = command.execute();

        assertEquals(assertMessage, expectedLoginResponse,response);
    }


    @Test
    public void testThatWhenThereAreNotificationsTheyAreIncludedInLoginResponseProperly(){
        Deque<String> testNotifications = new ArrayDeque<>();
        testNotifications.push("first notification");
        testNotifications.push("second notification");
        when(userService.checkCredentialsValidity(TEST_USERNAME,TEST_PASSWORD1)).thenReturn(true);
        when(userService.getUserNotifications(TEST_USERNAME)).thenReturn(testNotifications);

        String assertMessage = "Not right login response is returned when there are notifications.";
        String expectedResponse = SUCCESSFUL_LOGIN_MESSAGE +'\n'+ NOTIFICATIONS_TITLE +
                "\nsecond notification\n\nfirst notification\n\n";
        String response = command.execute();

        assertEquals(assertMessage,expectedResponse,response);
    }


}
