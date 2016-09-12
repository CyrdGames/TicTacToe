package server;

import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Console extends Thread {
    
    public Console() {
    }
    
    @Override
    public void run() {
        
        Scanner in = new Scanner(System.in);
        String line;
        
        while(true) {
            line = in.nextLine();
            try {
                processLine(line);
            } catch (IOException ex) {
                Logger.getLogger(Console.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void processLine(String line) throws IOException {
        Server.runCommand(line);
    }
}
