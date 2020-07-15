package server;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import splitwise.server.exceptions.PersistenceException;
import splitwise.server.exceptions.UserServiceException;
import splitwise.server.model.User;
import splitwise.server.model.UserRepository;
import splitwise.server.model.filesystem.FileSystemUserRepository;
import splitwise.server.server.ActiveClients;
import splitwise.server.services.UserService;

import java.util.Deque;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static server.TestConstants.*;

public class UserServiceTest {
    private static UserRepository userRepository;
    private static UserService userService;
    private static ActiveClients activeClients;

    @BeforeClass
    public static void setUp() throws UserServiceException {
        activeClients = Mockito.mock(ActiveClients.class);
        userRepository = Mockito.mock(FileSystemUserRepository.class);
        userService = new UserService(activeClients);
        userService.setUserRepository(userRepository);
    }

    @Test
    public void testCheckingCredentialsValidityWithNotRegisteredUsernameReturnsFalse(){
       when(userRepository.getById(TEST_USERNAME)).thenReturn(Optional.ofNullable(null));

       boolean result = userService.checkCredentialsValidity(TEST_USERNAME,TEST_PASSWORD1);

       String assertMessage = "Checking credentials validity of not registered user returned true!";
       assertFalse(assertMessage,result);
    }

    @Test
    public void testCheckingCredentialsValidityWithWrongCredentialsReturnsFalse(){
        User user = new User(TEST_USERNAME,TEST_PASSWORD1);
        when(userRepository.getById(TEST_USERNAME)).thenReturn(Optional.of(user));

        boolean result = userService.checkCredentialsValidity(TEST_USERNAME,TEST_PASSWORD2);

        String assertMessage = "Wrong credentials passed validation!";
        assertFalse(assertMessage,result);
    }

    @Test
    public void testCheckingCredentialsValidityWithValidCredentialsReturnTrue(){
        User user = new User(TEST_USERNAME,TEST_PASSWORD1);
        when(userRepository.getById(TEST_USERNAME)).thenReturn(Optional.of(user));

        boolean result = userService.checkCredentialsValidity(TEST_USERNAME,TEST_PASSWORD1);

        String assertMessage = "Right credentials did not pass validation!";
        assertTrue(assertMessage,result);
    }

    @Test
    public void testCheckIfRegisteredReturnsTrueIfUserExists(){
        User testUser = new User(TEST_USERNAME,TEST_PASSWORD1);
     doReturn(Optional.of(testUser)).when(userRepository).getById(TEST_USERNAME);

     boolean checkIfRegisteredResult = userService.checkIfRegistered(TEST_USERNAME);

     String assertMessage = "checking if registered returned false when user is registered.";
     assertTrue(assertMessage,checkIfRegisteredResult);

    }


    @Test
    public void testGetUserNotifications(){
        User testUser = new User(TEST_USERNAME,TEST_PASSWORD1);
        List<String>pushedNotifications = List.of("first notification","second notification");
        for(String notification:pushedNotifications){
            testUser.pushNotification(notification);
        }
        when(userRepository.getById(TEST_USERNAME)).thenReturn(Optional.of(testUser));

        String assertMessage = "Push notifications method did not push notifications properly.";
        Deque<String> notifications = userService.getUserNotifications(TEST_USERNAME);
        boolean assertCondition = notifications.containsAll(pushedNotifications);

        assertTrue(assertMessage, assertCondition);
    }

    @Test
    public void testThatNotificationsForNotActiveUserArePushedToHisNotificationsQueue() throws PersistenceException {
        User testUser = new User(TEST_USERNAME, TEST_PASSWORD1);
        when(activeClients.isActive(TEST_USERNAME)).thenReturn(false);
        when(userRepository.getById(TEST_USERNAME)).thenReturn(Optional.of(testUser));

        userService.sendNotification(testUser, "notification");

        String assertMessage = "Notifications of not active user must be pushed in his notifications queue.";
        boolean expectedAssertCondition = testUser.getNotifications().contains("notification");

        assertTrue(assertMessage, expectedAssertCondition);
    }

    @Test
    public void testThatNotificationsForActiveUserAreNotPushedToHisNotificationsQueue() throws PersistenceException {
        User testUser = new User(TEST_USERNAME, TEST_PASSWORD1);
        when(activeClients.isActive(TEST_USERNAME)).thenReturn(true);
        when(userRepository.getById(TEST_USERNAME)).thenReturn(Optional.of(testUser));

        userService.sendNotification(testUser, "notification");

        String assertMessage = "Notifications of active user must not be pushed in his notifications queue.";
        boolean expectedAssertCondition = !testUser.getNotifications().contains("notification");

        assertTrue(assertMessage, expectedAssertCondition);
    }

    @Test
    public void testThatNotificationsOfActiveUserAreSentInRealTime() throws PersistenceException {
        when(activeClients.isActive(TEST_USERNAME)).thenReturn(true);

        userService.sendNotification(new User(TEST_USERNAME, TEST_PASSWORD1), "notification");

        String failureMessage = "Notifications of active user must be send in real time.";

        verify(activeClients, description(failureMessage)).sendMessageToUser(TEST_USERNAME, "notification");
    }

   // @Test
    //public void testThatCreatingGroupFriendship


}
