package splitwise.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ServerInputReader implements Runnable{
    private BufferedReader serverInputReader;

    public ServerInputReader(InputStream inputStream){
        this.serverInputReader = new BufferedReader(new InputStreamReader(inputStream));
    }

    public void run() {
       while(true){
           try {
               String serverResponse = serverInputReader.readLine();
               System.out.println(serverResponse);
           }catch(IOException e){
               //log it
               //rethrow ServerConnectionException with descriptive message
           }finally {
               //close the socket
           }
       }
    }
}
