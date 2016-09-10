package client;

import game.TicTacToe;
import java.io.*;
import java.net.*;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;

public class Client extends Thread{

    private Socket client;
    private DataOutputStream outStream;
    private DataInputStream inStream;
    
    private TicTacToe TTTGUI;
    private SyncedRequest clientRequest;
    
    //TODO: share object to synchronize/wait on here (for communication with TicTacToe GUI
    public Client(TicTacToe TTTGUI, SyncedRequest clientRequest){
        this.TTTGUI = TTTGUI;
        this.clientRequest = clientRequest;
    }
    
    @Override
    public void run() {
        
        client = null;
        outStream = null;
        inStream = null;
        
        System.out.println("Testing Client");
        
        try{
            //Setup
            client = new Socket("192.168.12.105", 5555);
            
            System.out.println("Connected to port");
            
            outStream = new DataOutputStream(client.getOutputStream());
            inStream = new DataInputStream(client.getInputStream());
            
            System.out.println("Succcessfully ported IO streams");
            
        } catch (UnknownHostException e){
            System.err.println(e.toString());
            System.exit(1);
        } catch (IOException e){
            System.err.println(e.toString());
            System.exit(1);
        }
        
        try{
            String response;
            
            Executors.newSingleThreadExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    String message;
                    try{
                        while (true){
                            message = receiveMessages();
                            System.out.println("Total server command: " + message);
                            TTTGUI.reactToServer(message);
                        }
                    }
                    catch (IOException e){
                        System.err.println(e);
                    }
                }
            });
            
            //TODO: properly wait for communication from either server and/or TicTacToe GUI
            while(true){
                this.clientRequest.lock.lock();
                try{
                    if (this.clientRequest.request == null){
                        this.clientRequest.actionOccurred.await();
                    }
                    
                    System.out.println("Condvar await complete: " + this.clientRequest.request);
                    
                    if (this.clientRequest.request.equals("/closeConnection")){
                        break;
                    }
                    sendMessage(this.clientRequest.request);
                    this.clientRequest.request = null;
                } catch (InterruptedException e){
                    System.err.println(e);
                } finally{
                    this.clientRequest.lock.unlock();
                }
            }
            /*
            if (!closeSocket()){
                System.err.println("Error closing socket");
            }
            */
            outStream.close();
            inStream.close();
        } catch (IOException e){
            System.err.println(e.toString());
            System.exit(1);
        }
        
    }
    
    /**
     * Send a given message to an already connected server and return a String containing the entire response.
     * @param msg - String: Message to be sent to server.
     * @return String: Response from the server.
     * @throws IOException: Issue has occurred with writing bytes through the output stream to the server.
     */
    private void sendMessage(String message) throws IOException{
        outStream.writeBytes(message + "\n");
        //outStream.writeBytes((char)0 + "\n");
    }
    
    private String receiveMessages() throws IOException{
        String response = "";
        String line;
        while ((line = inStream.readLine()) != null){
            System.out.println("Server line: "+ line);
            //Agreed protocol value for end of response (ASCII code 0; null character)
            if (line.charAt(0) == ((char)0)){
                break;
            }
            response += line;
        }
        return response;
    }
    
    /*
    //TODO: fix up
    public boolean closeSocket() throws IOException{
        for (int i = 0; i < 10; i++){
            if (sendMsgnReceive("/close").contains("/closeAck")){
                client.close();
                return true;
            }
        }
        return false;
    }
    */
}
