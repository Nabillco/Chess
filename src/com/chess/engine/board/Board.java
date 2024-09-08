package com.chess.engine.board;

import com.chess.Allience;
import com.chess.engin.pieces.*;
import com.chess.player.BlackPlayer;
import com.chess.player.Player;
import com.chess.player.WhitePlayer;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.*;

public class Board {
    private final List<Tile>gameboard;
    private final Collection<Piece> whitepieces;
    private final Collection<Piece>blackpieces;

    private final WhitePlayer whitePlayer;
    private final BlackPlayer blackplayer;
    private final Player currentPlayer;
    private final Pawn enpassantPawn;
    private Board(final Builder builder){
        this.gameboard=CreateGameBoard(builder);
        this.whitepieces=calculateActivePieces(this.gameboard,Allience.WHITE);
        this.blackpieces=calculateActivePieces(this.gameboard,Allience.BLACK);
        this.enpassantPawn=builder.enPassantPawn;
        final Collection<Move>whitestandardlegalmoves=calculateLegalMoves(this.whitepieces);
        final Collection<Move>blackstandardlegalmoves=calculateLegalMoves(this.blackpieces);
        this.whitePlayer=new WhitePlayer(this,whitestandardlegalmoves,blackstandardlegalmoves);
        this.blackplayer=new BlackPlayer(this,whitestandardlegalmoves,blackstandardlegalmoves);
        this.currentPlayer=builder.nextmovemaker.choosePlayer(this.whitePlayer,this.blackplayer);
    }



    @Override
    public String toString(){
        final StringBuilder builder=new StringBuilder();
        for(int i=0;i<BoardUtils.NUM_TILES;i++){
            final String tiletext=this.gameboard.get(i).toString();
            builder.append(String.format("%3s",tiletext));
            if((i+1)%BoardUtils.NUM_TILES_PER_ROW==0){
                builder.append("\n");
            }
        }
        return builder.toString();
    }



    public Player WhitePlayer(){
        return this.whitePlayer;
    }
    public Player BlackPlayer(){
        return this.blackplayer;
    }
    public Player currentPlayer(){
        return this.currentPlayer;
    }

    public Pawn getEnpassantPawn(){
        return this.enpassantPawn;
    }
    public Collection<Piece>getBlackpieces(){
        return this.blackpieces;
    }
    public Collection<Piece>getWhitepieces(){
        return this.whitepieces;
    }
    private Collection<Move> calculateLegalMoves(final Collection<Piece> pieces) {
        final List<Move>legalmoves=new ArrayList<>();
        for(final Piece piece:pieces){
            legalmoves.addAll(piece.calculateLegalMoves(this));
        }

        return ImmutableList.copyOf(legalmoves);
    }


    private static Collection<Piece> calculateActivePieces(final List<Tile>gameboard,final Allience allience)
    {
        final List<Piece>activepieces=new ArrayList<>();
        for(final Tile tile:gameboard){
            if(tile.isTileOccupied()){
                final Piece piece=tile.getPiece();
                if(piece.getPieceAllience()==allience){
                    activepieces.add(piece);
                }
            }
        }
        return ImmutableList.copyOf(activepieces);

    }
    public Tile getTile(final int tilecoordinate)
    {
        return gameboard.get(tilecoordinate);
    }
    private static List<Tile> CreateGameBoard(final Builder builder){
        final Tile[] tiles=new Tile[BoardUtils.NUM_TILES];
        for(int i=0;i<BoardUtils.NUM_TILES;i++){
            tiles[i]=Tile.createTile(i,builder.boardconfig.getOrDefault(i,null));
        }
        return ImmutableList.copyOf(tiles);
    }

    public static Board createStandardBoard(){
         final Builder builder=new Builder();
         //Black pieces
        builder.setpiece(new ROOK(Allience.BLACK,0));
        builder.setpiece(new Knight(Allience.BLACK,1));
        builder.setpiece(new Bishop(Allience.BLACK,2));
        builder.setpiece(new Queen(Allience.BLACK,3));
        builder.setpiece(new King(Allience.BLACK,4,true,true));
        builder.setpiece(new Bishop(Allience.BLACK,5));
        builder.setpiece(new Knight(Allience.BLACK,6));
        builder.setpiece(new ROOK(Allience.BLACK,7));
        builder.setpiece(new Pawn(Allience.BLACK,8));
        builder.setpiece(new Pawn(Allience.BLACK,9));
        builder.setpiece(new Pawn(Allience.BLACK,10));
        builder.setpiece(new Pawn(Allience.BLACK,11));
        builder.setpiece(new Pawn(Allience.BLACK,12));
        builder.setpiece(new Pawn(Allience.BLACK,13));
        builder.setpiece(new Pawn(Allience.BLACK,14));
        builder.setpiece(new Pawn(Allience.BLACK,15));
        //WHITE pieces
        builder.setpiece(new Pawn(Allience.WHITE,48));
        builder.setpiece(new Pawn(Allience.WHITE,49));
        builder.setpiece(new Pawn(Allience.WHITE,50));
        builder.setpiece(new Pawn(Allience.WHITE,51));
        builder.setpiece(new Pawn(Allience.WHITE,52));
        builder.setpiece(new Pawn(Allience.WHITE,53));
        builder.setpiece(new Pawn(Allience.WHITE,54));
        builder.setpiece(new Pawn(Allience.WHITE,55));
        builder.setpiece(new ROOK(Allience.WHITE,56));
        builder.setpiece(new Knight(Allience.WHITE,57));
        builder.setpiece(new Bishop(Allience.WHITE,58));
        builder.setpiece(new Queen(Allience.WHITE,59));
        builder.setpiece(new King(Allience.WHITE,60,true,true));
        builder.setpiece(new Bishop(Allience.WHITE,61));
        builder.setpiece(new Knight(Allience.WHITE,62));
        builder.setpiece(new ROOK(Allience.WHITE,63));
        //White to move
        builder.setmovemaker(Allience.WHITE);
        return builder.build();



    }

    public Iterable<Move> getAllLegalMoves() {
        return Iterables.unmodifiableIterable(Iterables.concat(this.whitePlayer.getLegalMoves(),this.blackplayer.getLegalMoves()));
    }

    public static class Builder{
        Map<Integer, Piece>boardconfig;
        Allience nextmovemaker;
        Pawn enPassantPawn;

        public Builder(){
            this.boardconfig=new HashMap<>();

        }
        public Builder setpiece(final Piece piece){
            this.boardconfig.put(piece.getPiecePosition(),piece);
            return this;
        }
        public Builder setmovemaker(final Allience nextmovemaker){
            this.nextmovemaker=nextmovemaker;
            return this;
        }

        public Board build(){
            return new Board(this);
        }

        public void setEnPassantPawn(Pawn enPassantPawn) {
            this.enPassantPawn=enPassantPawn;
        }
    }
}
