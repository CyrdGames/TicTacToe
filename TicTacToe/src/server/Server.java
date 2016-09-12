package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    static ArrayList<ClientFactory> clientSockets = new ArrayList();
    static int start = 2;
    static int turnNumber = 1;
    static int[][] tic = new int[3][3];
    static boolean end = false;
    
    public static void main(String[] args) throws IOException {
        ServerSocket server;
        Socket clientSocket;
        
        int idCounter = 1;       
        
        try {
            server = new ServerSocket(5555);
            
            new Console().start();
            
            System.out.println("LocalHost: " + InetAddress.getLocalHost());
            
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
    
    static void log(ClientFactory client, int type, String message) {
        String log = "Client #" + client.id + ": ";
        
        switch(type) {
            case 0:
                log = log.concat("Received message: ");
                break;
            case 1:
                log = log.concat("Sent message: ");
                break;
            default:
                break;
        }
        
        log = log.concat(message);
//        System.out.println("test");
        System.out.println(log);        
    }
    
    static void broadcast(String line) throws IOException {
        for(ClientFactory c: clientSockets) {
            c.sendMessage(line);
        }
    }
    
    static void multicast(ArrayList<ClientFactory> list, String line) throws IOException {
        for(ClientFactory c: list) {
            c.sendMessage(line);
        }
    }
    
    static void unicast(ClientFactory c, String line) throws IOException {
        c.sendMessage(line);
    }
    
    static void processMessage(String line) {
        
    }    
    
    static int getStart() {
        switch (start) {
            case 2:
                start = (int)(Math.random() * 2);
                break;
            case 0:
                start = 1;
                break;
            default:
                start = 0;
                break;
        }
        
        return start;        
    }
    
    static boolean checkWin(int r, int c, int player) {
        int col, row, diag, rdiag;
        col = row = diag = rdiag = 0;
        
        for (int i = 0; i < 3; i++) {
            if (tic[r][i] == player) {
                col++;
            }
            
            if (tic[i][c] == player) {
                row++;
            }
            
            if (tic[i][i] == player) {
                diag++;
            }
            
            if (tic[i][2-i] == player) {
                rdiag++;
            }
            
            if (row == 3 || col == 3 || diag == 3 || rdiag == 3) {
                return true;
            }
        }
        return false;
    }
    
    static void runCommand(String line) throws IOException {
        System.out.print("Console: ");
        
        String[] command = line.split(" ");
        
        try {
            switch(command[0]) {
                case "/exit":
                    System.out.println("Shutting down.");
                    System.exit(0);
                    break;
                case "/win":
                    System.out.println("Player " + command[1] + " wins!");
                    broadcast("/gameEnded " + command[1]);
                    end = true;
                    break;
                case "/reset":
                    end = false;
                    tic = new int[3][3];
                    turnNumber = 1;
                    start = 2;
                    clientSockets.clear();
                    break;
                default:
                    System.out.println("No such command.");
                    break;
            }
        } catch(Exception e) {
            System.out.println("Error: " + command[0] + " contains invalid arguments.");
        }
    }
}

