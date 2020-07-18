package splitwise.server.commands;

import splitwise.server.exceptions.FriendshipException;
import splitwise.server.services.FriendshipService;

import java.util.Arrays;



public class CreateGroupCommand extends Command {
    public static final String NOT_ENOUGH_PARTICIPANTS = "Group friendship can be established with at least 3 participants.";
    public static final String NOT_REGISTERED_PARTICIPANTS = "All participants must be registered!";
    public static final String SUCCESSFULLY_CREATE_GROUP = "Group friendship is successfully created." + START_SPLITTING;
    public static final String ALREADY_TAKEN_GROUP_NAME = "group name is already taken. Try with another name.";
    public static final String GROUP_CREATION_FAILED = "Group creation failed. Try again later.";

    public static final int MINIMUM_COUNT_OF_GROUP_MEMBERS = 3;

    private String groupName;
    private String[] participants;
    private FriendshipService friendshipService;

    public CreateGroupCommand(String command, FriendshipService friendshipCreator) {
        super(friendshipCreator);
        this.friendshipService = friendshipCreator;
        initializeCommandParameters(command);

    }

    private void initializeCommandParameters(String command) {
        String[] commandParts = command.split("\\s+");
        this.groupName = commandParts[1];
        initializeGroupParticipants(commandParts);

    }

    private void initializeGroupParticipants(String[] commandParts) {
        participants = new String[commandParts.length - 1];

        participants[0] = commandInvokerUsername;
        for (int i = 1; i < participants.length; i++) {
            participants[i] = commandParts[i + 1];
        }
    }

    @Override
    public String execute() {
        if (isCommandInvokerLoggedIn) {
            if (enoughGroupMembersArePresent()) {
                if (allGroupMembersAreRegistered()) {
                    String commandResult = createGroup();
                    return commandResult;
                } else {
                    return NOT_REGISTERED_PARTICIPANTS;
                }
            } else {
                return NOT_ENOUGH_PARTICIPANTS;
            }
        }
        return LOGIN_OR_REGISTER;
    }

    private boolean enoughGroupMembersArePresent() {
        return participants.length >= MINIMUM_COUNT_OF_GROUP_MEMBERS
                && !isInvokerIncludedHimselfAsGroupParticipant();
    }

    private boolean isInvokerIncludedHimselfAsGroupParticipant() {
        int countOfInvokerUsernameOccurrences = 0;

        for (int i = 0; i < participants.length; i++) {
            if (participants[i].equals(commandInvokerUsername)) {
                ++countOfInvokerUsernameOccurrences;
            }
        }
        return countOfInvokerUsernameOccurrences > 1;
    }

    private boolean allGroupMembersAreRegistered() {
        return friendshipService.checkIfRegistered(participants);
    }

    private String createGroup() {
        String commandResult = "";
        try {
            boolean isGroupCreated = friendshipService.createGroupFriendship(groupName, Arrays.asList(participants));
            if (!isGroupCreated) {
                commandResult = ALREADY_TAKEN_GROUP_NAME;
            }
        } catch (FriendshipException e) {
            commandResult = GROUP_CREATION_FAILED;
        }
        return commandResult;
    }
}
