package splitwise.server.commands;

import splitwise.server.services.UserService;

public class AddFriendCommand extends Command {
    public static final String USER_NOT_FOUND = "%s is not found. Check friend's username and try again.";
    public static final String ESTABLISHED_FRIENDSHIP = "Friendship is established.";
    public static final String ALREADY_FRIENDS = "You are already friends.";

    private final String username;
    private String friendUsername;
    private final boolean isFriendshipInitiatorLoggedIn;

    public AddFriendCommand(String command, UserService userService) {
        super(userService);
        initializeCommandParameters(command);
        username = userService.getCurrentSessionsUsername();
        isFriendshipInitiatorLoggedIn = (username != null);
    }

    private void initializeCommandParameters(String command) {
        String[] commandParts = command.split("\\s+");
        friendUsername = commandParts[1];
    }

    @Override
    public String execute() {
        if (!isFriendshipInitiatorLoggedIn) {
            return LOGIN_OR_REGISTER;
        }
        if (!isFriendPresent()) {
            return String.format(USER_NOT_FOUND, friendUsername);
        }
        boolean isFriendshipEstablished = userService.createFriendship(username, friendUsername);

        if (isFriendshipEstablished) {
            return ESTABLISHED_FRIENDSHIP + START_SPLITTING;
        }
        return ALREADY_FRIENDS;
    }


    private boolean isFriendPresent() {
        return userService.checkIfRegistered(friendUsername) && (!username.equals(friendUsername));
    }

}
