package server.commands;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import splitwise.server.commands.AddFriendCommand;
import splitwise.server.exceptions.FriendshipException;
import splitwise.server.services.FriendshipService;


import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static server.TestConstants.*;
import static splitwise.server.commands.AddFriendCommand.FRIENDSHIP_CANNOT_BE_ESTABLISHED;
import static splitwise.server.commands.AddFriendCommand.USER_NOT_FOUND;

public class AddFriendCommandTest {
    public static String ADD_FRIEND_COMMAND = "add-friend " + TEST_USERNAME1;

    private static FriendshipService friendshipCreator;
    private static AddFriendCommand addFriendCommand;

    @BeforeClass
    public static void setUp() {
        friendshipCreator = Mockito.mock(FriendshipService.class);
    }

    @After
    public void resetDependencies(){
        reset(friendshipCreator);
    }


    @Test
    public void testThatAddFriendCommandInvocationWithoutBeingLoggedInReturnsLoginOrRegisterMessage() {
        when(friendshipCreator.getCurrentSessionsUsername()).thenReturn(null);
        addFriendCommand = new AddFriendCommand(ADD_FRIEND_COMMAND, friendshipCreator);

        String assertMessage = """
                Not right response is returned when not logged in user attempts to
                invoke add-friend command""";

        String actualResponse = addFriendCommand.execute();

        assertEquals(assertMessage, LOGIN_OR_REGISTER, actualResponse);
    }

    @Test
    public void addingNotExistingUserShouldReturnNotRegisteredMessage() {
        when(friendshipCreator.getCurrentSessionsUsername()).thenReturn(TEST_USERNAME2);
        addFriendCommand = new AddFriendCommand(ADD_FRIEND_COMMAND, friendshipCreator);
        when(friendshipCreator.checkIfRegistered(TEST_USERNAME1)).thenReturn(false);

        String assertMessage = "Adding not registered user should return right message.";
        String expectedResult = String.format(USER_NOT_FOUND, TEST_USERNAME1);
        String actualResult = addFriendCommand.execute();

        assertEquals(assertMessage, expectedResult, actualResult);
    }

    @Test
    public void testThatIfUserServiceThrowExceptionFailedCommandMessageIsReturned() throws FriendshipException {
        when(friendshipCreator.getCurrentSessionsUsername()).thenReturn(TEST_USERNAME2);
        when(friendshipCreator.checkIfRegistered(TEST_USERNAME1)).thenReturn(true);
        doThrow(new FriendshipException("dummy message", new Throwable())).when(friendshipCreator).createFriendship(TEST_USERNAME2, TEST_USERNAME1);
        addFriendCommand = new AddFriendCommand(ADD_FRIEND_COMMAND, friendshipCreator);

        String assertMessage = "When UserServiceException is thrown not right command failure response is returned";
        String actualResult = addFriendCommand.execute();

        assertEquals(assertMessage,FRIENDSHIP_CANNOT_BE_ESTABLISHED,actualResult);
    }

}
