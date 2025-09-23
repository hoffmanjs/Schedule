package usace.wm.schedule;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.text.JTextComponent;
public class ScheduleReschedule extends JDialog {
	private static final long serialVersionUID = 7526472295622776147L;
	private JTable schedReschedTable;
	private static Vector<String> sites = new Vector<String>();
	private HashMap<String, Vector<DlyData>> dlyDataFile = new HashMap<String, Vector<DlyData>>();
	HashMap<String, List<Integer>> capabilities = new HashMap<String, List<Integer>>();
	private String startDay = null;
	private String schfileEndDate = null;
	private String energyEndDate = null;
	private String schfileStartDate = null;
	private String mondayCapTolDate = null;
	private String saturdayCapTolDate = null;
	private JButton forwardButton = new JButton();
	private JButton backButton = new JButton();
	private JButton echoButton = new JButton();
	private JButton computeButton = new JButton();
	private JButton clearButton = new JButton();
	private JButton toleranceButton = new JButton();
	private JButton exitButton = new JButton();
	private Calendar currentRescheduleDate = null;
	private static String[] capToleranceInfo = null;
	DecimalFormat formatNoDecimal = new DecimalFormat("#####");
	DecimalFormat formatOneDecimal = new DecimalFormat("#####.0");
	DecimalFormat formatTwoDecimal = new DecimalFormat("#####.00");
	DecimalFormat formatThreeDecimal = new DecimalFormat("#####.000");
	private static HashMap<String, Vector<String>> scheduleData = null;
	private static Tolerances toleranceWin = null;
	private double ACFT_TO_CFS = 1.9835D;
	private static boolean updateFlag = false;
	private Calendar endDataDate = null;
	int reschedTableHeaderRow = 19;
	int damnSitesRow = reschedTableHeaderRow + 1;
	int reschOPRow = reschedTableHeaderRow + 2;
	int geOPsRow = reschedTableHeaderRow + 3;
	int coeffRow = reschedTableHeaderRow + 4;
	int outagesRow = reschedTableHeaderRow + 5;
	int odsRow = reschedTableHeaderRow + 6;
	int reschTotQRow = reschedTableHeaderRow + 7;
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

	public ScheduleReschedule(Vector<String> sites, HashMap<String, Vector<DlyData>> dlyDataFile, ScheduleFileData sfd,
			String schfileEndDay, String energyEndDate, String schfileStartDate, String[] capToleranceInfo) {
		setSites(sites);
		setDlyDataFile(dlyDataFile);
		Util.setSchedFileData(sfd);
		setSchfileEndDate(schfileEndDay);
		setEnergyEndDate(energyEndDate);
		setSchfileStartDate(schfileStartDate);
		setCapToleranceInfo(capToleranceInfo);
		// System.out.println("ActGenPower0: " + Util.getSchedFileData().getDlyDataMap().get("250419_2").getActGenPower());

		setStartDay(Util.getCalendarYYMMDD(Util.subtractCalendarDay(Util.getCalendarYYMMDD(getSchfileEndDate()), 8)));
		try {
			jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void jbInit() throws Exception {
		getContentPane().setLayout(null);
		setSize(new Dimension(740, 540));//Height 530

		// Changes window title for DST reminder
		if (Util.isDstDays(null) == 1) {
			setTitle("Schedule/Reschedule  ----  DST This Weekend: " + Util.getNextDstDate());
		} else {
			setTitle("Schedule/Reschedule");
		}

		setResizable(false);
		setModal(true);

		schedReschedTable = new JTable() {
			private static final long serialVersionUID = 7526472295622776147L;

			public Component prepareRenderer(TableCellRenderer renderer, int rowIndex, int vColIndex) {
				Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);
				c.setFont(new Font("Times New Roman", 1, 14));
				c.setBackground(Color.WHITE);
				c.setForeground(getForeground());
				return c;
			}

			public Component prepareEditor(TableCellEditor editor, int row, int column) {
				Component c = super.prepareEditor(editor, row, column);
				if ((c instanceof JTextComponent)) {
					String val = (String) schedReschedTable.getValueAt(row, column);
					((JTextComponent) c).requestFocus();
					((JTextComponent) c).selectAll();
				}
				return c;
			}
		};
		TableModel model = new myTableModel();

		schedReschedTable.setModel(model);
		schedReschedTable.setDefaultRenderer(Object.class, new SelectAllRenderer());

		schedReschedTable.setAutoResizeMode(0);
		schedReschedTable.setCellSelectionEnabled(true);

		TableColumn col = schedReschedTable.getColumnModel().getColumn(0);
		col.setPreferredWidth(180); //Setting the column width

		col = schedReschedTable.getColumnModel().getColumn(1);
		col.setPreferredWidth(130);

		col = schedReschedTable.getColumnModel().getColumn(2);
		col.setPreferredWidth(130);

		col = schedReschedTable.getColumnModel().getColumn(3);
		col.setPreferredWidth(145);

		col = schedReschedTable.getColumnModel().getColumn(4);
		col.setPreferredWidth(140);

		col = schedReschedTable.getColumnModel().getColumn(5);
		col.setPreferredWidth(145);

		col = schedReschedTable.getColumnModel().getColumn(6);
		col.setPreferredWidth(110);

		col = schedReschedTable.getColumnModel().getColumn(7);
		col.setPreferredWidth(110);

		col = schedReschedTable.getColumnModel().getColumn(8);
		col.setPreferredWidth(125);

		col = schedReschedTable.getColumnModel().getColumn(9);
		col.setPreferredWidth(90);  //GAPT column
		
		col = schedReschedTable.getColumnModel().getColumn(10);
		col.setPreferredWidth(1);

		col = schedReschedTable.getColumnModel().getColumn(11);
		col.setPreferredWidth(1);

		computeButton.setText("Compute");
		computeButton.setMnemonic('C');
		computeButton.setToolTipText("Compute Schedule Info");
		computeButton.setBounds(new Rectangle(10, 455, 90, 25));
		computeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setUpdateFlag(true);
				computeButton_actionPerformed(e);
				// computeButton_actionPerformed(e); //TODO not sure if this is needed
			}
		});
		clearButton.setText("Clear");
		clearButton.setMnemonic('l');
		clearButton.setToolTipText("Clear the Day's Info");
		clearButton.setBounds(new Rectangle(530, 455, 75, 25));
		clearButton.setToolTipText("Clear the data for a day.");
		clearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clearButton_actionPerformed(e);
				computeButton_actionPerformed(e);
			}
		});
		toleranceButton.setText("Tolerance");
		toleranceButton.setMnemonic('T');
		toleranceButton.setToolTipText("Show Tolerance Window");
		toleranceButton.setBounds(new Rectangle(115, 455, 95, 25));
		toleranceButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				toleranceButton_actionPerformed(e);
			}
		});
		exitButton.setText("Exit");
		exitButton.setMnemonic('X');
		exitButton.setToolTipText("Close Window");
		exitButton.setBounds(new Rectangle(630, 455, 75, 25));
		exitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exitButton_actionPerformed(e);
			}
		});
		echoButton.setText("Echo");
		echoButton.setMnemonic('E');
		echoButton.setToolTipText("Copy Previous Day Info");
		echoButton.setBounds(new Rectangle(235, 455, 75, 25));
		echoButton.setEnabled(true);
		echoButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				echoButton_actionPerformed(e);
			}
		});
		backButton.setText("Back");
		backButton.setMnemonic('B');
		backButton.setToolTipText("Back One Day");
		backButton.setBounds(new Rectangle(330, 455, 75, 25));
		backButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				backButton_actionPerformed(e);
			}
		});
		forwardButton.setText("Forward");
		forwardButton.setMnemonic('F');
		forwardButton.setToolTipText("Forward One Day");
		forwardButton.setBounds(new Rectangle(425, 455, 85, 25));
		forwardButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				forwardButton_actionPerformed(e);
			}
		});
		schedReschedTable.setBounds(new Rectangle(15, 10, 710, 440));//TODO Height 430
		schedReschedTable.setGridColor(Color.LIGHT_GRAY);
		schedReschedTable.setShowGrid(true);
		schedReschedTable.setShowVerticalLines(false);
		schedReschedTable.setBorder(BorderFactory.createLineBorder(Color.black, 1));
		getContentPane().add(computeButton, null);
		getContentPane().add(exitButton, null);
		getContentPane().add(toleranceButton, null);
		getContentPane().add(clearButton, null);
		getContentPane().add(echoButton, null);
		getContentPane().add(backButton, null);
		getContentPane().add(forwardButton, null);
		getContentPane().add(schedReschedTable, null);

		loadTable();
		schedReschedTable.setName(Util.getScheduleTableName());
		setUpIntegerEditor(schedReschedTable);
	}

	public class myTableModel extends DefaultTableModel {
		private static final long serialVersionUID = 7526472295622776147L;

		myTableModel() {
			super(27, 12);
		}

		public boolean isCellEditable(int row, int col) {
			Object obj = getValueAt(row, col);
			if (obj == null) {
				setValueAt("", row, col);
			}
			if (row == 25) {
				return true;
			} else if (((row < 21) || (row >= 24))) {
				return false;
			} else if ((col < 1) || (col > 10)) {
				return false;
			} else if (((col == 4) || (col == 5)) && (row == 22)) {
				return false;
			}

			return true;
		}
	}

	private void setUpIntegerEditor(JTable table) {
		Action action = new AbstractAction() {
			private static final long serialVersionUID = -4806611971344855367L;

			public void actionPerformed(ActionEvent e) {
				TableCellListener tcl = (TableCellListener) e.getSource();
				if (tcl.isFlag()) {
					computeButton.setEnabled(false);
				} else {
					computeButton.setEnabled(true);
				}
			}
		};
	}

	private void loadTable() {
		loadEnergyTable(getSchedReschedTable());
		loadWaterScheduleTable(getSchedReschedTable());
		setRescheduleInfo(getSchedReschedTable());
	}

	private void loadEnergyTable(JTable table) {
		int HEADER_ROW = 0;
		int SITE_ROW = 1;
		int startGErow = 2;

		table.setValueAt("Energy   -", HEADER_ROW, getColumnThree());
		table.setValueAt("Schedule", HEADER_ROW, getColumnFour());
		table.setValueAt(" (MW     -", HEADER_ROW, getColumnFive());
		table.setValueAt(" MWH)", HEADER_ROW, getColumnSix());

		Vector<String> sites = getSites();
		for (int x = 0; x < sites.size(); x++) {
			table.setValueAt(((String) sites.get(x)).toString(), SITE_ROW, x + 2);
		}
		table.setValueAt("GE Total", SITE_ROW, getColumnEight());

		Calendar eday = Util.getCalendarYYMMDD(getSchfileEndDate());
		Calendar sday = Util.getCalendarYYMMDD(getStartDay());

		Calendar endData = Util.getDate();
		endData.set(1, sday.get(1));
		endData.set(2, sday.get(2));
		endData.set(5, sday.get(5));
		endData.add(5, -1);
		setEndDataDate(endData);
		while (eday.after(sday)) {
			int record = 0;
			String startKey = Util.getDataKey(sday);

			HashMap<String, DlyData> dly = Util.getSchedFileData().getDlyDataMap();

			int dayWeek = sday.get(7);
			int dayMonth = sday.get(5);
			if (dly.containsKey(startKey + "_0")) {
				table.setValueAt(dayMonth + " " + Util.getDayOfWeek(dayWeek), startGErow, record + 1);
			} else if (sday.get(5) != eday.get(5)) {
				table.setValueAt(dayMonth + " " + Util.getDayOfWeek(dayWeek), startGErow, record + 1);
			}
			while (record < 6) {
				DlyData data = null;
				String key = startKey + "_" + record;
				if (dly.containsKey(key)) {
					data = (DlyData) dly.get(key);
					if (data.getActGenPowerNum().doubleValue() != 0.0D) {
						table.setValueAt(getFormatNoDecimal().format(data.getActGenPowerNum()), startGErow, record + 2);
					} else {
						table.setValueAt("", startGErow, record + 2);
					}
					if (data.getRescheduleEnergyNum().doubleValue() > 0.0D) {
						table.setValueAt(getFormatNoDecimal().format(data.getRescheduleEnergyNum()), startGErow,
								record + 2);
					}
				}
				record++;
			}
			HashMap<String, String> totEnergyMap = Util.getSchedFileData().getTotCalEnrgyMap();
			String ge = (String) totEnergyMap.get(startKey);
			if (ge.toString().trim().length() == 0) {
				ge = "0.0";
			}
			double totalEnergy = Double.parseDouble(ge);

			HashMap<String, String> totReschedEnergyMap = Util.getSchedFileData().getTotReschedCalEnrgyMap();
			double totalReschedEnergy = Double.parseDouble((String) totReschedEnergyMap.get(startKey));
			if (totalReschedEnergy > 0.0D) {
				table.setValueAt(getFormatNoDecimal().format(totalReschedEnergy), startGErow, record + 2);
				getEndDataDate().add(5, 1);
			} else if (totalEnergy > 0.0D) {
				table.setValueAt(getFormatNoDecimal().format(totalEnergy), startGErow, record + 2);
				getEndDataDate().add(5, 1);
			}
			if ((totalReschedEnergy == 0.0D) && (totalEnergy == 0.0D)) {
				table.setValueAt("", startGErow, record + 2);
			}
			startGErow++;
			sday.add(5, 1);
		}
		setSchedReschedTable(table);
		Util.getSchedFileData().setRescheduleDataEndDate(getEndDataDate());
	}

	private void loadWaterScheduleTable(JTable table) {
		int HEADER_ROW = 10;
		int startKCFSrow = 11;

		table.setValueAt("Water   -", HEADER_ROW, getColumnThree());
		table.setValueAt("Schedule", HEADER_ROW, getColumnFour());
		table.setValueAt(" - KCFS", HEADER_ROW, getColumnFive());

		Calendar sday = Util.getCalendarYYMMDD(getStartDay());
		Calendar eday = Util.getCalendarYYMMDD(getSchfileEndDate());
		while (eday.after(sday)) {
			int record = 0;
			String startKey = Util.getDataKey(sday);

			HashMap<String, DlyData> dly = Util.getSchedFileData().getDlyDataMap();

			int dayWeek = sday.get(7);
			int dayMonth = sday.get(5);
			if (dly.containsKey(startKey + "_0")) {
				table.setValueAt(dayMonth + " " + Util.getDayOfWeek(dayWeek), startKCFSrow, record + 1);
			} else if (sday.get(5) != eday.get(5)) {
				table.setValueAt(dayMonth + " " + Util.getDayOfWeek(dayWeek), startKCFSrow, record + 1);
			}
			while (record < 6) {
				DlyData data = null;
				String key = startKey + "_" + record;
				if (dly.containsKey(key)) {
					data = (DlyData) dly.get(key);
					if (data.getActGenPowerNum().doubleValue() != 0.0D) {
						if ((data.getODNum().doubleValue() > data.getKcfsNum().doubleValue())
								&& (data.getKcfsNum().doubleValue() > 0.0D)) {
							table.setValueAt(getFormatOneDecimal().format(data.getODNum()), startKCFSrow, record + 2);
						} else {
							table.setValueAt(getFormatOneDecimal().format(data.getKcfsNum()), startKCFSrow, record + 2);
						}
					}
					if ((data.getRescheduleEnergyNum().doubleValue() > 0.0D)
							&& (data.getODNum().doubleValue() <= 0.0D)) {
						table.setValueAt(getFormatOneDecimal().format(data.getRescheduleOpNum()), startKCFSrow,
								record + 2);
					}
					if ((data.getActGenPowerNum().doubleValue() == 0.0D)
							&& (data.getRescheduleEnergyNum().doubleValue() == 0.0D)) {
						table.setValueAt("", startKCFSrow, record + 2);
					}
				}
				record++;
			}
			startKCFSrow++;
			sday.add(5, 1);
		}
		setSchedReschedTable(table);
	}

	private Calendar getScheduleDate(int daysToAdd) {
		String sday = null;
		if (getCurrentRescheduleDate() == null) {
			sday = getEnergyEndDate();
		} else {
			sday = Util.getDataKey(getCurrentRescheduleDate());
		}
		Calendar startDay = Util.getCalendarYYMMDD(sday);
		startDay.add(5, daysToAdd);
		setCurrentRescheduleDate(startDay);
		return startDay;
	}

	private String getScheduleDateFormat(Calendar cal) {
		int dayWeek = cal.get(7);
		int dayOfMonth = cal.get(5);

		return String.valueOf(dayOfMonth) + " " + Util.getDayOfWeek(dayWeek);
	}

	private void setRescheduleInfo(JTable table) {
		table.setValueAt("Schedule", getReschedTableHeaderRow(), getColumnThree());
		table.setValueAt(" Date >", getReschedTableHeaderRow(), getColumnFour());
		table.setValueAt(getScheduleDateFormat(getScheduleDate(0)), getReschedTableHeaderRow(), getColumnFive());

		table.setValueAt("BB El", getReschedTableHeaderRow(), getColumnEight());
		table.setValueAt("GAPT", getReschedTableHeaderRow(), getColumnNine());
		table.setValueAt("GE Total", getReschedTableHeaderRow() + 1, getColumnOne());

		Vector<String> sites = getSites();
		for (int x = 0; x < sites.size(); x++) {
			String xy = ((String) sites.get(x)).toString();

			table.setValueAt(xy, getDamnSitesRow(), x + 2);
		}
		table.setValueAt("Chng", getDamnSitesRow(), getColumnEight());
		table.setValueAt("24ID", getDamnSitesRow(), getColumnNine());
		table.setValueAt("Resch OP", getReschOPRow(), getColumnZero());
		table.setValueAt("GE-OP's", getGeOPsRow(), getColumnZero());
		table.setValueAt("Coeff.", getCoeffRow(), getColumnZero());
		table.setValueAt("Outages", getOutagesRow(), getColumnZero());
		table.setValueAt("Total Q", getOdsRow(), getColumnZero());
		table.setValueAt("Resch Tot Q", getReschTotQRow(), getColumnZero());
		
		setSchedReschedTable(table);
		setSchedReschedTableData(table, getCurrentRescheduleDate(), true);
	}

	/**
	 * 
	 * @param table
	 * @param date
	 * @param echoFlag
	 */
	private void setSchedReschedTableData(JTable table, Calendar date, boolean echoFlag) {
		ScheduleFileData sch = Util.getSchedFileData();
		HashMap<String, DlyData> dly = sch.getDlyDataMap();
		String startKey = Util.getDataKey(date);
		try {
			if (echoFlag) {
				int reschedOpRecord = 0;
				while (reschedOpRecord < 6) {
					DlyData data = null;
					String key = startKey + "_" + reschedOpRecord;
					if (dly.containsKey(key)) {
						data = (DlyData) dly.get(key);
						if (data.getRescheduleOpNum().doubleValue() > 0.0D) {
							table.setValueAt(getFormatOneDecimal().format(data.getRescheduleOpNum()), getReschOPRow(),
									reschedOpRecord + 2);
						} else {
							table.setValueAt("", getReschOPRow(), reschedOpRecord + 2);
						}
					}
					reschedOpRecord++;
				}
			}
			HashMap<String, String> ge = sch.getTotCalEnrgyMap();
			if (ge.containsKey(Util.getDataKey(date))) {
				String data = (String) ge.get(Util.getDataKey(date));
				if (data.trim().length() > 0) {
					if (Double.parseDouble(data) > 0.0D) {
						table.setValueAt(getFormatNoDecimal().format(Double.parseDouble(data)), getGeOPsRow(), 1);
					} else {
						table.setValueAt("", getGeOPsRow(), getColumnOne());
					}
				} else {
					table.setValueAt("", getGeOPsRow(), getColumnOne());
				}
			} else {
				table.setValueAt("", getGeOPsRow(), getColumnOne());
			}
			int geRecord = 0;
			while (geRecord < 6) {
				DlyData data = null;
				String key = startKey + "_" + geRecord;
				if (dly.containsKey(key)) {
					data = (DlyData) dly.get(key);

					table.setValueAt(getFormatOneDecimal().format(data.getKcfsNum()), getGeOPsRow(), geRecord + 2);
				}
				geRecord++;
			}
			HashMap<String, String> bbElevMap = sch.getBbElevMap();
			if (bbElevMap.containsKey(Util.getDataKey(date))) {
				String data = (String) bbElevMap.get(Util.getDataKey(date));
				if (data.trim().length() > 0) {
					if (!data.equalsIgnoreCase("0.00")) {
						table.setValueAt(getFormatTwoDecimal().format(Double.parseDouble(data)), getGeOPsRow(),
								getColumnEight());
					} else {
						table.setValueAt("", getGeOPsRow(), getColumnEight());
					}
				} else {
					table.setValueAt("", getGeOPsRow(), getColumnEight());
				}
			} else {
				table.setValueAt("", getGeOPsRow(), getColumnEight());
			}
			if (echoFlag) {
				HashMap<String, String> id24Map = sch.getId24Map();
				if (id24Map.containsKey(Util.getDataKey(date))) {
					String data = (String) id24Map.get(Util.getDataKey(date));
					if (data.length() > 0) {
						if (Double.parseDouble(data) > 0.0D) {
							table.setValueAt(getFormatOneDecimal().format(Double.parseDouble(data)), getGeOPsRow(),
									getColumnNine());
						} else {
							table.setValueAt("", getGeOPsRow(), getColumnNine());
						}
					}
				} else {
					table.setValueAt("", getGeOPsRow(), getColumnNine());
				}
			}

			HashMap<String, String[]> coefficients = sch.getCoefficientMap();
			if (coefficients.containsKey(Util.getDataKey(date))) {
				String[] data = (String[]) coefficients.get(Util.getDataKey(date));
				for (int x = 0; x < data.length; x++) {
					if (data[x].trim().length() > 0) {
						if (Double.parseDouble(data[x]) > 0.0D) {
							table.setValueAt(getFormatOneDecimal().format(Double.parseDouble(data[x])), getCoeffRow(),
									x + 2);
						} else {
							table.setValueAt("", getCoeffRow(), x + 2);
						}
					} else {
						table.setValueAt("", getCoeffRow(), x + 2);
					}
				}
			}
			//TODO changes here for the new time-series for each project
			LinkedHashMap<String, LinkedHashMap<String, Vector<OutagesTable>>> oMap = WaterManagementUI.getOutagesMap();

			if (oMap.containsKey(Util.getDataKey(date))) {
				String[] data = WaterManagementUI.formatOutagesDayMap(oMap, Util.getDataKey(date));
				for (int x = 0; x < data.length; x++) {
					if (data[x].length() > 0) {
						if (Double.parseDouble(data[x]) > 0.0D) {
							table.setValueAt(getFormatNoDecimal().format(Double.parseDouble(data[x])), getOutagesRow(),
									x + 2);
						}
					} else {
						table.setValueAt("", getOutagesRow(), x + 2);
					}
				}
			}
			int dlyRecord = 0;
			while (dlyRecord < 6) {
				DlyData data = null;
				String key = startKey + "_" + dlyRecord;
				if (dly.containsKey(key)) {
					data = (DlyData) dly.get(key);
					if (data.getODNum().doubleValue() > 0.0D) {
						table.setValueAt(getFormatOneDecimal().format(data.getODNum()), getOdsRow(), dlyRecord + 2);
					} else {
						table.setValueAt("", getOdsRow(), dlyRecord + 2);
					}
				}
				dlyRecord++;
			}
		} catch (Exception e) {
			System.out.println("-Exception setSchedReschedTableData" + e);
			e.printStackTrace();
		}
		setSchedReschedTable(table);
	}

	private void updateReschedOPInformation() {
		ScheduleFileData sch = Util.getSchedFileData();
		HashMap<String, DlyData> dly = sch.getDlyDataMap();

		int geRecord = 0;
		while (geRecord < 6) {
			DlyData data = null;
			String key = getStartKey() + "_" + geRecord;
			if (dly.containsKey(key)) {
				data = (DlyData) dly.get(key);
			} else {
				data = new DlyData();
			}
			try {
				if ((geRecord == 0) || (geRecord == 1) || (geRecord == 4) || (geRecord == 5)) {
					Object d = getSchedReschedTable().getValueAt(getReschOPRow(), getColumnTwo() + geRecord);
					if (d != null) {
						if (d.toString().length() <= 0) {
							data.setRescheduleOP("0.0");
						}
					} else {
						data.setRescheduleOP("0.0");
					}
				}
			} catch (Exception e) {
				System.out.println("-Exception updateReschedOPInformation: ");
				e.printStackTrace();
			}
			dly.put(key, data);
			geRecord++;
		}
		sch.setDlyDataMap(dly);
		Util.setSchedFileData(sch);
	}

	private void updateRescheduleOPTotalInformation() {
		ScheduleFileData sch = Util.getSchedFileData();
		HashMap<String, String> reschedOP = sch.getTotReschedCalEnrgyMap();

		String data = "0.0";
		try {
			if (reschedOP.containsKey(getStartKey())) {
				String d = (String) getSchedReschedTable().getValueAt(getReschOPRow(), getColumnOne());
				if ((d != null) && (d.trim().length() > 0)) {
					data = d.toString();
				}
			} else {
				data = new String(getSchedReschedTable().getValueAt(getReschOPRow(), getColumnOne()).toString());
			}
		} catch (Exception e) {
			System.out.println("-Exception updateRescheduleOPTotalInformation: " + e);
		}
		reschedOP.put(getStartKey(), data);
		sch.setTotReschedCalEnrgyMap(reschedOP);
		Util.setSchedFileData(sch);
	}

	private void update24IDInformation() {
		ScheduleFileData sch = Util.getSchedFileData();
		HashMap<String, String> id24Map = sch.getId24Map();

		String data = "0";
		if (id24Map.containsKey(getStartKey())) {
			data = getSchedReschedTable().getValueAt(getGeOPsRow(), getColumnNine()).toString();
		} else {
			data = new String(getSchedReschedTable().getValueAt(getGeOPsRow(), getColumnNine()).toString());
		}
		if (data.trim().length() <= 0) {
			data = "0";
		}
		id24Map.put(getStartKey(), data);
		sch.setId24Map(id24Map);
		Util.setSchedFileData(sch);
	}

	private void updateBbElevInformation() {
		ScheduleFileData sch = Util.getSchedFileData();
		HashMap<String, String> bbElevMap = sch.getBbElevMap();

		String data = "0.0";
		if (bbElevMap.containsKey(getStartKey())) {
			data = getSchedReschedTable().getValueAt(getGeOPsRow(), getColumnEight()).toString();
			if (data.length() == 0) {
				data = "0.0";
			}
		} else {
			data = new String(getSchedReschedTable().getValueAt(getGeOPsRow(), getColumnEight()).toString());
		}
		bbElevMap.put(getStartKey(), data);
		sch.setBbElevMap(bbElevMap);
		Util.setSchedFileData(sch);
	}

	private void updateGEInformation() {
		ScheduleFileData sch = Util.getSchedFileData();
		HashMap<String, DlyData> dly = sch.getDlyDataMap();

		int geRecord = 0;
		while (geRecord < 6) {
			DlyData data = null;
			String key = getStartKey() + "_" + geRecord;
			if (dly.containsKey(key)) {
				data = (DlyData) dly.get(key);
			} else {
				data = new DlyData();
			}
			dly.put(key, data);
			geRecord++;
		}
		sch.setDlyDataMap(dly);
		Util.setSchedFileData(sch);
	}

	private void updateGeTotalInformation() {
		ScheduleFileData sch = Util.getSchedFileData();
		HashMap<String, String> ge = sch.getTotCalEnrgyMap();

		String data = "0.0";
		if (ge.containsKey(getStartKey())) {
			data = getSchedReschedTable().getValueAt(getGeOPsRow(), getColumnOne()).toString();
		} else {
			data = new String(getSchedReschedTable().getValueAt(getGeOPsRow(), getColumnOne()).toString());
		}
		if (data.length() == 0) {
			data = "0.0";
		}
		ge.put(getStartKey(), data);
		sch.setTotCalEnrgyMap(ge);
		Util.setSchedFileData(sch);
	}

	private void updateCoefficientInformation() {
		ScheduleFileData sch = Util.getSchedFileData();
		HashMap<String, String[]> coefficients = sch.getCoefficientMap();
		String[] data = (String[]) null;
		if (coefficients.containsKey(getStartKey())) {
			data = (String[]) coefficients.get(getStartKey());
			for (int x = 0; x < data.length; x++) {
				data[x] = getSchedReschedTable().getValueAt(getCoeffRow(), getColumnTwo() + x).toString();
				if (data[x] == "") {
					data[x] = "0.0";
				}
			}
		} else {
			data = new String[6];
			int record = 0;
			while (record < 6) {
				data[record] = getSchedReschedTable().getValueAt(getCoeffRow(), getColumnTwo() + record).toString();
				record++;
			}
		}
		coefficients.put(getStartKey(), data);
		sch.setCoefficientMap(coefficients);
		Util.setSchedFileData(sch);
	}

	private void updateODInformation() {
		ScheduleFileData sch = Util.getSchedFileData();
		HashMap<String, DlyData> dly = sch.getDlyDataMap();

		int dlyRecord = 0;
		while (dlyRecord < 6) {
			DlyData data = null;
			String key = getStartKey() + "_" + dlyRecord;
			if (dly.containsKey(key)) {
				data = (DlyData) dly.get(key);
			} else {
				data = new DlyData();
			}
			if (getSchedReschedTable().getValueAt(getOdsRow(), getColumnTwo() + dlyRecord).toString().trim()
					.length() > 0) {
				data.setOD(getSchedReschedTable().getValueAt(getOdsRow(), getColumnTwo() + dlyRecord).toString());
			} else {
				data.setOD("0");
			}
			dly.put(key, data);
			dlyRecord++;
		}
		sch.setDlyDataMap(dly);
		Util.setSchedFileData(sch);
	}

	private String getStartKey() {
		Calendar now = getCurrentRescheduleDate();
		String startKey = Util.getCalendarYYMMDD(now);
		return startKey;
	}

	private void clearScheduleTableInfo() {
		for (int row = getReschOPRow(); row < getOdsRow() + 1; row++) {
			for (int column = 0; column < 10; column++) {
				getSchedReschedTable().setValueAt("", row, column + 1);
			}
		}
	}

	private void setScheduleDataMap(JTable table) {
		String mapKey = null;
		for (int y = 0; y < 4; y++) {
			Vector<String> rowData = new Vector<String>();

			mapKey = getSchedReschedTable().getValueAt(19, getColumnFive()).toString();
			for (int column = 0; column < 10; column++) {
				if (table.getValueAt(getGeOPsRow() + y, column + 1) != null) {
					rowData.add(column, (String) table.getValueAt(getGeOPsRow() + y, column + 1));
				} else {
					rowData.add(column, "");
				}
			}
			getScheduleData().put(mapKey, rowData);
		}
	}

	private void forwardButton_actionPerformed(ActionEvent e) {
		if (getSchedReschedTable().getCellEditor() != null) {
			getSchedReschedTable().getCellEditor().stopCellEditing();
		}
		updateGapt24IdData(getCurrentRescheduleDate());
		clearScheduleTableInfo();
		if ((Util.getCalendarYYMMDD(getSchfileEndDate()).after(getCurrentRescheduleDate()))
				&& (getCurrentRescheduleDate().get(5) == Util.getCalendarYYMMDD(getSchfileEndDate()).get(5))) {
			forwardButton.setEnabled(false);
		}
		if (getScheduleDate(0).before(Util.getCalendarYYMMDD(getSchfileEndDate()))) {
			getSchedReschedTable().setValueAt(getScheduleDateFormat(getScheduleDate(1)), 19, getColumnFive());
			setSchedReschedTableData(getSchedReschedTable(), getCurrentRescheduleDate(), true);
			backButton.setEnabled(true);
		} else {
			forwardButton.setEnabled(false);
		}
	}

	private void backButton_actionPerformed(ActionEvent e) {
		if (getSchedReschedTable().getCellEditor() != null) {
			getSchedReschedTable().getCellEditor().stopCellEditing();
		}
		updateGapt24IdData(getCurrentRescheduleDate());
		if (getScheduleDate(0).after(Util.getCalendarYYMMDD(getSchfileStartDate()))) {
			Calendar cal = getScheduleDate(-1);
			getSchedReschedTable().setValueAt(getScheduleDateFormat(cal), 19, getColumnFive());
			if (getScheduleDate(0).after(Util.getCalendarYYMMDD(getSchfileStartDate()))) {
				clearScheduleTableInfo();
				setSchedReschedTableData(getSchedReschedTable(), getCurrentRescheduleDate(), true);
			} else {
				setSchedReschedTableData(getSchedReschedTable(), getCurrentRescheduleDate(), true);
				backButton.setEnabled(false);
			}
			forwardButton.setEnabled(true);
		} else {
			backButton.setEnabled(false);
		}
	}

	private void echoButton_actionPerformed(ActionEvent e) {
		if (getSchedReschedTable().getCellEditor() != null) {
			getSchedReschedTable().getCellEditor().stopCellEditing();
		}
		if (getScheduleData().containsKey(getSchedReschedTable().getValueAt(19, getColumnFive()).toString())) {
			setSchedReschedTableData(getSchedReschedTable(), Util.getCalendarYYMMDD(getEnergyEndDate()), false);
		} else {
			clearScheduleTableInfo();
			getCurrentRescheduleDate().add(5, -1);
			setSchedReschedTableData(getSchedReschedTable(), getCurrentRescheduleDate(), false);
			setScheduleDataMap(getSchedReschedTable());
			getCurrentRescheduleDate().add(5, 1);
		}
	}

	private void toleranceButton_actionPerformed(ActionEvent e) {
		if (getSchedReschedTable().getCellEditor() != null) {
			getSchedReschedTable().getCellEditor().stopCellEditing();
		}
		Tolerances tolwin = getToleranceWin();

		Point ui = getLocation();
		int width = getWidth();
		int height = getHeight();

		int w = tolwin.getSize().width;
		int h = tolwin.getSize().height;

		long x = Math.round(ui.getX() + (width - w) / 2.0D);
		long y = Math.round(ui.getY() + (height - h) / 2.0D);

		tolwin.setLocation((int) x, (int) y);
		tolwin.setVisible(true);
		if (!toleranceWin.isShowing()) {
			setCapToleranceInfo(tolwin.getCapToleranceInfo());
			loadTable();
		}
	}

	private void showErrorMessage(String siteName) {
		JOptionPane.showMessageDialog(this,
				"The power plant release (OP) appears too large for " + siteName + "\n"
						+ "In addition to the OP value, enter a value for OD if needed dam release is \n"
						+ "larger than power plant capacity.\n"
						+ "Note: The OP value is used to compute energy generation (GE).  While "
						+ "the OD values is shown on the print out if present.");
	}

	/**
	 * 
	 */
	protected void calculateEnergy() {
		ScheduleFileData sch = Util.getSchedFileData();
		HashMap<String, String[]> coefficients = sch.getCoefficientMap();
		HashMap<String, String[]> outages = sch.getOutageMap();
		HashMap<String, DlyData> dly = sch.getDlyDataMap();
		HashMap<String, String> totalEnergy = sch.getTotCalEnrgyMap();
		HashMap<String, String> totalReschedEnergy = sch.getTotReschedCalEnrgyMap();
		HashMap<String, String> reschedElevBB = sch.getRescheduleBBElevMap();
		HashMap<String, String> elevBB = sch.getBbElevMap();
		HashMap<String, String> idMap = sch.getId24Map();

		// System.out.println("calculateEnergy1: " + sch.getDlyDataMap().get("250419_2").getActGenPower());
		// System.out.println("calculateEnergy2: " + dly.get("250419_2").getActGenPower());

		JTable table = getSchedReschedTable();
		String[] coefficient = new String[6];
		String[] outage = new String[6];
		
		String idData = (String) table.getValueAt(getReschOPRow(), getColumnNine());
		double id = 0.0D;
		if (idData != null) {
			idData = table.getValueAt(getReschOPRow(), getColumnNine()).toString();
			if (idData.trim().length() > 0) {
				id = Double.parseDouble(idData);
			}
		}
		idMap.put(getStartKey(), Double.toString(id));

		String elevData = table.getValueAt(getGeOPsRow(), getColumnEight()).toString();
		double elev = 0.0D;
		if (elevData.trim().length() > 0) {
			elev = Double.parseDouble(elevData);
		}
		elevBB.put(getStartKey(), Double.toString(elev));

		String reschedElevData = (String) table.getValueAt(getReschOPRow(), getColumnEight());
		double reschedElev = 0.0D;
		if (reschedElevData != null) {
			reschedElevData = table.getValueAt(getReschOPRow(), getColumnEight()).toString();
			if (reschedElevData.trim().length() > 0) {
				reschedElev = Double.parseDouble(reschedElevData);
			}
		}
		reschedElevBB.put(getStartKey(), Double.toString(reschedElev));

		String energyData = (String) table.getValueAt(getGeOPsRow(), getColumnOne());
		int energy = 0;
		if (energyData != null) {
			energyData = table.getValueAt(getGeOPsRow(), getColumnOne()).toString();
			if (energyData.trim().length() > 0) {
				energy = Integer.parseInt(energyData);
			}
		}
		totalEnergy.put(getStartKey(), Integer.toString(energy));

		String reschedEnergyData = (String) table.getValueAt(getReschOPRow(), getColumnOne());
		int reschedEnergy = 0;
		if (reschedEnergyData != null) {
			reschedEnergyData = table.getValueAt(getReschOPRow(), getColumnOne()).toString();
			if (reschedEnergyData.trim().length() > 0) {
				reschedEnergy = Integer.parseInt(reschedEnergyData);
			}
		}
		totalReschedEnergy.put(getStartKey(), Integer.toString(reschedEnergy));
		for (int col = 2; col < 8; col++) {
			String key = getStartKey() + "_" + (col - 2);

			DlyData data = null;
			if (dly.containsKey(key)) {
				data = (DlyData) dly.get(key);
			} else {
				data = new DlyData();
			}
			String geopData = table.getValueAt(getGeOPsRow(), col).toString();
			double geop = 0.0D;
			if (geopData.trim().length() > 0) {
				geop = Double.parseDouble(geopData);
				data.setKcfs(geopData.trim());
			} else {
				data.setKcfs("0");
			}
			String reschedOPData = (String) table.getValueAt(getReschOPRow(), col);
			if (reschedOPData != null) {
				if (reschedOPData.trim().length() > 0) {
					reschedOPData = table.getValueAt(getReschOPRow(), col).toString();

					data.setRescheduleOP(reschedOPData.trim());
				}
			} else {
				data.setRescheduleOP("0");
			}
			String coefData = table.getValueAt(getCoeffRow(), col).toString();
			double coef = 0.0D;
			if (coefData.trim().length() > 0) {
				coefficient[(col - 2)] = coefData;
				coef = Double.parseDouble(coefData);
			} else {
				coefficient[(col - 2)] = "0.0";
			}
			String outageData = (String) table.getValueAt(getOutagesRow(), col);
			if (outageData != null) {
				outageData = table.getValueAt(getOutagesRow(), col).toString();
				if (outageData.trim().length() > 0) {
					outage[(col - 2)] = outageData;
				}
			} else {
				outage[(col - 2)] = "0";
			}
			String odData = (String) table.getValueAt(getOdsRow(), col);
			if (odData != null) {
				odData = table.getValueAt(getOdsRow(), col).toString();
				if (odData.trim().length() > 0) {
					data.setOD(odData.trim());
				}
			} else {
				data.setOD("0");
			}
			double resched = -99.99;
			if(col == 5){
				resched = coef > 0.5D ? 60 * (int) (geop * coef / 60.0D + 0.5D) : 0.0D;
			} else {
				resched = coef > 0.5D ? 50 * (int) (geop * coef / 50.0D + 0.5D) : 0.0D;
			}
			
//			if( key.startsWith("250513")){
//				System.out.println("DlyDataMap ActGenPower:" + key +": " + sch.getDlyDataMap().get(key).getActGenPower());
//				System.out.println("ActGenPower:" + key +":: " + data.getActGenPower());
//			}
						
			data.setActGenPower(Double.toString(resched));
			dly.put(key, data);
			
//			if( key.startsWith("250513_2")){
//				System.out.println("setActGenPower2: " + dly.get("250513_2").getActGenPower());
//			}
//			if( key.startsWith("250513_3")){
//				System.out.println("setActGenPower3: " + dly.get("250513_3").getActGenPower());
//			}

		}//for
		sch.setId24Map(idMap);
		sch.setBbElevMap(elevBB);
		sch.setRescheduleBBElevMap(reschedElevBB);
		outages.put(getStartKey(), outage);
		coefficients.put(getStartKey(), coefficient);
		sch.setDlyDataMap(dly);
		sch.setTotCalEnrgyMap(totalEnergy);
		sch.setTotReschedCalEnrgyMap(totalReschedEnergy);
		Util.setSchedFileData(sch);

//		System.out.println("getActGenPower1: " + sch.getDlyDataMap().get("250513_2").getActGenPower());
//		System.out.println("getActGenPower12: " + sch.getDlyDataMap().get("250513_3").getActGenPower());

	}

	/**
	 * Computes the Water Schedule information for a day.
	 * 
	 * @param day
	 *            - Calendar
	 */
	protected void compute(Calendar day) {
		System.out.println("Compute water schedule for " + Util.getCalendarYYMMDD(day));
		// compsch computes schedule for one dayKey
		// float
		// bendvol[2][9]={{1424.2,1476.3,1529.6,1584.2,1640,1696.9,1754.9,1814.1,1874.5},
		// {1415,1416,1417,1418,1419,1420,1421,1422,1423}};
		double[][] bendvol = new double[2][9];
		bendvol[0][0] = 1424.2;
		bendvol[0][1] = 1476.3;
		bendvol[0][2] = 1529.6;
		bendvol[0][3] = 1584.2;
		bendvol[0][4] = 1640;
		bendvol[0][5] = 1696.9;
		bendvol[0][6] = 1754.9;
		bendvol[0][7] = 1814.1;
		bendvol[0][8] = 1874.5;
		// Second Row
		bendvol[1][0] = 1415;
		bendvol[1][1] = 1416;
		bendvol[1][2] = 1417;
		bendvol[1][3] = 1418;
		bendvol[1][4] = 1419;
		bendvol[1][5] = 1420;
		bendvol[1][6] = 1421;
		bendvol[1][7] = 1422;
		bendvol[1][8] = 1423;

		int i;
		int d3, d4, a;
		double fa, fb, fc;
		// Note this is where discharge capability against outages would be
		// checked
		// The following checks scheduled OP to see if it is too large
		ScheduleFileData sch = Util.getSchedFileData();
		HashMap<String, DlyData> dlyDataMap = sch.getDlyDataMap();
		// System.out.println("ActGenPower3: " + sch.getDlyDataMap().get("250419_2").getActGenPower());
		String dayKeyNow = Util.getCalendarYYMMDD(day);

		// Check the reschedule data values
		// FTPK reschedule check
		boolean ftpkReschedCheck = ((dlyDataMap.get(dayKeyNow + "_0")).getRescheduleOpNum() > 0.5) ? true : false;
		// GARR reschedule check
		boolean garrReschedCheck = ((dlyDataMap.get(dayKeyNow + "_1")).getRescheduleOpNum() > 0.5) ? true : false;
		// FTRA reschedule check
		boolean ftraReschedCheck = ((dlyDataMap.get(dayKeyNow + "_4")).getRescheduleOpNum() > 0.5) ? true : false;
		// GAPT reschedule check
		boolean gaptReschedCheck = ((dlyDataMap.get(dayKeyNow + "_5")).getRescheduleOpNum() > 0.5) ? true : false;

		// remove oahe rescheduled Q's if other reschedule is eliminated
		(dlyDataMap.get(dayKeyNow + "_2")).setRescheduleOP("0");

		// remove bend rescheduled Q's if other reschedule is eliminated
		(dlyDataMap.get(dayKeyNow + "_3")).setRescheduleOP("0");

		ScheduleFileData sfd = Util.getSchedFileData();
		HashMap<String, String[]> out = sfd.getOutageMap();

		// FTPK outages
		if (null == out.get(dayKeyNow)[0]) {
			fb = accumPercentAllUnitOut(Double.parseDouble("0.0"), 0);
		} else {
			fb = accumPercentAllUnitOut(Double.parseDouble(out.get(dayKeyNow)[0]), 0);
		}
		// note discharge of 16 should actualy vary against pool level
		// if(data[rset][27+ftpkReschedCheck]>16*(1-fb))
		if (ftpkReschedCheck) {
			if (((dlyDataMap.get(dayKeyNow + "_0")).getRescheduleOpNum()) > 16 * (1 - fb)) {
				// The dam name is passed in - FTPK GARR OAHE BEND FTRA GAPT
				showErrorMessage("FTPK");
			}
		} else {
			if (((dlyDataMap.get(dayKeyNow + "_0")).getKcfsNum()) > 16 * (1 - fb)) {
				// The dam name is passed in - FTPK GARR OAHE BEND FTRA GAPT
				showErrorMessage("FTPK");
			}
		}

		// GARR outages
		if (null == out.get(dayKeyNow)[1]) {
			fb = accumPercentAllUnitOut(Double.parseDouble("0.0"), 1);
		} else {
			fb = accumPercentAllUnitOut(Double.parseDouble(out.get(dayKeyNow)[1]), 1);
		}
		// if(data[rset][30+garrReschedCheck]>38*(1-fb))
		if (garrReschedCheck) {
			if (((dlyDataMap.get(dayKeyNow + "_1")).getRescheduleOpNum()) > 38 * (1 - fb)) {
				showErrorMessage("GARR");
			}
		} else {
			if (((dlyDataMap.get(dayKeyNow + "_1")).getKcfsNum()) > 38 * (1 - fb)) {
				showErrorMessage("GARR");
			}
		}

		// FTRA outages
		if (null == out.get(dayKeyNow)[4]) {
			fb = accumPercentAllUnitOut(Double.parseDouble("0.0"), 4);
		} else {
			if ((out.get(dayKeyNow)[4]).trim().length() > 0) {
				fb = accumPercentAllUnitOut(Double.parseDouble(out.get(dayKeyNow)[4]), 4);
			} else {
				fb = accumPercentAllUnitOut(Double.parseDouble("0.0"), 4);
			}
		}
		if (ftraReschedCheck) {
			if (((dlyDataMap.get(dayKeyNow + "_4")).getRescheduleOpNum()) > 44.5 * (1 - fb)) {
				showErrorMessage("FTRA");
			}
		} else {
			if (((dlyDataMap.get(dayKeyNow + "_4")).getKcfsNum()) > 44.5 * (1 - fb)) {
				showErrorMessage("FTRA");
			}
		}

		// GAPT outages
		if (null == out.get(dayKeyNow)[5]) {
			fb = accumPercentAllUnitOut(Double.parseDouble("0.0"), 5);
		} else {
			if ((out.get(dayKeyNow)[5]).trim().length() > 0) {
				fb = accumPercentAllUnitOut(Double.parseDouble(out.get(dayKeyNow)[5]), 5);
			} else {
				fb = accumPercentAllUnitOut(Double.parseDouble("0.0"), 5);
			}
		}
		if (gaptReschedCheck) {
			if (((dlyDataMap.get(dayKeyNow + "_5")).getRescheduleOpNum()) > 35 * (1 - fb)) {
				showErrorMessage("GAPT");
			}
		} else {
			if (((dlyDataMap.get(dayKeyNow + "_5")).getKcfsNum()) > 35 * (1 - fb)) {
				showErrorMessage("GAPT");
			}
		}

		// Use reschedule total energy else scheduled total energy
		fc = (Double.valueOf(sfd.getTotReschedCalEnrgyMap().get(dayKeyNow)) > 0.1)
				? Double.valueOf((sfd.getTotReschedCalEnrgyMap().get(dayKeyNow)))
				: Double.valueOf(sfd.getTotCalEnrgyMap().get(dayKeyNow));

		// Note If k=0 then ge set equal to zero
		// FTPK GE
		// data[rset][25 + ftpkReschedCheck] = (data[rset][73] > 0.5) ? 50 *
		// ((int)((data[rset][27 + ftpkReschedCheck] * data[rset][73] / 50) +
		// .5)) : 0.0;
		// 18 + 25 = 43 this is the reschedule power value
		Double ftpkResched = -0.0;
		if (ftpkReschedCheck) {
			ftpkResched = ((Double.valueOf(sfd.getCoefficientMap().get(dayKeyNow)[0])) > 0.5)
					? ((50 * (int) (((dlyDataMap.get(dayKeyNow + "_0")).getRescheduleOpNum()
							* Double.valueOf(sfd.getCoefficientMap().get(dayKeyNow)[0]) / 50) + .5)))
					: 0.0;
			(dlyDataMap.get(dayKeyNow + "_0")).setRescheduleEnergy(ftpkResched.toString());
		} else {
			ftpkResched = ((Double.valueOf(sfd.getCoefficientMap().get(dayKeyNow)[0])) > 0.5)
					? ((50 * (int) (((dlyDataMap.get(dayKeyNow + "_0")).getKcfsNum()
							* Double.valueOf(sfd.getCoefficientMap().get(dayKeyNow)[0]) / 50) + .5)))
					: 0.0;
			(dlyDataMap.get(dayKeyNow + "_0")).setActGenPower(ftpkResched.toString());
		}
		fc = fc - ftpkResched;

		// GARR GE
		// data[rset][28+garrReschedCheck]=(data[rset][74]>0.5) ?
		// 50*((int)((data[rset][30+garrReschedCheck]*data[rset][74]/50)+.5)) :
		// 0.0;
		Double garrResched = -0.0;
		if (garrReschedCheck) {
			garrResched = ((Double.valueOf(sfd.getCoefficientMap().get(dayKeyNow)[1])) > 0.5)
					? ((50 * (int) (((dlyDataMap.get(dayKeyNow + "_1")).getRescheduleOpNum()
							* Double.valueOf(sfd.getCoefficientMap().get(dayKeyNow)[1]) / 50) + .5)))
					: 0.0;
			(dlyDataMap.get(dayKeyNow + "_1")).setRescheduleEnergy(garrResched.toString());
		} else {
			garrResched = ((Double.valueOf(sfd.getCoefficientMap().get(dayKeyNow)[1])) > 0.5)
					? ((50 * (int) (((dlyDataMap.get(dayKeyNow + "_1")).getKcfsNum()
							* Double.valueOf(sfd.getCoefficientMap().get(dayKeyNow)[1]) / 50) + .5)))
					: 0.0;
			(dlyDataMap.get(dayKeyNow + "_1")).setActGenPower(garrResched.toString());
		}
		fc = fc - garrResched;

		// GAPT GE
		// data[rset][40+gaptReschedCheck]=(data[rset][78]>0.5) ?
		// 50*((int)((data[rset][42+gaptReschedCheck]*data[rset][78]/50)+.5)) :
		// 0.0;
		Double gaptResched = -0.0;
		if (gaptReschedCheck) {
			gaptResched = ((Double.valueOf(sfd.getCoefficientMap().get(dayKeyNow)[5])) > 0.5)
					? ((50 * (int) (((dlyDataMap.get(dayKeyNow + "_5")).getRescheduleOpNum()
							* Double.valueOf(sfd.getCoefficientMap().get(dayKeyNow)[5]) / 50) + .5)))
					: 0.0;
			(dlyDataMap.get(dayKeyNow + "_5")).setRescheduleEnergy(gaptResched.toString());
		} else {
			gaptResched = ((Double.valueOf(sfd.getCoefficientMap().get(dayKeyNow)[5])) > 0.5)
					? ((50 * (int) (((dlyDataMap.get(dayKeyNow + "_5")).getKcfsNum()
							* Double.valueOf(sfd.getCoefficientMap().get(dayKeyNow)[5]) / 50) + .5)))
					: 0.0;
			(dlyDataMap.get(dayKeyNow + "_5")).setActGenPower(gaptResched.toString());
		}
		// power needed to be supplied by bend and oahe
		fc = fc - gaptResched;
		boolean allPlantRescheduled = false;

		// If FTRA OP compute rest
		if ((dlyDataMap.get(dayKeyNow + "_4")).getKcfsNum() > 0.5) {
			d3 = d4 = 0;
			// If any other plants being rescheduled then Oahe - Bend reschedule
			// also
			if (ftpkReschedCheck || garrReschedCheck || ftraReschedCheck || gaptReschedCheck) {
				d3 = d4 = 18;
				allPlantRescheduled = true;
			}

			// FTRA GE
			// data[rset][37+ftraReschedCheck]=((Double.valueOf(sfd.getCoefficientMap().get(dayKey)[4]))
			// > 0.5) ?
			// 50*((int)((data[rset][39+ftraReschedCheck]*(Double.valueOf(sfd.getCoefficientMap().get(dayKey)[4]))/50)+.5))
			// : 0.0;
			Double ftraResched = -0.0;
			if (ftraReschedCheck) {
				// data[rset][37+ftraReschedCheck]=((Double.valueOf(sfd.getCoefficientMap().get(dayKey)[4]))
				// > 0.5) ?
				// 50*((int)((data[rset][39+ftraReschedCheck]*(Double.valueOf(sfd.getCoefficientMap().get(dayKey)[4]))/50)+.5))
				// : 0.0;
				ftraResched = ((Double.valueOf(sfd.getCoefficientMap().get(dayKeyNow)[4])) > 0.5)
						? ((50 * (int) (((dlyDataMap.get(dayKeyNow + "_4")).getRescheduleOpNum()
								* Double.valueOf(sfd.getCoefficientMap().get(dayKeyNow)[4]) / 50) + .5)))
						: 0.0;
				(dlyDataMap.get(dayKeyNow + "_4")).setRescheduleEnergy(ftraResched.toString());
			} else {
				ftraResched = ((Double.valueOf(sfd.getCoefficientMap().get(dayKeyNow)[4])) > 0.5)
						? ((50 * (int) (((dlyDataMap.get(dayKeyNow + "_4")).getKcfsNum()
								* Double.valueOf(sfd.getCoefficientMap().get(dayKeyNow)[4]) / 50) + .5)))
						: 0.0;
				(dlyDataMap.get(dayKeyNow + "_4")).setActGenPower(ftraResched.toString());
			}

			if (ftraReschedCheck) {
				fc = fc - (dlyDataMap.get(dayKeyNow + "_4")).getRescheduleEnergyNum();
			} else {
				fc = fc - (dlyDataMap.get(dayKeyNow + "_4")).getActGenPowerNum();
			}

			fa = 0;
			// TODO
			// a = rset - 1;
			// Get Big Bend pool elevation
			// while (fa < 0.01 && a > 0) {
			Calendar aday = Util.getCalendarYYMMDD(dayKeyNow);

			while (fa < 0.01) {
				// TODO 1696
				fa = (dlyDataMap.get(Util.getCalendarYYMMDD(aday) + "_3")).getPoolElevationNum();
				// System.out.println("fa: " + fa);
				aday.add(Calendar.DAY_OF_MONTH, -1);
				// fa=data[a--][16];
			}

			HashMap<String, String> reschedbbElevMap = Util.getSchedFileData().getRescheduleBBElevMap();
			HashMap<String, String> bbElevMap = Util.getSchedFileData().getBbElevMap();
			// table lookup for bend vol
			for (i = 0; fa > bendvol[1][i]; i++)
				;

			// use reschedule bend incremental elev. if present
			int b = ((Double.valueOf(reschedbbElevMap.get(dayKeyNow)).doubleValue() > 0.001) ? 0 : 3);

			// b=( data[rset][44]>0.001 ) ? 0 : 3; /* use reschedule bend
			// incremental elev. if present */
			if (i == 0) {
				// fb=25*data[rset][44+b];
				if (Double.valueOf(reschedbbElevMap.get(dayKeyNow)).doubleValue() > 0.001) {
					// reschedule BB elevation
					fb = 25 * Double.valueOf(reschedbbElevMap.get(dayKeyNow)).doubleValue();
				} else {
					// BB elevation
					fb = 25 * Double.valueOf(bbElevMap.get(dayKeyNow)).doubleValue();
				}
			} else {
				if (Double.valueOf(reschedbbElevMap.get(dayKeyNow)).doubleValue() > 0.001) {
					// Use Big Bend reschedule elevation storage equivalent
					// discharge - 1.9835 conversion acft to cfs
					fb = (bendvol[0][i] - bendvol[0][i - 1])
							* Double.valueOf(reschedbbElevMap.get(dayKeyNow)).doubleValue() / ACFT_TO_CFS;
				} else {
					// Use Big Bend elevation storage equivalent discharge -
					// 1.9835 cobversion acft to cfs
					fb = (bendvol[0][i] - bendvol[0][i - 1]) * Double.valueOf(bbElevMap.get(dayKeyNow)).doubleValue()
							/ ACFT_TO_CFS; // 0.0
				}
			}

			HashMap<String, String[]> coefficients = Util.getSchedFileData().getCoefficientMap();
			String[] coefficient = coefficients.get(dayKeyNow);
			// int count = 0;
			// while (count < coefficient.length) {
			// count++;
			// }

			// Special case one or both shut off (OAHE, BEND)
			if (Double.valueOf(coefficient[2]).doubleValue() < 0.5
					|| Double.valueOf(coefficient[3]).doubleValue() < 0.5) {
				// OAHE
				if (Double.valueOf(coefficient[2]).doubleValue() < 0.5)
					if (allPlantRescheduled) {
						// reschedule +18
						// data[rset][31+d3]=data[rset][33+d3]=0.0;
						// OAHE Reschedule Power
						dlyDataMap.get(dayKeyNow + "_2").setRescheduleEnergy("0.0");
						// OAHE KCFS
						dlyDataMap.get(dayKeyNow + "_2").setRescheduleOP("0.0");
					} else {
						// +0
						// data[rset][31+d3]=data[rset][33+d3]=0.0;
						// OAHE CFS
						dlyDataMap.get(dayKeyNow + "_2").setKcfs("0.0");
						// OAHE Energy
						dlyDataMap.get(dayKeyNow + "_2").setActGenPower("0.0");
					}
				else {
					if (allPlantRescheduled) {
						// d3 = 18 ~ 51 && 49
						// OAHE Reschedule Power
						dlyDataMap.get(dayKeyNow + "_2")
								.setRescheduleEnergy(String.valueOf(fc / Double.valueOf(coefficient[2]).doubleValue()));
						// OAHE KCFS
						// OAHE ge if BEND is off
						dlyDataMap.get(dayKeyNow + "_2").setRescheduleOP(String.valueOf(
								50 * ((int) (((dlyDataMap.get(dayKeyNow + "_2").getRescheduleEnergyNum().doubleValue()
										* Double.valueOf(coefficient[2]).doubleValue() / 50.0) + 0.5)))));
						fa = dlyDataMap.get(dayKeyNow + "_3").getPoolElevationNum()
								+ dlyDataMap.get(dayKeyNow + "_2").getRescheduleOpNum() * 0.1 / 3.0;
					} else {
						// d3 = 0
						dlyDataMap.get(dayKeyNow + "_2")
								.setKcfs(Double.toString(fc / Double.valueOf(coefficient[2]).doubleValue()));

						// OAHE ge if BEND is off
						dlyDataMap.get(dayKeyNow + "_2").setActGenPower(
								Double.toString(50 * ((int) (((dlyDataMap.get(dayKeyNow + "_2").getKcfsNum()
										* Double.valueOf(coefficient[2]).doubleValue() / 50) + .5)))));

						fa = dlyDataMap.get(dayKeyNow + "_3").getPoolElevationNum()
								+ dlyDataMap.get(dayKeyNow + "_2").getKcfsNum() * 0.1 / 3.0;
					}
				}
				if (Double.valueOf(coefficient[3]).doubleValue() < 0.5)
					if (allPlantRescheduled) {
						// d4 = 18
						dlyDataMap.get(dayKeyNow + "_2").setRescheduleEnergy("0.0");
						dlyDataMap.get(dayKeyNow + "_3").setRescheduleOP("0.0");
						// data[rset][34+d4]=data[rset][36+d4]=0.0;
					} else {
						// d4 = 0
						dlyDataMap.get(dayKeyNow + "_3").setActGenPower("0.0");
						dlyDataMap.get(dayKeyNow + "_3").setKcfs("0.0");
						// data[rset][34+d4]=data[rset][36+d4]=0.0;
					}
				else {
					if (allPlantRescheduled) {
						// d4 = 18 //36+18=54
						// bend op = larger of oahe op or od
						dlyDataMap.get(dayKeyNow + "_3")
								.setRescheduleOP((dlyDataMap.get(dayKeyNow + "_2").getODNum() > 0.0)
										? dlyDataMap.get(dayKeyNow + "_2").getOD() : "0.0");
						// data[rset][36+d4]=(data[rset][32]>0.0) ?
						// data[rset][32] : 0.0;

						// 36+18=54, 33+18=51
						dlyDataMap.get(dayKeyNow + "_3")
								.setRescheduleOP((dlyDataMap.get(dayKeyNow + "_2").getRescheduleOpNum() > dlyDataMap
										.get(dayKeyNow + "_3").getRescheduleOpNum())
												? dlyDataMap.get(dayKeyNow + "_2").getRescheduleOP()
												: dlyDataMap.get(dayKeyNow + "_3").getRescheduleOP());

						// data[rset][36+d4]=(data[rset][33+d3]>data[rset][36+d4])
						// ? data[rset][33+d3] :data[rset][36+d4];
						// bend power release
						dlyDataMap.get(dayKeyNow + "_3").setRescheduleOP(
								Double.toString(dlyDataMap.get(dayKeyNow + "_3").getRescheduleOpNum() - fb));
						// data[rset][36+d4]=data[rset][36+d4] - fb;

						// bend ge if oahe off
						// 36+18=54 34+18=52
						dlyDataMap.get(dayKeyNow + "_3").setRescheduleEnergy(
								Double.toString(50 * ((int) ((dlyDataMap.get(dayKeyNow + "_3").getRescheduleOpNum()
										* Double.valueOf(coefficient[3]).doubleValue() / 50) + .5))));
						// data[rset][34+d4]=50*((int)((data[rset][36+d4]*Double.valueOf(coefficient[3]).doubleValue()/50)+.5));
					} else {
						// bend op = larger of oahe op or od
						dlyDataMap.get(dayKeyNow + "_3").setKcfs((dlyDataMap.get(dayKeyNow + "_2").getODNum() > 0.0)
								? dlyDataMap.get(dayKeyNow + "_2").getOD() : "0.0");
						// data[rset][36+d4]=(data[rset][32]>0.0) ?
						// data[rset][32] : 0.0;
						dlyDataMap.get(dayKeyNow + "_3")
								.setKcfs((dlyDataMap.get(dayKeyNow + "_2").getKcfsNum() > dlyDataMap
										.get(dayKeyNow + "_3").getKcfsNum())
												? dlyDataMap.get(dayKeyNow + "_2").getKcfs()
												: dlyDataMap.get(dayKeyNow + "_3").getKcfs());
						// data[rset][36+d4]=(data[rset][33+d3]>data[rset][36+d4])
						// ? data[rset][33+d3] :data[rset][36+d4];
						// bend power release
						dlyDataMap.get(dayKeyNow + "_3")
								.setKcfs(Double.toString(dlyDataMap.get(dayKeyNow + "_3").getKcfsNum() - fb));
						// data[rset][36+d4]=data[rset][36+d4] - fb;
						// bend ge if oahe off
						dlyDataMap.get(dayKeyNow + "_3").setActGenPower(
								Integer.toString(60 * ((int) ((dlyDataMap.get(dayKeyNow + "_3").getKcfsNum()
										* Double.valueOf(coefficient[3]).doubleValue() / 60) + .5))));
						// data[rset][34+d4]=50*((int)((data[rset][36+d4]*Double.valueOf(coefficient[3]).doubleValue()/50)+.5));
					}
				}
			} else {
				System.out.println("Usual case compute oahe bend relationship, day: " + Util.getCalendarYYMMDD(day));
				// Usual case compute oahe bend relationship
				// bend discharge
				fa = ((fc - fb * Double.valueOf(coefficient[2]))
						/ (Double.valueOf(coefficient[2]) + Double.valueOf(coefficient[3])));
				// fa=(fc-fb*data[rset][75])/(data[rset][75]+data[rset][76]);
				if (fa >= 0) {
					if (allPlantRescheduled) {
						System.out.println("allPlantRescheduled: " + allPlantRescheduled);
						// 36+18=54 34+18=52
						dlyDataMap.get(dayKeyNow + "_3")
								.setRescheduleOP(Float.toString((float) ((int) ((fa * 2) + .5)) / 2));
						System.out.println("if allPlantRescheduled bend RescheduleOP: "+(dlyDataMap.get(dayKeyNow + "_3").getRescheduleOP()));
						// data[rset][36+d4]=(float)((int)((fa*2)+.5))/2;
						// bend ge
						dlyDataMap.get(dayKeyNow + "_3").setRescheduleEnergy(
								Integer.toString(60 * ((int) (((dlyDataMap.get(dayKeyNow + "_3").getRescheduleOpNum()
										* Double.valueOf(coefficient[3]).doubleValue() / 60) + .5)))));
						// data[rset][34+d4]=50*((int)((data[rset][36+d4]*data[rset][76]/50)+.5));
						System.out.println("allPlantRescheduledbend - new bend getRescheduleEnergy: " + dlyDataMap.get(dayKeyNow + "_3").getRescheduleEnergy());
					} else {
						System.out.println("allPlantRescheduled else");
						// 36+0=36 34+0=34
						dlyDataMap.get(dayKeyNow + "_3").setKcfs(Float.toString((float) ((int) ((fa * 2) + .5)) / 2));
						System.out.println("else bend Kcfs: "+(dlyDataMap.get(dayKeyNow + "_3").getKcfs()));
						// data[rset][36+d4]=(float)((int)((fa*2)+.5))/2;
						// bend ge
						
						dlyDataMap.get(dayKeyNow + "_3").setActGenPower(
								Integer.toString(60 * ((int) (((dlyDataMap.get(dayKeyNow + "_3").getKcfsNum()
										* Double.valueOf(coefficient[3]).doubleValue() / 60) + .5)))));
						// data[rset][34+d4]=50*((int)((data[rset][36+d4]*data[rset][76]/50)+.5));
						System.out.println("else bend ActGenPower: "+(dlyDataMap.get(dayKeyNow + "_3").getActGenPower()));
					}
				} else { //Added this else to handle negative bend discharge
					if (allPlantRescheduled) {
						System.out.println("else allPlantRescheduled2 negative bend discharge: " + allPlantRescheduled);
						// 36+18=54 34+18=52
						dlyDataMap.get(dayKeyNow + "_3").setKcfs(Float.toString((float) ((int) ((fa * 2) - .5)) / 2));
						System.out.println("if bend Kcfs: "+(dlyDataMap.get(dayKeyNow + "_3").getKcfs()));
						// data[rset][36+d4]=(float)((int)((fa*2)-.5))/2;
						// bend ge  Changed to 60
						dlyDataMap.get(dayKeyNow + "_3").setRescheduleEnergy(
								Integer.toString(60 * ((int) ((dlyDataMap.get(dayKeyNow + "_3").getRescheduleOpNum()
										* Double.valueOf(coefficient[3]).doubleValue() / 60) - .5))));
						System.out.println("bend RescheduleEnergy: "+(dlyDataMap.get(dayKeyNow + "_3").getRescheduleEnergy()));
						// data[rset][34+d4]=50*((int)((data[rset][36+d4]*data[rset][76]/50)-.5));
					} else {
						System.out.println("else allPlantRescheduled2: " + allPlantRescheduled);
						// 36+0=36 34+0=34
						// String val = Float.toString((float) ((int) ((fa * 2)
						// - .5)) / 2);
						dlyDataMap.get(dayKeyNow + "_3").setKcfs(Float.toString((float) ((int) ((fa * 2) - .5)) / 2));
						System.out.println("else bend Kcfs: "+(dlyDataMap.get(dayKeyNow + "_3").getKcfs()));
						// data[rset][36+d4]=(float)((int)((fa*2)-.5))/2;
						// bend ge
						// String val2 = Integer.toString(50 * ((int)
						// ((dlyDataMap.get(dayKeyNow + "_3")
						// .getKcfsNum() *
						// Double.valueOf(coefficient[3]).doubleValue() / 50) -
						// .5)));  Changed to 60
						dlyDataMap.get(dayKeyNow + "_3").setActGenPower(
								Integer.toString(60 * ((int) ((dlyDataMap.get(dayKeyNow + "_3").getKcfsNum()
										* Double.valueOf(coefficient[3]).doubleValue() / 60) - .5))));
						System.out.println("else bend ActGenPower: "+(dlyDataMap.get(dayKeyNow + "_3").getActGenPower()));
						// data[rset][34+d4]=50*((int)((data[rset][36+d4]*data[rset][76]/50)-.5));
						// /* bend ge */
					}
				}
				if (allPlantRescheduled) {
					System.out.println("if allPlantRescheduled3: " + allPlantRescheduled);
					// Oahe discharge
					// 33+18=51, 34+18=52
					dlyDataMap.get(dayKeyNow + "_2")
							.setRescheduleOP(Float.toString((float) ((int) (((fa + fb) * 2) + .5)) / 2));	
					System.out.println("if allPlantRescheduled3 RescheduleOP: " + dlyDataMap.get(dayKeyNow + "_2").getRescheduleOP());
					// data[rset][33+d3]=(float)((int)(((fa + fb)*2)+.5))/2; /*
					// Oahe discharge */
					// oahe ge
					dlyDataMap.get(dayKeyNow + "_2").setRescheduleEnergy(
							Double.toString(fc - dlyDataMap.get(dayKeyNow + "_3").getRescheduleEnergyNum()));
					// data[rset][31+d3]=fc - data[rset][34+d4]; /* oahe ge */
					System.out.println("allPlantRescheduled - oahe new getRescheduleEnergy: " + dlyDataMap.get(dayKeyNow + "_2").getRescheduleEnergy());
				} else {
					System.out.println("if allPlantRescheduled3 else ");
					// Oahe discharge
					dlyDataMap.get(dayKeyNow + "_2").setKcfs(Float.toString((float) ((int) (((fa + fb) * 2) + .5)) / 2));
					System.out.println("if allPlantRescheduled3 else Oahe Kcfs: " + dlyDataMap.get(dayKeyNow + "_2").getKcfs());
					// data[rset][33+d3]=(float)((int)(((fa + fb)*2)+.5))/2; /*
					// Oahe discharge */
					// oahe ge
					dlyDataMap.get(dayKeyNow + "_2").setActGenPower(Double.toString(fc - dlyDataMap.get(dayKeyNow + "_3").getActGenPowerNum()));
					// data[rset][31+d3]=fc - data[rset][34+d4]; /* oahe ge */
					System.out.println("allPlantRescheduled3 else oahe ActGenPower: "+(dlyDataMap.get(dayKeyNow + "_2").getActGenPower()));
				}
			}
		}

		// Outages
		HashMap<String, String[]> outages = sch.getOutageMap();
		String[] outageData = null;
		if (outages.containsKey(dayKeyNow)) {
			outageData = outages.get(dayKeyNow);
			int count = 0;
			while (outageData.length > count) {
				if (null == outageData[count]) {
					outageData[count] = "0";
				}
				count++;
			}
		}
		// oahe
		fb = accumPercentAllUnitOut((int) Double.parseDouble(outageData[2]), 2);
		// fb=accumPercentAllUnitOut(data[rset][63],2); /* oahe */

		if (dlyDataMap.get(dayKeyNow + "_2").getRescheduleOpNum() > 54 * (1 - fb)
				|| dlyDataMap.get(dayKeyNow + "_2").getKcfsNum() > 54 * (1 - fb)) {
			// The dam number is passed in - FTPK GARR OAHE BEND FTRA GAPT
			warningMessage(2);
		}
		// bend
		fb = accumPercentAllUnitOut((int) Double.parseDouble(outageData[3]), 3);
		// fb = accumPercentAllUnitOut(data[rset][64], 3); /* bend */
		if (dlyDataMap.get(dayKeyNow + "_3").getRescheduleOpNum() > 103 * (1 - fb)
				|| dlyDataMap.get(dayKeyNow + "_3").getKcfsNum() > 103 * (1 - fb)) {
			// The dam number is passed in - FTPK GARR OAHE BEND FTRA GAPT
			warningMessage(3);
		}
		// this blanks reschedule ge if reschedule op has been changed to zero
		for (i = 0; i < 6; i++) {
			if (dlyDataMap.get(dayKeyNow + "_" + i).getRescheduleOpNum() < 0.5) {
				// if (data[rset][i * 3 + 45] < 0.5)
				dlyDataMap.get(dayKeyNow + "_" + i).setRescheduleEnergy("0");
				// data[rset][i * 3 + 43] = 0;
			}
		}
		sch.setDlyDataMap(dlyDataMap);
		Util.setSchedFileData(sch);
		// System.out.println("ActGenPower2: " + sch.getDlyDataMap().get("250419_2").getActGenPower());
	}

	private void warningMessage(int site) {
		String siteName;
		switch (site) {
		case 0:
			siteName = "FTPK";
			break;
		case 1:
			siteName = "GARR";
			break;
		case 2:
			siteName = "OAHE";
			break;
		case 3:
			siteName = "BEND";
			break;
		case 4:
			siteName = "FTRA";
			break;
		case 5:
			siteName = "GAPT";
			break;
		default:
			siteName = "Invalid site";
		}
		JOptionPane.showConfirmDialog(this, "The power plant release (OP) appears too large for " + siteName, "alert",
				-1);
	}

	private double accumPercentAllUnitOut(double damOutages, int dnum) {
		double fa = 0.0D;
		damOutages += 0.1D;
		if (damOutages > 9999999.0D) {
			int b = (int) (damOutages / 1.0E7D);
			damOutages -= 10000000 * b;
			fa += getPercentEachUnit(b, dnum);
		}
		if (damOutages > 999999.0D) {
			int b = (int) (damOutages / 1000000.0D);
			damOutages -= 1000000 * b;
			fa += getPercentEachUnit(b, dnum);
		}
		if (damOutages > 99999.0D) {
			int b = (int) (damOutages / 100000.0D);
			damOutages -= 100000 * b;
			fa += getPercentEachUnit(b, dnum);
		}
		if (damOutages > 9999.0D) {
			int b = (int) (damOutages / 10000.0D);
			damOutages -= 10000 * b;
			fa += getPercentEachUnit(b, dnum);
		}
		if (damOutages > 999.0D) {
			int b = (int) (damOutages / 1000.0D);
			damOutages -= 1000 * b;
			fa += getPercentEachUnit(b, dnum);
		}
		if (damOutages > 99.0D) {
			int b = (int) (damOutages / 100.0D);
			damOutages -= 100 * b;
			fa += getPercentEachUnit(b, dnum);
		}
		if (damOutages > 9.0D) {
			int b = (int) (damOutages / 10.0D);
			damOutages -= 10 * b;
			fa += getPercentEachUnit(b, dnum);
		}
		if (damOutages > 0.2D) {
			fa += getPercentEachUnit((int) damOutages, dnum);
		}
		return fa;
	}

	private double getPercentEachUnit(int a, int dnum) {
		double num = 0.0D;
		if (dnum == 0) {
			if ((a == 1) || (a == 3)) {
				num = 0.23D;
			}
			if (a == 2) {
				num = 0.1D;
			} else {
				num = 0.22D;
			}
		}
		if (dnum == 1) {
			num = 0.2D;
		}
		if (dnum == 2) {
			num = 0.143D;
		}
		if ((dnum == 3) || (dnum == 4)) {
			num = 0.125D;
		}
		if (dnum == 5) {
			num = 0.333D;
		}
		return num;
	}

	protected void setEnergyEndDate(String energyendDate) {
		energyEndDate = energyendDate;
	}

	protected String getEnergyEndDate() {
		return energyEndDate;
	}

	private void setSchedReschedTable(JTable srt) {
		schedReschedTable = srt;
	}

	protected JTable getSchedReschedTable() {
		return schedReschedTable;
	}

	protected void setSites(Vector<String> ste) {
		sites = ste;
	}

	protected static Vector<String> getSites() {
		return sites;
	}

	protected void setDlyDataFile(HashMap<String, Vector<DlyData>> df) {
		dlyDataFile = df;
	}

	protected HashMap<String, Vector<DlyData>> getDlyDataFile() {
		return dlyDataFile;
	}

	protected void setStartDay(String start) {
		startDay = start;
	}

	protected String getStartDay() {
		return startDay;
	}

	protected void setSchfileEndDate(String end) {
		schfileEndDate = end;
	}

	protected String getSchfileEndDate() {
		return schfileEndDate;
	}

	private void setCurrentRescheduleDate(Calendar crd) {
		currentRescheduleDate = crd;
	}

	private Calendar getCurrentRescheduleDate() {
		return currentRescheduleDate;
	}

	private DecimalFormat getFormatNoDecimal() {
		return formatNoDecimal;
	}

	private DecimalFormat getFormatOneDecimal() {
		return formatOneDecimal;
	}

	private int getReschedTableHeaderRow() {
		return reschedTableHeaderRow;
	}

	private void setSchfileStartDate(String sed) {
		schfileStartDate = sed;
	}

	private String getSchfileStartDate() {
		return schfileStartDate;
	}

	private DecimalFormat getFormatTwoDecimal() {
		return formatTwoDecimal;
	}

	private DecimalFormat getFormatThreeDecimal() {
		return formatThreeDecimal;
	}

	/**
	 * 
	 * @param e
	 */
	private void exitButton_actionPerformed(ActionEvent e) {
		if (getSchedReschedTable().getCellEditor() != null) {
			getSchedReschedTable().getCellEditor().stopCellEditing();
		}
		updateGapt24IdData(getCurrentRescheduleDate());
		setVisible(false);
	}

	private int getReschOPRow() {
		return reschOPRow;
	}

	private int getGeOPsRow() {
		return geOPsRow;
	}

	private int getCoeffRow() {
		return coeffRow;
	}

	private int getOutagesRow() {
		return outagesRow;
	}

	private int getOdsRow() {
		return odsRow;
	}

	private int getDamnSitesRow() {
		return damnSitesRow;
	}

	private int getColumnZero() {
		return columnZero;
	}

	private int getColumnOne() {
		return columnOne;
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

	private int getColumnSix() {
		return columnSix;
	}

	private int getColumnEight() {
		return columnEight;
	}

	private int getColumnNine() {
		return columnNine;
	}

	private void setCapToleranceInfo(String[] cti) {
		capToleranceInfo = cti;
	}

	protected static String[] getCapToleranceInfo() {
		return capToleranceInfo;
	}

	private HashMap<String, Vector<String>> getScheduleData() {
		if (scheduleData == null) {
			return scheduleData = new HashMap<String, Vector<String>>();
		}
		return scheduleData;
	}

	private void computeButton_actionPerformed(ActionEvent e) {
		if (getSchedReschedTable().getCellEditor() != null) {
			getSchedReschedTable().getCellEditor().stopCellEditing();
		}
		Calendar now = getCurrentRescheduleDate();
		//TODO update Scheduled releases
		calculateEnergy();
		compute(now);
		updateODInformation();
		updateCoefficientInformation();
		updateGeTotalInformation();
		updateGEInformation();
		updateBbElevInformation();
		update24IDInformation();
		updateRescheduleOPTotalInformation();
		updateReschedOPInformation();
		setSchedReschedTableData(getSchedReschedTable(), now, true);

		loadEnergyTable(getSchedReschedTable());
		loadWaterScheduleTable(getSchedReschedTable());
		updateCapacityToleranceInfo();
	}

	private void clearButton_actionPerformed(ActionEvent e) {
		if (getSchedReschedTable().getCellEditor() != null) {
			getSchedReschedTable().getCellEditor().stopCellEditing();
		}
		setUpdateFlag(true);
		Calendar now = Util.getDate();
		int today = now.get(5);
		int day = Integer
				.parseInt(getSchedReschedTable().getValueAt(19, getColumnFive()).toString().substring(0, 2).trim());
		if (day < today) {
			now.add(2, 1);
		}
		now.set(5, day);
		clearScheduleTableInfo();
		removeScheduleFileData(now);

		updateODInformation();
		updateCoefficientInformation();
		updateGeTotalInformation();
		updateGEInformation();
		updateBbElevInformation();
		update24IDInformation();
		updateRescheduleOPTotalInformation();
		updateReschedOPInformation();
		setSchedReschedTableData(getSchedReschedTable(), now, true);

		loadEnergyTable(getSchedReschedTable());
		loadWaterScheduleTable(getSchedReschedTable());
	}

	private void removeScheduleFileData(Calendar cal) {
		ScheduleFileData schedFile = Util.getSchedFileData();
		int record = 0;
		String daykey = Util.getDataKey(cal);
		while (record <= 6) {
			String key = daykey + "_" + record;
			if (schedFile.getDlyDataMap().containsKey(key)) {
				DlyData dly = (DlyData) schedFile.getDlyDataMap().get(key);
				dly.setActGenPower("0");
				dly.setDlyGenPower("0");
				dly.setFlowPower("0");
				dly.setFlowTotal("0");
				dly.setKcfs("0");
				dly.setOD("0");
				dly.setPoolElev("0");
				dly.setRescheduleEnergy("0");
				dly.setRescheduleOP("0");
			}
			if (schedFile.getBbElevMap().containsKey(key)) {
				schedFile.getBbElevMap().remove(key);
			}
			if (schedFile.getRescheduleBBElevMap().containsKey(key)) {
				schedFile.getRescheduleBBElevMap().remove(key);
			}
			if (schedFile.getId24Map().containsKey(key)) {
				schedFile.getId24Map().remove(key);
			}
			if (schedFile.getTotCalEnrgyMap().containsKey(key)) {
				schedFile.getTotCalEnrgyMap().remove(key);
			}
			if (schedFile.getTotReschedCalEnrgyMap().containsKey(key)) {
				schedFile.getTotReschedCalEnrgyMap().remove(key);
			}
			if (schedFile.getOutageMap().containsKey(key)) {
				schedFile.getOutageMap().remove(key);
			}
			if (schedFile.getCoefficientMap().containsKey(key)) {
				schedFile.getCoefficientMap().remove(key);
			}
			record++;
		}
	}

	/**
	 * 
	 * @param date
	 */
	private void updateGapt24IdData(Calendar date) {
		System.out.println("updateGapt24IdData " + date.getTime().toString());
		JTable table = getSchedReschedTable();
		String val = (String) table.getValueAt(getGeOPsRow(), getColumnNine());
		String currentDay = Util.getCalendarYYMMDD(date);
		Calendar now = Util.getDate();
		System.out.println("GAPT 24 ID data for " + currentDay + " to " + val);

		ScheduleFileData sch = Util.getSchedFileData();
		HashMap<String, String> gapt24IdData = sch.getId24Map();
		if (((date.before(now)) || (date.equals(now))) && (val != null) && (val.length() > 0)) {
			gapt24IdData.put(currentDay, val);
		}
	}

	/**
	 * 
	 */
	private void updateCapacityToleranceInfo() {
		updatcap();

		ScheduleFileData sch = Util.getSchedFileData();

		String startDate = getSchfileStartDate();
		String dlyEndDate = sch.getDailyEndDate();
		Calendar strtDay = Util.getCalendarDateYYMMDD(startDate);
		Calendar endDay = Util.getCalendarDateYYMMDD(dlyEndDate);
		Calendar end = (Calendar) endDay.clone();
		while ((end.after(strtDay)) || (end.equals(strtDay))) {
			int dayOfWeek = end.get(7);
			if (dayOfWeek == 2) {
				setMondayCapTolDate(Util.getCalendarYYMMDD(end));
			}
			if (dayOfWeek == 7) {
				setSaturdayCapTolDate(Util.getCalendarYYMMDD(end));
			}
			end.add(5, -1);
		}
		HashMap<String, List<Integer>> capabilities = getCapabilities();

		List<Integer> mondayCapTol = (List<Integer>) capabilities.get(getMondayCapTolDate());
		List<Integer> saturdayCapTol = (List<Integer>) capabilities.get(getSaturdayCapTolDate());
		String[] captol = getCapToleranceInfo();
		for (int x = 1; x < 7; x++) {
			captol[x] = String.valueOf(mondayCapTol.get(x - 1));
		}
		for (int y = 7; y < 13; y++) {
			captol[y] = String.valueOf(saturdayCapTol.get(y - 7));
		}
		setCapToleranceInfo(captol);
	}

	/**
	 * 
	 */
	private void updatcap() {
		int capability, i;
		double fa = 0;
		double cap = -1;

		HashMap<String, List<Integer>> capabilities = getCapabilities();

		ScheduleFileData sch = Util.getSchedFileData();
		HashMap<String, String[]> outages = sch.getOutageMap();
		Set<String> keys = outages.keySet();
		Iterator<String> it = keys.iterator();

		HashMap<String, DlyData> dlyDataMap = sch.getDlyDataMap();

		String dayKeyNow = null;
		while (it.hasNext()) {
			dayKeyNow = it.next();
			for (i = 0; i < 6; i++) { /* Saturday - i=damnumber - 0=ftpk */
				DlyData dlyData = dlyDataMap.get(dayKeyNow + "_" + i);

				fa = 0;
				// bend uses ftra elevs from schfile col 20 schfile
				if (i == 3) {
					// Get FTRA elevations
					dlyData = dlyDataMap.get(dayKeyNow + "_" + (i + 1));
					if (dlyData.getPoolElevationNum() > 0.2) {
						cap = findCapability(dlyData.getPoolElevationNum(), i);
					}
				} else {
					// 4, 8, 12, 20, 24 pool elevation columns schfile
					if (dlyData.getPoolElevationNum() > 0.2) {
						cap = findCapability(dlyData.getPoolElevationNum(), i);
					}
				}

				if (cap > 0) {
					String[] outage = outages.get(dayKeyNow);
					// sat night outage
					fa = accumPercentAllUnitOut(Double.parseDouble(outage[i]), i);
					if (cap > 999) { // error check
						cap = 1;
					}

					if (!capabilities.containsKey(dayKeyNow)) {
						capabilities.put(dayKeyNow, new ArrayList<Integer>());
					}

					capability = (int) (cap * (1 - fa) + .5);
					// System.out.println("1: " + dlyData.getDamName() + " - " +
					// capability + " " + dayKeyNow
					// + "_" + i);

					if (capabilities.containsKey(dayKeyNow)) {
						List<Integer> values = capabilities.get(dayKeyNow);
						if (values.contains(i)) {
							values.set(i, capability);
						} else {
							values.add(i, capability);
						}
					}
				}
				cap = -1;
			} // end for
		} // end while

		setCapabilities(capabilities);
	}

	private double findCapability(double elev, int dm) {
		double ellow = 0.0D;
		double elhigh = 0.0D;
		double caplow = 0.0D;
		double caphigh = 0.0D;

		int i = 0;
		if ((elev < 1190.0D) || (elev > 2255.0D)) {
			JOptionPane.showMessageDialog(this,
					"Elevation from monthly elevation table is not in range of\n elevation-capability table.  The bad elevation is: "
							+ elev);
		}
		HashMap<Integer, ElevationCapability> elevCap = readElevationCapabilityTable();
		ElevationCapability ec = (ElevationCapability) elevCap.get(Integer.valueOf(dm));
		List<Double> elevations = ec.getElevations();
		Object[] elcap = elevations.toArray();

		List<Double> capabilities = ec.getCapability();
		Object[] cap = capabilities.toArray();

		int k = 0;
		while (k < 1) {
			try {
				if ((ec.getSiteID() == dm) && (i >= 1) && (elev > ((Double) elcap[(i - 1)]).doubleValue())
						&& (elev <= ((Double) elcap[i]).doubleValue())) {
					ellow = ((Double) elcap[(i - 1)]).doubleValue();
					caplow = ((Double) cap[(i - 1)]).doubleValue();
					elhigh = ((Double) elcap[i]).doubleValue();
					caphigh = ((Double) cap[i]).doubleValue();
					k = 2;
				}
				i++;
			} catch (ArrayIndexOutOfBoundsException aioe) {
//				System.out.println("dm: " + dm + " Elev: " + elev);
				aioe.printStackTrace();
				break;
			}
		}
		return caplow + (caphigh - caplow) * (elev - ellow) / (elhigh - ellow);
	}

	/**
	 * Read the elevation capability table
	 * 
	 * @return
	 */
	private HashMap<Integer, ElevationCapability> readElevationCapabilityTable() {
		HashMap<Integer, ElevationCapability> dataMap = null;
		try {
			File outelcpFile = null;
			File outelcpFileMain = new File(Util.getDefaultProgramLocation() + File.separator + "OUTELCP");
			File outelcpFileBak = new File(Util.getDefaultProgramLocationBak() + File.separator + "OUTELCP");

			if (outelcpFileMain.exists()) {
				outelcpFile = outelcpFileMain;
			} else if (outelcpFileBak.exists()) {
				outelcpFile = outelcpFileBak;
			} else {
				JOptionPane.showMessageDialog(null, "Select a valid ELevation Capability Table - OUTELCP file.",
						"OUTELCP file not found", 0);
				outelcpFile = new File(Util.getFileChooserPath(null) + File.separator + "OUTELCP");
			}
			if (null != outelcpFile) {
				FileInputStream fstream = new FileInputStream(outelcpFile);
				DataInputStream in = new DataInputStream(fstream);
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				dataMap = new HashMap<Integer, ElevationCapability>();

				String location = null;
				int locationID = -9;
				if (fstream.toString().length() > 0) {
					String strLine;
					while ((strLine = br.readLine()) != null) {
						if (!strLine.startsWith("#")) {	//Avoid comments in the file
							StringTokenizer token = new StringTokenizer(strLine, " ");
							if (token.countTokens() == 1) {
								location = token.nextToken();
								if ((location.equalsIgnoreCase("FTPK")) || (location.equalsIgnoreCase("FP"))) {
									locationID = 0;
								} else if ((location.equalsIgnoreCase("GARR")) || (location.equalsIgnoreCase("GA"))) {
									locationID = 1;
								} else if ((location.equalsIgnoreCase("OAHE")) || (location.equalsIgnoreCase("OA"))) {
									locationID = 2;
								} else if ((location.equalsIgnoreCase("BEND")) || (location.equalsIgnoreCase("BB"))) {
									locationID = 3;
								} else if ((location.equalsIgnoreCase("FTRA")) || (location.equalsIgnoreCase("FR"))) {
									locationID = 4;
								} else if ((location.equalsIgnoreCase("GAPT")) || (location.equalsIgnoreCase("GP"))) {
									locationID = 5;
								}
								if (dataMap.containsKey(Integer.valueOf(locationID))) {
									System.out.println("Error already in hashmap!! " + locationID);
								} else {
									ElevationCapability ec = new ElevationCapability();
									ec.setSiteID(locationID);
									dataMap.put(Integer.valueOf(locationID), ec);
								}
							} else {
								ElevationCapability ec = (ElevationCapability) dataMap.get(Integer.valueOf(locationID));

								String elevation = token.nextToken();
								List<Double> elevations = ec.getElevations();
								elevations.add(Double.valueOf(Double.parseDouble(elevation)));

								String capacity = token.nextToken();
								List<Double> capacities = ec.getCapability();
								capacities.add(Double.valueOf(Double.parseDouble(capacity)));
								switch (locationID) {
								case 0:
									ec.setSite("FTPK");
									break;
								case 1:
									ec.setSite("GARR");
									break;
								case 2:
									ec.setSite("OAHE");
									break;
								case 3:
									ec.setSite("BEND");
									break;
								case 4:
									ec.setSite("FTRA");
									break;
								case 5:
									ec.setSite("GAPT");
								}
							}
						}
					} // while
				} else {
					System.err.println("outelcp contains no data");
				}
				in.close();
			} else {
				JOptionPane.showMessageDialog(null, "outelcp file not found.", "outelcp file not found", 0);
			}
		} catch (Exception e) {
			System.err.println(" GetOutelcpFile Error: " + e.getMessage());
			JOptionPane.showMessageDialog(null, "Please select a valid outelcp file location.", "Invalid outelcp file",
					0);
			e.printStackTrace();
		}
		return dataMap;
	}

	private static Tolerances getToleranceWin() {
		if (toleranceWin == null) {
			return toleranceWin = new Tolerances(getCapToleranceInfo(), getSites());
		}
		return toleranceWin;
	}

	protected Calendar getEndDataDate() {
		return endDataDate;
	}

	protected void setEndDataDate(Calendar endDate) {
		endDataDate = endDate;
	}

	public boolean isUpdateFlag() {
		return updateFlag;
	}

	public void setUpdateFlag(boolean updateFlg) {
		updateFlag = updateFlg;
	}

	protected HashMap<String, List<Integer>> getCapabilities() {
		return capabilities;
	}

	protected void setCapabilities(HashMap<String, List<Integer>> capabilitie) {
		capabilities = capabilitie;
	}

	protected String getMondayCapTolDate() {
		return mondayCapTolDate;
	}

	protected void setMondayCapTolDate(String mondayCapToldate) {
		mondayCapTolDate = mondayCapToldate;
	}

	protected String getSaturdayCapTolDate() {
		return saturdayCapTolDate;
	}

	protected void setSaturdayCapTolDate(String saturdayCapToldate) {
		saturdayCapTolDate = saturdayCapToldate;
	}

	protected int getReschTotQRow() {
		return reschTotQRow;
	}

	protected void setReschTotQRow(int reschTotQRow) {
		this.reschTotQRow = reschTotQRow;
	}
}
