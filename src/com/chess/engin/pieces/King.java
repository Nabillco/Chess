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


public class King extends Piece{
    private final static int[] CANDIDATE_MOVE_VECTOR_COORDINATE={-9,-8,-7,-1,1,7,8,9};
    private final boolean isCastled;
    private final boolean kingSideCastleCapable;
    private final boolean queenSideCastleCapable;

    public King(final Allience pieceAllience,final int pieceposition,final boolean kingSideCastleCapable,final boolean queenSideCastleCapable){
        super(PieceType.KING,pieceposition, pieceAllience,true);
        this.isCastled=false;
        this.kingSideCastleCapable=kingSideCastleCapable;
        this.queenSideCastleCapable=queenSideCastleCapable;
    }
    public King(final Allience pieceAllience,final int pieceposition,final boolean isFirstMove,final boolean isCastled,final boolean kingSideCastleCapable,final boolean queenSideCastleCapable){
        super(PieceType.KING,pieceposition,pieceAllience,isFirstMove);
        this.isCastled=isCastled;
        this.kingSideCastleCapable=kingSideCastleCapable;
        this.queenSideCastleCapable=queenSideCastleCapable;
    }
    public boolean isCastled(){
        return this.isCastled;
    }
    public boolean isKingSideCastleCapable(){
        return this.kingSideCastleCapable;
    }
    public boolean isQueenSideCastleCapable(){
        return this.queenSideCastleCapable;
    }

    @Override
    public Collection<Move> calculateLegalMoves(Board board) {
        final List<Move>legalMoves=new ArrayList<>();
        for(final int currentCandidateOffset:CANDIDATE_MOVE_VECTOR_COORDINATE){
            final int candidateDestenationCoordinate=this.pieceposition+currentCandidateOffset;

            if(isFirstColumnExclusion(this.pieceposition,currentCandidateOffset)
                    ||isEightColumnExclusion(this.pieceposition,currentCandidateOffset)){
                continue;
            }


            if(BoardUtils.isValidCoordinate(candidateDestenationCoordinate)){
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
                }
            }

        }
            return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public King movePiece(final Move move) {
        return new King(move.getMovedpiece().getPieceAllience(), move.getDestinationCoordinate(),false,move.isCastlingMove(),false,false);
    }
    @Override
    public String toString(){
        return PieceType.KING.toString();
    }
    private static boolean isFirstColumnExclusion(final int currentposition,final int candidateoffset)
    {
        return BoardUtils.FIRST_COLUMN[currentposition] && (candidateoffset==-9||candidateoffset==-1||
                candidateoffset==7);
    }

    private static boolean isEightColumnExclusion(final int currentposition,final int candidateoffset)
    {
        return BoardUtils.EIGHT_COLUMN[currentposition]&&(candidateoffset==-7
                ||candidateoffset==1||candidateoffset==9);
    }
}
