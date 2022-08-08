package com.crm.commonUtilities;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.crm.base.SetUp;

public class ExtentReporterNG extends SetUp 
{
	public static File flOutput;
	static ExtentReports extent ;
	static Logger log = LoggerFactory.getLogger(ExtentReporterNG.class);
	static String folderDate = new SimpleDateFormat("dd-MM-yyyy HH").format(new Date());
	public static String currentDir = System.getProperty("user.dir")+"\\src\\test\\resources\\Results";
	public static String outPutFolder = currentDir +"\\Output_"+folderDate;
	public static String reportPath = outPutFolder+"\\TestReport_"+folderDate+".html";

	public static ExtentReports getReportObject()
	{
		//String reportPath = System.getProperty("user.dir")+"\\Reports\\KMB_LeadCreationReport_"+folderDate;
		
			
		flOutput = new File(outPutFolder);
		if(!flOutput.exists()) {
			if(flOutput.mkdir()) {
				log.debug("Extent report Directory is created!");
				}
			else {
				log.debug("Failed to create extent report directory!");
				}
			}
			
		//String reportPath = System.getProperty("user.dir")+"\\TestReport"+folderDate+".html";
		
				
		ExtentSparkReporter reporter =new ExtentSparkReporter(reportPath);
		reporter.config().setReportName("Web Automation Result");
		reporter.config().setDocumentTitle("Test Results");
		
		extent = new ExtentReports();
		extent.attachReporter(reporter);
		extent.setSystemInfo("Project Name","Kotak Mahindra Bank");
		extent.setSystemInfo("Modules Consist","CRM and DAP Journeys");
		extent.setSystemInfo("Tester Name","Vrunda Vibhute");
		
		return extent;
	}
}
