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
import java.util.*;

public class Write {
    /**
     * метод генерирует xls файл и осуществляет его запись
     * @param newRow массив записываемых данных
     * @param fileName имя файла
     * @throws IOException кидается при невозможности записать файл
     */
    public void writeToXLS(HashMap<String, String> newRow, String fileName) throws IOException {

        Path p = Paths.get(fileName+".xls");
        String filePath = p.toString();

        if (!Files.exists(p)) {
            createFile(p);
        }

        try (BufferedInputStream fis = new BufferedInputStream(new FileInputStream(filePath))) {
            Workbook workbook = new HSSFWorkbook(fis);
            Sheet sheet = workbook.getSheetAt(0);

            createData(sheet, newRow);

            try (BufferedOutputStream fio = new BufferedOutputStream(new FileOutputStream(filePath))) {
                workbook.write(fio);
            }
        }
    }

    /**
     * Метод создает пустой файл для записи. Вызывать, если !Files.exists вернул false
     * @param p путь к файлу
     * @throws IOException
     */
    private void createFile(Path p) throws IOException {
        Files.createFile(p);
        try (BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(p.toString()))) {
            Workbook workbook = new HSSFWorkbook();
            workbook.createSheet("Ready");
            workbook.write(fos);
            workbook.close();
        }
    }

    /**
     * метод парсит ключ хешмапа, возвращает имена спортсменов
     * @param key ключ хэшмапа
     * @return Массив с именами
     */
    private String[] parsName(String key){
        String[] parsKey = key.trim().split("\\) ");

        String nameOne = parsKey[0]+")";
        String nameTwo = parsKey[1].replace(")","")+")";

        return new String[]{nameOne, nameTwo};
    }

    /**
     * Метод парсит строку со счетом,
     * разбивая её на двумерный массив со счетом каждого игрока
     * @param score строка счета-значение хэшмапа
     * @return String[][] - массив со счетом по турам у игроков
     */
    private String[][] parsScore(String score){
        String[] parsScore = score.trim().split(" ");

        if(parsScore.length<=2)
            return null;
        if((parsScore[0].equals("0") && parsScore[parsScore.length / 2].equals("0")) ||
                (Integer.parseInt(parsScore[0])+ Integer.parseInt(parsScore[parsScore.length / 2]) != parsScore.length / 2 - 1)){
            return null;
        }

        return new String[][]{
                Arrays.copyOf(parsScore, parsScore.length / 2),
                Arrays.copyOfRange(parsScore, parsScore.length / 2, parsScore.length)};

    }



    /**
     * Метод генерирует строки xls файла
     * @param sheet xls страница
     * @param newRow хэшмап с данными
     */
    public void createData(Sheet sheet, HashMap<String, String> newRow){
        int rowCount = sheet.getPhysicalNumberOfRows();


        for (String key: newRow.keySet()) {
            String[] names= parsName(key);
            String[][] scores = parsScore(newRow.get(key));

            if(names.length !=2 || scores == null)
                continue;

            Row row = sheet.createRow(rowCount+=2);

            for(int i =0; i<names.length; i++){
                row = sheet.createRow(rowCount+=1);
                Cell cellName = row.createCell(0);
                cellName.setCellValue(names[i]);
                sheet.autoSizeColumn(0);

                for (int cellIndex = 1, j=0; j < scores[i].length; cellIndex++, j++) {

                    Cell cell = row.createCell(cellIndex);
                    cell.setCellValue(scores[i][j]);

                    sheet.autoSizeColumn(cellIndex);

                }

                if(i==0){
                    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Moscow"));
                    Date date = calendar.getTime();

                    Cell cell = row.createCell(9);
                    cell.setCellValue(date.toString());
                    sheet.autoSizeColumn(9);
                }

            }

        }
    }

}
