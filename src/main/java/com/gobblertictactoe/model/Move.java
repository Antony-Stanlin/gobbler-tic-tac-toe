package com.gobblertictactoe.model;

public class Move {

    public final Piece piece;
    public final int fromRow,fromCol;
    public final int toRow,toCol;

    public Move(Piece piece,int fromRow,int fromCol,int toRow,int toCol){
        this.piece = piece;
        this.fromRow = fromRow;
        this.fromCol = fromCol;
        this.toRow = toRow;
        this.toCol = toCol;
    }

    @Override
    public String toString(){
        return piece.toString() + " -> " + (char)('A' + toCol) + (toRow + 1) + (fromRow>=0 ? " (moved)" : " (from pool)");
    }
}
