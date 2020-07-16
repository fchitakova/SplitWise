package server.commands;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import splitwise.server.commands.RegisterCommand;
import splitwise.server.exceptions.UserServiceException;
import splitwise.server.services.AuthenticationService;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static server.TestConstants.*;
import static splitwise.server.commands.RegisterCommand.*;
import static splitwise.server.commands.RegisterCommand.ALREADY_LOGGED_IN;

public class RegisterCommandTest {
    public static String REGISTER_COMMAND = "register " + TEST_USERNAME1 + " testPassword";

    private static AuthenticationService authenticationService;
    private static RegisterCommand registerCommand;

    @BeforeClass
    public static void setUp(){
        authenticationService = Mockito.mock(AuthenticationService.class);
        registerCommand = new RegisterCommand(REGISTER_COMMAND, authenticationService);
    }

    @After
    public void resetDependencies(){
        reset(authenticationService);
    }


    @Test
    public void testThatRegistrationAttemptWhenAlreadyLoggedInReturnsMessageThatThisIsNotAllowed(){
        when(authenticationService.getCurrentSessionsUsername()).thenReturn(TEST_USERNAME1);
        registerCommand = new RegisterCommand(REGISTER_COMMAND, authenticationService);

         String assertMessage = "Registration attempt when already logged in did not return response that this is not allowed.";
         String expectedResponse = ALREADY_LOGGED_IN;
         String actualResponse = registerCommand.execute();

         assertEquals(assertMessage,expectedResponse,actualResponse);
    }

    @Test
    public void testThatRegistrationAttemptWithNotTakenUsernameCallsRegisterUser() throws UserServiceException {
        when(authenticationService.getCurrentSessionsUsername()).thenReturn(null);
        registerCommand = new RegisterCommand(REGISTER_COMMAND, authenticationService);
        when(authenticationService.checkIfRegistered(TEST_USERNAME1)).thenReturn(false);

        registerCommand.execute();

        String failureMessage = "Registration with not taken username must be successful.";

        verify(authenticationService, description(failureMessage)).registerUser(TEST_USERNAME1, TEST_PASSWORD1);
    }

    @Test
    public void testThatRegistrationAttemptWithTakenUsernameReturnsTakenUsernameResponse() {
        when(authenticationService.getCurrentSessionsUsername()).thenReturn(null);
        registerCommand = new RegisterCommand(REGISTER_COMMAND, authenticationService);
        when(authenticationService.checkIfRegistered(TEST_USERNAME1)).thenReturn(true);

        String assertMessage = "Registration attempt with taken username did not return right taken username response.";
        String response = registerCommand.execute();

        assertEquals(assertMessage,TAKEN_USERNAME,response);
    }

    @Test
    public void testThatRegistrationWithNonTakenUsernameReturnsSuccessfulRegistrationResponse(){
        when(authenticationService.checkIfRegistered(TEST_USERNAME1)).thenReturn(false);

        String assertMessage = "Registration attempt with idle username did not return right successful registration response";
        String response = registerCommand.execute();

        assertEquals(assertMessage,RegisterCommand.SUCCESSFUL_REGISTRATION,response);
    }

    @Test
    public void testWhenRegistrationFailDueToRepositoryExceptionReturnsRightMessage() throws UserServiceException {
        when(authenticationService.checkIfRegistered(TEST_USERNAME1)).thenReturn(false);
        doThrow(UserServiceException.class).when(authenticationService).registerUser(TEST_USERNAME1,TEST_PASSWORD1);

        String assertMessage="Not right message is returner when userService.registerUser() throws exception.";
        String response = registerCommand.execute();

        assertEquals(assertMessage,RegisterCommand.REGISTRATION_FAILED,response);
    }

    @Test
    public void testThatIfUserServiceThrowExceptionFailedCommandMessageIsReturned() throws UserServiceException {
       when(authenticationService.getCurrentSessionsUsername()).thenReturn(null);
       when(authenticationService.checkIfRegistered(TEST_USERNAME1)).thenReturn(false);
       registerCommand = new RegisterCommand(REGISTER_COMMAND, authenticationService);

       doThrow(new UserServiceException("dummy message", new Throwable())).when(authenticationService).registerUser(TEST_USERNAME1,TEST_PASSWORD1);

       String assertMessage = "When UserServiceException is thrown not right command failure response is returned";
       String actualCommandResult = registerCommand.execute();

       assertEquals(assertMessage,actualCommandResult,REGISTRATION_FAILED);
    }
}


