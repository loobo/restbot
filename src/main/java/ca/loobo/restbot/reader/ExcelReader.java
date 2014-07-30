package ca.loobo.restbot.reader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.runners.model.InitializationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import ca.loobo.restbot.Case;
import ca.loobo.restbot.Context;

public class ExcelReader extends AbstractExcelReader {
	static final Logger logger = LoggerFactory.getLogger(ExcelReader.class);
	
	XSSFWorkbook  workbook;
	
	public ExcelReader() {
	}
	
	public ArrayList<Case> readResource(Context context, InputStream excelInputStream) {
		try {

			workbook = new XSSFWorkbook(excelInputStream);
			
			readInfoSheet(context, workbook.getSheetAt(0));
			Iterator<XSSFSheet> sheetIterator = workbook.iterator();
			while(sheetIterator.hasNext()) {
				XSSFSheet testSheet = sheetIterator.next();
				if (!testSheet.getSheetName().startsWith("test")) {
					continue;
				}
				new ExcelTestSheetReader(context).readResource(testSheet);
			}
			
			excelInputStream.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	

	@Override
	public void read(Context context, Resource resource) throws InitializationError {
		try {
			InputStream is = resource.getInputStream();
			readResource(context, is);
			is.close();
		} catch(IOException e) {
			logger.error(e.getMessage());
			throw new InitializationError(e);
		}		
	}

	@Override
	public void readProperties(Context context, Resource resource)
			throws InitializationError {
		
	}

}
