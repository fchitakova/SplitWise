package splitwise.server.commands;

import splitwise.server.services.SplitWiseService;

public class HelpCommand extends Command {
    public HelpCommand(SplitWiseService splitWiseService) {
        super(splitWiseService);
    }

    @Override
    public String execute() {
        return """
                $ login <username> <password>    - Login user if credentials are valid and no other user is currently logged in.

                $ register <username> <password> - Registers user if <username> is not taken and not other user is currently logged in.

                $ add-friend <username>          - Creates friendship between currently logged in user and <username> if:
                                                   1. they are not already friends
                                                   2. <username> is registered.

                $ create-group <group-name> <username> <username> ... <username> - Creates group with <group-name> if:
                                                      1. any of the members don't participates in group with same name
                                                      2. at least three registered members' usernames are provided (invoker is not count).

                $ split <amount> <username> <reason_for_payment> - Splits amount between currently logged in user and <username> if they are already friends.

                $ split-group <amount> <group-name> <reason_for_payment> - Splits <amount> between all group members if command invoker is part of group.

                $ payed <amount> <username> <split-reason> - Currently logged in accept <username> payment and friendship account is updated.
                                                             A user cannot accept his own payment!

                $ payed-group <amount> <username> <group-name> <split-reason> - Currently logged in accept <username>'s payment in <group-name> if both are part of this group.
                                                                                A user cannot accept his own payment!

                $ get-status - Shows all outstanding debts with user's friends and in user's groups.
                                
                $ help - Shows all supported commands.
                """;

    }
}
