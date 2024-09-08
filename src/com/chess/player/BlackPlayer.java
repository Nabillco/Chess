package com.chess.player;

import com.chess.Allience;
import com.chess.engin.pieces.Piece;
import com.chess.engin.pieces.ROOK;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.board.Tile;
import com.google.common.collect.ImmutableList;
import com.chess.engine.board.Move.KingSideCastleMove;
import com.chess.engine.board.Move.QueenSideCastleMove;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BlackPlayer extends Player{
    public BlackPlayer(final Board board,final Collection<Move> whitestandardlegalmoves,final Collection<Move> blackstandardlegalmoves) {
        super(board,blackstandardlegalmoves,whitestandardlegalmoves);
    }

    @Override
    public Collection<Piece> getActivePieces() {
        return this.board.getBlackpieces();
    }

    @Override
    public Allience getAllience() {
        return Allience.BLACK;
    }

    @Override
    public Player getOpponent() {
        return this.board.WhitePlayer();
    }

    @Override
    protected Collection<Move> calculateKingCastles(final Collection<Move> playerLegals,final Collection<Move> opponentsLegals) {
        final List<Move>KingCastles=new ArrayList<>();
        if(this.playerKing.isFirstMove()&&!this.isInCheck()){
            //whites king sidecastle
            if(!this.board.getTile(5).isTileOccupied()&&!this.board.getTile(6).isTileOccupied()){
                final Tile rookTile=this.board.getTile(7);

                if(rookTile.isTileOccupied()&&rookTile.getPiece().isFirstMove()){
                    if(Player.calculateAttacksOnTile(5,opponentsLegals).isEmpty()&&
                            Player.calculateAttacksOnTile(6,opponentsLegals).isEmpty()&&
                            rookTile.getPiece().getPieceType().isRook()){
                        KingCastles.add(new KingSideCastleMove(this.board,
                                                               this.playerKing,
                                              6,
                                                                (ROOK) rookTile.getPiece(),
                                                                 rookTile.getTileCoordinate(),
                                                5));
                    }

                }
            }
            if(!this.board.getTile(1).isTileOccupied()&&
                    !this.board.getTile(2).isTileOccupied()&&
                    !this.board.getTile(3).isTileOccupied()){
                final Tile rookTile=this.board.getTile(0);
                if(rookTile.isTileOccupied()&&rookTile.getPiece().isFirstMove()&&
                        Player.calculateAttacksOnTile(2,opponentsLegals).isEmpty()&&
                        Player.calculateAttacksOnTile(3,opponentsLegals).isEmpty()&&
                rookTile.getPiece().getPieceType().isRook()){


                    KingCastles.add(new QueenSideCastleMove(this.board,
                                                            this.playerKing,
                                          2,
                                                           (ROOK) rookTile.getPiece(),
                                                            rookTile.getTileCoordinate(),
                                           3));
                }
            }


        }

        return ImmutableList.copyOf(KingCastles);
    }
}
