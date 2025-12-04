package com.gobblertictactoe.model;

public class Piece implements Comparable<Piece> {

    private final char owner;
    private final int size;
    private static final String RESET = "\u001B[0m";
    private final String colorCode;

    public Piece(char owner,int size,String colorCode){
        this.owner = Character.toUpperCase(owner);
        this.size = size;
        this.colorCode = colorCode;
    }

    public char owner() { return owner; }

    public int size() { return size; }

    @Override
    public String toString(){ return colorCode + owner + size + RESET; }

    @Override
    public int compareTo(Piece o){
        return Integer.compare(this.size, o.size);
    }

    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(!(o instanceof Piece))   return false;
        Piece p = (Piece)o;
        return owner == p.owner && size == p.size;
    }

    @Override
    public int hashCode(){
        int result = Character.hashCode(owner);
        result = 31 * result + Integer.hashCode(size);
        return result;
    }
}
