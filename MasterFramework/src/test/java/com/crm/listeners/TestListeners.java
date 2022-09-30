package com.crm.listeners;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

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
    
    static File statusFile = new File("status.txt");
    static FileWriter myWriter;
    static int testCount = 0;

    
	public void onTestStart(ITestResult result) 
	{
		String methodName = result.getMethod().getMethodName();
		//test = extent.createTest(result.getTestClass().getName() + "  @TestCase : " + result.getMethod().getMethodName());

		test = extent.createTest(result.getTestClass().getName() );
		
		extentTest.set(test);

		log.info("Test Case_" + methodName+ "_Successfully Started");
	}

	public void onTestSuccess(ITestResult result) {
		
		testCount++;
		modifyExecutionStatus();
		String methodName = result.getMethod().getMethodName();
		String logText = "<b>" + "Test Case:- " + methodName+ " PASSED" + "</b>";
		Markup m = MarkupHelper.createLabel(logText, ExtentColor.GREEN);
		extentTest.get().pass(m);
        passedtests.add(result.getMethod());
        
        assignCategory(methodName);



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
		testCount++;
		modifyExecutionStatus();
		String methodName = result.getMethod().getMethodName();
		log.error(methodName+ " Get Failed due to " + "\n" + result.getThrowable().getMessage());

		String excepionMessage = Arrays.toString(result.getThrowable().getStackTrace());
		// String excepionMessage= result.getThrowable().getMessage();
		extentTest.get()
				.fail("<details>" + "<summary>" + "<b>" + "<font color=" + "red>" + "Exception Occured:Click to see"
						+ "</font>" + "</b >" + "</summary>" + excepionMessage.replaceAll(",", "<br>") + "</details>"
						+ " \n");
        failedtests.add(result.getMethod());
        
        assignCategory(methodName);

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
		extent.removeTest(test);
		
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
		try {
		      if(statusFile.exists()) {
			        System.out.println("Status file already exists. Deleting it..");
			        statusFile.delete();
		      }
			        if (statusFile.createNewFile()) {
				        System.out.println("File created: " + statusFile.getName());
				      }
			      
		      
		    } catch (IOException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
		    }
		try {
				myWriter = new FileWriter(statusFile.getName());
				myWriter.write("Total number of tests selected with runnable mode as YES: " + ExcelOperation.getRunnableCount());
				myWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void onFinish(ISuite arg0) {
		endTime =  LocalDateTime.now();
		try {
			extent.setSystemInfo("Browser Name" , CommonMethods.getSystemInfo()[0].toUpperCase());
			extent.setSystemInfo("Browser Version" , CommonMethods.getSystemInfo()[1]);
			extent.flush();
		    EmailReporting.sendReportViaEmail(passedtests.size(), failedtests.size(), skippedtests.size(), startTime, endTime);  
			writeStatus("Execution status mail sent.");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void assignCategory(String methodName) {
		try {
			String module = CommonMethods.getModule(methodName);

			test.assignCategory(module);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//To write data into status file
	public static void writeStatus(String status) {

		try {			
			 BufferedWriter out = new BufferedWriter(
		                new FileWriter(statusFile.getName(), true));
		 
		            // Writing on output stream
		            out.write("\n"+ status);
		            // Closing the connection
		            out.close();
		     
		      System.out.println("Successfully wrote to the file.");
		    } catch (IOException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
		    }
	}
	
	//To overwrite the test execution Status in the file
	public static void modifyExecutionStatus() {
		 try {
			 	String filePath = statusFile.getAbsolutePath();
			 	String result = fileToString(filePath);
			 	String oldStatus = "Tests Completed - "+(testCount - 1);
			 	String newStatus = "\nTests Completed - "+ testCount;
			 	if(result.contains(oldStatus))
			 		result = result.replaceAll(oldStatus,newStatus);
			 	else
			 		result = result.concat(newStatus);
			     //Rewriting the contents of the file
			     PrintWriter writer = new PrintWriter(new File(filePath));
			     writer.append(result);
			     writer.flush();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//To return the file contents as a string
	public static String fileToString(String filePath) throws Exception{
	      String input = null;
	      Scanner sc = new Scanner(new File(filePath));
	      StringBuffer sb = new StringBuffer();
	      while (sc.hasNextLine()) {
	         input = sc.nextLine();
	         sb.append(input);
	      }
	      return sb.toString();
	   }
}

	

