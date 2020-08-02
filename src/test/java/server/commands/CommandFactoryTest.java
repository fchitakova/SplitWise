package server.commands;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import splitwise.server.commands.*;
import splitwise.server.services.AuthenticationService;
import splitwise.server.services.CommandFactory;
import splitwise.server.services.FriendshipService;
import splitwise.server.services.MoneySplitService;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class CommandFactoryTest {
    private static AuthenticationService authenticationService;
    private static FriendshipService friendshipService;
    private static MoneySplitService moneySplitService;

    private static CommandFactory commandFactory;

    @BeforeClass
    public static void setUp() {
        authenticationService = Mockito.mock(AuthenticationService.class);
        when(authenticationService.getCurrentSessionsUsername()).thenReturn(null);
        friendshipService = Mockito.mock(FriendshipService.class);
        moneySplitService = Mockito.mock(MoneySplitService.class);
        commandFactory = new CommandFactory(authenticationService, friendshipService, moneySplitService);
    }

    @Test
    public void testThatLogoutCommandDoesNotSupportParameters() {
        String logoutWithParameter = "logout abc";

        String assertMessage = "Logout command does not support parameters.";
        Command command = commandFactory.getCommand(logoutWithParameter);

        assertTrue(assertMessage, (command instanceof InvalidCommand));
    }

    @Test
    public void testGroupCommandCreation() {
        String createGroupCommand = "create-group myGroup user1 user2";

        String assertMessage = "CommandFactory does not recognize create-group command.";
        Command command = commandFactory.getCommand(createGroupCommand);

        assertTrue(assertMessage, command instanceof CreateGroupCommand);
    }

    @Test
    public void testSplitCommandCreation() {
        String splitCommand = "split 5 ico_h limes and oranges";

        String assertMessage = "Split command format is not recognized.";
        Command command = commandFactory.getCommand(splitCommand);

        assertTrue(assertMessage, command instanceof SplitCommand);
    }

    @Test
    public void testThatSplitCommandWithNotValidAmountArgumentIsNotValid() {
        String splitCommand = "split five ico_h limes and oranges";

        String assertMessage = "Parsing Split command with invalid money amount argument must return InvalidCommand.";
        Command command = commandFactory.getCommand(splitCommand);

        assertTrue(assertMessage, command instanceof InvalidCommand);
    }

}
