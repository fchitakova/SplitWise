package splitwise.server.commands;

import splitwise.server.exceptions.UserServiceException;
import splitwise.server.services.UserService;

import java.lang.reflect.Array;
import java.nio.charset.CoderMalfunctionError;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * създава група, състояща се от няколко, вече регистрирани, потребители:
 *
 * $ create-group <group_name> <username> <username> ... <username>
 * Групите се създават от един потребител, като всяка група включва трима или повече потребители.
 * Можете да си представяте, че "приятелските" отношения са група от двама човека.
 */

public class CreateGroupCommand extends Command {
    public static final String NOT_ENOUGH_PARTICIPANTS = "Group friendship can be established with at least 3 participants.";
    public static final String NOT_REGISTERED_PARTICIPANTS = "All participants must be registered!";
    public static final String SUCCESSFULLY_CREATE_GROUP = "Group friendship is successfully created." + START_SPLITTING;
    public static final String ALREADY_TAKEN_GROUP_NAME = "group name is already taken. Try with another name.";
    public static final String GROUP_CREATION_FAILED = "Group creation failed. Try again later.";

    private String groupName;
    private String[] participants;

    public CreateGroupCommand(String command, UserService userService) {
        super(userService);
        initializeCommandParameters(command);

    }

    private void initializeCommandParameters(String command) {
        String[] commandParts = command.split("\\s+");
        this.groupName = commandParts[1];
        initializeGroupParticipants(commandParts);

    }

    private void initializeGroupParticipants(String[] commandParts) {
        participants = new String[commandParts.length - 2];

       for(int i = 0;i < participants.length;i++) {
           participants[i] = commandParts[i + 2];
        }
    }

    @Override
    public String execute() {
        if (participants.length < 2) {
            return NOT_ENOUGH_PARTICIPANTS;
        }
        if (!userService.checkIfRegistered(participants)) {
            return NOT_REGISTERED_PARTICIPANTS;
        }

        String commandResult = createGroup();
        return commandResult;
    }

    private String createGroup(){
        String commandResult;
        try {
            boolean isGroupCreated = userService.createGroupFriendship(groupName, Arrays.asList(participants));
            commandResult = isGroupCreated? SUCCESSFULLY_CREATE_GROUP: ALREADY_TAKEN_GROUP_NAME;
        } catch (UserServiceException e) {
            commandResult = GROUP_CREATION_FAILED;
        }
        return commandResult;
    }
}
