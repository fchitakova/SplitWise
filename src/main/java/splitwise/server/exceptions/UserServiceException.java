package splitwise.server.exceptions;


public class UserServiceException extends Exception{
    public UserServiceException(String message,Throwable cause){
        super(message, cause);
    }
}
