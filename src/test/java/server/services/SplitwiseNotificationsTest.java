package server.services;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import splitwise.server.exceptions.PersistenceException;
import splitwise.server.exceptions.AuthenticationException;
import splitwise.server.model.User;
import splitwise.server.repository.UserRepository;
import splitwise.server.repository.filesystem.FileSystemUserRepository;
import splitwise.server.server.ActiveUsers;
import splitwise.server.services.FriendshipService;
import splitwise.server.services.SplitWiseService;

import java.util.Optional;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static server.TestConstants.TEST_PASSWORD1;
import static server.TestConstants.TEST_USERNAME1;

public class SplitwiseNotificationsTest {
  private static UserRepository userRepository;
  private static ActiveUsers activeUsers;
  private static SplitWiseService splitWiseService;

  @BeforeClass
  public static void setUp() throws AuthenticationException {
    activeUsers = Mockito.mock(ActiveUsers.class);
    userRepository = Mockito.mock(FileSystemUserRepository.class);
    splitWiseService = new FriendshipService(userRepository, activeUsers);
  }

  @Test
  public void testThatNotificationsForNotActiveUserArePushedToHisNotificationsQueue()
      throws PersistenceException {
    User testUser = new User(TEST_USERNAME1, TEST_PASSWORD1);
    when(activeUsers.isActive(TEST_USERNAME1)).thenReturn(false);
    when(userRepository.getById(TEST_USERNAME1)).thenReturn(Optional.of(testUser));

    splitWiseService.sendNotification(testUser, "notification");

    String assertMessage =
        "Notifications of not active user must be pushed in his notifications queue.";
    boolean expectedAssertCondition = testUser.getNotifications().contains("notification");

    assertTrue(assertMessage, expectedAssertCondition);
  }

  @Test
  public void testThatNotificationsForActiveUserAreNotPushedToHisNotificationsQueue()
      throws PersistenceException {
    User testUser = new User(TEST_USERNAME1, TEST_PASSWORD1);
    when(activeUsers.isActive(TEST_USERNAME1)).thenReturn(true);
    when(userRepository.getById(TEST_USERNAME1)).thenReturn(Optional.of(testUser));

    splitWiseService.sendNotification(testUser, "notification");

    String assertMessage =
        "Notifications of active user must not be pushed in his notifications queue.";
    boolean expectedAssertCondition = !testUser.getNotifications().contains("notification");

    assertTrue(assertMessage, expectedAssertCondition);
  }

  @Test
  public void testThatNotificationsOfActiveUserAreSentInRealTime() throws PersistenceException {
    when(activeUsers.isActive(TEST_USERNAME1)).thenReturn(true);

    splitWiseService.sendNotification(new User(TEST_USERNAME1, TEST_PASSWORD1), "notification");

    String failureMessage = "Notifications of active user must be send in real time.";

    verify(activeUsers, description(failureMessage))
        .sendMessageToUser(TEST_USERNAME1, "notification");
  }
}
