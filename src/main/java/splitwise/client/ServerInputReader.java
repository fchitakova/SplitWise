package splitwise.client;


import java.io.BufferedReader;
import java.io.IOException;

import static splitwise.client.SplitWiseClientApplication.LOGGER;


public class ServerInputReader implements Runnable {
    private static final String APP_STOPPED = "app stopped";
    private static final String SERVER_IS_DOWN = "SplitWise is down. Try connecting later.";
    private static final String INPUT_MESSAGE_INDICATOR = "Server >>> ";


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
                String input = reader.readLine();
                processServerInput(input);
            } catch (IOException e) {
                if (application.isRunning()) {
                    LOGGER.info("Error occurred while getting SplitWise input.For more information see logs in error.log");
                    LOGGER.error("Error occurred while getting SplitWise input." + e.getMessage(), e);
                }
            }
        }
    }


    private void processServerInput(String message) {
        if (equalsApplicationStopped(message)) {
            System.out.println(SERVER_IS_DOWN);
            application.stop();
        } else {
            System.out.println(INPUT_MESSAGE_INDICATOR + message);
        }
    }

    boolean equalsApplicationStopped(String message) {
        return message.equals(APP_STOPPED);
    }
}
