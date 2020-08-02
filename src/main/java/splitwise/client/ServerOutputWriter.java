package splitwise.client;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import static splitwise.client.SplitWiseClientApplication.LOGGER;

public class ServerOutputWriter implements Runnable {
    public static final String EXIT_COMMAND = "exit";

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
            sendToServer(userInput);

            if (equalsExitCommand(userInput)) {
                application.stop();
            }
        }
    }

    private String getUserInput() {
        String command = "";
        try {
            if (inputReader.ready()) {
                command = inputReader.readLine();
            }
        } catch (IOException e) {
            LOGGER.info(
                    "Error occurred while reading user input. For more information see logs in error.log");
            LOGGER.error("Error occurred while reading user input.", e);
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
