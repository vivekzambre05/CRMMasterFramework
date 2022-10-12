package com.crm.commonUtilities;

import java.awt.Color;
import java.io.*;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.activation.*;
import javax.mail.*;
import javax.mail.internet.*;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.crm.base.SetUp;

public class EmailReporting {
	
	public static Properties emailConfig = new Properties();
	public static Properties config = SetUp.loadConfig();

	
	public static FileInputStream fis;
	
	public static Logger log = LoggerFactory.getLogger(CommonMethods.class);


	public static File getLatestReport() {
		 String sdir = ExtentReporterNG.reportPath;
		 File file = new File(sdir);		 
		 return file;
	}
	
	public static void getChart(int passedTests, int failedTests, int skippedTests) throws FileNotFoundException {
		DefaultPieDataset dataset = new DefaultPieDataset( );
	      dataset.setValue("Passed tests", passedTests );
	      dataset.setValue("Failed tests", failedTests );
	      dataset.setValue("Skipped tests", skippedTests );


	      JFreeChart chart = ChartFactory.createPieChart(
	         "Test Results",   // chart title
	         dataset,          // data
	         true,             // include legend
	         true,
	         false);
          PiePlot ColorConfigurator = (PiePlot) chart.getPlot();
          ColorConfigurator.setLabelGenerator(new StandardPieSectionLabelGenerator("{2}"));/* get PiePlot object for changing */
          
          ColorConfigurator.setSectionPaint("Passed tests", Color.GREEN);
          ColorConfigurator.setSectionPaint("Failed tests",Color.RED);
          ColorConfigurator.setSectionPaint("Skipped tests",Color.YELLOW);

          int width = 440;   /* Width of the image */
	      int height = 280;  /* Height of the image */ 
	      File pieChart = new File( "PieChart.jpeg" ); 
	      try {
	    	  
			ChartUtilities.saveChartAsJPEG( pieChart , chart , width , height );
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	
	public static String formMailBody(LocalDateTime startTime, LocalDateTime endTime) throws ParseException
	{
        Duration duration = Duration.between(startTime, endTime);
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
		long hours = duration.toHours();
		int minutes = (int) ((duration.getSeconds() % (60 * 60)) / 60);
		
		String sb = "<head>" +
                "<style>table{font-family: arial, sans-serif; border-collapse: collapse; width: 70%;}" 
                +"td, th {border: 1px solid #dddddd; text-align: center; padding: 3px;}"
                +"th{background-color:#b3ccff}"+
                "</style>" +
                "</head>" +
                "Dear All,"+
                "<p> Please find below, the automation execution status for " + emailConfig.getProperty("Engagement_Name")+" as below:" +"</p>" + "<p>"
                		+ "<table><tr><th>Start Time</th><td>"+dtf.format(startTime)+"</td></tr><tr><th>End Time</th><td>"+dtf.format(endTime)+"</td></tr><tr><th>Test Execution Time</th><td>"+ hours +":"+ minutes + "(HH:MM)</td></tr></table><img src=\"cid:image\"><br><br><p>Regards,<br>SAG Automation Team</P>";
		return sb;
	}
	
	public static void loadConfig() {
		try {
			fis = new FileInputStream(
					System.getProperty("user.dir") + config.getProperty("EmailConfig"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			emailConfig.load(fis);
			log.debug("Email Config file loaded !!!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private static void zipFolder(Path sourceFolderPath, Path zipPath) throws Exception {
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipPath.toFile()));
        Files.walkFileTree(sourceFolderPath, new SimpleFileVisitor<Path>() {
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                zos.putNextEntry(new ZipEntry(sourceFolderPath.relativize(file).toString()));
                Files.copy(file, zos);
                zos.closeEntry();
                return FileVisitResult.CONTINUE;
            }
        });
        zos.close();
    }

	public static void sendReportViaEmail(int passedTests, int failedTests, int skippedTests, LocalDateTime startTime, LocalDateTime endTime) throws FileNotFoundException {
		
		  loadConfig();
		  String fileName = "";
	      String to = emailConfig.getProperty("to");
	      String from = emailConfig.getProperty("from");

	      final String username = emailConfig.getProperty("userName");
	      final String password = emailConfig.getProperty("password");

		  String host = emailConfig.getProperty("host");

	      Properties props = new Properties();
	      props.put("mail.smtp.auth", "true");
	      props.put("mail.smtp.starttls.enable", "true");
	      props.put("mail.smtp.host", host);
	      props.put("mail.smtp.port", "587");

	      // Get the Session object.
	      Session session = Session.getInstance(props,
	         new javax.mail.Authenticator() {
	            protected PasswordAuthentication getPasswordAuthentication() {
	               return new PasswordAuthentication(username, password);
		   }
	         });

	      try {
		   Message message = new MimeMessage(session);
		
		   // Set From: header field of the header.
		   message.setFrom(new InternetAddress(from));
		
		   // Set To: header field of the header.
		   message.setRecipients(Message.RecipientType.TO,
	               InternetAddress.parse(to));
		
		   // Set Subject: header field
		   message.setSubject(emailConfig.getProperty("subject")+ " | " + LocalDate.now()+" | "+emailConfig.getProperty("Engagement_Name"));
		
		   BodyPart messageBodyPart = new MimeBodyPart();

	       getChart(passedTests, failedTests, skippedTests);

	       String body = formMailBody(startTime, endTime);
	       messageBodyPart.setContent(body, "text/html");

	   
		// Create a multipart message
	       Multipart multipart = new MimeMultipart();

	       // Set text message part
	       multipart.addBodyPart(messageBodyPart);
	       
	       
	    // second part (the image)
	       messageBodyPart = new MimeBodyPart();
	       DataSource fds = new FileDataSource(System.getProperty("user.dir") + "//" + "PieChart.jpeg");

	       messageBodyPart.setDataHandler(new DataHandler(fds));
	       messageBodyPart.setHeader("Content-ID", "<image>");
	         
	       multipart.addBodyPart(messageBodyPart);

	     //File f = getLatestReport();
	      
	       //To zip the results folder
	       try {
	    	   String folderToZip = System.getProperty("user.dir")+"\\Results";
	           String zipName = config.getProperty("ZipFile")+".zip";
	           zipFolder(Paths.get(folderToZip), Paths.get(zipName));
	       } catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	       
	       File f = new File(System.getProperty("user.dir")+ "//"+ config.getProperty("ZipFile")+".zip");
	       if(f!= null)
	    	   fileName = f.getPath();
	       else
	    	   log.debug("Zip file not found");
	       // Part three is attachment
	       messageBodyPart = new MimeBodyPart();

	       DataSource source = new FileDataSource(fileName);
	       messageBodyPart.setDataHandler(new DataHandler(source));
	       //messageBodyPart.setFileName(filename);
	       
	       messageBodyPart.setFileName(new File(fileName).getName());

	       multipart.addBodyPart(messageBodyPart);

	       // Send the complete message parts
	       message.setContent(multipart);
	       
		   // Send message
		   Transport.send(message);

		   log.info("Test Report Mail sent successfully to "+to);
		   System.out.println("Test Report Mail sent successfully to "+to);

	      } catch (MessagingException e) {
	         throw new RuntimeException(e);
	      }
		
	}

}
