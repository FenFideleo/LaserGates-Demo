/*
 *   The purpose of this class is to enable program to write directly to an .xlsx(Excel) file
 *
 *
 *   Process:
 *       - When program initializes, creates spreadsheet with current date and time as file name
 *           in same folder as executable
 *       - When test configured, ensure Test has own sheet and the layout follows as in the Word doc
 *           - e.g Test 1 prints info to Sheet1, which will be named Test1
 *       - When test ends, print total laps, total time, and total as shown below and save the file
 *       - When program ends, BE SURE TO CLOSE THE STREAM TO THE FILE
 *       - THE FILE DOES NOT SAVE AS IT GOES
 *
 *   Fields:
 *       private String fileName
 *       private XSSFWorkbook workbook
 *       private ArrayList<XSSFSheet> spreadsheets
 *       private ArrayList<XSSFRow> rows
 *       private FileOutputStream out
 *
 *
 *   Functions:
 *       private:
 *           write(<XSSFRow>, Object[])  - Writes array contents into specified row
 *
 *       public:
 *           WriteToExcel() - Creates a new WriteToExcel object that creates a new workbook object
 *           createSheet(<String>) - Creates a new sheet with String as name
 *           addRow(<int>, Object[]) - Creates new row at specified index and adds all objects within object array in cells
 *           save() - Saves by closing file stream and overwriting currently existing file if needed.
 *           close()
 *
 *
 * */


// Apache.poi library important statements
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
// Java library import statements
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class WriteToExcel {
    // Class fields
    final private String fileName;     //  Stores file name to be saved
    final private File file;            // Holds higher level file object
    private XSSFWorkbook workbook;      // Stores reference to workbook object
    private XSSFSheet currSheet; // Stores current sheet
    private ArrayList<XSSFSheet> spreadsheets;      // Stores array for sheets added to workbook object
    private ArrayList<XSSFRow> rows;        // Stores rows for an individual sheet
    private FileOutputStream out;

    public WriteToExcel() throws IOException {

        // Makes file named according to format yyyy.mm.dd_hh.mm.ss (like a timestamp)
        fileName = new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss").format(new java.util.Date()) + ".xlsx";
        // File file = new File("user.dir/TestData", fileName);

        File dir = new File("TestData");
        dir.mkdirs();
        file = new File(dir, fileName);
        file.createNewFile();


        // Create output stream to file
        out = new FileOutputStream(file);

        // Creates high level workbook
        workbook = new XSSFWorkbook();

        // Creates array of spreadsheets
        spreadsheets = new ArrayList<>();

    }

    private void write() throws IOException {  // Writes contents to spreadsheet file

        workbook.write(out);

    }

    public void createSheet(String testName) throws IOException {     // Creates new sheet to be written to and write headers
        if (spreadsheets.size() > 0) {
            save();
        }
        String sheetName = testName;                            // Creates new sheet and adds to sheet array
        currSheet = workbook.createSheet(sheetName);
        spreadsheets.add(currSheet);

        rows = new ArrayList<>();                               // Creates header row and adds to rows array
        Object[] headerContents = {"Lap #", "Time", ""};
        addRow(0, headerContents);

    }

    public void addRow(int index, Object[] contents) throws IOException{     // Adds new row with cells to sheet and writes to file
        // Rows added following creation of a new sheet should not have index 0
        rows.add(currSheet.createRow(index));
        XSSFRow row = rows.get(index);
        for (int i = 0; i < 3; i++) {
            Cell cell = row.createCell(i);
            if (contents[i] instanceof Integer) {
                cell.setCellValue((Integer) contents[i]);
            }
            else if (contents[i] instanceof String) {
                cell.setCellValue((String) contents[i]);
            }
        }

    }

    // Saves contents by overwriting existing file
    // Not ideal way of saving, but couldn't figure out anything else
    // What's the difference between save() and close()?
    // Use save() if the program is still going to write to the workbook
    // Else, use close()
    public void save() throws IOException {

        write();
        out.close();
        out = new FileOutputStream(file);
    }

    // Writes then closes the output stream for good
    public void close() throws IOException {

        write();
        out.close();

    }
}
