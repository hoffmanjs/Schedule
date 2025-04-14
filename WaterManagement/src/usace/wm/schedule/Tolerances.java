package usace.wm.schedule;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.text.JTextComponent;

public class Tolerances extends JDialog {
	private static final long serialVersionUID = 7526472295622776147L;
	private JTable toleranceTable = new JTable();
	private JButton backButton = new JButton();
	private JButton updateButton = new JButton();
	private String[] capToleranceInfo = null;
	private Vector<String> sites = null;

	// Table Columns defined
	int columnZero = 0;
	int columnOne = 1;
	int columnTwo = 2;
	int columnThree = 3;
	int columnFour = 4;
	int columnFive = 5;
	int columnSix = 6;
	int columnSeven = 7;

	// Table Rows defined
	int rowZero = 0;
	int rowOne = 1;
	int satCapRow = 2;
	int monCapRow = 3;
	int plusTolRow = 4;
	int minusTolRow = 5;

	/**
	 * Constructor
	 * 
	 * @param capToleranceInfo
	 *            - String[] - capacity and tolerance information
	 * @param damns
	 *            - Vector<String> - the reservoir site names
	 */
	public Tolerances(String[] capToleranceInfo, Vector<String> damns) {
		super();

		setCapToleranceInfo(capToleranceInfo);
		setSites(damns);

		try {
			jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * The window initialization method sets up and build the window
	 * 
	 * @throws Exception
	 */
	private void jbInit() throws Exception {
		this.getContentPane().setLayout(null);
		this.setSize(new Dimension(450, 200));
		this.setTitle("Capacity and Tolerance Information");
		this.setResizable(false);
		this.setModal(true);
		backButton.setText("Back");
		backButton.setMnemonic('B');
		backButton.setToolTipText("Cancel/Close Window");
		backButton.setBounds(new Rectangle(95, 135, 75, 25));
		backButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				backButton_actionPerformed(e);
			}
		});
		updateButton.setText("Update");
		updateButton.setMnemonic('U');
		updateButton.setToolTipText("Update/Save Changes");
		updateButton.setBounds(new Rectangle(250, 135, 75, 25));
		updateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateButton_actionPerformed(e);
			}
		});

		DefaultTableModel model = new DefaultTableModel(6, 8);

		// Define the table model for highlighting specific rows
		toleranceTable = new JTable(model) {
			private static final long serialVersionUID = 7526472295622776147L;

			public Component prepareRenderer(TableCellRenderer renderer, int rowIndex, int vColIndex) {
				Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);
				c.setFont(new Font("Times New Roman", Font.BOLD, 14));
				c.setBackground(Color.WHITE);
				c.setForeground(getForeground());
				return c;
			}

			@Override
			public Component prepareEditor(TableCellEditor editor, int row, int column) {
				Component c = super.prepareEditor(editor, row, column);
				if (c instanceof JTextComponent) {
					// String val = (String) toleranceTable.getValueAt(row,
					// column);
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
				if (row < getPlusTolRow()) {
					return false;
				} else {
					if (col < getColumnTwo()) {
						return false;
					} else {
						return true;
					}
				}
			}
		};

		toleranceTable.setDefaultRenderer(Object.class, new SelectAllRenderer());

		// Set the first visible column to 100 pixels wide
		TableColumn col = toleranceTable.getColumnModel().getColumn(0);
		col.setPreferredWidth(110);

		col = toleranceTable.getColumnModel().getColumn(1);
		col.setPreferredWidth(10);

		col = toleranceTable.getColumnModel().getColumn(3);
		col.setPreferredWidth(80);

		col = toleranceTable.getColumnModel().getColumn(4);
		col.setPreferredWidth(80);

		col = toleranceTable.getColumnModel().getColumn(5);
		col.setPreferredWidth(80);

		col = toleranceTable.getColumnModel().getColumn(6);
		col.setPreferredWidth(80);

		col = toleranceTable.getColumnModel().getColumn(7);
		col.setPreferredWidth(80);

		// Disable auto resizing
		toleranceTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		toleranceTable.setGridColor(Color.LIGHT_GRAY);
		toleranceTable.setShowGrid(true);
		toleranceTable.setShowVerticalLines(false);
		// TODO
		toleranceTable.setBounds(new Rectangle(10, 15, 400, 110));
		toleranceTable.setBorder(BorderFactory.createLineBorder(Color.black, 1));
		this.getContentPane().add(updateButton, null);
		this.getContentPane().add(backButton, null);
		this.getContentPane().add(toleranceTable, null);
		toleranceTable.setName(Util.getToleranceTableName());
		setToleranceTable(toleranceTable);
		loadTable();
		setUpIntegerEditor(toleranceTable);

	}

	/**
	 * The table cell data editor
	 * 
	 * @param table
	 *            - JTable
	 */
	private void setUpIntegerEditor(JTable table) {

		// Set up the editor for the integer cells.
		Action action = new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 9095295324523960568L;

			public void actionPerformed(ActionEvent e) {
				TableCellListener tcl = (TableCellListener) e.getSource();
				if (tcl.isFlag()) {
					if (tcl.getColumn() == 7 && (tcl.getRow() == 4 || tcl.getRow() == 5)) {

					} else {
						updateButton.setEnabled(false);
					}
				} else {
					updateButton.setEnabled(true);
				}
			}

		};
		TableCellListener tcl = new TableCellListener(toleranceTable, action);
	}

	/**
	 * Loads the capacity and tolerance window
	 */
	private void loadTable() {
		JTable table = getToleranceTable();

		// Put the dam sites in the table
		Vector<String> sites = getSites();
		for (int x = 0; x < sites.size(); x++) {
			table.setValueAt(sites.get(x).toString(), 1, x + 2);
		}

		table.setValueAt("Sat Cap", getSatCapRow(), getColumnZero());
		table.setValueAt("Mon Cap", getMonCapRow(), getColumnZero());
		table.setValueAt("+ Tol", getPlusTolRow(), getColumnZero());
		table.setValueAt("- Tol", getMinusTolRow(), getColumnZero());

		String[] capTol = getCapToleranceInfo();
		int row = 2;
		int count = 1;
		for (int x = 1; x < getCapToleranceInfo().length; x++) {
			table.setValueAt(capTol[x], row, 1 + count);
			count++;
			if (x % 6 == 0) {
				row++;
				count = 1;
			}
		}

		setToleranceTable(table);
	}

	/**
	 * Closes the window when the back button is pressed
	 * 
	 * @param e
	 *            - ActionEvent
	 */
	private void backButton_actionPerformed(ActionEvent e) {
		this.setVisible(false);
	}

	/**
	 * Updates the capacity and tolerance information when the update button is
	 * pressed
	 * 
	 * @param e
	 *            - ActionEvent
	 */
	private void updateButton_actionPerformed(ActionEvent e) {
		String[] capTolUpdate = new String[25];
		capTolUpdate[0] = "99999";
		int count = 1;
		JTable table = getToleranceTable();
		// Stop any table editing that maybe going on
		if (null != table.getCellEditor()) {
			table.getCellEditor().stopCellEditing();
		}
		for (int row = getSatCapRow(); row < 6; row++) {
			for (int col = getColumnTwo(); col < 8; col++) {
				capTolUpdate[count] = (String) table.getValueAt(row, col);
				count++;
			}
		}
		setCapToleranceInfo(capTolUpdate);
	}

	/**
	 * Set the capacity and tolerance Information
	 * 
	 * @param cti
	 *            - String[]
	 */
	private void setCapToleranceInfo(String[] cti) {
		capToleranceInfo = cti;
	}

	/**
	 * Get the capacity and tolerance Information
	 * 
	 * @return capToleranceInfo - String[]
	 */
	protected String[] getCapToleranceInfo() {
		return capToleranceInfo;
	}

	/**
	 * Set the Tolerance table
	 * 
	 * @param tt
	 *            - JTable
	 */
	protected void setToleranceTable(JTable tt) {
		toleranceTable = tt;
	}

	/**
	 * Get the tolerance table
	 * 
	 * @return toleranceTable - JTable
	 */
	protected JTable getToleranceTable() {
		return toleranceTable;
	}

	/**
	 * Set the reservoir sites
	 * 
	 * @param damns
	 *            - Vector<String>
	 */
	private void setSites(Vector<String> damns) {
		sites = damns;
	}

	/**
	 * Get the reservoir sites
	 * 
	 * @return sites - Vector<String>
	 */
	private Vector<String> getSites() {
		return sites;
	}

	private int getColumnZero() {
		return columnZero;
	}

	/**
	 * Get the table index for column 2
	 * 
	 * @return columnTwo - int
	 */
	private int getColumnTwo() {
		return columnTwo;
	}

	/**
	 * Get the Saturday capacity table row number
	 * 
	 * @return satCapRow - int
	 */
	private int getSatCapRow() {
		return satCapRow;
	}

	/**
	 * Get the Monday capacity table row number
	 * 
	 * @return monCapRow - int
	 */
	private int getMonCapRow() {
		return monCapRow;
	}

	/**
	 * Get the plus tolerance information row number from the table
	 * 
	 * @return plusTolRow - int
	 */
	private int getPlusTolRow() {
		return plusTolRow;
	}

	/**
	 * Get the minus tolerance information row number from the table
	 * 
	 * @return minusTolRow - int
	 */
	private int getMinusTolRow() {
		return minusTolRow;
	}
}