package core;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.biff.EmptyCell;
import jxl.format.Format;
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
                            new File(filePath)));
            var sheets = xlsFile.getSheets();
            if(sheets.length <1) return;

            var excelSheet = sheets[0];

            createData(excelSheet, newRow);

            xlsFile.write();
            xlsFile.close();


        } catch (WriteException | BiffException e) {
            e.printStackTrace();
        }
    }

    /**
     * Метод создает пустой файл для записи. Вызывать, если !Files.exists вернул false
     * @param p путь к файлу
     * @throws IOException
     */
    private void createFile(Path p) throws IOException, WriteException {
        WritableWorkbook xlsFile = Workbook.createWorkbook(new File(p.toString()));
        WritableSheet excelSheet = xlsFile.createSheet("Tournament", 0);

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
//        if((parsScore[0].equals("0") && parsScore[parsScore.length / 2].equals("0")) ||
//                (Integer.parseInt(parsScore[0])+ Integer.parseInt(parsScore[parsScore.length / 2]) != parsScore.length / 2 - 1)){
//            return null;
//        }

        return new String[][]{
                Arrays.copyOf(parsScore, parsScore.length / 2),
                Arrays.copyOfRange(parsScore, parsScore.length / 2, parsScore.length)};

    }



    /**
     * Метод генерирует строки xls файла
     //* @param sheet xls страница
     * @param newRow хэшмап с данными
     */
    public void createData(WritableSheet sheet, HashMap<String, String> newRow) throws WriteException {
        int rowCount = sheet.getRows();


        for (String key: newRow.keySet()) {
            String[] names= parsName(key);
            String[][] scores = parsScore(newRow.get(key));

            if(names.length !=2 || scores == null)
                continue;


            for(int i =0; i<names.length; i++){
                Label name = new Label(0, rowCount+=1, names[i]);
                sheet.addCell(name);

                for (int cellIndex = 1, j=0; j < scores[i].length; cellIndex++, j++) {
                    Label score = new Label(cellIndex, rowCount, scores[i][j]);
                    sheet.addCell(score);
                }

                if(i==0){
                    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Moscow"));
                    Date date = calendar.getTime();

                    Label score = new Label(9, rowCount, date.toString());
                    sheet.addCell(score);
                }

            }
        }
    }

    /**
     * Метод получает название нужного файла из conectionform и открывает его.
     * После вызывает метод обработки файла.
     * @param fileName
     * @throws IOException
     */
    public void readXls (String fileName) throws IOException{
        Path p = Paths.get(fileName);
        String filePath = p.toString();


        try {

            WritableWorkbook xlsFile = Workbook.createWorkbook(
                    new File(p.toString()), Workbook.getWorkbook(
                            new File(filePath)));
            var sheets = xlsFile.getSheets();
            if(sheets.length <1) return;

            var excelSheet = sheets[0];

            writeToXls(excelSheet, xlsFile);

            xlsFile.write();
            xlsFile.close();


        } catch (WriteException | BiffException e) {
            e.printStackTrace();
        }

    }


    /**
     * Метод парсит эксельник, отображая справа от полей всех игроков с финальным счётом.
     * @param excelSheet
     * @param xlsFile
     */
    public void writeToXls(WritableSheet excelSheet, WritableWorkbook xlsFile){
        HashMap<String, Integer> sportsmanScore = new HashMap<>();
        Sheet sheet = xlsFile.getSheet(0);
        int numberOfRows = sheet.getRows();


        for (int i=0; i<=numberOfRows; i++){

            if (sheet.getCell(0,i).getClass() == EmptyCell.class) continue;

            Cell sportsmanName = sheet.getCell(0,i);

            if (sheet.getCell(1,i).getClass() == EmptyCell.class) continue;

            Cell sportsmanResult = sheet.getCell(1, i);
            String content = sportsmanName.getContents();


            if (sportsmanScore.containsKey(sportsmanName.getContents())){

                int buff = sportsmanScore.get(content);
                sportsmanScore.put(sportsmanName.getContents(), buff + Integer.valueOf(sportsmanResult.getContents()));

            }

            else {
                sportsmanScore.put(sportsmanName.getContents(), Integer.valueOf(sportsmanResult.getContents()));
            }
        }

        int i=0;
        for (String key: sportsmanScore.keySet()) {

            Label sportsmanName = new Label(15,i,"" + key);
            Label sportsmanResult = new Label(16,i,"" + sportsmanScore.get(key));


            try {

                excelSheet.addCell(sportsmanName);
                excelSheet.addCell(sportsmanResult);

            } catch (WriteException e) {
                e.printStackTrace();
            }

            i++;
        }

    }

}

