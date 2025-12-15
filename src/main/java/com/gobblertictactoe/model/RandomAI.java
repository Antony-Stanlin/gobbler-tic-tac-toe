package com.gobblertictactoe.model;

import java.util.List;
import java.util.Random;

public class RandomAI implements AI{

    private final Random random = new Random();

    @Override
    public Move chooseMove(Board board, Player me, Player opponent, List<Move> legalMoves) {
        if(legalMoves == null || legalMoves.isEmpty()) return null;
        return legalMoves.get(random.nextInt(legalMoves.size()));
    }
}
