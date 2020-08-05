package server.commands;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import splitwise.server.commands.PayedCommand;
import splitwise.server.exceptions.MoneySplitException;
import splitwise.server.services.MoneySplitService;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static server.TestConstants.TEST_USERNAME1;
import static server.TestConstants.TEST_USERNAME2;
import static splitwise.server.commands.Command.LOGIN_OR_REGISTER;
import static splitwise.server.commands.PayedCommand.*;

public class PayedCommandTest {
    private static final String PAYED_COMMAND = "payed 6.66 " + TEST_USERNAME1 + " reason";

    private static MoneySplitService moneySplitService;
    private static PayedCommand payedCommand;

    @BeforeClass
    public static void setUp() {
        moneySplitService = Mockito.mock(MoneySplitService.class);
    }

    @Test
    public void resetDependencies() {
        reset(moneySplitService);
    }

    @Test
    public void testThatPayedCommandInvocationWithoutBeingLoggedInReturnsLoginOrRegisterMessage() {
        when(moneySplitService.getCurrentSessionsUsername()).thenReturn(null);
        payedCommand = new PayedCommand(PAYED_COMMAND, moneySplitService);

        String commandResult = payedCommand.execute();
        String assertMessage = "Invoking payed command without being logged in must not be enabled.";

        assertEquals(assertMessage, LOGIN_OR_REGISTER, commandResult);
    }

    @Test
    public void testThatInvokingPayedCommandForOneselfReturnsNotPermittedMessage() {
        when(moneySplitService.getCurrentSessionsUsername()).thenReturn(TEST_USERNAME1);
        payedCommand = new PayedCommand(PAYED_COMMAND, moneySplitService);

        String commandResult = payedCommand.execute();
        String assertMessage = "Invoking payed command for your accepting your own debt is not permitted.";

        assertEquals(assertMessage, CANNOT_APPROVE_OWN_PAYMENT, commandResult);
    }

    @Test
    public void testThatInvokingPayedCommandForNonFriendReturnsNotAllowedMessage() {
        when(moneySplitService.getCurrentSessionsUsername()).thenReturn(TEST_USERNAME2);
        when(moneySplitService.areFriends(TEST_USERNAME2, TEST_USERNAME1)).thenReturn(false);
        payedCommand = new PayedCommand(PAYED_COMMAND, moneySplitService);

        String commandResult = payedCommand.execute();
        String assertMessage = "Invoking payed command for non-friend user is not allowed.";

        assertEquals(assertMessage, ALLOWED_ONLY_FOR_FRIENDS, commandResult);
    }

    @Test
    public void testThatIfPaymentAttemptFailsFailedCommandResponseIsReturned() throws MoneySplitException {
        when(moneySplitService.getCurrentSessionsUsername()).thenReturn(TEST_USERNAME2);
        when(moneySplitService.areFriends(TEST_USERNAME2, TEST_USERNAME1)).thenReturn(true);
        doThrow(new MoneySplitException("", new Throwable())).when(moneySplitService).payOff(anyString(), anyDouble(), anyString(), anyString());

        payedCommand = new PayedCommand(PAYED_COMMAND, moneySplitService);

        String commandResult = payedCommand.execute();
        String assertMessage = "If payment fails, command failed response must be returned.";

        assertEquals(assertMessage, COMMAND_FAILED, commandResult);
    }

}














