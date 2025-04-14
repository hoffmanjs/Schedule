package usace.wm.schedule;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.LinkedList;

import javax.swing.JOptionPane;

public class ScheduleFileData {
	private String[] capacityToleranceInfo = null;
	private String[] satCapTol = null;
	private String[] monCapTol = null;
	private String[] plusCapTol = null;
	private String[] minusCapTol = null;
	private HashMap<String, DlyData> dlyDataMap = null;
	private HashMap<String, String> spcMap = null;
	LinkedList<String> dates = null;
	private HashMap<String, String> bbElevMap = null;
	private HashMap<String, String> rescheduleBBElevMap = null;
	private HashMap<String, String> totCalEnrgyMap = null;
	private HashMap<String, String> totReschedCalEnrgyMap = null;
	private HashMap<String, String> id24Map = null;
	private HashMap<String, String[]> outageMap = null;
	private HashMap<String, String[]> coefficientMap = null;
	private HashMap<String, String[]> siteGEInfo = null;
	private String energyEndDate = null;
	private String dailyEndDate = null;
	private String schFileEndDate = null;
	private String schFileStartDate = null;
	private Calendar rescheduleDataEndDate = null;
	private static int LINES_TO_READ = 14;
	private LinkedList datesMap = null;

	public ScheduleFileData() {
		//TODO
//		getScheduleFileData();
	}
	
	private ScheduleFileData getScheduleFileData() {
		Util.checkDriveMaping();
		try {
			File theSchfile = new File(Util.getDefaultProgramLocation() + File.separator + Util.getDefaultScheduleFileName()).getAbsoluteFile();

			if ((theSchfile.exists()) && (theSchfile.length() > 0)) {				
				if (theSchfile.canRead() && theSchfile.canWrite()) {
					FileInputStream fstream = new FileInputStream(theSchfile);

					DataInputStream in = new DataInputStream(fstream);
					BufferedReader br = new BufferedReader(new InputStreamReader(in));

					HashMap<String, String[]> siteInfo = new HashMap<String, String[]>();
					DecimalFormat fmt = new DecimalFormat("#####");
					if (fstream.toString().length() > 0) {
						int lineCount = 0;
						String strLine;
						while ((strLine = br.readLine()) != null) {
							if ((lineCount >= 0) && (lineCount < 14)) {
								StringTokenizer token = new StringTokenizer(strLine);
								String[] data = new String[token.countTokens()];
								int record = 0;
								while (token.hasMoreTokens()) {
									data[record] = token.nextToken();
									record++;
								}
								siteInfo.put(Integer.toString(lineCount), data);
							}
							if (strLine.indexOf("99999") != -1) {
								setCapacityToleranceInfoTxt(strLine);
							}
							if ((!strLine.equals(null)) && (lineCount < 14)) {
								StringTokenizer tok = new StringTokenizer(strLine);
								String[] dat = new String[tok.countTokens()];
								int rec = 0;
								while (tok.hasMoreTokens()) {
									dat[rec] = tok.nextToken();
									if ((rec == 25) && (Double.parseDouble(dat[rec]) > 0.0D)) {
										setEnergyEndDate(fmt.format(Double.parseDouble(dat[0])).toString());
									}
									if (rec == 24) {
										boolean flag = false;
										char[] value = dat[rec].toCharArray();
										for (int x = 0; x < value.length; x++) {
											if (!Character.isDigit(value[x])) {
												break;
											}
											flag = true;
										}
										if ((flag) && (Double.parseDouble(dat[rec]) > 0.0D)) {
											setDailyEndDate(fmt.format(Double.parseDouble(dat[0])).toString());
										}
									}
									if ((rec == 0) && (Double.parseDouble(dat[rec]) > 0.0D)) {
										setSchFileEndDate(fmt.format(Double.parseDouble(dat[0])).toString());
									}
									rec++;
								}
							}
							lineCount++;
						}
						br.close();
						setSiteInfo(siteInfo);
					}
				} else {
					JOptionPane.showMessageDialog(null,
							"Can't read or write to the schfile.",
							"Error Reading or Writing to Schfile file.", 0);
				}
			} else {
				String errorText = null;
				if(!theSchfile.exists()){
					errorText = "Please select a valid schfile file location.\n" + "Schfile file DOES NOT exist!!!";
				}else {
					errorText = "Please select a valid schfile file.\n" + "Schfile file length = " + theSchfile.length();
				}
				JOptionPane.showMessageDialog(null,
						errorText,
						"Error in reading Schfile file.", 0);
				if (Util.getFileChooser(null)) {
					getScheduleFileData();
				} else {
					System.exit(0);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Please select a valid schfile file location or empty schfile file.",
					"Invalid schfile file.", 0);
		}
		//TODO
//		getSpcDbInfo();
		return this;
	}

	/**
	 * 
	 * @param map
	 */
	private void setSiteInfo(HashMap<String, String[]> map) {
		siteGEInfo = new HashMap<String, String[]>();
		siteGEInfo = map;
		dlyDataMap = new HashMap<String, DlyData>();
		bbElevMap = new HashMap<String, String>();
		rescheduleBBElevMap = new HashMap<String, String>();
		spcMap = new HashMap<String, String>();
		dates = new LinkedList<String>();
		id24Map = new HashMap<String, String>();
		totCalEnrgyMap = new HashMap<String, String>();
		totReschedCalEnrgyMap = new HashMap<String, String>();
		outageMap = new HashMap<String, String[]>();
		coefficientMap = new HashMap<String, String[]>();
		for (int line = 0; line < siteGEInfo.keySet().size(); line++) {
			String[] data = (String[]) siteGEInfo.get(Integer.toString(line));
			if ((line >= 0) && (line < 14)) {
				setDlyStatInfo(data, line);

				setSpcInfo(data, line);

				set24ID(data, line);

				setOutageInfo(data, line);

				setCoefficientInfo(data, line);

				setBbElevMap(data, line);

				setRescheduleBBElevMap(data, line);

				setTotCalEnrgyMap(data, line);

				setReschedTotCalEnrgyMap(data, line);
			}
		}
	}

	/**
	 * 
	 * @param data
	 * @param line
	 */
	private void setCoefficientInfo(String[] data, int line) {
		String key = null;
		String[] coefficient = new String[6];
		int record = 0;
		for (int x = 0; x < data.length; x++) {
			if ((x == 0) && (line < LINES_TO_READ)) {
				key = data[0].substring(0, 6);
			}
			if ((x >= 73) && (x <= 78) && (line < LINES_TO_READ)) {
				coefficient[record] = data[x];
				record++;
			}
		}
		coefficientMap.put(key, coefficient);
	}

	private void setOutageInfo(String[] data, int line) {
		String key = null;
		String[] outage = new String[6];
		int record = 0;
		for (int x = 0; x < data.length; x++) {
			if ((x == 0) && (line < LINES_TO_READ)) {
				key = data[0].substring(0, 6);
			}
			if ((x >= 61) && (x <= 66) && (line < LINES_TO_READ)) {
				outage[record] = data[x];
				record++;
			}
		}
		outageMap.put(key, outage);
	}

	private void setBBElevChng(String[] data, int line) {
		String key = null;
		String[] outage = new String[6];
		int record = 0;
		for (int x = 0; x < data.length; x++) {
			if ((x == 0) && (line < LINES_TO_READ)) {
				key = data[0].substring(0, 6);
			}
			if ((x >= 61) && (x <= 66) && (line < LINES_TO_READ)) {
				outage[record] = data[x];
				record++;
			}
		}
		outageMap.put(key, outage);
	}

	private void setBbElevMap(String[] data, int line) {
		String key = "";
		for (int x = 0; x < data.length; x++) {
			if ((x == 0) && (line < 14)) {
				key = data[0].substring(0, 6);
			}
			if ((key.length() > 0) && (x == 47)) {
				bbElevMap.put(key, data[x]);
			}
		}
	}

	private void setRescheduleBBElevMap(String[] data, int line) {
		String key = "";
		for (int x = 0; x < data.length; x++) {
			if ((x == 0) && (line < 14)) {
				key = data[0].substring(0, 6);
			}
			if ((key.length() > 0) && (x == 44)) {
				rescheduleBBElevMap.put(key, data[x]);
			}
		}
	}

	private void setTotCalEnrgyMap(String[] data, int line) {
		String key = "";
		for (int x = 0; x < data.length; x++) {
			if ((x == 0) && (line < 14)) {
				key = data[0].substring(0, 6);
			}
			if (key.length() > 0) {
				if (x == 53) {
					totCalEnrgyMap.put(key, data[x]);
				}
			}
		}
	}

	private void setReschedTotCalEnrgyMap(String[] data, int line) {
		String key = "";
		for (int x = 0; x < data.length; x++) {
			if ((x == 0) && (line < 14)) {
				key = data[0].substring(0, 6);
			}
			if (key.length() > 0) {
				if (x == 50) {
					totReschedCalEnrgyMap.put(key, data[x]);
				}
			}
		}
	}

	private void set24ID(String[] data, int line) {
		String key = null;
		for (int x = 0; x < data.length; x++) {
			if ((x == 0) && (line < 14)) {
				key = data[0].substring(0, 6);
			}
			if ((x == 59) && (line < 14)) {
				id24Map.put(key, data[x]);
			}
		}
	}

	/**
	 * 
	 * @param data
	 * @param line
	 */
	private void setSpcInfo(String[] data, int line) {
		String key = null;

		for (int x = 0; x < data.length; x++) {
			if ((x == 0) && (line < 14)) {
				key = data[0].substring(0, 6);
			}
			if ((x == 56) && (line < 14)) {
				spcMap.put(key, data[x]);
				dates.add(key);
			}
		}
	}

	/**
	 * Get Spencer values from the CWMS Database
	 */
	//TODO
//	private void getSpcDbInfo() {
//		spcMap.clear();
//
//		List<SpencerData> spc = Util.getSpencerValues(Util.getCalendarYYMMDD(dates.getFirst()),
//				Util.getCalendarYYMMDD(dates.getLast())); // TODO
//		Iterator<SpencerData> spcIt = spc.iterator();
//		while (spcIt.hasNext()) {
//			SpencerData spcdata = spcIt.next();
//			String date = spcdata.getDate();
//			double value = spcdata.getValue();
//
//			if (spcMap.containsKey(spcdata.getDate())) {
//				spcMap.remove(date);
//			}
//			spcMap.put(date, String.valueOf(value));
//		}
//	}

	protected HashMap<String, String[]> getSiteInfo() {
		return siteGEInfo;
	}

	private void setDlyStatInfo(String[] data, int line) {
		DlyData dly = new DlyData();
		int count = 0;
		for (int x = 0; x < data.length; x++) {

			// Only for DlyData info
			if (x < 25 && line < 14) {
				if (x == 5 || x == 9 || x == 13 || x == 17 || x == 21) {
					dly = new DlyData();
					dly.setDateYYMMDD(data[0].substring(0, 6));
				}

				if (x == 0) {
					dly.setDateYYMMDD(data[x].substring(0, 6));
					if (line == 0) {
						setSchFileStartDate(data[x].substring(0, 6));
					}
				} else if (x == 1 || x == 5 || x == 9 || x == 13 || x == 17 || x == 21) {
					dly.setDlyGenPower(data[x]);
					// Energy
					switch (x) {
					case 1:
						dly.setDamName("FTPK");
						break;
					case 5:
						dly.setDamName("GARR");
						break;
					case 9:
						dly.setDamName("OAHE");
						break;
					case 13:
						dly.setDamName("BEND");
						break;
					case 17:
						dly.setDamName("FTRA");
						break;
					case 21:
						dly.setDamName("GAPT");
						break;
					default:
						break;
					}

				} else if (x == 2 || x == 6 || x == 10 || x == 14 || x == 18 || x == 22) {
					// Flow Total
					dly.setFlowTotal(data[x]);
				} else if (x == 3 || x == 7 || x == 11 || x == 15 || x == 19 || x == 23) {
					// Flow Power
					dly.setFlowPower(data[x]);
				} else if (x == 4 || x == 8 || x == 12 || x == 16 || x == 20 || x == 24) {
					// Pool Elevation
					dly.setPoolElev(data[x]);
					dlyDataMap.put(dly.getDate() + "_" + count, dly);
					count++;
				}
			}

			// DlyData - Actuals
			if (x >= 25 && x < 61 && line < 14) {
				// Actual Generated Power
				if (x == 25 || x == 28 || x == 31 || x == 34 || x == 37 || x == 40) {
					String keyEnergy = null;
					// Energy
					switch (x) {
					case 25:
						keyEnergy = dly.getDate() + "_0";
						break;
					case 28:
						keyEnergy = dly.getDate() + "_1";
						break;
					case 31:
						keyEnergy = dly.getDate() + "_2";
						break;
					case 34:
						keyEnergy = dly.getDate() + "_3";
						break;
					case 37:
						keyEnergy = dly.getDate() + "_4";
						break;
					case 40:
						keyEnergy = dly.getDate() + "_5";
						break;
					default:
						break;
					}

					DlyData energy = dlyDataMap.get(keyEnergy);
					energy.setActGenPower(data[x]);
					dlyDataMap.put(keyEnergy, energy);

				} else if (x == 26 || x == 29 || x == 32 || x == 35 || x == 38 || x == 41) {
					// OD info
					String odKey = null;
					// Energy
					switch (x) {
					case 26:
						odKey = dly.getDate() + "_0";
						break;
					case 29:
						odKey = dly.getDate() + "_1";
						break;
					case 32:
						odKey = dly.getDate() + "_2";
						break;
					case 35:
						odKey = dly.getDate() + "_3";
						break;
					case 38:
						odKey = dly.getDate() + "_4";
						break;
					case 41:
						odKey = dly.getDate() + "_5";
						break;
					default:
						break;
					}
					DlyData od = dlyDataMap.get(odKey);
					od.setOD(data[x]);
					dlyDataMap.put(odKey, od);

				} else if (x == 27 || x == 30 || x == 33 || x == 36 || x == 39 || x == 42) {
					// KCFS
					String kCfsKey = null;
					// Energy
					switch (x) {
					case 27:
						kCfsKey = dly.getDate() + "_0";
						break;
					case 30:
						kCfsKey = dly.getDate() + "_1";
						break;
					case 33:
						kCfsKey = dly.getDate() + "_2";
						break;
					case 36:
						kCfsKey = dly.getDate() + "_3";
						break;
					case 39:
						kCfsKey = dly.getDate() + "_4";
						break;
					case 42:
						kCfsKey = dly.getDate() + "_5";
						break;
					default:
						break;
					}
					DlyData cfs = dlyDataMap.get(kCfsKey);
					cfs.setKcfs(data[x]);
					dlyDataMap.put(kCfsKey, cfs);

				} else if (x == 43 || x == 46 || x == 49 || x == 52 || x == 55 || x == 58) {
					// Reschedule Energy
					String reschedEnergyKey = null;
					switch (x) {
					case 43:
						reschedEnergyKey = dly.getDate() + "_0";
						break;
					case 46:
						reschedEnergyKey = dly.getDate() + "_1";
						break;
					case 49:
						reschedEnergyKey = dly.getDate() + "_2";
						break;
					case 52:
						reschedEnergyKey = dly.getDate() + "_3";
						break;
					case 55:
						reschedEnergyKey = dly.getDate() + "_4";
						break;
					case 58:
						reschedEnergyKey = dly.getDate() + "_5";
						break;
					default:
						break;
					}
					DlyData reschedEnergy = dlyDataMap.get(reschedEnergyKey);
					reschedEnergy.setRescheduleEnergy(data[x]);
					dlyDataMap.put(reschedEnergyKey, reschedEnergy);

				} else if (x == 45 || x == 48 || x == 51 || x == 54 || x == 57 || x == 60) {
					// Reschedule KCFS
					String reschedOPkey = null;
					switch (x) {
					case 45:
						reschedOPkey = dly.getDate() + "_0";
						break;
					case 48:
						reschedOPkey = dly.getDate() + "_1";
						break;
					case 51:
						reschedOPkey = dly.getDate() + "_2";
						break;
					case 54:
						reschedOPkey = dly.getDate() + "_3";
						break;
					case 57:
						reschedOPkey = dly.getDate() + "_4";
						break;
					case 60:
						reschedOPkey = dly.getDate() + "_5";
						break;
					default:
						break;
					}
					DlyData reschedOP = dlyDataMap.get(reschedOPkey);
					reschedOP.setRescheduleOP(data[x]);
					dlyDataMap.put(reschedOPkey, reschedOP);
				}
			}
		}
	}

	// private void setDlyStatInfo(String[] data, int line)
	// {
	// DlyData dly = new DlyData();
	// int count = 0;
	// for (int x = 0; x < data.length; x++)
	// {
	// if ((x < 25) && (line < 14))
	// {
	// if ((x == 5) || (x == 9) || (x == 13) || (x == 17) || (x == 21))
	// {
	// dly = new DlyData();
	// dly.setDateYYMMDD(data[0].substring(0, 6));
	// }
	// if (x == 0)
	// {
	// dly.setDateYYMMDD(data[x].substring(0, 6));
	// if (line == 0) {
	// setSchFileStartDate(data[x].substring(0, 6));
	// }
	// }
	// else
	// {
	// if ((x == 1) || (x == 5) || (x == 9) || (x == 13) || (x == 17) || (x ==
	// 21)) {
	// dly.setDlyGenPower(data[x]);
	// }
	// switch (x)
	// {
	// case 1:
	// dly.setDamName("FTPK");
	// break;
	// case 5:
	// dly.setDamName("GARR");
	// break;
	// case 9:
	// dly.setDamName("OAHE");
	// break;
	// case 13:
	// dly.setDamName("BEND");
	// break;
	// case 17:
	// dly.setDamName("FTRA");
	// break;
	// case 21:
	// dly.setDamName("GAPT");
	// break;
	// default:
	// break;
	//
	// if ((x == 2) || (x == 6) || (x == 10) || (x == 14) || (x == 18) || (x ==
	// 22))
	// {
	// dly.setFlowTotal(data[x]);
	// }
	// else if ((x == 3) || (x == 7) || (x == 11) || (x == 15) || (x == 19) ||
	// (x == 23))
	// {
	// dly.setFlowPower(data[x]);
	// }
	// else if ((x == 4) || (x == 8) || (x == 12) || (x == 16) || (x == 20) ||
	// (x == 24))
	// {
	// dly.setPoolElev(data[x]);
	// dlyDataMap.put(dly.getDate() + "_" + count, dly);
	// count++;
	// }
	// break;
	// }
	// }
	// }
	// if ((x >= 25) && (x < 61) && (line < 14)) {
	// if ((x == 25) || (x == 28) || (x == 31) || (x == 34) || (x == 37) || (x
	// == 40))
	// {
	// String keyEnergy = null;
	// switch (x)
	// {
	// case 25:
	// keyEnergy = dly.getDate() + "_0";
	// break;
	// case 28:
	// keyEnergy = dly.getDate() + "_1";
	// break;
	// case 31:
	// keyEnergy = dly.getDate() + "_2";
	// break;
	// case 34:
	// keyEnergy = dly.getDate() + "_3";
	// break;
	// case 37:
	// keyEnergy = dly.getDate() + "_4";
	// break;
	// case 40:
	// keyEnergy = dly.getDate() + "_5";
	// break;
	// }
	// DlyData energy = (DlyData)dlyDataMap.get(keyEnergy);
	// energy.setActGenPower(data[x]);
	// dlyDataMap.put(keyEnergy, energy);
	// }
	// else if ((x == 26) || (x == 29) || (x == 32) || (x == 35) || (x == 38) ||
	// (x == 41))
	// {
	// String odKey = null;
	// switch (x)
	// {
	// case 26:
	// odKey = dly.getDate() + "_0";
	// break;
	// case 29:
	// odKey = dly.getDate() + "_1";
	// break;
	// case 32:
	// odKey = dly.getDate() + "_2";
	// break;
	// case 35:
	// odKey = dly.getDate() + "_3";
	// break;
	// case 38:
	// odKey = dly.getDate() + "_4";
	// break;
	// case 41:
	// odKey = dly.getDate() + "_5";
	// break;
	// }
	// DlyData od = (DlyData)dlyDataMap.get(odKey);
	// od.setOD(data[x]);
	// dlyDataMap.put(odKey, od);
	// }
	// else if ((x == 27) || (x == 30) || (x == 33) || (x == 36) || (x == 39) ||
	// (x == 42))
	// {
	// String kCfsKey = null;
	// switch (x)
	// {
	// case 27:
	// kCfsKey = dly.getDate() + "_0";
	// break;
	// case 30:
	// kCfsKey = dly.getDate() + "_1";
	// break;
	// case 33:
	// kCfsKey = dly.getDate() + "_2";
	// break;
	// case 36:
	// kCfsKey = dly.getDate() + "_3";
	// break;
	// case 39:
	// kCfsKey = dly.getDate() + "_4";
	// break;
	// case 42:
	// kCfsKey = dly.getDate() + "_5";
	// break;
	// }
	// DlyData cfs = (DlyData)dlyDataMap.get(kCfsKey);
	// cfs.setKcfs(data[x]);
	// dlyDataMap.put(kCfsKey, cfs);
	// }
	// else if ((x == 43) || (x == 46) || (x == 49) || (x == 52) || (x == 55) ||
	// (x == 58))
	// {
	// String reschedEnergyKey = null;
	// switch (x)
	// {
	// case 43:
	// reschedEnergyKey = dly.getDate() + "_0";
	// break;
	// case 46:
	// reschedEnergyKey = dly.getDate() + "_1";
	// break;
	// case 49:
	// reschedEnergyKey = dly.getDate() + "_2";
	// break;
	// case 52:
	// reschedEnergyKey = dly.getDate() + "_3";
	// break;
	// case 55:
	// reschedEnergyKey = dly.getDate() + "_4";
	// break;
	// case 58:
	// reschedEnergyKey = dly.getDate() + "_5";
	// break;
	// }
	// DlyData reschedEnergy = (DlyData)dlyDataMap.get(reschedEnergyKey);
	// reschedEnergy.setRescheduleEnergy(data[x]);
	// dlyDataMap.put(reschedEnergyKey, reschedEnergy);
	// }
	// else if ((x == 45) || (x == 48) || (x == 51) || (x == 54) || (x == 57) ||
	// (x == 60))
	// {
	// String reschedOPkey = null;
	// switch (x)
	// {
	// case 45:
	// reschedOPkey = dly.getDate() + "_0";
	// break;
	// case 48:
	// reschedOPkey = dly.getDate() + "_1";
	// break;
	// case 51:
	// reschedOPkey = dly.getDate() + "_2";
	// break;
	// case 54:
	// reschedOPkey = dly.getDate() + "_3";
	// break;
	// case 57:
	// reschedOPkey = dly.getDate() + "_4";
	// break;
	// case 60:
	// reschedOPkey = dly.getDate() + "_5";
	// break;
	// }
	// DlyData reschedOP = (DlyData)dlyDataMap.get(reschedOPkey);
	// reschedOP.setRescheduleOP(data[x]);
	// dlyDataMap.put(reschedOPkey, reschedOP);
	// }
	// }
	// }
	// }

	private void setCapacityToleranceInfoTxt(String strLine) {
		StringTokenizer token = new StringTokenizer(strLine);
		String[] data = new String[25];

		String[] satCap = new String[6];
		String[] monCap = new String[6];
		String[] plusTol = new String[6];
		String[] minusTol = new String[6];

		int reccount = 0;
		int satCount = 0;
		int monCount = 0;
		int plusCount = 0;
		int minusCount = 0;
		while (token.hasMoreTokens()) {
			String linedata = token.nextToken();
			data[reccount] = linedata;
			if ((reccount > 0) && (reccount <= 6)) {
				satCap[satCount] = linedata;
				satCount++;
			} else if ((reccount > 6) && (reccount <= 12)) {
				monCap[monCount] = linedata;
				monCount++;
			} else if ((reccount > 12) && (reccount <= 18)) {
				plusTol[plusCount] = linedata;
				plusCount++;
			} else if ((reccount > 18) && (reccount <= 24)) {
				minusTol[minusCount] = linedata;
				minusCount++;
			}
			reccount++;
		}
		setSatCapTol(satCap);
		setMonCapTol(monCap);
		setPlusCapTol(plusTol);
		setMinusCapTol(minusTol);
		setCapacityToleranceInfo(data);
	}

	protected void updateCapacityToleranceInfo(String[] strLine) {
		String[] satCap = new String[6];
		String[] monCap = new String[6];
		String[] plusTol = new String[6];
		String[] minusTol = new String[6];

		int reccount = 0;
		int satCount = 0;
		int monCount = 0;
		int plusCount = 0;
		int minusCount = 0;
		for (int x = 0; x < strLine.length; x++) {
			if ((x > 0) && (x <= 6)) {
				satCap[satCount] = strLine[x];
				satCount++;
			} else if ((x >= 7) && (x <= 12)) {
				monCap[monCount] = strLine[x];
				monCount++;
			} else if ((x >= 13) && (x <= 18)) {
				plusTol[plusCount] = strLine[x];
				plusCount++;
			} else if ((x >= 19) && (x <= 24)) {
				minusTol[minusCount] = strLine[x];
				minusCount++;
			}
			reccount++;
		}
		setSatCapTol(satCap);
		setMonCapTol(monCap);
		setPlusCapTol(plusTol);
		setMinusCapTol(minusTol);
		setCapacityToleranceInfo(strLine);
	}

	public void setCapacityToleranceInfo(String[] capacityToleranceInf) {
		capacityToleranceInfo = capacityToleranceInf;
	}

	public String[] getCapacityToleranceInfo() {
		return capacityToleranceInfo;
	}

	public void setSatCapTol(String[] satCapTl) {
		satCapTol = satCapTl;
	}

	public String[] getSatCapTol() {
		return satCapTol;
	}

	public void setMonCapTol(String[] monCapTl) {
		monCapTol = monCapTl;
	}

	public String[] getMonCapTol() {
		return monCapTol;
	}

	public void setPlusCapTol(String[] plusCapTl) {
		plusCapTol = plusCapTl;
	}

	public String[] getPlusCapTol() {
		return plusCapTol;
	}

	public void setMinusCapTol(String[] minusCapTl) {
		minusCapTol = minusCapTl;
	}

	public String[] getMinusCapTol() {
		return minusCapTol;
	}

	protected void setDlyDataMap(HashMap<String, DlyData> dlyDataMp) {
		dlyDataMap = dlyDataMp;
	}

	protected HashMap<String, DlyData> getDlyDataMap() {
		return dlyDataMap;
	}

	protected HashMap<String, String> getSpcMap() {
		return spcMap;
	}

	protected void setSpcMap(HashMap<String, String> smap) {
		spcMap = smap;
	}

	protected HashMap<String, String> getId24Map() {
		return id24Map;
	}

	protected void setId24Map(HashMap<String, String> idmap) {
		id24Map = idmap;
	}

	protected HashMap<String, String[]> getOutageMap() {
		return outageMap;
	}

	protected void setOutageMap(HashMap<String, String[]> omap) {
		outageMap = omap;
	}

	private void setEnergyEndDate(String startDate) {
		energyEndDate = startDate;
	}

	protected String getEnergyEndDate() {
		return energyEndDate;
	}

	private void setDailyEndDate(String dailyEnd) {
		dailyEndDate = dailyEnd;
	}

	protected String getDailyEndDate() {
		return dailyEndDate;
	}

	protected void setBbElevMap(HashMap<String, String> bbMap) {
		bbElevMap = bbMap;
	}

	protected HashMap<String, String> getBbElevMap() {
		return bbElevMap;
	}

	protected HashMap<String, String> getRescheduleBBElevMap() {
		return rescheduleBBElevMap;
	}

	protected void setRescheduleBBElevMap(HashMap<String, String> data) {
		rescheduleBBElevMap = data;
	}

	protected void setTotCalEnrgyMap(HashMap<String, String> totCalEnrgy) {
		totCalEnrgyMap = totCalEnrgy;
	}

	protected HashMap<String, String> getTotCalEnrgyMap() {
		return totCalEnrgyMap;
	}

	protected HashMap<String, String[]> getCoefficientMap() {
		return coefficientMap;
	}

	protected void setCoefficientMap(HashMap<String, String[]> cmap) {
		coefficientMap = cmap;
	}

	protected void setSchFileEndDate(String schFileEnd) {
		schFileEndDate = schFileEnd;
	}

	protected String getSchFileEndDate() {
		return schFileEndDate;
	}

	protected void setSchFileStartDate(String fsd) {
		schFileStartDate = fsd;
	}

	protected String getSchFileStartDate() {
		return schFileStartDate;
	}

	protected HashMap<String, String> getTotReschedCalEnrgyMap() {
		return totReschedCalEnrgyMap;
	}

	protected void setTotReschedCalEnrgyMap(HashMap<String, String> totCalEnrgy) {
		totReschedCalEnrgyMap = totCalEnrgy;
	}

	protected Calendar getRescheduleDataEndDate() {
		return rescheduleDataEndDate;
	}

	protected void setRescheduleDataEndDate(Calendar rescheduleDataendDate) {
		rescheduleDataEndDate = rescheduleDataendDate;
	}
}
