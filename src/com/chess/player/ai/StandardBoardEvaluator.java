package com.chess.player.ai;

import com.chess.engin.pieces.Piece;
import com.chess.engine.board.Board;
import com.chess.player.Player;

public final class StandardBoardEvaluator implements BoardEvaluator {


    private static final int CHECK_BONUS=50;
    private static final int CHECK_MATE_BONUS=10000;
    private static final int DEPTH_BONUS=100;
    private static final int CASTLED_BONUS = 60 ;

    @Override
    public int evaluate(final Board board,final int depth) {
        return scorePlayer(board,board.WhitePlayer(),depth)-
                scorePlayer(board,board.BlackPlayer(),depth);
    }

    private int scorePlayer(final Board board, final Player player,final int depth) {

        return pieceValue(player)+mobility(player)+check(player)+checkmate(player,depth)+castle(player);
    }

    private static int castle(Player player) {
      return player.isCastled()?CASTLED_BONUS:0;
    }

    private static int checkmate(Player player,int depth) {

        return player.getOpponent().isInCheckMate()?CHECK_MATE_BONUS*depthbonus(depth):0;
    }

    private static int depthbonus(int depth) {
    return depth==0?1:DEPTH_BONUS*depth;
    }

    private static int check(Player player){
        return player.getOpponent().isInCheck()?CHECK_BONUS:0;
    }
    private static int mobility(Player player){
        return player.getLegalMoves().size();
    }
    private static int pieceValue(final Player player)
    {
        int piecevalueScore=0;
        for(final Piece piece:player.getActivePieces()){
            piecevalueScore+=piece.getPieceValue();
        }
        return piecevalueScore;
    }


}
