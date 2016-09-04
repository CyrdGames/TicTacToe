package client;

import java.io.*;
import java.net.*;

public class Client {

    private static Socket client;
    private static DataOutputStream outStream;
    private static DataInputStream inStream;
    
    public static void main(String[] args) {
        
        client = null;
        outStream = null;
        inStream = null;
        
        try{
            //Setup
            client = new Socket("192.168.12.110", 5555);
            
            System.out.println("Connected to port");
            
            outStream = new DataOutputStream(client.getOutputStream());
            inStream = new DataInputStream(client.getInputStream());
            
            System.out.println("Succcessfully ported IO streams");
            
        } catch (UnknownHostException e){
            System.err.println(e.toString());
        } catch (IOException e){
            System.err.println(e.toString());
        }
        
        if (client != null && outStream != null && inStream != null){
            try{
                String response;
                for(int i = 0; i < 10; i++){
                    System.out.println("Sending message "+ i);
                    response = sendMsgnReceive("Test messages ["+ i + "]");
                    //System.out.println("Total Response: "+ response);
                }
                if (!closeSocket()){
                    System.err.println("Error closing socket");
                }
                outStream.close();
                inStream.close();
            } catch (IOException e){
                System.err.println(e.toString());
            }
        }
        
    }
    
    /**
     * Send a given message to an already connected server and return a String containing the entire response.
     * @param msg - String: Message to be sent to server.
     * @return String: Response from the server.
     * @throws IOException: Issue has occurred with writing bytes through the output stream to the server.
     */
    private static String sendMsgnReceive(String msg) throws IOException{
        outStream.writeBytes(msg + "\n");
        
        String response = "";
        String line;
        while ((line = inStream.readLine()) != null){
            System.out.println("Server: "+ line);
            //Agreed protocol value for end of response (ASCII code 0; null character)
            if (line.charAt(0) == ((char)0)){
                break;
            }
            response += line;
        }
        return response;
    }
    
    private static boolean closeSocket() throws IOException{
        for (int i = 0; i < 10; i++){
            if (sendMsgnReceive("/close").contains("/closeAck")){
                client.close();
                return true;
            }
        }
        return false;
    }
    
}
