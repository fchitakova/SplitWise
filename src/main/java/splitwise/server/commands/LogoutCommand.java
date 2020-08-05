package splitwise.server.commands;

import splitwise.server.services.AuthenticationService;

public class LogoutCommand extends Command {
    public static final String GOODBYE_MESSAGE = "*GoodBye*";
    
    private AuthenticationService authenticationService;
    
    public LogoutCommand(AuthenticationService authenticationService) {
	super(authenticationService);
	this.authenticationService = authenticationService;
    }
    
    @Override
    public String execute() {
	authenticationService.logoutUser();
	return GOODBYE_MESSAGE;
    }
}
