/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import client.SyncedRequest;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.util.concurrent.locks.Lock;

/**
 *
 * @author Michael Li
 */
public class TicTacToe {
    
    TTT game = new TTT();
    GridBagConstraints cell = new GridBagConstraints(); 
    JFrame frame;
    JPanel panel;
    JLabel label;
    JButton[][] TTTGrid;
    private int numRows, numCols;
    private int turn = 0;
    private int cellWidth, cellHeight;
    private boolean canGo = true;
    private SyncedRequest clientRequest;
    //TODO: possibly obsolete; consider deletion
    private String symbol;
    private int playerNum;
    
    public TicTacToe(SyncedRequest clientRequest) {
        //TODO: may not need to keep a reference to request in TicTacToe class; used in GridListener
        this.clientRequest = clientRequest;
        
        frame = new JFrame("Tic Tac Toe");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(2,1));
        
        panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        
        label = new JLabel("", SwingConstants.CENTER);
        label.setFont(new Font("Serif", Font.PLAIN, 40));
        
        frame.add(label);
        
        //create game grid
        numRows = numCols = 3;
        cellWidth = cellHeight = 80;
        
        TTTGrid = new JButton[numRows][numCols];
        
        for(int row = 0; row < numRows; row++) {
            for(int col = 0; col < numCols; col++) {
                TTTGrid[row][col] = new JButton("   ");
                TTTGrid[row][col].setFont(new Font("Serif", Font.PLAIN, 30));
                TTTGrid[row][col].setSize(cellWidth, cellHeight);
                TTTGrid[row][col].setActionCommand("Empty");
                TTTGrid[row][col].addActionListener(new GridListener(row, col, this.clientRequest));
                TTTGrid[row][col].setOpaque(true);
                TTTGrid[row][col].setForeground(Color.BLACK);
                TTTGrid[row][col].setBackground(Color.WHITE);
                
                cell.ipady = 50;
                cell.ipadx = 38;   
                cell.gridx = col;
                cell.gridy = row;
                
                panel.add(TTTGrid[row][col], cell);
            }
        }
        
        cell.ipady = 50;
        cell.ipadx = 100;        
        cell.gridx = 0;
        cell.gridy = 0;
        frame.add(panel, cell);
                
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.setVisible(true);
    }
    
    public void reactToServer(String serverMessage){
        String[] serverCommand = serverMessage.split(" ");
        switch(serverCommand[0]){
            case "/setPlayer":
                this.playerNum = Integer.parseInt(serverCommand[1]);
                if (Integer.parseInt(serverCommand[2]) == 1){
                    this.symbol = "X";
                    label.setText("You are player " + this.playerNum + "; you are starting.");
                } else {
                    this.symbol = "O";
                    label.setText("You are player " + this.playerNum + "; you must wait for the starting player.");
                }
                break;
            case "/updateGame":
                int row = Integer.parseInt(serverCommand[1]), column = Integer.parseInt(serverCommand[2]);
                TTTGrid[row][column].setText(serverCommand[3]);
                if(this.symbol.equals(serverCommand[3])){
                    label.setText("Waiting for the other player's turn...");
                } else{
                    label.setText("It is currently your turn.");
                }
                break;
            case "/denyPlacement":
                label.setText("It is not your turn; wait for the other player to go.");
                break;
            case "/gameEnded":
                int winner = Integer.parseInt(serverCommand[1]);
                if (winner == 0){
                    label.setText("You have tied!");
                } else if (winner == this.playerNum){
                    label.setText("Congratulations! You have won!");
                } else{
                    label.setText("Better luck next time! You have lost.");
                }
                this.clientRequest.lock.lock();
                try{
                    this.clientRequest.request = "/closeConnection";
                    this.clientRequest.actionOccurred.signalAll();
                } finally{
                    this.clientRequest.lock.unlock();
                }
            default:
                System.err.println("Unknown server command");
        }
    }
/*
    //TODO: obsolete implementation
    private static void runGUI() {
        TicTacToe TTTGUI = new TicTacToe();
    }

    //TODO: obsolete implementation
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                runGUI();
            }
        });
    }
    */
}