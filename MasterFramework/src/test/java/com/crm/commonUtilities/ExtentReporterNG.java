package com.crm.commonUtilities;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.ViewName;
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
		extent = new ExtentReports();

		flOutput = new File(outPutFolder);
		if(!flOutput.exists()) {
			if(flOutput.mkdir()) {
				log.info("Extent report Directory is created!");
			}
			else {
				log.error("Failed to create extent report directory!");
			}
		}

		ExtentSparkReporter reporter =new ExtentSparkReporter(reportPath).viewConfigurer()
			    .viewOrder()
			    .as(new ViewName[] { 
				   ViewName.DASHBOARD, 
				   ViewName.TEST, 
				   ViewName.AUTHOR, 
				   ViewName.DEVICE, 
				   ViewName.EXCEPTION, 
				   ViewName.LOG 
				})
			  .apply();
		//ExtentSparkReporter reporter =new ExtentSparkReporter(reportPath).filter().statusFilter().as(new Status [] {Status.FAIL,Status.PASS}).apply().viewConfigurer().viewOrder().as(new ViewName [] {ViewName.DASHBOARD,ViewName.TEST}).apply();
		try {
			reporter.loadXMLConfig(new File(".\\src\\test\\resources\\Extent-Config\\ReportsConfig.xml"));
		} catch (IOException e) {
			log.error("Unable to load config.xml file due to "+e.getMessage());
		}


		//reporter.config().setReportName("Web Automation Result");
		//reporter.config().setDocumentTitle("Test Results");

		
		extent.attachReporter(reporter);
		extent.setSystemInfo("Project Name","Kotak Mahindra Bank");
		extent.setSystemInfo("Modules Consist","CRM and DAP Journeys");
		extent.setSystemInfo("Tester Name","Vrunda Vibhute");

		return extent;
	}
}
