package magnConv;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;

public class POSaero extends Magnetometer {
	
	private String patternPos = "MM.dd.yy HH:mm:ss,SSS";
	
	ArrayList<Double> field;  // array with measured field values
	ArrayList<Date> date; // array with date-time
	
	public POSaero(String tz) throws Exception {
		super(tz);
		this.field = new ArrayList<Double>();
		this.date = new ArrayList<Date>();
	}
	
	
	// get date-time and field values from data
	public void getDataFromFile(String path) {
		String[] line;
		try {
			FileReader fr = new FileReader(path);
			Scanner sc = new Scanner(fr);
			while(sc.hasNextLine()) {
				
				line = sc.nextLine().split(" ");
				
				if(line[0].equals(String.format("%s", ";")) | line.length<=1) {
					continue;
				} else {
					this.field.add(Double.parseDouble(line[0])/1000);
					this.date.add(parseStringForDateObject(line[3] + " " + line[4] + "0", patternPos, super.tz));
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
				fw.write("GPST DATE_UTC TIME_UTC FIELD\n");
				
				for(int i=0; i<field.size(); i++) {
					fw.write(String.format(Locale.ENGLISH, "%.3f ", convertToGPSTime(date.get(i))));
					fw.write(String.format(Locale.ENGLISH, "%s ", returnFormatTime(date.get(i), super.pattern.replace(",", "."), tz)));
					fw.write(field.get(i) + "\n");
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
}
