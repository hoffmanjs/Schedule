package usace.wm.schedule;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.JOptionPane;

public class GetDlyDataFile
{
  HashMap<String, DlyData> dataMap = null;
  Vector<String> siteNames = null;
  HashMap<String, Vector<DlyData>> dailySortedValuesMap = null;
  protected String dlystatStartDay = null;
  protected Calendar dlystatEndDay = null;
  
  public GetDlyDataFile()
  {
    getDlyStationData();
  }
  
  public HashMap<String, DlyData> getDlyStationData()
  {
	  Util.checkDriveMaping();
    try
    {
      File dlystatFile = new File(Util.getDefaultProgramLocation() + File.separator + Util.getDefaultDlyStationDataFileName());
      if ((dlystatFile.exists()) && (dlystatFile.length() > 0))
      {
        FileInputStream fstream = new FileInputStream(dlystatFile);
        
        DataInputStream in = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        dataMap = new HashMap();
        String strLine;
        String location = "unknown";
        int locationCount = 0;
        int lineCount = 0;
        
        if (fstream.toString().length() > 0)
        {
          dailySortedValuesMap = new HashMap();
          siteNames = new Vector();
          while ((strLine = br.readLine()) != null)
          {
            StringTokenizer token = new StringTokenizer(strLine);
            
            String[] dat = new String[7];
            int reccount = 0;
            while (token.hasMoreTokens())
            {
              String linedata = token.nextToken();
              dat[reccount] = linedata;
              if ((lineCount == 0) && (reccount == 1)) {
                setDlystatStartDay(linedata);
              }
              if ((lineCount != 0) && (reccount == 1)) {
                setDlystatEndDay(linedata);
              }
              reccount++;
            }
            if (!location.equalsIgnoreCase(dat[0]))
            {
              locationCount = 0;
              location = dat[0];
            }
            DlyData dlydata = new DlyData();
            dlydata.setDamName(dat[0]);
            dlydata.setDateMMDDYY(dat[1]);
            
            dlydata.setDlyGenPower(dat[2]);
            dlydata.setFlowTotal(dat[3]);
            dlydata.setFlowPower(dat[4]);
            dlydata.setPoolElev(dat[5]);
            dlydata.setDamNumber(locationCount);
            if (!getSiteNames().contains(dlydata.getDamName())) {
              siteNames.add(dlydata.getDamName());
            }
            dataMap.put(dat[0] + locationCount, dlydata);
            
            String startKey = Util.getDataKey(Util.getCalendarDateMMDDYY(dlydata.getDate()));
            if (!dailySortedValuesMap.containsKey(startKey + "_" + String.valueOf(locationCount)))
            {
              Vector<DlyData> data = new Vector();
              data.add(dlydata);
              dailySortedValuesMap.put(startKey + "_" + String.valueOf(locationCount), data);
            }
            else
            {
              Vector<DlyData> vec = (Vector)dailySortedValuesMap.get(startKey + "_" + 
                String.valueOf(locationCount));
              vec.add(dlydata);
              dailySortedValuesMap.put(startKey + "_" + String.valueOf(locationCount), vec);
            }
            locationCount++;
            lineCount++;
          }
        }
        else
        {
          System.err.println("dlystat contains no data");
        }
        in.close();
      }
      else
      {
        JOptionPane.showMessageDialog(null, 
          "Please select a valid dlystat file location or empty dlystat file.", 
          "Invalid dlystat file", 0);
        if (Util.getFileChooser(null)) {
          getDlyStationData();
        } else {
          System.exit(0);
        }
      }
    }
    catch (Exception e)
    {
      System.err.println(" GetDlyDataFile Error: " + e.getMessage());
      JOptionPane.showMessageDialog(null, 
        "Please select a valid dlystat file location or empty dlystat file.", 
        "Invalid dlystat file", 0);
      e.printStackTrace();
    }
    return dataMap;
  }
  
  public void setSiteNames(Vector<String> siteName)
  {
    siteNames = siteName;
  }
  
  public Vector<String> getSiteNames()
  {
    return siteNames;
  }
  
  public void setDailySortedValuesMap(HashMap<String, Vector<DlyData>> dValuesMap)
  {
    dailySortedValuesMap = dValuesMap;
  }
  
  public HashMap<String, Vector<DlyData>> getDailySortedValuesMap()
  {
    return dailySortedValuesMap;
  }
  
  protected String getDlystatStartDay()
  {
    return dlystatStartDay;
  }
  
  protected void setDlystatStartDay(String dlyStartDay)
  {
    dlystatStartDay = dlyStartDay;
  }
  
  protected Calendar getDlystatEndDay()
  {
    return dlystatEndDay;
  }
  
  protected void setDlystatEndDay(String dlyEndDay)
  {
    dlystatEndDay = Util.getCalendarDate(dlyEndDay);
  }
}
