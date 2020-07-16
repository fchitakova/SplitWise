package splitwise.server.commands;

import splitwise.server.model.Friendship;
import splitwise.server.services.AuthenticationService;
import splitwise.server.services.FriendshipService;
import splitwise.server.services.SplitWiseService;

import java.util.List;


public class CommandFactory {
    public static final String LOGIN_COMMAND = "login %s %s";
    public static final String REGISTER_COMMAND = "register %s %s";
    public static final String ADD_FRIEND_COMMAND = "add-friend %s";
    public String CREATE_GROUP_COMMAND = "create-group";
    public String SPLIT_COMMAND = "split %s %s";
    public String SPLIT_GROUP_COMMAND = "split-group %s %s %s";
    public String GET_STATUS_COMMAND = "get-status";
    public String PAYED_COMMAND = "payed %s %s";
    public String LOGOUT_COMMAND = "logout";

    private AuthenticationService authenticationService;
    private FriendshipService friendshipService;


    public CommandFactory(AuthenticationService authenticationService, FriendshipService friendshipService) {
        this.authenticationService = authenticationService;
        this.friendshipService = friendshipService;
    }

    public Command createCommand(String input) {
        if (input.equalsIgnoreCase(LOGOUT_COMMAND)) {
            return new LogoutCommand(authenticationService);
        }
        if (inputMatchesCommandFormat(input, LOGIN_COMMAND)) {
            return new LoginCommand(input, authenticationService);
        }
        if (inputMatchesCommandFormat(input, REGISTER_COMMAND)) {
            return new RegisterCommand(input, authenticationService);
        }
        if (inputMatchesCommandFormat(input, ADD_FRIEND_COMMAND)) {
            return new AddFriendCommand(input, friendshipService);
        }
        if (inputMatchesCreateGroupCommandFormat(input)) {
            return new CreateGroupCommand(input, friendshipService);
        }

        return new InvalidCommand(authenticationService);
    }

    private boolean inputMatchesCommandFormat(String input, String commandFormat){
        String[] inputParameters = getInputParameters(input);
        boolean parametersMatch = checkIfParametersCountMatch(inputParameters,commandFormat);

        if(parametersMatch){
            boolean formatMatch = String.format(commandFormat, inputParameters).equals(input);
            if(formatMatch){
                return true;
            }
        }
        return false;
    }

    private String[] getInputParameters(String input){
        String commandParametersAsString = input.substring(input.indexOf(' ')+1);
        String[] commandParameters = commandParametersAsString.split("\\s+");
        return commandParameters;
    }

    private boolean checkIfParametersCountMatch(String[] commandParameters, String commandFormat) {
        int inputParametersCount = commandParameters.length;
        int commandFormatterParametersCount = commandFormat.split("\\s+").length - 1;
        return inputParametersCount == commandFormatterParametersCount;
    }


    private boolean inputMatchesCreateGroupCommandFormat(String input) {
        String[] inputParameters = getInputParameters(input);
        return inputParameters.length > 1 && containsCreateGroupCommand(input);
    }

    private boolean containsCreateGroupCommand(String input) {
        int createGroupCommandLength = CREATE_GROUP_COMMAND.length();
        if (input.length() > createGroupCommandLength) {
            return input.substring(0, CREATE_GROUP_COMMAND.length()).equals(CREATE_GROUP_COMMAND);
        }
        return false;
    }

}
