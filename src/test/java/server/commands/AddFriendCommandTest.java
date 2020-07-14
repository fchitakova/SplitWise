package server.commands;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import splitwise.server.commands.AddFriendCommand;
import splitwise.server.services.UserService;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static server.TestConstants.*;
import static splitwise.server.commands.AddFriendCommand.USER_NOT_FOUND;

public class AddFriendCommandTest {

    private static UserService userService;
    private static AddFriendCommand addFriendCommand;

    @BeforeClass
    public static void setUp() {
        userService = Mockito.mock(UserService.class);
    }


    @Test
    public void testThatAddFriendCommandInvocationWithoutBeingLoggedInReturnsLoginOrRegisterMessage() {
        when(userService.getCurrentSessionsUsername()).thenReturn(null);
        addFriendCommand = new AddFriendCommand(ADD_FRIEND_COMMAND, userService);

        String assertMessage = """
                Not right response is returned when not logged in user attempts to
                invoke add-friend command""";

        String actualResponse = addFriendCommand.execute();

        assertEquals(assertMessage, LOGIN_OR_REGISTER, actualResponse);
    }

    @Test
    public void addingNotExistingUserShouldReturnNotRegisteredMessage() {
        when(userService.getCurrentSessionsUsername()).thenReturn(TEST_USERNAME2);
        addFriendCommand = new AddFriendCommand(ADD_FRIEND_COMMAND, userService);

        when(userService.checkIfRegistered(TEST_USERNAME)).thenReturn(false);

        String assertMessage = "Adding not registered user should return right message.";
        String expectedResult = String.format(USER_NOT_FOUND, TEST_USERNAME);
        String actualResult = addFriendCommand.execute();

        assertEquals(assertMessage, expectedResult, actualResult);
    }

}
