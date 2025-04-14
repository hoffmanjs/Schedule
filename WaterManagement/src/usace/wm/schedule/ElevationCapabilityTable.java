package usace.wm.schedule;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TreeMap;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.JTextComponent;

//import usace.wm.inflows.Utils;


public class ElevationCapabilityTable extends JDialog {

	private static final boolean DEBUG = false;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static JTable elevationCapabilityTable;

	// private DefaultTableModel defaultTableModel = null;
	private JPanel elevCapPanel = new JPanel();
	private JButton upDateButton = new JButton();
	private JButton exitButton = new JButton();
	private JScrollPane tableScrollPane = null;

	public ElevationCapabilityTable() {
		try {
			jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * The initialization method for this class sets up the gui
	 * 
	 * @throws Exception
	 */
	private void jbInit() throws Exception {
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		// if (!Util.loadProperties()) {
		// System.exit(0);
		// }

		this.setTitle("Elevation Capability Table");
		this.setModal(true);

		// Set the model table sizes for the schedule table
		DefaultTableModel model = new DefaultTableModel(getTableRowCount() + 2, 4);
		// TableTestModel model = new TableTestModel(this);

		// Setup the table for display
		// Define the table model for highlighting specific rows
		elevationCapabilityTable = new JTable(model) {
			private static final long serialVersionUID = 7526472295622776147L;

			public Component prepareRenderer(TableCellRenderer renderer, int rowIndex, int vColIndex) {
				Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);
				c.setFont(new Font("Times New Roman", Font.BOLD, 14));
				if (rowIndex == 0) {
					setValueAt("Site", 0, 0);
					setValueAt("Elevation", 0, 1);
					setValueAt("Capability", 0, 2);
					c.setForeground(Color.BLUE);
					c.setBackground(Color.WHITE);
				} else {
					Object obj = getValueAt(rowIndex, 0);
					if (null != obj) {
						String val = obj.toString();
						if (val.equals("GARR")) {
							c.setForeground(Color.BLUE);
							c.setBackground(Color.WHITE);
						} else if (val.equals("BEND")) {
							c.setForeground(Color.BLUE);
							c.setBackground(Color.WHITE);
						} else if (val.equals("GAPT")) {
							c.setForeground(Color.BLUE);
							c.setBackground(Color.WHITE);
						} else {
							c.setBackground(Color.WHITE);
							c.setForeground(getForeground());
						}
					}
				}
				return c;
			}

			@Override
			public Component prepareEditor(TableCellEditor editor, int row, int column) {
				Component c = super.prepareEditor(editor, row, column);
				if (c instanceof JTextComponent) {
					((JTextComponent) c).requestFocus();
					((JTextComponent) c).selectAll();
				}
				return c;
			}

			/**
			 * This method makes each cell noneditable
			 */
			public boolean isCellEditable(int row, int col) {

				// Note that the data/cell address is constant,
				// no matter where the cell appears onscreen.
				if (col < 3 && row > 0) {
					return true;
				} else {
					return false;
				}
			}
		};
		elevationCapabilityTable.setCellSelectionEnabled(true);
		elevationCapabilityTable.setName(Util.getElevationCapabiliityTableName());
		elevationCapabilityTable.setDefaultRenderer(Object.class, new SelectAllRenderer());
		elevationCapabilityTable.getColumnModel().getColumn(3).setMaxWidth(0);
		elevationCapabilityTable.getColumnModel().getColumn(3).setMinWidth(0);
		elevationCapabilityTable.getColumnModel().getColumn(3).setPreferredWidth(0);

		// elevationCapabilityTable.registerKeyboardAction(new AbstractAction()
		// {
		// public void actionPerformed(final ActionEvent event) {
		// editSelection();
		// }
		// }, KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_TAB, 0),
		// JComponent.WHEN_FOCUSED);

		setElevCapTable(elevationCapabilityTable);

		elevCapPanel.setLayout(new GridBagLayout());

		upDateButton.setEnabled(false);
		upDateButton.setText("Update");
		upDateButton.setMnemonic('u');
		upDateButton.setToolTipText("Update Database Info");
		upDateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				upDateDB_ActionPerformed(e);
			}
		});

		exitButton.setText("Exit");
		exitButton.setMnemonic('x');
		exitButton.setToolTipText("Close this window");
		exitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exit_ActionPerformed(e);
			}
		});

		// Label Constraints
		GridBagConstraints labelcon = new GridBagConstraints();
		labelcon.anchor = GridBagConstraints.CENTER; // bottom of space
		labelcon.fill = GridBagConstraints.HORIZONTAL;
		labelcon.gridwidth = 2;
		labelcon.gridheight = 1;
		labelcon.weightx = 0.5;
		labelcon.weighty = 0; // request any extra vertical space
		labelcon.insets = new Insets(1, 1, 1, 1); // padding
		labelcon.gridx = 3;
		labelcon.gridy = 9;

		tableScrollPane = new JScrollPane(elevationCapabilityTable);

		// Table Constraints
		GridBagConstraints tablecon = new GridBagConstraints();
		tablecon.fill = GridBagConstraints.HORIZONTAL;
		tablecon.anchor = GridBagConstraints.PAGE_START; // bottom of space
		tablecon.ipady = 1000; // make this component tall
		tablecon.ipadx = 850; // make this component tall
		tablecon.gridwidth = 11;
		tablecon.gridheight = 8;
		tablecon.weighty = 1.0; // request any extra vertical space
		elevCapPanel.add(tableScrollPane, tablecon);

		GridBagConstraints buttoncon = new GridBagConstraints();
		buttoncon.fill = GridBagConstraints.HORIZONTAL;
		buttoncon.weighty = 0; // request any extra vertical space
		buttoncon.weightx = 0.5;
		buttoncon.anchor = GridBagConstraints.SOUTH; // bottom of space
		buttoncon.insets = new Insets(3, 1, 3, 1); // top padding

		// Bottom row of Buttons
		buttoncon.gridx = 0;
		buttoncon.gridy = 10;
		elevCapPanel.add(exitButton, buttoncon);

		buttoncon.gridx = 2;
		buttoncon.gridy = 10;
		elevCapPanel.add(upDateButton, buttoncon);

		this.add(elevCapPanel);
		setupCellEditor(elevationCapabilityTable);

		loadTable();
	}

	/**
	 * 
	 */
	// public void editSelection() {
	// JTable table = getElevCapTable();
	// int row = table.getSelectedRow();
	// int col = table.getSelectedColumn();
	// DefaultTableModel model = (DefaultTableModel) table.getModel();
	// // model.setEditableRow(row);
	// if (table.editCellAt(row, col)) {
	// table.getEditorComponent().requestFocus();
	// // table.getCellEditor().addCellEditorListener(model);
	// } else {
	// System.out.println("editCellAt() returned false");
	// }
	// }

	/**
	 * The table cell data editor
	 * 
	 * @param table
	 *            - JTable
	 */
	private void setupCellEditor(JTable table) {

		// Set up the editor for the cells.
		Action action = new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 9095295324523960568L;

			public void actionPerformed(ActionEvent e) {
				TableCellListener tcl = (TableCellListener) e.getSource();
				if (!tcl.getNewValue().toString().equalsIgnoreCase(tcl.getOldValue().toString())) {
					upDateButton.setEnabled(true);
				}
			}

		};
		TableCellListener tcl = new TableCellListener(elevationCapabilityTable, action);
	}

	/**
	 * Hide the window
	 * 
	 * @param e
	 */
	private void exit_ActionPerformed(ActionEvent e) {
		this.setVisible(false);
	}

	/**
	 * Update the database
	 * 
	 * @param e
	 */
	private void upDateDB_ActionPerformed(ActionEvent e) {

	}

	/**
	 * Load the data from the database.
	 */
	protected void loadTable() {
		int SITE_COL = 0;
		int ELEVATION_COL = 1;
		int CAPABILITY_COL = 2;
		int KEY_COL = 3;

		JTable table = getElevCapTable();
		JTableHeader header = table.getTableHeader();
		if (null != header) {
			header.setVisible(false);
		}

		table.setGridColor(Color.LIGHT_GRAY);
		table.setShowGrid(true);
		table.setShowVerticalLines(true);

		int rows = 1;
		double elev = 1000.0;
		int cap = 210;
		while (rows < (table.getRowCount() - 2)) {
			table.setValueAt("FTPK", rows, SITE_COL);
			table.setValueAt(Util.getFormatOneDecimal().format(elev), rows, ELEVATION_COL);
			table.setValueAt(cap, rows, CAPABILITY_COL);
			table.setValueAt(rows, rows, KEY_COL);
			rows++;
			cap = cap + 1;
			elev = elev + .1;
		}
		setElevCapTable(table);
	}

	/**
	 * 
	 * @return
	 */
	protected int getTableRowCount() {
		// Connection dbconn = null;
		// int count = -1;
		// try {
		// dbconn = Util.getDBConnection();
		// Statement stmt = dbconn.createStatement();
		//
		// String sql = "SELECT COUNT(*) FROM LOCAL_SCHEDULE_CAPABILITY";
		// ResultSet rset = stmt.executeQuery(sql);
		// count = Integer.parseInt(rset.getString(1));
		//
		// rset.close();
		// stmt.close();
		// dbconn.close();
		// } catch (SQLException sqle) {
		// // Could not connect to the database
		// sqle.printStackTrace();
		// }
		// return count;
		return 205;
	}

	/**
	 * 
	 * @return
	 */
	public TreeMap<String, List<ThreeWkStationData>> getElevCapabilityDBData() {
		Connection dbconn = null;
		TreeMap<String, List<ThreeWkStationData>> dataMap = null;

		try {
			dbconn = Util.getDBConnection();
			Statement stmt = dbconn.createStatement();

			Calendar cal = Calendar.getInstance();
			int monthOne = cal.get(Calendar.MONTH) + 1;
			int monthThree = cal.get(Calendar.MONTH) + 3;

			String sql = "select * from LOCAL_SCHEDULE_INFO where MONTH_ID between " + monthOne + " and "
					+ monthThree + " order by SITE, ELEVATION ASC";
			ResultSet rset = stmt.executeQuery(sql);

			if (DEBUG) {
				System.out.println("\n" + sql);
			}
			dataMap = new TreeMap<String, List<ThreeWkStationData>>();
			List<ThreeWkStationData> ftpk = new ArrayList<ThreeWkStationData>();
			List<ThreeWkStationData> garr = new ArrayList<ThreeWkStationData>();
			List<ThreeWkStationData> oahe = new ArrayList<ThreeWkStationData>();
			List<ThreeWkStationData> bend = new ArrayList<ThreeWkStationData>();
			List<ThreeWkStationData> ftra = new ArrayList<ThreeWkStationData>();
			List<ThreeWkStationData> gapt = new ArrayList<ThreeWkStationData>();

			while (rset.next()) {
				ThreeWkStationData twmd = new ThreeWkStationData();
				twmd.setMonth(rset.getString(1)); // ex. January
				int monthid = (rset.getInt(2));
				twmd.setMonthID(monthid); // ex. 1
				twmd.setStation(rset.getString(3)); // ex. GARR
				int siteid = (rset.getInt(4));
				twmd.setStationID(siteid); // ex. 2

				switch (siteid) {
				case 1:
					ftpk.add(twmd);
					break;
				case 2:
					garr.add(twmd);
					break;
				case 3:
					oahe.add(twmd);
					break;
				case 4:
					bend.add(twmd);
					break;
				case 5:
					ftra.add(twmd);
					break;
				case 6:
					gapt.add(twmd);
					break;
				}

				if (DEBUG) {
					System.out.println(rset.getString(1));
					System.out.println(rset.getString(2));
					System.out.println(rset.getString(3));
					System.out.println(rset.getString(4));
					System.out.println(rset.getString(5));
					System.out.println(rset.getString(6));
					System.out.println(rset.getString(7) + "\n");
				}
			}
			dataMap.put(Util.FTPK_ID, ftpk);
			dataMap.put(Util.GARR_ID, garr);
			dataMap.put(Util.OAHE_ID, oahe);
			dataMap.put(Util.BEND_ID, bend);
			dataMap.put(Util.FTRA_ID, ftra);
			dataMap.put(Util.GAPT_ID, gapt);

			rset.close();
			stmt.close();
			dbconn.close();
		} catch (SQLException sqle) {
			// Could not connect to the database
			sqle.printStackTrace();
		}
		return dataMap;
	}

	/**
	 * Get the Schedule table
	 * 
	 * @return JTable with the rows
	 */
	private static JTable getElevCapTable() {
		return elevationCapabilityTable;
	}

	/**
	 * Set the Schedule table
	 * 
	 * @param elevCapTable
	 */
	private void setElevCapTable(JTable elevCapTable) {
		elevationCapabilityTable = elevCapTable;
	}

}
//public class ElevationCapabilityTable extends JDialog {
//	private static final boolean DEBUG = false;
//	private static final long serialVersionUID = 1L;
//	private static JTable elevationCapabilityTable;
//	private JPanel elevCapPanel = new JPanel();
//	private JButton upDateButton = new JButton();
//	private JButton exitButton = new JButton();
//	private JScrollPane tableScrollPane = null;
//
//	public ElevationCapabilityTable() {
//		try {
//			jbInit();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	private void jbInit() throws Exception {
//		setDefaultCloseOperation(1);
//
//		setTitle("Elevation Capability Table");
//		setModal(true);
//
//		DefaultTableModel model = new DefaultTableModel(getTableRowCount() + 2, 4);
//
//		elevationCapabilityTable = new JTable(model) {
//			private static final long serialVersionUID = 7526472295622776147L;
//
//			public Component prepareRenderer(TableCellRenderer renderer, int rowIndex, int vColIndex) {
//				Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);
//				c.setFont(new Font("Times New Roman", 1, 14));
//				if (rowIndex == 0) {
//					setValueAt("Site", 0, 0);
//					setValueAt("Elevation", 0, 1);
//					setValueAt("Capability", 0, 2);
//					c.setForeground(Color.BLUE);
//					c.setBackground(Color.WHITE);
//				} else {
//					Object obj = getValueAt(rowIndex, 0);
//					if (obj != null) {
//						String val = obj.toString();
//						if (val.equals("GARR")) {
//							c.setForeground(Color.BLUE);
//							c.setBackground(Color.WHITE);
//						} else if (val.equals("BEND")) {
//							c.setForeground(Color.BLUE);
//							c.setBackground(Color.WHITE);
//						} else if (val.equals("GAPT")) {
//							c.setForeground(Color.BLUE);
//							c.setBackground(Color.WHITE);
//						} else {
//							c.setBackground(Color.WHITE);
//							c.setForeground(getForeground());
//						}
//					}
//				}
//				return c;
//			}
//
//			public Component prepareEditor(TableCellEditor editor, int row, int column) {
//				Component c = super.prepareEditor(editor, row, column);
//				if ((c instanceof JTextComponent)) {
//					((JTextComponent) c).requestFocus();
//					((JTextComponent) c).selectAll();
//				}
//				return c;
//			}
//
//			public boolean isCellEditable(int row, int col) {
//				if ((col < 3) && (row > 0)) {
//					return true;
//				}
//				return false;
//			}
//		};
//		elevationCapabilityTable.setCellSelectionEnabled(true);
//		elevationCapabilityTable.setName(Util.getElevationCapabiliityTableName());
//		elevationCapabilityTable.setDefaultRenderer(Object.class, new SelectAllRenderer());
//		elevationCapabilityTable.getColumnModel().getColumn(3).setMaxWidth(0);
//		elevationCapabilityTable.getColumnModel().getColumn(3).setMinWidth(0);
//		elevationCapabilityTable.getColumnModel().getColumn(3).setPreferredWidth(0);
//
//		setElevCapTable(elevationCapabilityTable);
//
//		elevCapPanel.setLayout(new GridBagLayout());
//
//		upDateButton.setEnabled(false);
//		upDateButton.setText("Update");
//		upDateButton.setMnemonic('u');
//		upDateButton.setToolTipText("Update Database Info");
//		upDateButton.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				upDateDB_ActionPerformed(e);
//			}
//		});
//		exitButton.setText("Exit");
//		exitButton.setMnemonic('x');
//		exitButton.setToolTipText("Close this window");
//		exitButton.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				exit_ActionPerformed(e);
//			}
//		});
//		GridBagConstraints labelcon = new GridBagConstraints();
//		labelcon.anchor = 10;
//		labelcon.fill = 2;
//		labelcon.gridwidth = 2;
//		labelcon.gridheight = 1;
//		labelcon.weightx = 0.5D;
//		labelcon.weighty = 0.0D;
//		labelcon.insets = new Insets(1, 1, 1, 1);
//		labelcon.gridx = 3;
//		labelcon.gridy = 9;
//
//		tableScrollPane = new JScrollPane(elevationCapabilityTable);
//
//		GridBagConstraints tablecon = new GridBagConstraints();
//		tablecon.fill = 2;
//		tablecon.anchor = 19;
//		tablecon.ipady = 1000;
//		tablecon.ipadx = 850;
//		tablecon.gridwidth = 11;
//		tablecon.gridheight = 8;
//		tablecon.weighty = 1.0D;
//		elevCapPanel.add(tableScrollPane, tablecon);
//
//		GridBagConstraints buttoncon = new GridBagConstraints();
//		buttoncon.fill = 2;
//		buttoncon.weighty = 0.0D;
//		buttoncon.weightx = 0.5D;
//		buttoncon.anchor = 15;
//		buttoncon.insets = new Insets(3, 1, 3, 1);
//
//		buttoncon.gridx = 0;
//		buttoncon.gridy = 10;
//		elevCapPanel.add(exitButton, buttoncon);
//
//		buttoncon.gridx = 2;
//		buttoncon.gridy = 10;
//		elevCapPanel.add(upDateButton, buttoncon);
//
//		add(elevCapPanel);
//		setupCellEditor(elevationCapabilityTable);
//
//		loadTable();
//	}
//
//	private void setupCellEditor(JTable table) {
//		Action action = new AbstractAction() {
//			private static final long serialVersionUID = 9095295324523960568L;
//
//			public void actionPerformed(ActionEvent e) {
//				TableCellListener tcl = (TableCellListener) e.getSource();
//				if (!tcl.getNewValue().toString().equalsIgnoreCase(tcl.getOldValue().toString())) {
//					upDateButton.setEnabled(true);
//				}
//			}
//		};
//		TableCellListener tcl = new TableCellListener(elevationCapabilityTable, action);
//	}
//
//	private void exit_ActionPerformed(ActionEvent e) {
//		setVisible(false);
//	}
//
//	private void upDateDB_ActionPerformed(ActionEvent e) {
//	}
//
//	protected void loadTable() {
//		int SITE_COL = 0;
//		int ELEVATION_COL = 1;
//		int CAPABILITY_COL = 2;
//		int KEY_COL = 3;
//
//		JTable table = getElevCapTable();
//		JTableHeader header = table.getTableHeader();
//		if (header != null) {
//			header.setVisible(false);
//		}
//		table.setGridColor(Color.LIGHT_GRAY);
//		table.setShowGrid(true);
//		table.setShowVerticalLines(true);
//
//		int rows = 1;
//		double elev = 1000.0D;
//		int cap = 210;
//		while (rows < table.getRowCount() - 2) {
//			table.setValueAt("FTPK", rows, SITE_COL);
//			table.setValueAt(Util.getFormatOneDecimal().format(elev), rows, ELEVATION_COL);
//			table.setValueAt(Integer.valueOf(cap), rows, CAPABILITY_COL);
//			table.setValueAt(Integer.valueOf(rows), rows, KEY_COL);
//			rows++;
//			cap++;
//			elev += 0.1D;
//		}
//		setElevCapTable(table);
//	}
//
//	protected int getTableRowCount() {
//		return 205;
//	}
//
//	public TreeMap<String, List<ThreeWkStationData>> getElevCapabilityDBData() {
//		Connection dbconn = null;
//		TreeMap<String, List<ThreeWkStationData>> dataMap = null;
//		try {
//			dbconn = Util.getDBConnection();
//			Statement stmt = dbconn.createStatement();
//
//			Calendar cal = Calendar.getInstance();
//			int monthOne = cal.get(2) + 1;
//			int monthThree = cal.get(2) + 3;
//
//			String sql = "select * from LOCAL_SCHEDULE_INFO where MONTH_ID between " + monthOne + " and " + monthThree
//					+ " order by SITE, ELEVATION ASC";
//			ResultSet rset = stmt.executeQuery(sql);
//
//			dataMap = new TreeMap();
//			List<ThreeWkStationData> ftpk = new ArrayList();
//			List<ThreeWkStationData> garr = new ArrayList();
//			List<ThreeWkStationData> oahe = new ArrayList();
//			List<ThreeWkStationData> bend = new ArrayList();
//			List<ThreeWkStationData> ftra = new ArrayList();
//			List<ThreeWkStationData> gapt = new ArrayList();
//			while (rset.next()) {
//				ThreeWkStationData twmd = new ThreeWkStationData();
//				twmd.setMonth(rset.getString(1));
//				int monthid = rset.getInt(2);
//				twmd.setMonthID(monthid);
//				twmd.setStation(rset.getString(3));
//				int siteid = rset.getInt(4);
//				twmd.setStationID(siteid);
//				switch (siteid) {
//				case 1:
//					ftpk.add(twmd);
//					break;
//				case 2:
//					garr.add(twmd);
//					break;
//				case 3:
//					oahe.add(twmd);
//					break;
//				case 4:
//					bend.add(twmd);
//					break;
//				case 5:
//					ftra.add(twmd);
//					break;
//				case 6:
//					gapt.add(twmd);
//				}
//			}
//			dataMap.put("1", ftpk);
//			dataMap.put("2", garr);
//			dataMap.put("3", oahe);
//			dataMap.put("4", bend);
//			dataMap.put("5", ftra);
//			dataMap.put("6", gapt);
//
//			rset.close();
//			stmt.close();
//			dbconn.close();
//		} catch (SQLException sqle) {
//			sqle.printStackTrace();
//		}
//		return dataMap;
//	}
//
//	private static JTable getElevCapTable() {
//		return elevationCapabilityTable;
//	}
//
//	private void setElevCapTable(JTable elevCapTable) {
//		elevationCapabilityTable = elevCapTable;
//	}
//}
