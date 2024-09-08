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
import static com.chess.engine.board.Move.*;

public class ROOK extends Piece{
    private final static int[] CANDIDATE_MOVE_VECTOR_COORDINATE={-8,-1,1,8};

   public ROOK(final Allience pieceAllience,final int pieceposition) {
        super(PieceType.ROOK,pieceposition, pieceAllience,true);
    }
    public ROOK(final Allience pieceAllience,final int pieceposition,final boolean isFirstMove){
       super(PieceType.ROOK,pieceposition,pieceAllience,isFirstMove);
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
                        legalMoves.add(new MajorMove(board,this,candidateDestenationCoordinate));
                    }
                    else {
                        final Piece pieceatthatdestination=candidatedestinationtile.getPiece();
                        final Allience pieceAllience=pieceatthatdestination.getPieceAllience();
                        if(this.pieceAllience!=pieceAllience)
                        {
                            legalMoves.add(new MajorAttackMove(board,this,candidateDestenationCoordinate,pieceatthatdestination));
                        }
                        break;
                    }

                }
            }
        }




        return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public ROOK movePiece(final Move move) {
        return new ROOK(move.getMovedpiece().getPieceAllience(), move.getDestinationCoordinate());
    }

    @Override
    public String toString(){
        return PieceType.ROOK.toString();
    }
    private static boolean isFirstColumnExclusion(final int currentPosition,final int CandidateOffset){
        return BoardUtils.FIRST_COLUMN[currentPosition]&&(CandidateOffset==-1);
    }
    private static boolean isEIGHTColumnExclusion(final int currentPosition,final int CandidateOffset){
        return BoardUtils.EIGHT_COLUMN[currentPosition]&&(CandidateOffset==1);
    }
}
