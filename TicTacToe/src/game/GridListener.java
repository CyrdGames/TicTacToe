package game;

import client.SyncedRequest;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.locks.Lock;

public class GridListener implements ActionListener {
    private int row;
    private int column;
    private SyncedRequest clientRequest;

    public GridListener(int row, int column, SyncedRequest clientRequest){
        super();
        this.row = row;
        this.column = column;
        this.clientRequest = clientRequest;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.clientRequest.lock.lock();
        try{
            this.clientRequest.request = "/placeTic " + row + " " + column;
            this.clientRequest.actionOccurred.signalAll();
        } finally{
            this.clientRequest.lock.unlock();
        }
        /*
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

                }
            }
        }
    }
        */
    }
}


//TODO: Delete.
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
