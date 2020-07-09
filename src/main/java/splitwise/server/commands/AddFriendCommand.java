package splitwise.server.commands;

import splitwise.server.services.UserService;

public class AddFriendCommand extends Command{
    public static final String USER_NOT_FOUND = "%s is not found. Check friend's username and try again.";

    private String friendUsername;

    public AddFriendCommand(String command, UserService userRepository)
    {
        super(userRepository);
        initializeCommandParameters(command);
    }

    private void initializeCommandParameters(String command) {
        String[]commandParts = command.split("\\s+");
        friendUsername = commandParts[1];
    }

    @Override
    public String execute() {
        String friendshipInitiatorUsername = userService.getCurrentlyLoggedInUserUsername();

        boolean isFriendshipInitiatorLoggedIn = friendshipInitiatorUsername != null;

        if(!isFriendshipInitiatorLoggedIn){
            return LOGIN_OR_REGISTER;
        }
        boolean isFriendRegistered = userService.checkIfRegistered(friendUsername);

        if(!isFriendRegistered){
            return USER_NOT_FOUND;
        }

        userService.createFriendship(friendshipInitiatorUsername,friendUsername);
        return null;
    }
}
