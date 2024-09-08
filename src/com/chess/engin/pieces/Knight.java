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

public class Knight extends Piece{

    private final static int [] CANDIDATE_MOVE_COORDINATES={-17,-15,-10,-6,6,10,15,17};
    public Knight(final Allience pieceAllience,final int pieceposition){
        super(PieceType.KNIGHT,pieceposition, pieceAllience,true);
    }
    public Knight(final Allience pieceAllience,final int pieceposition,final boolean isFirstMove){
        super(PieceType.KNIGHT,pieceposition,pieceAllience,isFirstMove);
    }

    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {
        final List<Move>legalmoves=new ArrayList<>();
        for(final int currentCandidateOfset:CANDIDATE_MOVE_COORDINATES)
        {
          final int candidatadestinationcooridinate=this.pieceposition+currentCandidateOfset;
          if(BoardUtils.isValidCoordinate(candidatadestinationcooridinate))
          {
              if(isFirstColumnExclusion(this.pieceposition,currentCandidateOfset)
              ||isSecondColumnExclusion(this.pieceposition,currentCandidateOfset)
              ||isSevenColumnExclusion(this.pieceposition,currentCandidateOfset)
              ||isEightColumnExclusion(this.pieceposition,currentCandidateOfset))
              {
                  continue;
              }


              final Tile candidatedestinationtile=board.getTile(candidatadestinationcooridinate);
              if(!candidatedestinationtile.isTileOccupied())
              {
                  legalmoves.add(new MajorMove(board,this,candidatadestinationcooridinate));
              }
              else {
                  final Piece pieceatthatdestination=candidatedestinationtile.getPiece();
                  final Allience pieceAllience=pieceatthatdestination.getPieceAllience();
                  if(this.pieceAllience!=pieceAllience)
                  {
                      legalmoves.add(new MajorAttackMove(board,this,candidatadestinationcooridinate,pieceatthatdestination));
                  }
              }
          }
        }

        return ImmutableList.copyOf(legalmoves);
    }

    @Override
    public Knight movePiece(final Move move) {
        return new Knight(move.getMovedpiece().getPieceAllience(), move.getDestinationCoordinate());
    }

    @Override
    public String toString(){
        return PieceType.KNIGHT.toString();
    }
    private static boolean isFirstColumnExclusion(final int currentposition,final int candidateoffset)
    {
       return BoardUtils.FIRST_COLUMN[currentposition] && (candidateoffset==-17||candidateoffset==-10||
               candidateoffset==6||candidateoffset==15);
    }

    private static boolean isSecondColumnExclusion(final int currentposition,final int candidateoffset)
    {
        return BoardUtils.SECOND_COLUMN[currentposition]&&(candidateoffset==-10||candidateoffset==6);
    }
    private static boolean isSevenColumnExclusion(final int currentposition,final int candidateoffset)
    {
        return BoardUtils.SEVEN_COLUMN[currentposition] && (candidateoffset==-6||(candidateoffset==10));
    }
    private static boolean isEightColumnExclusion(final int currentposition,final int candidateoffset)
    {
        return BoardUtils.EIGHT_COLUMN[currentposition] && (candidateoffset==17||candidateoffset==10||
                candidateoffset==-6||candidateoffset==-15);
    }
}
