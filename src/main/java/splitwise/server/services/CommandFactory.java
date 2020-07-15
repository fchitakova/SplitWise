package splitwise.server.services;

import splitwise.server.commands.*;

import java.util.List;


public class CommandFactory {
    public static final String LOGIN_COMMAND = "login %s %s";
    public static final String REGISTER_COMMAND = "register %s %s";
    public static final String ADD_FRIEND_COMMAND  = "add-friend %s";
    public String CREATE_GROUP_COMMAND = "create-group %s %s";
    public String SPLIT_COMMAND = "split %s %s";
    public String SPLIT_GROUP_COMMAND = "split-group %s %s %s";
    public String GET_STATUS_COMMAND = "get-status";
    public String PAYED_COMMAND = "payed %s %s";
    public String LOGOUT_COMMAND = "logout";

    private List<SplitWiseService> splitWiseServices;


    public CommandFactory(List<SplitWiseService>splitWiseServices){
        this.splitWiseServices = splitWiseServices;
    }

    public Command createCommand(String input) {
        if(input.equalsIgnoreCase(LOGOUT_COMMAND)){
            return new LogoutCommand((AuthenticationService)getService(AuthenticationService.class));
        }
        if (inputMatchesCommandFormat(input, LOGIN_COMMAND)) {
            return new LoginCommand(input,(AuthenticationService)getService(AuthenticationService.class));
        }
        if (inputMatchesCommandFormat(input, REGISTER_COMMAND)) {
            return new RegisterCommand(input,(AuthenticationService)getService(AuthenticationService.class));
        }
        if(inputMatchesCommandFormat(input, ADD_FRIEND_COMMAND)){
            return new AddFriendCommand(input,(FriendshipCreator) getService(FriendshipCreator.class));
        }

        return new InvalidCommand(getService(AuthenticationService.class));
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
        String[]commandParameters = commandParametersAsString.split("\\s+");
        return commandParameters;
    }

    private boolean checkIfParametersCountMatch(String[]commandParameters,String commandFormat){
        int inputParametersCount = commandParameters.length;
        int commandFormatterParametersCount = commandFormat.split("\\s+").length-1;
        return inputParametersCount == commandFormatterParametersCount;
    }

    private SplitWiseService getService(Class serviceClass){
        for(SplitWiseService service:splitWiseServices){
            if(service.getClass().equals(serviceClass)){
                return service;
            }
        }
        return null;
    }

}
