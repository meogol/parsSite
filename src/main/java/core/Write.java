package core;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

public class Write {
    public void writeToXLS(HashMap<String, String> newRow, String fileName) throws IOException {

        Path p = Paths.get(fileName+".xls");

        if (!Files.exists( p)) {
            Files.createFile(p);
            try (BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(fileName))) {
                Workbook workbook = new HSSFWorkbook();
                workbook.createSheet("Ready");
                workbook.write(fos);
                workbook.close();
            }
        }

        try (BufferedInputStream fis = new BufferedInputStream(new FileInputStream(fileName))) {

            Workbook workbook = new HSSFWorkbook(fis);
            Sheet sheet = workbook.getSheetAt(0);
            int rowCount = sheet.getPhysicalNumberOfRows();

            createData(sheet, newRow, rowCount);

            try (BufferedOutputStream fio = new BufferedOutputStream(new FileOutputStream(fileName))) {
                workbook.write(fio);
            }
        }
    }


    public void createData(Sheet sheet, HashMap<String, String> newRow, int rowCount){
        for (String key: newRow.keySet()) {

            String score = newRow.get(key);

            String[] parsScore = score.trim().split(" ");

            if((parsScore[0].equals("0") && parsScore[parsScore.length / 2].equals("0")) ||
                    (Integer.parseInt(parsScore[0])+ Integer.parseInt(parsScore[parsScore.length / 2]) != parsScore.length / 2 - 1)){
                continue;
            }

            String[] parsKey = key.trim().split("\\) ");

            Row row = sheet.createRow(rowCount+=2);

            Cell cellName = row.createCell(0);
            cellName.setCellValue(parsKey[0]+")");

            sheet.autoSizeColumn(0);

            for (int cellIndex = 1, i=0; i < parsScore.length/2; cellIndex++, i++) {

                Cell cell = row.createCell(cellIndex);
                cell.setCellValue(parsScore[i]);
            }

            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Moscow"));
            Date date = calendar.getTime();

            Cell cell = row.createCell(9);
            cell.setCellValue(date.toString());


            row = sheet.createRow(rowCount+=1);

            Cell cellNameTwo = row.createCell(0);
            cellNameTwo.setCellValue(parsKey[1].replace(")","")+")");

            sheet.autoSizeColumn(0);

            for (int cellIndex = 1, i=parsScore.length/2; i < parsScore.length; cellIndex++, i++) {
                Cell newCell = row.createCell(cellIndex);
                newCell.setCellValue(parsScore[i]);

                sheet.autoSizeColumn(cellIndex);

            }

            sheet.autoSizeColumn(7);
        }
    }

}
