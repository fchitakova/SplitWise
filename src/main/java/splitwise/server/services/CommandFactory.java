package splitwise.server.services;

import splitwise.server.commands.*;

import static splitwise.server.services.CommandParser.*;

public class CommandFactory {
  public static final String LOGIN_COMMAND = "login %s %s";
  public static final String REGISTER_COMMAND = "register %s %s";
  public static final String ADD_FRIEND_COMMAND = "add-friend %s";
  public static final String LOGOUT_COMMAND = "logout";
  public static final String GET_STATUS_COMMAND = "get-status";
  public static final String HELP_COMMAND = "help";

  private AuthenticationService authenticationService;
  private FriendshipService friendshipService;
  private MoneySplitService moneySplitService;

  public CommandFactory(
      AuthenticationService authenticationService,
      FriendshipService friendshipService,
      MoneySplitService moneySplitService) {
    this.authenticationService = authenticationService;
    this.friendshipService = friendshipService;
    this.moneySplitService = moneySplitService;
  }

  public Command getCommand(String input) {

    if (input.equalsIgnoreCase(HELP_COMMAND)) {
      return new HelpCommand(authenticationService);
    }

    if (input.equalsIgnoreCase(LOGOUT_COMMAND)) {
      return new LogoutCommand(authenticationService);
    }

    if (input.equalsIgnoreCase(GET_STATUS_COMMAND)) {
      return new GetStatusCommand(moneySplitService);
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

    if (matchPayedCommand(input)) {
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
