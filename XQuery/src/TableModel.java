import java.util.List;

import javax.swing.table.AbstractTableModel;


public class TableModel extends AbstractTableModel {

	private List<String>paths;
	public TableModel(List<String>paths){
		this.paths = paths;
	}
	
	public void setPaths(List<String>paths){
		this.paths = paths;
		fireTableDataChanged();
	}
	
	@Override
	public int getColumnCount() {
		return 1;
	}

	@Override
	public int getRowCount() {
		return paths.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return paths.get(rowIndex);
	}
	
	@Override
	public String getColumnName(int column) {
		return "Path";
	}

}
