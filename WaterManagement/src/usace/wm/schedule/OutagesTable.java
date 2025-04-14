package usace.wm.schedule;

import java.util.ArrayList;

public class OutagesTable {
	String locId = null;
	String date = null;
	String ts = null;
	String unit = null;
	Object[] units = null;

	protected OutagesTable(String id, String day, String ts, String num){
		setLocId(id);
		setDate(day);
		setTs(ts);
		setUnit(num);
	}
	
	protected OutagesTable(String id, String day, String ts, Object[] num){
		setLocId(id);
		setDate(day);
		setTs(ts);
		setUnits(num);
	}
	
	protected String getLocId() {
		return locId;
	}

	protected void setLocId(String locid) {
		locId = locid;
	}

	protected String getDate() {
		return date;
	}

	protected void setDate(String dte) {
		date = dte;
	}

	protected String getTs() {
		return ts;
	}

	protected void setTs(String tms) {
		ts = tms;
	}

	protected String getUnit() {
		return unit;
	}

	protected void setUnit(String unt) {
		unit = unt;
	}

	protected Object[] getUnits() {
		return units;
	}

	protected void setUnits(Object[] unts) {
		units = unts;
	}
}
