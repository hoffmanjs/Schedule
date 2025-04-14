package usace.wm.schedule;

import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

public class DlyData {
	private String damName = null;
	private String date = null;
	private Calendar calendarDate = null;
	private Calendar lastValueDate = null;
	private String dlyGenPower = null;
	private Double dlyGenPowerNum = Double.valueOf(0.0D);
	private String actGenPower = null;
	private Double actGenPowerNum = Double.valueOf(0.0D);
	private String flowPower = null;
	private Double flowPowerNum = Double.valueOf(0.0D);
	private String flowTotal = null;
	private Double flowTotalNum = null;
	private String poolElev = null;
	private Double poolElevationNum = Double.valueOf(0.0D);
	private Vector<String> siteNames = null;
	private int damNumber = -1;
	private String oD = null;
	private Double oDNum = Double.valueOf(0.0D);
	private String kcfs = null;
	private Double kcfsNum = Double.valueOf(0.0D);
	private String rescheduleEnergy = null;
	private Double rescheduleEnergyNum = Double.valueOf(0.0D);
	private String rescheduleOP = null;
	private Double rescheduleOPNum = Double.valueOf(0.0D);

	public void setDamName(String dName) {
		damName = dName;
	}

	public String getDamName() {
		return damName;
	}

	public void setDateYYMMDD(String dte) {
		date = dte;
		calendarDate = Util.getCalendarDateYYMMDD(date);
		if (Util.getLastValueDate().before(calendarDate)) {
			lastValueDate = calendarDate;
		}
	}

	public void setDateMMDDYY(String dte) {
		date = dte;
		calendarDate = Util.getCalendarDateMMDDYY(date);
		if (Util.getLastValueDate().before(calendarDate)) {
			lastValueDate = Calendar.getInstance();
			lastValueDate.setTime(calendarDate.getTime());
			Util.setLastValueDate(lastValueDate);
		}
	}

	public void setDate(String dte) {
		date = dte;
		calendarDate = Util.getCalendarDate(date);
		if (Util.getLastValueDate().before(calendarDate)) {
			lastValueDate = calendarDate;
		}
	}

	public String getDate() {
		return date;
	}

	public void setDlyGenPower(String pw) {
		dlyGenPower = pw;
		setDlyGenPowerNum(dlyGenPower);
	}

	public String getDlyGenPower() {
		return dlyGenPower;
	}

	public void setFlowPower(String flowPwr) {
		flowPower = flowPwr;
		setFlowPowerNum(flowPower);
	}

	public String getFlowPower() {
		return flowPower;
	}

	public void setFlowTotal(String flwTotal) {
		flowTotal = flwTotal;
		setFlowTotalNum(flowTotal);
	}

	public String getFlowTotal() {
		return flowTotal;
	}

	public void setPoolElev(String pe) {
		poolElev = pe;
		setPoolElevationNum(poolElev);
	}

	public String getPoolElev() {
		return poolElev;
	}

	public void setSiteNames(Vector<String> sitNames) {
		siteNames = sitNames;
	}

	public Vector<String> getSiteNames() {
		return siteNames;
	}

	public Calendar getCalendarDate() {
		return calendarDate;
	}

	public Double getDlyGenPowerNum() {
		return dlyGenPowerNum;
	}

	private void setDlyGenPowerNum(String pwr) {
		try {
			if (pwr.trim().length() > 0) {
				dlyGenPowerNum = Double.valueOf(Double.parseDouble(pwr));
			} else {
				dlyGenPowerNum = Double.valueOf(0.0D);
			}
		} catch (Exception e) {
			System.err.println("Error parsing Daily Generated Power Number: " + e);
		}
	}

	private void setPoolElevationNum(String pe) {
		try {
			if (pe.trim().length() > 0) {
				poolElevationNum = Double.valueOf(Double.parseDouble(pe));
			} else {
				poolElevationNum = Double.valueOf(0.0D);
			}
		} catch (Exception e) {
			System.err.println("Error parsing pool elevation number: " + e);
		}
	}

	protected Double getPoolElevationNum() {
		return poolElevationNum;
	}

	private void setFlowPowerNum(String flowPwr) {
		try {
			if (flowPwr.trim().length() > 0) {
				flowPowerNum = Double.valueOf(Double.parseDouble(flowPwr));
			} else {
				flowPowerNum = Double.valueOf(0.0D);
			}
		} catch (Exception e) {
			System.err.println("Error parsing Flow Power: " + e);
		}
	}

	public Double getFlowPowerNum() {
		return flowPowerNum;
	}

	public void setDamNumber(int damNumber) {
		damNumber = damNumber;
	}

	public int getDamNumber() {
		return damNumber;
	}

	protected void setActGenPower(String pwr) {
		actGenPower = pwr;
		setActlGenPowerNum(actGenPower);
	}

	protected String getActGenPower() {
		return actGenPower;
	}

	private void setActlGenPowerNum(String gp) {
		try {
			if (gp.trim().length() > 0) {
				actGenPowerNum = Double.valueOf(Double.parseDouble(gp));
			} else {
				actGenPowerNum = Double.valueOf(0.0D);
			}
		} catch (Exception e) {
			System.err.println("Error parsing Actual Generated Power Number: " + e);
		}
	}

	protected Double getActGenPowerNum() {
		return actGenPowerNum;
	}

	protected void setOD(String od) {
		oD = od;
		setODNum(oD);
	}

	protected String getOD() {
		return oD;
	}

	protected void setKcfs(String cfs) {
		kcfs = cfs;
		setKcfsNum(kcfs);
	}

	protected String getKcfs() {
		return kcfs;
	}

	private void setODNum(String od) {
		try {
			if (od.trim().length() > 0) {
				oDNum = Double.valueOf(Double.parseDouble(od));
			} else {
				oDNum = Double.valueOf(0.0D);
			}
		} catch (Exception e) {
			System.err.println("Error parsing Reschedule Number: " + e);
		}
	}

	protected Double getODNum() {
		return oDNum;
	}

	private void setKcfsNum(String cfsNum) {
		try {
			if (cfsNum.trim().length() > 0) {
				kcfsNum = Double.valueOf(Double.parseDouble(cfsNum));
			} else {
				kcfsNum = Double.valueOf(0.0D);
			}
		} catch (Exception e) {
			System.err.println("Error parsing KCFS Number: " + e);
		}
	}

	protected Double getKcfsNum() {
		return kcfsNum;
	}

	private void setFlowTotalNum(String ftn) {
		try {
			if (ftn.trim().length() > 0) {
				flowTotalNum = Double.valueOf(Double.parseDouble(ftn));
			} else {
				flowTotalNum = Double.valueOf(0.0D);
			}
		} catch (Exception e) {
			System.err.println("Error parsing Flow Total Number: " + e);
		}
	}

	protected Double getFlowTotalNum() {
		return flowTotalNum;
	}

	protected void setRescheduleEnergy(String reschedule) {
		rescheduleEnergy = reschedule;
		setRescheduleNum(reschedule);
	}

	protected String getRescheduleEnergy() {
		return rescheduleEnergy;
	}

	private void setRescheduleNum(String rescheduleNum) {
		try {
			rescheduleEnergyNum = Double.valueOf(Double.parseDouble(rescheduleNum));
		} catch (Exception e) {
			System.err.println("Error parsing Reschedule Energy Number: " + e);
		}
	}

	protected Double getRescheduleEnergyNum() {
		return rescheduleEnergyNum;
	}

	protected void setRescheduleOP(String rescheduleop) {
		rescheduleOP = rescheduleop;
		setRescheduleOpNum(rescheduleop);
	}

	protected String getRescheduleOP() {
		return rescheduleOP;
	}

	private void setRescheduleOpNum(String rescheduleOpNum) {
		try {
			rescheduleOPNum = Double.valueOf(Double.parseDouble(rescheduleOpNum));
		} catch (Exception e) {
			System.err.println("Error parsing Reschedule OP Number: " + e);
		}
	}

	protected Double getRescheduleOpNum() {
		return rescheduleOPNum;
	}

	protected Calendar getLastValueDate() {
		return lastValueDate;
	}

	protected void setLastValueDate(Calendar lastValueDate) {
		this.lastValueDate = lastValueDate;
	}
}
