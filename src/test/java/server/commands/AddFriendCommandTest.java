package server.commands;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import splitwise.server.commands.AddFriendCommand;
import splitwise.server.services.UserService;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static server.TestConstants.ADD_FRIEND_COMMAND;
import static server.TestConstants.LOGIN_OR_REGISTER;

public class AddFriendCommandTest {

    private static UserService userService;
    private static AddFriendCommand addFriendCommand;

    @BeforeClass
    public static void setUp(){
        userService = Mockito.mock(UserService.class);
        addFriendCommand = new AddFriendCommand(ADD_FRIEND_COMMAND,userService);
    }

    @Test
    public void testThatAddFriendCommandInvocationWithoutBeingLoggedInReturnsLoginOrRegisterMessage(){
        when(userService.getCurrentlyLoggedInUserUsername()).thenReturn(null);

        String assertMessage = """
                       Not right response is returned when not logged in user attempts to
                       invoke add-friend command""";

        String actualResponse = addFriendCommand.execute();

        assertEquals(assertMessage,LOGIN_OR_REGISTER,actualResponse);
    }




}
