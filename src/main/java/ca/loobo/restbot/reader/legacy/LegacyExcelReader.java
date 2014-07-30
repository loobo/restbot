package ca.loobo.restbot.reader.legacy;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.runners.model.InitializationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import ca.loobo.restbot.Case;
import ca.loobo.restbot.Context;
import ca.loobo.restbot.reader.AbstractExcelReader;

public class LegacyExcelReader extends AbstractExcelReader {
	static final Logger logger = LoggerFactory.getLogger(LegacyExcelReader.class);
	
	XSSFWorkbook  workbook;
	
	public LegacyExcelReader() {
	}
	

	public String makeTemplateName(String desc) {
		String[] words = WordUtils.capitalize(desc.toLowerCase().replaceAll("\\.", "")).split(" ");
		words[0] = words[0].toLowerCase();
		return StringUtils.join(words)+"Gen.json";
	}
	
	public ArrayList<Case> readResource(Context context, InputStream excelInputStream) {
		try {

			workbook = new XSSFWorkbook(excelInputStream);
			
			Iterator<XSSFSheet> iter = workbook.iterator();
			while(iter.hasNext()) {
				XSSFSheet sheet = iter.next();
				if (sheet.getSheetName().startsWith("test")) {
					readTestSheet(context, sheet);
				}
				else if (sheet.getSheetName().startsWith("info")) {
					readInfoSheet(context, sheet);
				}
			}

			
			excelInputStream.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void readTestSheet(Context context, XSSFSheet sheet) {
		Iterator<Row> rowIterator = sheet.rowIterator();
		rowIterator.next();
		while(rowIterator.hasNext()) {
			Row row = rowIterator.next();
			Case c = new Case(context);
			String resource = row.getCell(0).getStringCellValue();
			String url = row.getCell(2).getStringCellValue();
			String desc = row.getCell(3).getStringCellValue();
			String id = ""+row.getRowNum();
			
			c.setMeta(Case.META_ID, id);
			c.setMeta(Case.META_DESCRIPTION, desc);
			c.setMeta(Case.META_PATH, url);
			c.setMeta(Case.META_RESOURCE_FOLDER, resource);
			c.setMeta(Case.META_RESPONSE_TEMPLATE, makeTemplateName(desc));
			context.addCase(id, c);
		}
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
	public void readProperties(Context context, Resource resource) throws InitializationError {
		Properties prop = new Properties();
		try {
			prop.load(resource.getInputStream());
			for(Entry<Object, Object> e: prop.entrySet()) {
				context.addVariable(e.getKey().toString(), e.getValue().toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}		
	}	
	
}
