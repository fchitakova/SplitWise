package server;

import org.junit.Test;
import splitwise.server.model.User;

import static org.junit.Assert.assertEquals;
import static server.TestConstants.*;
import static splitwise.server.model.User.NOT_ANY_OUTSTANDING_FINANCES;

public class UserTest {

  @Test
  public void testThatIfSplittingStatusIsEmptyRightResponseIsReturned() {
    User user = new User(TEST_USERNAME1, TEST_PASSWORD1);
    user.addFriendship(TEST_USERNAME2);
    user.splitWithFriend(TEST_USERNAME2, 0.0);

    String assertMessage =
        "When splitting status is empty then not any outstanding finances message must be returned.";
    String splittingStatus = user.getSplittingStatus();

    assertEquals(assertMessage, NOT_ANY_OUTSTANDING_FINANCES, splittingStatus);
  }
}
