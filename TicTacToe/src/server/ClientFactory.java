package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientFactory extends Thread {
    protected Socket client;
    protected String id;
    protected DataInputStream is;
    protected DataOutputStream os;
    protected ArrayList<String> messages = new ArrayList();    
    protected boolean open = true;
    protected int playerNumber;
    
    public ClientFactory(Socket clientSocket, int id) {       
        try {
            this.id = "" + id;
            client = clientSocket;
            is = new DataInputStream(client.getInputStream());
            os = new DataOutputStream(client.getOutputStream());
        } catch(IOException e) {
            System.out.println(e);
        }
    }
    
    @Override
    public void run() {
        Server.clientSockets.add(this);     
        Server.log(this, 2, "has connected.");
        
        String line;
        
        try {
            playerNumber = Server.getStart();
            sendMessage("/setPlayer " + id + " " + playerNumber);
            sendMessage("/disable");
        } catch (IOException ex) {
            Logger.getLogger(ClientFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        while(open) {
            try {
                line = is.readLine();
                
                if(line != null) {
                    processLine(line);
                }
            } catch(IOException e) {
                System.out.println(e);
                return;
            }
        }
    }
    
    private void endSession() throws IOException {
        messages.add("/closeAck");
        sendMessages();
        
        Server.clientSockets.remove(this);
        Server.log(this, 2, "has disconnected.");
        
        is.close();
        os.close();        
        client.close();        
        open = false;
    }
    
    public void sendMessage(String str) throws IOException {
        os.writeBytes(str + "\n");
        os.writeBytes((char)0 + "\n");
    }
    
    public void sendMessages() throws IOException {
        for(String str: messages) {
            os.writeBytes(str + "\n");
            Server.log(this, 1, str);
        }
        os.writeBytes((char)0 + "\n");
        messages.clear();
    }
    
    private void processLine(String line) throws IOException {
        Server.log(this, 0, line);
        
        String[] command = line.split(" ");
        
        switch(command[0]) {
            case "/close":
                endSession();
                break;
            case "/getPlayerNumber":
                messages.add(id);
                break;
            case "/placeTic":
                if(Server.turnNumber % 2 == playerNumber) {
                    calculatePlacement(command[1], command[2]);
                } else {
                    messages.add("/denyPlacement");
                }
                break;
            default:
                messages.add(line);
                break;
        }
        
        sendMessages();
    }
    
    private void calculatePlacement(String row, String col) throws IOException {
        int r = Integer.parseInt(row);
        int c = Integer.parseInt(col);
        
        if(Server.tic[r][c] == 0 && !Server.end) {
            Server.tic[r][c] = Integer.parseInt(id);
            
            String type = "O";
            
            if(playerNumber == 1) {
                type = "X";
            }
            
            Server.broadcast("/updateGame " + r + " " + c + " " + type);
            
            if(Server.checkWin(r, c, Integer.parseInt(id))) {
                Server.broadcast("/gameEnded " + Integer.parseInt(id));
                Server.end = true;
            } else if(Server.turnNumber == 9) {
                Server.broadcast("/gameEnded 0");
                Server.end = true;
            }
        } else {
            messages.add("/denyPlacement");
        }
        
        Server.turnNumber += 1;
    }
}

