package server.commands;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import splitwise.server.commands.CreateGroupCommand;
import splitwise.server.services.UserService;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static server.TestConstants.TEST_USERNAME;
import static server.TestConstants.TEST_USERNAME2;
import static splitwise.server.commands.CreateGroupCommand.NOT_ENOUGH_PARTICIPANTS;
import static splitwise.server.commands.CreateGroupCommand.NOT_REGISTERED_PARTICIPANTS;

public class CreateGroupCommandTest {
    private static String CREATE_GROUP_COMMAND = "create-group myGroup "+TEST_USERNAME+" "+TEST_USERNAME2;

    private static UserService userService;
    private static CreateGroupCommand createGroupCommand;

    @BeforeClass
    public static void setUp(){
       userService = Mockito.mock(UserService.class);
    }

    @Test
    public void testThatCreateGroupWithNotEnoughMembersReturnsNotAllowedResponse(){
        String createGroupCommandWith2Participants = "create-group myGroup "+TEST_USERNAME;
        createGroupCommand = new CreateGroupCommand(createGroupCommandWith2Participants,userService);

        String assertMessage = "Group creation with less than 3 members (creator included) is not allowed.";
        String commandResult = createGroupCommand.execute();

        assertEquals(assertMessage,NOT_ENOUGH_PARTICIPANTS,commandResult);
    }

    @Test
    public void testThatGroupCreationWithNotRegisteredParticipantReturnsNotAllowedResponse(){
        createGroupCommand = new CreateGroupCommand(CREATE_GROUP_COMMAND,userService);
        when(userService.checkIfRegistered(anyString())).thenReturn(false);

        String assertMessage = "Group creation with not registered participants is not allowed";
        String commandResult = createGroupCommand.execute();

        assertEquals(assertMessage,NOT_REGISTERED_PARTICIPANTS,commandResult);
    }

}
