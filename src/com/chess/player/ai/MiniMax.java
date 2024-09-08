package com.chess.player.ai;

import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.player.MoveTransition;

public class MiniMax implements MoveStrategy{

    private final BoardEvaluator boardEvaluator;
    private final int searchDepth;
    public MiniMax(final int searchDepth){
        this.boardEvaluator=new StandardBoardEvaluator();
        this.searchDepth=searchDepth;

    }

    @Override
    public String toString(){
        return "MiniMax";
    }

    @Override
    public Move excute(Board board) {
        final long starttime=System.currentTimeMillis();
        Move bestMove=null;
        int highestseenvalue=Integer.MIN_VALUE;
        int lowestseenvalue=Integer.MAX_VALUE;
        int currentvalue;
        System.out.println(board.currentPlayer()+"THINKING WITH DEPTH = "+this.searchDepth);
        int numMoves=board.currentPlayer().getLegalMoves().size();
        for(final Move move:board.currentPlayer().getLegalMoves()){
            final MoveTransition moveTransition=board.currentPlayer().makeMove(move);
            if(moveTransition.getMoveStatus().isDone()){
                currentvalue=board.currentPlayer().getAllience().isWhite()?
                        min(moveTransition.getTransitionBoard(),this.searchDepth-1):
                        max(moveTransition.getTransitionBoard(),this.searchDepth-1);

                if(board.currentPlayer().getAllience().isWhite()&&currentvalue>=highestseenvalue){

                    highestseenvalue=currentvalue;
                    bestMove=move;
                }
                else if(board.currentPlayer().getAllience().isBlack()&&currentvalue<=lowestseenvalue){
                    lowestseenvalue=currentvalue;
                    bestMove=move;

                }

            }
        }
        final long excutonTime=System.currentTimeMillis()-starttime;

        return bestMove;
    }

    public int min(final Board board,final int depth){

        if(depth==0||isEndGameScenario(board)){
            return this.boardEvaluator.evaluate(board,depth);
        }
        int lowestsenvalue=Integer.MAX_VALUE;
        for(final Move move:board.currentPlayer().getLegalMoves()){
            final MoveTransition moveTransition=board.currentPlayer().makeMove(move);
            if(moveTransition.getMoveStatus().isDone()){
                final int currentvalue=max(moveTransition.getTransitionBoard(),depth-1);
                if(currentvalue<=lowestsenvalue){
                    lowestsenvalue=currentvalue;
                }
            }
        }
        return lowestsenvalue;
    }
    private static boolean isEndGameScenario(final Board board){
        return board.currentPlayer().isInCheckMate()||board.currentPlayer().isInStaleMate();
    }
    public int max(final Board board,final int depth){
        if(depth==0||isEndGameScenario(board)){
            return this.boardEvaluator.evaluate(board,depth);
        }
        int highstseenvalue=Integer.MIN_VALUE;
        for(final Move move:board.currentPlayer().getLegalMoves()){
            final MoveTransition moveTransition=board.currentPlayer().makeMove(move);
            if(moveTransition.getMoveStatus().isDone()){
                final int currentvalue=min(moveTransition.getTransitionBoard(),depth-1);
                if(currentvalue>=highstseenvalue){
                    highstseenvalue=currentvalue;
                }
            }
        }
        return highstseenvalue;
    }
}
