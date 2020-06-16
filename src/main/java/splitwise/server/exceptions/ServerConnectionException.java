package splitwise.server.exceptions;

public class ServerConnectionException extends Exception {
    public ServerConnectionException(String message,Throwable cause){
        super(message,cause);
    }
}
