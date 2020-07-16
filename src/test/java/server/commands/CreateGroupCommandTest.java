package server.commands;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import splitwise.server.commands.CreateGroupCommand;
import splitwise.server.services.FriendshipService;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static server.TestConstants.TEST_USERNAME1;
import static server.TestConstants.TEST_USERNAME2;
import static splitwise.server.commands.Command.LOGIN_OR_REGISTER;
import static splitwise.server.commands.CreateGroupCommand.NOT_ENOUGH_PARTICIPANTS;
import static splitwise.server.commands.CreateGroupCommand.NOT_REGISTERED_PARTICIPANTS;

public class CreateGroupCommandTest {
    private static String CREATE_GROUP_COMMAND = "create-group myGroup " + TEST_USERNAME1 + " " + TEST_USERNAME2;

    private static FriendshipService friendshipService;
    private static CreateGroupCommand createGroupCommand;


    @BeforeClass
    public static void setUp() {
        friendshipService = Mockito.mock(FriendshipService.class);
    }

    @Test
    public void testThatCreateGroupCommandInvocationWithoutBeingLoggedInReturnsLoginOrRegisterResponse() {
        when(friendshipService.getCurrentSessionsUsername()).thenReturn(null);
        createGroupCommand = new CreateGroupCommand(CREATE_GROUP_COMMAND, friendshipService);

        String assertMessage = "Group creation attempt without being logged in must return login or register response";
        String commandResult = createGroupCommand.execute();

        assertEquals(assertMessage, LOGIN_OR_REGISTER, commandResult);
    }

    @Test
    public void testThatCreateGroupWithNotEnoughMembersReturnsNotAllowedResponse() {
        String createGroupCommandWith2Participants = "create-group myGroup " + TEST_USERNAME2;
        when(friendshipService.getCurrentSessionsUsername()).thenReturn(TEST_USERNAME1);
        when(friendshipService.checkIfRegistered(TEST_USERNAME2)).thenReturn(true);

        createGroupCommand = new CreateGroupCommand(createGroupCommandWith2Participants, friendshipService);

        String assertMessage = "Group creation with less than 3 members (creator included) is not allowed.";
        String commandResult = createGroupCommand.execute();

        assertEquals(assertMessage, NOT_ENOUGH_PARTICIPANTS, commandResult);
    }

    @Test
    public void testThatGroupCreationWithNotRegisteredParticipantReturnsNotAllowedResponse() {
        when(friendshipService.checkIfRegistered(anyString())).thenReturn(false);
        when(friendshipService.getCurrentSessionsUsername()).thenReturn(TEST_USERNAME1);
        createGroupCommand = new CreateGroupCommand(CREATE_GROUP_COMMAND, friendshipService);

        String assertMessage = "Group creation with not registered participants is not allowed";
        String commandResult = createGroupCommand.execute();

        assertEquals(assertMessage, NOT_REGISTERED_PARTICIPANTS, commandResult);
    }

}
