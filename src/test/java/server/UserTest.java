package server;

import org.junit.Test;
import splitwise.server.model.User;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static server.TestConstants.*;
import static splitwise.server.model.User.NOT_ANY_OUTSTANDING_FINANCES;

public class UserTest {

    @Test
    public void testThatFriedWithSameUsernameCanBeAddedOnlyOnce() {
        User user = new User(TEST_USERNAME1, TEST_PASSWORD1);
        user.addFriendship(TEST_USERNAME2);

        String assertMessage = "Friend can be added only once.";
        boolean isFriendAddedAgain = user.addFriendship(TEST_USERNAME2);

        assertEquals(assertMessage, false, isFriendAddedAgain);
    }

    @Test
    public void testThatUserCanBeAddedToTheSameGroupOnlyOnce() {
        User user = new User(TEST_USERNAME1, TEST_PASSWORD1);
        user.addToGroup(GROUP_NAME, List.of(TEST_USERNAME2, TEST_USERNAME3));

        String assertMessage = "User can be added to the same group only once.";
        boolean isUserAddedToGroupAgain = user.addToGroup(GROUP_NAME, List.of(TEST_USERNAME2, TEST_USERNAME3));

        assertEquals(assertMessage, false, isUserAddedToGroupAgain);
    }

    @Test
    public void testThatIfSplittingStatusIsEmptyRightResponseIsReturned() {
        User user = new User(TEST_USERNAME1, TEST_PASSWORD1);
        user.addFriendship(TEST_USERNAME2);
        user.splitWithFriend(TEST_USERNAME2, 0.0);

        String assertMessage = "When splitting status is empty then not any outstanding finances message must be returned.";
        String splittingStatus = user.getSplittingStatus();

        assertEquals(assertMessage, NOT_ANY_OUTSTANDING_FINANCES, splittingStatus);
    }

}
