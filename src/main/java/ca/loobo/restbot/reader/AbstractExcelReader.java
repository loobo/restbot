package ca.loobo.restbot.reader;

import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import ca.loobo.restbot.Context;

public abstract class AbstractExcelReader implements ResourceReader {
	protected void readInfoSheet(Context context, XSSFSheet sheet) {
		Iterator<Row> rowIterator = sheet.iterator();
		while(rowIterator.hasNext()) {
			Row row = rowIterator.next();
			Cell cell = row.getCell(0);
			if (cell == null) {
				continue;
			}
			
			String name = cell.getStringCellValue();
			if (name == null) {
				continue;
			}
			String value = ExcelUtils.getCellValue(row.getCell(1));
			if (value == null) {
				continue;
			}
			context.addVariable(name, value);
		}
		
		//it has to be done, here, because parameters defined in Info Sheet are required
		context.initialize();
	}
}
