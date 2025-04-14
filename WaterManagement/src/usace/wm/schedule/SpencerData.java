package usace.wm.schedule;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class SpencerData {

	private String dateTime = null;
	private String date = null;
	private double value = -0.0;
	private int count = 0;
	String timeSeries = null;

	protected SpencerData(String ts, String dt, String val) {
		setDateTime(dt);
		setValue(val);
		setTimeSeries(ts);
		setDate(dt);
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
	protected void setDateTime(String dateTime) {
		this.dateTime = dateTime;
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
		if(null != val){
			value = Double.parseDouble(val)/1000;
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
	}

	protected String getDate() {
		return date;
	}

	protected void setDate(String d) {
		Calendar cal = null;
		try {
//			System.out.println(d);
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.s");// 2016-02-19 06:00:00.0
			df.setTimeZone(TimeZone.getTimeZone("UTC"));
			cal = Calendar.getInstance();
			cal.setTime(df.parse(d));
			Date dt = cal.getTime();
//			System.out.println("dt: " + dt);
			DateFormat dtFormat = new SimpleDateFormat("yyMMdd"); //160223
			dtFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
			date = dtFormat.format(dt);
		} catch (ParseException e) {
			e.printStackTrace();
		}		
	}
}
