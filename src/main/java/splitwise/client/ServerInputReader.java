package splitwise.client;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;

public class ServerInputReader implements Runnable {
    private static final String SEE_LOG_FILES = "For more information see logs in logging.log";
    private static final String FAIL_READING_SERVER_INPUT_INFO_MESSAGE = "Error occurred while getting server response. "+SEE_LOG_FILES;
    private static final String FAIL_READING_SERVER_INPUT_ERROR_MESSAGE = "IO error occurred while trying to read server input stream.";
    private static final String SERVER_STOPPED = "server stopped";
    private static final String SERVER_WENT_DOWN = "SplitWise server went down. Try connecting later.";
    private static final String SERVER_RESPONSE_INDICATOR = "Server >>> ";


    private static Logger LOGGER = Logger.getLogger(ServerInputReader.class);

    private BufferedReader serverInputReader;
    private SplitWiseClientApplication clientApplication;

    public ServerInputReader(BufferedReader serverInputReader,SplitWiseClientApplication clientApplication){
        this.serverInputReader = serverInputReader;
        this.clientApplication = clientApplication;
    }


    @Override
    public void run() {
        while(!clientApplication.isStopped()) {
            try {
                String serverResponse = serverInputReader.readLine();
                printServerResponse(serverResponse);
            } catch (IOException e) {
                if(!clientApplication.isStopped()){
                    LOGGER.info(FAIL_READING_SERVER_INPUT_INFO_MESSAGE);
                    LOGGER.error(FAIL_READING_SERVER_INPUT_ERROR_MESSAGE, e);
                }
            }
        }
    }

    private void printServerResponse(String serverResponse) {
        if(!isServerStopped(serverResponse)){
            System.out.println(SERVER_RESPONSE_INDICATOR + serverResponse);
        }else {
            System.out.println(SERVER_WENT_DOWN);
            clientApplication.stop();
        }
    }

    boolean isServerStopped(String serverResponse){
        return serverResponse.equals(SERVER_STOPPED);
    }
}
