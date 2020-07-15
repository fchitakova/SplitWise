package server.services;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import splitwise.server.model.User;
import splitwise.server.model.UserRepository;
import splitwise.server.model.filesystem.FileSystemUserRepository;
import splitwise.server.server.ActiveUsers;
import splitwise.server.services.AuthenticationService;

import java.util.Deque;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static server.TestConstants.*;

public class AuthenticationServiceTest {
    private static ActiveUsers activeClients;
    private static UserRepository userRepository;
    private static AuthenticationService authenticationService;

    @BeforeClass
    public static void setUp() {
        activeClients = Mockito.mock(ActiveUsers.class);
        userRepository = Mockito.mock(FileSystemUserRepository.class);
        authenticationService = new AuthenticationService(userRepository, activeClients);
    }


    @Test
    public void testCheckingCredentialsValidityWithNotRegisteredUsernameReturnsFalse() {
        when(userRepository.getById(TEST_USERNAME)).thenReturn(Optional.ofNullable(null));

        boolean result = authenticationService.checkCredentialsValidity(TEST_USERNAME, TEST_PASSWORD1);

        String assertMessage = "Checking credentials validity of not registered user returned true!";
        assertFalse(assertMessage, result);
    }

    @Test
    public void testCheckingCredentialsValidityWithWrongCredentialsReturnsFalse(){
        User user = new User(TEST_USERNAME,TEST_PASSWORD1);
        when(userRepository.getById(TEST_USERNAME)).thenReturn(Optional.of(user));

        boolean result = authenticationService.checkCredentialsValidity(TEST_USERNAME,TEST_PASSWORD2);

        String assertMessage = "Wrong credentials passed validation!";
        assertFalse(assertMessage,result);
    }

    @Test
    public void testCheckingCredentialsValidityWithValidCredentialsReturnTrue(){
        User user = new User(TEST_USERNAME,TEST_PASSWORD1);
        when(userRepository.getById(TEST_USERNAME)).thenReturn(Optional.of(user));

        boolean result = authenticationService.checkCredentialsValidity(TEST_USERNAME,TEST_PASSWORD1);

        String assertMessage = "Right credentials did not pass validation!";
        assertTrue(assertMessage,result);
    }

    @Test
    public void testCheckIfRegisteredReturnsTrueIfUserExists(){
        User testUser = new User(TEST_USERNAME,TEST_PASSWORD1);
        doReturn(Optional.of(testUser)).when(userRepository).getById(TEST_USERNAME);

        boolean checkIfRegisteredResult = authenticationService.checkIfRegistered(TEST_USERNAME);

        String assertMessage = "checking if registered returned false when user is registered.";
        assertTrue(assertMessage,checkIfRegisteredResult);
    }

    @Test
    public void testGetUserNotifications(){
        User testUser = new User(TEST_USERNAME,TEST_PASSWORD1);
        List<String> pushedNotifications = List.of("first notification","second notification");
        for(String notification:pushedNotifications){
            testUser.pushNotification(notification);
        }
        when(userRepository.getById(TEST_USERNAME)).thenReturn(Optional.of(testUser));

        String assertMessage = "Wrong notifications are returned.";
        Deque<String> notifications = authenticationService.getUserNotifications(TEST_USERNAME);
        boolean assertCondition = notifications.containsAll(pushedNotifications);

        assertTrue(assertMessage, assertCondition);
    }


}
