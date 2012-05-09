/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import manager1.Storage;

/**
 *
 * @author Tomas
 */
public class StoragesTableModel extends AbstractTableModel{    
    
    private List<Storage> storages = new ArrayList<>();
    
    
    @Override
    public int getRowCount(){
        return storages.size();
    }
    
    @Override
    public int getColumnCount(){
        return 3;
    }
    
    
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex){
        Storage storage = storages.get(rowIndex);
        switch (columnIndex){
            case 0:
                return storage.getName();
            case 1:
                return storage.getCapacity();
            case 2:
                return storage.getAddress();
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }
    
    
}
