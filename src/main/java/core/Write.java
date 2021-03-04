package core;

import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.*;

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


        try {
            if (!Files.exists(p)) {
                createFile(p);
            }

            WritableWorkbook xlsFile = Workbook.createWorkbook(
                    new File(p.toString()), Workbook.getWorkbook(
                            new File(p.toString())));
            var s = xlsFile.getSheets();
            var excelSheet = s[0];


            Label label = new Label(0, excelSheet.getRows()+1, "Test Count");
            excelSheet.addCell(label);

            xlsFile.write();
            xlsFile.close();


        } catch (WriteException | BiffException e) {
            e.printStackTrace();
        }

        //
//        try (BufferedInputStream fis = new BufferedInputStream(new FileInputStream(filePath))) {
//            Workbook workbook = new HSSFWorkbook(fis);
//            Sheet sheet = workbook.getSheetAt(0);
//
//            createData(sheet, newRow);
//
//
//            try (BufferedOutputStream fio = new BufferedOutputStream(new FileOutputStream(filePath))) {
//                workbook.write(fio);
//            }
//        }
    }

    /**
     * Метод создает пустой файл для записи. Вызывать, если !Files.exists вернул false
     * @param p путь к файлу
     * @throws IOException
     */
    private void createFile(Path p) throws IOException, WriteException {
        WritableWorkbook xlsFile = Workbook.createWorkbook(new File(p.toString()));
        WritableSheet excelSheet = xlsFile.createSheet("test", 0);

        xlsFile.write();
        xlsFile.close();
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
     //* @param sheet xls страница
     * @param newRow хэшмап с данными
     */
    public void createData(/*Sheet sheet,*/ HashMap<String, String> newRow){
//        int rowCount = sheet.getPhysicalNumberOfRows()+2;
//
//
//        for (String key: newRow.keySet()) {
//            String[] names= parsName(key);
//            String[][] scores = parsScore(newRow.get(key));
//
//            if(names.length !=2 || scores == null)
//                continue;
//
//            Row row = sheet.createRow(rowCount+=2);
//
//            System.out.println();
//            System.out.println();
//            System.out.println();
//            System.out.println();
//            System.out.println(Arrays.toString(names));
//            System.out.println(Arrays.deepToString(scores));
//
//            for(int i =0; i<names.length; i++){
//                row = sheet.createRow(rowCount+=1);
//                Cell cellName = row.createCell(0);
//                cellName.setCellValue(names[i]);
//                sheet.autoSizeColumn(0);
//                System.out.println();
//
//                System.out.print(names[i] +"CV"+cellName.getStringCellValue());
//
//                for (int cellIndex = 1, j=0; j < scores[i].length; cellIndex++, j++) {
//
//                    Cell cell = row.createCell(cellIndex);
//                    cell.setCellValue(scores[i][j]);
//
//                    sheet.autoSizeColumn(cellIndex);
//                    System.out.print(" "+scores[i][j] +"CV"+cell.getStringCellValue());
//
//                }
//
//                if(i==0){
//                    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Moscow"));
//                    Date date = calendar.getTime();
//
//                    Cell cell = row.createCell(9);
//                    cell.setCellValue(date.toString());
//                    sheet.autoSizeColumn(9);
//                }
//
//            }
//        }
    }

    /**
     * Чтение *.xls файла, выбранного в диалоговом окне
     * @param fileName - имя файла из диалогового окна
     */
    public void readXls(String fileName){
        try (BufferedInputStream fis = new BufferedInputStream(new FileInputStream(fileName))) {
            Workbook workbook = new HSSFWorkbook(fis);
            Sheet sheet = workbook.getSheetAt(0);

            filePars(sheet);
            try (BufferedOutputStream fio = new BufferedOutputStream(new FileOutputStream(fileName))) {
                workbook.write(fio);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Парсинг файла с дальнейшей записью
     * @param sheet - лист, прочтённый в методе readXls
     */
    private void filePars(Sheet sheet){
        int rowCount = sheet.getPhysicalNumberOfRows();
        Row row;
        String sportsmanName = "";
        int sportsmanScoreOnTable;
        HashMap<String, Integer> sportsmanScore = new HashMap<>();


        for (int i=3;i<=rowCount;i++){
            row = sheet.getRow(i);

            if (row == null ) continue;
                if ((row.getCell(0).getCellType() == HSSFCell.CELL_TYPE_STRING)) {

                    sportsmanName = row.getCell(0).getStringCellValue();
                    sportsmanScoreOnTable = Integer.valueOf(row.getCell(1).getStringCellValue());

                    if(sportsmanScore.containsKey(sportsmanName)){
                        int k = sportsmanScore.get(sportsmanName);
                        sportsmanScore.put(sportsmanName,(k + sportsmanScoreOnTable));
                    }
                    else {
                        sportsmanScore.put(sportsmanName,sportsmanScoreOnTable);
                    }

                } else continue;
        }
        int k=3;
        for (String key:sportsmanScore.keySet()) {
            row = sheet.getRow(k);
            if (row == null ) {
                k++;
                continue;
            }
            Cell cellName = row.createCell(14);
            Cell cellScore = row.createCell(15);
            cellName.setCellValue(key);
            cellScore.setCellValue(sportsmanScore.get(key));
            k++;

        }


    }
}
