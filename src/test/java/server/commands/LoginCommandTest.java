package server.commands;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import splitwise.server.commands.LoginCommand;
import splitwise.server.services.AuthenticationService;

import java.util.ArrayDeque;
import java.util.Deque;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static server.TestConstants.*;
import static splitwise.server.commands.LoginCommand.ALREADY_LOGGED_IN;
import static splitwise.server.commands.LoginCommand.*;


public class LoginCommandTest {
    private static AuthenticationService authenticationService;
    private static LoginCommand command;

    @BeforeClass
    public static void setUp(){
        authenticationService = Mockito.mock(AuthenticationService.class);
        command = new LoginCommand(LOGIN_COMMAND, authenticationService);
    }

    @After
    public void resetDependencies(){
        reset(authenticationService);
    }


    @Test
    public void testThatLoginAttemptWhenAlreadyLoggedInIsNotAllowed(){
        when(authenticationService.getCurrentSessionsUsername()).thenReturn(TEST_USERNAME1);
        command = new LoginCommand(LOGIN_COMMAND, authenticationService);

        String assertMessage = "Login attempt when already logged in did not return right message.";
        String expectedResult = ALREADY_LOGGED_IN;
        String actualResult = command.execute();

        assertEquals(assertMessage,expectedResult,actualResult);
    }

    @Test
    public void testThatInvalidCredentialsMessageIsReturnedWhenInvalidCredentialsAreProvided() {
        when(authenticationService.getCurrentSessionsUsername()).thenReturn(null);
        when(authenticationService.checkCredentialsValidity(TEST_USERNAME1, TEST_PASSWORD1)).thenReturn(false);

        String assertMessage = "Login attempt with invalid credentials did not return right invalid credentials message.";
        String expectedInvalidCredentialsResponse = INVALID_CREDENTIALS;
        String actualResponse = command.execute();

        assertEquals(assertMessage, expectedInvalidCredentialsResponse, actualResponse);
    }


    @Test
    public void testThatLoginWithValidCredentialsAddsUserToActiveClients() {
        when(authenticationService.getCurrentSessionsUsername()).thenReturn(null);
        when(authenticationService.checkCredentialsValidity(TEST_USERNAME1, TEST_PASSWORD1)).thenReturn(true);
        when(authenticationService.getUserNotifications(TEST_USERNAME1)).thenReturn(new ArrayDeque<>());

        String failureMessage = "Successful login did not set user as active!";
        command.execute();

        verify(authenticationService, description(failureMessage)).setUserAsActive(TEST_USERNAME1);
    }


    @Test
    public void testThatNoNotificationsMessageIsReturnedWhenThereAreNotAnyNotifications() {
        when(authenticationService.getCurrentSessionsUsername()).thenReturn(null);
        when(authenticationService.checkCredentialsValidity(TEST_USERNAME1, TEST_PASSWORD1)).thenReturn(true);
        when(authenticationService.getUserNotifications(TEST_USERNAME1)).thenReturn(new ArrayDeque<>());

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
        when(authenticationService.checkCredentialsValidity(TEST_USERNAME1,TEST_PASSWORD1)).thenReturn(true);
        when(authenticationService.getUserNotifications(TEST_USERNAME1)).thenReturn(testNotifications);
        when(authenticationService.getCurrentSessionsUsername()).thenReturn(null);

        String assertMessage = "Not right login response is returned when there are notifications.";
        String expectedResponse = SUCCESSFUL_LOGIN + '\n' + NOTIFICATIONS_TITLE +
                "\nsecond notification\n\nfirst notification\n\n";
        String response = command.execute();

        assertEquals(assertMessage, expectedResponse, response);
    }

}
