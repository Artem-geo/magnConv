package magnConv;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;

public class Geoscan extends Magnetometer {
	
	ArrayList<Double> field; // array with measured field values
	ArrayList<Date> date; // array with date-time
	ArrayList<Double> lat, lon, heightMSL; // survey point latitude, longitude and height about middle sea level
	
	public Geoscan(String tz) throws Exception {
		super(tz);
		this.field = new ArrayList<Double>();
		this.date = new ArrayList<Date>();
		this.lat = new ArrayList<Double>();
		this.lon = new ArrayList<Double>();
		this.heightMSL = new ArrayList<Double>();
	}
	
	
	// get date-time and field values from data
	public void getDataFromFile(String path) {
		String[] line;
		try {
			FileReader fr = new FileReader(path);
			Scanner sc = new Scanner(fr);
			while(sc.hasNextLine()) {
				
				line = sc.nextLine().split(" ");
				
				this.date.add(gpsTimeToDateObj(line[0]));
				this.lat.add(Double.parseDouble(line[1]));
				this.lon.add(Double.parseDouble(line[2]));
				this.heightMSL.add(Double.parseDouble(line[3]));
				this.field.add(Double.parseDouble(line[4]));
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
					fw.write(returnFormatTime(this.date.get(i), super.pattern.replace(",", "."), tz) + " ");
					fw.write(String.format(Locale.ENGLISH, "%.7f %.7f %.1f ", this.lat.get(i), this.lon.get(i), this.heightMSL.get(i)));
					fw.write(String.format(Locale.ENGLISH, "%.3f\n", this.field.get(i)));
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
	
	
	// convert date GPS time (double value) to Date object
	private Date gpsTimeToDateObj(String gpsTimeStr) {
		var msc = (long)((Double.parseDouble(gpsTimeStr)-18) * 1000d) + super.initGPS.getTime();
		return new Date(msc);
	}
	

}
