package splitwise.server;

import splitwise.server.commands.Command;
import splitwise.server.model.User;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static splitwise.server.model.SplitWiseConstants.SERVER_STARTED;


public class SplitWiseServer {
    public static final int SERVER_PORT = 8080;
    public static final int MAXIMUM_CONNECTIONS_COUNT = 100;
    public static final String FAILED_SERVER_SOCKET_CREATION = "Server socket creation failed because of unexpected IOException during socket creation.";
    private ServerSocket serverSocket;
    private ExecutorService executorService;
    private CommandFactory commandFactory;

    public static void main(String[]args){
        SplitWiseServer splitWiseServer;
        try{
            splitWiseServer = new SplitWiseServer(SERVER_PORT);
        }catch(IOException e){
            System.out.println(FAILED_SERVER_SOCKET_CREATION);
            //log it maybe
            return;
        }

        splitWiseServer.start();


    }

    public SplitWiseServer(int port) throws IOException{
        serverSocket = new ServerSocket(port);
        executorService = Executors.newFixedThreadPool(MAXIMUM_CONNECTIONS_COUNT);
        commandFactory = new CommandFactory(new UserService());
    }

    public void start(){
        System.out.println(SERVER_STARTED);
        while(true){
            try{
                Socket clientSocket = this.serverSocket.accept();
                executorService.execute(new ClientConnection(clientSocket,this));
            }catch(IOException e){
                //log it throw new RuntimeException(ERROR_ACCEPTING_CLIENT_CONNECTION,e);
                //log it to server console
            }
        }
      //  this.executorService.shutdown();
    }

    public String processClientInput(String input){
        Command command = commandFactory.createCommand(input);
        String commandExecutionResult = command.execute();
        return commandExecutionResult;
    }

}
