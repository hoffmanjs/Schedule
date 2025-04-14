package usace.wm.schedule;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.print.PrinterException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

public class WaterManagementUI extends JFrame {
	private static final long serialVersionUID = 7526472295622776147L;
	private JMenuBar menuBar = new JMenuBar();
	private JMenu menuFile = new JMenu();
	private JMenuItem menuFileExit = new JMenuItem();
	private JMenu menuHelp = new JMenu();
	private JMenuItem menuHelpAbout = new JMenuItem();
	private JMenuItem menuScheduleHelp = new JMenuItem();
	private JTabbedPane tabpane = new JTabbedPane();
	private JPanel schedulePanel = new JPanel();
	private JButton saveOutputFilesButton = new JButton();
	private JButton displayPropertiesButton = new JButton();
	private JButton printButton = new JButton();
	private JButton printRecordCopyButton = new JButton();
	private JButton scheduleReschedButton = new JButton();
	private JButton createBriefSchFileButton = new JButton();
	private JButton updateElevCapDBButton = new JButton();
	private JLabel lastUpdateLabel = new JLabel();
	private JLabel userNameLabel = new JLabel();
	private JLabel dateLabel = new JLabel();
	private JScrollPane tableScrollPane = null;
	private static JTable scheduleTable;
	private static JTable printScheduleTable;
	private static JTable printRecordCopyTable;
	private Double rowTotals = Double.valueOf(0.0D);
	private static LinkedHashMap<String, LinkedHashMap<String, Vector<OutagesTable>>> outagesMap = null;
	Double ftpkPlusTolerance = null;
	Double garrPlusTolerance = null;
	Double ftraPlusTolerance = null;
	Double ftpkMinusTolerance = null;
	Double garrMinusTolerance = null;
	Double ftraMinusTolerance = null;
	ScheduleFileData schedFileData = null;
	int maxRowNum = 0;
	Calendar schedStartDay = null;
	Calendar schedEndDay = null;
	String schedFileEndDay = null;
	Calendar dailyEndDay = null;
	int startDayAdjustment = -6;
	private static PrintText printTextWin = null;
	private static PrintText printRecordCopyWin = null;
	private DefaultTableModel defaultTableModel = null;
	private static boolean saveFlag = false;
	int columnZero = 0;
	int columnOne = 1;
	int columnTwo = 2;
	int columnThree = 3;
	int columnFour = 4;
	int columnFive = 5;
	int columnSix = 6;
	int columnSeven = 7;
	int columnEight = 8;
	int columnNine = 9;
	int columnTen = 10;
	int rowZero = 0;
	int rowOne = 1;
	int rowTwo = 2;
	int rowThree = 3;
	int rowFour = 4;
	int rowFive = 5;
	int rowSix = 6;

	public WaterManagementUI() {
		try {
			jbInit();
		} catch (Exception e) {
			//TODO
			e.printStackTrace();
			String message = getStackTrace(e);
			JOptionPane.showMessageDialog(null, "Error Exception: \n" + message, "Error Exception Occured", 0);
		}
	}
	
	/**
	 * Get the stack trace
	 * @param throwable
	 * @return
	 */
	public static String getStackTrace(final Throwable throwable) {
	     final StringWriter sw = new StringWriter();
	     final PrintWriter pw = new PrintWriter(sw, true);
	     throwable.printStackTrace(pw);
	     return sw.getBuffer().toString();
	}

	public static void main(String[] args) {
		SplashScreen splashScreen = new SplashScreen();
		long startTime = System.currentTimeMillis();		
		try {
			
			UIManager.LookAndFeelInfo[] arrayOfLookAndFeelInfo;
			int j = (arrayOfLookAndFeelInfo = UIManager.getInstalledLookAndFeels()).length;
			for (int i = 0; i < j; i++) {
				UIManager.LookAndFeelInfo info = arrayOfLookAndFeelInfo[i];
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
				UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			}
								
			WaterManagementUI window = new WaterManagementUI();			

			Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

			double screenWidth = screen.width * 0.8D;
			double screenHeight = screen.height * 0.8D;
			window.setSize((int) screenWidth, (int) screenHeight);

			int winWidth = window.getSize().width;
			int winHeight = window.getSize().height;

			int x = (screen.width - winWidth) / 2;
			int y = (screen.height - winHeight) / 2;

			window.setLocation(x, y);

			window.setIconImage(Toolkit.getDefaultToolkit().getImage("/USACE.gif"));
			window.setVisible(true);
				
			window.addWindowListener(new WindowListener() {
				public void windowClosed(WindowEvent arg0) {
//					System.out.println("closed");
				}

				public void windowActivated(WindowEvent arg0) {
//					System.out.println("Active");					
				}

				public void windowClosing(WindowEvent arg0) {
					if (WaterManagementUI.isSaveFlag()) {
						if (JOptionPane.showConfirmDialog(null, "Are you sure you want to exit without saving?",
								"Exit Without Saving?", 0) == 0) {
							System.exit(0);
						}
					} else {
						System.exit(0);
					}
				}

				public void windowDeactivated(WindowEvent arg0) {
//					System.out.println("deActive");
				}

				public void windowDeiconified(WindowEvent arg0) {
				}

				public void windowIconified(WindowEvent arg0) {
				}

				public void windowOpened(WindowEvent arg0) {
//					System.out.println("open");
				}
			});
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		} 
		long endTime = System.currentTimeMillis();
		System.out.println("It took " + (endTime - startTime) + " milliseconds");
		splashScreen.setVisible(false);
	}

	/**
	 * Set up the table of information
	 * 
	 * @param table
	 *            - JTable
	 */
	private void setupTable(JTable table) {
		// Define the table model for highlighting specific rows
		table = new JTable(getDefaultTableModel()) {
			private static final long serialVersionUID = 7526472295622776147L;

			public Component prepareRenderer(TableCellRenderer renderer, int rowIndex, int vColIndex) {
				Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);
				c.setFont(new Font("Times New Roman", Font.BOLD, 14));
				c.setPreferredSize(new Dimension(100, 20));
				if (rowIndex < getMaxRowNum()) {
					Object obj = getValueAt(rowIndex, 11);
					if (null != obj) {
						String val = obj.toString();
						if (val.equals("true")) {
							c.setForeground(Color.BLUE);
							c.setBackground(Color.WHITE);
						} else {
							c.setBackground(Color.WHITE);
							c.setForeground(getForeground());
						}
					}
				} else {
					c.setBackground(Color.WHITE);
					c.setForeground(getForeground());
				}

				return c;
			}

			/**
			 * This method makes each cell noneditable
			 */
			public boolean isCellEditable(int row, int col) {

				// Note that the data/cell address is constant,
				// no matter where the cell appears onscreen.
				if (col < 12) {
					return false;
				} else {
					return true;
				}
			}

			// Right justify the table cells
			DefaultTableCellRenderer renderRight = new DefaultTableCellRenderer();

			{
				// initializer block
				renderRight.setHorizontalAlignment(SwingConstants.RIGHT);
			}

			@Override
			public TableCellRenderer getCellRenderer(int arg0, int arg1) {
				return renderRight;
			}
		};

		// Set the first visible column to 100 pixels wide
		TableColumn col0 = table.getColumnModel().getColumn(0);
		col0.setPreferredWidth(100);

		TableColumn col1 = table.getColumnModel().getColumn(1);
		col1.setPreferredWidth(70);

		TableColumn col2 = table.getColumnModel().getColumn(2);
		col2.setPreferredWidth(90);

		TableColumn col3 = table.getColumnModel().getColumn(3);
		col3.setPreferredWidth(80);

		TableColumn col4 = table.getColumnModel().getColumn(4);
		col4.setPreferredWidth(70);

		TableColumn col6 = table.getColumnModel().getColumn(6);
		col6.setPreferredWidth(85);

		TableColumn col7 = table.getColumnModel().getColumn(7);
		col7.setPreferredWidth(5);

		TableColumn col8 = table.getColumnModel().getColumn(8);
		col8.setPreferredWidth(60);

		TableColumn col9 = table.getColumnModel().getColumn(9);
		col9.setPreferredWidth(80);

		TableColumn col10 = table.getColumnModel().getColumn(10);
		col10.setPreferredWidth(65);

		table.getColumnModel().getColumn(11).setMaxWidth(0);
		table.getColumnModel().getColumn(11).setMinWidth(0);
		table.getColumnModel().getColumn(11).setPreferredWidth(0);
		setScheduleTable(table);
	}

	// private void setupTable(JTable table) {
	// table = new JTable(getDefaultTableModel()) {
	// private static final long serialVersionUID = 7526472295622776147L;
	// DefaultTableCellRenderer renderRight;
	//
	// public Component prepareRenderer(TableCellRenderer renderer, int
	// rowIndex, int vColIndex) {
	// Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);
	// c.setFont(new Font("Times New Roman", 1, 14));
	// c.setPreferredSize(new Dimension(100, 20));
	// if (rowIndex < getMaxRowNum()) {
	// Object obj = getValueAt(rowIndex, 11);
	// if (obj != null) {
	// String val = obj.toString();
	// if (val.equals("true")) {
	// c.setForeground(Color.BLUE);
	// c.setBackground(Color.WHITE);
	// } else {
	// c.setBackground(Color.WHITE);
	// c.setForeground(getForeground());
	// }
	// }
	// } else {
	// c.setBackground(Color.WHITE);
	// c.setForeground(getForeground());
	// }
	// return c;
	// }
	//
	// public boolean isCellEditable(int row, int col) {
	// if (col < 12) {
	// return false;
	// } else {
	// return true;
	// }
	// }
	//
	// public TableCellRenderer getCellRenderer(int arg0, int arg1) {
	// return renderRight;
	// }
	// };
	// TableColumn col0 = table.getColumnModel().getColumn(0);
	// col0.setPreferredWidth(100);
	//
	// TableColumn col1 = table.getColumnModel().getColumn(1);
	// col1.setPreferredWidth(70);
	//
	// TableColumn col2 = table.getColumnModel().getColumn(2);
	// col2.setPreferredWidth(90);
	//
	// TableColumn col3 = table.getColumnModel().getColumn(3);
	// col3.setPreferredWidth(80);
	//
	// TableColumn col4 = table.getColumnModel().getColumn(4);
	// col4.setPreferredWidth(70);
	//
	// TableColumn col6 = table.getColumnModel().getColumn(6);
	// col6.setPreferredWidth(85);
	//
	// TableColumn col7 = table.getColumnModel().getColumn(7);
	// col7.setPreferredWidth(5);
	//
	// TableColumn col8 = table.getColumnModel().getColumn(8);
	// col8.setPreferredWidth(60);
	//
	// TableColumn col9 = table.getColumnModel().getColumn(9);
	// col9.setPreferredWidth(80);
	//
	// TableColumn col10 = table.getColumnModel().getColumn(10);
	// col10.setPreferredWidth(65);
	//
	// table.getColumnModel().getColumn(11).setMaxWidth(0);
	// table.getColumnModel().getColumn(11).setMinWidth(0);
	// table.getColumnModel().getColumn(11).setPreferredWidth(0);
	// setScheduleTable(table);
	// }

	private DefaultTableModel getDefaultTableModel() {
		return defaultTableModel;
	}

	private void jbInit() throws Exception {
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		if (!Util.loadProperties()) {
			System.exit(0);
		}
		long startTime2 = System.currentTimeMillis();
				
		// Bring up help on F1 key
		tabpane.registerKeyboardAction(new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(final ActionEvent event) {
				scheduleHelp_ActionPerformed(event);
			}
		}, KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0), JComponent.WHEN_FOCUSED);

		defaultTableModel = new DefaultTableModel(65, 12);
		setupTable(getScheduleTable());
		setupPrintTable();
		setupPrintRecordCopyTable();

		setJMenuBar(menuBar);
		setSize(new Dimension(850, 560));
		setTitle("USACE - Water Management Tools");
		menuFile.setText("File");
		menuFile.setMnemonic('F');
		menuFileExit.setText("Exit");
		menuFileExit.setMnemonic('x');
		menuFileExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				fileExit_ActionPerformed(ae);
			}
		});
		// Bring up help on F1 key
		menuFile.registerKeyboardAction(new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(final ActionEvent event) {
				scheduleHelp_ActionPerformed(event);
			}
		}, KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0), JComponent.WHEN_FOCUSED);

		menuHelp.setText("Help");
		menuHelp.setMnemonic('H');
		menuHelpAbout.setText("About");
		menuHelpAbout.setMnemonic('A');
		menuHelpAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				helpAbout_ActionPerformed(ae);
			}
		});
		menuScheduleHelp.setText("Schedule");
		menuScheduleHelp.setMnemonic('S');
		menuScheduleHelp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				scheduleHelp_ActionPerformed(ae);
			}
		});
		tabpane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		schedulePanel.setLayout(new GridBagLayout());

		displayPropertiesButton.setText("Properties");
		displayPropertiesButton.setMnemonic('r');
		displayPropertiesButton.setToolTipText("Schedule Properties");
		displayPropertiesButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				displayPropertiesButton_actionPerformed(e);
			}
		});

		// Bring up help on F1 key
		displayPropertiesButton.registerKeyboardAction(new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(final ActionEvent event) {
				scheduleHelp_ActionPerformed(event);
			}
		}, KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0), JComponent.WHEN_FOCUSED);

		printButton.setText("Print");
		printButton.setMnemonic('p');
		printButton.setToolTipText("Print Table");
		printButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Print_ActionPerformed(e);
			}
		});
		// Bring up help on F1 key
		printButton.registerKeyboardAction(new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(final ActionEvent event) {
				scheduleHelp_ActionPerformed(event);
			}
		}, KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0), JComponent.WHEN_FOCUSED);

		printRecordCopyButton.setText("Print Record Copy");
		printRecordCopyButton.setMnemonic('C');
		printRecordCopyButton.setToolTipText("Print Record Copy");
		printRecordCopyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				printRecordCopy_ActionPerformed(e);
			}
		});
		// Bring up help on F1 key
		printRecordCopyButton.registerKeyboardAction(new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(final ActionEvent event) {
				scheduleHelp_ActionPerformed(event);
			}
		}, KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0), JComponent.WHEN_FOCUSED);

		scheduleReschedButton.setText("Schedule/Reschedule");
		scheduleReschedButton.setMnemonic('s');
		scheduleReschedButton.setToolTipText("Show Schedule Window");
		scheduleReschedButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scheduleReschedButton_actionPerformed(e);
			}
		});
		// Bring up help on F1 key
		scheduleReschedButton.registerKeyboardAction(new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(final ActionEvent event) {
				scheduleHelp_ActionPerformed(event);
			}
		}, KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0), JComponent.WHEN_FOCUSED);

		saveOutputFilesButton.setText("Save Files");
		saveOutputFilesButton.setMnemonic('a');
		saveOutputFilesButton.setToolTipText("Update/Save Output File");
		saveOutputFilesButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveOutputFilesButton_actionPerformed(e);
			}
		});
		// Bring up help on F1 key
		saveOutputFilesButton.registerKeyboardAction(new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(final ActionEvent event) {
				scheduleHelp_ActionPerformed(event);
			}
		}, KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0), JComponent.WHEN_FOCUSED);

		createBriefSchFileButton.setText("Create Brief");
		createBriefSchFileButton.setMnemonic('b');
		createBriefSchFileButton.setToolTipText("Create Brief File");
		createBriefSchFileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createBriefSchFileButton_actionPerformed(e);
				copyBriefFileFileButton_actionPerformed(e);
			}
		});
		// Bring up help on F1 key
		createBriefSchFileButton.registerKeyboardAction(new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(final ActionEvent event) {
				scheduleHelp_ActionPerformed(event);
			}
		}, KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0), JComponent.WHEN_FOCUSED);

		updateElevCapDBButton.setText("Update DB Elev Cap Table");
		updateElevCapDBButton.setMnemonic('u');
		updateElevCapDBButton.setToolTipText("Update DB Elev. & Capability Table");
		updateElevCapDBButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateElevCapDBButton_actionPerformed(e);
			}
		});
		// Bring up help on F1 key
		updateElevCapDBButton.registerKeyboardAction(new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(final ActionEvent event) {
				scheduleHelp_ActionPerformed(event);
			}
		}, KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0), JComponent.WHEN_FOCUSED);

		// Bring up help on F1 key
		scheduleTable.registerKeyboardAction(new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(final ActionEvent event) {
				scheduleHelp_ActionPerformed(event);
			}
		}, KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0), JComponent.WHEN_FOCUSED);

		GridBagConstraints labelcon = new GridBagConstraints();
		labelcon.anchor = GridBagConstraints.CENTER; // bottom of space
		labelcon.fill = GridBagConstraints.HORIZONTAL;
		labelcon.gridwidth = 2;
		labelcon.gridheight = 1;
		labelcon.weightx = 0.5D;
		labelcon.weighty = 0.0D;
		labelcon.insets = new Insets(1, 1, 1, 1);
		labelcon.gridx = 3;
		labelcon.gridy = 9;

		lastUpdateLabel.setText("Last Updated by:");

		GridBagConstraints datelblcon = new GridBagConstraints();
		datelblcon.anchor = GridBagConstraints.CENTER; // bottom of space
		datelblcon.fill = GridBagConstraints.HORIZONTAL;
		datelblcon.gridwidth = 2;
		datelblcon.gridheight = 1;
		datelblcon.weightx = 0.5D;
		datelblcon.weighty = 0.0D;
		datelblcon.insets = new Insets(1, 1, 1, 1);
		datelblcon.gridx = 7;
		datelblcon.gridy = 9;

		dateLabel.setText("on: " + Util.getLastUpdate());

		GridBagConstraints usernamelblcon = new GridBagConstraints();
		usernamelblcon.anchor = GridBagConstraints.CENTER; // bottom of space
		usernamelblcon.fill = GridBagConstraints.HORIZONTAL;
		usernamelblcon.gridwidth = 2;
		usernamelblcon.gridheight = 1;
		usernamelblcon.weightx = 0.5D;
		usernamelblcon.weighty = 0.0D;
		usernamelblcon.insets = new Insets(1, 1, 1, 1);
		usernamelblcon.gridx = 5;
		usernamelblcon.gridy = 9;

		userNameLabel.setText(Util.getUserName());
		userNameLabel.setForeground(Color.BLUE);

		menuFile.add(menuFileExit);
		menuBar.add(menuFile);
		menuHelp.add(menuScheduleHelp);
		menuHelp.add(menuHelpAbout);
		menuBar.add(menuHelp);
		tableScrollPane = new JScrollPane(scheduleTable);

		GridBagConstraints tablecon = new GridBagConstraints();
		tablecon.fill = GridBagConstraints.HORIZONTAL;
		tablecon.anchor = GridBagConstraints.PAGE_START; // bottom of space
		tablecon.ipady = 1000;
		tablecon.ipadx = 850;
		tablecon.gridwidth = 11;
		tablecon.gridheight = 8;
		tablecon.weighty = 1.0;
		schedulePanel.add(tableScrollPane, tablecon);

		schedulePanel.add(lastUpdateLabel, labelcon);
		schedulePanel.add(userNameLabel, usernamelblcon);
		schedulePanel.add(dateLabel, datelblcon);

		GridBagConstraints buttoncon = new GridBagConstraints();
		buttoncon.fill = GridBagConstraints.HORIZONTAL;
		buttoncon.weighty = 0.0;
		buttoncon.weightx = 0.5;
		buttoncon.anchor = GridBagConstraints.SOUTH; // bottom of space
		buttoncon.insets = new Insets(3, 1, 3, 1);

		buttoncon.gridx = 0;
		buttoncon.gridy = 9;
		schedulePanel.add(updateElevCapDBButton, buttoncon);

		buttoncon.gridx = 0;
		buttoncon.gridy = 10;
		schedulePanel.add(printRecordCopyButton, buttoncon);

		buttoncon.gridx = 2;
		buttoncon.gridy = 10;
		schedulePanel.add(printButton, buttoncon);

		buttoncon.gridx = 4;
		buttoncon.gridy = 10;
		schedulePanel.add(displayPropertiesButton, buttoncon);

		buttoncon.gridx = 6;
		buttoncon.gridy = 10;
		schedulePanel.add(scheduleReschedButton, buttoncon);

		buttoncon.gridx = 8;
		buttoncon.gridy = 10;
		schedulePanel.add(saveOutputFilesButton, buttoncon);

		buttoncon.gridx = 10;
		buttoncon.gridy = 10;
		schedulePanel.add(createBriefSchFileButton, buttoncon);

		add(tabpane);
		tabpane.add("Schedule", schedulePanel);

		// JPanel threeWeekPanel = new JPanel();
		// threeWeekPanel.setLayout(new GridBagLayout());
		// GridBagConstraints lblcon = new GridBagConstraints();
		// lblcon.fill = 10;
		// lblcon.anchor = 19;
		// lblcon.ipady = 600;
		// lblcon.gridwidth = 5;
		// JLabel thrWkLbl = new JLabel("Three Week Program Under
		// Construction");
		// threeWeekPanel.add(thrWkLbl, lblcon);

		// tabpane.add("3 Week", thrWkLbl);
		long endTime2 = System.currentTimeMillis();
		System.out.println("It took " + (endTime2 - startTime2) + " milliseconds for gui");

		upDateSchfileFromDlystatFile(true, true);
		long endTime3 = System.currentTimeMillis();
		System.out.println("It took " + (endTime3 - startTime2) + " milliseconds for update files");
		
		loadTable(getScheduleTable(), getSchedFileData(false), false, false);
	}

	/**
	 * Set up the print table
	 */
	private void setupPrintTable() {
		DefaultTableModel model = new DefaultTableModel(65, 12);

		// Define the table model for highlighting specific rows
		printScheduleTable = new JTable(model) {
			private static final long serialVersionUID = -2452023939557325485L;

			public Component prepareRenderer(TableCellRenderer renderer, int rowIndex, int vColIndex) {
				Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);
				c.setFont(new Font("Times New Roman", Font.BOLD, 18));
				if (rowIndex < getMaxRowNum()) {
					Object obj = getValueAt(rowIndex, 11);
					if (null != obj) {
						String val = obj.toString();
						if (val.equals("true")) {
							c.setForeground(Color.BLUE);
							if (rowIndex > 1 && rowIndex != 29 && rowIndex != 30 && rowIndex != 53 && rowIndex != 54) {
								c.setFont(new Font("Times New Roman", Font.BOLD, 18));
							} else if (rowIndex <= 1 || rowIndex == 29 || rowIndex == 30 || rowIndex == 53
									|| rowIndex == 54) {
								c.setFont(new Font("Times New Roman", Font.BOLD, 18));
							}
						} else {
							c.setFont(new Font("Times New Roman", Font.PLAIN, 18));
							c.setBackground(Color.WHITE);
						}
					}
				} else {
					c.setFont(new Font("Times New Roman", Font.PLAIN, 18));
					c.setBackground(Color.WHITE);
				}
				return c;
			}

			// Right justify the table cells
			DefaultTableCellRenderer renderRight = new DefaultTableCellRenderer();

			{
				// initializer block
				renderRight.setHorizontalAlignment(SwingConstants.RIGHT);
			}

			@Override
			public TableCellRenderer getCellRenderer(int arg0, int arg1) {
				return renderRight;
			}
		};

		// Disable auto resizing
		printScheduleTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		// Set the first visible column to 100 pixels wide
		TableColumn col0 = printScheduleTable.getColumnModel().getColumn(0);
		col0.setPreferredWidth(100);

		TableColumn col1 = printScheduleTable.getColumnModel().getColumn(1);
		col1.setPreferredWidth(70);

		TableColumn col2 = printScheduleTable.getColumnModel().getColumn(2);
		col2.setPreferredWidth(100);

		TableColumn col3 = printScheduleTable.getColumnModel().getColumn(3);
		col3.setPreferredWidth(80);

		TableColumn col4 = printScheduleTable.getColumnModel().getColumn(4);
		col4.setPreferredWidth(85); // 70

		TableColumn col6 = printScheduleTable.getColumnModel().getColumn(6);
		col6.setPreferredWidth(85);

		TableColumn col7 = printScheduleTable.getColumnModel().getColumn(7);
		col7.setPreferredWidth(2);// 5

		TableColumn col8 = printScheduleTable.getColumnModel().getColumn(8);
		col8.setPreferredWidth(75);

		TableColumn col9 = printScheduleTable.getColumnModel().getColumn(9);
		col9.setPreferredWidth(85);

		TableColumn col10 = printScheduleTable.getColumnModel().getColumn(10);
		col10.setPreferredWidth(70);// 65

		printScheduleTable.getColumnModel().getColumn(11).setMaxWidth(0);
		printScheduleTable.getColumnModel().getColumn(11).setMinWidth(0);
		printScheduleTable.getColumnModel().getColumn(11).setPreferredWidth(0);

	}

	/**
	 * Set up the print record copy table
	 */
	private void setupPrintRecordCopyTable() {
		DefaultTableModel model = new DefaultTableModel(65, 12);

		// Define the table model for highlighting specific rows
		printRecordCopyTable = new JTable(model) {
			private static final long serialVersionUID = -3589017634198877870L;

			public Component prepareRenderer(TableCellRenderer renderer, int rowIndex, int vColIndex) {
				Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);
				c.setFont(new Font("Times New Roman", Font.PLAIN, 18));
				c.setPreferredSize(new Dimension(100, 20));
				if (rowIndex < getMaxRowNum()) {
					Object obj = getValueAt(rowIndex, 11);
					if (null != obj) {
						String val = obj.toString();
						if (val.equals("true")) {
							c.setForeground(Color.BLUE);
							if (rowIndex > 1 && rowIndex != 29 && rowIndex != 30 && rowIndex != 53 && rowIndex != 54) {
								c.setFont(new Font("Times New Roman", Font.BOLD, 18));
							} else if (rowIndex <= 1 || rowIndex == 29 || rowIndex == 30 || rowIndex == 53
									|| rowIndex == 54) {
								c.setFont(new Font("Times New Roman", Font.BOLD, 18));
							}
						} else {
							c.setFont(new Font("Times New Roman", Font.PLAIN, 18));
							c.setBackground(Color.WHITE);
						}
					}
				} else {
					c.setFont(new Font("Times New Roman", Font.PLAIN, 18));
					c.setBackground(Color.WHITE);
				}
				return c;
			}

			// Right justify the table cells
			DefaultTableCellRenderer renderRight = new DefaultTableCellRenderer();

			{
				// initializer block
				renderRight.setHorizontalAlignment(SwingConstants.RIGHT);
			}

			@Override
			public TableCellRenderer getCellRenderer(int arg0, int arg1) {
				return renderRight;
			}
		};

		// Disable auto resizing
		printRecordCopyTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		// Set the first visible column to 100 pixels wide
		TableColumn col0 = printRecordCopyTable.getColumnModel().getColumn(0);
		col0.setPreferredWidth(100);

		TableColumn col1 = printRecordCopyTable.getColumnModel().getColumn(1);
		col1.setPreferredWidth(70);

		TableColumn col2 = printRecordCopyTable.getColumnModel().getColumn(2);
		col2.setPreferredWidth(100);

		TableColumn col3 = printRecordCopyTable.getColumnModel().getColumn(3);
		col3.setPreferredWidth(80);

		TableColumn col4 = printRecordCopyTable.getColumnModel().getColumn(4);
		col4.setPreferredWidth(85);

		TableColumn col6 = printRecordCopyTable.getColumnModel().getColumn(6);
		col6.setPreferredWidth(85);

		TableColumn col7 = printRecordCopyTable.getColumnModel().getColumn(7);
		col7.setPreferredWidth(2);

		TableColumn col8 = printRecordCopyTable.getColumnModel().getColumn(8);
		col8.setPreferredWidth(75);

		TableColumn col9 = printRecordCopyTable.getColumnModel().getColumn(9);
		col9.setPreferredWidth(85);

		TableColumn col10 = printRecordCopyTable.getColumnModel().getColumn(10);
		col10.setPreferredWidth(65);

		printRecordCopyTable.getColumnModel().getColumn(11).setMaxWidth(0);
		printRecordCopyTable.getColumnModel().getColumn(11).setMinWidth(0);
		printRecordCopyTable.getColumnModel().getColumn(11).setPreferredWidth(0);
	}

	// private void setupPrintRecordCopyTable() {
	// DefaultTableModel model = new DefaultTableModel(65, 12);
	//
	// printRecordCopyTable = new JTable(model) {
	// private static final long serialVersionUID = -3589017634198877870L;
	// DefaultTableCellRenderer renderRight;
	//
	// public Component prepareRenderer(TableCellRenderer renderer, int
	// rowIndex, int vColIndex) {
	// Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);
	// c.setFont(new Font("Times New Roman", 0, 18));
	// c.setPreferredSize(new Dimension(100, 20));
	// if (rowIndex < getMaxRowNum()) {
	// Object obj = getValueAt(rowIndex, 11);
	// if (obj != null) {
	// String val = obj.toString();
	// if (val.equals("true")) {
	// c.setForeground(Color.BLUE);
	// if ((rowIndex > 1) && (rowIndex != 29) && (rowIndex != 30) && (rowIndex
	// != 53)
	// && (rowIndex != 54)) {
	// c.setFont(new Font("Times New Roman", 1, 18));
	// } else if ((rowIndex <= 1) || (rowIndex == 29) || (rowIndex == 30) ||
	// (rowIndex == 53)
	// || (rowIndex == 54)) {
	// c.setFont(new Font("Times New Roman", 1, 18));
	// }
	// } else {
	// c.setFont(new Font("Times New Roman", 0, 18));
	// c.setBackground(Color.WHITE);
	// }
	// }
	// } else {
	// c.setFont(new Font("Times New Roman", 0, 18));
	// c.setBackground(Color.WHITE);
	// }
	// return c;
	// }
	//
	// public TableCellRenderer getCellRenderer(int arg0, int arg1) {
	// return renderRight;
	// }
	// };
	// printRecordCopyTable.setAutoResizeMode(0);
	//
	// TableColumn col0 = printRecordCopyTable.getColumnModel().getColumn(0);
	// col0.setPreferredWidth(100);
	//
	// TableColumn col1 = printRecordCopyTable.getColumnModel().getColumn(1);
	// col1.setPreferredWidth(70);
	//
	// TableColumn col2 = printRecordCopyTable.getColumnModel().getColumn(2);
	// col2.setPreferredWidth(100);
	//
	// TableColumn col3 = printRecordCopyTable.getColumnModel().getColumn(3);
	// col3.setPreferredWidth(80);
	//
	// TableColumn col4 = printRecordCopyTable.getColumnModel().getColumn(4);
	// col4.setPreferredWidth(85);
	//
	// TableColumn col6 = printRecordCopyTable.getColumnModel().getColumn(6);
	// col6.setPreferredWidth(85);
	//
	// TableColumn col7 = printRecordCopyTable.getColumnModel().getColumn(7);
	// col7.setPreferredWidth(2);
	//
	// TableColumn col8 = printRecordCopyTable.getColumnModel().getColumn(8);
	// col8.setPreferredWidth(75);
	//
	// TableColumn col9 = printRecordCopyTable.getColumnModel().getColumn(9);
	// col9.setPreferredWidth(85);
	//
	// TableColumn col10 = printRecordCopyTable.getColumnModel().getColumn(10);
	// col10.setPreferredWidth(65);
	//
	// printRecordCopyTable.getColumnModel().getColumn(11).setMaxWidth(0);
	// printRecordCopyTable.getColumnModel().getColumn(11).setMinWidth(0);
	// printRecordCopyTable.getColumnModel().getColumn(11).setPreferredWidth(0);
	// }

	void fileExit_ActionPerformed(ActionEvent e) {
		if (isSaveFlag()) {
			if (JOptionPane.showConfirmDialog(null, "Are you sure you want to exit without saving?",
					"Exit Without Saving?", 0) == 0) {
				System.exit(0);
			}
		} else {
			System.exit(0);
		}
	}

	/**
	 * Prints the file.  Will create a Microsoft PDF but not an Adobe PDF.
	 * Need to figure out why
	 * @param e
	 */
	void Print_ActionPerformed(ActionEvent e) {
		setSchedulePrintTable(Util.copyTableText(getScheduleTable(), getSchedulePrintTable()));
		boolean printFlag = false;
		try {
			tableScrollPane.getViewport().add(printScheduleTable, null);
			getSchedulePrintTable().setShowGrid(false);
			getSchedulePrintTable().setTableHeader(null);

			printFlag = getSchedulePrintTable().print();
			tableScrollPane.getViewport().add(scheduleTable, null);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		if (!printFlag) {
			PrintText printwin = getPrintTextWin();
			printwin.setVisible(true);
		}
	}

	void printRecordCopy_ActionPerformed(ActionEvent e) {
		loadTable(getPrintRecordCopyTable(), getSchedFileData(false), false, true);
		boolean printFlag = false;
		try {
			tableScrollPane.getViewport().add(printRecordCopyTable, null);
			getPrintRecordCopyTable().setShowGrid(false);
			getPrintRecordCopyTable().setTableHeader(null);
			printFlag = getPrintRecordCopyTable().print();
			tableScrollPane.getViewport().add(scheduleTable, null);
		} catch (PrinterException e1) {
			e1.printStackTrace();
		}
		if (!printFlag) {
			PrintText printwin = getPrintRecordCopyWin();
			printwin.setVisible(true);
		}
	}

	void properties_ActionPerformed(ActionEvent e) {
		ScheduleProperties queryPropWin = new ScheduleProperties();

		Point ui = getLocation();
		int width = getWidth();
		int height = getHeight();

		int w = queryPropWin.getSize().width;
		int h = queryPropWin.getSize().height;

		long x = Math.round(ui.getX() + (width - w) / 2.0D);
		long y = Math.round(ui.getY() + (height - h) / 2.0D);

		queryPropWin.setLocation((int) x, (int) y);
		queryPropWin.setVisible(true);
		if (!queryPropWin.isShowing()) {
			clearScheduleTable(getScheduleTable());
			loadTable(getScheduleTable(), getSchedFileData(false), false, false);
		}
	}

	void helpAbout_ActionPerformed(ActionEvent e) {
		//"Next DST: " + Util.getNextDstDate() + " Days: " + Util.getNextDstDays()
		JOptionPane.showMessageDialog(this, new WaterManagementUI_AboutBoxPanel(), "About", -1);
	}

	void scheduleHelp_ActionPerformed(ActionEvent e) {
		ScheduleHelp helpwin = new ScheduleHelp();

		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

		Point ui = getLocation();

		double screenWidth = screen.width * 0.8D;
		double screenHeight = screen.height * 0.8D;
		helpwin.setSize((int) screenWidth, (int) screenHeight);

		int w = helpwin.getSize().width;
		int h = helpwin.getSize().height;

		long x = Math.round(ui.getX() + ((int) screenWidth - w) / 2.0D);
		long y = Math.round(ui.getY() + ((int) screenHeight - h) / 2.0D);

		helpwin.setLocation((int) x, (int) y);
		helpwin.setVisible(true);
	}

	/**
	 * 
	 * @param e
	 */
	private void saveOutputFilesButton_actionPerformed(ActionEvent e) {
		ScheduleFileData map = getSchedFileData(false);
		//TODO
//		saveSPCData(map.getSpcMap());

		upDateSchfileFromDlystatFile(true, true);
		setSaveFlag(false);

		Util.makeBackupScheduleFile();
		Util.makeBackupDlystatFile();
	}

	private void scheduleReschedButton_actionPerformed(ActionEvent e) {
		scheduleReschedule(e);
	}

	private void updateElevCapDBButton_actionPerformed(ActionEvent e) {
		ElevationCapabilityTable elevCapTableWin = new ElevationCapabilityTable();

		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int width = screen.width;
		int height = screen.height;

		elevCapTableWin.setSize(300, (int) (height * 0.8D));

		int winWidth = elevCapTableWin.getSize().width;
		int winHeight = elevCapTableWin.getSize().height;

		int x = (width - winWidth) / 2;
		int y = (height - winHeight) / 2;

		elevCapTableWin.setLocation(x, y);
		elevCapTableWin.setVisible(true);

		elevCapTableWin.isShowing();

		elevCapTableWin.addWindowListener(new WindowListener() {
			public void windowClosed(WindowEvent arg0) {
			}

			public void windowActivated(WindowEvent arg0) {
			}

			public void windowClosing(WindowEvent arg0) {
			}

			public void windowDeactivated(WindowEvent arg0) {
			}

			public void windowDeiconified(WindowEvent arg0) {
			}

			public void windowIconified(WindowEvent arg0) {
			}

			public void windowOpened(WindowEvent arg0) {
			}
		});
	}

	private void copyBriefFileFileButton_actionPerformed(ActionEvent e) {
		if (Util.getBriefingFile() != null) {
			if (Util.getBriefingFile().length() > 0L) {
				if (Util.copyBrfSchTxtFile()) {
					JOptionPane.showMessageDialog(null, "BRFSCH File Copy Successful.", "File Copy Successful", 1);
				}
			} else {
				JOptionPane.showMessageDialog(null, "BRFSCH.TXT File Is Empty! \n Create Valid Brief File.",
						"Invalid/Empty File!", 0);
			}
		} else {
			JOptionPane.showMessageDialog(null, "BRFSCH.TXT File Does Not Exist! \n Create Brief File.",
					"File Does Not Exist!", 0);
		}
	}

	/**
	 * 
	 * @param e
	 */
	private void createBriefSchFileButton_actionPerformed(ActionEvent e) {
		setSchedulePrintTable(Util.copyTableText(getScheduleTable(), getSchedulePrintTable()));

		List<String> fileData = new ArrayList<String>();
		int lineNum = 0;
		for (int row = 0; row < getSchedulePrintTable().getRowCount(); row++) {
			StringBuffer data = new StringBuffer();
			data.append(lineNum + ",");
			for (int column = 0; column < getSchedulePrintTable().getColumnCount() - 1; column++) {
				String x = (String) getSchedulePrintTable().getValueAt(row, column);
				if (x != null) {
					if (x.trim().length() > 0) {
						data.append(x.trim() + ",");
					} else {
						data.append("~,");
					}
				} else {
					data.append("~,");
				}
			}
			fileData.add(data.toString());
			lineNum++;
		}
		Util.formatBrfSchTxt(fileData);
		
		
		//TODO save the files, added 10/31/2018
		saveOutputFilesButton_actionPerformed(e);
	}

	private void displayPropertiesButton_actionPerformed(ActionEvent e) {
		properties_ActionPerformed(e);
	}

	/**
	 * 
	 * @param e
	 */
	private void scheduleReschedule(ActionEvent e) {
		//Popup for DST reminder
		if(Util.isDstDays(null) == 1){
			JOptionPane.showMessageDialog(null, "Schedule for DST this weekend: " + Util.getNextDstDate(), "DST This Weekend.", -1);
		}
		
		GetDlyDataFile dlyDataFile = new GetDlyDataFile();

		ScheduleReschedule querySchedReschedWin = new ScheduleReschedule(getDlystatSites(),
				dlyDataFile.getDailySortedValuesMap(), getSchedFileData(false), getSchedFileEndDay(),
				getSchedFileData(false).getEnergyEndDate(), getSchedFileData(false).getSchFileStartDate(),
				getSchedFileData(false).getCapacityToleranceInfo());
		Point ui = getLocation();
		int width = getWidth();
		int height = getHeight();

		int w = querySchedReschedWin.getSize().width;
		int h = querySchedReschedWin.getSize().height;

		long x = Math.round(ui.getX() + (width - w) / 2.0D);
		long y = Math.round(ui.getY() + (height - h) / 2.0D);

		querySchedReschedWin.setLocation((int) x, (int) y);
		querySchedReschedWin.setVisible(true);
		if (!querySchedReschedWin.isShowing()) {
			setSaveFlag(querySchedReschedWin.isUpdateFlag());
			getSchedFileData(false).updateCapacityToleranceInfo(ScheduleReschedule.getCapToleranceInfo());

			clearScheduleTable(getScheduleTable());
			// loadTable(getScheduleTable(), getSchedFileData(false), true,
			// false);
			HashMap<String, String> test = Util.getSchedFileData().getSpcMap();
			loadTable(getScheduleTable(), Util.getSchedFileData(), true, false);
		}
		querySchedReschedWin.addWindowListener(new WindowListener() {
			public void windowClosed(WindowEvent arg0) {
			}

			public void windowActivated(WindowEvent arg0) {
			}

			public void windowClosing(WindowEvent arg0) {
			}

			public void windowDeactivated(WindowEvent arg0) {
			}

			public void windowDeiconified(WindowEvent arg0) {
			}

			public void windowIconified(WindowEvent arg0) {
			}

			public void windowOpened(WindowEvent arg0) {
			}
		});
	}

	private void upDateSchfileFromDlystatFile(boolean printFlag, boolean saveFlag) {
		// Get the dlystat file info
		GetDlyDataFile dlyDataFile = new GetDlyDataFile();

		/////
		// HashMap<String, DlyData> xyt = dlyDataFile.getDlyStationData();
		// Set<String> set = xyt.keySet();
		// Iterator<String> its = set.iterator();
		// while(its.hasNext()){
		// String data = its.next();
		//
		// DlyData x = xyt.get(data);
		// System.out.println(data + " " + x.getDlyGenPower() + " " +
		// x.getPoolElevationNum());
		// }
		////

		HashMap<String, Vector<DlyData>> sorted = dlyDataFile.getDailySortedValuesMap();
		setDailyEndDay(dlyDataFile.getDlystatEndDay());

		// Get the schedule file data
		ScheduleFileData sfd = getSchedFileData(false); // TODO the first call
														// to get the schfile
														// data
		setSchedFileData(sfd);

		HashMap<String, DlyData> schfileDlyData = sfd.getDlyDataMap();

		// System.out.println("size:" + schfileDlyData.size());

		// Update the schfile with the dlystat file information.
		Iterator<String> it = sorted.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			Vector<DlyData> data = (Vector<DlyData>) sorted.get(key);
			Iterator<DlyData> dlydata = data.iterator();

			int count = 0;
			while (dlydata.hasNext()) {
				// Schedule File dlydata
				String mapKey = key.substring(0, 6) + "_" + count;
				System.out.println(mapKey);
				DlyData dailySchFile = schfileDlyData.get(mapKey);

				// Daily file data
				DlyData dlystat = (DlyData) dlydata.next();
				if (null != dailySchFile) {
					// Update the schedule file dailySchFile data
					dailySchFile.setDlyGenPower(dlystat.getDlyGenPower());
					dailySchFile.setFlowTotal(dlystat.getFlowTotal());
					dailySchFile.setFlowPower(dlystat.getFlowPower());
					dailySchFile.setPoolElev(dlystat.getPoolElev());

					schfileDlyData.remove(mapKey);
					schfileDlyData.put(mapKey, dailySchFile);
				}
				count++;
			} // End while
		} // End while

		if (printFlag) {
			// HashMap<String, String[]> xyz = sfd.getOutageMap(); // 160223,
			// 160224,
			// 160225,
			// Set<String> keys = xyz.keySet();
			// Iterator<String> itz = keys.iterator();
			// while (itz.hasNext()) {
			// String data = itz.next();
			// System.out.println(data);
			// String[] r = xyz.get(data);
			// for (int x = 0; x < r.length; x++) {
			// System.out.println(x);
			// }
			// }

			// Write all data out to the schfile
			Util.writeOutFileData(schfileDlyData, sfd.getId24Map(), sfd.getSpcMap(), sfd.getBbElevMap(),
					sfd.getTotCalEnrgyMap(), sfd.getTotReschedCalEnrgyMap(), sfd.getOutageMap(),
					sfd.getCoefficientMap(), sfd.getCapacityToleranceInfo(), dlyDataFile.getDlystatStartDay(), saveFlag);

			// Need to reload the table after it is written out
			loadTable(getScheduleTable(), getSchedFileData(true), false, false);

			// Update the properties file
			Util.saveProperties();
		}

		// Added this part to pull outage info from DB
		Calendar start = Calendar.getInstance();
		start.setTimeZone(TimeZone.getTimeZone("UTC"));
		start.set(Calendar.HOUR_OF_DAY, 6);
		start.set(Calendar.MINUTE, 0);
		start.set(Calendar.SECOND, 0);
		start.add(Calendar.DAY_OF_MONTH, 6); // Add future days for
												// schedule/reschedule
		// System.out.println("start: " + start.getTime().toString());
		Calendar end = (Calendar) start.clone();
		end.setTimeZone(TimeZone.getTimeZone("UTC"));
		end.add(Calendar.DAY_OF_MONTH, -13);// go back 13 days
		// System.out.println("end: " + end.getTime().toString());

		HashMap<String, String> map = Util.getOutagesMap();
		Set<String> keys = map.keySet();
		Iterator<String> keyit = keys.iterator();
		Vector<List<CwmsTableData>> allOutages = new Vector<List<CwmsTableData>>();
		
		long startTime1 = System.currentTimeMillis();		
		while (keyit.hasNext()) {
			String site = keyit.next();
			String loc = map.get(site);
			// System.out.println("loc: " + loc);
			List<CwmsTableData> data = Util.getDbOutages(end, start, loc, "Unk");
			allOutages.add(data);
			// System.out.println(data.size());
		}
		sortOutages(end, start, allOutages);
		long endTime1 = System.currentTimeMillis();
		System.out.println("It took " + (endTime1 - startTime1) + " milliseconds for DB");

		// end of new outages part
	}

	/**
	 * 
	 */
	//TODO
//	private void saveSPCData(HashMap<String, String> spcMap) {
//		boolean spc = Util.saveSpencerData(spcMap);
//		// if(!spc){
//		// JOptionPane.showMessageDialog(this, "Error storing Spencer values to
//		// the CWMS database", "Error", -1);
//		// }
//	}

	/**
	 * Sort outage information into a usefull set
	 * 
	 * @param end
	 * @param start
	 * @param data
	 */
	private void sortOutages(Calendar end, Calendar start, Vector<List<CwmsTableData>> data) {
		setOutagesMap(new LinkedHashMap<String, LinkedHashMap<String, Vector<OutagesTable>>>());

		Iterator<List<CwmsTableData>> it = data.iterator();
		while (it.hasNext()) {
			List<CwmsTableData> list = it.next();
			Iterator<CwmsTableData> tbl = list.iterator();
			while (tbl.hasNext()) {
				CwmsTableData td = tbl.next();
				if (td.outage) {
					// String num = td.getUnitNumber();
					String id = td.getLocationID();
					// String date = td.getDateTime();
					String ts = td.getTimeSeries();
					String d = td.getDate(); // 160223
					int x = td.getUnit();
					// System.out.println(date + " " + id + " " + num + " " + d
					// + " " + ts + " " + x);
					loadoutagesMap(id, d, ts, x);
				}
			}
		}

		// LinkedHashMap<String, LinkedHashMap<String, Vector<OutagesTable>>>
		// oMap = getOutagesMap();
		// System.out.println(oMap.size());
		// Set<String> set = oMap.keySet();
		// Iterator<String> iter = set.iterator();
		// while(iter.hasNext()){
		// String day = iter.next();
		// System.out.println("date: " + day);
		// LinkedHashMap<String, Vector<OutagesTable>> map = oMap.get(day);
		// Set<String> s = map.keySet();
		// Iterator<String> i = s.iterator();
		// while(i.hasNext()){
		// String xyz = i.next();
		// System.out.println("location: " + xyz);
		// Vector<OutagesTable> vec = map.get(xyz);
		// for(int x=0; x<vec.size(); x++){
		// OutagesTable outT = vec.get(x);
		// System.out.println(outT.getDate() + " " + outT.getLocId() + " " +
		// outT.getTs() + " " + outT.getUnit());
		// }//for
		// }//while
		// }//while
	}

	/**
	 * 
	 * @param locId
	 * @param date
	 * @param ts
	 * @param unit
	 */
	private void loadoutagesMap(String locId, String date, String ts, int unit) {
		// date, locationID, vector of outages
		LinkedHashMap<String, LinkedHashMap<String, Vector<OutagesTable>>> outMap = getOutagesMap();
		OutagesTable outageTable = new OutagesTable(locId, date, ts, String.valueOf(unit));

		if (outMap.containsKey(date)) { // already has the date
			// locationID, Vector<OutagesTable>
			LinkedHashMap<String, Vector<OutagesTable>> outages = outMap.get(date);
			if (outages.containsKey(locId)) {// Check for locationId, exists,
												// add outage
				Vector<OutagesTable> vec = outages.get(locId);
				vec.add(outageTable);
			} else {// locationId does not exist, so add
				Vector<OutagesTable> vec = new Vector<OutagesTable>();
				vec.add(outageTable);
				outages.put(locId, vec);
			}
		} else { // add the date
			LinkedHashMap<String, Vector<OutagesTable>> xunit = new LinkedHashMap<String, Vector<OutagesTable>>();
			Vector<OutagesTable> vec = new Vector<OutagesTable>();
			vec.add(outageTable);
			xunit.put(locId, vec);
			outMap.put(date, xunit);
		}
		setOutagesMap(outMap);
	}

	/**
	 * 
	 * @return
	 */
	private Vector<String> getDlystatSites() {
		GetDlyDataFile dlyDataFile = new GetDlyDataFile();
		Vector<String> sites = dlyDataFile.getSiteNames();
		return sites;
	}

	/**
	 * 
	 * @param table
	 * @param schedfiledata
	 * @param reschedule
	 * @param printRecordCopy
	 */
	protected void loadTable(JTable table, ScheduleFileData schedfiledata, boolean reschedule,
			boolean printRecordCopy) {
		int TOTAL_COL = 8;
		int DATE_COL = 9;
		int HIGHLIGHT_COL = 11;
		JTableHeader header = table.getTableHeader();
		if (header != null) {
			header.setVisible(false);
		}
		ScheduleFileData sfd = schedfiledata;
		// HashMap<String, String> xy = sfd.getSpcMap();

		Calendar endDay = null;
		if (!reschedule) {
			endDay = Util.getCalendarYYMMDD(sfd.getEnergyEndDate());
		} else {
			endDay = sfd.getRescheduleDataEndDate();
		}
		if (printRecordCopy) {
			endDay = Util.getCalendarYYMMDD(sfd.getDailyEndDate());
		}
		endDay.set(endDay.get(1), endDay.get(2), endDay.get(5), 0, 0, 0);
		endDay.set(14, 0);

		Calendar startDay = (Calendar) endDay.clone();
		startDay.add(5, getStartDayAdjustment());
		startDay.set(startDay.get(1), startDay.get(2), startDay.get(5), 0, 0, 0);
		startDay.set(14, 0);
		setSchedStartDay(startDay);
		setSchedFileEndDay(sfd.getSchFileEndDate());

		loadGeneratedEnergyInfo(table, startDay, endDay, TOTAL_COL, sfd, DATE_COL, HIGHLIGHT_COL);

		Calendar waterSchedEndDay = null;
		if (!reschedule) {
			waterSchedEndDay = Util.getCalendarYYMMDD(sfd.getEnergyEndDate());
		} else {
			waterSchedEndDay = sfd.getRescheduleDataEndDate();
		}
		if (printRecordCopy) {
			waterSchedEndDay = Util.getCalendarYYMMDD(sfd.getDailyEndDate());
		}
		Calendar waterSchedStartDay = (Calendar) waterSchedEndDay.clone();
		waterSchedStartDay.add(5, getStartDayAdjustment());

		Calendar ftraCfsDay = Util.getCalendarYYMMDD(sfd.getEnergyEndDate());
		if (printRecordCopy) {
			ftraCfsDay = Util.getCalendarYYMMDD(sfd.getSchFileStartDate());
		} else {
			ftraCfsDay.add(5, getStartDayAdjustment());
		}
		loadWaterScheduleInfo(table, waterSchedStartDay, waterSchedEndDay, TOTAL_COL, sfd, DATE_COL, ftraCfsDay,
				HIGHLIGHT_COL);

		Calendar poolElevStartDay = (Calendar) waterSchedEndDay.clone();
		Calendar poolElevEndDay = Util.getCalendarYYMMDD(sfd.getDailyEndDate());
		poolElevStartDay.add(5, getStartDayAdjustment());

		loadPoolElevationInfo(table, poolElevStartDay, poolElevEndDay, TOTAL_COL, sfd, DATE_COL, HIGHLIGHT_COL);
	}

	/**
	 * 
	 * @param table
	 * @param startDay
	 * @param endDay
	 * @param TOTAL_COL
	 * @param sfd
	 * @param DATE_COL
	 * @param HIGHLIGHT_COL
	 */
	private void loadGeneratedEnergyInfo(JTable table, Calendar startDay, Calendar endDay, int TOTAL_COL,
			ScheduleFileData sfd, int DATE_COL, int HIGHLIGHT_COL) {
		int HEADER_ROW = 0;
		int SITES_ROW = 1;
		int OA_BB_RATIO_COL = 10;

		table.setGridColor(Color.LIGHT_GRAY);
		table.setShowGrid(true);
		table.setShowVerticalLines(false);

		table.setValueAt("Energy", HEADER_ROW, getColumnTwo());
		table.setValueAt("Schedule", HEADER_ROW, getColumnThree());
		table.setValueAt("(MW     -", HEADER_ROW, getColumnFour());
		table.setValueAt(" MWH)", HEADER_ROW, getColumnFive());
		table.setValueAt(getDateToday(), HEADER_ROW, getColumnNine());

		table.setValueAt("true", HEADER_ROW, HIGHLIGHT_COL);

		int count = getDamSites(table, SITES_ROW);

		table.setValueAt("true", SITES_ROW, HIGHLIGHT_COL);

		table.setValueAt("TOTAL", SITES_ROW, count + 1);
		table.setValueAt("DIFF", SITES_ROW, count + 2);
		if ((table.equals(scheduleTable)) || (table.equals(printScheduleTable))) {
			table.setValueAt("OA/BB", SITES_ROW, count + 3);
		}
		table.setValueAt("Sat Cap", getRowTwo(), getColumnZero());
		table.setValueAt("Mon Cap", getRowThree(), getColumnZero());
		table.setValueAt("+ Tol", getRowFour(), getColumnZero());
		table.setValueAt("- Tol", getRowFive(), getColumnZero());
		table.setValueAt("false", getRowSix(), HIGHLIGHT_COL);

		table = populateTable(table, 2, 1, sfd.getSatCapTol(), true);
		table.setValueAt(getRowTotals(), getRowTwo(), TOTAL_COL);
		table.setValueAt("false", getRowTwo(), HIGHLIGHT_COL);
		table = populateTable(table, getRowThree(), 1, sfd.getMonCapTol(), true);
		table.setValueAt(getRowTotals(), getRowThree(), TOTAL_COL);
		table.setValueAt("false", getRowThree(), HIGHLIGHT_COL);
		table = populateTable(table, 4, 1, sfd.getPlusCapTol(), true);
		table.setValueAt(getRowTotals(), getRowFour(), TOTAL_COL);
		table.setValueAt("false", getRowFour(), HIGHLIGHT_COL);
		table = populateTable(table, 5, 1, sfd.getMinusCapTol(), true);
		table.setValueAt(getRowTotals(), getRowFive(), TOTAL_COL);
		table.setValueAt("false", getRowFive(), HIGHLIGHT_COL);

		HashMap<String, DlyData> dly = sfd.getDlyDataMap();

		String startKey = Util.getDataKey(startDay);

		int startGErow = 8;
		int startDlyGErow = 9;

		table.setValueAt("false", startGErow - 1, HIGHLIGHT_COL);

		Double genPowerRecordCopyTotal = Double.valueOf(0.0D);
		Double dlyPowerRecordCopyTotal = Double.valueOf(0.0D);
		while ((endDay.after(startDay)) || (endDay.equals(startDay))) {
			int record = 0;

			int dayWeek = startDay.get(7);
			int dayMonth = startDay.get(5);
			Double genPowerTotal = Double.valueOf(0.0D);
			Double dlyPowerTotal = Double.valueOf(0.0D);

			Double genPowerOahe = Double.valueOf(0.0D);
			Double dlyPowerOahe = Double.valueOf(0.0D);
			if (dly.containsKey(startKey + "_0")) {
				table.setValueAt(dayMonth + " " + Util.getDayOfWeek(dayWeek), startGErow, record);
			}
			while (record < 6) {
				DlyData data = null;
				String key = startKey + "_" + record;
				if (dly.containsKey(key)) {
					data = (DlyData) dly.get(key);
					if (data.getRescheduleEnergyNum().doubleValue() > 0.0D) {
						table.setValueAt(Util.getFormatNoDecimal().format(data.getRescheduleEnergyNum()),
								startGErow - 1, record + 1);
					}
					table.setValueAt(Util.getFormatNoDecimal().format(data.getActGenPowerNum()), startGErow,
							record + 1);

					Double reschedEnergyNum = Double.valueOf(0.0D);
					if (data.getRescheduleEnergyNum().doubleValue() > 0.0D) {
						genPowerTotal = Double
								.valueOf(genPowerTotal.doubleValue() + data.getRescheduleEnergyNum().doubleValue());
						reschedEnergyNum = data.getRescheduleEnergyNum();
					} else {
						genPowerTotal = Double
								.valueOf(genPowerTotal.doubleValue() + data.getActGenPowerNum().doubleValue());
					}
					dlyPowerTotal = Double
							.valueOf(dlyPowerTotal.doubleValue() + data.getDlyGenPowerNum().doubleValue());
					if ((data.getDlyGenPowerNum().doubleValue() >= 0.0D)
							&& ((getDailyEndDay().equals(startDay)) || (getDailyEndDay().after(startDay)))) {
						table.setValueAt(CheckTolerances(data.getDlyGenPowerNum(), startDlyGErow, record + 1,
								reschedEnergyNum, data.getActGenPowerNum()), startDlyGErow, record + 1);
					}
					if (record == 2) {
						if (data.getRescheduleEnergyNum().doubleValue() == 0.0D) {
							genPowerOahe = Double.valueOf(
									data.getActGenPowerNum().doubleValue() - data.getDlyGenPowerNum().doubleValue());
						} else {
							genPowerOahe = Double.valueOf(data.getRescheduleEnergyNum().doubleValue()
									- data.getDlyGenPowerNum().doubleValue());
						}
					}
					if (record == 3) {
						if (data.getRescheduleEnergyNum().doubleValue() == 0.0D) {
							dlyPowerOahe = Double.valueOf(
									data.getActGenPowerNum().doubleValue() - data.getDlyGenPowerNum().doubleValue());
						} else {
							dlyPowerOahe = Double.valueOf(data.getRescheduleEnergyNum().doubleValue()
									- data.getDlyGenPowerNum().doubleValue());
						}
					}
					data.getFlowTotalNum();
				}
				record++;
			}
			genPowerRecordCopyTotal = Double
					.valueOf(genPowerRecordCopyTotal.doubleValue() + genPowerTotal.doubleValue());
			dlyPowerRecordCopyTotal = Double
					.valueOf(dlyPowerRecordCopyTotal.doubleValue() + dlyPowerTotal.doubleValue());
			if (genPowerTotal.doubleValue() > 0.0D) {
				table.setValueAt(Util.getFormatNoDecimal().format(genPowerTotal), startGErow, TOTAL_COL);
				table.setValueAt("false", startGErow, HIGHLIGHT_COL);
			}
			if (dlyPowerTotal.doubleValue() > 0.0D) {
				table.setValueAt(Util.getFormatNoDecimal().format(dlyPowerTotal), startDlyGErow, TOTAL_COL);
			}
			if (dlyPowerTotal.doubleValue() > 0.0D) {
				table.setValueAt(
						Util.getFormatNoDecimal().format(dlyPowerTotal.doubleValue() - genPowerTotal.doubleValue()),
						startDlyGErow, DATE_COL);
			}
			if (dlyPowerTotal.doubleValue() > 0.0D) {
				if ((table.equals(scheduleTable)) || (table.equals(printScheduleTable))) {
					if (genPowerOahe.doubleValue() / dlyPowerOahe.doubleValue() > 0.0D) {
						table.setValueAt(
								Util.getFormatOneDecimal()
										.format(genPowerOahe.doubleValue() / dlyPowerOahe.doubleValue()) + "/1",
								startDlyGErow, OA_BB_RATIO_COL);
					} else {
						table.setValueAt("Bad", startDlyGErow, OA_BB_RATIO_COL);
					}
				}
				table.setValueAt("true", startDlyGErow, HIGHLIGHT_COL);
			} else {
				table.setValueAt("false", startDlyGErow, HIGHLIGHT_COL);
			}
			table.setValueAt("false", startGErow - 1, HIGHLIGHT_COL);
			startGErow += 3;
			startDlyGErow += 3;
			startDay.add(5, 1);
			startKey = Util.getDataKey(startDay);
		}
	}

	/**
	 * Checks to see it energy values are within the tolerances
	 * 
	 * @param dlyGenPower
	 *            - the generated energy
	 * @param row
	 *            - the row in the table
	 * @param col
	 *            - the column in the table
	 * @param reschedEnergyNum
	 *            - the reschedule energy value
	 * @return String
	 */
	private String CheckTolerances(Double dlyGenPower, int row, int col, Double reschedEnergyNum, Double energyNum) {
		String data = null;
		Double valplus;
		Double valminus;
		// FTPK
		if (col == 1) {
			// Use Rescheduled value if it exists
			if (reschedEnergyNum > 0) {
				valplus = dlyGenPower - reschedEnergyNum;
				valminus = reschedEnergyNum - dlyGenPower;
			} else {
				valplus = dlyGenPower - energyNum;
				valminus = energyNum - dlyGenPower;
			}

			if (valplus > getFtpkPlusTolerance()) {
				data = Util.getFormatNoDecimal().format(dlyGenPower) + "+";
			} else if (valminus > getFtpkMinusTolerance()) {
				data = Util.getFormatNoDecimal().format(dlyGenPower) + "-";
			} else {
				data = Util.getFormatNoDecimal().format(dlyGenPower);
			}
			// GARR
		} else if (col == 2) {
			// Use Rescheduled value if it exists
			if (reschedEnergyNum > 0) {
				valplus = dlyGenPower - reschedEnergyNum;
				valminus = reschedEnergyNum - dlyGenPower;
			} else {
				valplus = dlyGenPower - energyNum;
				valminus = energyNum - dlyGenPower;
			}

			if (valplus > getGarrPlusTolerance()) {
				data = Util.getFormatNoDecimal().format(dlyGenPower) + "+";
			} else if (valminus > getGarrMinusTolerance()) {
				data = Util.getFormatNoDecimal().format(dlyGenPower) + "-";
			} else {
				data = Util.getFormatNoDecimal().format(dlyGenPower);
			}
			// FTRA
		} else if (col == 5) {
			// Use Rescheduled value if it exists
			if (reschedEnergyNum > 0) {
				valplus = dlyGenPower - reschedEnergyNum;
				valminus = reschedEnergyNum - dlyGenPower;
			} else {
				valplus = dlyGenPower - energyNum;
				valminus = energyNum - dlyGenPower;
			}

			if (valplus > getFtraPlusTolerance()) {
				data = Util.getFormatNoDecimal().format(dlyGenPower) + "+";
			} else if (valminus > getFtraMinusTolerance()) {
				data = Util.getFormatNoDecimal().format(dlyGenPower) + "-";
			} else {
				data = Util.getFormatNoDecimal().format(dlyGenPower);
			}
		} else {
			data = Util.getFormatNoDecimal().format(dlyGenPower);
		}

		return data;
	}

	// private String CheckTolerances(Double dlyGenPower, int row, int col,
	// Double reschedEnergyNum, Double energyNum)
	// {
	// String data = null;
	// if (col == 1)
	// {
	// Double valminus;
	// Double valplus;
	// Double valminus;
	// if (reschedEnergyNum.doubleValue() > 0.0D)
	// {
	// Double valplus = Double.valueOf(dlyGenPower.doubleValue() -
	// reschedEnergyNum.doubleValue());
	// valminus = Double.valueOf(reschedEnergyNum.doubleValue() -
	// dlyGenPower.doubleValue());
	// }
	// else
	// {
	// valplus = Double.valueOf(dlyGenPower.doubleValue() -
	// energyNum.doubleValue());
	// valminus = Double.valueOf(energyNum.doubleValue() -
	// dlyGenPower.doubleValue());
	// }
	// if (valplus.doubleValue() > getFtpkPlusTolerance().doubleValue()) {
	// data = Util.getFormatNoDecimal().format(dlyGenPower) + "+";
	// } else if (valminus.doubleValue() >
	// getFtpkMinusTolerance().doubleValue()) {
	// data = Util.getFormatNoDecimal().format(dlyGenPower) + "-";
	// } else {
	// data = Util.getFormatNoDecimal().format(dlyGenPower);
	// }
	// }
	// else if (col == 2)
	// {
	// Double valminus;
	// Double valplus;
	// Double valminus;
	// if (reschedEnergyNum.doubleValue() > 0.0D)
	// {
	// Double valplus = Double.valueOf(dlyGenPower.doubleValue() -
	// reschedEnergyNum.doubleValue());
	// valminus = Double.valueOf(reschedEnergyNum.doubleValue() -
	// dlyGenPower.doubleValue());
	// }
	// else
	// {
	// valplus = Double.valueOf(dlyGenPower.doubleValue() -
	// energyNum.doubleValue());
	// valminus = Double.valueOf(energyNum.doubleValue() -
	// dlyGenPower.doubleValue());
	// }
	// if (valplus.doubleValue() > getGarrPlusTolerance().doubleValue()) {
	// data = Util.getFormatNoDecimal().format(dlyGenPower) + "+";
	// } else if (valminus.doubleValue() >
	// getGarrMinusTolerance().doubleValue()) {
	// data = Util.getFormatNoDecimal().format(dlyGenPower) + "-";
	// } else {
	// data = Util.getFormatNoDecimal().format(dlyGenPower);
	// }
	// }
	// else if (col == 5)
	// {
	// Double valminus;
	// Double valplus;
	// Double valminus;
	// if (reschedEnergyNum.doubleValue() > 0.0D)
	// {
	// Double valplus = Double.valueOf(dlyGenPower.doubleValue() -
	// reschedEnergyNum.doubleValue());
	// valminus = Double.valueOf(reschedEnergyNum.doubleValue() -
	// dlyGenPower.doubleValue());
	// }
	// else
	// {
	// valplus = Double.valueOf(dlyGenPower.doubleValue() -
	// energyNum.doubleValue());
	// valminus = Double.valueOf(energyNum.doubleValue() -
	// dlyGenPower.doubleValue());
	// }
	// if (valplus.doubleValue() > getFtraPlusTolerance().doubleValue()) {
	// data = Util.getFormatNoDecimal().format(dlyGenPower) + "+";
	// } else if (valminus.doubleValue() >
	// getFtraMinusTolerance().doubleValue()) {
	// data = Util.getFormatNoDecimal().format(dlyGenPower) + "-";
	// } else {
	// data = Util.getFormatNoDecimal().format(dlyGenPower);
	// }
	// }
	// else
	// {
	// data = Util.getFormatNoDecimal().format(dlyGenPower);
	// }
	// return data;
	// }

	/**
	 * 
	 * @param table
	 * @param startDay
	 * @param endDay
	 * @param TOTAL_COL
	 * @param sfd
	 * @param DATE_COL
	 * @param ftraDay
	 * @param HIGHLIGHT_COL
	 */
	private void loadWaterScheduleInfo(JTable table, Calendar startDay, Calendar endDay, int TOTAL_COL,
			ScheduleFileData sfd, int DATE_COL, Calendar ftraDay, int HIGHLIGHT_COL) {
		int ROW_WATER_SCHEDULE = 29;
		int SITES_WATER_SCHEDULE = 30;
		int START_WATER_ROW = 32;

		// TODO get SPC from the DB
		HashMap<String, String> spcMap = sfd.getSpcMap();// 160417=0.00,
															// 160416=0.00,
															// 160415=0.00,
															// 160414=0.00,
		Calendar start = Calendar.getInstance();
		start.setTimeZone(TimeZone.getTimeZone("UTC"));

		Calendar end = (Calendar) start.clone();
		end.setTimeZone(TimeZone.getTimeZone("UTC"));
		end.add(Calendar.DAY_OF_MONTH, -13);// go back 13 days

		HashMap<String, String> id24Map = sfd.getId24Map();

		table.setValueAt("false", ROW_WATER_SCHEDULE - 1, HIGHLIGHT_COL);

		table.setValueAt("Water", ROW_WATER_SCHEDULE, getColumnTwo());
		table.setValueAt("Schedule", ROW_WATER_SCHEDULE, getColumnThree());
		table.setValueAt("-  KCFS", ROW_WATER_SCHEDULE, getColumnFour());
		table.setValueAt("true", ROW_WATER_SCHEDULE, HIGHLIGHT_COL);

		int count = getDamSites(table, SITES_WATER_SCHEDULE);
		table.setValueAt("true", SITES_WATER_SCHEDULE, HIGHLIGHT_COL);

		table.setValueAt("SPC", SITES_WATER_SCHEDULE, count + 1);
		table.setValueAt("GP-QL", SITES_WATER_SCHEDULE, count + 2);
		table.setValueAt("24ID", SITES_WATER_SCHEDULE, count + 3);

		HashMap<String, DlyData> dly = sfd.getDlyDataMap();
		while ((endDay.after(startDay)) || (endDay.equals(startDay))) {
			int record = 0;

			String startKey = Util.getDataKey(startDay);

			int dayWeek = startDay.get(7);
			int dayMonth = startDay.get(5);
			if (dly.containsKey(startKey + "_0")) {
				table.setValueAt(dayMonth + " " + Util.getDayOfWeek(dayWeek), START_WATER_ROW, record);
			}
			while (record < 6) {
				DlyData data = null;
				String key = startKey + "_" + record;
				if (dly.containsKey(key)) {
					data = (DlyData) dly.get(key);
					if (data.getRescheduleOpNum().doubleValue() > 0.0D) {
						if (data.getRescheduleOpNum().doubleValue() > data.getODNum().doubleValue()) {
							table.setValueAt(Util.getFormatOneDecimal().format(data.getRescheduleOpNum()),
									START_WATER_ROW - 1, record + 1);
						} else {
							table.setValueAt(Util.getFormatOneDecimal().format(data.getODNum()) + "*",
									START_WATER_ROW - 1, record + 1);
						}
						table.setValueAt("false", START_WATER_ROW - 1, HIGHLIGHT_COL);
					}
					if (data.getODNum().doubleValue() > data.getKcfsNum().doubleValue()) {
						if (data.getKcfsNum().doubleValue() >= 0.0D) {
							table.setValueAt(Util.getFormatOneDecimal().format(data.getODNum()) + "*", START_WATER_ROW,
									record + 1);
						} else if (data.getODNum().doubleValue() != 0.0D) {
							table.setValueAt(Util.getFormatOneDecimal().format(data.getODNum()), START_WATER_ROW,
									record + 1);
						} else {
							table.setValueAt(Util.getFormatNoDecimal().format(data.getODNum()), START_WATER_ROW,
									record + 1);
						}
						table.setValueAt("false", START_WATER_ROW, HIGHLIGHT_COL);
					} else {// here
						table.setValueAt(Util.getFormatOneDecimal().format(data.getKcfsNum()), START_WATER_ROW,
								record + 1);
						table.setValueAt("false", START_WATER_ROW, HIGHLIGHT_COL);
					}
					if (data.getFlowPowerNum().doubleValue() >= 0.0D) {
						if (data.getFlowTotalNum().doubleValue() > data.getFlowPowerNum().doubleValue()) {
							if (data.getFlowTotalNum().doubleValue() > 0.0D) {
								table.setValueAt(Util.getFormatOneDecimal().format(data.getFlowTotalNum()) + "*",
										START_WATER_ROW + 1, record + 1);
							}
						} else if (data.getFlowPowerNum().doubleValue() >= 0.0D) {// here
							Date ds = data.getCalendarDate().getTime();
							Date l = Util.getLastValueDate().getTime();
							if (data.getCalendarDate().after(Util.getLastValueDate())) {
								table.setValueAt("", START_WATER_ROW + 1, record + 1);
							} else {
								table.setValueAt(Util.getFormatOneDecimal().format(data.getFlowPowerNum()),
										START_WATER_ROW + 1, record + 1);
							}

						}
						table.setValueAt("true", START_WATER_ROW + 1, HIGHLIGHT_COL);
						table.setValueAt("false", START_WATER_ROW + 2, HIGHLIGHT_COL);
					} else {
						table.setValueAt("false", START_WATER_ROW + 1, HIGHLIGHT_COL);
						table.setValueAt("false", START_WATER_ROW + 2, HIGHLIGHT_COL);
					}
					// TODO SPC
					if (!spcMap.get(startKey).equals("null")) {
						if (spcMap.containsKey(startKey) && (Double.parseDouble((String) spcMap.get(startKey)) > 0.0)) {
							table.setValueAt(Util.getFormatTwoDecimal().format(Double.parseDouble((String) spcMap.get(startKey))),
									START_WATER_ROW, TOTAL_COL);
						}
					}
					
					//FTRA percentages
					double val = Util.getFTRAOneDayAgoPercentage().doubleValue() * getFtraCfsHist(ftraDay, -1, dly).doubleValue()
							+ Util.getFTRATwoDayAgpPercentage().doubleValue() * getFtraCfsHist(ftraDay, -2, dly).doubleValue();
					
					if (id24Map.containsKey(startKey)) {						
						if (Double.parseDouble((String) id24Map.get(startKey)) > 0.0D) {							
							double id24 = Double.parseDouble((String) id24Map.get(startKey)) - val;
							if ((getFtraCfsHist(ftraDay, -2, dly).doubleValue() >= 0.0D) && (getFtraCfsHist(ftraDay, -1, dly).doubleValue() >= 0.0D)) {
								table.setValueAt(Util.getFormatOneDecimal().format(id24), START_WATER_ROW, DATE_COL);
							} else {
								table.setValueAt("  ", START_WATER_ROW, DATE_COL);
							}
						}
						//24ID
						if (Double.parseDouble((String) id24Map.get(startKey)) > 0.0D) {
							table.setValueAt(Util.getFormatOneDecimal().format(Double.parseDouble((String) id24Map.get(startKey))),
									START_WATER_ROW, DATE_COL + 1);
						}
					}
				}
				record++;
			}
			START_WATER_ROW += 3;
			startDay.add(5, 1);
			ftraDay.add(5, 1);
			startKey = Util.getDataKey(startDay);
			record++;
		}
	}

	/**
	 * 
	 * @param table
	 * @param startDay
	 * @param endDay
	 * @param TOTAL_COL
	 * @param sfd
	 * @param DATE_COL
	 * @param HIGHLIGHT_COL
	 */
	private void loadPoolElevationInfo(JTable table, Calendar startDay, Calendar endDay, int TOTAL_COL,
			ScheduleFileData sfd, int DATE_COL, int HIGHLIGHT_COL) {
		int ROW_POOL_ELEVATION = 53;
		int SITES_POOL_ELEVATION = 54;
		int START_POOL_ELEVATION = 55;

		table.setValueAt("false", ROW_POOL_ELEVATION - 1, HIGHLIGHT_COL);

		table.setValueAt("MIDNIGHT", ROW_POOL_ELEVATION, 2);
		table.setValueAt("  POOL", ROW_POOL_ELEVATION, 3);
		table.setValueAt("ELEV.", ROW_POOL_ELEVATION, 4);
		table.setValueAt("true", ROW_POOL_ELEVATION, HIGHLIGHT_COL);

		String theStartDay = Util.getCalendarYYMMDD(startDay);
		if ((table.equals(printScheduleTable)) || (table.equals(scheduleTable))) {
			table.setValueAt(getDateToday(), ROW_POOL_ELEVATION, 6);
		}
		getDamSites(table, SITES_POOL_ELEVATION);

		table.setValueAt("true", SITES_POOL_ELEVATION, HIGHLIGHT_COL);

		HashMap<String, DlyData> dly = sfd.getDlyDataMap();

		HashMap<String, String[]> keyInfo = new HashMap<String, String[]>();

		int elevInfoCount = 0;
		while ((endDay.after(startDay)) || (startDay.get(5) == endDay.get(5))) {
			int record = 0;
			String startKey = Util.getDataKey(startDay);

			int dayWeek = startDay.get(7);
			int dayMonth = startDay.get(5);
			if (dly.containsKey(startKey + "_0")) {
				table.setValueAt(dayMonth + " " + Util.getDayOfWeek(dayWeek), START_POOL_ELEVATION, record);
			}
			String[] elevData = new String[6];
			while (record < 6) {
				DlyData data = null;
				String key = startKey + "_" + record;
				if (dly.containsKey(key)) {
					elevData[record] = key;
					data = (DlyData) dly.get(key);
					if (data.getPoolElevationNum().doubleValue() > 0.0D) {
						table.setValueAt(Util.getFormatOneDecimal().format(data.getPoolElevationNum()),
								START_POOL_ELEVATION, record + 1);

						table.setValueAt("false", START_POOL_ELEVATION, HIGHLIGHT_COL);
					}
				}
				record++;
			}
			keyInfo.put(String.valueOf(elevInfoCount), elevData);

			startDay.add(5, 1);
			START_POOL_ELEVATION++;
			elevInfoCount++;
		}
		int record = 0;
		table.setValueAt("24Hr Chng", START_POOL_ELEVATION, record);

		int elevSize = keyInfo.size();
		if (elevSize == 1) {
			Calendar redoDay = Util.getCalendarYYMMDD(theStartDay);
			redoDay.add(5, -1);

			elevInfoCount = elevSize;
			while (endDay.after(redoDay)) {
				int rec = 0;
				String startKey = Util.getDataKey(redoDay);

				String[] elevData = new String[6];
				while (rec < 6) {
					DlyData data = null;
					String key = startKey + "_" + rec;
					if (dly.containsKey(key)) {
						elevData[rec] = key;
						data = (DlyData) dly.get(key);
					}
					rec++;
				}
				keyInfo.put(String.valueOf(elevInfoCount), elevData);
				redoDay.add(5, 1);
				elevInfoCount++;
			}
			elevSize = keyInfo.size();
		}
		String[] elevationKey2 = (String[]) null;
		if ((table.equals(printScheduleTable)) || (table.equals(scheduleTable))) {
			if (elevSize > 0) {
				int akey = elevSize - 1;
				String[] elevationKey = (String[]) keyInfo.get(String.valueOf(akey));
				if (elevSize > 1) {
					int akey2 = elevSize - 2;
					elevationKey2 = (String[]) keyInfo.get(String.valueOf(akey2));
				}
				while (record < 6) {
					DlyData elev = (DlyData) dly.get(elevationKey[record]);
					DlyData elev2 = (DlyData) dly.get(elevationKey2[record]);

					double change = elev.getPoolElevationNum().doubleValue()
							- elev2.getPoolElevationNum().doubleValue();
					if (change != 0.0D) {
						table.setValueAt(Util.getFormatTwoDecimal().format(change), START_POOL_ELEVATION, record + 1);
					} else {
						table.setValueAt(Util.getFormatNoDecimal().format(change), START_POOL_ELEVATION, record + 1);
					}
					record++;
				}
			}
			table.setValueAt("true", START_POOL_ELEVATION, HIGHLIGHT_COL);
		}
		table.setValueAt("OUTAGES", START_POOL_ELEVATION + 1, 0);
		table.setValueAt("false", START_POOL_ELEVATION + 1, HIGHLIGHT_COL);
		setMaxRowNum(START_POOL_ELEVATION + 1);

		// HashMap<String, String[]> outageMap = sfd.getOutageMap();
		// TODO outages
		LinkedHashMap<String, LinkedHashMap<String, Vector<OutagesTable>>> oMap = getOutagesMap();

		if (null != oMap) {
			Calendar today = Calendar.getInstance();
			String outageKey = Util.getDataKey(today);
			// Set<String> s = oMap.keySet();
			// if (outageMap.containsKey(outageKey)) { //old
			if (oMap.containsKey(outageKey)) {
				// String[] outageData = (String[]) outageMap.get(outageKey);
				// //old
				String[] outageData = formatOutagesDayMap(oMap, outageKey);
				// LinkedHashMap<String, Vector<OutagesTable>> data =
				// oMap.get(outageKey);

				for (int column = 0; column < outageData.length; column++) {
					String outageNum = outageData[column];
					if (outageNum.length() > 0) {
//						Double siteOutage = Double.valueOf(Double.parseDouble(outageNum));
//						String str = Util.getFormatNoDecimal().format(siteOutage).toString();
//						String str = outageNum;
						int index = 0;
						StringBuffer buff = new StringBuffer();
						if(outageNum.length()>1){
							for(int x = 0; x<outageNum.length(); x++){
								String outage1 = outageNum.substring(index, index+1);
								if(outageNum.length()-index>1){
									//Changed back to a dash
									buff.append(outage1+"-");//If changed to a "," then it messes up the Create Brief
									index++;
								} else {
									buff.append(outage1);
								}
							}
						} else {
							buff.append(outageNum);
						}
						
//						if (Util.getFormatNoDecimal().format(siteOutage).toString().length() > 4) {
//							table.setValueAt(siteOutage.toString(), START_POOL_ELEVATION + 1, column + 1);
//						} 
//						else if (siteOutage.doubleValue() > 10000.0D) {
////							String str = Util.getFormatNoDecimal().format(siteOutage).toString();
//							String outage1 = str.substring(0, 1);
//							String outage2 = str.substring(1, 2);
//							String outage3 = str.substring(2, 3);
//							String outage4 = str.substring(3, 4);
//							String outage5 = str.substring(4, 5);
//							table.setValueAt(outage1 + "-" + outage2 + "-" + outage3 + "-" + outage4 + "-" + outage5, START_POOL_ELEVATION + 1, column + 1);
//						} else if (siteOutage.doubleValue() > 1000.0D) {
////							String str = Util.getFormatNoDecimal().format(siteOutage).toString();
//							String outage1 = str.substring(0, 1);
//							String outage2 = str.substring(1, 2);
//							String outage3 = str.substring(2, 3);
//							String outage4 = str.substring(3, 4);
//							table.setValueAt(outage1 + "-" + outage2 + "-" + outage3 + "-" + outage4, START_POOL_ELEVATION + 1, column + 1);
//						} else if (siteOutage.doubleValue() > 100.0D) {
////							String str = Util.getFormatNoDecimal().format(siteOutage).toString();
//							String outage1 = str.substring(0, 1);
//							String outage2 = str.substring(1, 2);
//							String outage3 = str.substring(2, 3);
//							table.setValueAt(outage1 + "-" + outage2 + "-" + outage3, START_POOL_ELEVATION + 1,	column + 1);
//						} else if (siteOutage.doubleValue() > 10.0D) {
////							String str = Util.getFormatNoDecimal().format(siteOutage).toString();
//							String outage1 = str.substring(0, 1);
//							String outage2 = str.substring(1, 2);
//							table.setValueAt(outage1 + "-" + outage2, START_POOL_ELEVATION + 1, column + 1);
//						} 
//						else if (siteOutage.doubleValue() > 0.0D) {
//							table.setValueAt(Util.getFormatNoDecimal().format(siteOutage), START_POOL_ELEVATION + 1, column + 1);
//						}
						if (outageNum.length() > 0) {
							table.setValueAt(buff.toString(), START_POOL_ELEVATION + 1, column + 1);
						}
						else {
							table.setValueAt("     ", START_POOL_ELEVATION + 1, column + 1);
						}
					}
				}
				table.setValueAt("false", START_POOL_ELEVATION + 1, HIGHLIGHT_COL);
			}
		} // if
	}

	/**
	 * 
	 * @param tbl
	 * @param outageKey
	 * @return
	 */
	protected static String[] formatOutagesDayMap(
			LinkedHashMap<String, LinkedHashMap<String, Vector<OutagesTable>>> tbl, String outageKey) {
		String[] sites = { "", "", "", "", "", "" };
		LinkedHashMap<String, Vector<OutagesTable>> locations = tbl.get(outageKey);
		Set<String> set = locations.keySet();
		Iterator<String> it = set.iterator();
		while (it.hasNext()) {
			String location = it.next();
			Vector<OutagesTable> vec = locations.get(location);
			for (int x = 0; x < vec.size(); x++) {
				OutagesTable site = vec.get(x);
				String loc = site.getLocId();
				String unit = site.getUnit();
				// System.out.println(loc + " " + unit);

				if (loc.equals("FTPK")) {
					sites[0] = sites[0].concat(unit);
				} else if (loc.equals("GARR")) {
					sites[1] = sites[1].concat(unit);
				} else if (loc.equals("OAHE")) {
					sites[2] = sites[2].concat(unit);
				} else if (loc.equals("BEND")) {
					sites[3] = sites[3].concat(unit);
				} else if (loc.equals("FTRA")) {
					sites[4] = sites[4].concat(unit);
				} else if (loc.equals("GAPT")) {
					sites[5] = sites[5].concat(unit);
				}
			}
		}
		return sites;
	}

	private Double getFtraCfsHist(Calendar theDay, int num, HashMap<String, DlyData> dly) {
		DlyData data = null;
		Double val = Double.valueOf(0.0D);

		theDay.add(5, num);

		String startKey = Util.getDataKey(theDay);

		String key = startKey + "_4";
		if (dly.containsKey(key)) {
			data = (DlyData) dly.get(key);
			if (data.getFlowTotalNum().doubleValue() > data.getFlowPowerNum().doubleValue()) {
				val = data.getFlowTotalNum();
			} else {
				val = data.getFlowPowerNum();
			}
		}
		theDay.add(5, num * -1);
		return val;
	}

	private int getDamSites(JTable table, int row) {
		Iterator<String> nameIt = getDlystatSites().iterator();
		int count = 1;
		while (nameIt.hasNext()) {
			String dam = (String) nameIt.next();
			table.setValueAt(dam, row, count);
			count++;
		}
		return count;
	}

	private String getDateToday() {
		Calendar now = Calendar.getInstance();
		String today = now.get(2) + 1 + "/" + now.get(5) + "/" + now.get(1);
		return today;
	}

	private JTable populateTable(JTable table, int row, int col, String[] data, boolean rowTotal) {
		double total = 0.0D;
		boolean valid = true;
		for (int x = 0; x < data.length; x++) {
			if (data[x] != "99999") {
				table.setValueAt(data[x], row, col);
				if ((row == 4) && (col == 1)) {
					setFtpkPlusTolerance(data[x]);
				} else if ((row == 4) && (col == 2)) {
					setGarrPlusTolerance(data[x]);
				} else if ((row == 4) && (col == 5)) {
					setFtraPlusTolerance(data[x]);
				} else if ((row == 5) && (col == 1)) {
					setFtpkMinusTolerance(data[x]);
				} else if ((row == 5) && (col == 2)) {
					setGarrMinusTolerance(data[x]);
				} else if ((row == 5) && (col == 5)) {
					setFtraMinusTolerance(data[x]);
				}
				try {
					total += Double.parseDouble(data[x]);
				} catch (Exception e) {
					valid = false;
					setRowTotals(Double.valueOf(0.0D));
				}
				col++;
			}
		}
		if (valid) {
			setRowTotals(Double.valueOf(total));
		}
		return table;
	}

	private void setScheduleTable(JTable schedTable) {
		scheduleTable = schedTable;
	}

	private static JTable getScheduleTable() {
		return scheduleTable;
	}

	private static JTable getSchedulePrintTable() {
		return printScheduleTable;
	}

	private static void setSchedulePrintTable(JTable tbl) {
		printScheduleTable = tbl;
	}

	private static JTable getPrintRecordCopyTable() {
		return printRecordCopyTable;
	}

	private static void setPrintRecordCopyTable(JTable tbl) {
		printRecordCopyTable = tbl;
	}

	private void setRowTotals(Double rowTotal) {
		rowTotals = rowTotal;
	}

	private String getRowTotals() {
		String val = "";
		if (rowTotals.doubleValue() != 0.0D) {
			val = Util.getFormatNoDecimal().format(rowTotals);
		}
		return val;
	}

	private void setFtpkPlusTolerance(String ftpkplstolerance) {
		try {
			ftpkPlusTolerance = Double.valueOf(Double.parseDouble(ftpkplstolerance));
		} catch (Exception e) {
			System.err.println("Error parsing ftpkPlusTolerance Number: " + e);
		}
	}

	private Double getFtpkPlusTolerance() {
		return ftpkPlusTolerance;
	}

	private void setGarrPlusTolerance(String garrPlsTolerance) {
		try {
			garrPlusTolerance = Double.valueOf(Double.parseDouble(garrPlsTolerance));
		} catch (Exception e) {
			System.err.println("Error parsing garrPlusTolerance Number: " + e);
		}
	}

	private Double getGarrPlusTolerance() {
		return garrPlusTolerance;
	}

	private void setFtraPlusTolerance(String ftraPlsTolerance) {
		try {
			ftraPlusTolerance = Double.valueOf(Double.parseDouble(ftraPlsTolerance));
		} catch (Exception e) {
			System.err.println("Error parsing ftraPlusTolerance Number: " + e);
		}
	}

	private Double getFtraPlusTolerance() {
		return ftraPlusTolerance;
	}

	private void setFtpkMinusTolerance(String ftpkMinsTolerance) {
		try {
			ftpkMinusTolerance = Double.valueOf(Double.parseDouble(ftpkMinsTolerance));
		} catch (Exception e) {
			System.err.println("Error parsing ftpkMinusTolerance Number: " + e);
		}
	}

	private Double getFtpkMinusTolerance() {
		return ftpkMinusTolerance;
	}

	private void setGarrMinusTolerance(String garrMinsTolerance) {
		try {
			garrMinusTolerance = Double.valueOf(Double.parseDouble(garrMinsTolerance));
		} catch (Exception e) {
			System.err.println("Error parsing garrMinusTolerance Number: " + e);
		}
	}

	private Double getGarrMinusTolerance() {
		return garrMinusTolerance;
	}

	private void setFtraMinusTolerance(String ftraMinsTolerance) {
		try {
			ftraMinusTolerance = Double.valueOf(Double.parseDouble(ftraMinsTolerance));
		} catch (Exception e) {
			System.err.println("Error parsing ftraMinusTolerance Number: " + e);
		}
	}

	private static PrintText getPrintTextWin() {
		if (printTextWin == null) {
			return printTextWin = new PrintText(getScheduleTable(), "Print");
		}
		return printTextWin;
	}

	private static PrintText getPrintRecordCopyWin() {
		if (printRecordCopyWin == null) {
			return printRecordCopyWin = new PrintText(getPrintRecordCopyTable(), "Print Record Copy");
		}
		return printRecordCopyWin;
	}

	private Double getFtraMinusTolerance() {
		return ftraMinusTolerance;
	}

	private ScheduleFileData getSchedFileData(boolean flag) {
		if ((schedFileData == null) || (flag)) {
			schedFileData = new ScheduleFileData();
		}

		return schedFileData;
	}

	private ScheduleFileData setSchedFileData(ScheduleFileData sfd) {
		schedFileData = sfd;
		return schedFileData;
	}

	private void clearScheduleTable(JTable table) {
		for (int rows = 0; rows < table.getRowCount(); rows++) {
			for (int cols = 0; cols < table.getColumnCount(); cols++) {
				table.setValueAt("", rows, cols);
			}
		}
	}

	private void setMaxRowNum(int num) {
		maxRowNum = num;
	}

	private int getMaxRowNum() {
		return maxRowNum;
	}

	protected void setSchedStartDay(Calendar schdStartDay) {
		schedStartDay = schdStartDay;
	}

	protected Calendar getSchedStartDay() {
		return schedStartDay;
	}

	protected void setSchedEndDay(Calendar schdEndDay) {
		schedEndDay = schdEndDay;
	}

	protected Calendar getSchedEndDay() {
		return schedEndDay;
	}

	protected void setSchedFileEndDay(String sfed) {
		schedFileEndDay = sfed;
	}

	protected String getSchedFileEndDay() {
		return schedFileEndDay;
	}

	protected void setDailyEndDay(Calendar ded) {
		dailyEndDay = ded;
	}

	protected Calendar getDailyEndDay() {
		return dailyEndDay;
	}

	private int getStartDayAdjustment() {
		return startDayAdjustment;
	}

	private int getColumnZero() {
		return columnZero;
	}

	private int getColumnTwo() {
		return columnTwo;
	}

	private int getColumnThree() {
		return columnThree;
	}

	private int getColumnFour() {
		return columnFour;
	}

	private int getColumnFive() {
		return columnFive;
	}

	private int getColumnNine() {
		return columnNine;
	}

	private int getRowTwo() {
		return rowTwo;
	}

	private int getRowThree() {
		return rowThree;
	}

	private int getRowFour() {
		return rowFour;
	}

	private int getRowFive() {
		return rowFive;
	}

	private int getRowSix() {
		return rowSix;
	}

	protected static boolean isSaveFlag() {
		return saveFlag;
	}

	protected static void setSaveFlag(boolean saveFlag) {
		saveFlag = saveFlag;
	}

	protected static LinkedHashMap<String, LinkedHashMap<String, Vector<OutagesTable>>> getOutagesMap() {
		return outagesMap;
	}

	protected static void setOutagesMap(LinkedHashMap<String, LinkedHashMap<String, Vector<OutagesTable>>> outMap) {
		outagesMap = outMap;
	}
}
