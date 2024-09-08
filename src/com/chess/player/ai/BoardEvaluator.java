package com.chess.player.ai;

import com.chess.engine.board.Board;

public interface BoardEvaluator {
    int evaluate(Board board , int depth);
}
