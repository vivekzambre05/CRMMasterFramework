package com.crm.commonUtilities;

import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.UnhandledException;
import org.apache.poi.EncryptedDocumentException;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.crm.base.SetUp;
import com.crm.listeners.TestListeners;


public class CommonMethods extends SetUp 
{
	public static JavascriptExecutor js ;
	public static WebDriverWait wait ;
	public static Actions a ;
	public static Logger log = LoggerFactory.getLogger(CommonMethods.class);
	public static Properties prop=new Properties();

	
	public static void ExWait(String locator) throws Exception
	{
		wait= new WebDriverWait(driver,Duration.ofSeconds(60));
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(CommonMethods.readPropertyFile(locator))));
	}

	public static void waitForURL(String urlContains)
	{
		wait= new WebDriverWait(driver,Duration.ofSeconds(60));
		wait.until(ExpectedConditions.urlContains(urlContains)	);
	}
	
	public static void Click(String locator)
	{
		try {
			 ExWait(locator); 
			if (locator.endsWith("_XPATH")) {
				driver.findElement(By.xpath(CommonMethods.readPropertyFile(locator))).click();
			} else if (locator.endsWith("_ID")) {
				driver.findElement(By.id(CommonMethods.readPropertyFile(locator))).click();
			}
			log.info("Sucessfully clicked on "+locator);
			TestListeners.extentInfo("Sucessfully clicked on ",locator);

		} catch (Exception e) {
			log.error("Not Sucessfully clicked on "+locator+" due to :"+e.getMessage());
		}
	}
	public static void input(String locator, String SheetName, String ColName, int rowNum) 
	{
		try 
		{
			 Click(locator); 
			if (locator.endsWith("_XPATH"))
			 {
				 driver.findElement(By.xpath(CommonMethods.readPropertyFile(locator))).clear();
				 driver.findElement(By.xpath(CommonMethods.readPropertyFile(locator))).sendKeys(ExcelOperation.getCellData(SheetName,ColName,rowNum));
			 } 
			else if (locator.endsWith("_ID")) 
			{
				driver.findElement(By.id(CommonMethods.readPropertyFile(locator))).clear();	
				driver.findElement(By.id(CommonMethods.readPropertyFile(locator))).sendKeys(ExcelOperation.getCellData(SheetName,ColName,rowNum));
			}
			log.info("Data sucessfully entered on "+locator+" = "+ExcelOperation.getCellData(SheetName,ColName,rowNum));
			 TestListeners.extentInfo("Data sucessfully entered on "+locator," = "+ExcelOperation.getCellData(SheetName,ColName,rowNum));

		} catch (Exception e) {
			log.error("Data Not Sucessfully entered on "+locator+" due to :"+e.getMessage());
		}
	}
	
	public static String getElementText(String locator) throws Exception 
	{
		 ExWait(locator);
		 String txtMsg = driver.findElement(By.xpath(CommonMethods.readPropertyFile(locator))).getText();
		 return txtMsg;
	}

	public static String getElementValue(String locator) throws Exception
	{
		ExWait(locator);
		String elementValue = driver.findElement(By.xpath(CommonMethods.readPropertyFile(locator))).getAttribute("value");
		//log.info("Value of WebElement :" +elementValue);
		return elementValue;

	}
	public static void ExWaitsForWebelements(List<WebElement> ele )
	{
		wait= new WebDriverWait(driver,Duration.ofSeconds(60));
		wait.until(ExpectedConditions.visibilityOfAllElements(ele));
	}
	
	//To highlight selected webelement
	public static void highLight(String locator) throws Exception
	{
		ExWait(locator);
		js = (JavascriptExecutor) driver;
		WebElement element = driver.findElement(By.xpath(CommonMethods.readPropertyFile(locator)));
		js.executeScript("arguments[0].style.border='4px solid yellow'", element);
	}

	//to scroll down the page by pixel values as Y co-ordiante
	public static void scrollDown(int y) 
	{
		js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollBy(0,"+y+")");
	}

	//To scroll down the page by visibility of the element
	public static void scrollByVisibilityofElement(String locator) throws Exception
	{
		js = (JavascriptExecutor) driver;
		WebElement element = driver.findElement(By.xpath(CommonMethods.readPropertyFile(locator)));
		js.executeScript("arguments[0].scrollIntoView()",element);
	}

	//To scroll down the page at the bottom of page.
	public static void scrollAtBottom()
	{
		js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
		//Return the complete height of body (page)
	}

	//To select values from dropdown by visible text
	public static void selectByText(String locator, String sheetName, String colName, int rowNum) throws Exception 
	{
		WebElement element = driver.findElement(By.xpath(CommonMethods.readPropertyFile(locator)));
		Select sel = new Select(element);
		try {
			ExWait(locator);
			String text = ExcelOperation.getCellData(sheetName,colName, rowNum);
			sel.selectByVisibleText(text);
			log.info("Data = "+text+" Sucessfully Selected from dropdown "+locator);
		} catch (Exception e) {
			log.error("Not able to select from dropdown "+locator+ "due to " +e.getMessage());
		} 
	}

	//To select values from dropdown by its value
	public static void selectByValue(String locator, String sheetName, String colName, int rowNum) throws InterruptedException, EncryptedDocumentException, IOException 
	{
		try {
			WebElement element = driver.findElement(By.xpath(CommonMethods.readPropertyFile(locator)));
			String value = ExcelOperation.getCellData(sheetName,colName, rowNum);

			Select sel=  new Select(element);
			ExWait(locator);
			sel.selectByValue(value);
			log.info("Data = "+value+" Sucessfully Selected from dropdown "+locator);
		} catch (Exception e) {
			log.error("Not able to select from dropdown "+locator+ "due to " +e.getMessage());
		}
	}
	//To select values from dropdown by its index value
	public static void selectByIndex(String locator, int index) throws Exception 
	{
		try {
			WebElement element = driver.findElement(By.xpath(CommonMethods.readPropertyFile(locator)));
			Select sel=  new Select(element);
			ExWait(locator);
			sel.selectByIndex(index);
			log.info("Data = "+index+" Sucessfully Selected from dropdown "+locator);
		} catch (Exception e) {
			log.error("Not able to select from dropdown "+locator+ "due to " +e.getMessage());
		}
	}

	//To handle mouse hover actions
	public static void mouseHover(String locator)throws Exception 
	{
		try {
			a = new Actions(driver);
			ExWait(locator);
			highLight(locator);
			WebElement element = driver.findElement(By.xpath(CommonMethods.readPropertyFile(locator)));

			a.moveToElement(element).perform();	
			log.debug("Mouse hover on "+locator);
		} catch (Exception e) {
			log.error("Unable to mouse hover due to "+e.getMessage());
		}
		
	}
	
	//To handle mouse hover actions
		public static void mouseClick(String locator) throws Exception 
		{
			try {
				a = new Actions(driver);
				ExWait(locator);
				highLight(locator);
				WebElement element = driver.findElement(By.xpath(CommonMethods.readPropertyFile(locator)));

				a.moveToElement(element).click().perform();	
				log.debug("Mouse Click on "+locator);
			} catch (Exception e) {
				log.error("Not able to Mouseclick due to "+e.getMessage());
			}
			
		}

	public static void AlertHandle(String action) 
	{
		try {
			if(action.equalsIgnoreCase("accept")) {
				driver.switchTo().alert().accept();
				log.info("Alert accepted succesfully");
			}else if(action.equalsIgnoreCase("dismiss")) {
				driver.switchTo().alert().dismiss();
				log.error("Alert dismissed succesfully");
			}
		}catch(Exception e){
			log.info("Not able to clicked on alert due to "+e.getMessage());
			}
	}
	
	public static void switchToWindow() 
	{
		try {
			//for(String winhandle:driver.getWindowHandles()) {
				//driver.switchTo().window(winhandle);
				//log.info("Swiched to window name"+driver.getTitle()+" with id "+winhandle );
			
			String parentWin = driver.getWindowHandle(); 
	        Set<String> windows = driver.getWindowHandles();	        
	        Iterator<String> it = windows.iterator();	        
	        while(it.hasNext()) 
	        {	            
	            String childWin = it.next();	            
	            if(!parentWin.equals(childWin)) 
	            {
	              	driver.switchTo().window(childWin);	            
	              	//Verify title of the child window
	              	//System.out.println("Window Title = "+driver.getTitle() +"and URL = "+driver.getCurrentUrl() );
					log.info("Swiched to Child window name : "+driver.getTitle()+" || URL :"+ driver.getCurrentUrl() );
	            }
	        }
		}catch(Exception e) {
			log.error("Not able switch to window "+e.getMessage());
		}
	}
	
	public static boolean switchToWindowByTitle(String title)
	{
	    String currentWindow = driver.getWindowHandle(); 
	    Set<String> availableWindows = driver.getWindowHandles(); 
	    if (!availableWindows.isEmpty()) { 
	         for (String windowId : availableWindows) {
	              String switchedWindowTitle=driver.switchTo().window(windowId).getTitle();
	              if ((switchedWindowTitle.equals(title))||(switchedWindowTitle.contains(title))){ 
	                  return true; 
	              } else { 
	                driver.switchTo().window(currentWindow); 
	              } 
	          } 
	     } 
	     return false;
	}
	public static void switchToParentWin() {
		
		try {
			String parentwindow=driver.getWindowHandle();
			driver.switchTo().window(parentwindow);
			log.info("Swiched to parent window "+driver.getTitle()+" with ID "+parentwindow);
			
		}catch(Exception e) {
			log.error("Not able to switch to parent window "+e.getMessage());
		}

	}

public static boolean isTestRunnable(String testName, String sheetName) throws Exception
{
		int rows =ExcelOperation.getRowCount("TestScenario");
		//System.out.println("No of rows : "+rows + " and test name = "+testName);
	
		for(int rNum=1; rNum<=rows; rNum++){
			
			String testCase = ExcelOperation.getCellData("TestScenario", "TC Name", rNum);
			
			if(testCase.equalsIgnoreCase(testName)){
				
				String runmode = ExcelOperation.getCellData("TestScenario", "RunMode", rNum);
				
				if(runmode.equalsIgnoreCase("Yes"))
				{
					setUpTest1(sheetName);
					return true;
				}
					else
					return false;
			}
		}
		return false;
	}

public static String getModule(String methodName) throws Exception
{
		int rows =ExcelOperation.getRowCount("TestScenario");
		String module = null;
		
		for(int rNum=1; rNum<=rows; rNum++){
			
			String testCase = ExcelOperation.getCellData("TestScenario", "TC Name", rNum);
			
			if(testCase.equalsIgnoreCase(methodName)){
				
				module = ExcelOperation.getCellData("TestScenario", "Module", rNum);
			}
		}
		return module;
}

	public static String getTestTypes()
	{
		ArrayList<String> columnData = new ArrayList<String>();
		String testTypes = null;
		try {
			columnData = ExcelOperation.getcolumnData("TestScenario", "Test Type");
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	//To remove duplicates from the array list
	List<String> newList = columnData.stream().distinct().collect(Collectors.toList());
	
	testTypes = String.join(", ", newList);
	
	System.out.println("ArrayList with duplicates removed: "
	        + newList);
	
	return testTypes;
	}

	//To get system info
	public static String[] getSystemInfo() {
		String [] sysInfo = new String [2];
		Capabilities browserCap = ((RemoteWebDriver) driver).getCapabilities();
		String browserName = browserCap.getBrowserName();
		String browserVersion = browserCap.getBrowserVersion();
		sysInfo[0] = browserName;
		sysInfo[1]= browserVersion;
		return sysInfo;
	}

	public static int getTestScenarioRowNum(String testScenario) throws Exception
	{
		String sheetName = CommonMethods.readPropertyFile("SheetName");
		int rows =ExcelOperation.getRowCount(sheetName);
		int rNum=1;
		for( ;rNum<=rows; rNum++)
		{
			String testCase = ExcelOperation.getCellData(sheetName, "TC Name", rNum);
			if(testCase.equalsIgnoreCase(testScenario))
			{	
				log.info("Row num for TestScenario = "+testScenario+ " is = "+rNum);
				return rNum;
			}
		}
		return rNum;
	}

	public static String readPropertyFile(String propertyName)throws UnhandledException, IOException
	{
		prop = SetUp.loadConfig();
		String propertyValue=prop.getProperty(propertyName);
		
		return propertyValue;
	}

	//Method to Upload File 
	public static void FileUpload(String filepath) 
	{
		try {
			Robot robo = new Robot();
			/*
			 * element=driver.findElement(By.xpath(object)); robo.setAutoDelay(3000);
			 * element.click();
			 */
			robo.setAutoDelay(2000);
			StringSelection StringSel = new StringSelection(filepath);
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(StringSel, null);

			robo.setAutoDelay(3000);

			robo.keyPress(KeyEvent.VK_CONTROL);
			Thread.sleep(1000);
			robo.keyPress(KeyEvent.VK_V);
			robo.keyRelease(KeyEvent.VK_CONTROL);
			Thread.sleep(1000);
			robo.keyRelease(KeyEvent.VK_V);
			robo.keyPress(KeyEvent.VK_ENTER);
			robo.keyRelease(KeyEvent.VK_ENTER);

			log.info("File uploaded succesfully");
			TestListeners.extentInfo("File Uploaded Successfully ","");
		} catch (Exception e) 
		{
			log.error("File Not get upload sucessfully due to" +e.getMessage());
		}
	}


}
