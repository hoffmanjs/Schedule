package usace.wm.schedule;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

public class CalculateEnergy {
	private HashMap<String, DlyData> dlyData = null;
	private Double[] totalDlyEnergy;
	private StringBuffer energyScreenText = null;
	private Vector<String> siteNames = null;

	public CalculateEnergy(HashMap<String, DlyData> dlyDataMap, StringBuffer screenTxt, Vector<String> siteNames) {
		setDlyData(dlyDataMap);
		setEnergyScreenText(screenTxt);
		setSiteNames(siteNames);
	}

	public Double[] calculateTotalDlyEnergy() {
		HashMap<String, DlyData> datamap = getDlyData();

		totalDlyEnergy = new Double[7];
		totalDlyEnergy[0] = Double.valueOf(0.0D);
		totalDlyEnergy[1] = Double.valueOf(0.0D);
		totalDlyEnergy[2] = Double.valueOf(0.0D);
		totalDlyEnergy[3] = Double.valueOf(0.0D);
		totalDlyEnergy[4] = Double.valueOf(0.0D);
		totalDlyEnergy[5] = Double.valueOf(0.0D);
		totalDlyEnergy[6] = Double.valueOf(0.0D);

		Set<String> set = datamap.keySet();
		Iterator<String> it = set.iterator();
		Double energy = Double.valueOf(0.0D);
		while (it.hasNext()) {
			String key = (String) it.next();
			DlyData data = (DlyData) datamap.get(key);
			int num = data.getDamNumber();

			energy = Double.valueOf(totalDlyEnergy[num].doubleValue() + data.getDlyGenPowerNum().doubleValue());
			totalDlyEnergy[num] = energy;
		}
		Vector<String> sites = getSiteNames();
		for (int count = 0; count < 7; count++) {
			Iterator<String> itsite = sites.iterator();
			while (itsite.hasNext()) {
				String key = (String) itsite.next();
				String str1 = ((DlyData) datamap.get(key + count)).getDlyGenPower();
			}
		}
		return totalDlyEnergy;
	}

	private void setDlyData(HashMap<String, DlyData> dlydata) {
		dlyData = dlydata;
	}

	private HashMap<String, DlyData> getDlyData() {
		return dlyData;
	}

	public void setEnergyScreenText(StringBuffer energyText) {
		energyScreenText = energyText;
	}

	public StringBuffer getEnergyScreenText() {
		return energyScreenText;
	}

	public void setSiteNames(Vector<String> sites) {
		siteNames = sites;
	}

	public Vector<String> getSiteNames() {
		return siteNames;
	}
}
