package server.commands;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import splitwise.server.commands.SplitCommand;
import splitwise.server.services.MoneySplitService;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static server.TestConstants.*;
import static splitwise.server.commands.SplitCommand.SPLITTING_IN_GROUP_IS_ALLOWED_ONLY_FOR_MEMBERS;
import static splitwise.server.commands.SplitCommand.SPLITTING_IS_ALLOWED_ONLY_WITH_FRIENDS;

public class SplitCommandTest {
    private static final String SPLIT_WITH_FRIEND_COMMAND = "split 5 " + TEST_USERNAME2 + " limes and oranges";
    private static final String SPLIT_WITH_GROUP_COMMAND = "split-group 5 " + GROUP_NAME + " limes and oranges";

    private static MoneySplitService moneySplitService;
    private static SplitCommand splitCommand;

    @BeforeClass
    public static void setUp() {
        moneySplitService = Mockito.mock(MoneySplitService.class);
    }

    @Test
    public void testThatSplittingWithoutBeingLoggedInReturnsLoginOrRegisterMessage() {
        when(moneySplitService.getCurrentSessionsUsername()).thenReturn(null);
        splitCommand = new SplitCommand(SPLIT_WITH_FRIEND_COMMAND, moneySplitService);

        String assertMessage = "Splitting without being logged in must return login or register response";
        String commandResult = splitCommand.execute();

        assertEquals(assertMessage, LOGIN_OR_REGISTER, commandResult);
    }

    @Test
    public void testThatSplittingWithNonFriendUserReturnsNotAllowedResponse() {
        when(moneySplitService.getCurrentSessionsUsername()).thenReturn(TEST_USERNAME1);
        when(moneySplitService.isMoneySharingAllowedBetween(TEST_USERNAME1, TEST_USERNAME2)).thenReturn(false);
        splitCommand = new SplitCommand(SPLIT_WITH_FRIEND_COMMAND, moneySplitService);

        String assertMessage = "Splitting with non friend user must return response indicating it is not allowed";
        String commandResult = splitCommand.execute();

        assertEquals(assertMessage, SPLITTING_IS_ALLOWED_ONLY_WITH_FRIENDS, commandResult);
    }

    @Test
    public void testThatSplittingInGroupInWhichInvokerIsNotMemberReturnsNotAllowedResponse() {
        when(moneySplitService.getCurrentSessionsUsername()).thenReturn(TEST_USERNAME1);
        when(moneySplitService.isMoneySharingAllowedBetween(TEST_USERNAME1, GROUP_NAME)).thenReturn(false);
        splitCommand = new SplitCommand(SPLIT_WITH_GROUP_COMMAND, moneySplitService);

        String assertMessage = "Splitting with group of which the invoker is not member must return response indicating it is not allowed";
        String commandResult = splitCommand.execute();

        assertEquals(assertMessage, SPLITTING_IN_GROUP_IS_ALLOWED_ONLY_FOR_MEMBERS, commandResult);
    }
}
