package splitwise.server.commands;

import splitwise.server.services.MoneySplitService;
import splitwise.server.services.SplitWiseService;

public class GetStatusCommand extends Command{
    private MoneySplitService moneySplitService;

    public GetStatusCommand(SplitWiseService splitWiseService) {
        super(splitWiseService);
    }

    @Override
    public String execute() {
        return null;
    }
}
