package splitwise.server;


import splitwise.server.commands.Command;
import splitwise.server.commands.InvalidCommand;
import splitwise.server.commands.LoginCommand;
import splitwise.server.commands.RegisterCommand;
import splitwise.server.model.SplitWiseConstants;

public class SplitWiseController {

    private UserService userService;

    public SplitWiseController() {
        userService = new UserService();
    }

    public String processUserInput(String input){
        Command command = createCommand(input);
        String executionResult = command.execute();
        return executionResult;
    }


    private Command createCommand(String userInput) {
        if (userInput.matches(SplitWiseConstants.LOGIN_COMMAND)) {
            return new LoginCommand(userInput, userService);
        }
        if (userInput.matches(SplitWiseConstants.REGISTER_COMMAND)) {
            return new RegisterCommand(userInput, userService);
        }
        //other commands too

        return new InvalidCommand(userInput, userService);
    }


}
