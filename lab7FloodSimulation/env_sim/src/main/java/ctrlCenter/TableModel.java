package ctrlCenter;

import interfaces.IRetensionBasin;
import retBasin.IRetentionBasinOld;

import javax.swing.table.AbstractTableModel;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class TableModel extends AbstractTableModel {
    private final String[] columnNames = {"Name", "Water Inflow", "Water Outflow", "Filling %"};
    private final Map<String, IRetensionBasin> retensionBasins;
    private final List<String> keys = new ArrayList<>();

    public TableModel(Map<String, IRetensionBasin> retensionBasins) {
        this.retensionBasins = retensionBasins;
        this.keys.addAll(retensionBasins.keySet());
    }

    public void addRetensionBasin(String name, IRetensionBasin irb) {
        retensionBasins.put(name, irb);
        keys.add(name);
        fireTableRowsInserted(keys.size() - 1, keys.size() - 1);
    }

    @Override
    public int getRowCount() {
        return keys.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        String key = keys.get(rowIndex);
        IRetentionBasinOld basin = (IRetentionBasinOld) retensionBasins.get(key);
        if (basin == null) {
            return null;
        }
        switch (columnIndex) {
            case 0:
                return key; // Name
            case 1:
                try {
                    return basin.getWaterInflow(); // Water Inflow
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            case 2:
                try {
                    return basin.getWaterOutflow(); // Water Outflow
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            case 3:
                try {
                    return basin.getFillingPercentageOLD() * 100 + "%"; // Filling %
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            default:
                return null;
        }
    }

    public void refreshTable() {
        keys.clear();
        keys.addAll(retensionBasins.keySet());
        fireTableDataChanged();
    }
}