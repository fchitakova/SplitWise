package main.splitwise.client;

import java.io.*;
import java.net.Socket;

public class SplitWiseClient {
    public static final String SERVER_HOST = "127.0.0.1";
    public static final int SERVER_PORT = 8080;
    public static final String CONNECTING_TO_SERVER_FAILED = "Connecting to server failed. See log file for more information.";
    public static final String IO_ERROR_DURING_SOCKET_CREATION = "I/O error when establishing connection.";
    public static final String LOGOUT_COMMAND = "logout";
    public static final String GOODBYE_MESSAGE = "Split Wise Server: GoodBye";

    private Socket socket;
    private BufferedReader clientInputReader;
    private ServerInputReader serverInputReader;
    private PrintWriter serverOutputWriter;


    public static void main(String[]args)  {
        SplitWiseClient splitWiseClient;
        try {
             splitWiseClient = new SplitWiseClient(SERVER_HOST,SERVER_PORT);
        }catch(IOException e){
            //LOG it with message I/O exception while connecting to server
            System.out.println(CONNECTING_TO_SERVER_FAILED);
            return;
        }
        splitWiseClient.start();
    }

    public SplitWiseClient(String host,int port) throws IOException {
        try{
        this.socket = new Socket(host,port);
        this.clientInputReader = new BufferedReader(new InputStreamReader(System.in));
        this.initializeIOPerformers();
        }catch(IOException e){
            throw new IOException(IO_ERROR_DURING_SOCKET_CREATION,e);
        }
    }

    private void initializeIOPerformers() throws IOException {
        this.serverInputReader = new ServerInputReader(socket.getInputStream());
        this.serverOutputWriter = new PrintWriter(socket.getOutputStream(),true);
    }

    public void start(){
       new Thread(serverInputReader).start();
           String command;
           try {
               command = clientInputReader.readLine();
               while (!command.equals(LOGOUT_COMMAND)){
                   serverOutputWriter.println(command);
                   command = clientInputReader.readLine();
               }
               endConnection();
               System.out.println(GOODBYE_MESSAGE);
           }catch(IOException e){
               //log it throw new RuntimeException("IO Error while getting client input",e);
           }
    }

     private void endConnection(){
         try{
             this.socket.close();
         }catch (IOException ioException){
             //log it
         }
     }

}
