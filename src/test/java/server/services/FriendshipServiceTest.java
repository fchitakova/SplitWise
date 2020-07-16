package server.services;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import splitwise.server.exceptions.AuthenticationException;
import splitwise.server.model.GroupFriendship;
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
    private static List<User> stubUsers;

    @BeforeClass
    public static void setUp() {
        stubUsers = List.of(new User(TEST_USERNAME1, TEST_PASSWORD1), new User(TEST_USERNAME2, TEST_PASSWORD1), new User(TEST_USERNAME3, TEST_PASSWORD1));
        setUpUserRepository();
        activeUsers = Mockito.mock(ActiveUsers.class);
        friendshipService = new FriendshipService(userRepository,activeUsers);
    }

    public static void setUpUserRepository() {
        userRepository = Mockito.mock(UserRepository.class);
        when(userRepository.getById(TEST_USERNAME1)).thenReturn(Optional.of(stubUsers.get(0)));
        when(userRepository.getById(TEST_USERNAME2)).thenReturn(Optional.of(stubUsers.get(1)));
        when(userRepository.getById(TEST_USERNAME3)).thenReturn(Optional.of(stubUsers.get(2)));
    }

    @Test
    public void testThatIfFriendshipIsEstablishedNotificationIsSentToAddedFriend() throws AuthenticationException {
        friendshipService.createFriendship(TEST_USERNAME1, TEST_USERNAME2);

        String assertMessage = "Notification is not sent to added friend after establishing friendship.";
        Deque<String> userNotifications = stubUsers.get(1).getNotifications();
        String expectedNotification = String.format(RECEIVED_FRIENDSHIP_NOTIFICATION, TEST_USERNAME1);

        assertTrue(assertMessage, userNotifications.contains(expectedNotification));
    }

    @Test
    public void testThatGroupCannotBeCreatedIfAnyOfTheUsersAlreadyParticipatesInGroupWithSameName() throws AuthenticationException {
        stubUsers.get(0).addFriendship(new GroupFriendship(GROUP_NAME, List.of(TEST_USERNAME2, TEST_USERNAME3)));

        String assertMessage = "Group creation is not allowed when any of the members already participates in another group with the same name.";
        boolean groupCreationResult = friendshipService.createGroupFriendship(GROUP_NAME, List.of(TEST_USERNAME1, TEST_USERNAME2, TEST_USERNAME2));

        assertFalse(assertMessage, groupCreationResult);
    }


     @Test
     @Ignore
     public void testThatSuccessfulGroupCreationSendsNotificationsToAllNonActiveParticipatingUsers() throws AuthenticationException {
         stubUsers = new ArrayList<>();
         stubUsers.add(new User(TEST_USERNAME1, TEST_PASSWORD1));
         stubUsers.add(new User(TEST_USERNAME2, TEST_PASSWORD1));
         stubUsers.add(new User(TEST_USERNAME3, TEST_PASSWORD1));
         when(friendshipService.getCurrentSessionsUsername()).thenReturn(TEST_USERNAME1);

         friendshipService.createGroupFriendship(GROUP_NAME,List.of(TEST_USERNAME1,TEST_USERNAME2,TEST_USERNAME3));

         String assertMessage = "Adding not active user to group must send him right notification";

         for(User currentUser:stubUsers){
             String participantsUsernamesWithoutCurrent = stubUsers.stream().filter(user->user!=currentUser).map(User::getUsername).collect(Collectors.joining(", "));
             String notification = String.format(ADDED_TO_GROUP_NOTIFICATION,GROUP_NAME,participantsUsernamesWithoutCurrent);
             if(!currentUser.getNotifications().contains(notification)){
                 fail(assertMessage);
             }
         }
     }


}
