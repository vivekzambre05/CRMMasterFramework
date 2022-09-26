package com.crm.commonUtilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.sl.usermodel.Sheet;
import org.apache.poi.ss.formula.functions.Columns;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class ExcelOperation
{
	public static XSSFWorkbook wb;
	public static XSSFSheet sheet;
	public static XSSFRow row;
	public static Cell cell;
	public static FileInputStream inputStream ;
	public static FileOutputStream outputStream;
	public static ArrayList<String> sheetNames;
	public static int TotalSheetsNum;
	
	public static Logger log = LoggerFactory.getLogger(ExcelOperation.class);

	static HashMap<String, Integer> excelColumns = new HashMap<String, Integer>();
	static String ExcelPATH = System.getProperty("user.dir")+"\\src\\test\\resources\\TestData\\AutomationTestData.xlsx";
	public static int getRowCount(String sheet) throws IOException
	{
		File f = new File(ExcelPATH);

		//Create an object of FileInputStream class to read excel file
		inputStream = new FileInputStream(f);

		//creating workbook instance that refers to .xlsx file
		wb=new XSSFWorkbook(inputStream); 

		int rowCount = wb.getSheet(sheet).getLastRowNum();

		return rowCount; 
	}
	
	public static ArrayList<String> getSheetsName() throws IOException
	{
		File f = new File(ExcelPATH);

		//Create an object of FileInputStream class to read excel file
		inputStream = new FileInputStream(f);

		//creating workbook instance that refers to .xlsx file
		wb=new XSSFWorkbook(inputStream); 
		
		TotalSheetsNum=wb.getNumberOfSheets();
		log.info("Total Number of sheets= "+TotalSheetsNum);
		sheetNames = new ArrayList<String>();

		for(int i=0;i<TotalSheetsNum;i++) 
		{
			//System.out.println("Sheet name in for Loop : "+wb.getSheetName(i));
			sheetNames.add(wb.getSheetName(i));
		}
		log.info("Sheet Names List from excel = "+sheetNames);
		return sheetNames;
	}

	//To read test data from excel sheet
	public static String readData(String sheet , int rowNum , int colNum) throws EncryptedDocumentException, IOException
	{
		File f = new File(ExcelPATH);

		//Create an object of FileInputStream class to read excel file
		 inputStream = new FileInputStream(f);

		//creating workbook instance that refers to .xls file
		 wb=new XSSFWorkbook(inputStream); 

		String CellData;
		cell  = wb.getSheet(sheet).getRow(rowNum).getCell(colNum);

		//DataFormatter formatter = new DataFormatter();
		//CellData = formatter.formatCellValue(cell);
		//CellData= cell.getStringCellValue();
		//log.info("Excel Data : "+CellData);
	
		try
		{
		  if(cell.getCellType()==CellType.STRING) 
		  { 
			  CellData= cell.getStringCellValue(); 
			  //log.info("Excel Data : "+CellData); 
			  return CellData;
		  } 
		  else if(cell.getCellType()==CellType.NUMERIC)
		  {
			  CellData = NumberToTextConverter.toText(cell.getNumericCellValue());
			  //log.info("Excel Data : "+CellData); 
			  return CellData;
			  
		  } else if(cell.getCellType()==CellType.FORMULA )
		  {
			  CellData = cell.getStringCellValue();
			  //log.info("Excel Data : "+CellData); 
			  return CellData;
		  }else if(cell.getCellType()==CellType.BLANK)
		      return ""; 
		  else 
			  return String.valueOf(cell.getBooleanCellValue());
		}catch(Exception e)
		{
			return "row "+rowNum+" or column "+colNum +" does not exist in xls";
		}
	
	}	
	
	public static ArrayList<String> getcolumnData(String SheetName, String colName) throws IOException
	{	
        ArrayList<String> columnData = new ArrayList<String>();

		File f = new File(ExcelPATH);

		//Create an object of FileInputStream class to read excel file
		inputStream = new FileInputStream(f);

		//creating workbook instance that refers to .xls file
		wb=new XSSFWorkbook(inputStream); 

		sheet = wb.getSheet(SheetName);
		
		//adding all the column header names to the map 'columns'
		sheet.getRow(0).forEach(cell ->{
			excelColumns.put(cell.getStringCellValue(), cell.getColumnIndex());
		});
				
        int lastRowIndex = sheet.getLastRowNum() + 1;
        int colNum = excelColumns.get(colName);
        for (int j = 1; j < lastRowIndex; j++) {
        	String runmode = ExcelOperation.getCellData("TestScenario", "RunMode", j);
			if(runmode.equalsIgnoreCase("Yes"))
			{
	        	String s = readData(SheetName, j, colNum);
	        	if (!(s.isEmpty() || s.isBlank()))
	        		columnData.add(s);
			}
        }
        
	return columnData;

	}

	//To read excel data using column header
	public static String getCellData(String SheetName ,String columnName, int rowNum) throws EncryptedDocumentException, IOException 
	{
		File f = new File(ExcelPATH);

		//Create an object of FileInputStream class to read excel file
		 inputStream = new FileInputStream(f);

		//creating workbook instance that refers to .xls file
		 wb=new XSSFWorkbook(inputStream); 

		 sheet = wb.getSheet(SheetName);
		//XSSFSheet sh = wb.getSheetAt(0);    //0 - index of 1st sheet

		//adding all the column header names to the map 'columns'
		sheet.getRow(0).forEach(cell ->{
			excelColumns.put(cell.getStringCellValue(), cell.getColumnIndex());
		});

		return readData(SheetName, rowNum, excelColumns.get(columnName));
	}


	//To write data into excel
	public static void writeToExcel(String sheetName , int rowNum , int colNum, String data) throws Exception
	{
		try {
			Thread.sleep(1000);
			//System.out.println("Write values : "+sheet +"|"+rowNum+"|"+colNum+"|"+data);
			File f = new File(ExcelPATH);

			//Create an object of FileInputStream class to read excel file
			 inputStream = new FileInputStream(f);

			//creating workbook instance that refers to .xls file
			 wb=new XSSFWorkbook(inputStream); 

			//creating a Sheet object using the sheet Name
			 sheet = wb.getSheet(sheetName);

			//Create a row object to retrieve row at index m
			 row=sheet.getRow(rowNum);
			if(row ==null)
			{
				row = sheet.createRow(rowNum);
			}

			//create a cell object to enter value in it using cell Index
			XSSFCell cell = row.getCell(colNum);
			
			if (cell == null) 
			{
				cell = row.createCell(colNum);
			}
			cell.setCellValue(data);
			//System.out.println("Row : "+rowNum+" | Cell :"+colNum+" | Data : "+data);
			log.info("Write to excel with : Row="+rowNum+" | Cell ="+colNum+" | Data ="+data);

			// Write the data back in the Excel file
			outputStream = new FileOutputStream(ExcelPATH);
			wb.write(outputStream);
			Thread.sleep(1000);

			inputStream.close();
			outputStream.flush();
			outputStream.close();

		} catch (Exception e) {
			throw(e);		
		}

	}

	public static void setCellData(String sheetName,String colName,int rowNum,String data) throws Exception
	{
		File f = new File(ExcelPATH);

		//Create an object of FileInputStream class to read excel file
		 inputStream = new FileInputStream(f);

		//creating workbook instance that refers to .xls file
		 wb=new XSSFWorkbook(inputStream); 

		 sheet = wb.getSheet(sheetName);
		//XSSFSheet sh = wb.getSheetAt(0);    //0 - index of 1st sheet

		//adding all the column header names to the map 'columns'
		sheet.getRow(0).forEach(cell ->{
			excelColumns.put(cell.getStringCellValue(), cell.getColumnIndex());
		});
		
		writeToExcel(sheetName, rowNum, excelColumns.get(colName), data);
		 
	}
	
	
	

}
