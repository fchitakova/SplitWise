package splitwise.server.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;


public class ServerAdminCommandExecutor extends Thread {
    public static final String STOP_COMMAND = "stop";

    private static Logger LOGGER = Logger.getLogger(ServerAdminCommandExecutor.class);

    private BufferedReader reader;
    private SplitWiseServer splitWiseServer;

    public ServerAdminCommandExecutor(SplitWiseServer splitWiseServer) {
        this.reader = new BufferedReader(new InputStreamReader(System.in));
        this.splitWiseServer = splitWiseServer;
    }

    @Override
    public void run() {
        try {
            String command = reader.readLine();
            while(!command.equals(STOP_COMMAND)){
                command = reader.readLine();
            }
            reader.close();
            splitWiseServer.stop();
        } catch (IOException e) {
            LOGGER.info("ServerConsoleReader cannot read console input stream. See logging.log for more information.");
            LOGGER.error("ServerConsoleReader cannot read console input stream.", e);
        }
    }
}
