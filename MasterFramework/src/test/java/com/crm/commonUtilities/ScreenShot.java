package com.crm.commonUtilities;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.imageio.ImageIO;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.crm.base.SetUp;

import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;

public class ScreenShot extends SetUp 
{
	static Logger log = LoggerFactory.getLogger(ScreenShot.class);
	static String FileDateTime = null;
	static File FilePath = null;
	static String folderDate = new SimpleDateFormat("dd-MM-yyyy HH").format(new Date());

	
	//Variables for Screenshots location 
		public static String currentDir;
		public static String outPutFolder;
		public static String PassScreenShotFolder ;
		public static String FailedScreenShotFolder;
		public static File flOutput;
		public static File flPassScreenShotFolder, src, dest;
		public static File flFailedScreenShotFolder;
		public static String PassScreenShot;
		public static String FailedScreenShot;
		
		public static String ScreenShotPath;
		public static String ScreenShotName;
		public static Properties config = SetUp.loadConfig();


	
	//Create a folder to save Pass screenshots	
	public static File screenShotFolder(String status)
	{
			//folderDate = java.time.LocalDate.now().toString();
			//folderDate = new SimpleDateFormat("dd-MM-yyyy HH").format(new Date());
		  
			currentDir = System.getProperty("user.dir")+"\\Results";
			
			if(config.getProperty("RunExecutedFromJar").equalsIgnoreCase("YES"))
			{
				String runID = config.getProperty("RunID");
				outPutFolder = currentDir +"\\" + runID;
				PassScreenShot = runID + "_PassScreenShot";
				PassScreenShotFolder = outPutFolder+"\\"+PassScreenShot;
				FailedScreenShot = runID + "_FailedScreenShot";
				FailedScreenShotFolder = outPutFolder+"\\"+FailedScreenShot;
			}
			else
			{
				outPutFolder = currentDir +"\\Output_"+folderDate;
				PassScreenShot ="PassScreenShot_"+folderDate;
				PassScreenShotFolder = outPutFolder+"\\"+PassScreenShot;
				FailedScreenShot = "FailedScreenShot_"+folderDate;
				FailedScreenShotFolder = outPutFolder+"\\"+FailedScreenShot;
			}
		   
			flOutput = new File(outPutFolder);
			if(!flOutput.exists()) {
				if(flOutput.mkdir()) {
					System.out.println("Directory is created!");
					log.debug("Output Directory Created..");
					}
				else {
					System.out.println("Failed to create directory!");
	                log.error("Failed to Create Output Directory ");
					}
				}
			
			if(status.equalsIgnoreCase("Pass"))
			{
				flPassScreenShotFolder = new File(PassScreenShotFolder);
				if(!flPassScreenShotFolder.exists()) {
					if(flPassScreenShotFolder.mkdir()) {
						System.out.println("Pass Screen shot Directory is created!");
						log.debug("PassScreenshot Directory Created..");
						}
					else {
		                System.out.println("Failed to create directory!");
		                log.error("Failed to Create PassScreenshot Directory ");
						}
					}
					FilePath = flPassScreenShotFolder;
					//return FilePath;
			}
			else if(status.equalsIgnoreCase("Fail"))
			{
				flFailedScreenShotFolder=new File(FailedScreenShotFolder);
				if(!flFailedScreenShotFolder.exists())
				{
					if(flFailedScreenShotFolder.mkdir())
					{
						System.out.println("Failed Screen shot Directory is created!");
						log.debug("FailScreenshot Directory Created..");
					}
					else
					{
						System.out.println("Failed to create directory!");
		                log.error("Failed to Create FailScreenshot Directory ");
					}
					FilePath = flFailedScreenShotFolder;
				}
				//return FilePath;
			}
				//FilePath = flScreenShotFolder+"\\"+leadCreationTest1.TestCaseDesc+"_"+fileName+".jpeg";
			
		return FilePath;
			
	}
	
	
	//To capture screen shots of Pass cases and web page display on screen 
	public static String takeSnapShot(String name, String status) throws Exception
	{
		ScreenShotPath =null;
		try {
			//Convert web driver object to TakeScreenshot
			TakesScreenshot scrShot =((TakesScreenshot)driver);

			//Call getScreenshotAs method to create image file
			File SrcFile=scrShot.getScreenshotAs(OutputType.FILE);

			//Move image file to new destination
			File DestFile=new File(screenShotFolder(status)+"\\"+name+".jpeg");
			ScreenShotName  =screenShotFolder(status)+"\\"+name+".jpeg";
			
			//Copy file at destination
			FileUtils.copyFile(SrcFile, DestFile);
			log.info("ScreenShot captured "+name+ " || Status :"+status);
		}catch(Exception e)
		{
			log.error("Unbale to capture screenshot due to "+e.getMessage());
		}
		return ScreenShotPath;
    }
	
	//To capture screen shot of complete web page from start to end
	public static void Ashot(String fileName, String status) throws IOException
	{
		try {
		Screenshot ss= new AShot().shootingStrategy(ShootingStrategies.viewportPasting(700)).takeScreenshot(driver);
		String ScreenShotPath=screenShotFolder(status)+"\\"+fileName+".jpeg";
		
		ImageIO.write(ss.getImage(), "jpeg", new File(ScreenShotPath));
		log.info("AShot captured "+fileName+ " || Status :"+status);

		}
		catch(Exception e) {
			log.error("Unbale to capture Ashot due to "+e.getMessage());
		}
	}

}

