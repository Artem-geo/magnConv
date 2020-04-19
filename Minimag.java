package magnConv;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Minimag extends Magnetometer {
	
	String surveyDate = "";
	ArrayList<Double> field;
	ArrayList<Date> date;
	ArrayList<Integer> pr;
	ArrayList<Integer> pk;
	SimpleDateFormat simpleDateFormat;
	
	String pattern = "dd.MM.yy HH:mm:ssX";
	
	public Minimag(String tz) throws Exception {
		super(tz);		
		this.field = new ArrayList<Double>();
		this.date = new ArrayList<Date>();
		this.pr = new ArrayList<Integer>();
		this.pk = new ArrayList<Integer>();
		simpleDateFormat = new SimpleDateFormat(pattern, new Locale("en", "GB"));
	}
	
	public void getDataFromFile(String path) {
		String line[];
		try {
			FileReader fr = new FileReader(path);
			Scanner sc = new Scanner(fr);
			
			int l = 0;
			
			while(sc.hasNext()) {
				line = sc.nextLine().split("[ \t]");
				
				if (l==3) surveyDate = line[1];

				if(l<8) {
					l++;
					continue;
				} else {
					field.add(Double.parseDouble(line[0]));
					date.add(simpleDateFormat.parse(surveyDate + " " + line[2] + super.tz));
					if (line.length == 5) {
						pr.add(Integer.parseInt(line[3]));
						pk.add(Integer.parseInt(line[4]));
					}
				}
			}
			sc.close();
			fr.close();
			
		} catch (IOException e) {
			System.out.println(e.getMessage());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void writeDataToFile(String pathToFile, String resultsType, String tz) {
		switch(resultsType) {
		case "-v":
			writeToVarFile(this.field, this.date, pathToFile, tz);
			break;
		case "-f":
			try {
				FileWriter fw = new FileWriter(pathToFile);
				fw.write("GPST DATE_UTC TIME_UTC FIELD\n");
				
				for(int i=0; i<field.size(); i++) {
					fw.write(String.format("%.3f ", convertToGPSTime(date.get(i))));
					fw.write(returnFormatTime(date.get(i), super.pattern, tz) + " ");
					fw.write(field.get(i) + "\n");
				}
				fw.close();
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
			break;
		}
	};

}
