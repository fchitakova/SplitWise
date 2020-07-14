package splitwise.client;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class ConsoleInputReader implements Runnable {
    private static final String EXIT_COMMAND = "exit";
    private static final String SEE_LOG_FILES = "For more information see logs in logging.log";
    private static final String FAILED_READING_USER_INPUT_ERROR_MESSAGE = "Error occurred while reading user input. ";
    private static final String FAILED_CLOSING_CONSOLE_READER_ERROR_MESSAGE = "IO error occurred while closing user input reader.";


    private static Logger LOGGER = Logger.getLogger(ConsoleInputReader.class);

    private BufferedReader consoleInputReader;
    private PrintWriter serverOutputWriter;
    private SplitWiseClientApplication clientApplication;

    public ConsoleInputReader(BufferedReader consoleInputReader, PrintWriter serverOutputWriter, SplitWiseClientApplication clientConnection) {
        this.consoleInputReader = consoleInputReader;
        this.serverOutputWriter = serverOutputWriter;
        this.clientApplication = clientConnection;
    }

    @Override
    public void run() {
        String command = getUserInput();

        while (!exitCommandRead(command)) {
            sendUserInputToServer(command);
            command = getUserInput();
        }

        clientApplication.stop();
        closeUserInputReader();
    }

    private String getUserInput() {
        String command = null;
        try {
            command = consoleInputReader.readLine();
        } catch (IOException e) {
            LOGGER.info(FAILED_READING_USER_INPUT_ERROR_MESSAGE + SEE_LOG_FILES);
            LOGGER.error(FAILED_READING_USER_INPUT_ERROR_MESSAGE, e);
        }
        return command;
    }

    private boolean exitCommandRead(String command) {
        return command.equals(EXIT_COMMAND);
    }

    private void sendUserInputToServer(String input){
        if (input != null && !input.isBlank()) {
            serverOutputWriter.println(input);
        }
    }

    private void closeUserInputReader(){
        try{
            consoleInputReader.close();
        }catch(IOException e){
            LOGGER.error(FAILED_CLOSING_CONSOLE_READER_ERROR_MESSAGE,e);
        }
    }

}
