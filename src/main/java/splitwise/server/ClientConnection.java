package splitwise.server;


import splitwise.server.exceptions.ClientConnectionException;

import java.io.*;
import java.net.Socket;

public class ClientConnection implements Runnable{

    public static final String ERROR_READING_SOCKET_INPUT = "Error reading socket input.";
    private Socket socket;
    private BufferedReader socketInputReader;
    private PrintWriter socketOutputWriter;
    private SplitWiseServer splitWiseServer;

    public ClientConnection(Socket socket,SplitWiseServer splitWiseServer) throws IOException {
        this.socket = socket;
        this.socketInputReader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.socketOutputWriter =new PrintWriter(socket.getOutputStream(),true);
        this.splitWiseServer = splitWiseServer;
    }

    public void run() {
        //print info about commands
        try {
            while(!socket.isClosed()) {
                String clientInput = readClientInput();
                String serverResponse = splitWiseServer.processClientInput(clientInput);
                sendServerResponseToClient(serverResponse);
            }
        }catch(ClientConnectionException e){
            //log it
        }finally {
            UserContextHolder.usernameHolder.remove();
            closeSocketConnection();
        }
    }


    private String readClientInput() throws ClientConnectionException {
        String input;
        try{
            input = socketInputReader.readLine();
        }catch(IOException e){
            throw new ClientConnectionException(ERROR_READING_SOCKET_INPUT,e);
        }
        return input;
    }

    private void sendServerResponseToClient(String response) {
        socketOutputWriter.println(response);
    }

    private void closeSocketConnection(){
        try{
            this.socket.close();
        }catch (IOException e){
            //log it with message Error closing client socket
        }
    }

}
