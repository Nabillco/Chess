package com.chess.pgn;

import com.chess.engin.pieces.Pawn;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;

public class FenUtilities {
    private FenUtilities(){
        throw new RuntimeException("Not instanble");
    }
    public static Board createGameFromFEN(final String fenString){
        return null;
    }
    public static String createFENFromGame(final Board board){
        return calculateBoardText(board)+" "+calculateCurrentPlayerText(board)+" "+
                calculateCastleText(board)+" "+calculateEnPassantSquare(board)+" "+"0 1";
    }

    private static String calculateBoardText(final Board board) {
    final StringBuilder builder=new StringBuilder();
    for(int i=0;i<BoardUtils.NUM_TILES;i++){
        final String tileText=board.getTile(i).toString();
        builder.append(tileText);
    }
    builder.insert(8,"/");
        builder.insert(17,"/");
        builder.insert(26,"/");
        builder.insert(35,"/");
        builder.insert(44,"/");
        builder.insert(53,"/");
        builder.insert(62,"/");
        return board.toString().
                replaceAll("--------","8").
                replaceAll("-------","7").
                replaceAll("------","6").
                replaceAll("-----","5").
                replaceAll("----","4").
                replaceAll("---","3").
                replaceAll("--","2").
                replaceAll("-","1");




    }

    private static String calculateEnPassantSquare(final Board board) {
    final Pawn enPassantPawn=board.getEnpassantPawn();
    if(enPassantPawn!=null){
       return BoardUtils.getPositionAtCoordinate(enPassantPawn.getPiecePosition()+(8)*enPassantPawn.getPieceAllience().getOppositeDirection());
    }
    return "-";
    }

    private static String calculateCastleText(final Board board){
        final StringBuilder builder=new StringBuilder();
        if(board.WhitePlayer().isKingSideCastleCapable()){
            builder.append("K");
        }
        if(board.WhitePlayer().isQueenSideCastleCapable()){
            builder.append("Q");
        }
        if(board.WhitePlayer().isKingSideCastleCapable()){
            builder.append("k");
        }
        if(board.WhitePlayer().isQueenSideCastleCapable()){
            builder.append("q");
        }
        final String result=builder.toString();
        return result.isEmpty()?"-":result;
    }
    private static String calculateCurrentPlayerText(final Board board){
        return board.currentPlayer().toString().substring(0,1).toLowerCase();
    }
}
