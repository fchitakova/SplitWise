package splitwise.server.commands;


import splitwise.server.services.SplitWiseService;

public abstract class Command {
    public static final String LOGIN_OR_REGISTER = """
                                                   This command can be invoked only by logged in users.
                                                   Please first login or register.""";
    public static final String START_SPLITTING = "You can start splitting!";
    
    protected String commandInvokerUsername;
    protected boolean isCommandInvokerLoggedIn;
    
    
    public Command(SplitWiseService splitWiseService) {
        commandInvokerUsername = splitWiseService.getCurrentSessionsUsername();
        isCommandInvokerLoggedIn = (commandInvokerUsername!=null);
    }
    
    public abstract String execute();
    
}
