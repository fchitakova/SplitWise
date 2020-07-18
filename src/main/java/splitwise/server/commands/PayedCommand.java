package splitwise.server.commands;

import splitwise.server.exceptions.MoneySplitException;
import splitwise.server.services.MoneySplitService;

public class PayedCommand extends Command {
    public static final String SEE_STATUS = "You can view the status of all splits with get-status command.";
    public static final String PAYED_COMMAND = "payed";
    public static final String PAYED_IN_GROUP_COMMAND = "payed-group";
    public static final String COMMAND_FAILED = "Command failed due to error. Try again later.";
    public static final String SUCCESSFULLY_PAYED_RESULT = "Successfully updated friendship account between you and %s [%s]. " + SEE_STATUS;
    public static final String SUCCESSFULLY_PAYED_IN_GROUP_RESULT = "Successfully updated friendship account between you and %s in group:%s [%s]. " + SEE_STATUS;
    public static final String ALLOWED_ONLY_FOR_GROUP_MEMBERS = "You must be group member to accept group payments.";
    public static final String ALLOWED_ONLY_FOR_FRIENDS = "Accepting payments of non-friend is not allowed.";
    public static final String CANNOT_APPROVE_OWN_PAYMENT = "You can't approve your own payment!";


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
            if (commandInvokerUsername.equals(debtorUsername)) {
                return CANNOT_APPROVE_OWN_PAYMENT;
            } else {
                String commandResult = isPayedInGroup ? payedInGroup() : payed();
                return commandResult;
            }
        }
        return LOGIN_OR_REGISTER;
    }

    private String payedInGroup() {
        if (isInvokerGroupMember()) {
            try {
                moneySplitService.groupPayOff(commandInvokerUsername, amount, debtorUsername, groupName, splitReason);

                String commandSuccessResult = String.format(SUCCESSFULLY_PAYED_IN_GROUP_RESULT, debtorUsername, groupName, splitReason);
                return commandSuccessResult;
            } catch (MoneySplitException e) {
                return COMMAND_FAILED;
            }
        }
        return ALLOWED_ONLY_FOR_GROUP_MEMBERS;
    }

    private boolean isInvokerGroupMember() {
        return moneySplitService.isGroupMember(commandInvokerUsername, groupName);
    }

    private String payed() {
        if (moneySplitService.areFriends(commandInvokerUsername, debtorUsername)) {
            try {
                moneySplitService.payOff(commandInvokerUsername, amount, debtorUsername, splitReason);

                String commandSuccessResult = String.format(SUCCESSFULLY_PAYED_RESULT, debtorUsername, splitReason);
                return commandSuccessResult;
            } catch (MoneySplitException e) {
                return COMMAND_FAILED;
            }
        }
        return ALLOWED_ONLY_FOR_FRIENDS;
    }

}
