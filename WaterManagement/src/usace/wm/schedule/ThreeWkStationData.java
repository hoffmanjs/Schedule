package usace.wm.schedule;

public class ThreeWkStationData {

	private String month = null;
	private int monthID = -1;
	private String station = null;
	private int stationID = -1;
	private int inflow = -1;
	private int evap = -1;
	private int release = -1;
	private double inflowValueCalc = -9.9;

	/**
	 * @return the month
	 */
	public String getMonth() {
		return month;
	}

	/**
	 * @param month
	 *            the month to set
	 */
	public void setMonth(String month) {
		this.month = month;
	}

	/**
	 * @return the monthID
	 */
	public int getMonthID() {
		return monthID;
	}

	/**
	 * @param monthID
	 *            the monthID to set
	 */
	public void setMonthID(int monthID) {
		this.monthID = monthID;
	}

	/**
	 * @return the station
	 */
	public String getStation() {
		return station;
	}

	/**
	 * @param station
	 *            the station to set
	 */
	public void setStation(String station) {
		this.station = station;
	}

	/**
	 * @return the stationID
	 */
	public int getStationID() {
		return stationID;
	}

	/**
	 * @param stationID
	 *            the stationID to set
	 */
	public void setStationID(int stationID) {
		this.stationID = stationID;
	}

	/**
	 * @return the release
	 */
	public int getRelease() {
		return release;
	}

	/**
	 * @param release
	 *            the release to set
	 */
	public void setRelease(int release) {
		this.release = release;
	}

	/**
	 * @return the inflow
	 */
	public int getInflow() {
		return inflow;
	}

	/**
	 * @param inflow
	 *            the inflow to set
	 */
	public void setInflow(int inflow) {
		this.inflow = inflow;
	}

	/**
	 * @return the evap
	 */
	public int getEvap() {
		return evap;
	}

	/**
	 * @param evap
	 *            the evap to set
	 */
	public void setEvap(int evap) {
		this.evap = evap;
	}

	/**
	 * @return the inflowValueCalc
	 */
	public double getInflowValueCalc() {
		return inflowValueCalc;
	}

	/**
	 * @param inflowValueCalc
	 *            the inflowValueCalc to set
	 */
	public void setInflowValueCalc(double inflowValueCalc) {
		this.inflowValueCalc = inflowValueCalc;
	}
}
