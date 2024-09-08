package com.chess.engin.pieces;

import com.chess.Allience;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import static com.chess.engine.board.Move.*;

public class Pawn extends Piece{
    private final static int[] CANDIDATE_MOVE_VECTOR_COORDINATE={8,16,7,9};

    public Pawn(final Allience pieceAllience,final int pieceposition) {
        super(PieceType.PAWN,pieceposition, pieceAllience,true);
    }
    public Pawn(final Allience pieceAllience,final int pieceposition,final boolean isFirstMove){
        super(PieceType.PAWN,pieceposition,pieceAllience,isFirstMove);
    }

    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {
        final List<Move>legalMoves=new ArrayList<>();
        for(final int currentCandidateOffset:CANDIDATE_MOVE_VECTOR_COORDINATE)
        {
            final int candidateDestenationCoordinate=this.pieceposition+(this.pieceAllience.getDirection()*currentCandidateOffset);

            if(!BoardUtils.isValidCoordinate(candidateDestenationCoordinate)){
                continue;
            }
            if(currentCandidateOffset==8&&!board.getTile(candidateDestenationCoordinate).isTileOccupied()){
                if(this.pieceAllience.isPawnPromotionSquare(candidateDestenationCoordinate)){
                    legalMoves.add(new PawnPromotion(new PawnMove(board,this,candidateDestenationCoordinate)));
                }

                else {
                    legalMoves.add(new PawnMove(board,this,candidateDestenationCoordinate));
                }
            }
            else if(currentCandidateOffset==16&&this.isFirstMove()&&
                    ((BoardUtils.SEVENTH_RANK[this.pieceposition]&&this.getPieceAllience().isBlack())
                    ||(BoardUtils.SECOND_RANK[this.pieceposition]&&this.getPieceAllience().isWhite()))){
                final int behindCandidateDestinationCoordinate=this.pieceposition+(this.pieceAllience.getDirection()*8);
                if(!board.getTile(behindCandidateDestinationCoordinate).isTileOccupied()&&
                   !board.getTile(candidateDestenationCoordinate).isTileOccupied()){
                    legalMoves.add(new PawnJump(board,this,candidateDestenationCoordinate));

                }

            }
            else if (currentCandidateOffset==7&&
                    !((BoardUtils.EIGHT_COLUMN[this.pieceposition]&&this.pieceAllience.isWhite())||
                    (BoardUtils.FIRST_COLUMN[this.pieceposition]&&this.pieceAllience.isBlack()))){
                if(board.getTile(candidateDestenationCoordinate).isTileOccupied()){
                    final Piece pieceOnCandidate=board.getTile(candidateDestenationCoordinate).getPiece();
                    if(this.pieceAllience!=pieceOnCandidate.getPieceAllience()){
                        if(this.pieceAllience.isPawnPromotionSquare(candidateDestenationCoordinate)){
                            legalMoves.add(new PawnPromotion(new PawnAttackedMove(board, this, candidateDestenationCoordinate, pieceOnCandidate)));
                        }
                        else {
                            legalMoves.add(new PawnAttackedMove(board, this, candidateDestenationCoordinate, pieceOnCandidate));
                        }
                    }
                }
                else if(board.getEnpassantPawn()!=null){

                    if(board.getEnpassantPawn().getPiecePosition()==(this.pieceposition+(this.pieceAllience.getOppositeDirection()))){
                        final Piece pieceOnCandidate=board.getEnpassantPawn();
                        if(this.pieceAllience!=pieceOnCandidate.getPieceAllience()){
                            legalMoves.add(new PawnEnPassantAttack(board,this,candidateDestenationCoordinate,pieceOnCandidate));
                        }
                    }
                }

            }
            else if (currentCandidateOffset==9&&
                    !((BoardUtils.FIRST_COLUMN[this.pieceposition]&&this.pieceAllience.isWhite())||
                            (BoardUtils.EIGHT_COLUMN[this.pieceposition]&&this.pieceAllience.isBlack()))){
                if(board.getTile(candidateDestenationCoordinate).isTileOccupied()){
                    final Piece pieceOnCandidate=board.getTile(candidateDestenationCoordinate).getPiece();
                    if(this.pieceAllience!=pieceOnCandidate.getPieceAllience()){
                        if(this.pieceAllience.isPawnPromotionSquare(candidateDestenationCoordinate)){
                            legalMoves.add(new PawnPromotion(new PawnAttackedMove(board, this, candidateDestenationCoordinate, pieceOnCandidate)));
                        }
                        else {

                            legalMoves.add(new PawnAttackedMove(board, this, candidateDestenationCoordinate, pieceOnCandidate));
                        }
                    }
                }
                else if(board.getEnpassantPawn()!=null){

                    if(board.getEnpassantPawn().getPiecePosition()==(this.pieceposition-(this.pieceAllience.getOppositeDirection()))){
                        final Piece pieceOnCandidate=board.getEnpassantPawn();
                        if(this.pieceAllience!=pieceOnCandidate.getPieceAllience()){
                            legalMoves.add(new PawnEnPassantAttack(board,this,candidateDestenationCoordinate,pieceOnCandidate));
                        }
                    }
                }
            }


        }
        return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public Pawn movePiece(final Move move) {
        return new Pawn(move.getMovedpiece().getPieceAllience(), move.getDestinationCoordinate());
    }

    @Override
    public String toString(){
        return PieceType.PAWN.toString();
    }
    public Piece getPromotionPiece(){
        return new Queen(this.pieceAllience,this.pieceposition,false);
    }
}
