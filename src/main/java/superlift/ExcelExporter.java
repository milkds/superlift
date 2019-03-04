package superlift;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.Session;
import superlift.entities.Fitment;
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

    public static void saveToExcel() throws IOException, InvalidFormatException {
        Workbook workbook = writeDBtoExcel();
        FileOutputStream fileOut = new FileOutputStream(FINAL_FILE_PATH);
        workbook.write(fileOut);
        fileOut.close();

        // Closing the workbook
        workbook.close();
    }

    public static File prepareReportForEmail(){
        Workbook workbook = writeDBtoExcel();
        File file = null;
        try {
            //file =  File.createTempFile("dbToExcel", ".xlsx");
            String fName = formatTime(Instant.now());
            fName = fName.replaceAll(":", "-");
            fName = fName.substring(0, fName.length()-3);
            fName = fName + "_Superlift_parsedItems.xlsx";
            fName = "C:/Dropbox/Shared_ServerGrisha/SuperliftParse/"+ fName;
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

    private static Workbook writeDBtoExcel() {
        Session session = HibernateUtil.getSession();
        List<SuperLiftItem> items = SuperliftDAO.getAllItems();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        //counter used for creating new rows in excel
        Integer counter = 1;
        setFirstRow(sheet);
        for (SuperLiftItem item : items){
            counter = setCells(item,sheet,counter, session);
        }
        session.close();

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

        cell = row.createCell(12);
        cell.setCellType(CellType.STRING);
        cell.setCellValue("FITS");

        cell = row.createCell(13);
        cell.setCellType(CellType.STRING);
        cell.setCellValue("FITS_2");

        cell = row.createCell(14);
        cell.setCellType(CellType.STRING);
        cell.setCellValue("FITS_2_YS");

        cell = row.createCell(15);
        cell.setCellType(CellType.STRING);
        cell.setCellValue("FITS_2_YF");

        cell = row.createCell(16);
        cell.setCellType(CellType.STRING);
        cell.setCellValue("FITS_2_MAKE");

        cell = row.createCell(17);
        cell.setCellType(CellType.STRING);
        cell.setCellValue("FITS_2_MODEL");

        cell = row.createCell(18);
        cell.setCellType(CellType.STRING);
        cell.setCellValue("WHEEL_INFO_TIRE");

        cell = row.createCell(19);
        cell.setCellType(CellType.STRING);
        cell.setCellValue("WHEEL_INFO_WHEEL");

        cell = row.createCell(20);
        cell.setCellType(CellType.STRING);
        cell.setCellValue("WHEEL_INFO_BACKSPACING");

        cell = row.createCell(21);
        cell.setCellType(CellType.STRING);
        cell.setCellValue("WHEEL_INFO_OFFSET");

        cell = row.createCell(22);
        cell.setCellType(CellType.STRING);
        cell.setCellValue("WHEEL_INFO_BACKSPRING");

        cell = row.createCell(23);
        cell.setCellType(CellType.STRING);
        cell.setCellValue("WHEEL_INFO_BACKSPACING_INCH");

        cell = row.createCell(24);
        cell.setCellType(CellType.STRING);
        cell.setCellValue("WHEEL_INFO_OFFSET_MM");
    }

    private static Integer setCells(SuperLiftItem item, Sheet sheet, Integer counter, Session session) {
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
        cell.setCellValue(getWheelDataStr(SuperliftDAO.getWheelDataForItem(session, item)));

        cell = row.createCell(12);
        cell.setCellType(CellType.STRING);
        cell.setCellValue(getFitDataStr(SuperliftDAO.getFitmentsForItem(session, item)));

        cell = row.createCell(13);
        cell.setCellType(CellType.STRING);
        cell.setCellValue(getShortFitDataStr(item));

        int size = item.getWheelData().size();
        int wheelDataCounter = 0;
        if (size>0){
            wheelDataCounter = counter;
        }

        counter = setDetailedFits(item, counter, sheet, row);
        if (wheelDataCounter!=0){
            wheelDataCounter = setWheelInfo(item, wheelDataCounter, sheet);
            if (counter<wheelDataCounter){
                counter = wheelDataCounter;
            }
        }

        return counter;
    }

    private static int setWheelInfo(SuperLiftItem item, int wheelDataCounter, Sheet sheet) {
        List<WheelData> data = item.getWheelData();
        Row row = sheet.getRow(wheelDataCounter-1);
        fillExtendedWheelData(row, data.get(0));
        int newCounter = wheelDataCounter;
        int dataSize = data.size();
        if (dataSize>1){
            for (int i = 1; i < dataSize; i++) {
                row = sheet.getRow(newCounter);
                if (row==null){
                    row = sheet.createRow(newCounter);
                }
                fillExtendedWheelData(row, data.get(i));
                newCounter++;
            }
        }


        return newCounter;
    }

    private static void fillExtendedWheelData(Row row, WheelData wheelData) {
        Cell cell = null;

        cell = row.createCell(18);
        cell.setCellType(CellType.STRING);
        cell.setCellValue(wheelData.getTire());

        cell = row.createCell(19);
        cell.setCellType(CellType.STRING);
        cell.setCellValue(wheelData.getWheel());

        cell = row.createCell(20);
        cell.setCellType(CellType.STRING);
        cell.setCellValue(wheelData.getBackspacing());

        cell = row.createCell(21);
        cell.setCellType(CellType.STRING);
        cell.setCellValue(wheelData.getOffset());

        cell = row.createCell(22);
        cell.setCellType(CellType.STRING);
        cell.setCellValue(wheelData.getBackspring());

        cell = row.createCell(23);
        cell.setCellType(CellType.STRING);
        cell.setCellValue(wheelData.getBackspacingInch());

        cell = row.createCell(24);
        cell.setCellType(CellType.STRING);
        cell.setCellValue(wheelData.getOffsetMM());
    }

    private static Integer setDetailedFits(SuperLiftItem item, Integer counter, Sheet sheet, Row row) {
        List<Fitment> fitments = item.getFits();
        int size = fitments.size();
        if (size>0){
            Fitment firstFit = fitments.get(0);
            fillExtendedFit(row, firstFit);
            if (size>1){
                for (int i = 1; i < size; i++) {
                   row = sheet.createRow(counter);
                   fillExtendedFit(row, fitments.get(i));
                   counter++;
                }
            }
        }

        return counter;
    }

    private static void fillExtendedFit(Row row, Fitment fit) {
        Cell cell = null;

        cell = row.createCell(14);
        cell.setCellType(CellType.STRING);
        cell.setCellValue(fit.getYearStart());

        cell = row.createCell(15);
        cell.setCellType(CellType.STRING);
        cell.setCellValue(fit.getYearFinish());

        String car = fit.getCar();
        String make = car.split(" ")[0];
        String model = car.replace(make, "").trim();

        cell = row.createCell(16);
        cell.setCellType(CellType.STRING);
        cell.setCellValue(make);

        cell = row.createCell(17);
        cell.setCellType(CellType.STRING);
        cell.setCellValue(model);

    }

    private static String getShortFitDataStr(SuperLiftItem item) {
        StringBuilder builder = new StringBuilder();
        item.getFits().forEach(fit->{
            builder.append(fit.getYearStart());
            builder.append("-");
            builder.append(fit.getYearFinish());
            builder.append(" ");
            builder.append(fit.getCar());
            builder.append(System.lineSeparator());
        });
        int length = builder.length();

        if (length>0){
            builder.setLength(length-2);
        }

        return builder.toString();
    }

    private static String getFitDataStr(List<Fitment> fits) {
        StringBuilder builder = new StringBuilder();
        fits.forEach(fit->{
            builder.append(fit.toString());
            builder.append(System.lineSeparator());
        });
        int length = builder.length();

        if (length>0){
            builder.setLength(length-2);
        }

        return builder.toString();
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
