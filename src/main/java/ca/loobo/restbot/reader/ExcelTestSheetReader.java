package ca.loobo.restbot.reader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import ca.loobo.restbot.Case;
import ca.loobo.restbot.Context;

/**
 * Excel format
 * 		Column groups are separated by at least one empty column
 * 		Column groups must be in the following order
 * 			1. meta infos
 * 			2. result columns filling by this program
 * 			3. request parameters
 * 			4. test expectation definitions, header is in the format of JsonPath, result is regular expression
 * 
 * @author Robert Xu
 *
 */
public class ExcelTestSheetReader {
	static final Logger logger = LoggerFactory.getLogger(ExcelTestSheetReader.class);
	final static String COL_NAMES[] = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q"
		,"R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
	final static int MAX_COL_GROUP = 4;
	final static int LAST_COL_INDEX_OF_META = 0;
	final static int LAST_COL_INDEX_OF_PARAM = 1;
	final static int LAST_COL_INDEX_OF_EXPECTATIONS = 2;
	
	ArrayList<Cell> headerCells = new ArrayList<Cell>();
	public String servicePath = "";
	XSSFWorkbook  workbook;
	int delimitColIndexes[] = new int[MAX_COL_GROUP];
	Map<String, Map<String, String>> paramValueMap = new HashMap<String, Map<String, String>>();
	final Context context;
	
	ExcelTestSheetReader(Context ctx) {
		this.context = ctx;
		Map<String, String> localeValueMap = new HashMap<String, String>();
		localeValueMap.put("en_CA", "{g_locale1}");
		localeValueMap.put("fr_CA", "{g_locale2}");
		
		paramValueMap.put("locale", localeValueMap);
	}
	
	private void parseHeader(Row row) {
		Iterator<Cell> cellIterator = row.cellIterator();
		int curColIndex=0, lastValidColIndex=0;
		int i=0; 

		while (cellIterator.hasNext()) {
			Cell cell = cellIterator.next();
			curColIndex = cell.getColumnIndex();
			
			// skip all empty cells
			if (isCellEmpty(cell)) {
				continue;
			}
			headerCells.add(cell) ;
			if (curColIndex - lastValidColIndex > 1 && delimitColIndexes[i] != lastValidColIndex) {
				delimitColIndexes[i++] = lastValidColIndex;
				if (i >= MAX_COL_GROUP) {
					logger.debug("all delemeters have been found");
					break;
				}
			}
			
			lastValidColIndex = curColIndex;
		}
		
		if (i<MAX_COL_GROUP) {
			delimitColIndexes[i] = lastValidColIndex;
		}
		
		for(Integer d : delimitColIndexes) {
			logger.trace("found dlimiter at col index" + d + " col name" + getColName(d));
		}
	}

	private String getColName(int index) {
		int n = index+1;
		LinkedList<String> lst = new LinkedList<String>();
		while (n > 0) {
			if (n == 26) {
				lst.push(COL_NAMES[25]);				
			}
			else {
				lst.push(COL_NAMES[n%26 - 1]);
			}
			n /= 26;
		}
		
		StringBuilder sb = new StringBuilder();
		for(String a : lst) {
			sb.append(a);
		}
		
		return sb.toString();
	}
	
	private Case parseValues(Row values) {
		Case c = new Case(this.context);

		for(Cell headerCell : headerCells) {
			int i = headerCell.getColumnIndex();
			String name = headerCell.getStringCellValue();
			Cell vcell = values.getCell(i);
			if (vcell == null) {
				vcell = values.createCell(i);
				vcell.setCellValue("");
			}
			String value = ExcelUtils.getCellValue(vcell);
			if (i <= delimitColIndexes[LAST_COL_INDEX_OF_META]) {
					c.setMeta(name, value);
			}
			else if (i <= delimitColIndexes[LAST_COL_INDEX_OF_PARAM]) {
				if (!StringUtils.isEmpty(value)) {
					addQueryParam(c, name, value);
				}
			}
			else {
				if (name.startsWith("$")) {
					c.addExpection(name, value);
				}
			}
		}

		logger.trace(c.getId() + " " + c.getUrlPattern());
		return c.getId() == null || StringUtils.isEmpty(c.getId()) ? null : c;
	}
	
	public void readResource(XSSFSheet testSheet) {
		Iterator<Row> rowIterator = testSheet.iterator();
		parseHeader(rowIterator.next());
		
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			Case c = parseValues(row);
			if (c != null) {
				context.addCase(c.getId(), c);
			}
		}

	}
	
	private boolean isCellEmpty(Cell cell) {
		if (cell.getStringCellValue().trim().length()==0) {
			return true;
		}
		
		return false;
	}

	private void addQueryParam(Case ac, String paraName, String paramValue) {
		ac.getQueryParams().put(paraName, paramValue);
	}	
}
