package com.chess.player;

import com.chess.Allience;
import com.chess.engin.pieces.Piece;
import com.chess.engin.pieces.ROOK;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.board.Tile;
import com.chess.engine.board.Move.KingSideCastleMove;
import com.chess.engine.board.Move.QueenSideCastleMove;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class WhitePlayer extends Player{
    public WhitePlayer(final Board board,final Collection<Move> whitestandardlegalmoves,final Collection<Move> blackstandardlegalmoves) {
        super(board,whitestandardlegalmoves,blackstandardlegalmoves);
    }

    @Override
    public Collection<Piece> getActivePieces() {
        return this.board.getWhitepieces();
    }

    @Override
    public Allience getAllience() {
        return Allience.WHITE;
    }

    @Override
    public Player getOpponent() {
        return this.board.BlackPlayer();
    }

    @Override
    protected Collection<Move> calculateKingCastles(final Collection<Move> playerLegals,final Collection<Move> opponentsLegals) {
        final List<Move>KingCastles=new ArrayList<>();
        if(this.playerKing.isFirstMove()&&!this.isInCheck()){
            //whites king sidecastle
            if(!this.board.getTile(61).isTileOccupied()&&!this.board.getTile(62).isTileOccupied()){
                final Tile rookTile=this.board.getTile(63);

                if(rookTile.isTileOccupied()&&rookTile.getPiece().isFirstMove()){
                    if(Player.calculateAttacksOnTile(61,opponentsLegals).isEmpty()&&
                    Player.calculateAttacksOnTile(62,opponentsLegals).isEmpty()&&
                    rookTile.getPiece().getPieceType().isRook()){
                        KingCastles.add(new KingSideCastleMove(this.board,
                                                               this.playerKing,
                                             62,
                                                               (ROOK) rookTile.getPiece(),
                                                               rookTile.getTileCoordinate() ,
                                              61));
                    }

                }
            }
            if(!this.board.getTile(59).isTileOccupied()&&
                    !this.board.getTile(58).isTileOccupied()&&
                    !this.board.getTile(57).isTileOccupied()){
                final Tile rookTile=this.board.getTile(56);
                if(rookTile.isTileOccupied()&&rookTile.getPiece().isFirstMove()&&
                Player.calculateAttacksOnTile(58,opponentsLegals).isEmpty()&&
                Player.calculateAttacksOnTile(59,opponentsLegals).isEmpty()&&
                rookTile.getPiece().getPieceType().isRook()){


                    KingCastles.add(new QueenSideCastleMove(this.board,
                                                                this.playerKing,
                                              58,
                                                                (ROOK) rookTile.getPiece(),
                                                                 rookTile.getTileCoordinate(),
                                                59));
                }
            }


        }

        return ImmutableList.copyOf(KingCastles);
    }


}
