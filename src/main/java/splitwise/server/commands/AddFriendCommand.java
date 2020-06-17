package splitwise.server.commands;

import splitwise.server.UserContextHolder;
import splitwise.server.services.UserService;

public class AddFriendCommand extends Command{
    public static final String USER_NOT_FOUND = "%s is not found. Check friend's username and try again.";
    public static final String LOGIN_OR_REGISTER="""
           add-friend command can be invoked only by registered users.Please first login or register.""";

    private static final String ADD_FRIEND_COMMAND = "add-friend %s";

    private String friendUsername;

    public AddFriendCommand(String command,UserService userRepository) {
        super(userRepository);
    }

    private void initializeCommandParameters(String command) {
        String[]commandParts = command.split("\\s+");
        friendUsername = commandParts[1];
    }

    @Override
    public String execute() {
        String friendshipInitiatorUsername = UserContextHolder.usernameHolder.get();

        boolean isInitiatorLoggedIn = friendshipInitiatorUsername.equals(UserContextHolder.INITIAL_VALUE);
        if(!isInitiatorLoggedIn){
            return LOGIN_OR_REGISTER;
        }
        boolean isFriendRegistered = userService.checkIfRegistered(friendUsername);

        if(!isFriendRegistered){
            return USER_NOT_FOUND;
        }
       // userService.createFriendship(friendshipInitiatorUsername,friendUsername);




        return null;
    }
}
