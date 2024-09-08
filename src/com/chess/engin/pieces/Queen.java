package com.chess.engin.pieces;

import com.chess.Allience;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Tile;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Queen extends Piece{

    private final static int[] CANDIDATE_MOVE_VECTOR_COORDINATE={-9,-8,-7,-1,1,7,8,9};

    public Queen(final Allience pieceAllience,final int pieceposition) {
        super(PieceType.QUEEN,pieceposition, pieceAllience,true);
    }
    public Queen(final Allience pieceAllience,final int pieceposition,final boolean isFirstMove){
        super(PieceType.QUEEN,pieceposition,pieceAllience,isFirstMove);
    }

    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {
        final List<Move> legalMoves=new ArrayList<>();

        for(final int candidateCoordinateOffset:CANDIDATE_MOVE_VECTOR_COORDINATE)
        {
            int candidateDestenationCoordinate=this.pieceposition;
            while(BoardUtils.isValidCoordinate(candidateDestenationCoordinate)){

                if(isFirstColumnExclusion(candidateDestenationCoordinate,candidateCoordinateOffset)||
                        isEIGHTColumnExclusion(candidateDestenationCoordinate,candidateCoordinateOffset)){
                    break;
                }
                candidateDestenationCoordinate+=candidateCoordinateOffset;
                if(BoardUtils.isValidCoordinate(candidateDestenationCoordinate))
                {
                    final Tile candidatedestinationtile=board.getTile(candidateDestenationCoordinate);
                    if(!candidatedestinationtile.isTileOccupied())
                    {
                        legalMoves.add(new Move.MajorMove(board,this,candidateDestenationCoordinate));
                    }
                    else {
                        final Piece pieceatthatdestination=candidatedestinationtile.getPiece();
                        final Allience pieceAllience=pieceatthatdestination.getPieceAllience();
                        if(this.pieceAllience!=pieceAllience)
                        {
                            legalMoves.add(new Move.MajorAttackMove(board,this,candidateDestenationCoordinate,pieceatthatdestination));
                        }
                        break;
                    }

                }
            }
        }




        return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public Queen movePiece(final Move move) {
        return new Queen(move.getMovedpiece().getPieceAllience(), move.getDestinationCoordinate());
    }

    @Override
    public String toString(){
        return PieceType.QUEEN.toString();
    }
    private static boolean isFirstColumnExclusion(final int currentPosition,final int CandidateOffset){
        return BoardUtils.FIRST_COLUMN[currentPosition]&&(CandidateOffset==-1||CandidateOffset==-9||CandidateOffset==7);
    }
    private static boolean isEIGHTColumnExclusion(final int currentPosition,final int CandidateOffset){
        return BoardUtils.EIGHT_COLUMN[currentPosition]&&(CandidateOffset==-7||CandidateOffset==1||CandidateOffset==9);
    }
}
