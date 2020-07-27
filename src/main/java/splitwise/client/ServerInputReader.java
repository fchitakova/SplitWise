package splitwise.client;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;

public class ServerInputReader implements Runnable {
    private static final String APP_STOPPED = "app stopped";
    private static final String APPLICATION_IS_DOWN = "SplitWise is down. Try connecting later.";
    private static final String INPUT_MESSAGE_INDICATOR = "Server >>> ";


    private static Logger LOGGER = Logger.getLogger(ServerInputReader.class);

    private BufferedReader reader;
    private SplitWiseClientApplication application;

    public ServerInputReader(BufferedReader reader, SplitWiseClientApplication application) {
        this.reader = reader;
        this.application = application;
    }


    @Override
    public void run() {
        while (application.isRunning()) {
            try {
                String input = getInput();
                print(input, System.out);
            } catch (IOException e) {
                if (application.isRunning()) {
                    LOGGER.info("Error occurred while getting SplitWise input.For more information see logs in logging.log");
                    LOGGER.error("Error occurred while getting SplitWise input." + e.getMessage(), e);
                    return;
                }
            }
        }
    }


    private String getInput() throws IOException {
        return reader.readLine();
    }


    private void print(String message, PrintStream stream) {
        if (equalsApplicationStopped(message)) {
            stream.println(APPLICATION_IS_DOWN);
            application.stop();
        } else {
            stream.println(INPUT_MESSAGE_INDICATOR + message);
        }
    }

    boolean equalsApplicationStopped(String message) {
        return message.equals(APP_STOPPED);
    }
}
