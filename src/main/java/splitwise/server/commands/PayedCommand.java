package splitwise.server.commands;

import splitwise.server.services.MoneySplitService;

public class PayedCommand extends Command {
    public static final String PAYED_COMMAND = "payed";
    public static final String PAYED_IN_GROUP_COMMAND = "payed-group";

    private MoneySplitService moneySplitService;
    private boolean isPayedInGroup;
    private Double amount;
    private String debtorUsername;
    private String groupName;

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

        if (isPayedInGroup) {
            groupName = commandParts[3];
        }
    }

    private void setIsPayedInGroup(String command) {
        if (command.equals(PAYED_COMMAND)) {
            isPayedInGroup = false;
        } else {
            isPayedInGroup = true;
        }
    }


    @Override
    public String execute() {
        if (isCommandInvokerLoggedIn) {
            if (isPayedInGroup) {
                if (moneySplitService.isMoneySharingAllowedBetween(commandInvokerUsername, groupName)) {
                    //    moneySplitService.groupPayOff(commandInvokerUsername, amount, debtorUsername, groupName);
                }
            } else {
                if (moneySplitService.isMoneySharingAllowedBetween(commandInvokerUsername, debtorUsername)) {
                    //  moneySplitService.payOff(commandInvokerUsername, amount, debtorUsername);
                }
            }
        }
        return LOGIN_OR_REGISTER;
    }


}
