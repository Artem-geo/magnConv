package magnConv;

import java.io.FileWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;
import java.util.TimeZone;

abstract class Magnetometer {
	
	String pattern = "yyyy/MM/dd HH:mm:ss,SSS"; // pattern of resultant date-time object
	Date initGPS; // initial GPS time
	String tz; // time zone of data
	
	
	public Magnetometer(String tz) throws Exception {
		initGPS = parseStringForDateObject("1980/01/06 00:00:00,000", pattern, "+00");
		this.tz = tz;
	}
	
	
	// parse string for date-time pattern and create new Date object
	public Date parseStringForDateObject(String s, String pattern, String tz) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, new Locale("en", "GB"));
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone(returnTimeZone(tz)));
		try {
			return simpleDateFormat.parse(s);	
		} catch(ParseException e) {
			System.out.println(e.getMessage());
			return null;
		}
	}
	
	
	// return formatted string with date and time 
	public String returnFormatTime(Date date, String patternOut, String tz) {
		String formattedTime = "";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(patternOut);
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone(returnTimeZone(tz)));
		formattedTime = simpleDateFormat.format(date);
		return formattedTime;
	}
	
	
	// convert Date object to GPS time
	public double convertToGPSTime(Date date) {
		return ((double)(date.getTime() - initGPS.getTime()))/1000 + 18; // при пересчёте почему-то всегда возникает ошибка в 18 секунд
	}
	
	
	// return string representation of time zone
	public String returnTimeZone(String tz) {
		switch(tz) {
		case "+00":
			return "UTC";
		case "+01":
			return "Europe/London";
		case "+02":
			return "Europe/Kaliningrad";
		case "+03":
			return "Europe/Moscow";
		case "+04":
			return "Europe/Samara"; 
		case "+05":
			return "Asia/Yekaterinburg";
		case "+06":
			return "Asia/Omsk";
		case "+07":
			return "Asia/Novosibirsk";
		case "+08":
			return "Asia/Irkutsk";
		case "+09":
			return "Asia/Yakutsk";
		case "+10":
			return "Asia/Vladivostok";
		case "+11":
			return "Asia/Magadan";
		case "+12":
			return "Asia/Kamchatka";
		default:
			return null;
		}
	}
	
	
	// write data into file with variations
	public void writeToVarFile(ArrayList<Double> field, ArrayList<Date> date, String pathToVar, String tz) {
		
		String patternOut = this.pattern.replace(" ", "\t");
		patternOut = patternOut.replace(",", ".");
		
		Scanner sc = new Scanner(System.in);
		System.out.print("Enter value of average field: ");
		double fieldAver = sc.nextDouble();
		sc.nextLine();
		sc.close();
		
		try {
			FileWriter fw = new FileWriter(pathToVar, true);
			for (int i=0; i<field.size(); i++) {
				fw.write(returnFormatTime(date.get(i), patternOut, tz));
				fw.write(String.format("\t%.3f\n", (fieldAver - field.get(i))));
			}
			fw.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
