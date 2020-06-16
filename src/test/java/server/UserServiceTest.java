package server;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import splitwise.server.UserService;
import splitwise.server.model.User;
import splitwise.server.model.UserRepository;
import splitwise.server.model.filesystem.FileSystemUserRepository;

import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import static server.TestConstants.TEST_USERNAME;
import static server.TestConstants.TEST_PASSWORD1;
import static server.TestConstants.TEST_PASSWORD2;

public class UserServiceTest {
    private static UserRepository userRepository;
    private static UserService userService;

    @BeforeClass
    public static void setUp() {
        userRepository = Mockito.mock(FileSystemUserRepository.class);
        userService = new UserService(userRepository);
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

}
