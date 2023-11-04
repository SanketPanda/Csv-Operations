import com.opencsv.CSVReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Main {

	public static void main(String[] args) {
		System.out.println("Hello World!");
		String[] listOfColumnHeaders = {"Data Source",
		"Transaction ID", "Match ID", "Status", "Account ID", "Alert ID", "Alert Name", "Alert Status", "Created Date", "Edited", "Extract Status", "Import Job ID", "Journal Export Job ID", "Match Process Name", "Matched", "Matched By", "Matched On", "Rule Name", "Rule Type", "Support ID", "Support Type", "Supported By", "Supported On", "Adjusted", "POS M", "POS Memo"};
		System.out.println(verifyCSVFileContainsListOfColumnHeaders("CSVDataFile.csv", listOfColumnHeaders));
	}

	public static boolean verifyCSVFileContainsListOfColumnHeaders(String cSVFilePath, String[] listOfColumnHeaders){
		try (CSVReader reader = new CSVReader(new FileReader(cSVFilePath))) {
			String[] headers = reader.readNext();
			boolean finalResult = true;
			if (headers != null) {
				for (String columnHeader : listOfColumnHeaders) {
					boolean found = false;
					for (String header : headers) {
						if (columnHeader.equals(header)) {
							found = true;
							System.out.println(columnHeader + " column header present in CSV file header columns.");
							break;
						}
					}
					if (!found) {
						System.out.println("FAIL: " + columnHeader + " column header NOT present in CSV file header columns.");
						finalResult = false;
					}
				}
				return finalResult;
			} else {
				System.out.println("CSV file is empty or does not have headers.");
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean validateColumnHasOnlyOneCellValueInAllRows(String cSVFilePath, String columnName, String cellValue) {
		try (CSVReader reader = new CSVReader(new FileReader(cSVFilePath))) {
			String[] headers = reader.readNext();

			if (headers != null) {
				int columnIndex = findColumnIndex(headers, columnName);

				if (columnIndex != -1) {
					String[] row;
					while ((row = reader.readNext()) != null) {
						if (row.length > columnIndex && !row[columnIndex].equals(cellValue)) {
							System.out.println("FAIL: Row contains a different value in column " + columnName);
							return false;
						}
					}
					return true;
				} else {
					System.out.println("Column '" + columnName + "' not found in CSV file.");
					return false;
				}

			} else {
				System.out.println("CSV file is empty or does not have headers.");
				return false;
			}

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	private int findColumnIndex(String[] headers, String columnName) {
		for (int i = 0; i < headers.length; i++) {
			if (headers[i].equals(columnName)) {
				return i;
			}
		}
		return -1;
	}

	public boolean validateCellValuesInFilteredRow(String cSVFilePath, String[][] listOfFilters, HashMap<String, String> inputMap) {
		try (CSVReader reader = new CSVReader(new FileReader(cSVFilePath))) {
			String[] headers = reader.readNext();

			if (headers != null) {
				String[] row;
				while ((row = reader.readNext()) != null) {
					if (matchesFilters(row, headers, listOfFilters)) {
						for (String columnName : inputMap.keySet()) {
							int columnIndex = findColumnIndex(headers, columnName);

							if (columnIndex != -1) {
								String actualValue = row[columnIndex];
								String expectedValue = inputMap.get(columnName);

								if (actualValue.equals(expectedValue)) {
									System.out.println(columnName + " cell matches with the expected value.");
								} else {
									System.out.println("FAIL: " + columnName + " cell DOES NOT match with the expected value. Expected Value = " + expectedValue + ". Actual Value = " + actualValue);
									return false;
								}
							} else {
								System.out.println("Column '" + columnName + "' not found in CSV file.");
								return false;
							}
						}
					}
				}
				return true;
			} else {
				System.out.println("CSV file is empty or does not have headers.");
				return false;
			}

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean matchesFilters(String[] row, String[] headers, String[][] filters) {
		for (String[] filter : filters) {
			int index = findColumnIndex(headers, filter[0]);

			if (index != -1 && row.length > index && row[index].equals(filter[1])) {
				return true;
			}
		}
		return false;
	}
}
