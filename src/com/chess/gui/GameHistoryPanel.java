package com.chess.gui;

import com.chess.engine.board.Board;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.xml.datatype.DatatypeFactory;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import  com.chess.gui.Table.*;
import com.chess.engine.board.Move;

public class GameHistoryPanel extends JPanel {
    private final DataModel model;
    private final JScrollPane scrollPane;
    private static final Dimension HISTORY_PANEL_DIMENSION=new Dimension(100,400);
    GameHistoryPanel(){
        this.setLayout(new BorderLayout());
        this.model=new DataModel();
        final JTable table=new JTable(model);
        table.setRowHeight(15);
        this.scrollPane=new JScrollPane(table);
        scrollPane.setColumnHeaderView(table.getTableHeader());
        scrollPane.setPreferredSize(HISTORY_PANEL_DIMENSION);
        this.add(scrollPane,BorderLayout.CENTER);
        this.setVisible(true);
    }
        void redo(final Board board, final MoveLog moveHistory){
        int currentRow=0;
        this.model.clear();
        for(final Move move:moveHistory.getMoves()){
            final String moveText=move.toString();
            if(move.getMovedpiece().getPieceAllience().isWhite()){
                this.model.setValueAt(moveText,currentRow,0);
            }
            else if(move.getMovedpiece().getPieceAllience().isBlack()){
                this.model.setValueAt(moveText,currentRow,1);
                currentRow++;
            }
        }
            if (moveHistory.getMoves().size() > 0) {
                final Move lastMove=moveHistory.getMoves().get(moveHistory.size()-1);
                final String moveText=lastMove.toString();
                if(lastMove.getMovedpiece().getPieceAllience().isWhite()){
                    this.model.setValueAt(moveText+calculateCheckMateHash(board),currentRow-1,0);
                }
                else if(lastMove.getMovedpiece().getPieceAllience().isBlack()){
                    this.model.setValueAt(moveText+calculateCheckMateHash(board),currentRow-1,1);
                }
            }

            final JScrollBar vertical=scrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());

        }

    private String calculateCheckMateHash(Board board) {
        if(board.currentPlayer().isInCheckMate()){
            return "#";
        }else if(board.currentPlayer().isInCheckMate()){
            return "+";
        }
        return "";
    }

    private static class DataModel extends DefaultTableModel {
        private final List<Row>values;
        private static final String[]NAMES={"WHITE","BLACK"};
        DataModel(){
            this.values=new ArrayList<>();
        }
        public void clear(){
            this.values.clear();
            setRowCount(0);
        }
        @Override
            public int getRowCount(){
            if(this.values==null){
                return 0;
            }
            return this.values.size();
        }
        @Override
            public int getColumnCount(){
            return NAMES.length;
        }
        @Override
            public Object getValueAt(final int row,final int column){
            final Row currentRow=this.values.get(row);
            if(column==0){
                return currentRow.getWhiteMove();
            }
            else if(column==1){
                return currentRow.getBlackMove();
            }
            else return null;
        }
        @Override
            public void setValueAt(final Object aValue,final int row,final int column){
            final Row currentRow;
            if(this.values.size()<=row){
                currentRow=new Row();
                this.values.add(currentRow);
            }
            else{
                currentRow=this.values.get(row);
            }
            if(column==0){
                currentRow.setWhiteMove((String)aValue);
                fireTableCellUpdated(row,row);
            }
            else if(column==1){
                currentRow.setBlackMove((String)aValue);
                fireTableCellUpdated(row,column);
            }
        }
        @Override
            public Class<?> getColumnClass(final int column){
            return Move.class;
        }
        @Override
            public String getColumnName(final int column){
            return NAMES[column];
        }

        }
        private static class Row{

        private String whiteMove;
        private String blackMove;
        Row(){

        }
        public String getWhiteMove(){
            return this.whiteMove;
        }
        public String getBlackMove(){
            return this.blackMove;
        }
        public void setWhiteMove(final String move){
            this.whiteMove=move;
        }
        public void setBlackMove(final String move){
            this.blackMove=move;
        }

        }

}
