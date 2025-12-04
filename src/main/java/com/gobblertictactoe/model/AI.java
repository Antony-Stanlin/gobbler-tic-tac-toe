package com.gobblertictactoe.model;

import java.util.List;

public interface AI {
    Move chooseMove(Board board, Player me, Player opponent, List<Move> legalMoves);
}
