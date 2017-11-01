package prototype_MinimalFunctionality;

import javax.swing.table.DefaultTableModel;

public class TaskTableModel extends DefaultTableModel {

	public TaskTableModel(String[] columnNames, int i) {
		super(columnNames, i);
	}

	@Override
	public String getColumnName(int col) {
	    return super.getColumnName(col);
	}
	    
	public boolean isCellEditable(int row, int column) {
	    return false;
	}
}
