package splitwise.server.commands;

import splitwise.server.services.MoneySplitService;

public class GetStatusCommand extends Command {
    
    private MoneySplitService moneySplitService;
    
    public GetStatusCommand(MoneySplitService moneySplitService) {
	super(moneySplitService);
	this.moneySplitService = moneySplitService;
    }
    
    @Override
    public String execute() {
	if(isCommandInvokerLoggedIn) {
	    return moneySplitService.getStatus(commandInvokerUsername);
	} else {
	    return LOGIN_OR_REGISTER;
	}
    }
}
