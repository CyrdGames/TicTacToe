package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientFactory extends Thread {
    protected Socket client;
    protected String id;
    
    public ClientFactory(Socket clientSocket, int id) {
        client = clientSocket;
        this.id = "" + id;
    }
    
    @Override
    public void run() {
        DataInputStream is = null;
        DataOutputStream os = null;
        
        try {
            is = new DataInputStream(client.getInputStream());
            os = new DataOutputStream(client.getOutputStream());
        } catch(IOException e) {
            return;
        }
                
        Server.clientSocket.add(client);
        
        System.out.println("client #" + id + " connected.");
        
        String line;
        
        while(true) {
            try {
                line = is.readLine();
                
                if((line == null)) {
                    continue;
                } else if(line.equalsIgnoreCase("/close")) {
                    os.writeBytes("/closeAck");
                    os.writeBytes((char)0 + "\n");
                    //os.flush();
                    is.close();
                    os.close();
                    Server.clientSocket.remove(client);
                    client.close();
                    System.out.println("Client #" + id + " has disconnected.");
                    break;
                }
                
                System.out.println("Client " + id + ": " + line);
                os.writeBytes(line + " OK!\n");
                os.writeBytes((char)0 + "\n");
            } catch(IOException e) {
                return;
            }
        }
    }
}
