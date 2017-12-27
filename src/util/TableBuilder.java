package util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * TableBuilder allows for the creation of 
 * tables through the use of formatted print
 * statement.
 * @author jdowd
 *
 */
public class TableBuilder {
	/**
	 * the number of spaces generated between columns.
	 */
	private static final int NUMBER_OF_SPACES = 2;
	/**
	 * the number of columns.
	 */
	private int nCol;
	/**
	 * the widths of each column.
	 */
	private int[] colWidths;

	/**
	 * the values of the column headers.
	 */
	private String[] colHeaders;
	/**
	 * the contents of each row.
	 */
	private List<String[]> colContents = new ArrayList<String[]>();
	/**
	 * the printf format string.
	 */
	private StringBuilder formatOption = new StringBuilder();
	/**
	 * whether the user had their own widths or not.
	 */
	private boolean specificWidths = false;
	/**
	 * if the table is meant to be right justified.
	 */
	private boolean rightJustified = false;
	
	
	/**
	 * this is a no arg constructor,
	 * it should be used when adding
	 * columns to a table.
	 */
	public TableBuilder() {
		nCol = 0;
		colWidths = new int[nCol];
		colHeaders = new String[nCol];
	}
	
	
	/**
	 * this constructor allows for the creation
	 * of a one by one table
	 * for the display of simple strings.
	 * @param simpleMessage the message to display
	 */
	public TableBuilder(String simpleMessage) {
		nCol = 1;
		colWidths = new int[nCol];
		colHeaders = new String[nCol];
		colHeaders[0] = simpleMessage;
		checkLengths();
		calculateColWidth();
	}
	//
	/**
	 * this constructor allows for the
	 * insertion of the column headers.
	 * @param pColHeaders the column headers
	 */
	public TableBuilder(String[] pColHeaders) {
		nCol = pColHeaders.length;
		colWidths = new int[nCol];
		colHeaders = pColHeaders;
		checkLengths();
		calculateColWidth();
	}
	/**
	 * @param pColHeaders
	 * @param pColWidths
	 */
	public TableBuilder(String[] pColHeaders, int[] pColWidths) {
		nCol = pColHeaders.length;
		colHeaders = pColHeaders;
		checkLengths();
		checkNegativeWidths();
		specificWidths = false;
	}
	/**
	 * @param pColHeaders
	 * @param pColContents
	 */
	public TableBuilder(String[] pColHeaders, List<String[]> pColContents) {
		nCol = pColHeaders.length;
		colWidths = new int[nCol];
		colHeaders = pColHeaders;
		colContents = pColContents;
		checkLengths();
		calculateColWidth();
	}
	/**
	 * @param pColWidths specify the width of each column
	 * @param pColHeaders
	 * @param pColContents
	 */
	public TableBuilder(String[] pColHeaders, List<String[]> pColContents, int[] pColWidths) {
		nCol = pColHeaders.length;
		colWidths = pColWidths;
		colHeaders = pColHeaders;
		colContents = pColContents;
		checkLengths();
		checkNegativeWidths();
		specificWidths = false;
	}
	/**
	 * allows for the manual setting of the
	 * width of each column.
	 * @param colWidths the width of each column
	 */
	public void setColWidths(int[] colWidths) {
		this.colWidths = colWidths;
		checkLengths();
	}
	
	/**
	 * @return the colWidths
	 */
	public int[] getColWidths() {
		return colWidths;
	}


	/**
	 * allows for the manual setting of
	 * the header of each column.
	 * @param colHeaders the headers of the table
	 */
	public void setColHeaders(String[] colHeaders) {
		nCol = colHeaders.length;
		this.colHeaders = colHeaders;
		//checkLengths();
	}
	
	/**
	 * allows for the manual setting of
	 * the header of each column.
	 * @param colHeaders the headers of the table
	 */
	public void setColHeaders(List<String> colHeaders) {
		nCol = colHeaders.size();
		this.colHeaders = new String[colHeaders.size()];
		this.colHeaders = colHeaders.toArray(this.colHeaders);
		//checkLengths();
	}
	
	/**
	 * @param colContents the column contents
	 */
	public void setColContents(List<String[]> colContents) {
		this.colContents = colContents;
		checkLengths();
	}
	
	
	/**
	 * allows for the addition of an
	 * entire row to the table
	 * @param row the row to be added
	 */
	public void addRow(String[] row) {
		colContents.add(row);
		//checkLengths();
	}
	
	/**
	 * allows for the addition of an
	 * entire row to the table
	 * @param row the row to be added
	 */
	public void addRow(List<String> row) {
		String[] array = row.toArray(new String[row.size()]);
		colContents.add(array);
		//checkLengths();
	}
	
	/**
	 * @return the number of rows
	 */
	public int getNumberOfRows() {
		if (colContents.isEmpty()) {
			return 0;
		} else {
			return colContents.size();
		}
	}
	/**
	 * @return the printf format string
	 */
	public String getFormatString() {
		formatOption = new StringBuilder();
		
		if (!specificWidths) {
			calculateColWidth();
		}
		for (int i = 0; i < nCol; i++) {
			if (rightJustified) {
				formatOption.append("%" + colWidths[i] + "s");
			} else {
				formatOption.append("%-" + colWidths[i] + "s");
			}
		}
		
		return formatOption.toString();
	}
	/**
	 * @param pRowNumber the row number of the table
	 * @return the contents of that row
	 */
	public Object[] getColumnStrings(int pRowNumber) {
		return colContents.get(pRowNumber);
	}
	/**
	 * @return the header row
	 */
	public Object[] getHeaderStrings() {
		return colHeaders;
	}
	/**
	 * @param pRowNumber the row number of the field
	 * @param pColumnNumber the column number of the field
	 * @return the String in that field 
	 */
	public String getFieldString(int pRowNumber, int pColumnNumber) {
		return colContents.get(pRowNumber)[pColumnNumber];
	}
	/**
	 * @param pColumnNumber the column number of the header field
	 * @return the String in that header field
	 */
	public String getHeaderString(int pColumnNumber) {
		return colHeaders[pColumnNumber];
	}
	
	/**
	 * checks to ensure that the lengths of all the 
	 * variables match up.
	 */
	private void checkLengths () {
		//only do this part if their are no extra columns
		if (colContents.isEmpty()) {
			if (colHeaders.length != nCol
					|| colHeaders.length != colWidths.length
					|| colWidths.length != nCol) {
					throw new IllegalArgumentException ("numbers of columns do not match");
				}
		} else {
			for (int i = 0; i < getNumberOfRows(); i++) {
				//ensures all numbers of column lengths are the same
				if (colContents.get(i).length != nCol
						|| colContents.get(i).length != colHeaders.length
						|| colContents.get(i).length != colWidths.length
						|| colHeaders.length != nCol
						|| colHeaders.length != colWidths.length
						|| colWidths.length != nCol) {
						throw new IllegalArgumentException ("numbers of columns do not match");
					}
			}
		}
	}
	
	
	/**
	 * makes sure that column widths are not negative.
	 */
	private void checkNegativeWidths() {
		for (int i = 0; i < nCol; i++) {
			if (colWidths[i] < 0) {
				throw new IllegalArgumentException ("widths may not be negative");
			}
		}
	}
	
	/**
	 * sets the column width for each column
	 * with added padding.
	 */
	private void calculateColWidth(){
		colWidths = new int[nCol];
		int[] longests = new int[nCol];
		//look through every column and find longest one.
		//automatically set headers as longest to start
		for (int i = 0; i < nCol; i++) {
			longests[i] = getHeaderString(i).length();
		}
		//if their are more columns, check them against previous longests
		if (!colContents.isEmpty()) {
			for (int r = 0; r < getNumberOfRows(); r++) {
				for (int c = 0; c < nCol; c++) {
					if (longests[c] < getFieldString(r, c).length() ) {
						longests[c] = getFieldString(r, c).length();
					}
				}
			}
		}
		for (int i = 0; i < nCol; i++) {
			colWidths[i] = longests[i] + NUMBER_OF_SPACES;
		}
	}

	/**
	 * allows for the addition of an entire column
	 * to the table.
	 * @param header the header value of the column
	 * @param values the values of the column
	 */
	public void addColumn (String header, List<String> values) {
		nCol++;
		colHeaders = Arrays.copyOf(colHeaders, nCol);
		colHeaders[(nCol-1)] = header;
		
		String tempContents[];
		if (colContents.isEmpty()) {
			for  (int i = 0; i < values.size(); i++) {
				tempContents = new String[1];
				tempContents[0] = values.get(i);
				addRow(tempContents);
			}
		} else {
			
			for (int i = 0; i < colContents.size(); i++) {
				tempContents = Arrays.copyOf(colContents.get(i), nCol);
				tempContents[(nCol-1)] = values.get(i);
				colContents.set(i, tempContents);
			}
		}
	}
	
	/**
	 * allows for the addition of an entire column
	 * to the table.
	 * @param header the header value of the column
	 * @param values the values of the column
	 */
	public void addColumn (String header, String value) {
		nCol++;
		colHeaders = Arrays.copyOf(colHeaders, nCol);
		colHeaders[(nCol-1)] = header;
		
		List<String> values = new ArrayList<String>();
		values.add(value);
		
		String tempContents[];
		if (colContents.isEmpty()) {
			for  (int i = 0; i < values.size(); i++) {
				tempContents = new String[1];
				tempContents[0] = values.get(i);
				addRow(tempContents);
			}
		} else {
			
			for (int i = 0; i < colContents.size(); i++) {
				tempContents = Arrays.copyOf(colContents.get(i), nCol);
				tempContents[(nCol-1)] = values.get(i);
				colContents.set(i, tempContents);
			}
		}
	}
	
	/**
	 * clears the current TableBuilder.
	 */
	public void clear() {
		nCol = 0;
		colWidths = new int[nCol];
		colHeaders = new String[nCol];
		colContents = new ArrayList<String[]>();
		formatOption = new StringBuilder();
		specificWidths = false;
		rightJustified = false;
	}
	
	/**
	 * set the table to right justified.
	 */
	public void rightJustified() {
		rightJustified  = true;
	}




}
