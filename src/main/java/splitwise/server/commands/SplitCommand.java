package splitwise.server.commands;

import splitwise.server.exceptions.MoneySplitException;
import splitwise.server.services.MoneySplitService;


public class SplitCommand extends Command {
    public static final String SPLIT_GROUP_COMMAND = "split-group";
    public static final String SPLITTING_IN_GROUP_IS_ALLOWED_ONLY_FOR_MEMBERS = "You must be part of the group to be allowed to split with its members!";
    public static final String SPLITTING_IS_ALLOWED_ONLY_WITH_FRIENDS = "You can split only with friends. Ensure that friendship is established before starting splitting.";
    public static final String COMMAND_FAILED = "Split command failed. try again later.";
    public static final String SEE_STATUS = "You can view the status of all splits with get-status command.";
    public static final String SPLITTING_RESULT = "Split %s LV between you and %s for %s. " + SEE_STATUS;
    public static final String GROUP_SPLITTING_RESULT = "Split %s LV between you and %s group members %s. " + SEE_STATUS;

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
        amount = Double.valueOf(commandParts[1]);
        friendshipName = commandParts[2];

        StringBuilder splitReason = new StringBuilder();
        for (int i = 3; i < commandParts.length; i++) {
            splitReason.append(commandParts[i] + " ");
        }
        this.splitReason = (splitReason.toString()).trim();
    }

    @Override
    public String execute() {
        if (isCommandInvokerLoggedIn) {
            String commandResult = isSplitAllowed() ? split() : createSplitNotAllowedResponse();
            return commandResult;
        }
        return LOGIN_OR_REGISTER;
    }

    private boolean isSplitAllowed() {
        return moneySplitService.isMoneySharingAllowedBetween(commandInvokerUsername, friendshipName);
    }

    private String split() {
        try {
            moneySplitService.split(commandInvokerUsername, friendshipName, amount, splitReason);
            String splittingResult = createSplitResponse();
            return splittingResult;
        } catch (MoneySplitException e) {
            return COMMAND_FAILED;
        }
    }


    private String createSplitNotAllowedResponse() {
        String response = isGroupSplit ?
                SPLITTING_IN_GROUP_IS_ALLOWED_ONLY_FOR_MEMBERS :
                SPLITTING_IS_ALLOWED_ONLY_WITH_FRIENDS;
        return response;
    }

    private String createSplitResponse() {
        String response = isGroupSplit ?
                String.format(GROUP_SPLITTING_RESULT, amount, friendshipName, splitReason) :
                String.format(SPLITTING_RESULT, amount, friendshipName, splitReason);
        return response;
    }

}
