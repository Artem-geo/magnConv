package magnConv;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;

public class GEM extends Magnetometer {
	
	private String dateString = "";
	private String patternGEM = "MM.dd.yy HHmmss.SSS";
	
	ArrayList<Double> field; // array with measured field values
	ArrayList<Date> date; // array with date-time
	ArrayList<Double> lat, lon, heightMSL; // survey point latitude, longitude and height about middle sea level
	ArrayList<Integer> pr, pk; // number of survey line and point
	
	
	public GEM(String tz) throws Exception {
		super(tz);
		this.field = new ArrayList<Double>();
		this.date = new ArrayList<Date>();
		this.lat = new ArrayList<Double>();
		this.lon = new ArrayList<Double>();
		this.heightMSL = new ArrayList<Double>(); 
		this.pr = new ArrayList<Integer>();
		this.pk = new ArrayList<Integer>();
	}
	
	
	// get date-time and field values from data
	public void getDataFromFile(String path) {
		String[] line;
		try {
			FileReader fr = new FileReader(path);
			Scanner sc = new Scanner(fr);
			while(sc.hasNextLine()) {
				
				line = sc.nextLine().split(" ");
				
				// 
				if(line[0].equals(String.format("%s", "/ID"))) {
					var li = line.length-1;
					dateString = String.format("%s.%s.%s", defineMonth(line[li-1]), line[li-2], line[li]); // extract date of survey from line
				}

				if(line[0].startsWith("/") | line[0].startsWith("l") | line.length<=2) {
					continue;
				} else {
					this.field.add(Double.parseDouble(line[7]));
					this.date.add(parseStringForDateObject(dateString + " " + line[13] + "00", patternGEM, super.tz));
					this.lat.add(Double.parseDouble(line[1]));
					this.lon.add(Double.parseDouble(line[3]));
					this.heightMSL.add(Double.parseDouble(line[5]));
					this.pr.add(Integer.parseInt(line[14].substring(0,5)));
					this.pk.add(Integer.parseInt(line[15]));
				}
			}
			fr.close();
			sc.close();	
		} catch (IOException e) {
			System.out.println("Can't find such file or incorrect file");
		}	
	}
	
	
	// write data to file
	public void writeDataToFile(String path, String resultsType, String tz) {
		switch (resultsType) {
		case "-v": // write converted data to the end of .bas file
			writeToVarFile(this.field, this.date, path, tz);
			break;
		case "-f": // write converted data to new file
			try {
				FileWriter fw = new FileWriter(path);
				fw.write("GPST DATE_UTC TIME_UTC LAT LON HEIGHT PR PK FIELD\n");
				
				for(int i=0; i<field.size(); i++) {
					fw.write(String.format(Locale.ENGLISH, "%.3f ", convertToGPSTime(date.get(i))));
					fw.write(returnFormatTime(this.date.get(i), super.pattern, tz) + " ");
					fw.write(String.format(Locale.ENGLISH, "%.7f %.7f %.1f ", this.lat.get(i), this.lon.get(i), this.heightMSL.get(i)));
					fw.write(String.format(Locale.ENGLISH, "%d %d ", this.pr.get(i), this.pk.get(i)));
					fw.write(this.field.get(i) + "\n");
				}
				fw.close();
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
			break;
		default:
			break;
		}
	}
	
	
	// replace greek month number by two-digits arabic numbers
	private String defineMonth(String monthGreek) {
		switch(monthGreek) {
		case "I":
			return "01";
		case "II":
			return "02";
		case "III":
			return "03";
		case "IV":
			return "04";
		case "V":
			return "05";
		case "VI":
			return "06";
		case "VII":
			return "07";
		case "VIII":
			return "08";
		case "IX":
			return "09";
		case "X":
			return "10";
		case "XI":
			return "11";
		case "XII":
			return "12";
		default:
			return "00";
		}
	}
}
