/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

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
    
    public TicTacToe() {
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
                TTTGrid[row][col].addActionListener(new gridListener());
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

    class gridListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {            
            for(int row = 0; row < numRows; row++) {
                for(int col = 0; col < numCols; col++) {
                    if(e.getSource().equals(TTTGrid[row][col]) && e.getActionCommand().equals("Empty") && canGo) {                    	
                    	//alternate turns
                    	turn += 1;
                    	                    	
                    	if(turn % 2 == 1) {
                    		TTTGrid[row][col].setText("X");
                    		game.placeTick(1, row, col);
                    	} else {
                    		TTTGrid[row][col].setText("O");
                    		game.placeTick(2, row, col);
                    	}     
                    	
                        TTTGrid[row][col].setActionCommand("Full");
                        
                        //check if game is over
                        if(game.checkWinner() || turn == 9) {                        	
                        	int winner = game.getWinner();
                        	switch(winner) {
                        		case 1:
                        			label.setText("The winner is player X!");
                        			break;
                        		case 2:
                        			label.setText("The winner is player O!");
                        			break;
                    			default: 
                    				label.setText("Tie!");
                    				break;
                        	}
                        	//disable future clicks
                        	canGo = false;
                        }
                    }
                }
            }
        }
    }
    
    private static void runGUI() {
        TicTacToe TTTGUI = new TicTacToe();
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                runGUI();
            }
        });
    }
}

class TTT {

	private int[][] board = new int[3][3];
	private int winner = 0;
	public boolean gameOver = false;
		
	public boolean checkWinner() {
		if(checkCol() || checkRow() || checkDiag()) {
			gameOver = true;
		}
		return gameOver;
	}
	
	private boolean checkCol() {
		for(int col = 0; col < board[0].length; col++) {
			if(board[0][col] == board[1][col] && board[1][col] == board[2][col]) {				
				if(board[0][col] > 0) {
					winner = board[0][col];
					return true;
				}
			}		
		}
		return false;	
	}
	
	private boolean checkRow() {
		for(int row = 0; row < board.length; row++) {
			if(board[row][0] == board[row][1] && board[row][1] == board[row][2]) {
				if(board[row][0] > 0) {
					winner = board[row][0];
					return true;
				}
			}		
		}
		return false;	
	}
	
	private boolean checkDiag() {
		if(board[0][0] > 0 && board[0][0] == board[1][1] && board[1][1] == board[2][2] || board[0][2] > 0 && board[0][2] == board[1][1] && board[1][1] == board[2][0]) {
			if(board[1][1] > 0) {
				winner = board[1][1];
				return true;
			}			
		}		
		return false;		
	}
	
	/**
	 * 
	 * @param value = player number
	 * @param row = row
	 * @param col = column
	 */
	public void placeTick(int value, int row, int col) {
		board[row][col] = value;
	}
	
	/**
	 * 
	 * @return winner of game
	 */
	public int getWinner() {
		return winner;
	}
}
