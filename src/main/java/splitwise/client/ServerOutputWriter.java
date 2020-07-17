package splitwise.client;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class CommandProcessor implements Runnable {
    public static final String EXIT_COMMAND = "exit";
    public static final String SEE_LOG_FILES = "For more information see logs in logging.log";
    public static final String FAILED_READING_USER_INPUT_ERROR_MESSAGE = "Error occurred while reading user input. ";
    public static final String FAILED_CLOSING_CONSOLE_READER_ERROR_MESSAGE = "IO error occurred while closing user input reader.";


    private static Logger LOGGER = Logger.getLogger(CommandProcessor.class);

    private BufferedReader inputReader;
    private PrintWriter outputWriter;
    private SplitWiseClientApplication application;


    public CommandProcessor(BufferedReader inputReader, PrintWriter outputWriter, SplitWiseClientApplication application) {
        this.inputReader = inputReader;
        this.outputWriter = outputWriter;
        this.application = application;
    }

    @Override
    public void run() {
        String userInput = getUserInput();
        while (!equalsExitCommand(userInput)) {
            sendToServer(userInput);
            userInput = getUserInput();
        }
        application.stop();
        closeUserInputReader();
    }

    private String getUserInput() {
        String command = "";
        try {
            command = inputReader.readLine();
        } catch (IOException e) {
            LOGGER.info(FAILED_READING_USER_INPUT_ERROR_MESSAGE + SEE_LOG_FILES);
            LOGGER.error(FAILED_READING_USER_INPUT_ERROR_MESSAGE, e);
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

    private void closeUserInputReader() {
        try {
            inputReader.close();
        } catch (IOException e) {
            LOGGER.error(FAILED_CLOSING_CONSOLE_READER_ERROR_MESSAGE, e);
        }
    }

}
