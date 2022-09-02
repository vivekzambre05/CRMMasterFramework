package com.crm.listeners;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.Markup;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.crm.base.SetUp;
import com.crm.commonUtilities.CommonMethods;
import com.crm.commonUtilities.ExcelOperation;
import com.crm.commonUtilities.ExtentReporterNG;
import com.crm.commonUtilities.ScreenShot;
import com.crm.commonUtilities.EmailReporting;



public class TestListeners extends SetUp implements ITestListener, ISuiteListener
{
	public static ExtentTest test;
	public static ExtentReports extent=ExtentReporterNG.getReportObject();
	public static ThreadLocal<ExtentTest> extentTest =new ThreadLocal<ExtentTest>();
	
	public static List<ITestNGMethod> passedtests = new ArrayList<ITestNGMethod>();
	public static List<ITestNGMethod> failedtests = new ArrayList<ITestNGMethod>();
	public static List<ITestNGMethod> skippedtests = new ArrayList<ITestNGMethod>();
	public static LocalDateTime startTime;
	public static LocalDateTime endTime;
    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");  
    
	public void onTestStart(ITestResult result) 
	{
		String methodName = result.getMethod().getMethodName();
		//test = extent.createTest(result.getTestClass().getName() + "  @TestCase : " + result.getMethod().getMethodName());
		test = extent.createTest(result.getTestClass().getName() );

		extentTest.set(test);

		log.info("Test Case_" + methodName+ "_Successfully Started");
	}

	public void onTestSuccess(ITestResult result) {
		
    	String methodName = result.getMethod().getMethodName();
		String logText = "<b>" + "Test Case:- " + methodName+ " PASSED" + "</b>";
		Markup m = MarkupHelper.createLabel(logText, ExtentColor.GREEN);
		extentTest.get().pass(m);
        passedtests.add(result.getMethod());


		log.info("Test Case_" + methodName + "_Successfully Passed");
		try {
			String sheetName = CommonMethods.readPropertyFile("SheetName");
			int rowNum = CommonMethods.getTestScenarioRowNum(methodName);
			ExcelOperation.setCellData(sheetName, "Status", rowNum, "Pass");
		} catch (Exception e) {}
	}

	public static void extentInfo(String message,String name) throws Exception
	{
		Markup m = MarkupHelper.createLabel(message +" "+name, ExtentColor.BLUE);
    	extentTest.get().log(Status.INFO, m);
    	//ScreenShot.takeSnapShot(name, "Pass");
    	//extentTest.get().log(Status.INFO, message,MediaEntityBuilder.createScreenCaptureFromPath(ScreenShot.ScreenShotName).build() );
    	
	}
	
	public static void extentError(String message,String name)
	{
		Markup m = MarkupHelper.createLabel(message +" "+name, ExtentColor.RED);
    	extentTest.get().log(Status.FAIL, m);
	}
	
	
	public void onTestFailure(ITestResult result)
	{
		
		String methodName = result.getMethod().getMethodName();
		log.error(methodName+ " Get Failed due to " + "\n" + result.getThrowable().getMessage());

		String excepionMessage = Arrays.toString(result.getThrowable().getStackTrace());
		// String excepionMessage= result.getThrowable().getMessage();
		extentTest.get()
				.fail("<details>" + "<summary>" + "<b>" + "<font color=" + "red>" + "Exception Occured:Click to see"
						+ "</font>" + "</b >" + "</summary>" + excepionMessage.replaceAll(",", "<br>") + "</details>"
						+ " \n");
        failedtests.add(result.getMethod());

		try {
			ScreenShot.takeSnapShot(methodName, "Fail");
			extentTest.get().fail("<b>" + "<font color=" + "red>" + "Screenshot of failure" + "</font>" + "</b>",
					MediaEntityBuilder.createScreenCaptureFromPath(ScreenShot.ScreenShotName).build());
		} catch (Exception e) 
		{
			System.out.println("Exception occured while adding SS to extent report :"+e.getMessage());
		}
		
		String failureLogg = "TEST CASE FAILED";
		Markup m = MarkupHelper.createLabel(failureLogg, ExtentColor.RED);
		extentTest.get().log(Status.FAIL, m);
		try {
			String sheetName = CommonMethods.readPropertyFile("SheetName");
			int rowNum = CommonMethods.getTestScenarioRowNum(methodName);
			ExcelOperation.setCellData(sheetName, "Status", rowNum, "Fail");
		} catch (Exception e) {}

	}

	public void onTestSkipped(ITestResult result) {
		
		//extentTest.get().log(Status.SKIP,result.getMethod().getMethodName()+" Skipped");
		String methodName = result.getMethod().getMethodName();
		String logText = "<b>" + "Test Case:- " + methodName + " Skipped" + "</b>";
		Markup m = MarkupHelper.createLabel(logText, ExtentColor.YELLOW);
		extentTest.get().skip(m);
        skippedtests.add(result.getMethod());

		log.info("Test Case_" + methodName + "_get Skipped as its Runmode is 'NO' ");

	}


	public void onFinish(ITestContext context) {
		if (extent != null) {

			extent.flush();
		}
	}
	
	public void onStart(ISuite arg0) {
		startTime =  LocalDateTime.now();

	}
	
	public void onFinish(ISuite arg0) {
		endTime =  LocalDateTime.now();
		try {
		      EmailReporting.sendReportViaEmail(passedtests.size(), failedtests.size(), skippedtests.size(), startTime, endTime);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

	

