package splitwise.client;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;

public class InputReader implements Runnable {
    private static final String SEE_LOG_FILES = "For more information see logs in logging.log";
    private static final String INPUT_READING_FAILED = "Error occurred while getting SplitWise input.";
    private static final String APP_STOPPED = "app stopped";
    private static final String APPLICATION_IS_DOWN = "SplitWise is down. Try connecting later.";
    private static final String INPUT_MESSAGE_INDICATOR = "Server >>> ";


    private static Logger LOGGER = Logger.getLogger(InputReader.class);

    private BufferedReader reader;
    private SplitWiseClientApplication application;

    public InputReader(BufferedReader reader, SplitWiseClientApplication application) {
        this.reader = reader;
        this.application = application;
    }


    @Override
    public void run() {
        while (true) {
            try {
                String input = getInput();
                print(input, System.out);
            } catch (IOException e) {
                if (!application.isStopped()) {
                    LOGGER.info(INPUT_READING_FAILED + SEE_LOG_FILES);
                    LOGGER.error(INPUT_READING_FAILED + e.getMessage(), e);
                    return;
                }
            }
        }
    }

    private String getInput() throws IOException {
        return reader.readLine();
    }


    private void print(String message, PrintStream stream) {
        if (isServerStopped(message)) {
            stream.println(APPLICATION_IS_DOWN);
            application.stop();
        } else {
            stream.println(INPUT_MESSAGE_INDICATOR + message);
        }
    }

    boolean isServerStopped(String serverResponse) {
        return serverResponse.equals(APP_STOPPED);
    }
}
