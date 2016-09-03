package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    public static ArrayList<Socket> clientSocket = new ArrayList();
    
    public static void main(String[] args) throws IOException {
        ServerSocket server = null;
        Socket clientSocket = null;
        int idCounter = 0;       
        
        try {
            server = new ServerSocket(5555);
            
            while(true) {
                System.out.println("waiting for new client...");
                clientSocket = server.accept();
                new ClientFactory(clientSocket, idCounter).start();
                idCounter += 1;
            }
        } catch(IOException e) {
            System.out.println(e);
        }
    }   
}

