package usace.wm.schedule;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class CwmsTableData {

	private String dateTime = null;
	private String date = null;
	private double value = -99.99;
	private int count = 0;
	String timeSeries = null;
	String unitNumber = null;
	String unitNumbers = null;
	int unit = -99;
	ArrayList<Integer> units = new ArrayList<Integer>();
	boolean outage = false;
	String locationID = null;

	protected CwmsTableData(String ts, String dt, String val) {
//		System.out.println(ts + " = " + dt + " = " + val);
		setDateTime(dt);
		setValue(val);
		setTimeSeries(ts);
//		setUnitNumber(ts);
		setUnitNumbers(val);
		setDate(dt);
	}

	protected void setUnitNumber(String ts) {
		unitNumber = ts.substring(ts.indexOf("-Unit") + 5, ts.indexOf("."));
		setUnit(Integer.parseInt(unitNumber));
	}
	
	protected void setUnitNumbers(String ts) {
		unitNumbers = ts;
		setUnits(ts);
	}

	protected String getUnitNumber() {
		return unitNumber;
	}

	/**
	 * @return the dateTime
	 */
	protected String getDateTime() {
		return dateTime;
	}

	/**
	 * @param dateTime
	 *            the dateTime to set
	 */
	protected void setDateTime(String datetime) {
		dateTime = datetime;
	}

	/**
	 * @return the value
	 */
	protected double getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	protected void setValue(String val) {
		if (null != val) {
			value = Double.parseDouble(val);
			if (value == 0) {
				setOutage(false);
			} else {
				setOutage(true);
			}
		}
	}

	/**
	 * @return the count
	 */
	protected int getCount() {
		return count;
	}

	/**
	 * @param count
	 *            the count to set
	 */
	protected void setCount(int c) {
		count = c;
	}

	protected String getTimeSeries() {
		return timeSeries;
	}

	protected void setTimeSeries(String ts) {
		timeSeries = ts;
		setLocationID(ts.substring(0, 4));
	}

	protected boolean isOutage() {
		return outage;
	}

	protected void setOutage(boolean out) {
		outage = out;
	}

	protected String getLocationID() {
		return locationID;
	}

	protected void setLocationID(String id) {
		locationID = id;
	}

	protected String getDate() {
		return date;
	}

	protected void setDate(String d) {
		Calendar cal = null;
		try {
//			System.out.println(d);
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 2016-02-19 06:00:00.0 yyyy-MM-dd HH:mm:ss.s
			df.setTimeZone(TimeZone.getTimeZone("UTC"));
			cal = Calendar.getInstance();
			cal.setTime(df.parse(d)); //2021-03-23 06:00:00
			Date dt = cal.getTime();
//			System.out.println("dt: " + dt);
			DateFormat dtFormat = new SimpleDateFormat("yyMMdd"); //160223
			dtFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
			date = dtFormat.format(dt);
		} catch (ParseException e) {
			e.printStackTrace();
		}		
	}

	protected int getUnit() {
		return unit;
	}

	protected void setUnit(int unit) {
		this.unit = unit;
	}

	protected String getUnitNumbers() {
		return unitNumbers;
	}

	protected ArrayList<Integer> getUnits() {
		return units;
	}

	protected void setUnits(String unts) {
		Double y = Double.parseDouble(unts.trim());
		String unitsOut = String.valueOf(y.intValue());
		for(int x=0; x < unitsOut.length(); x++){
			String num = unitsOut.substring(0+x, x+1);
			units.add(Integer.parseInt(num));
		}
	}
}
