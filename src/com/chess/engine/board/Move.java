package com.chess.engine.board;

import com.chess.engin.pieces.Pawn;
import com.chess.engin.pieces.Piece;
import com.chess.engin.pieces.ROOK;
import com.chess.engine.board.Board.*;
public abstract class Move {
    protected final Board board;
    protected final Piece movedpiece;
    protected final int destinationCoordinate;
    protected final boolean isFirstMove;

    public static final Move NULL_MOVE=new NullMove();

       private Move(final Board board,final Piece movedpiece,final int destinationCoordinate){
        this.board=board;
        this.movedpiece=movedpiece;
        this.destinationCoordinate=destinationCoordinate;
        this.isFirstMove=movedpiece!=null&&movedpiece.isFirstMove();
    }
    private Move(final Board board,final int destinationCoordinate){
       this.board=board;
       this.destinationCoordinate=destinationCoordinate;
       this.movedpiece=null;
       this.isFirstMove=false;
    }

    @Override
    public int hashCode(){
       final int prime=31;
       int result=1;
       result =prime*result+this.destinationCoordinate;
       result=prime*result+this.movedpiece.hashCode();
       result=prime*result+this.movedpiece.getPiecePosition();
       return result;
    }
    @Override
    public boolean equals(final Object other){

       if(this==other){
           return true;
       }
       if(!(other instanceof Move)){
           return false;
       }
       final Move otherMove=(Move) other;
       return getCurrentCoordinate()==otherMove.getCurrentCoordinate()&&
               getDestinationCoordinate()==otherMove.getDestinationCoordinate()&&
               getMovedpiece().equals(otherMove.getMovedpiece());
    }

    public Board getBoard(){
           return this.board;
    }
    public int getCurrentCoordinate(){
       return this.getMovedpiece().getPiecePosition();
    }
    public int getDestinationCoordinate(){
       return this.destinationCoordinate;
    }

    public Piece getMovedpiece(){
       return this.movedpiece;
    }

    public boolean isAttack(){
       return false;
    }
    public boolean isCastlingMove(){
       return false;
    }
    public Piece getAttackedPiece(){
       return null;
    }



    public Board excute(){
        final Builder builder=new Builder();
        for(final Piece piece:this.board.currentPlayer().getActivePieces()){
            if(!this.movedpiece.equals(piece)){
                builder.setpiece(piece);
            }
        }
        for(final Piece piece:this.board.currentPlayer().getOpponent().getActivePieces()){
            builder.setpiece(piece);
        }
        //move to moved piece!
        builder.setpiece(this.movedpiece.movePiece(this));
        builder.setmovemaker(this.board.currentPlayer().getOpponent().getAllience());
        return builder.build();
    }

    public static class MajorAttackMove extends AttackMove{

       public MajorAttackMove(final Board board,final Piece pieceMoved,final int destinationCoordinate,final Piece pieceAttacked){
           super(board,pieceMoved,destinationCoordinate,pieceAttacked);
       }
       @Override
        public boolean equals(final Object other){
           return this==other|| other instanceof MajorAttackMove &&super.equals(other);
       }
       @Override
        public String toString(){
           return movedpiece.getPieceType()+BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
       }
    }
    public static final class MajorMove extends Move{

        public MajorMove(final Board board,final Piece movedpiece,final int destinationCoordinate) {
            super(board, movedpiece, destinationCoordinate);
        }
        @Override
        public boolean equals(final Object other){
            return this==other||other instanceof MajorMove && super.equals(other);
        }
        @Override
        public String toString(){
            return movedpiece.getPieceType().toString()+BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }
    }

    public static class AttackMove extends Move{

        final Piece AttackedPiece;
        public AttackMove(final Board board,final Piece movedpiece,final int destinationCoordinate,final Piece AttackedPiece) {
            super(board, movedpiece, destinationCoordinate);
            this.AttackedPiece=AttackedPiece;
        }
        @Override
        public int hashCode(){
            return this.AttackedPiece.hashCode()+super.hashCode();
        }
        @Override
        public boolean equals(final Object other){
            if(this==other)
            {
                return true;
            }
            if(!(other instanceof AttackMove)){
                return false;
            }
            final AttackMove otherAttackMove=(AttackMove)  other;
            return super.equals(otherAttackMove)&&getAttackedPiece().equals(otherAttackMove.getAttackedPiece());
        }
        @Override
        public boolean isAttack(){
            return true;
        }
        @Override
        public Piece getAttackedPiece(){
            return this.AttackedPiece;
        }

    }

    public static class PawnPromotion extends Move{
           final Move decoratedMove;
           final Pawn promotoedPawn;
        public PawnPromotion(final Move decoratedMove) {
            super(decoratedMove.getBoard(),decoratedMove.getMovedpiece(),decoratedMove.getDestinationCoordinate());
            this.decoratedMove=decoratedMove;
            this.promotoedPawn=(Pawn) decoratedMove.getMovedpiece();
        }
        @Override
        public int hashCode(){
            return decoratedMove.hashCode()+(31*promotoedPawn.hashCode());
        }
        @Override
        public boolean equals(final Object other){
            return this==other || other instanceof PawnPromotion && (super.equals(other));
        }
        @Override
        public Board excute(){
            final Board pawnMoveBoard=this.decoratedMove.excute();
            final Board.Builder builder=new Builder();
            for(final Piece piece:pawnMoveBoard.currentPlayer().getActivePieces()){
                if(!this.promotoedPawn.equals(piece)){
                    builder.setpiece(piece);
                }
            }
            for(final Piece piece:pawnMoveBoard.currentPlayer().getOpponent().getActivePieces()){
                builder.setpiece(piece);
            }

            builder.setpiece(this.promotoedPawn.getPromotionPiece().movePiece(this));
            builder.setmovemaker(pawnMoveBoard.currentPlayer().getAllience());
            return builder.build();

        }
        @Override
        public boolean isAttack(){
            return this.decoratedMove.isAttack();
        }
        @Override
        public Piece getAttackedPiece(){
            return this.decoratedMove.getAttackedPiece();
        }
        @Override
        public String toString(){
            return "";
        }
    }
    public static final class PawnMove extends Move{

        public PawnMove(final Board board,final Piece movedpiece,final int destinationCoordinate) {
            super(board, movedpiece, destinationCoordinate);
        }
        @Override
        public boolean equals(final Object other){
            return this==other||other instanceof PawnMove &&super.equals(other);
        }
        @Override
        public String toString(){
            return BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }
    }
    public static class PawnAttackedMove extends AttackMove{

        public PawnAttackedMove(final Board board,final Piece movedpiece,final int destinationCoordinate,final Piece AttackedPiece) {
            super(board, movedpiece, destinationCoordinate,AttackedPiece);
        }
        @Override
        public boolean equals(final Object other){
            return this==other||other instanceof PawnAttackedMove && super.equals(other);
        }
        @Override
        public String toString(){
            return BoardUtils.getPositionAtCoordinate(this.movedpiece.getPiecePosition()).substring(0,1)+"x"+BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }
    }
    public static final class PawnEnPassantAttack extends AttackMove{

        public PawnEnPassantAttack (final Board board,final Piece movedpiece,final int destinationCoordinate,final Piece AttackedPiece) {
            super(board, movedpiece, destinationCoordinate,AttackedPiece);
        }
        @Override
        public boolean equals(final Object other){
            return this==other|| other instanceof PawnEnPassantAttack&&super.equals(other);
        }
        @Override
        public Board excute(){
            final Builder builder=new Builder();
            for(final Piece piece:this.board.currentPlayer().getActivePieces()){
                if(!this.movedpiece.equals(piece)){
                    builder.setpiece(piece);
                }
            }
            for(final Piece piece:this.board.currentPlayer().getOpponent().getActivePieces()){
                if(!piece.equals(this.getAttackedPiece())){
                    builder.setpiece(piece);
                }
            }
            builder.setpiece(this.movedpiece.movePiece(this));
            builder.setmovemaker(this.board.currentPlayer().getOpponent().getAllience());
            return builder.build();
            }
    }
    public static final class PawnJump extends Move{

        public PawnJump(final Board board,final Piece movedpiece,final int destinationCoordinate) {
            super(board, movedpiece, destinationCoordinate);
        }

        @Override
        public Board excute(){
            final Builder builder=new Builder();
            for(final Piece piece:this.board.currentPlayer().getActivePieces()){
                if(!this.movedpiece.equals(piece)){
                    builder.setpiece(piece);
                }

            }
            for(final Piece piece:this.board.currentPlayer().getOpponent().getActivePieces()){
                builder.setpiece(piece);
            }
            final Pawn movedPawn=(Pawn) this.movedpiece.movePiece(this);
            builder.setpiece(movedPawn);
            builder.setEnPassantPawn(movedPawn);
            builder.setmovemaker(this.board.currentPlayer().getOpponent().getAllience());
            return builder.build();
        }
        @Override
        public String toString(){
            return BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }
    }

    static abstract class CastleMove extends Move{

       protected final ROOK castleRook;
       protected final int castleRookStart;
       protected final int castleRookDestination;

        public CastleMove(final Board board,final Piece movedpiece,final int destinationCoordinate,final ROOK castleRook,final int castleRookStart,final int castleRookDestination) {
            super(board, movedpiece, destinationCoordinate);
            this.castleRook=castleRook;
            this.castleRookStart=castleRookStart;
            this.castleRookDestination=castleRookDestination;
        }
        public ROOK getCastleRook(){
            return this.castleRook;
        }
        @Override
        public boolean isCastlingMove(){
            return true;
        }
        @Override
        public Board excute(){
            final Builder builder=new Builder();
            for(final Piece piece:this.board.currentPlayer().getActivePieces()){
                if(!this.movedpiece.equals(piece)&&!this.castleRook.equals(piece)){
                    builder.setpiece(piece);
                }

            }
            for(final Piece piece:this.board.currentPlayer().getOpponent().getActivePieces()){
                builder.setpiece(piece);
            }
            builder.setpiece(this.movedpiece.movePiece(this));
            //todo look into the first move  on normal pieces
            builder.setpiece(new ROOK(this.castleRook.getPieceAllience(),this.castleRookDestination));
            builder.setmovemaker(this.board.currentPlayer().getOpponent().getAllience());
            return builder.build();
        }
        @Override
        public int hashCode(){
            final int prime=31;
            int result=super.hashCode();
            result=prime*result+this.castleRook.hashCode();
            result=prime*result+this.castleRookDestination;
            return result;
        }
        @Override
        public boolean equals(final Object other){
            if(this==other){
                return true;
            }
            if(!(other instanceof CastleMove)){
                return false;
            }
            final CastleMove otherCastleMove=(CastleMove) other;
            return super.equals(otherCastleMove)&&this.castleRook.equals(otherCastleMove.getCastleRook());
        }
    }

    public static final class KingSideCastleMove extends CastleMove{

        public KingSideCastleMove(final Board board,final Piece movedpiece,final int destinationCoordinate,final ROOK castleRook,final int castleRookStart,final int castleRookDestination) {
            super(board, movedpiece, destinationCoordinate,castleRook,castleRookStart,castleRookDestination);
        }
        @Override
        public boolean equals(final Object other){
            return this==other||other instanceof KingSideCastleMove && super.equals(other);
        }
        @Override
        public String toString(){
            return "O-O";
        }
    }

    public static final class QueenSideCastleMove extends CastleMove{

        public QueenSideCastleMove(final Board board,final Piece movedpiece,final int destinationCoordinate,final ROOK castleRook,final int castleRookStart,final int castleRookDestination) {
            super(board, movedpiece, destinationCoordinate,castleRook,castleRookStart,castleRookDestination);
        }
        @Override
        public boolean equals(final Object other){
            return this==other||other instanceof QueenSideCastleMove && super.equals(other);
        }
        @Override
        public String toString(){
            return "O-O-O";
        }
    }


    public static final class NullMove extends Move{

        public NullMove() {
                super(null,65);
        }
        @Override
        public Board excute(){
            throw new RuntimeException("cannot excute the null move!");
        }
        @Override
        public int getCurrentCoordinate(){
            return -1;
        }
    }

    public static class MoveFactory{
       private MoveFactory(){
           throw new RuntimeException("Not instantiable");

       }
       public static Move createMove(final Board board,final int currentCoordinate,final int destinationCoordinate){
           for(final Move move:board.getAllLegalMoves()){
               if(move.getCurrentCoordinate()==currentCoordinate&&move.getDestinationCoordinate()==destinationCoordinate){
                   return move;
               }
           }
           return NULL_MOVE;
       }
    }
}
