package client;
import game.TicTacToe;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ClientManager {
    
    public static void main(String[] args){
        SyncedRequest clientRequest = new SyncedRequest();
        
        TicTacToe TTTGUI = new TicTacToe(clientRequest);
        Client client = new Client(TTTGUI, clientRequest);
        client.start();
    }
}
