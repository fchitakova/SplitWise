package splitwise.client;

import logger.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Path;

public class SplitWiseClientApplication {
  public static final String SERVER_HOST = "127.0.0.1";
  public static final int SERVER_PORT = 8081;

  static Logger LOGGER;

  private Socket socket;
  private BufferedReader socketInputReader;
  private BufferedReader userInputReader;
  private PrintWriter socketOutputWriter;

  public SplitWiseClientApplication(String host, int port) throws IOException {
    createSocketConnection(host, port);
    initializeSocketIOStreams();

    userInputReader = new BufferedReader(new InputStreamReader(System.in));
  }

  public static void main(String[] args) {
    Path logDirectory = Path.of(args[0]);
    Path logFile = Path.of(args[1]);
    LOGGER = new Logger(logDirectory, logFile);

    try {
      SplitWiseClientApplication splitWiseClient =
          new SplitWiseClientApplication(SERVER_HOST, SERVER_PORT);
      splitWiseClient.start();
    } catch (IOException e) {
      LOGGER.info(
          "Unable to connect to the server.It may be shut down. For more information see logs in error.log");
      LOGGER.error(e.getMessage(), e);
    }
  }

  private void createSocketConnection(String host, int port) throws IOException {
    try {
      socket = new Socket(host, port);
      initializeSocketIOStreams();
    } catch (IOException e) {
      throw new IOException("Socket creation failed because of IO exception.", e);
    }
  }

  private void initializeSocketIOStreams() throws IOException {
    try {
      socketInputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      socketOutputWriter = new PrintWriter(socket.getOutputStream(), true);
    } catch (IOException e) {
      throw new IOException("Error occurred during getting socket IO streams.", e);
    }
  }

  public void start() {
    Thread serverInputReader = new Thread(new ServerInputReader(socketInputReader, this));
    serverInputReader.start();

    Thread serverOutputWriter =
        new Thread(new ServerOutputWriter(userInputReader, socketOutputWriter, this));
    serverOutputWriter.start();
  }

  public void stop() {
    try {
      socket.close();
    } catch (IOException e) {
      LOGGER.error(e.getMessage(), e);
    }
    closeUserInputReader();
  }

  private void closeUserInputReader() {
    try {
      userInputReader.close();
    } catch (IOException e) {
      LOGGER.error("IO error occurred while closing user input reader.", e);
    }
  }

  public boolean isStopped() {
    return socket.isClosed();
  }

  public boolean isRunning() {
    return !isStopped();
  }
}
