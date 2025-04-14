package usace.wm.schedule;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableModel;

/*
 *  This class listens for changes made to the data in the table via the
 *  TableCellEditor. When editing is started, the value of the cell is saved
 *  When editing is stopped the new value is saved. When the oold and new
 *  values are different, then the provided Action is invoked.
 *
 *  The source of the Action is a TableCellListener instance.
 */
public class TableCellListener implements PropertyChangeListener, Runnable {
	private JTable table;
	private Action action;

	private int row;
	private int column;
	private Object oldValue;
	private Object newValue;
	private boolean flag;

	/**
	 * Create a TableCellListener.
	 * 
	 * @param tbl
	 *            the table to be monitored for data changes
	 * @param action
	 *            the Action to invoke when cell data is changed
	 */
	public TableCellListener(JTable tbl, Action action) {
		table = tbl;
		this.action = action;
		table.addPropertyChangeListener(this);
	}

	/**
	 * Create a TableCellListener with a copy of all the data relevant to the
	 * change of data for a given cell.
	 * 
	 * @param row
	 *            the row of the changed cell
	 * @param column
	 *            the column of the changed cell
	 * @param oldValue
	 *            the old data of the changed cell
	 * @param newValue
	 *            the new data of the changed cell
	 */
	private TableCellListener(JTable tbl, int row, int column, Object oldValue, Object newValue, boolean f) {
		this.table = tbl;
		this.row = row;
		this.column = column;
		this.oldValue = oldValue;
		this.newValue = newValue;
		this.flag = f;
	}

	/**
	 * Get the column that was last edited
	 * 
	 * @return the column that was edited
	 */
	public int getColumn() {
		return column;
	}

	/**
	 * Get the new value in the cell
	 * 
	 * @return the new value in the cell
	 */
	public Object getNewValue() {
		return newValue;
	}

	/**
	 * Get the old value of the cell
	 * 
	 * @return the old value of the cell
	 */
	public Object getOldValue() {
		return oldValue;
	}

	/**
	 * Get the row that was last edited
	 * 
	 * @return the row that was edited
	 */
	public int getRow() {
		return row;
	}

	/**
	 * Get the table of the cell that was changed
	 * 
	 * @return the table of the cell that was changed
	 */
	public JTable getTable() {
		return table;
	}

	//
	// Implement the PropertyChangeListener interface
	//
	@Override
	public void propertyChange(PropertyChangeEvent e) {
		// A cell has started/stopped editing
		if ("tableCellEditor".equals(e.getPropertyName())) {
			if (table.isEditing()) {
				processEditingStarted();
			} else
				processEditingStopped();
		}
	}

	/*
	 * Save information of the cell about to be edited
	 */
	private void processEditingStarted() {
		// The invokeLater is necessary because the editing row and editing
		// column of the table have not been set when the "tableCellEditor"
		// PropertyChangeEvent is fired.
		// This results in the "run" method being invoked
		SwingUtilities.invokeLater(this);
	}

	/*
	 * See above.
	 */
	@Override
	public void run() {
		row = table.convertRowIndexToModel(table.getEditingRow());
		column = table.convertColumnIndexToModel(table.getEditingColumn());
		oldValue = table.getModel().getValueAt(row, column);
		newValue = null;
	}

	/*
	 * Update the Cell history when necessary
	 */
	private void processEditingStopped() {
		newValue = table.getModel().getValueAt(row, column);
		String data = ((String) newValue).trim();
		try {
			if (data.length() > 0) {
				if (table.getName().equals(Util.getScheduleTableName())) {
					// System.out.println("Name: " + table.getName());
					setFlag(false);
					Double.parseDouble(data);
				} else if (table.getName().equals(Util.getToleranceTableName())) {
					if (table.getName().equals(Util.getToleranceTableName())
							&& (column == 7 && (row == 4 || row == 5))) {
						setFlag(false);
					} else {
						setFlag(false);
						Integer.parseInt(data);
					}
				} else if (table.getName().equals(Util.getElevationCapabiliityTableName())) {
					setFlag(false);
					// Site Name
					if (column == 0) {
						if (data.equalsIgnoreCase("FTPK") || data.equalsIgnoreCase("GARR")
								|| data.equalsIgnoreCase("OAHE") || data.equalsIgnoreCase("BEND")
								|| data.equalsIgnoreCase("FTRA") || data.equalsIgnoreCase("GAPT")) {
							// Be sure the site Name is upper case
							table.setValueAt(data.toUpperCase(), row, column);
						} else {
							JOptionPane.showMessageDialog(null, "Invalid Site Name: " + data
									+ " - Not A Valid Name", "Input Error", JOptionPane.ERROR_MESSAGE);
							table.setValueAt(oldValue, row, column);
						}
						// Elevation/Capability
					} else if (column == 1 || column == 2) {
						Double d = Double.parseDouble(data);
						// Elevation
						if (column == 1) {
							if (d > 1000 && d < 2500) {
								// Do nothing
							} else {
								JOptionPane
										.showMessageDialog(null, "Invalid Elevation: " + data
												+ " - Not A Valid Elvation", "Input Error",
												JOptionPane.ERROR_MESSAGE);
								table.setValueAt(oldValue, row, column);
							}
						} else {
							// Capability
							if (d > 10 && d < 1500) {
								// Do nothing
							} else {
								JOptionPane.showMessageDialog(null, "Invalid Capability: " + data
										+ " - Not A Valid Capability", "Input Error",
										JOptionPane.ERROR_MESSAGE);
								table.setValueAt(oldValue, row, column);
							}
						}
					}
				}
			}
		} catch (NumberFormatException nfe) {
			if (table.getName().equals(Util.getScheduleTableName())) {
				setFlag(true);
				JOptionPane.showMessageDialog(null, "Invalid Value: " + newValue + " - Not A Valid Number",
						"Input Error", JOptionPane.ERROR_MESSAGE);
				table.setValueAt(oldValue, row, column);
			} else if (table.getName().equals(Util.getToleranceTableName())) {
				setFlag(true);
				JOptionPane.showMessageDialog(null, "Invalid Tolerance Value: " + newValue, "Input Error",
						JOptionPane.ERROR_MESSAGE);
				table.setValueAt(oldValue, row, column);
			} else if (table.getName().equals(Util.getElevationCapabiliityTableName())) {
				setFlag(true);
				if (column == 0) {
					JOptionPane.showMessageDialog(null, "Invalid Site Value: " + newValue, "Input Error",
							JOptionPane.ERROR_MESSAGE);
					table.setValueAt(oldValue, row, column);
				} else if (column == 1) {
					JOptionPane.showMessageDialog(null, "Invalid Elevation Value: " + newValue,
							"Input Error", JOptionPane.ERROR_MESSAGE);
					table.setValueAt(oldValue, row, column);
				} else if (column == 2) {
					JOptionPane.showMessageDialog(null, "Invalid Capability Value: " + newValue,
							"Input Error", JOptionPane.ERROR_MESSAGE);
					table.setValueAt(oldValue, row, column);
				}
			}
		}

		// The data has changed, invoke the supplied Action
		if (!newValue.equals(oldValue)) {
			// Make a copy of the data in case another cell starts editing
			// while processing this change

			TableCellListener tcl = new TableCellListener(getTable(), getRow(), getColumn(), getOldValue(),
					getNewValue(), isFlag());

			ActionEvent event = new ActionEvent(tcl, ActionEvent.ACTION_PERFORMED, "");
			action.actionPerformed(event);
		}
	}

	/**
	 * @return the flag
	 */
	protected boolean isFlag() {
		return flag;
	}

	/**
	 * @param flag
	 *            the flag to set
	 */
	private void setFlag(boolean flag) {
		this.flag = flag;
	}
}