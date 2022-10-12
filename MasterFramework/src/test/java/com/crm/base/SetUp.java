package com.crm.base;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterTest;

import com.crm.commonUtilities.CommonMethods;
import com.crm.commonUtilities.ExcelOperation;

import io.github.bonigarcia.wdm.WebDriverManager;



public class SetUp 
{
	public static WebDriver driver;
	public static Properties prop=new Properties();
    protected static Logger log = LoggerFactory.getLogger(SetUp.class);

	public static void setUpTest1(String sheetName)throws Exception
	{
		String browserName=CommonMethods.readPropertyFile("Browser");
		//using webdriver manager
		if(browserName.equalsIgnoreCase("Chrome"))
		{
			ChromeOptions chromeOptions = new ChromeOptions();
			WebDriverManager.chromedriver().setup();
			driver = new ChromeDriver(chromeOptions);
			log.info("Chrome Browser Launched..");
		}
		else if(browserName.equalsIgnoreCase("Firefox"))
		{
			
			FirefoxOptions firefoxOptions = new FirefoxOptions();
			WebDriverManager.firefoxdriver().setup();
			driver = new FirefoxDriver(firefoxOptions);
			log.info("Firefox Browser Launched..");
		}
		
		String URL =ExcelOperation.getCellData(sheetName, "URL", 1);
		driver.get(URL);
		log.info("URL : "+URL);
		driver.manage().window().maximize();
		
	}
	
	@AfterTest
	public void tearDownTest() 
	{
		//Close Browser		
		  driver.close();
		  driver.quit(); 
		  log.info("Browser Closed..");
	}
	
	public static Properties loadConfig() {
		  Properties config = new Properties();

		  FileInputStream fis = null;
		
		try {

			fis = new FileInputStream(
					System.getProperty("user.dir") + "\\src\\test\\resources\\PropertyFiles\\Config.properties");
			//comment the previous two lines and Uncomment this for exporting the code as jar.
			//fis = new FileInputStream(
					 //System.getProperty("user.dir") + "\\resources\\PropertyFiles\\Config.properties");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			config.load(fis);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				fis.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return config;
	}


}
