package magnConv;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;

public class MMPOS extends Magnetometer {
	
	private String patternPosGround = "MM-dd-yy HH:mm:ss,SSS";
	
	ArrayList<Double> field; // array with measured field values
	ArrayList<Date> date; // array with date-time
	ArrayList<Integer> pr; // number of survey line
	ArrayList<Integer> pk; // number of survey point
	
	
	public MMPOS(String tz) throws Exception {
		super(tz);
		this.field = new ArrayList<Double>();
		this.date = new ArrayList<Date>();
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
				
				if(line[0].equals(String.format("%s", ";")) | line.length<=1) {
					continue;
				} else {
					this.field.add(Double.parseDouble(line[0])/1000);
					this.date.add(parseStringForDateObject(line[3] + " " + line[4] + "0", patternPosGround, super.tz));
					
					if (line.length==8) { // extract line and survey point number
						this.pr.add(Integer.parseInt(line[5]));
						this.pk.add(Integer.parseInt(line[6]));
					}
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
				var isPrPk = (this.pr.size()!=0 && this.pk.size()!=0);
				if (isPrPk) {
					fw.write("GPST DATE_UTC TIME_UTC PR PK FIELD\n"); // headers of data columns
				} else {
					fw.write("GPST DATE_UTC TIME_UTC FIELD\n"); // headers of data columns
				}
				
				for(int i=0; i<field.size(); i++) {
					fw.write(String.format(Locale.ENGLISH, "%.3f ", convertToGPSTime(date.get(i))));
					fw.write(returnFormatTime(this.date.get(i), super.pattern.replace(",", "."), tz) + " ");
					if(isPrPk) {
						fw.write(String.format(Locale.ENGLISH, "%d %d ", this.pr.get(i), this.pk.get(i)));
					}
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
