package server.commands;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import splitwise.server.exceptions.UserServiceException;
import splitwise.server.services.UserService;
import splitwise.server.commands.RegisterCommand;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static server.TestConstants.TEST_USERNAME;
import static server.TestConstants.TEST_PASSWORD1;
import static server.TestConstants.REGISTER_COMMAND;

public class RegisterCommandTest {
    private static UserService userService;
    private static RegisterCommand registerCommand;
    private String TAKEN_USERNAME = "Username is already taken.Try using another.";

    @BeforeClass
    public static void setUp(){
        userService = Mockito.mock(UserService.class);
        registerCommand = new RegisterCommand(REGISTER_COMMAND,userService);
    }


    @Test
    public void testThatRegistrationAttemptWithNotTakenUsernameCallsRegisterUser() throws UserServiceException {
        reset(userService);
        when(userService.checkIfRegistered(TEST_USERNAME)).thenReturn(false);

        registerCommand.execute();

        verify(userService,times(1)).registerUser(TEST_USERNAME,TEST_PASSWORD1);
    }

    @Test
    public void testThatRegistrationAttemptWithTakenUsernameReturnsTakenUsernameResponse() {
        when(userService.checkIfRegistered(TEST_USERNAME)).thenReturn(true);

        String assertMessage = "Registration attempt with taken username did not return right taken username response.";
        String response = registerCommand.execute();

        assertEquals(assertMessage,TAKEN_USERNAME,response);
    }

    @Test
    public void testThatRegistrationWithNonTakenUsernameReturnsSuccessfulRegistrationResponse(){
        when(userService.checkIfRegistered(TEST_USERNAME)).thenReturn(false);

        String assertMessage = "Registration attempt with idle username did not return right successful registration response";
        String response = registerCommand.execute();

        assertEquals(assertMessage,RegisterCommand.SUCCESSFUL_REGISTRATION,response);
    }

    @Test
    public void testWhenRegistrationFailDueToRepositoryExceptionReturnsRightMessage() throws UserServiceException {
        when(userService.checkIfRegistered(TEST_USERNAME)).thenReturn(false);
        doThrow(UserServiceException.class).when(userService).registerUser(TEST_USERNAME,TEST_PASSWORD1);

        String assertMessage="Not right message is returner when userService.registerUser() throws exception.";
        String response = registerCommand.execute();

        assertEquals(assertMessage,RegisterCommand.REGISTRATION_FAILED,response);

    }
}
