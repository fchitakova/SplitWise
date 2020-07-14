package server.commands;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import splitwise.server.commands.LoginCommand;
import splitwise.server.services.UserService;

import java.util.ArrayDeque;
import java.util.Deque;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static server.TestConstants.*;
import static splitwise.server.commands.LoginCommand.ALREADY_LOGGED_IN;
import static splitwise.server.commands.LoginCommand.*;


public class LoginCommandTest {

    private static UserService userService;
    private static LoginCommand command;

    @BeforeClass
    public static void setUp(){
        userService = Mockito.mock(UserService.class);
        command = new LoginCommand(LOGIN_COMMAND,userService);
    }

    @After
    public void resetDependencies(){
        reset(userService);
    }


    @Test
    public void testThatLoginAttemptWhenAlreadyLoggedInIsNotAllowed(){
        when(userService.getCurrentSessionsUsername()).thenReturn(TEST_USERNAME);

        String assertMessage = "Login attempt when already logged in did not return right message.";
        String expectedResult = ALREADY_LOGGED_IN;
        String actualResult = command.execute();

        assertEquals(assertMessage,expectedResult,actualResult);
    }

    @Test
    public void testThatInvalidCredentialsMessageIsReturnedWhenInvalidCredentialsAreProvided() {
        when(userService.getCurrentSessionsUsername()).thenReturn(null);
        when(userService.checkCredentialsValidity(TEST_USERNAME, TEST_PASSWORD1)).thenReturn(false);

        String assertMessage = "Login attempt with invalid credentials did not return right invalid credentials message.";
        String expectedInvalidCredentialsResponse = INVALID_CREDENTIALS;
        String actualResponse = command.execute();

        assertEquals(assertMessage, expectedInvalidCredentialsResponse, actualResponse);
    }


    @Test
    public void testThatLoginWithValidCredentialsAddsUserToActiveClients() {
        when(userService.getCurrentSessionsUsername()).thenReturn(null);
        when(userService.checkCredentialsValidity(TEST_USERNAME, TEST_PASSWORD1)).thenReturn(true);
        when(userService.getUserNotifications(TEST_USERNAME)).thenReturn(new ArrayDeque<>());

        String failureMessage = "Successful login did not set user as active!";
        command.execute();

        verify(userService, description(failureMessage)).setUserAsActive(TEST_USERNAME);
    }


    @Test
    public void testThatNoNotificationsMessageIsReturnedWhenThereAreNotAnyNotifications() {
        when(userService.getCurrentSessionsUsername()).thenReturn(null);
        when(userService.checkCredentialsValidity(TEST_USERNAME, TEST_PASSWORD1)).thenReturn(true);
        when(userService.getUserNotifications(TEST_USERNAME)).thenReturn(new ArrayDeque<>());

        String assertMessage = "Not right login response is returned when there are not any notifications.";
        String expectedLoginResponse = SUCCESSFUL_LOGIN + "\nNo notifications to show.";
        String response = command.execute();

        assertEquals(assertMessage, expectedLoginResponse, response);
    }


    @Test
    public void testThatWhenThereAreNotificationsTheyAreIncludedInLoginResponseProperly(){
        Deque<String> testNotifications = new ArrayDeque<>();
        testNotifications.push("first notification");
        testNotifications.push("second notification");
        when(userService.checkCredentialsValidity(TEST_USERNAME,TEST_PASSWORD1)).thenReturn(true);
        when(userService.getUserNotifications(TEST_USERNAME)).thenReturn(testNotifications);
        when(userService.getCurrentSessionsUsername()).thenReturn(null);

        String assertMessage = "Not right login response is returned when there are notifications.";
        String expectedResponse = SUCCESSFUL_LOGIN + '\n' + NOTIFICATIONS_TITLE +
                "\nsecond notification\n\nfirst notification\n\n";
        String response = command.execute();

        assertEquals(assertMessage, expectedResponse, response);
    }

    @Test
    public void testThatAfterLoginNotificationsAreCleared() {
        when(userService.checkCredentialsValidity(TEST_USERNAME, TEST_PASSWORD1)).thenReturn(true);
        when(userService.getUserNotifications(TEST_USERNAME)).thenReturn(new ArrayDeque<>());
        when(userService.getCurrentSessionsUsername()).thenReturn(null);

        String failureMessage = "User notifications are not reset after login.";
        command.execute();

        verify(userService, description(failureMessage)).resetNotifications(TEST_USERNAME);
    }


}
