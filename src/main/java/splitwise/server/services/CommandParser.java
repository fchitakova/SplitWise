package splitwise.server.services;

import static splitwise.server.commands.PayedCommand.PAYED_COMMAND;
import static splitwise.server.commands.PayedCommand.PAYED_IN_GROUP_COMMAND;
import static splitwise.server.commands.SplitCommand.SPLIT_COMMAND;
import static splitwise.server.commands.SplitCommand.SPLIT_GROUP_COMMAND;

public class CommandParser {
    public static final String CREATE_GROUP_COMMAND = "create-group";
    public static final int MINIMUM_COUNT_OF_CREATE_GROUP_COMMAND_ARGUMENTS = 2;
    public static final int MINIMUM_COUNT_OF_PAYED_COMMAND_ARGUMENTS = 3;
    public static final int MINIMUM_COUNT_OF_PAYED_IN_GROUP_COMMAND_ARGUMENTS = 4;
    public static final int MINIMUM_COUNT_OF_SPLIT_COMMAND_ARGUMENTS = 3;
    
    public static boolean match(String command, String commandFormat) {
        String[] commandArguments = getArguments(command);
        
        if(hasEqualCountOfParametersAndArguments(commandArguments, commandFormat)) {
            boolean formatMatch = String.format(commandFormat, commandArguments).equals(command);
            return formatMatch;
        }
        return false;
    }
    
    private static String[] getArguments(String command) {
        String commandParametersAsString = command.substring(command.indexOf(' ') + 1);
        String[] commandParameters = commandParametersAsString.split("\\s+");
        return commandParameters;
    }
    
    private static boolean hasEqualCountOfParametersAndArguments(String[] commandArguments, String commandFormat) {
        int argumentsCount = commandArguments.length;
        int parametersCount = commandFormat.split("\\s+").length - 1;
        
        return (argumentsCount==parametersCount);
    }
    
    public static boolean matchPayedCommand(String command) {
        String[] commandArguments = getArguments(command);
        
        boolean commandMatchesPayedCommand = startsWith(command, PAYED_COMMAND) && hasMinimumNumberOfArguments(commandArguments, MINIMUM_COUNT_OF_PAYED_COMMAND_ARGUMENTS);
        boolean commandMatchesPayedInGroupCommand = startsWith(command, PAYED_IN_GROUP_COMMAND) && hasMinimumNumberOfArguments(commandArguments, MINIMUM_COUNT_OF_PAYED_IN_GROUP_COMMAND_ARGUMENTS);
        
        if(commandMatchesPayedCommand || commandMatchesPayedInGroupCommand) {
            String moneyAmountArgument = commandArguments[0];
            return argumentIsValidDouble(moneyAmountArgument);
        }
        
        return false;
    }
    
    public static boolean startsWith(String command, String prefix) {
        if(command.length()>prefix.length()) {
            return command.startsWith(prefix);
        }
        return false;
    }
    
    private static boolean hasMinimumNumberOfArguments(String[] commandArguments, int minimumNumberOfParameters) {
        return commandArguments.length >= minimumNumberOfParameters;
    }
    
    private static boolean argumentIsValidDouble(String argument) {
        try {
            Double.parseDouble(argument);
        } catch(NumberFormatException e) {
            return false;
        }
        return true;
    }
    
    public static boolean matchCreateGroupCommandFormat(String command) {
        String[] commandArguments = getArguments(command);
        boolean doesInputMatchCommand = hasMinimumNumberOfArguments(commandArguments, MINIMUM_COUNT_OF_CREATE_GROUP_COMMAND_ARGUMENTS) && startsWith(command, CREATE_GROUP_COMMAND);
        return doesInputMatchCommand;
    }
    
    public static boolean matchSplitCommand(String command) {
        boolean hasSplitCommandPrefix = startsWith(command, SPLIT_COMMAND) || startsWith(command, SPLIT_GROUP_COMMAND);
        if(hasSplitCommandPrefix) {
            
            String[] commandArguments = getArguments(command);
            String moneyAmountArgument = commandArguments[0];
            
            return hasMinimumNumberOfArguments(commandArguments, MINIMUM_COUNT_OF_SPLIT_COMMAND_ARGUMENTS) && argumentIsValidDouble(moneyAmountArgument);
        }
        return false;
    }
}
