package splitwise.client;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SplitWiseClientApplication {
    public static final String SERVER_HOST = "127.0.0.1";
    public static final int SERVER_PORT = 8081;

    private static final Logger LOGGER = Logger.getLogger(SplitWiseClientApplication.class);

    private Socket socket;
    private BufferedReader socketInputReader;
    private PrintWriter socketOutputWriter;


    public static void main(String[] args) {
        try {
            SplitWiseClientApplication splitWiseClient = new SplitWiseClientApplication(SERVER_HOST, SERVER_PORT);
            splitWiseClient.start();
        } catch (IOException e) {
            LOGGER.info("Unable to connect to the server.It may be shut down. For more information see logs in logging.log");
            LOGGER.error(e.getMessage(), e);
        }
    }

    public SplitWiseClientApplication(String host, int port) throws IOException {
        createSocketConnection(host, port);
        initializeSocketIOStreams();
    }


    private void createSocketConnection(String host,int port) throws IOException {
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

        Thread serverOutputWriter = new Thread(new ServerOutputWriter(socketOutputWriter, this));
        serverOutputWriter.start();
    }


    public void stop() {
        try {
            socket.close();
        } catch (IOException e) {
            LOGGER.error("Cannot close socket connection because of I/O exception.", e);
        }
    }

    public boolean isStopped() {
        return socket.isClosed();
    }

}
