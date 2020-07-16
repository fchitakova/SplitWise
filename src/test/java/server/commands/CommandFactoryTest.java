package server.commands;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import splitwise.server.commands.Command;
import splitwise.server.commands.CommandFactory;
import splitwise.server.commands.InvalidCommand;
import splitwise.server.model.Friendship;
import splitwise.server.services.AuthenticationService;
import splitwise.server.services.FriendshipService;
import splitwise.server.services.SplitWiseService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class CommandFactoryTest {
    private static CommandFactory commandFactory;
    private static AuthenticationService authenticationService;
    private static FriendshipService friendshipService;


    @BeforeClass
    public static void setUp(){
        authenticationService = Mockito.mock(AuthenticationService.class);
        when(authenticationService.getCurrentSessionsUsername()).thenReturn(null);
        friendshipService = Mockito.mock(FriendshipService.class);
        commandFactory = new CommandFactory(authenticationService,friendshipService);
    }

    @Test
    public void testThatLogoutCommandDoesNotSupportParameters(){
        String logoutWithParameter = "logout abc";

        String assertMessage = "Logout command does not support parameters.";
        Command command = commandFactory.createCommand(logoutWithParameter);

        assertTrue(assertMessage,(command instanceof InvalidCommand));
    }

}
