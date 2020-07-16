package splitwise.server.services;

public class CommandParser {
    public static final String CREATE_GROUP_COMMAND = "create-group";
    public static final String SPLIT_COMMAND = "split";
    public static final String SPLIT_GROUP_COMMAND = "split-group";

    public static final int MINIMUM_COUNT_OF_CREATE_GROUP_COMMAND_ARGUMENTS = 2;
    public static final int MINIMUM_COUNT_OF_SPLIT_COMMAND_ARGUMENTS = 3;

    public static boolean match(String command, String commandFormat) {
        String[] commandArguments = getArguments(command);

        if (hasEqualCountOfParametersAndArguments(commandArguments, commandFormat)) {
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

        return (argumentsCount == parametersCount);
    }


    public static boolean matchCreateGroupCommandFormat(String command) {
        String[]commandArguments = getArguments(command);
        return hasMinimumNumberOfParameters(commandArguments, MINIMUM_COUNT_OF_CREATE_GROUP_COMMAND_ARGUMENTS) && startsWithCreateGroupCommand(command);
    }

    private static boolean hasMinimumNumberOfParameters(String[]commandArguments, int minimumNumberOfParameters) {
        return commandArguments.length > minimumNumberOfParameters;
    }

    public static boolean startsWithCreateGroupCommand(String command) {
        int createGroupCommandLength = CREATE_GROUP_COMMAND.length();
        if (command.length() > createGroupCommandLength) {
            return command.startsWith( CREATE_GROUP_COMMAND);
        }
        return false;
    }

    public static boolean matchSplitCommand(String command) {
        String[] arguments = getArguments(command);
        String moneyAmountArgument = arguments[0];

        if(startsWithSplitCommand(command) && argumentIsValidDouble(moneyAmountArgument) &&
                hasMinimumNumberOfParameters(arguments, MINIMUM_COUNT_OF_SPLIT_COMMAND_ARGUMENTS)){
            return true;
        }
        return false;
    }

    private static boolean startsWithSplitCommand(String command){
        return command.startsWith(SPLIT_COMMAND) || command.startsWith(SPLIT_GROUP_COMMAND);
    }

    private static boolean argumentIsValidDouble(String argument){
        try {
            Double.parseDouble(argument);
        }catch(NumberFormatException e){
            return false;
        }
        return true;
    }

}
