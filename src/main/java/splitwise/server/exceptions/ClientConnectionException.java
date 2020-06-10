package splitwise.server.exceptions;

public class ClientConnectionException extends RuntimeException{
    public ClientConnectionException(String message,Throwable cause){
        super(message,cause);
    }
}
