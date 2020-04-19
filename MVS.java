package magnConv;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class MVS extends Magnetometer {
	
	private String dateString = "";
	private String patternMVS = "dd.MM.yy HH:mm:ss.SSS";
	
	ArrayList<Double> field; // array with measured field values
	ArrayList<Date> date; // array with date-time
	
	
	public MVS(String tz) throws Exception {
		super(tz);
		this.field = new ArrayList<Double>();
		this.date = new ArrayList<Date>();
	}
	
	
	// get date-time and field values from data
	public void getDataFromFile(String path) {
		String[] line;
		int l = 0;
		try {
			FileReader fr = new FileReader(path);
			Scanner sc = new Scanner(fr);
			while(sc.hasNextLine()) {
				
				line = sc.nextLine().split(" ");

				if(l<8) { // skip 7 lines 
					if(l==3) {
						dateString = line[1]; // extract date of measuring from line array
					}
					l++;
					continue;
				} else {
					this.field.add(Double.parseDouble(line[0]));
					this.date.add(parseStringForDateObject(dateString + " " + line[2] + "00", patternMVS, super.tz));
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
				fw.write("GPST DATE_UTC TIME_UTC FIELD\n"); // headers of data columns
				
				for(int i=0; i<field.size(); i++) {
					fw.write(String.format("%.3f ", convertToGPSTime(date.get(i))));
					fw.write(returnFormatTime(this.date.get(i), super.pattern, tz) + " ");
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
}
