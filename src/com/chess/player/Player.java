package com.chess.player;

import com.chess.Allience;
import com.chess.engin.pieces.King;
import com.chess.engin.pieces.Piece;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class Player {

    protected final Board board;
    protected final King playerKing;
    protected final Collection<Move> legalMoves;
    private final boolean isInCheck;

    Player(final Board board,final Collection<Move>legalMoves,final Collection<Move>opponentMoves){
        this.board=board;
        this.playerKing=establishKing();
        this.legalMoves=ImmutableList.copyOf(Iterables.concat(legalMoves,calculateKingCastles(legalMoves,opponentMoves)));
        this.isInCheck=!Player.calculateAttacksOnTile(this.playerKing.getPiecePosition(),opponentMoves).isEmpty() ;
    }


    public King getPlayerKing(){
        return this.playerKing;
    }

    public Collection<Move>getLegalMoves(){
        return this.legalMoves;
    }

     protected static Collection<Move> calculateAttacksOnTile(int piecePosition, Collection<Move>moves) {
         final List<Move>attackMoves=new ArrayList<>();
         for(final Move move:moves){
             if(piecePosition==move.getDestinationCoordinate()){
                 attackMoves.add(move);
             }
         }
         return ImmutableList.copyOf(attackMoves);
    }

    private King establishKing(){
        for(final Piece piece:getActivePieces()){
            if(piece.getPieceType().isKing()){
                return (King) piece;
            }
        }
        throw new RuntimeException("Should not reach here! Not a Valid Board");
    }

    public boolean isMoveLegal(final Move move){
        return this.legalMoves.contains(move);
    }

    public boolean isInCheck(){
        return isInCheck;
    }


    public boolean isInCheckMate(){
        return this.isInCheck&&!hasEscapeMoves();
    }

    public boolean isInStaleMate(){
        return !this.isInCheck&&!hasEscapeMoves();
    }
    public boolean isKingSideCastleCapable(){
        return this.playerKing.isKingSideCastleCapable();
    }
    public boolean isQueenSideCastleCapable(){
        return this.playerKing.isQueenSideCastleCapable();
    }
    protected boolean hasEscapeMoves(){
        for(final Move move:this.legalMoves){
            final MoveTransition transition=makeMove(move);
            if(transition.getMoveStatus().isDone()){
                return true;
            }
        }
        return false;
    }

    public boolean isCastled(){
        return false;
    }

    public MoveTransition makeMove(final Move move){
       if(!isMoveLegal(move)){
           return new MoveTransition(this.board,move,MoveStatus.ILLEGAL_MOVE);
       }
       final Board transitionboard=move.excute();
       final Collection<Move>kingAttacks=Player.calculateAttacksOnTile(transitionboard.currentPlayer().getOpponent().getPlayerKing().getPiecePosition(),
               transitionboard.currentPlayer().getLegalMoves());

       if(!kingAttacks.isEmpty()){
           return new MoveTransition(this.board,move,MoveStatus.LEAVES_PLAYER_IN_CHECK);
       }
        return new MoveTransition(transitionboard,move,MoveStatus.DONE);
    }

    public abstract Collection<Piece>getActivePieces();

    public abstract Allience getAllience();

    public abstract Player getOpponent();
    protected abstract Collection<Move>calculateKingCastles(Collection<Move>playerLegals,Collection<Move>opponentsLegals);

}
