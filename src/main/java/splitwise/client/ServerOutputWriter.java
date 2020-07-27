package splitwise.client;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class ServerOutputWriter implements Runnable {
    public static final String EXIT_COMMAND = "exit";

    private static Logger LOGGER = Logger.getLogger(ServerOutputWriter.class);

    private BufferedReader inputReader;
    private PrintWriter outputWriter;
    private SplitWiseClientApplication application;


    public ServerOutputWriter(BufferedReader reader, PrintWriter outputWriter, SplitWiseClientApplication application) {
        this.inputReader = reader;
        this.outputWriter = outputWriter;
        this.application = application;
    }

    @Override
    public void run() {
        while (application.isRunning()) {
            String userInput = getUserInput();
            if (equalsExitCommand(userInput)) {
                application.stop();
            } else {
                sendToServer(userInput);
            }

            application.stop();
        }
    }

    private String getUserInput() {
        String command = "";
        try {
            command = inputReader.readLine();
        } catch (IOException e) {
            if (application.isRunning()) {
                LOGGER.info(
                        "Error occurred while reading user input. For more information see logs in logging.log");
                LOGGER.error("Error occurred while reading user input.", e);
            }
        }
        return command;
    }

    private boolean equalsExitCommand(String command) {
        return command.equals(EXIT_COMMAND);
    }

    private void sendToServer(String input) {
        if (!input.isBlank()) {
            outputWriter.println(input);
        }
    }

}
