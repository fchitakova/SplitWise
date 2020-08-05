package server.services;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import splitwise.server.exceptions.FriendshipException;
import splitwise.server.model.User;
import splitwise.server.repository.UserRepository;
import splitwise.server.server.ActiveUsers;
import splitwise.server.services.FriendshipService;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static server.TestConstants.*;
import static splitwise.server.services.FriendshipService.ADDED_TO_GROUP_NOTIFICATION;
import static splitwise.server.services.FriendshipService.RECEIVED_FRIENDSHIP_NOTIFICATION;

public class FriendshipServiceTest {

  private static UserRepository userRepository;
  private static ActiveUsers activeUsers;
  private static FriendshipService friendshipService;
  private static List<User> users;

  @BeforeClass
  public static void setUp() {
    users =
        List.of(
            new User(TEST_USERNAME1, TEST_PASSWORD1),
            new User(TEST_USERNAME2, TEST_PASSWORD1),
            new User(TEST_USERNAME3, TEST_PASSWORD1));
    setUpUserRepository();
    activeUsers = Mockito.mock(ActiveUsers.class);
    friendshipService = new FriendshipService(userRepository, activeUsers);
  }

  public static void setUpUserRepository() {
    userRepository = Mockito.mock(UserRepository.class);
    when(userRepository.getById(TEST_USERNAME1)).thenReturn(Optional.of(users.get(0)));
    when(userRepository.getById(TEST_USERNAME2)).thenReturn(Optional.of(users.get(1)));
    when(userRepository.getById(TEST_USERNAME3)).thenReturn(Optional.of(users.get(2)));
  }

  @Test
  public void testThatIfFriendshipIsEstablishedNotificationIsSentToAddedFriend()
      throws FriendshipException {
    friendshipService.createFriendship(TEST_USERNAME1, TEST_USERNAME2);

    String assertMessage =
        "Notification is not sent to added friend after establishing friendship.";
    Deque<String> userNotifications = users.get(1).getNotifications();
    String expectedNotification = String.format(RECEIVED_FRIENDSHIP_NOTIFICATION, TEST_USERNAME1);

    assertTrue(assertMessage, userNotifications.contains(expectedNotification));
  }

  @Test
  public void testThatSuccessfulGroupCreationSendsNotificationsToAllNonActiveParticipatingUsers()
      throws FriendshipException {
    friendshipService.createGroupFriendship(
        TEST_USERNAME1, GROUP_NAME, List.of(TEST_USERNAME1, TEST_USERNAME2, TEST_USERNAME3));

    String assertMessage = "Adding not active user to group must send him right notification";

    for (User currentUser : users) {
      String notification =
          String.format(
              ADDED_TO_GROUP_NOTIFICATION,
              GROUP_NAME,
              users.stream().map(User::getUsername).collect(Collectors.joining(", ")));
      if (!currentUser.getUsername().equals(TEST_USERNAME1)
          && !currentUser.getNotifications().contains(notification)) {
        fail(assertMessage);
      }
    }
  }
}
