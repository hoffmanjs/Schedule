package usace.wm.schedule;

import java.util.ArrayList;
import java.util.List;

public class ElevationCapacity {
	private String site = null;
	private int siteID = -99;
	private List<Double> elevations = new ArrayList<Double>();
	private List<Double> capability = new ArrayList<Double>();

	protected String getSite() {
		return site;
	}

	protected void setSite(String ste) {
		site = ste;
	}

	protected List<Double> getElevations() {
		return elevations;
	}

	protected void setElevations(List<Double> elevation) {
		elevations = elevation;
	}

	protected List<Double> getCapability() {
		return capability;
	}

	protected void setCapability(List<Double> cap) {
		capability = cap;
	}

	protected int getSiteID() {
		return siteID;
	}

	protected void setSiteID(int sitID) {
		siteID = sitID;
	}
}
