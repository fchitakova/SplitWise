package splitwise.server.commands;

import splitwise.server.exceptions.MoneySplitException;
import splitwise.server.services.MoneySplitService;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class SplitCommand extends Command {
    public static final String SPLIT_COMMAND = "split";
    public static final String SPLIT_GROUP_COMMAND = "split-group";
    public static final String SPLITTING_IN_GROUP_IS_ALLOWED_ONLY_FOR_MEMBERS = "You must be part of the group to be allowed to split with its members!";
    public static final String SPLITTING_IS_ALLOWED_ONLY_WITH_FRIENDS = "You can split only with friends. Ensure that friendship is established before starting splitting.";
    public static final String COMMAND_FAILED = "Split command failed. try again later.";
    public static final String SEE_STATUS = "You can view the status of all splits with get-status command.";
    public static final String SPLITTING_RESULT = "Split %s LV between you and [%s].Reason: %s. " + SEE_STATUS;
    public static final String GROUP_SPLITTING_RESULT = "Split %s LV between you and [%s] group members. Reason: [%s]. " + SEE_STATUS;
    
    private MoneySplitService moneySplitService;
    private boolean isGroupSplit;
    private Double amount;
    private String friendshipName;
    private String splitReason;
    
    public SplitCommand(String input, MoneySplitService moneySplitService) {
	super(moneySplitService);
	
	this.moneySplitService = moneySplitService;
	initializeCommandParameters(input);
    }
    
    private void initializeCommandParameters(String input) {
	String[] commandParts = input.split("\\s+");
 
	isGroupSplit = commandParts[0].equalsIgnoreCase(SPLIT_GROUP_COMMAND);
	amount = BigDecimal.valueOf(Double.parseDouble(commandParts[1])).setScale(2, RoundingMode.FLOOR).doubleValue();
	friendshipName = commandParts[2];
 
	setUpSplitReason(commandParts);
    }
    
    private void setUpSplitReason(String[] commandParts) {
	StringBuilder splitReason = new StringBuilder();
	int splitReasonPartStartPosition = 3;
	
	for(int i = splitReasonPartStartPosition; i<commandParts.length; i++) {
	    splitReason.append(commandParts[i] + " ");
	}
	this.splitReason = (splitReason.toString()).trim();
    }
    
    @Override
    public String execute() {
	if(isCommandInvokerLoggedIn) {
	    String splitResult = split();
	    return splitResult;
	}
	return LOGIN_OR_REGISTER;
    }
    
    private String split() {
	try {
	    String commandResult = isGroupSplit ? splitInGroupAndGetResult():splitWithFriendAndGetResult();
	    return commandResult;
	} catch(MoneySplitException e) {
	    return COMMAND_FAILED;
	}
    }
    
    private String splitInGroupAndGetResult() throws MoneySplitException {
	if(isInvokerGroupMember()) {
	    moneySplitService.splitInGroup(commandInvokerUsername, friendshipName, amount, splitReason);
	    return getSuccessfulSplitResponse();
	}
	return getSplitNotAllowedResponse();
    }
    
    private String splitWithFriendAndGetResult() throws MoneySplitException {
	if(moneySplitService.areFriends(commandInvokerUsername, friendshipName)) {
	    moneySplitService.split(commandInvokerUsername, friendshipName, amount, splitReason);
	    return getSuccessfulSplitResponse();
	}
	return getSplitNotAllowedResponse();
    }
    
    private boolean isInvokerGroupMember() {
	return moneySplitService.isGroupMember(commandInvokerUsername, friendshipName);
    }
    
    private String getSuccessfulSplitResponse() {
	String response = isGroupSplit ? String.format(GROUP_SPLITTING_RESULT, amount, friendshipName, splitReason):String.format(SPLITTING_RESULT, amount, friendshipName, splitReason);
	return response;
    }
    
    private String getSplitNotAllowedResponse() {
	String response = isGroupSplit ? SPLITTING_IN_GROUP_IS_ALLOWED_ONLY_FOR_MEMBERS:SPLITTING_IS_ALLOWED_ONLY_WITH_FRIENDS;
	return response;
    }
}
