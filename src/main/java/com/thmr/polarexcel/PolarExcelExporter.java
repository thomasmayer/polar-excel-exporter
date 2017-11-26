package com.thmr.polarexcel;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.thmr.polar.PolarFlowAPI;
import com.thmr.polar.Training;
import com.thmr.polar.TrainingList;

/**
 * PolarExcelExporter uses the java-polar-flow-api to get training data and exports it to a excel file
 * data
 * 
 * java PolarExcelExporter <username> <password> <destination-dir>
 * 
 * @author Thomas Mayer
 */

public class PolarExcelExporter
{
	public PolarExcelExporter()
	{
		super();
	}
	
	private int getColumnForSportId(Integer sportId, List<Integer> sportIds)
	{
		int column = 1;
		
		for(Integer i : sportIds)
		{
			if(sportId.equals(i))
			{
				return column;
			}
			else
			{
				column+=2;
			}
		}
		
		return -1;
	}

	private CellStyle getTimeStyle(Workbook wb)
	{
		CellStyle style = wb.createCellStyle();
        DataFormat df = wb.createDataFormat();
        style.setDataFormat(df.getFormat("[h]:mm:ss;@"));
        
        return style;
    }
	
	public Cell getCell(Sheet sheet, int row, int column)
	{
		Row r = sheet.getRow(row);
		Cell c = r.getCell(column);
		
		if(c == null)
		{
			c = r.createCell(column);
		}
		
		return c;
	}
	
	private void initSheet(Sheet sheet, TrainingList tl)
	{
		int maxColumns = (tl.getSportsIds().size() * 2) + 1;
		int maxRows = 4 + tl.getTrainings().size();
		
		for(int r = 0; r < maxRows; r++)
		{
			Row row = sheet.createRow(r);
			
			for(int c = 0; c < maxColumns; c++)
			{
				Cell column = row.createCell(c);
				CellStyle cs = sheet.getWorkbook().createCellStyle();
				
				sheet.setColumnWidth(c, 3500);
				column.setCellStyle(cs);
			}
		}
	}
	
	private void applyBorders(Sheet sheet, TrainingList tl)
	{
		int maxColumns = (tl.getSportsIds().size() * 2) + 1;

		for(int c = 0; c < maxColumns; c++)
		{
			Cell cell = sheet.getRow(1).getCell(c);
			cell.getCellStyle().setBorderBottom(BorderStyle.THIN);
		}	
	}
	
	private void createTitle(Sheet sheet, TrainingList tl)
	{
		Locale currentLocale = Locale.getDefault();
		ResourceBundle labels = ResourceBundle.getBundle("PolarExcelExporter", currentLocale);
	    List<Integer> sportIds = tl.getSportsIds();
		Font font = sheet.getWorkbook().createFont();
		int column = 1;
		
		font.setFontName(HSSFFont.FONT_ARIAL);
		font.setFontHeightInPoints((short)10);
		font.setBold(true);
		
	    for(Integer i: sportIds)
	    {
		    Cell cell = sheet.getRow(0).getCell(column);
		    String translationKey = PolarExporterConfig.sportIdTranslationKeyMapping.get(i);
		    System.out.println("translationkey: " + translationKey + ", sportid: " + i);
		    String columnTitle = labels.getString(translationKey);
		    cell.setCellValue(columnTitle);
		    cell.getCellStyle().setFont(font);
		    
		    Cell cell1 = sheet.getRow(1).getCell(column++);
		    cell1.setCellValue(labels.getString(PolarExporterConfig.DURATION_TRANSLATION_KEY));
		    cell1.getCellStyle().setFont(font);
		    
		    Cell cell2 = sheet.getRow(1).getCell(column++);
		    cell2.setCellValue(labels.getString(PolarExporterConfig.DISTANCE_TRANSLATION_KEY));
		    cell2.getCellStyle().setFont(font);
	    }
	}
	
	private void createContent(Sheet sheet, TrainingList tl)
	{
	    int row = 2;
	    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
	    List<Integer> sportIds = tl.getSportsIds();
	    
	    for (Training training : tl.getTrainings())
		{
			int columnForTraining = getColumnForSportId(training.getSportId(), sportIds);
		    Row contentRow = sheet.getRow(row++);
		 
		    Cell cell = contentRow.getCell(0);
		    cell.setCellValue(sdf.format(training.getStartDateAsDate()));
		    
		    Cell cell2 = contentRow.getCell(columnForTraining);
		    long duration = training.getDuration();
			String hms = String.format("%02d,%02d,%02d", TimeUnit.MILLISECONDS.toHours(duration),
				    TimeUnit.MILLISECONDS.toMinutes(duration) % TimeUnit.HOURS.toMinutes(1),
				    TimeUnit.MILLISECONDS.toSeconds(duration) % TimeUnit.MINUTES.toSeconds(1));
			CellStyle s = getTimeStyle(sheet.getWorkbook());
		    cell2.setCellStyle(s);
		    cell2.setCellFormula("TIME(" + hms + ")"); // 00:15:00
	        cell2.setCellType(CellType.FORMULA);
		    
		    Cell cell3 = contentRow.createCell(columnForTraining+1);
		    cell3.setCellValue(training.getDistanceInKm());
		}
	}
	
	private void createSumRow(Sheet sheet, TrainingList tl)
	{
	    List<Integer> sportIds = tl.getSportsIds();
	    Row sumRow = sheet.getRow(3 + tl.getTrainings().size());
	    
	    for(int i = 1; i < sportIds.size() * 2 + 1; i++)
	    {
		    Cell sumCell = sumRow.getCell(i);
		    char x = (char)(i + 65);
		    String formula = "SUM(" + x + "3:" + x + "" + (2 + tl.getTrainings().size()) + ")";
		    sumCell.setCellFormula(formula);
		    
		    if(i % 2 == 1)
		    {
		        DataFormat df = sheet.getWorkbook().createDataFormat();
		        CellStyle style = getTimeStyle(sheet.getWorkbook());
		        style.setDataFormat(df.getFormat("[h]:mm:ss;@"));
			    sumCell.setCellStyle(style);
		        sumCell.setCellType(CellType.FORMULA);
		    }
	    }
	}
	
	private Workbook createExcel(TrainingList tl)
	{
		Workbook wb = new HSSFWorkbook();
	    Sheet sheet = wb.createSheet("Polar");
	    
	    // initialize sheet with default cell style and create all rows and cells
		initSheet(sheet, tl);

		// create title rows
		createTitle(sheet, tl);
	    
	    // create content
		createContent(sheet, tl);
		
		// create sum row
		createSumRow(sheet, tl);

		applyBorders(sheet, tl);

	    return wb;
	}
	
	private void writeExcel(Workbook wb, String filename)
	{
		try(FileOutputStream fos = new FileOutputStream(filename))
		{
		    wb.write(fos);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private void start(String username, String pw, Date fromDate, Date toDate, String outputfile, boolean mock)
	{
		// create java sdk object
		PolarFlowAPI polarFlowAPI = new PolarFlowAPI();
		TrainingList trainingList = null;
		
		if(!mock)
		{
			// login with username (email) and password
			polarFlowAPI.login(username, pw);

			// get training data
			int[] sportIds = {}; // get all sport ids
			trainingList = polarFlowAPI.getTrainingData(fromDate, toDate, sportIds);
		}
		else
		{
			try
			{
				InputStream is = this.getClass().getClassLoader().getResourceAsStream("trainingdata.json");
				trainingList = polarFlowAPI.getTrainingData(is);
				is.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		Workbook wb = createExcel(trainingList);
		writeExcel(wb, outputfile);
	}
	
	public static void main(String[] args)
	{
		if(args.length < 3)
		{
			System.out.println("ERROR: USAGE: java PolarExcelExporter <username> <password> <outputfile> <from-date dd.MM.yyyy> <to-date dd.MM.yyyy>, please provide username, password and destination-directory as arguments");
			return;
		}

		try
		{
			String username = args[0];
			String pw = args[1];
			String outputfile = args[2];		
			SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
			Date fromDate = sdf.parse(args[3]);
			Date toDate = sdf.parse(args[4]);
			
			System.out.println("starting PolarExcelExport with following arguments: ");
			System.out.println("username: " + username);
			System.out.println("pw: " + pw);
			System.out.println("outputfile: " + outputfile);
			System.out.println("fromDate: " + fromDate.toString());
			System.out.println("toDate: " + toDate.toString());
	
			PolarExcelExporter p = new PolarExcelExporter();
			// mock = true: read json file locally, no remote call, set to false to make remote calls to polar
			p.start(username, pw, fromDate, toDate, outputfile, false);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
