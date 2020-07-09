package server.commands;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import splitwise.server.exceptions.UserServiceException;
import splitwise.server.services.UserService;
import splitwise.server.commands.RegisterCommand;
import static splitwise.server.commands.RegisterCommand.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static server.TestConstants.TEST_USERNAME;
import static server.TestConstants.TEST_PASSWORD1;
import static server.TestConstants.REGISTER_COMMAND;

public class RegisterCommandTest {
    private static UserService userService;
    private static RegisterCommand registerCommand;

    @BeforeClass
    public static void setUp(){
        userService = Mockito.mock(UserService.class);
        registerCommand = new RegisterCommand(REGISTER_COMMAND,userService);
    }

    @After
    public void resetDependencies(){
        reset(userService);
    }


    @Test
    public void testThatRegistrationAttemptWhenAlreadyLoggedInReturnsMessageThatThisIsNotAllowed(){
         when(userService.getCurrentlyLoggedInUserUsername()).thenReturn(TEST_USERNAME);

         String assertMessage = "Registration attempt when already logged in did not return response that this is not allowed.";
         String expectedResponse = ALREADY_LOGGED_IN;
         String actualResponse = registerCommand.execute();

         assertEquals(assertMessage,expectedResponse,actualResponse);
    }

    @Test
    public void testThatRegistrationAttemptWithNotTakenUsernameCallsRegisterUser() throws UserServiceException {
        when(userService.getCurrentlyLoggedInUserUsername()).thenReturn(null);
        when(userService.checkIfRegistered(TEST_USERNAME)).thenReturn(false);

        registerCommand.execute();

        verify(userService).registerUser(TEST_USERNAME,TEST_PASSWORD1);
    }

    @Test
    public void testThatRegistrationAttemptWithTakenUsernameReturnsTakenUsernameResponse() {
        when(userService.getCurrentlyLoggedInUserUsername()).thenReturn(null);
        when(userService.checkIfRegistered(TEST_USERNAME)).thenReturn(true);

        String assertMessage = "Registration attempt with taken username did not return right taken username response.";
        String response = registerCommand.execute();

        assertEquals(assertMessage,TAKEN_USERNAME,response);
    }

    @Test
    public void testThatRegistrationWithNonTakenUsernameReturnsSuccessfulRegistrationResponse(){
        when(userService.getCurrentlyLoggedInUserUsername()).thenReturn(null);
        when(userService.checkIfRegistered(TEST_USERNAME)).thenReturn(false);

        String assertMessage = "Registration attempt with idle username did not return right successful registration response";
        String response = registerCommand.execute();

        assertEquals(assertMessage,RegisterCommand.SUCCESSFUL_REGISTRATION,response);
    }

    @Test
    public void testWhenRegistrationFailDueToRepositoryExceptionReturnsRightMessage() throws UserServiceException {
        when(userService.getCurrentlyLoggedInUserUsername()).thenReturn(null);
        when(userService.checkIfRegistered(TEST_USERNAME)).thenReturn(false);
        doThrow(UserServiceException.class).when(userService).registerUser(TEST_USERNAME,TEST_PASSWORD1);

        String assertMessage="Not right message is returner when userService.registerUser() throws exception.";
        String response = registerCommand.execute();

        assertEquals(assertMessage,RegisterCommand.REGISTRATION_FAILED,response);

    }
}
