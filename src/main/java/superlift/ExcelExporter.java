package superlift;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.Session;
import superlift.entities.SuperLiftItem;
import superlift.entities.WheelData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;

public class ExcelExporter {

    private static final String FINAL_FILE_PATH = "src\\main\\resources\\from_db.xlsx";

    public static void saveToExcel(Session session) throws IOException, InvalidFormatException {
        Workbook workbook = writeDBtoExcel(session);
        FileOutputStream fileOut = new FileOutputStream(FINAL_FILE_PATH);
        workbook.write(fileOut);
        fileOut.close();

        // Closing the workbook
        workbook.close();
    }

    public static File prepareReportForEmail(Session session){
        Workbook workbook = writeDBtoExcel(session);
        File file = null;
        try {
            //file =  File.createTempFile("dbToExcel", ".xlsx");
            String fName = formatTime(Instant.now());
            fName = fName.replaceAll(":", "-");
            fName = fName.substring(0, fName.length()-3);
            fName = fName + "_Superlift_parsedItems.xlsx";
            fName = "C:/Dropbox/Superlift/"+ fName;
            file = new File(fName);
            FileOutputStream fileOut = new FileOutputStream(file);
            workbook.write(fileOut);
            fileOut.close();
            workbook.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }

        return file;
    }

    private static Workbook writeDBtoExcel(Session session) {
        List<SuperLiftItem> items = SuperliftDAO.getAllItems();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        //counter used for creating new rows in excel
        Integer counter = 1;
        setFirstRow(sheet);
        for (SuperLiftItem item : items){
            counter = setCells(item,sheet,counter);
        }

        return workbook;
    }

    private static void setFirstRow(Sheet sheet) {
        Row row = sheet.createRow(0);

        Cell cell = row.createCell(0);
        cell.setCellType(CellType.STRING);
        cell.setCellValue("ITEM_ID");

        cell = row.createCell(1);
        cell.setCellType(CellType.STRING);
        cell.setCellValue("PART_NO");

        cell = row.createCell(2);
        cell.setCellType(CellType.STRING);
        cell.setCellValue("TITLE");

        cell = row.createCell(3);
        cell.setCellType(CellType.STRING);
        cell.setCellValue("CATEGORY");

        cell = row.createCell(4);
        cell.setCellType(CellType.STRING);
        cell.setCellValue("INCLUDE");

        cell = row.createCell(5);
        cell.setCellType(CellType.STRING);
        cell.setCellValue("KEY_DETAILS");

        cell = row.createCell(6);
        cell.setCellType(CellType.STRING);
        cell.setCellValue("NOTES");

        cell = row.createCell(7);
        cell.setCellType(CellType.STRING);
        cell.setCellValue("INSTALL_INFO");

        cell = row.createCell(8);
        cell.setCellType(CellType.STRING);
        cell.setCellValue("ITEM_URL");

        cell = row.createCell(9);
        cell.setCellType(CellType.STRING);
        cell.setCellValue("PRICE");

        cell = row.createCell(10);
        cell.setCellType(CellType.STRING);
        cell.setCellValue("ITEM_IMG_LINKS");

        cell = row.createCell(11);
        cell.setCellType(CellType.STRING);
        cell.setCellValue("WHEEL_INFO");

    }

    private static Integer setCells(SuperLiftItem item, Sheet sheet, Integer counter) {
        Row row = sheet.createRow(counter);
        counter++;

        Cell cell = row.createCell(0);
        cell.setCellType(CellType.STRING);
        cell.setCellValue(item.getItemID());

        cell = row.createCell(1);
        cell.setCellType(CellType.STRING);
        cell.setCellValue(item.getPartNo());

        cell = row.createCell(2);
        cell.setCellType(CellType.STRING);
        cell.setCellValue(item.getTitle());

        cell = row.createCell(3);
        cell.setCellType(CellType.STRING);
        cell.setCellValue(item.getCategory());

        cell = row.createCell(4);
        cell.setCellType(CellType.STRING);
        cell.setCellValue(item.getInclude());

        cell = row.createCell(5);
        cell.setCellType(CellType.STRING);
        cell.setCellValue(item.getKeyDetails());

        cell = row.createCell(6);
        cell.setCellType(CellType.STRING);
        cell.setCellValue(item.getNotes());

        cell = row.createCell(7);
        cell.setCellType(CellType.STRING);
        cell.setCellValue(item.getInstallInfo());

        cell = row.createCell(8);
        cell.setCellType(CellType.STRING);
        cell.setCellValue(item.getItemUrl());

        cell = row.createCell(9);
        cell.setCellType(CellType.STRING);
        cell.setCellValue(item.getPrice().toString());

        cell = row.createCell(10);
        cell.setCellType(CellType.STRING);
        cell.setCellValue(item.getImgLinks());

        cell = row.createCell(11);
        cell.setCellType(CellType.STRING);
        cell.setCellValue(getWheelDataStr(item.getWheelData()));

        return counter;
    }

    private static String getWheelDataStr(List<WheelData> wheelData) {
        if (wheelData==null||wheelData.size()==0){
            return "";
        }
        StringBuilder sb = new StringBuilder();
        wheelData.forEach(data->{
            sb.append(data.toString());
            sb.append(System.lineSeparator());
        });
        sb.setLength(sb.length()-2);

        return sb.toString();
    }


    private static String formatTime(Instant instant) {
        DateTimeFormatter formatter =
                DateTimeFormatter.ofLocalizedDateTime( FormatStyle.MEDIUM )
                        .withLocale( Locale.UK )
                        .withZone( ZoneId.systemDefault() );

        return formatter.format(instant);
    }
}
