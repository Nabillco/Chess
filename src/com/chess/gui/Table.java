package com.chess.gui;

import com.chess.engin.pieces.Piece;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Tile;
import com.chess.player.MoveTransition;
import com.chess.player.ai.MiniMax;
import com.chess.player.ai.MoveStrategy;
import com.google.common.collect.Lists;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;

public class Table extends Observable {

    private final JFrame gameFrame;
    private final GameHistoryPanel gameHistoryPanel;
    private final TakenPiecesPanel takenPiecesPanel;
    private final BoardPanel boardPanel;
    private final MoveLog moveLog;
    private final GameSetup gameSetup;
    private Board chessBoard;

    private Tile sourceTile;
    private Tile destinationTile;
    private Piece humanMovedPiece;

    private BoardDirection boardDirection;
    private Move computerMove;
    private boolean highlightLegalMoves;
    private final static Dimension OUTER_FRAME_DIMENSION=new Dimension(600,600);
    private final static Dimension BOARD_PANEL_DIMENSION=new Dimension(400,350);
    private final static Dimension TLE_PANEL_DIMENSION=new Dimension(10,10);
    private static String defaultPieceImagesPath="Pieces22/plain/";


    private final Color lightTileColor=Color.decode("#F0D9B5");
    private final Color darkTileColor=Color.decode("#B58863");
    private static final Table INSTANCE=new Table();
    private Table(){
            this.gameFrame=new JFrame("Chess Bordo");
            this.gameFrame.setLayout(new BorderLayout());
            final JMenuBar tableMenuBar = createTableMenuBar();
            this.gameFrame.setJMenuBar(tableMenuBar);
            this.gameFrame.setSize(OUTER_FRAME_DIMENSION);
            this.chessBoard=Board.createStandardBoard();
            this.gameHistoryPanel=new GameHistoryPanel();
            this.takenPiecesPanel=new TakenPiecesPanel();
            this.boardPanel=new BoardPanel();
            this.moveLog=new MoveLog();
            this.addObserver(new TableGameAIwatcher());
            this.gameSetup=new GameSetup(this.gameFrame,true);
            this.boardDirection=BoardDirection.NORMAL;
            this.highlightLegalMoves=false;
            //this.gameFrame.add(this.takenPiecesPanel,BorderLayout.WEST);
            this.gameFrame.add(this.boardPanel,BorderLayout.CENTER);
            //this.gameFrame.add(this.gameHistoryPanel,BorderLayout.EAST);
            this.gameFrame.setVisible(true);

    }
    public static Table get(){
        return INSTANCE;
    }
    public void show(){
        Table.get().getMoveLog().clear();
        Table.get().getGameHistoryPanel().redo(chessBoard, Table.get().getMoveLog());
        Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
        Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard());
    }
    private GameSetup getGameSetup(){
        return this.gameSetup;
    }
    private Board getGameBoard(){
        return this.chessBoard;
    }
    private JMenuBar createTableMenuBar(){

        final JMenuBar tableMenuBar=new JMenuBar();
        tableMenuBar.add(createFileMenu());
        tableMenuBar.add(createPreferencesMenu());
        tableMenuBar.add(createOptionsMenu());
        return tableMenuBar;
    }
    private JMenu createFileMenu(){
        final JMenu fileMenu=new JMenu("File");
        final JMenuItem openPGN=new JMenuItem("Load PGN File");
        openPGN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Open up that PGN File");
            }
        });
        fileMenu.add(openPGN);

        final JMenuItem exitMenuItem=new JMenuItem("Exit");
        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                System.exit(0);
            }
        });
        fileMenu.add(exitMenuItem);
        return fileMenu;
    }

    private JMenu createPreferencesMenu(){
        final JMenu preferencesMenu=new JMenu("Preferences");
        final JMenuItem flipBoardMenuItem=new JMenuItem("Flib Board");
        flipBoardMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boardDirection=boardDirection.opposite();
                boardPanel.drawBoard(chessBoard);
            }
        });
        preferencesMenu.add(flipBoardMenuItem);

        preferencesMenu.addSeparator();
        final JCheckBoxMenuItem legalMovesHightlighterCheckbox=new JCheckBoxMenuItem("Highlight Legal Moves",false);
        legalMovesHightlighterCheckbox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                highlightLegalMoves=legalMovesHightlighterCheckbox.isSelected();
            }
        });

        preferencesMenu.add(legalMovesHightlighterCheckbox);

        return preferencesMenu;
    }

    private JMenu createOptionsMenu(){
        final JMenu optionMenu= new JMenu("Options");
        final JMenuItem setupGameMenuItem= new JMenuItem("Setup Game");
        setupGameMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                Table.get().getGameSetup().promptUser();
                Table.get().setupUpdate(Table.get().getGameSetup());
            }
        });
        optionMenu.add(setupGameMenuItem);
        return optionMenu;
    }
    private void setupUpdate(final GameSetup gameSetup){

        setChanged();
        notifyObservers(gameSetup);
    }
    private static class TableGameAIwatcher implements Observer{

        @Override
        public void update(final Observable o,final Object arg) {

            if(Table.get().getGameSetup().isAIPlayer(Table.get().getGameBoard().currentPlayer())&&
            !Table.get().getGameBoard().currentPlayer().isInCheckMate()&&!Table.get().getGameBoard().currentPlayer().isInStaleMate()){
                //create an Ai thread
                //excute ai work
                final AIThinkTank thinkTank= new AIThinkTank();
                thinkTank.execute();
            }
            if(Table.get().getGameBoard().currentPlayer().isInCheckMate()){

                System.out.println("Game Over, "+Table.get().getGameBoard().currentPlayer()+"is in CheckMate!");
            }
            if(Table.get().getGameBoard().currentPlayer().isInStaleMate()){

                System.out.println("Game Over, "+Table.get().getGameBoard().currentPlayer()+"is in StaleMate!");
            }
        }
    }

    public void updateGameBoard(final Board board){
        this.chessBoard=board;
    }
    public void upadeteComputerMove(final Move move){
        this.computerMove=move;
    }
    private MoveLog getMoveLog(){
            return this.moveLog;
    }
    private GameHistoryPanel getGameHistoryPanel(){
        return this.gameHistoryPanel;
    }
    private TakenPiecesPanel getTakenPiecesPanel(){
        return this.takenPiecesPanel;
    }
    private BoardPanel getBoardPanel(){
        return this.boardPanel;
    }
    private void moveMadeUpdate(final PlayerType playerType){
        setChanged();
        notifyObservers(playerType);

    }
    private static class AIThinkTank extends SwingWorker<Move,String>{
        private AIThinkTank(){


        }

        @Override
        protected Move doInBackground() throws Exception{
            final MoveStrategy minimax=new MiniMax(4);
            final Move bestMove=minimax.excute(Table.get().getGameBoard());

            return bestMove;
        }
        @Override
        public void done(){
            try {
                final Move bestMove=get();

                Table.get().upadeteComputerMove(bestMove);
                Table.get().updateGameBoard(Table.get().getGameBoard().currentPlayer().makeMove(bestMove).getTransitionBoard());
                Table.get().getMoveLog().addMove(bestMove);
                Table.get().getGameHistoryPanel().redo(Table.get().getGameBoard(),Table.get().getMoveLog());
                Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
                Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard());
                Table.get().moveMadeUpdate(PlayerType.COMPUTER);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }

        }
    }
    public enum BoardDirection{

        NORMAL{
            @Override
            List<TilePanel>traverse(final List<TilePanel>boardTiles){
                return boardTiles;
            }
            @Override
            BoardDirection opposite(){
                return FLIPPED;
            }
        },
        FLIPPED{
            @Override
            List<TilePanel>traverse(final List<TilePanel>boardTiles){
                return Lists.reverse(boardTiles);
            }
            @Override
            BoardDirection opposite(){
                return NORMAL;
            }
        };
        abstract List<TilePanel>traverse(final List<TilePanel>boardTiles);
        abstract BoardDirection opposite();
    }
    private class BoardPanel extends JPanel{

        final List<TilePanel>boardTiles;
        BoardPanel(){
            super(new GridLayout(8,8));
            this.boardTiles=new ArrayList<>();
            for(int i=0;i< BoardUtils.NUM_TILES;i++){
                final TilePanel tilePanel=new TilePanel(this,i);
                this.boardTiles.add(tilePanel);
                add(tilePanel);
            }
            setPreferredSize(BOARD_PANEL_DIMENSION);
            validate();

        }
        public void drawBoard(final Board board){
            removeAll();
            for(final TilePanel tilePanel:boardDirection.traverse(boardTiles )){
                tilePanel.drawTile(board);
                add(tilePanel);
            }
            validate();
            repaint();
        }
    }
    public static class MoveLog{
        private final List<Move>moves;
        MoveLog(){
            this.moves=new ArrayList<>();
        }
        public List<Move>getMoves(){
           return this.moves;
        }
        public void addMove(final Move move){
            this.moves.add(move);
        }
        public int size(){
            return this.moves.size();
        }
        public void clear(){
            this.moves.clear();
        }
        public Move remoMove(int index){
            return this.moves.remove(index);
        }
        public boolean removeMove(final Move move){
            return this.moves.remove(move);
        }
    }

    enum PlayerType{
        HUMAN,
        COMPUTER
    }
    private class  TilePanel extends JPanel{

        private final int tileId;
        TilePanel(final BoardPanel boardPanel,final int tileId){
            super(new GridBagLayout());
            this.tileId=tileId;
            setPreferredSize(TLE_PANEL_DIMENSION);
            assignTileColor();
            assignTilePieceIcon(chessBoard);

            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(final MouseEvent e) {
                    if(isRightMouseButton(e)){
                        sourceTile=null;
                        destinationTile=null;
                        humanMovedPiece=null;
                    }
                    else if(isLeftMouseButton(e)){
                        if(sourceTile==null){
                            sourceTile=chessBoard.getTile(tileId);
                            humanMovedPiece=sourceTile.getPiece();
                            if(humanMovedPiece==null){
                                sourceTile=null;
                            }
                        }
                        else {
                            destinationTile=chessBoard.getTile(tileId);
                            final Move move=Move.MoveFactory.createMove(chessBoard,sourceTile.getTileCoordinate(),destinationTile.getTileCoordinate());
                            final MoveTransition transition=chessBoard.currentPlayer().makeMove(move);
                            if(transition.getMoveStatus().isDone()){
                                chessBoard=transition.getTransitionBoard();
                                moveLog.addMove(move);
                            }
                            sourceTile=null;
                            destinationTile=null;
                            humanMovedPiece=null;
                        }
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                gameHistoryPanel.redo(chessBoard,moveLog);
                                takenPiecesPanel.redo(moveLog);
                                if(gameSetup.isAIPlayer(chessBoard.currentPlayer())){
                                    Table.get().moveMadeUpdate(PlayerType.HUMAN);
                                }

                                boardPanel.drawBoard(chessBoard);
                            }
                        });
                    }

                }

                @Override
                public void mousePressed(final MouseEvent e) {

                }

                @Override
                public void mouseReleased(final MouseEvent e) {

                }

                @Override
                public void mouseEntered(final MouseEvent e) {

                }

                @Override
                public void mouseExited(final MouseEvent e) {

                }
            });
            validate();
        }

        public void drawTile(final Board board){
            assignTileColor();
            assignTilePieceIcon(board);
            highlightLegals(board);
            validate();
            repaint();
        }
        private void assignTilePieceIcon(final Board board){
            this.removeAll();
            if(board.getTile(this.tileId).isTileOccupied()){
                try {
                    final BufferedImage image= ImageIO.read(new File(defaultPieceImagesPath+
                                board.getTile(this.tileId).getPiece().getPieceAllience().toString().substring(0,1)+
                            board.getTile(this.tileId).getPiece().toString()+".png"));
                    add(new JLabel(new ImageIcon(image)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        private void highlightLegals(final Board board){
            if(highlightLegalMoves){
                for(final Move move:pieceLegalMoves(board)){
                    if(move.getDestinationCoordinate()==this.tileId){
                        try {
                            add(new JLabel(new ImageIcon(ImageIO.read(new File("Pieces/moveve/green_dot.png")))));
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        private Collection<Move>pieceLegalMoves(final Board board){
            if(humanMovedPiece!=null&&humanMovedPiece.getPieceAllience()==board.currentPlayer().getAllience()){
                return humanMovedPiece.calculateLegalMoves(board);
            }
            return Collections.emptyList();
        }

        private void assignTileColor() {
            if(BoardUtils.EIGHTH_RANK[this.tileId]||
                    BoardUtils.SIXTH_RANK[this.tileId]||
                    BoardUtils.FOURTH_RANK[this.tileId]||
                    BoardUtils.SECOND_RANK[this.tileId]){
                setBackground(this.tileId%2==0?lightTileColor:darkTileColor);
            }
            else if(BoardUtils.SEVENTH_RANK[this.tileId]||
                    BoardUtils.FIFTH_RANK[this.tileId]||
                    BoardUtils.THIRD_RANK[this.tileId]||
                    BoardUtils.FIRST_RANK[this.tileId]){
                setBackground(this.tileId%2!=0?lightTileColor:darkTileColor);
            }
        }
    }
}
