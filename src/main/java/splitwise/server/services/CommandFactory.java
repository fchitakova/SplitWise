package splitwise.server.services;

import splitwise.server.commands.*;

import static splitwise.server.services.CommandParser.*;


public class CommandFactory {
    public static final String LOGIN_COMMAND = "login %s %s";
    public static final String REGISTER_COMMAND = "register %s %s";
    public static final String ADD_FRIEND_COMMAND = "add-friend %s";
    public static final String GET_STATUS_COMMAND = "get-status";
    public static final String PAYED_COMMAND = "payed %s %s";
    public static final String PAYED_IN_GROUP_COMMAND = "payed-group %s %s %s";
    public static final String LOGOUT_COMMAND = "logout";

    private AuthenticationService authenticationService;
    private FriendshipService friendshipService;
    private MoneySplitService moneySplitService;


    public CommandFactory(AuthenticationService authenticationService, FriendshipService friendshipService, MoneySplitService moneySplitService) {
        this.authenticationService = authenticationService;
        this.friendshipService = friendshipService;
        this.moneySplitService = moneySplitService;
    }

    public Command createCommand(String input) {
        if (input.equalsIgnoreCase(LOGOUT_COMMAND)) {
            return new LogoutCommand(authenticationService);
        }
        if (match(input, LOGIN_COMMAND)) {
            return new LoginCommand(input, authenticationService);
        }
        if (match(input, REGISTER_COMMAND)) {
            return new RegisterCommand(input, authenticationService);
        }
        if (match(input, ADD_FRIEND_COMMAND)) {
            return new AddFriendCommand(input, friendshipService);
        }

        if (match(input, PAYED_COMMAND) || match(input, PAYED_IN_GROUP_COMMAND)) {
            return new PayedCommand(input, moneySplitService);
        }

        if (matchCreateGroupCommandFormat(input)) {
            return new CreateGroupCommand(input, friendshipService);
        }
        if (matchSplitCommand(input)) {
            return new SplitCommand(input, moneySplitService);
        }

        return new InvalidCommand(authenticationService);
    }

}