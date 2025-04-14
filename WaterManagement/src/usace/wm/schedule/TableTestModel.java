package usace.wm.schedule;

import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;

class TableTestModel extends AbstractTableModel implements CellEditorListener {
	private static final String[] columnName = { "Site", "Elevation", "Capability" };
	private static final Class[] columnClass = { String.class, Double.class, Double.class };
	private int editableRow = -1;
	private final int editableColumn = 0;
	private ElevationCapabilityTable viewer;

	public TableTestModel(ElevationCapabilityTable vewer) {
		viewer = vewer;
	}

	public Class getColumnClass(int columnindex) {
		return columnClass[columnindex];
	}

	public int getColumnCount() {
		return columnName.length;
	}

	public String getColumnName(int columnIndx) {
		return columnName[columnIndx];
	}

	public int getRowCount() {
		return viewer.getTableRowCount();
	}

	public Object getValueAt(int rowIndex, int columnIndx) {
		return getValueAt(rowIndex, columnIndx);
	}

	public void setValueAt(Object value, int rowIndex, int columnIndx) {
		editableRow = -1;
	}

	public void setEditableRow(int row) {
		editableRow = row;
	}

	public boolean isCellEditable(int row, int column) {
		return (row == editableRow) && (column == 0);
	}

	public void editingCanceled(ChangeEvent event) {
		editableRow = -1;
		((TableCellEditor) event.getSource()).removeCellEditorListener(this);
	}

	public void editingStopped(ChangeEvent event) {
		((TableCellEditor) event.getSource()).removeCellEditorListener(this);
	}
}
