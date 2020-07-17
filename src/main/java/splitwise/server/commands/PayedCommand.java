package splitwise.server.commands;

import splitwise.server.exceptions.MoneySplitException;
import splitwise.server.services.MoneySplitService;

public class PayedCommand extends Command {
    public static final String PAYED_COMMAND = "payed";
    public static final String PAYED_IN_GROUP_COMMAND = "payed-group";
    public static final String COMMAND_FAILED = "Command failed due to error. Try again later.";
    public static final String SUCCESSFULLY_PAYED_RESULT = "Successfully updated friendship account between you and %s [%s]. ";
    public static final String SUCCESSFULLY_PAYED_IN_GROUP_RESULT = "Successfully updated friendship account between you and %s in group:%s [%s].";


    private MoneySplitService moneySplitService;
    private boolean isPayedInGroup;
    private Double amount;
    private String debtorUsername;
    private String groupName;
    private String splitReason;

    public PayedCommand(String input, MoneySplitService moneySplitService) {
        super(moneySplitService);
        this.moneySplitService = moneySplitService;
        initializeCommandParameters(input);
    }

    private void initializeCommandParameters(String input) {
        String[] commandParts = input.split("\\s+");

        setIsPayedInGroup(commandParts[0]);
        amount = Double.valueOf(commandParts[1]);
        debtorUsername = commandParts[2];

        int splitReasonPartStartPosition = 3;

        if (isPayedInGroup) {
            groupName = commandParts[3];
            splitReasonPartStartPosition = 4;
        }
        setUpSplitReason(commandParts, splitReasonPartStartPosition);
    }

    private void setIsPayedInGroup(String command) {
        if (command.equals(PAYED_COMMAND)) {
            isPayedInGroup = false;
        } else {
            isPayedInGroup = true;
        }
    }

    private void setUpSplitReason(String[] commandParts, int splitReasonPartStartPosition) {
        StringBuilder splitReason = new StringBuilder();

        for (int i = splitReasonPartStartPosition; i < commandParts.length; i++) {
            splitReason.append(commandParts[i] + " ");
        }
        this.splitReason = (splitReason.toString()).trim();
    }


    @Override
    public String execute() {
        if (isCommandInvokerLoggedIn) {
            String commandResult = isPayedInGroup ? payedInGroup() : payed();
            return commandResult;
        }
        return LOGIN_OR_REGISTER;
    }

    private String payedInGroup() {
        if (moneySplitService.isMoneySharingAllowedBetween(commandInvokerUsername, groupName)) {
            try {
                moneySplitService.groupPayOff(
                        commandInvokerUsername, amount, debtorUsername, groupName, splitReason);
            } catch (MoneySplitException e) {
                return COMMAND_FAILED;
            }
        }
        return String.format(SUCCESSFULLY_PAYED_IN_GROUP_RESULT, debtorUsername, groupName, splitReason);
    }

    private String payed() {
        if (moneySplitService.isMoneySharingAllowedBetween(commandInvokerUsername, debtorUsername)) {
            try {
                moneySplitService.payOff(commandInvokerUsername, amount, debtorUsername, splitReason);
            } catch (MoneySplitException e) {
                return COMMAND_FAILED;
            }
        }
        return String.format(SUCCESSFULLY_PAYED_RESULT, debtorUsername, splitReason);
    }

}
