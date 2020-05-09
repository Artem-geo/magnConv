package magnConv;

import java.io.FileReader;
import java.util.Scanner;

public class MagnConv {
	
	public static void main(String[] args) throws Exception {
		
		/*
		 *args[0] - path to text file containing input parameters
		 *
		 * par1 - magnetometer type
		 * 		-pa - POS1-aero (aero magnetometer, Quantum Magnetometry Laboratory (Russia));
		 * 		-pg - MMPOS (Overhauser magnetometer, Quantum Magnetometer Laboratory (Russia));
		 * 		-g - GEM-19 (Overhauser magnetometer, GEM Systems (Canada));
		 * 		-m - MINIMAG (Proton precession magnetometer (Russia));
		 * 		-mq - MVS (Quantum magnetometer (Russia));
		 * 		-gs - Geoscan (Quantum magnetometer, Geoscan group of companies (Russia)).
		 * 
		 * par2 - type of results format
		 * 		-v - upgrade of file with variations (.bas);
		 * 		-f - field data results.
		 * 	File with field results will consist of such columns:
		 * 		GPST - GPS time (amount of seconds from 00:00:00 06.01.1980)
		 * 		DATE_UTC - UTC date (Universal Coordinated Time) in form YYYY/MM/DD
		 * 		TIME_UTC - UTC time in format hh:mm:ss.SSS
		 * 		FIELD - field value after corrections (nT)
		 * 
		 * par3 - time zone of data in file
		 * 
		 * par4 - path to the file with data
		 * 
		 * par5 - path and name to the result's file
		 * Depends on result's type format. For -v - path to .bas file, for -f - path to resultant file.
		 *  
		 */		
		
		
		//================================================================
//		Instantiation and initialization of input parameters
		
		String magType = "";
		String resultsType = "";
		String timeZone = "";
		String pathToDataFile = "";
		String pathToResultsFile = "";
			
		String path = args[0];
		
		try {
			FileReader fr = new FileReader(path);
			Scanner sc = new Scanner(fr);
			
			magType = sc.nextLine();
			resultsType = sc.nextLine();
			timeZone = sc.nextLine();
			pathToDataFile = sc.nextLine();
			pathToResultsFile = sc.nextLine();
			
			sc.close();
			fr.close();
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
			
		// choose between different types of magnetometer
		switch(magType) { 
		case "-pa": // Pos1-aero
			POSaero pa = new POSaero(timeZone);
			pa.getDataFromFile(pathToDataFile);
			pa.writeDataToFile(pathToResultsFile, resultsType, "+00");	
			break;
		case "-pg": // MMPOS 
			MMPOS pg = new MMPOS(timeZone);
			pg.getDataFromFile(pathToDataFile);
			pg.writeDataToFile(pathToResultsFile, resultsType, "+00");
			break;
		case "-m": // Minimag
			Minimag m = new Minimag(timeZone);
			m.getDataFromFile(pathToDataFile);
			m.writeDataToFile(pathToResultsFile, resultsType, "+00");
			break;
		case "-g": // GEM
			GEM g = new GEM(timeZone);
			g.getDataFromFile(pathToDataFile);
			g.writeDataToFile(pathToResultsFile, resultsType, "+00");
			break;
		case "-mq": // quantum (MVS)
			MVS mvs = new MVS(timeZone);
			mvs.getDataFromFile(pathToDataFile);
			mvs.writeDataToFile(pathToResultsFile, resultsType, "+00");
			break;
		case "-gs": // Geoscan
			Geoscan gs = new Geoscan(timeZone);
			gs.getDataFromFile(pathToDataFile);
			gs.writeDataToFile(pathToResultsFile, resultsType, "+00");
			break;
		default:
			System.out.println("Wrong type of magnetometer");
		}
	}

}
