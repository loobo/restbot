package ca.loobo.restbot.reader;

import org.apache.poi.ss.usermodel.Cell;

public class ExcelUtils {
	public static String getCellValue(Cell vcell) {
		if (vcell == null) {
			return "";
		}
		
		String value;
		if (vcell.getCellType() == Cell.CELL_TYPE_STRING) {
			value = vcell.getStringCellValue();
		}
		else if (vcell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
			double d = vcell.getNumericCellValue();
			value = String.valueOf((int)d);
		}
		else if (vcell.getCellType() == Cell.CELL_TYPE_FORMULA) {
			value="";
		}
		else {
			value = vcell.getStringCellValue();				
		}
		return value;
	}
	
}
