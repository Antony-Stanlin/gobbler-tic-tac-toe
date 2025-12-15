package com.gobblertictactoe.model;

import java.util.Optional;

import com.gobblertictactoe.util.Stack;

public class Board {

    private final int rows;
    private final int cols;
    private final Stack<Piece> board[][];

    @SuppressWarnings("unchecked")
    public Board(int n){
        this.rows = n;
        this.cols = n;
        board = new Stack[n][n];
        for(int i=0;i<rows;i++){
            for(int j=0;j<cols;j++){
                board[i][j] = new Stack<>();
            }
        }
    }

    public int size(){ return rows; }

    private void ensureInBounds(int row, int col){
        if(row < 0 || row >= rows || col < 0 || col >= cols)
            throw new IndexOutOfBoundsException("Board index out of bounds");
    }

    /**
     * Expose the stack at (row,col). View may inspect but should not mutate internals.
     */
    public Stack<Piece> stackAt(int row, int col) {
        ensureInBounds(row, col);
        return board[row][col];
    }

    public Optional<Piece> topAt(int row,int col){
        ensureInBounds(row,col);
        return board[row][col].isEmpty() ? Optional.empty() : Optional.of(board[row][col].peek());
    }

    public boolean canPlace(Piece piece,int row,int col){
        ensureInBounds(row,col);
        return board[row][col].isEmpty() || piece.size() > board[row][col].peek().size();
    }

    public void place(Piece piece,int row,int col){
        ensureInBounds(row,col);
        board[row][col].push(piece);
    }

    public Piece popTop(int row,int col){
        ensureInBounds(row,col);
        return board[row][col].pop();
    }
}
