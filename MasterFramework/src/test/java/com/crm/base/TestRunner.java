package com.crm.base;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import org.testng.TestNG;
import org.testng.collections.Lists;

public class TestRunner {
	public static Properties config = new Properties();
	public static String runID = null;


	static TestNG testng;
	public static void main(String[] args) throws ClassNotFoundException {
		// TODO Auto-generated method stub
		config = SetUp.loadConfig();
		if (args.length != 0) {
			runID = args[0];
			System.out.println("Run ID from command line arguments: "+ runID);
			//To set the path of the emailConfig, extent report name and TestData Excel
			setConfig();
		}

		TestNG testng = new TestNG();

		List<String> suites = Lists.newArrayList();

		System.out.println(System.getProperty("user.dir") + config.getProperty("TestNgPath"));

		try {

			suites.add( System.getProperty("user.dir") + config.getProperty("TestNgPath"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		testng.setTestSuites(suites);
		testng.run();
	}
	
	public static void setConfig() {
		FileOutputStream out = null;
		
		try {
			out = new FileOutputStream(
					 System.getProperty("user.dir") + "\\resources\\PropertyFiles\\Config.properties");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		config.setProperty("ExcelData", "\\AutomationsFiles\\"+runID+".xlsm");
		config.setProperty("ToEmailConfig", "\\AutomationsFiles\\email_"+runID+".txt");
		config.setProperty("Report", runID);
		config.setProperty("RunID", runID);
		config.setProperty("RunExecutedFromJar", "YES");
		config.setProperty("StatusFile", "\\AutomationsFiles\\log_"+runID+".txt");

		try {
			config.store(out, "Modified properties according to runID");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
