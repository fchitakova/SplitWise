package splitwise.server.commands;

import splitwise.server.services.UserService;

public class AddFriendCommand extends Command{
    public static final String USER_NOT_FOUND = "%s is not found. Check friend's username and try again.";

    private static final String ADD_FRIEND_COMMAND = "add-friend %s";

    private String friendsUsername;

    public AddFriendCommand(String command,UserService userRepository) {
        super(userRepository);
    }

    private void initializeCommandParameters(String command) {
        String[]commandParts = command.split("\\s+");
        this.friendsUsername = commandParts[1];
    }

    @Override
    public String execute() {
       boolean isFriendRegistered = userService.checkIfRegistered(friendsUsername);
 //      if(!isFriendRegistered){
   //        return USER_NOT_FOUND;
     //  }
   //    String invokerUsername
      // this.userService.createFriendship()
        //if not exists return not found user
        //else add friend
     return null;
    }
}
