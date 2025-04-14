package usace.wm.schedule;

import java.io.PrintStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DailyPrecipData {
	private String lid = null;
	private String name = null;
	private String state = null;
	private String value = null;
	private double valueNumber = -9999.99D;
	private String obstime = null;
	private Calendar time = null;

	public DailyPrecipData() {
	}

	public DailyPrecipData(String id, String nm, String st, String val, String obtime) {
		setLid(id);
		setName(nm);
		setState(st);
		setValue(val);
		setObstime(obtime);
	}

	public String getLid() {
		return lid;
	}

	public void setLid(String ld) {
		lid = ld;
	}

	public String getName() {
		return name;
	}

	public void setName(String nme) {
		name = nme;
	}

	public String getState() {
		return state;
	}

	public void setState(String stte) {
		state = stte;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String val) {
		value = val;
		try {
			setValueNumber(Double.parseDouble(value));
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
		}
	}

	public String getObstime() {
		return obstime;
	}

	public void setObstime(String obtme) {
		obstime = obtme;
		Calendar cal = Calendar.getInstance();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date processDate = sdf.parse(obstime);
			cal.setTime(processDate);
			setTime(cal);
		} catch (ParseException e) {
			System.err.println("Error parsing obstime: " + e);
		}
		setTime(time);
	}

	public Calendar getTime() {
		return time;
	}

	private void setTime(Calendar tme) {
		time = tme;
	}

	public double getValueNumber() {
		return valueNumber;
	}

	private void setValueNumber(double valNumber) {
		valueNumber = valNumber;
	}
}
