package splitwise.server.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.log4j.Logger;
import static splitwise.server.server.SplitWiseServer.*;

public class ServerAdministrationCommandExecutor implements Runnable{
    public static final String STOP_COMMAND = "stop";

    private static final String CANNOT_READ_CONSOLE_INPUT = "ServerConsoleReader cannot read console input stream.";
    private static Logger LOGGER = Logger.getLogger(ServerAdministrationCommandExecutor.class);

    private BufferedReader reader;
    private SplitWiseServer splitWiseServer;

    public ServerAdministrationCommandExecutor(SplitWiseServer splitWiseServer){
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
            splitWiseServer.stop();
        } catch (IOException e) {
            LOGGER.info(CANNOT_READ_CONSOLE_INPUT + SEE_LOG_FILE);
            LOGGER.error(CANNOT_READ_CONSOLE_INPUT,e);
        }
    }
}
