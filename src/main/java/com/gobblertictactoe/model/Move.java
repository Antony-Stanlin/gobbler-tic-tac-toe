package com.gobblertictactoe.model;

public class Move {

    public final Piece piece;
    public final int fromR,fromC;
    public final int toR,toC;

    public Move(Piece piece,int fromR,int fromC,int toR,int toC){
        this.piece = piece;
        this.fromR = fromR;
        this.fromC = fromC;
        this.toR = toR;
        this.toC = toC;
    }

    @Override
    public String toString(){
        return piece.toString() + " -> " + (char)('A' + toC) + (toR + 1) + (fromR>=0 ? " (moved)" : " (from pool)");
    }
}
