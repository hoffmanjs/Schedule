package usace.wm.schedule;

import java.awt.Component;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpProgressMonitor;
import com.jcraft.jsch.UserInfo;

import oracle.jdbc.OracleCallableStatement;
//import oracle.jdbc.driver.OracleCallableStatement;

public class Util {
	private static final String DEFAULT_PROGRAM_LOCATION = "defaultProgramLocation";
	private static final String DEFAULT_PROGRAM_LOCATION_BAK = "defaultProgramLocationBak";
	private static boolean checkDriveMappping = false;

	private static final String FTRA_ONE_DAY_AGO_PERCENTAGE = "FTRAOneDayAgoPercentage";
	private static Double FTRAOneDayAgoPercentage = Double.valueOf(0.6);

	private static final String FTRA_TWO_DAY_AGO_PERCENTAGE = "FTRATwoDayAgoPercentage";
	private static Double FTRATwoDayAgoPercentage = Double.valueOf(0.4);

	private static final String BRIEFING_FILE_NAME = "briefingScheduleFileName";

	private static final String USER_NAME = "userName";
	private static String userName = null;

	private static final String LAST_UPDATE = "lastUpdate";
	private static String lastUpdate = null;

	 private static final String SCHEDULE_TABLE_NAME = "ScheduleRescheduleTable";
	private static final String TOLERANCE_TABLE_NAME = "ToleranceTable";
	private static final String ELEVATION_CAPABILIITY_TABLE_NAME = "ElevationCapabilityTable";
	private static final String PROPERTY_FILE = "WaterManagement.properties";

	// CWMS Database Info
	private static final String DB_SERVER_NAME = "dBServerName";
	private static String dBServerName = null;

	// Port Number
	private static final String DB_PORT_NUMBER = "dBPortNumber";
	private static String dBPortNumber = null;

	// SID
	private static final String DB_SID = "dBSID";
	private static String dBSID = null;

	// User Name
	private static final String DB_USER_NAME = "dBUserName";
	private static String dBUserName = null;

	// Password
	private static final String DB_USER_PASSWORD = "dBUserPassword";
	private static String dBUserPassword = null;

	// password encrypted boolean
	private static final String DB_PASSWORD_ENCRYPTED = "dBPasswordEncrypted";
	private static String dBPasswordEncrypted = null;

	private static EncryptDecrypt cryptDecryptApp = null;

	private static final String SPENCER_TS = "spencerTS";
	private static String spencerTS = null;

	protected static String driverName = "oracle.jdbc.driver.OracleDriver";

	private static final String NWD_WMLOCAL_BRFSCH_LOCATION = "nwdWmLocalBrfschLocation";
	private static String nwdWmLocalBrfschLocation = null;

	private static final String NWD_WMLOCAL_UNIX_HOST = "nwdWmLocalUnixHost";
	private static String nwdWmLocalUnixHost = null;

	private static String briefingFileName = "BRFSCH.TXT";
	private static File propertyFile = null;
	private static File briefingFile = null;
	private static File briefingFileBak = null;
	private static String defaultDlyStationDataFileName = "dlystat";
	private static String defaultScheduleFileName = "schfile";
	private static String defaultWaterManagementLocation = null;
	private static String defaultProgramLocationBak = null;
	private static boolean defaultProgramLocationBakExists = false;

	private static String pcUserName = null;

	private static HashMap<String, String> outagesMap = null;
	private static HashMap<String, String> resOutagesMap = null;

	public static final String FTPK_ID = "1";
	public static final String GARR_ID = "2";
	public static final String OAHE_ID = "3";
	public static final String BEND_ID = "4";
	public static final String FTRA_ID = "5";
	public static final String GAPT_ID = "6";

	private static HashMap<String, String> propertiesMap = null;
	private static String mapKey = null;
	private static String lastDay = null;
	private static String[] capacityToleranceInfo = null;
	private static DecimalFormat formatNoDecimal = new DecimalFormat("#####");
	private static DecimalFormat formatOneDecimal = new DecimalFormat("#####.0");
	private static DecimalFormat formatTwoDecimal = new DecimalFormat("#####.00");
	private static DecimalFormat formatThreeDecimal = new DecimalFormat("#####.000");

	private static SimpleDateFormat sdfMDYHHMMSS = null;

	private static Calendar calendarUTC = null;
	private static Calendar lastValueDate = null;

	protected static final String TIME_ZONE_UTC = "UTC";
	protected static final String TIME_ZONE_CST = "America/Chicago";
	private static final String OFFICE_ID = "NWDM";
	private static boolean DEBUG = false;

	private static ScheduleFileData schedFileData = null;
	
	private static Date nextDstDate = null;
	private static long nextDstDays = -9999;

	/**
	 * 
	 * @param data
	 * @return
	 */
	protected static Calendar getDate(String data) {
		Calendar calendar = Calendar.getInstance();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat();
			sdf.applyPattern("MM/dd/yy");
			Date processDate = sdf.parse(data);
			calendar.setTime(processDate);
		} catch (Exception e) {
			System.out.println("Error in getDate: " + e);
		}
		return calendar;
	}

	protected static String getDataKey(Calendar aDay) {
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.applyPattern("yyMMdd");
		return sdf.format(aDay.getTime());
	}

	protected static boolean getFileChooser(Component cpt) {
		boolean flag = false;
		JFileChooser chooser = new JFileChooser(new File(getDefaultProgramLocation())) {
			private static final long serialVersionUID = -5217585327054535750L;

			protected JDialog createDialog(Component parent) throws HeadlessException {
				JDialog dialog = super.createDialog(parent);

				dialog.setIconImage(Toolkit.getDefaultToolkit().getImage("/USACE.gif"));

				return dialog;
			}
		};
		chooser.setDialogTitle("Select Schedule Program Directory");
		chooser.setFileSelectionMode(1);

		chooser.setAcceptAllFileFilterUsed(false);
		if (chooser.showOpenDialog(cpt) == 0) {
			setDefaultProgramLocation(chooser.getSelectedFile().toString());
			flag = true;
		}
		return flag;
	}
	
	/**
	 * Selecting a OUTELCP file when it is not in the default directory.
	 * @param cpt
	 * @param file
	 * @return The directory path
	 */
	protected static String getFileChooserPath(Component cpt) {
		boolean flag = false;
		String fileLocation = null;
		
		JFileChooser chooser = new JFileChooser(new File(getDefaultProgramLocation())) {
			private static final long serialVersionUID = -5217585327054535750L;

			protected JDialog createDialog(Component parent) throws HeadlessException {
				JDialog dialog = super.createDialog(parent);

				dialog.setIconImage(Toolkit.getDefaultToolkit().getImage("/USACE.gif"));

				return dialog;
			}
		};
		chooser.setDialogTitle("Select a ELevation Capability Table (OUTELCP) file directory for the compute function to work.");
		chooser.setFileSelectionMode(1);

		chooser.setAcceptAllFileFilterUsed(false);
		if (chooser.showOpenDialog(cpt) == 0) {
			setDefaultProgramLocation(chooser.getSelectedFile().toString());
		}
		fileLocation = chooser.getSelectedFile().toString();
		return fileLocation;
	}

	/**
	 * Write out the schfile
	 * 
	 * @param schfileDlyData
	 * @param id24Map
	 * @param spcMap
	 * @param bbMap
	 * @param totCalEnrgyMap
	 * @param totCalReschedEnrgyMap
	 * @param outageMap
	 * @param coeffMap
	 * @param capTolInfo
	 * @param dlystatStartDay
	 */
	protected static void writeOutFileData(HashMap<String, DlyData> schfileDlyData, HashMap<String, String> id24Map,
			HashMap<String, String> spcMap, HashMap<String, String> bbMap, HashMap<String, String> totCalEnrgyMap,
			HashMap<String, String> totCalReschedEnrgyMap, HashMap<String, String[]> outageMap,
			HashMap<String, String[]> coeffMap, String[] capTolInfo, String dlystatStartDay, boolean savebutton) {
		File schfilebak = null;
		BufferedWriter outbak = null;

		File schfile = null;
		if(savebutton){
			schfile = new File(getDefaultProgramLocation() + File.separator + getDefaultScheduleFileName());
		} else {			
			schfile = new File(getDefaultProgramLocation() + File.separator + getDefaultScheduleFileName()+".init");
		}
		//TODO
		if (isDefaultProgramLocationBakExists()) {
			schfilebak = new File(getDefaultProgramLocationBak() + File.separator + getDefaultScheduleFileName()); // Create backup copy of file
//			schfile = new File(getDefaultProgramLocationBak() + File.separator + getDefaultScheduleFileName()); //Copy to COOP drive as well, added this
		}
		
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(schfile));

			if (isDefaultProgramLocationBakExists()) {
				outbak = new BufferedWriter(new FileWriter(schfilebak));
			}

			StringBuffer dlydataLine = new StringBuffer();
			StringBuffer energyLine = new StringBuffer();
			StringBuffer reschedLine = new StringBuffer();
			StringBuffer outageLine = new StringBuffer();
			StringBuffer bbElevChngLine = new StringBuffer();
			StringBuffer coeffLine = new StringBuffer();

			Object[] obj = schfileDlyData.keySet().toArray();

			Arrays.sort(obj);
			Calendar start = getCalendarDate(dlystatStartDay);
			int lineCounter = 0;
			for (int x = 0; x < obj.length; x++) {
				DlyData dlydata = (DlyData) schfileDlyData.get(obj[x]);
				if ((getCalendarYYMMDD(dlydata.getDate()).equals(start))
						|| (getCalendarYYMMDD(dlydata.getDate()).after(start))) {
					if (dlydataLine.length() == 0) {
						dlydataLine.append("\t" + dlydata.getDate() + "\t");
						setLastDay(dlydata.getDate());
						lineCounter++;
					}
					dlydataLine.append(dlydata.getDlyGenPower() + "\t");
					dlydataLine.append(dlydata.getFlowTotal() + "\t");
					dlydataLine.append(dlydata.getFlowPower() + "\t");
					dlydataLine.append(dlydata.getPoolElev() + "\t");

					energyLine.append(dlydata.getActGenPower() + "\t");
					energyLine.append(dlydata.getOD() + "\t");
					energyLine.append(dlydata.getKcfs() + "\t");

					reschedLine.append(dlydata.getRescheduleEnergy() + "\t");

					reschedLine = addReSchedLineData(obj[x].toString(), id24Map, spcMap, bbMap, totCalEnrgyMap,
							totCalReschedEnrgyMap, reschedLine);
					reschedLine.append(dlydata.getRescheduleOP() + "\t");
					if (obj[x].toString().endsWith("_5")) {
						String[] outages = (String[]) outageMap.get(obj[x].toString().substring(0, 6));
						if (outages != null) {
							for (int y = 0; y < outages.length; y++) {
								outageLine.append(outages[y].toString() + "\t");
							}
							reschedLine.append(outageLine.toString());
						}
						for (int z = 0; z < 6; z++) {
							reschedLine.append("0\t");
						}
						String[] coefficients = (String[]) coeffMap.get(obj[x].toString().substring(0, 6));
						if (coefficients != null) {
							for (int y = 0; y < coefficients.length; y++) {
								coeffLine.append(coefficients[y].toString() + "\t");
							}
							reschedLine.append(coeffLine.toString());
						}
					}
					if (obj[x].toString().endsWith("_5")) {
						dlydataLine.append(energyLine.toString());
						dlydataLine.append(reschedLine.toString());
						dlydataLine.append("\n");
						out.write(dlydataLine.toString());
						if (null != outbak) {
							outbak.write(dlydataLine.toString());
						}
						dlydataLine.delete(0, dlydataLine.length());
						energyLine.delete(0, energyLine.length());
						reschedLine.delete(0, reschedLine.length());
						outageLine.delete(0, outageLine.length());
						coeffLine.delete(0, coeffLine.length());
						dlydataLine.trimToSize();
						energyLine.trimToSize();
						reschedLine.trimToSize();
						outageLine.trimToSize();
						reschedLine.trimToSize();
					}
				}
			}
			while (lineCounter < 14) {
				Calendar aday = subtractCalendarDay(getCalendarYYMMDD(getLastDay()), -1);

				dlydataLine.delete(0, dlydataLine.length());
				dlydataLine.append("\t" + getCalendarYYMMDD(aday) + "\t");
				for (int count = 0; count < 78; count++) {
					dlydataLine.append("0.00\t");
				}
				dlydataLine.append("\n");
				out.write(dlydataLine.toString());
				if (null != outbak) {
					outbak.write(dlydataLine.toString());
				}

				setLastDay(getCalendarYYMMDD(aday));
				lineCounter++;
			}
			StringBuffer capTolLine = new StringBuffer();
			for (int a = 0; a < capTolInfo.length; a++) {
				if (a == 0) {
					capTolLine.append("\t");
				}
				capTolLine.append(capTolInfo[a] + "\t");
			}
			out.write(capTolLine.toString());
			if (null != outbak) {
				outbak.write(capTolLine.toString());
			}
			out.close();
			if (null != outbak) {
				outbak.close();
			}
		} catch (IOException e) {
			System.err.println("Error writing to file: " + schfile.getName());
			JOptionPane.showConfirmDialog(null,
					"Error writing to file: " + schfile.getPath() + "\nInformation not saved. \n" + e.toString(), "File Write Error", -1);
			e.printStackTrace();
		}
	}//End

	/**
	 * make backup copies of the files
	 */
	protected static void makeBackupScheduleFile() {
		File schfile = new File(getDefaultProgramLocation() + File.separator + getDefaultScheduleFileName());
		if ((schfile.exists()) && (schfile.length() > 0L)) {
			File schfilebak2 = null;
			File schfilebak = new File(
					getDefaultProgramLocation() + File.separator + getDefaultScheduleFileName() + ".bak");

			if (isDefaultProgramLocationBakExists()) {
				schfilebak2 = new File(
						getDefaultProgramLocationBak() + File.separator + getDefaultScheduleFileName() + ".bak");
			}

			InputStream inStream = null;
			OutputStream outStream = null;
			try {
				inStream = new FileInputStream(schfile);
				outStream = new FileOutputStream(schfilebak);

				byte[] buffer = new byte['?'];
				int length;
				while ((length = inStream.read(buffer)) > 0) {
					outStream.write(buffer, 0, length);
				}
				inStream.close();
				outStream.close();

				System.out.println("schfile File is copied successful!" + schfilebak.getAbsolutePath());
				if (null != schfilebak2) {
					System.out.println("schfile File is copied successful!" + schfilebak2.getAbsolutePath());
					OutputStream outbak = new FileOutputStream(schfilebak2);
					Files.copy(schfilebak.toPath(), outbak);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * 
	 */
	protected static void makeBackupDlystatFile() {
		File dlystat = new File(getDefaultProgramLocation() + File.separator + getDefaultDlyStationDataFileName());
		if ((dlystat.exists()) && (dlystat.length() > 0L)) {
			File dlystatbak2 = null;

			File dlystatbak = new File(
					getDefaultProgramLocation() + File.separator + getDefaultDlyStationDataFileName() + ".bak");

			if (isDefaultProgramLocationBakExists()) {
				dlystatbak2 = new File(
						getDefaultProgramLocationBak() + File.separator + getDefaultDlyStationDataFileName() + ".bak");
			}

			InputStream inStream = null;
			OutputStream outStream = null;
			try {
				inStream = new FileInputStream(dlystat);
				outStream = new FileOutputStream(dlystatbak);

				byte[] buffer = new byte['?'];
				int length;
				while ((length = inStream.read(buffer)) > 0) {
					outStream.write(buffer, 0, length);
				}
				inStream.close();
				outStream.close();

				System.out.println("dlystat File is copied successful!" + dlystatbak.getAbsolutePath());

				if (null != dlystatbak2) {
					System.out.println("dlystat File is copied successful!" + dlystatbak2.getAbsolutePath());
					OutputStream outbak = new FileOutputStream(dlystatbak2);
					Files.copy(dlystatbak.toPath(), outbak);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static StringBuffer addReSchedLineData(String key, HashMap<String, String> id24Map,
			HashMap<String, String> spcMap, HashMap<String, String> bbMap, HashMap<String, String> totCalEnrgyMap,
			HashMap<String, String> totCalReschedEnrgyMap, StringBuffer reschedLine) {
		String noVal = "0";
		setMapKey(key.substring(0, 6));
		String k = getMapKey();
		int rec = Integer.parseInt(key.substring(7, 8));
		switch (rec) {
		case 0:
			reschedLine.append(noVal + "\t");
			break;
		case 1:
			reschedLine.append((String) bbMap.get(getMapKey()) + "\t");
			break;
		case 2:
			reschedLine.append((String) totCalReschedEnrgyMap.get(getMapKey()) + "\t");
			break;
		case 3:
			reschedLine.append((String) totCalEnrgyMap.get(getMapKey()) + "\t");
			break;
		case 4:
			reschedLine.append((String) spcMap.get(getMapKey()) + "\t");
			break;
		case 5:
			reschedLine.append((String) id24Map.get(getMapKey()) + "\t");
			break;
		default:
			reschedLine.append("BAD\t");
		}
		return reschedLine;
	}

	protected static Calendar getCalendarYYMMDD(String date) {
		Calendar day = Calendar.getInstance();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat();
			sdf.applyPattern("yyMMdd");
			Date processDate = sdf.parse(date);
			day.setTime(processDate);
		} catch (Exception e) {
			System.out.println("Error in getCalendarYYMMDD: " + e);
		}
		return day;
	}

	protected static Calendar getCalendarDate(String calendarDate) {
		Calendar now = Calendar.getInstance();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat();
			sdf.applyPattern("MM/dd/yy");
			Date processDate = sdf.parse(calendarDate);
			now.setTime(processDate);
		} catch (ParseException e) {
			System.err.println("Error parsing getCalendarDate: " + e);
		}
		return now;
	}

	protected static Calendar getCalendarDateYYMMDD(String calendarDate) {
		Calendar now = Calendar.getInstance();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat();
			sdf.applyPattern("yyMMdd");
			Date processDate = sdf.parse(calendarDate);
			now.setTime(processDate);
		} catch (Exception e) {
			System.out.println("Error in getCalendarDateYYMMDD: " + e);
		}
		return now;
	}

	protected static Calendar getCalendarDateMMDDYY(String calendarDate) {
		Calendar now = Calendar.getInstance();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat();
			sdf.applyPattern("MM/dd/yy");
			Date processDate = sdf.parse(calendarDate);
			now.setTime(processDate);
		} catch (Exception e) {
			System.out.println("Error in getCalendarDateYYMMDD: " + e);
		}
		return now;
	}

	protected static Calendar subtractCalendarDay(Calendar date, int days) {
		date.add(5, -days);

		return date;
	}

	protected static String getCalendarYYMMDD(Calendar date) {
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.applyPattern("yyMMdd");
		return sdf.format(date.getTime());
	}

	protected static String getTS(String token) {
		String data = null;
		if (token.equalsIgnoreCase("~")) {
			data = " ";
		} else {
			data = token;
		}
		return data;
	}

	/**
	 * Check if the date is in DST
	 * 
	 * @param dayTime
	 * @return boolean
	 */
	protected static long isDstDays(String dayTime) {
		// create timezone object
		DateTimeZone zone = DateTimeZone.forID("America/Chicago");
		Calendar c = Calendar.getInstance();
//		c.set(Calendar.DAY_OF_MONTH, 2);
//		c.add(Calendar.DAY_OF_MONTH, 120);
//		System.out.println(c.getTime());

		// check if a date is in DST
		DateTime inDst = new DateTime(c.getTime().getTime(), zone);
		// isStandardOffset returns true (it's in DST)
		boolean dst = !zone.isStandardOffset(inDst.getMillis());
//		System.out.println("In dst: " + dst);

		// check when it'll be the next DST change
		DateTime nextDstChange = new DateTime(zone.nextTransition(inDst.getMillis()), zone);
//		System.out.println(nextDstChange); // 2017-02-18T23:00:00.000-03:00
		Date datedst = nextDstChange.toDate();
		setNextDstDate(datedst);

		Map<TimeUnit, Long> xyz = computeDiff(c.getTime(), datedst);
		setNextDstDays(xyz.get(TimeUnit.DAYS));
//		System.out.println(getNextDstDays());
		
		return getNextDstDays();
	}

	/**
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static Map<TimeUnit, Long> computeDiff(Date date1, Date date2) {
		long diffInMillies = date2.getTime() - date1.getTime();
		List<TimeUnit> units = new ArrayList<TimeUnit>(EnumSet.allOf(TimeUnit.class));
		Collections.reverse(units);
		Map<TimeUnit, Long> result = new LinkedHashMap<TimeUnit, Long>();
		long milliesRest = diffInMillies;
		for (TimeUnit unit : units) {
			long diff = unit.convert(milliesRest, TimeUnit.MILLISECONDS);
			long diffInMilliesForUnit = unit.toMillis(diff);
			milliesRest = milliesRest - diffInMilliesForUnit;
			result.put(unit, diff);
		}
		return result;
	}

	/**
	 * Check if the date is in DST
	 * 
	 * @param dayTime
	 * @return boolean
	 */
	protected static boolean isDST(String dayTime) {
		TimeZone timezoneone = null;
		Date day = null;
		Date date = null;

		try {
			Calendar c = Calendar.getInstance();
			c.add(Calendar.DAY_OF_MONTH, 2);
			Date d = c.getTime();
			// System.out.println("date: " + d);
			// example: 2018-06-15 15:09:46
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if (null == dayTime) {
				dayTime = sdf.format(d);
				// System.out.println(dayTime);
			}
			date = sdf.parse(dayTime);
			Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
			cal.setTime(date);
			// checking day light
			timezoneone = TimeZone.getDefault();
			day = cal.getTime();

		} catch (ParseException e) {
			e.printStackTrace();
		}

		return timezoneone.inDaylightTime(day);
	}

	/**
	 * 
	 */
	protected static void formatBrfSchTxt(List<String> data) {
		Iterator<String> it = data.iterator();
		try {
			OutputStream outbak = null;
			briefingFile = new File(new File("").getAbsolutePath() + File.separator + getBriefingFileName()); // default location\BRFSCH.TXT
			setBriefingFile(briefingFile);
			briefingFileBak = new File(new File(getDefaultProgramLocationBak()).getAbsolutePath() + File.separator + getBriefingFileName());// backup location\BRFSCH.TXT
			setBriefingFileBak(briefingFileBak);

			BufferedWriter out = new BufferedWriter(new FileWriter(briefingFile));
			if (isDefaultProgramLocationBakExists()) {
				outbak = new FileOutputStream(briefingFileBak);
			}
			while (it.hasNext()) {
				String line = (String) it.next();
				int lineNum = Integer.parseInt(line.substring(0, line.indexOf(",")));

				String lineData = line.substring(line.indexOf(",") + 1, line.length());
				if (lineData.trim().length() > 0) {
					StringTokenizer st = new StringTokenizer(lineData.trim(), ",");
					if (lineData.contains("Energy")) {
						out.write(String.format("%1s%1s%26s%10s%7s%7s%1s%1s%1s%13s%1s\n",
								new Object[] { getTS(st.nextToken()), getTS(st.nextToken()), getTS(st.nextToken()),
										getTS(st.nextToken()), getTS(st.nextToken()), getTS(st.nextToken()),
										getTS(st.nextToken()), getTS(st.nextToken()), getTS(st.nextToken()),
										getTS(st.nextToken()), getTS(st.nextToken()) }));
					} else if (lineData.contains("Water")) {
						out.write(String.format("%1s%1s%24s%10s%10s%1s%1s%1s%13s%1s%1s\n",
								new Object[] { getTS(st.nextToken()), getTS(st.nextToken()), getTS(st.nextToken()),
										getTS(st.nextToken()), getTS(st.nextToken()), getTS(st.nextToken()),
										getTS(st.nextToken()), getTS(st.nextToken()), getTS(st.nextToken()),
										getTS(st.nextToken()), getTS(st.nextToken()) }));
					} else if (lineData.contains("MIDNIGHT")) {
						out.write(String.format("%1s%1s%32s%10s%10s%1s%15s%1s%1s\n",
								new Object[] { getTS(st.nextToken()), getTS(st.nextToken()), getTS(st.nextToken()),
										getTS(st.nextToken()), getTS(st.nextToken()), getTS(st.nextToken()),
										getTS(st.nextToken()), getTS(st.nextToken()), getTS(st.nextToken()) }));
					} else if ((lineData.contains("FTPK")) && (lineNum < 5)) {
						out.write(String.format("%1s%23s%6s%6s%6s%6s%6s%1s%10s%7s\n",
								new Object[] { getTS(st.nextToken()), getTS(st.nextToken()), getTS(st.nextToken()),
										getTS(st.nextToken()), getTS(st.nextToken()), getTS(st.nextToken()),
										getTS(st.nextToken()), getTS(st.nextToken()), getTS(st.nextToken()),
										getTS(st.nextToken()) }));
					} else if ((lineData.contains("FTPK")) && (lineNum > 25) && (lineNum < 35)) {
						out.write(String.format("%1s%23s%6s%6s%6s%6s%6s%1s%6s%6s%5s\n",
								new Object[] { getTS(st.nextToken()), getTS(st.nextToken()), getTS(st.nextToken()),
										getTS(st.nextToken()), getTS(st.nextToken()), getTS(st.nextToken()),
										getTS(st.nextToken()), getTS(st.nextToken()), getTS(st.nextToken()),
										getTS(st.nextToken()), getTS(st.nextToken()) }));
					} else if ((lineData.contains("FTPK")) && (lineNum > 50)) {
						out.write(String.format("%1s%23s%9s%9s%9s%9s%9s\n",
								new Object[] { getTS(st.nextToken()), getTS(st.nextToken()), getTS(st.nextToken()),
										getTS(st.nextToken()), getTS(st.nextToken()), getTS(st.nextToken()),
										getTS(st.nextToken()) }));
					} else if ((lineData.contains("Sat Cap")) || (lineData.contains("Mon Cap"))) {
						out.write(String.format("%17s%7s%6s%6s%6s%6s%6s%5s%6s%1s%1s\n",
								new Object[] { getTS(st.nextToken()), getTS(st.nextToken()), getTS(st.nextToken()),
										getTS(st.nextToken()), getTS(st.nextToken()), getTS(st.nextToken()),
										getTS(st.nextToken()), getTS(st.nextToken()), getTS(st.nextToken()),
										getTS(st.nextToken()), getTS(st.nextToken()) }));
					} else if ((lineData.contains("+ Tol")) || (lineData.contains("- Tol"))) {
						out.write(String.format("%17s%7s%6s%6s%6s%6s%6s%1s%1s\n",
								new Object[] { getTS(st.nextToken()), getTS(st.nextToken()), getTS(st.nextToken()),
										getTS(st.nextToken()), getTS(st.nextToken()), getTS(st.nextToken()),
										getTS(st.nextToken()), getTS(st.nextToken()), getTS(st.nextToken()) }));
					} else if ((lineData.contains("24Hr Chng")) || (lineData.contains("OUTAGES"))) {
						out.write(String.format("%17s%8s%9s%9s%9s%9s%9s\n",
								new Object[] { getTS(st.nextToken()), getTS(st.nextToken()), getTS(st.nextToken()),
										getTS(st.nextToken()), getTS(st.nextToken()), getTS(st.nextToken()),
										getTS(st.nextToken()) }));
					} else if ((lineNum > 5) && (lineNum < 30)) {
						out.write(String.format("%16s%8s%6s%6s%6s%6s%6s%1s%10s%7s\n",
								new Object[] { getTS(st.nextToken()), getTS(st.nextToken()), getTS(st.nextToken()),
										getTS(st.nextToken()), getTS(st.nextToken()), getTS(st.nextToken()),
										getTS(st.nextToken()), getTS(st.nextToken()), getTS(st.nextToken()),
										getTS(st.nextToken()) }));
					} else if ((lineNum > 29) && (lineNum < 55)) {
						out.write(String.format("%16s%8s%6s%6s%6s%6s%6s%1s%7s%5s%5s\n",
								new Object[] { getTS(st.nextToken()), getTS(st.nextToken()), getTS(st.nextToken()),
										getTS(st.nextToken()), getTS(st.nextToken()), getTS(st.nextToken()),
										getTS(st.nextToken()), getTS(st.nextToken()), getTS(st.nextToken()),
										getTS(st.nextToken()), getTS(st.nextToken()) }));
					} else if ((lineNum > 50) && (lineNum < 65)) {
						out.write(String.format("%16s%9s%9s%9s%9s%9s%9s%1s%7s%5s%5s\n",
								new Object[] { getTS(st.nextToken()), getTS(st.nextToken()), getTS(st.nextToken()),
										getTS(st.nextToken()), getTS(st.nextToken()), getTS(st.nextToken()),
										getTS(st.nextToken()), getTS(st.nextToken()), getTS(st.nextToken()),
										getTS(st.nextToken()), getTS(st.nextToken()) }));
					}
				} else {
					out.write(String.format("\n", new Object[] { "" }));
				}
			}
			out.close();
			if (null != outbak) {
				Files.copy(briefingFile.toPath(), outbak); // Copy file to the
															// backup location
			}
		} catch (NumberFormatException nfe) {
			System.out.println("Error in creating BRFSCH.TXT file: " + nfe);
			nfe.printStackTrace();
		} catch (IOException ioe) {
			System.out.println("Error in writing BRFSCH.TXT file: " + ioe);
			ioe.printStackTrace();
		}
	}

	protected static boolean copyBrfSchTxtFile() {
		JSch jsch = new JSch();
		String host = getNwdWmLocalUnixHost();
		String username = null;
		boolean success = false;
		ChannelSftp channelsftp = null;

		username = (String) JOptionPane.showInputDialog(null, "Enter username for " + host + " login:",
				host + "UserName", -1, null, null, "");
		if (username != null) {
			String user = username.trim();

			int port = 22;
			Session session = null;
			Channel channel = null;
			try {
				session = jsch.getSession(user, host, port);

				UserInfo ui = new MyUserInfo();
				session.setUserInfo(ui);

				SftpProgressMonitor monitor = new MyProgressMonitor();
				Properties config = new Properties();
				config.put("PreferredAuthentications", "publickey,keyboard-interactive,password");
				session.setConfig(config);
				session.connect();
				channel = session.openChannel("sftp");
				channel.connect();
				channelsftp = (ChannelSftp) channel;

				int mode = ChannelSftp.OVERWRITE;
//				String loc = getNwdWmLocalBrfschLocation() + "/BRFSCH";
//				channelsftp.put(getBriefingFile().getAbsolutePath(), loc, monitor, mode);
				String filename = getBriefingFile().getName();
				String netwrkPath = getBriefingFile().getAbsolutePath().substring(0, getBriefingFile().getAbsolutePath().indexOf(filename));
				String servername = getNwdWmLocalBrfschLocation();
				
				success = putFilesOnServerNameChange(channelsftp, filename, "BRFSCH", netwrkPath, servername, monitor, mode);
//				success = true;
			} catch (JSchException jse) {
				String messageText = "Not Connected To: " + host.toUpperCase() + "\n"
						+ "BRFSCH.TXT File Not Copied To: " + host.toUpperCase();
				JOptionPane.showMessageDialog(null, messageText, " Unknown Host: " + host.toUpperCase(),
						JOptionPane.ERROR_MESSAGE);
				jse.printStackTrace();
			} catch (Exception sftpe) {
				sftpe.printStackTrace();
				success = false;
				sftpe.printStackTrace();
				JOptionPane.showMessageDialog(null,  "Error BRFSCH Not Copied to: " + host.toUpperCase() + "\n" + sftpe.getMessage(),
						"Error BRFSCH Not Copied", JOptionPane.ERROR_MESSAGE);
			}
		}
		return success;
	}
	
	/**
	 * 
	 * @param c
	 * @param origFileName
	 * @param newFileName
	 * @param publicDriveLocation
	 * @param serverLocation
	 * @param monitor
	 * @param mode
	 * @return
	 */
	protected static boolean putFilesOnServerNameChange(ChannelSftp c, String origFileName, String newFileName, String publicDriveLocation, String serverLocation, SftpProgressMonitor monitor, int mode){
		boolean flag = false;
		try{
			try {
				c.rm(serverLocation + newFileName);
			} catch (Exception ex){
				System.out.println("Error Removing the File: " + serverLocation + newFileName + "\n" + ex);
			}
			
			try {
				c.put(publicDriveLocation + origFileName, serverLocation + newFileName, monitor, mode);
				flag = true;
			} catch (Exception ex){
				System.out.println("Error Copying the File: " + serverLocation + origFileName + "\n" + ex);
			}
			
			try{
				c.chmod(0664, serverLocation + newFileName);
			} catch (Exception ex){
				System.out.println("Error Changing File Permissions: " + serverLocation + origFileName + "\n" + ex);
			}
		} catch(Exception ex){
			System.out.println(ex);
		}
		return flag;
	}
	
//	protected static boolean putFilesOnServer(ChannelSftp c, String fileName, String publicDriveLocation, String serverLocation, SftpProgressMonitor monitor, int mode){
//		boolean flag = false;
//		try{
//			try {
//				c.rm(serverLocation + fileName);
//			} catch (Exception ex){
//				System.out.println("Error Removing the File: " + serverLocation + fileName + "\n" + ex);
//			}
//			
//			try {
//				c.put(publicDriveLocation + fileName, serverLocation + fileName, monitor,	mode);
//				flag = true;
//			} catch (Exception ex){
//				System.out.println("Error Copying the File: " + serverLocation + fileName + "\n" + ex);
//			}
//			
//			try{
//				c.chmod(0664, serverLocation + fileName);
//			} catch (Exception ex){
//				System.out.println("Error Changing File Permissions: " + serverLocation + fileName + "\n" + ex);
//			}
//		} catch(Exception ex){
//			System.out.println(ex);
//		}
//		return flag;
//	}

	protected static String getCalendarYYMMDD(int aday) {
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.applyPattern("yyMMdd");
		Calendar calendar = Calendar.getInstance();
		calendar.set(5, aday);
		return sdf.format(calendar.getTime());
	}

	protected static String getDayOfWeek(int d) {
		String[] strDays = { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };
		return strDays[(d - 1)];
	}

	protected static Calendar getDate() {
		return Calendar.getInstance();
	}

	protected static String getDateString() {
		Calendar cal = Calendar.getInstance();
		return cal.getTime().toString();
	}

	private static void setDefaultProgramLocation(String dfl) {
		defaultWaterManagementLocation = dfl;
	}

	protected static String getDefaultProgramLocation() {
		return defaultWaterManagementLocation;
	}

	protected static HashMap<String, String[]> checkMap(HashMap<String, String[]> map) {
		HashMap<String, String[]> theMap = new HashMap<String, String[]>();
		Iterator<String> it = map.keySet().iterator();
		while (it.hasNext()) {
			Object key = it.next();
			String[] data = (String[]) map.get(key);
			for (int x = 0; x < data.length; x++) {
				if (data[x] == null) {
					data[x] = "0";
				}
			}
			theMap.put((String) key, data);
		}
		return theMap;
	}

	protected static String getTableText(JTable table, int row) {
		StringBuffer sb = new StringBuffer();
		for (int column = 0; column < table.getColumnCount(); column++) {
			Object obj = table.getValueAt(row, column);
			if (column != 11) {
				if (obj != null) {
					sb.append(obj.toString() + "~");
				} else {
					sb.append("~");
				}
			} else {
				sb.append(obj);
			}
		}
		return sb.toString();
	}

	protected static String getPCUser() {
		pcUserName = System.getProperty("user.name");
		return pcUserName;
	}

	protected static JTable copyTableText(JTable table, JTable tableCopy) {
		for (int row = 0; row < table.getRowCount(); row++) {
			for (int column = 0; column < table.getColumnCount(); column++) {
				tableCopy.setValueAt(table.getValueAt(row, column), row, column);
			}
		}
		return tableCopy;
	}

	/**
	 * Get properties file
	 * 
	 * @return
	 */
	private static boolean getProperties() {
		boolean flag = true;

		propertiesMap = new HashMap<String, String>();
		Properties properties = new Properties();
		try {
			FileInputStream fis = new FileInputStream(getPropertyfile());
			properties.load(fis);
			for (Enumeration<Object> keys = properties.keys(); keys.hasMoreElements();) {
				String key = (String) keys.nextElement();
				String value = properties.getProperty(key);
				propertiesMap.put(key, value);
			}
			fis.close();
		} catch (IOException e) {
			flag = false;
			JOptionPane.showMessageDialog(null, "Property file does not exist: " + getPropertyfile(),
					"WaterManagement Property file not found.", 0);
			e.printStackTrace();
		}
		setPropertiesMap(propertiesMap);

		return flag;
	}

	/**
	 * Save Properties file
	 */
	protected static void saveProperties() {
		// HashMap<String, String> data = getPropertiesMap();
		// Properties properties = new Properties();
		// try {
		// FileOutputStream fos = new FileOutputStream(getPropertyfile());
		// Set<String> keys = data.keySet();
		// Iterator<String> it = keys.iterator();
		// while (it.hasNext()) {
		// String key = (String) it.next();
		// if (key.equalsIgnoreCase(USER_NAME)) {
		// properties.setProperty(key, getPCUser());
		// } else if (key.equalsIgnoreCase(LAST_UPDATE)) {
		// properties.setProperty(key, getDateString());
		// } else if (key.equalsIgnoreCase(FTRA_ONE_DAY_AGO_PERCENTAGE)) {
		// properties.setProperty(key, getFTRAOneDayAgoPercentage().toString());
		// } else if (key.equalsIgnoreCase(FTRA_TWO_DAY_AGO_PERCENTAGE)) {
		// properties.setProperty(key, getFTRATwoDayAgpPercentage().toString());
		// } else if (key.equalsIgnoreCase(DEFAULT_PROGRAM_LOCATION)) {
		// properties.setProperty(key, getDefaultProgramLocation());
		// } else if (key.equalsIgnoreCase(BRIEFING_FILE_NAME)) {
		// properties.setProperty(key, getBriefingFileName());
		// } else if (key.equalsIgnoreCase(NWD_WMLOCAL_BRFSCH_LOCATION)) {
		// properties.setProperty(key, getNwdWmLocalBrfschLocation());
		// } else if (key.equalsIgnoreCase(NWD_WMLOCAL_UNIX_HOST)) {
		// properties.setProperty(key, getNwdWmLocalUnixHost());
		// } else if (key.equalsIgnoreCase(DB_SERVER_NAME)) {
		// properties.setProperty(key, getdBServerName());
		// } else if (key.equalsIgnoreCase(DB_PORT_NUMBER)) {
		// properties.setProperty(key, getdBPortNumber());
		// } else if (key.equalsIgnoreCase(DB_SID)) {
		// properties.setProperty(key, getdBSID());
		// } else if (key.equalsIgnoreCase(DB_USER_NAME)) {
		// properties.setProperty(key, getdBUserName());
		// } else if (key.equalsIgnoreCase(DB_USER_PASSWORD)) {
		// properties.setProperty(key, getdBUserPassword());
		// } else if (key.equalsIgnoreCase(DB_PASSWORD_ENCRYPTED)) {
		// properties.setProperty(key, getdBPasswordEncrypted());
		// }
		// }
		// properties.store(fos, "Updated by: " + getPCUser());
		// fos.close();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		Calendar now = Calendar.getInstance();
		setLastUpdate(now.getTime().toString());

		PropertiesConfiguration config = null;
		try {
			config = new PropertiesConfiguration(getPropertyfile().getAbsolutePath());
			config.setProperty(USER_NAME, getPCUser());
			config.setProperty(LAST_UPDATE, getLastUpdate());
			config.save();
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Load Properties file
	 */
	protected static boolean loadProperties() {
		boolean flag = false;
		outagesMap = new HashMap<String, String>();
		resOutagesMap = new HashMap<String, String>();
		if (getProperties()) {
			flag = true;
			HashMap<String, String> propMap = getPropertiesMap();
			Set<String> keys = propMap.keySet();
			Iterator<String> it = keys.iterator();
			while (it.hasNext()) {
				String val = it.next();
				if (propMap.containsKey(val)) {
					if (val.equals(DEFAULT_PROGRAM_LOCATION)) {
						setDefaultProgramLocation((String) propMap.get(DEFAULT_PROGRAM_LOCATION));
					} else if (val.equals(DEFAULT_PROGRAM_LOCATION_BAK)) {
						setDefaultProgramLocationBak((String) propMap.get(DEFAULT_PROGRAM_LOCATION_BAK));
					} else if (val.equals(LAST_UPDATE)) {
						setLastUpdate((String) propMap.get(LAST_UPDATE));
					} else if (val.equals(FTRA_ONE_DAY_AGO_PERCENTAGE)) {
						setFTRAOneDayAgoPercentage(
								Double.valueOf(Double.parseDouble((String) propMap.get(FTRA_ONE_DAY_AGO_PERCENTAGE))));
					} else if (val.equals(FTRA_TWO_DAY_AGO_PERCENTAGE)) {
						setFTRATwoDayAgpPercentage(
								Double.valueOf(Double.parseDouble((String) propMap.get(FTRA_TWO_DAY_AGO_PERCENTAGE))));
					} else if (val.equals(USER_NAME)) {
						setUserName((String) propMap.get(USER_NAME));
					} else if (val.equals(BRIEFING_FILE_NAME)) {
						setBriefingFileName((String) propMap.get(BRIEFING_FILE_NAME));
					} else if (val.equals(NWD_WMLOCAL_BRFSCH_LOCATION)) {
						setNwdWmLocalBrfschLocation((String) propMap.get(NWD_WMLOCAL_BRFSCH_LOCATION));
					} else if (val.equals(NWD_WMLOCAL_UNIX_HOST)) {
						setNwdWmLocalUnixHost((String) propMap.get(NWD_WMLOCAL_UNIX_HOST));
					} else if (val.equals(DB_SERVER_NAME)) {
						setdBServerName(propMap.get(DB_SERVER_NAME));
					} else if (val.equals(DB_PORT_NUMBER)) {
						setdBPortNumber(propMap.get(DB_PORT_NUMBER));
					} else if (val.equals(DB_SID)) {
						setdBSID(propMap.get(DB_SID));
					} else if (val.equals(DB_USER_NAME)) {
						setdBUserName(propMap.get(DB_USER_NAME));
					} else if (val.equals(DB_USER_PASSWORD)) {
						setdBUserPassword(propMap.get(DB_USER_PASSWORD));
					} else if (val.equals(DB_PASSWORD_ENCRYPTED)) {
						setdBPasswordEncrypted(propMap.get(DB_PASSWORD_ENCRYPTED));
					} else if (val.equals(SPENCER_TS)) {
						setSpencerTS(propMap.get(SPENCER_TS));
					} else if(val.indexOf("Outages")>-1){
						resOutagesMap.put(val, propMap.get(val));
					} else {
//						outagesMap.put(val, propMap.get(val));
					}
				}
			}
		}
		initEncrypter();
		return flag;
	}

	protected static String initEncrypter() {
		String result = null;
		String file = getPropertyfile().getAbsolutePath();
		if (getdBPasswordEncrypted().equalsIgnoreCase("false")) {
			try {
				setCryptDecryptApp(new EncryptDecrypt(file, DB_USER_PASSWORD, DB_PASSWORD_ENCRYPTED));
			} catch (Exception e) {
				e.printStackTrace();
			}
			// Retrieve the decrypted password
			result = getCryptDecryptApp().decryptedUserPassword;
		} else {

			try {
				setCryptDecryptApp(new EncryptDecrypt(file, DB_USER_PASSWORD, DB_PASSWORD_ENCRYPTED));
			} catch (Exception e) {
				e.printStackTrace();
			}
			String passwd = getdBUserPassword();
			// System.out.println(passwd);
		}
		return result;
	}

	/**
	 * Get a connection to the Database
	 * 
	 * @return Connection
	 */
	protected static Connection getDBConnection() {
		Connection dbconn = null;
		try {
			// Load the JDBC driver
			Class.forName(driverName);

			// Create a connection to the database
			String url = "jdbc:oracle:thin:@" + getdBServerName() + ":" + getdBPortNumber() + ":" + getdBSID();

			String passwd = getCryptDecryptApp().decryptedUserPassword;
			dbconn = DriverManager.getConnection(url, getdBUserName(), passwd);
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
			// Could not find the database driver
			System.exit(0);
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			// Could not connect to the database
			JOptionPane
					.showMessageDialog(null,
							sqle.getMessage() + "<html> Could not connect to: <B>" + getdBServerName()
									+ "</B></html>,\n" + getdBSID(),
							"DB Connection Error on: " + getdBSID(), JOptionPane.WARNING_MESSAGE);
			System.exit(0);
		}
		return dbconn;
	}

	/**
	 * Get the outage information from the database
	 */
//	protected static List<SpencerData> getSpencerValues(Calendar strtDate, Calendar eDate) {
//		Connection dbconn = null;
//		List<SpencerData> dataMap = null;
//		String timeSeries = getSpencerTS();
//		String units = "cfs";
//
//		try {
//			dbconn = getDBConnection();
//			Statement stmt = dbconn.createStatement();
//
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//			sdf.setTimeZone(TimeZone.getTimeZone(TIME_ZONE_UTC));
//			strtDate.setTimeZone(TimeZone.getTimeZone(TIME_ZONE_UTC));
//			strtDate.set(Calendar.HOUR_OF_DAY, 12);
//			strtDate.set(Calendar.MINUTE, 0);
//			strtDate.set(Calendar.SECOND, 0);
//			String startDate = sdf.format(strtDate.getTime());
//			// System.out.println(startDate);
//			eDate.setTimeZone(TimeZone.getTimeZone(TIME_ZONE_UTC));
//			eDate.set(Calendar.HOUR_OF_DAY, 12);
//			eDate.set(Calendar.MINUTE, 0);
//			eDate.set(Calendar.SECOND, 0);
//			String endDate = sdf.format(eDate.getTime());
//			// System.out.println(endDate);
//
//			String sql = "select DATE_TIME, VALUE from table(cwms_20.cwms_ts.retrieve_ts_out_tab(p_cwms_ts_id => '"
//					+ timeSeries + "'," + " p_units => '" + units + "'," + " p_start_time => date '" + startDate + "',"
//					+ " p_end_time => date '" + endDate + "',"
//					// + " p_time_zone => 'US/Central'," + " p_office_id =>
//					// 'NWDM'))";
//					+ " p_time_zone => 'UTC'," + " p_office_id => 'NWDM'))";
//			// System.out.println("2.1: " + sql);
//			ResultSet rset = stmt.executeQuery(sql);
//
//			dataMap = new ArrayList<SpencerData>();
//			while (rset.next()) {
//				// System.out.println("1: " + rset.getString(1));
//				// System.out.println("2: " + rset.getString(2));
//				SpencerData ctd = new SpencerData(timeSeries, rset.getString(1), rset.getString(2));
//				dataMap.add(ctd);
//			}
//
//			rset.close();
//			stmt.close();
//			dbconn.close();
//		} catch (SQLException sqle) {
//			// Could not connect to the database
//			sqle.printStackTrace();
//		}
//		return dataMap;
//	}

	/**
	 * 
	 * @param tz
	 * @return
	 */
	protected static SimpleDateFormat getSdfMDYHHMMSS(String tz) {
		sdfMDYHHMMSS = new SimpleDateFormat("dd-MMM-yyyy HHmm");
		sdfMDYHHMMSS.setTimeZone(TimeZone.getTimeZone(tz));
		return sdfMDYHHMMSS;
	}

	/**
	 * 
	 * @return
	 */
	protected static Calendar getCalendarUTC() {
		if (calendarUTC == null) {
			System.out.println("New calendarUTC!!!!");
			calendarUTC = Calendar.getInstance();
			calendarUTC.setTimeZone(TimeZone.getTimeZone(TIME_ZONE_UTC));
		}
		return calendarUTC;
	}

	/**
	 * 
	 * @param value
	 * @param units
	 * @param cal
	 * @param tsId
	 * @return
	 */
//	static protected boolean saveSpencerData(HashMap<String, String> spcMap) {
//		SimpleDateFormat dtfData = new SimpleDateFormat("yyMMdd");
//		dtfData.setTimeZone(TimeZone.getTimeZone(TIME_ZONE_UTC));
//
//		boolean saveFlag = true;
//		int counter = 1;
//		Connection dbconn = null;
//		OracleCallableStatement cs = null;
//		String units = "cfs";
//		String tsId = getSpencerTS();
//
//		Set<String> set = spcMap.keySet();
//		Iterator<String> setIt = set.iterator();
//
//		while (setIt.hasNext()) {
//			try {
//				String day = setIt.next();
//				Date date = dtfData.parse(day);
//				String val = spcMap.get(day);
//				double value = Double.valueOf(val);
//
//				String storevalue = getFormatThreeDecimal().format(value * 1000);
//
//				if (value > 0) {
//					dbconn = getDBConnection();
//					long quality = 0;
//					String SQLstr = null;
//					// Convert time from CST to UTC
//					Calendar utc = getCalendarUTC(); // 160417
//					utc.setTime(date);
//					utc.setTimeZone(TimeZone.getTimeZone(TIME_ZONE_UTC));
//					utc.set(Calendar.HOUR, 12);
//					utc.set(Calendar.MINUTE, 0);
//					utc.set(Calendar.SECOND, 0);
//					String timestamp = getSdfMDYHHMMSS(TIME_ZONE_UTC).format(utc.getTime());
//
//					SQLstr = "declare\n";
//					SQLstr += " ts timestamp(6);\n";
//					SQLstr += " tstz timestamp(6) with time zone;\n";
//					SQLstr += " val binary_double;\n";
//					SQLstr += " qual number;\n";
//					SQLstr += " l_tsv cwms_20.tsv_array := cwms_20.tsv_array();\n";
//					SQLstr += " begin\n";
//					SQLstr += "ts := to_timestamp('" + timestamp + "','DD-MON-YYYY HH24MI');\n";
//					SQLstr += "tstz := from_tz(ts,'" + TIME_ZONE_UTC + "');\n";
//					SQLstr += "val  := " + storevalue + ";\n";
//					SQLstr += "qual := " + quality + ";\n";
//					SQLstr += "l_tsv.extend;\n";
//					SQLstr += "l_tsv(" + counter + ") := cwms_20.tsv_type(tstz, val, qual);\n";
//					SQLstr += "cwms_20.cwms_ts.store_ts('" + tsId + "','" + units + "',l_tsv,'REPLACE ALL',null,null,'"
//							+ OFFICE_ID + "');\n";
//					SQLstr += "commit;\n";
//					SQLstr += "end;\n";
//					if (DEBUG) {
//						System.out.println(SQLstr);
//					}
//
//					cs = (OracleCallableStatement) dbconn.prepareCall(SQLstr);
//					saveFlag = cs.execute();
//					cs.close();
//					dbconn.close();
//				}
//
//			} catch (Exception e) {
//				System.out.println("DB Stores before exception: " + counter);
//				saveFlag = false;
//				// JOptionPane.showMessageDialog(this, "Error storing Spencer
//				// values to the CWMS database" + e, "Error", -1);
//				e.printStackTrace();
//			}
//		}
//		return saveFlag;
//	}

	/**
	 * Get the outage information from the database
	 */
	protected static List<CwmsTableData> getDbOutages(Calendar strtDate, Calendar eDate, String timeSeries,	String units) {
		Connection dbconn = null;
		List<CwmsTableData> dataMap = null;

		try {
			dbconn = getDBConnection();
			Statement stmt = dbconn.createStatement();

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			sdf.setTimeZone(TimeZone.getTimeZone("TIME_ZONE_UTC"));
			strtDate.setTimeZone(TimeZone.getTimeZone("TIME_ZONE_UTC"));
			String startDate = sdf.format(strtDate.getTime());
			eDate.setTimeZone(TimeZone.getTimeZone("TIME_ZONE_UTC"));
			String endDate = sdf.format(eDate.getTime());

			String sql = "select DATE_TIME, VALUE from table(cwms_20.cwms_ts.retrieve_ts_out_tab(p_cwms_ts_id => '"
					+ timeSeries + "'," + " p_units => '" + units + "'," + " p_start_time => date '" + startDate + "',"
					+ " p_end_time => date '" + endDate + "',"
					+ " p_time_zone => 'UTC'," + " p_office_id => 'NWDM'))";
			System.out.println(sql);
			ResultSet rset = stmt.executeQuery(sql);

			dataMap = new ArrayList<CwmsTableData>();
			while (rset.next()) {
				CwmsTableData ctd = new CwmsTableData(timeSeries, rset.getString(1), rset.getString(2));
				dataMap.add(ctd);
			}
//			System.out.println(count + " e " + endDate + " s " + startDate + " ts " + timeSeries);
			rset.close();
			stmt.close();
			dbconn.close();
		} catch (SQLException sqle) {
			// Could not connect to the database
			System.out.println("SQL getDbOutages: " + timeSeries + "\n");
			sqle.printStackTrace();
		}
		return dataMap;
	}

	/**
	 * Checks if all the network drives are mapped
	 */
	protected static void checkDriveMaping() {
		// TODO
		File schfile = new File(getDefaultProgramLocation());
		if (!isCheckDriveMappping()) {
			if (!schfile.exists()) {
				JOptionPane.showMessageDialog(null,
						"Can't find path: " + getDefaultProgramLocation() + "\n" + "Drive not mapped.  "
								+ "  Please map the drive.\n" 
								+ "Check property file variable: " + DEFAULT_PROGRAM_LOCATION +  "\n"
								+ "Files will not be written to nextwork drive location.",
						"Error Reading Path.", 0);
				// System.exit(0);
			}

			File schfileBak = new File(getDefaultProgramLocationBak());
			if (!schfileBak.exists()) {
				JOptionPane.showMessageDialog(null,
						"Can't find path: " + getDefaultProgramLocationBak() + "\n" + "Drive not mapped.  "
								+ "  Please map the drive.\n" 
								+ "Check property file variable: " + DEFAULT_PROGRAM_LOCATION_BAK +  "\n"
								+ "Files will not be written to bakup drive location.",
						"Error Reading Path.", 0);
				// System.exit(0);
				setDefaultProgramLocationBakExists(false);
			}else {
				setDefaultProgramLocationBakExists(true);//Added 5/19/2021 sets backup location to true
			}
			setCheckDriveMappping(true);
		}
	}

	private static void setMapKey(String mapKy) {
		mapKey = mapKy;
	}

	private static String getMapKey() {
		return mapKey;
	}

	private static void setLastDay(String lDay) {
		lastDay = lDay;
	}

	private static String getLastDay() {
		return lastDay;
	}

	protected static String[] getCapacityToleranceInfo() {
		return capacityToleranceInfo;
	}

	protected static void setCapacityToleranceInfo(String[] capacityToleranceInf) {
		capacityToleranceInfo = capacityToleranceInf;
	}

	protected static void setFTRAOneDayAgoPercentage(Double FTRAOneDayAgoPer) {
		FTRAOneDayAgoPercentage = FTRAOneDayAgoPer;
	}

	protected static Double getFTRAOneDayAgoPercentage() {
		return FTRAOneDayAgoPercentage;
	}

	protected static void setFTRATwoDayAgpPercentage(Double FTRATwoDayAgpPer) {
		FTRATwoDayAgoPercentage = FTRATwoDayAgpPer;
	}

	protected static Double getFTRATwoDayAgpPercentage() {
		return FTRATwoDayAgoPercentage;
	}

	private static HashMap<String, String> getPropertiesMap() {
		return propertiesMap;
	}

	private static void setPropertiesMap(HashMap<String, String> propertiesMp) {
		propertiesMap = propertiesMp;
	}

	protected static String getUserName() {
		return userName;
	}

	private static void setUserName(String uName) {
		userName = uName;
	}

	private static File getPropertyfile() {
//		propertyFile = new File(new File("").getAbsolutePath() + File.separator + "WaterManagement.properties");
		propertyFile = new File(new File("").getAbsolutePath() + File.separator + PROPERTY_FILE);
		return propertyFile;
	}

	protected static String getScheduleTableName() {
//		return "ScheduleRescheduleTable";
		return SCHEDULE_TABLE_NAME;
	}

	protected static String getToleranceTableName() {
//		return "ToleranceTable";
		return TOLERANCE_TABLE_NAME;
	}

	protected static String getDefaultScheduleFileName() {
		return defaultScheduleFileName;
	}

	protected static String getDefaultDlyStationDataFileName() {
		return defaultDlyStationDataFileName;
	}

	private static String getBriefingFileName() {
		return briefingFileName;
	}

	private static void setBriefingFileName(String name) {
		briefingFileName = name;
	}

	protected static File getBriefingFile() {
		if (briefingFile == null) {
			briefingFile = new File(new File("").getAbsolutePath() + File.separator + getBriefingFileName());
			if (briefingFile.isFile()) {
				setBriefingFile(briefingFile);
			} else {
				setBriefingFile(null);
			}
		}
		return briefingFile;
	}

	protected static void setBriefingFile(File briefingfile) {
		briefingFile = briefingfile;
	}

	protected static DecimalFormat getFormatNoDecimal() {
		return formatNoDecimal;
	}

	protected static DecimalFormat getFormatOneDecimal() {
		return formatOneDecimal;
	}

	protected static DecimalFormat getFormatTwoDecimal() {
		return formatTwoDecimal;
	}

	protected static DecimalFormat getFormatThreeDecimal() {
		return formatThreeDecimal;
	}

	protected static String getElevationCapabiliityTableName() {
//		return "ElevationCapabilityTable";
		return ELEVATION_CAPABILIITY_TABLE_NAME;
	}

	protected static String getNwdWmLocalBrfschLocation() {
		return nwdWmLocalBrfschLocation;
	}

	protected static void setNwdWmLocalBrfschLocation(String nwdWmLocalBrfschLocatn) {
		nwdWmLocalBrfschLocation = nwdWmLocalBrfschLocatn;
	}

	protected static String getLastUpdate() {
		return lastUpdate;
	}

	protected static void setLastUpdate(String lastUpdate) {
		Util.lastUpdate = lastUpdate;
	}

	protected static String getNwdWmLocalUnixHost() {
		return nwdWmLocalUnixHost;
	}

	protected static void setNwdWmLocalUnixHost(String nwdWmLocalUnixHost) {
		Util.nwdWmLocalUnixHost = nwdWmLocalUnixHost;
	}

	protected static String getdBServerName() {
		return dBServerName;
	}

	protected static void setdBServerName(String dBServerName) {
		Util.dBServerName = dBServerName;
	}

	protected static String getdBPortNumber() {
		return dBPortNumber;
	}

	protected static void setdBPortNumber(String dBPortNumber) {
		Util.dBPortNumber = dBPortNumber;
	}

	protected static String getdBSID() {
		return dBSID;
	}

	protected static void setdBSID(String dBSId) {
		dBSID = dBSId;
	}

	protected static String getdBUserName() {
		return dBUserName;
	}

	protected static void setdBUserName(String dBUserNam) {
		dBUserName = dBUserNam;
	}

	protected static String getdBUserPassword() {
		return dBUserPassword;
	}

	protected static void setdBUserPassword(String dBUserPasswd) {
		dBUserPassword = dBUserPasswd;
	}

	protected static String getdBPasswordEncrypted() {
		return dBPasswordEncrypted;
	}

	protected static void setdBPasswordEncrypted(String dBPasswordEncryptd) {
		dBPasswordEncrypted = dBPasswordEncryptd;
	}

	protected static EncryptDecrypt getCryptDecryptApp() {
		return cryptDecryptApp;
	}

	protected static void setCryptDecryptApp(EncryptDecrypt cryptDecryptapp) {
		cryptDecryptApp = cryptDecryptapp;
	}

//	protected static void setOutagesMap(HashMap<String, String> map) {
//		outagesMap = map;
//	}

//	protected static HashMap<String, String> getOutagesMap() {
//		return outagesMap;
//	}

	protected static String getSpencerTS() {
		return spencerTS;
	}

	protected static void setSpencerTS(String spencerTs) {
		spencerTS = spencerTs;
	}

	protected static void setSchedFileData(ScheduleFileData sfd) {
		schedFileData = sfd;
	}

	protected static ScheduleFileData getSchedFileData() {
		if (schedFileData == null) {
			return schedFileData = new ScheduleFileData();
		}
		return schedFileData;
	}

	protected static Calendar getLastValueDate() {
		if (null == lastValueDate) {
			lastValueDate = Calendar.getInstance();
			lastValueDate.set(1900, 1, 1);
		}
		return lastValueDate;
	}

	protected static void setLastValueDate(Calendar lvd) {
		lastValueDate = lvd;
	}

	protected static String getDefaultProgramLocationBak() {
		return defaultProgramLocationBak;
	}

	protected static void setDefaultProgramLocationBak(String dplb) {
		defaultProgramLocationBak = dplb;
	}

	protected static File getBriefingFileBak() {
		return briefingFileBak;
	}

	protected static void setBriefingFileBak(File briefingFilBak) {
		briefingFileBak = briefingFilBak;
	}

	protected static boolean isDefaultProgramLocationBakExists() {
		return defaultProgramLocationBakExists;
	}

	protected static void setDefaultProgramLocationBakExists(boolean defaultProgramLocationBakExist) {
		defaultProgramLocationBakExists = defaultProgramLocationBakExist;
	}

	protected static boolean isCheckDriveMappping() {
		return checkDriveMappping;
	}

	protected static void setCheckDriveMappping(boolean checkDriveMapp) {
		checkDriveMappping = checkDriveMapp;
	}

	protected static Date getNextDstDate() {
		return nextDstDate;
	}

	protected static void setNextDstDate(Date nextDstDat) {
		nextDstDate = nextDstDat;
	}

	protected static long getNextDstDays() {
		return nextDstDays;
	}

	protected static void setNextDstDays(long nextDstDay) {
		nextDstDays = nextDstDay;
	}

	protected static HashMap<String, String> getResOutagesMap() {
		return resOutagesMap;
	}

	protected static void setResOutagesMap(HashMap<String, String> resOutageMap) {
		resOutagesMap = resOutageMap;
	}
}
