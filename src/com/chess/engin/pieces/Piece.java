package com.chess.engin.pieces;

import com.chess.Allience;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;

import java.util.Collection;
import java.util.Objects;

public abstract class Piece {

    protected final PieceType pieceType;
    protected final int pieceposition;
    protected final Allience pieceAllience;
    protected final boolean isFirstMove;
    private final int cacheHashCode;

    Piece(final PieceType pieceType,final int pieceposition,final Allience pieceAllience,final boolean isFirstMove  ){
        this.pieceType=pieceType;
        this.pieceAllience=pieceAllience;
        this.pieceposition=pieceposition;
        this.isFirstMove=isFirstMove;
        this.cacheHashCode=computerHashCode();
    }

    private int computerHashCode() {
        int result=pieceType.hashCode();
        result=31*result+pieceAllience.hashCode();
        result=31*result+pieceposition;
        result=31*result+(isFirstMove?1:0);
        return result;
    }


    @Override
    public boolean equals(final Object other){
       if(this==other){
           return true;
       }
       if(!(other instanceof Piece)){
           return false;
       }
       final Piece otherpiece=(Piece) other;
       return pieceposition==otherpiece.getPiecePosition()&&pieceType==otherpiece.getPieceType()&&
               pieceAllience==otherpiece.getPieceAllience()&&isFirstMove==otherpiece.isFirstMove();

    }
    @Override
    public int hashCode(){
        return this.cacheHashCode;
    }
    public int getPiecePosition(){
        return this.pieceposition;
    }
    public Allience getPieceAllience()
    {
        return this.pieceAllience;
    }
    public boolean isFirstMove(){
        return this.isFirstMove;
    }
    public PieceType getPieceType(){
        return this.pieceType;
    }
    public int getPieceValue(){
        return this.pieceType.getPieceValue();
    }

    public abstract Collection<Move> calculateLegalMoves(final Board board);

    public abstract Piece movePiece(Move move);

    public enum PieceType{
        PAWN(100,"P") {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        },
        KNIGHT(300,"N") {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        },
        BISHOP(300,"B") {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        },
        ROOK(500,"R") {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return true;
            }
        },
        QUEEN(900,"Q") {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        },
        KING(10000,"K") {
            @Override
            public boolean isKing() {
                return true;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        };
        private String pieceName;
        private int pieceValue;
        PieceType(final int pieceValue,final String pieceName){
            this.pieceName=pieceName;
            this.pieceValue=pieceValue;
        }
        @Override
        public String toString(){
            return this.pieceName;
        }
        public int getPieceValue(){
            return this.pieceValue;
        }

        public abstract boolean isKing();

         public abstract boolean isRook();
    }
}
