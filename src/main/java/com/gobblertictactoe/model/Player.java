package com.gobblertictactoe.model;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

public class Player {

    private final char id;
    private final String color;
    private final String name;
    private static final String RESET = "\u001B[0m";
    private final Set<Piece> pool = new LinkedHashSet<>(); // stable iteration order

    public Player(char id, String color,String name){
        this.id = Character.toUpperCase(id);
        this.color = color;
        this.name=name;
    }

    @Override
    public String toString(){
        return color + name + RESET;
    }

    public char id(){ return id; }

    public String colorCode(){ return color; }

    /**
     * Adds a piece to pool. Returns false if a piece with same owner+size already exists.
     */
    public boolean addToPool(Piece p){
        if (p == null) return false;
        if (p.owner() != id) throw new IllegalArgumentException("Piece owner mismatch");
        return pool.add(p);
    }
    
    public boolean removeFromPool(Piece p){ return pool.remove(p); }

    /**
     * Find a piece in pool by size (returns Optional of the real Piece object).
     */
    public Optional<Piece> findInPoolBySize(int size){
        for (Piece piece : pool) 
            if (piece.size() == size) 
                return Optional.of(piece);

        return Optional.empty();
    }

    /**
     * Read-only view of pool to prevent external mutation.
     */
    public Set<Piece> poolView() { return Collections.unmodifiableSet(pool); }

    public boolean isPoolEmpty(){ return pool.isEmpty(); }


}
