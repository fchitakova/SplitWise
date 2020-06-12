package splitwise.server;

import splitwise.server.commands.*;
import splitwise.server.model.SplitWiseConstants;

import static splitwise.client.SplitWiseClient.LOGOUT_COMMAND;

public class CommandFactory {

    private UserService userService;

    public CommandFactory(UserService userService){
        this.userService = userService;
    }


    public Command createCommand(String input) {

        if(input.equalsIgnoreCase(LOGOUT_COMMAND)){
            return new LogoutCommand(userService);
        }
        if (inputMatchesCommandFormat(input, SplitWiseConstants.LOGIN_COMMAND)) {
            return new LoginCommand(input, userService);
        }
        if (inputMatchesCommandFormat(input,SplitWiseConstants.REGISTER_COMMAND)) {
            return new RegisterCommand(input, userService);
        }
        //other commands too

        return new InvalidCommand(input, userService);
    }

    private boolean inputMatchesCommandFormat(String input, String commandFormat){
        String[] commandParameters = getInputParameters(input);
        boolean parametersCountMatch = checkIfParametersCountMatch(commandParameters,commandFormat);

        if (parametersCountMatch && String.format(commandFormat, commandParameters).equals(input)) {
            return true;
        }
        return false;
    }

    private String[] getInputParameters(String input){
        String commandParametersAsString = input.substring(input.indexOf(' ')+1);
        String[]commandParameters = commandParametersAsString.split("\\s+");
        return commandParameters;
    }

    private boolean checkIfParametersCountMatch(String[]commandParameters,String commandFormat){
        int inputParametersCount = commandParameters.length;
        int commandFormatterParametersCount = commandFormat.split("\\s+").length-1;
        return inputParametersCount == commandFormatterParametersCount;
    }

}
