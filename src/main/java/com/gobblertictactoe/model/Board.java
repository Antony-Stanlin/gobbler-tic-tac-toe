package com.gobblertictactoe.model;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

public class Board {

    private final int rows;
    private final int cols;
    private final Deque<Piece> board[][];

    @SuppressWarnings("unchecked")
    public Board(int n){
        this.rows = n;
        this.cols = n;
        board = new Deque[n][n];
        for(int i=0;i<rows;i++){
            for(int j=0;j<cols;j++){
                board[i][j] = new ArrayDeque<>();
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
    public Deque<Piece> stackAt(int row, int col) {
        ensureInBounds(row, col);
        return board[row][col];
    }

    public Optional<Piece> topAt(int row,int col){
        ensureInBounds(row,col);
        return board[row][col].isEmpty() ? Optional.empty() : Optional.of(board[row][col].peek());
    }

    public boolean canPlace(Piece p,int row,int col){
        ensureInBounds(row,col);
        return board[row][col].isEmpty() || p.size() > board[row][col].peek().size();
    }

    public void place(Piece p,int row,int col){
        ensureInBounds(row,col);
        board[row][col].push(p);
    }

    public Piece popTop(int row,int col){
        ensureInBounds(row,col);
        return board[row][col].pop();
    }

    /**
     * Check win for playerId by looking at top pieces (correct row/col indexing).
     */
    public boolean checkWin(char playerId){
        // rows
        for (int r = 0; r < rows; r++) {
            boolean ok = true;
            for (int c = 0; c < cols; c++) {
                Optional<Piece> t = topAt(r, c);
                if (!t.isPresent() || t.get().owner() != playerId) { ok = false; break; }
            }
            if (ok) return true;
        }
        // cols
        for (int c = 0; c < cols; c++) {
            boolean ok = true;
            for (int r = 0; r < rows; r++) {
                Optional<Piece> t = topAt(r, c);
                if (!t.isPresent() || t.get().owner() != playerId) { ok = false; break; }
            }
            if (ok) return true;
        }
        // main diag
        boolean ok = true;
        for (int i = 0; i < Math.min(rows, cols); i++) {
            Optional<Piece> t = topAt(i, i);
            if (!t.isPresent() || t.get().owner() != playerId) { ok = false; break; }
        }
        if (ok) return true;
        // anti-diag
        ok = true;
        for (int i = 0; i < Math.min(rows, cols); i++) {
            Optional<Piece> t = topAt(i, cols - 1 - i);
            if (!t.isPresent() || t.get().owner() != playerId) { ok = false; break; }
        }
        return ok;
    }
}
