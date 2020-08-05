package splitwise.server.exceptions;

public class ClientConnectionException extends Exception {
  public ClientConnectionException(String message, Throwable cause) {
    super(message, cause);
  }
}
