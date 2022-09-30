package com.crm.commonUtilities;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.ViewName;
import com.crm.base.SetUp;

public class ExtentReporterNG extends SetUp
{
	public static File flOutput;
	static ExtentReports extent ;
	static Logger log = LoggerFactory.getLogger(ExtentReporterNG.class);
	static String folderDate = new SimpleDateFormat("dd-MM-yyyy_HH").format(new Date());
	public static String currentDir = System.getProperty("user.dir")+"\\Results";
	public static String outPutFolder = currentDir +"\\Output_"+folderDate;
	public static String reportPath = outPutFolder+"\\TestReport_"+folderDate+".html";
	public static Properties config = SetUp.loadConfig();

	public static ExtentReports getReportObject()
	{
		//String reportPath = System.getProperty("user.dir")+"\\Reports\\KMB_LeadCreationReport_"+folderDate;
		extent = new ExtentReports();
		System.out.println("*********");
		System.out.println(outPutFolder);
		System.out.println("*********");
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
				   ViewName.CATEGORY,
				   ViewName.AUTHOR, 
				   ViewName.DEVICE, 
				   ViewName.EXCEPTION, 
				   ViewName.LOG
				})
			  .apply();
		//ExtentSparkReporter reporter =new ExtentSparkReporter(reportPath).filter().statusFilter().as(new Status [] {Status.FAIL,Status.PASS}).apply().viewConfigurer().viewOrder().as(new ViewName [] {ViewName.DASHBOARD,ViewName.TEST}).apply();
		
		 reporter.config().setTimelineEnabled(false);
		 reporter.config().setCss(".sysenv-container{right:50%} .category-container{left:50%}");
		 reporter.config().setJs("document.querySelector('.category-container .card .card-header p').innerHTML='Cases/Scenarios <br> Note: Skipped Tests - Either not selected during run / error during run';");
		 
		try {
			reporter.loadXMLConfig(new File(System.getProperty("user.dir") + config.getProperty("ExtentConfigXml")));
		} catch (IOException e) {
			log.error("Unable to load config.xml file due to "+e.getMessage());
		}


		//reporter.config().setReportName("Web Automation Result");
		//reporter.config().setDocumentTitle("Test Results");

		extent.attachReporter(reporter);
		extent.setSystemInfo("Project Name","Kotak Mahindra Bank");
		extent.setSystemInfo("Test Coverage", CommonMethods.getTestTypes());
		extent.setSystemInfo("OS", System.getProperty("os.name"));

		return extent;
	}
	
}
