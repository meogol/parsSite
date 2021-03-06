package core;

import core.data.Winrate;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.biff.EmptyCell;
import jxl.CellView;

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
    public void writeToXLS(HashMap<String, String> newRow, String fileName) throws IOException, WriteException, BiffException {

        Path p = Paths.get(fileName+".xls");
        String filePath = p.toString();

        WritableWorkbook xlsFile = null;
        try {
            if (!Files.exists(p)) {
                createFile(p);
            }

            xlsFile = Workbook.createWorkbook(
                    new File(p.toString()), Workbook.getWorkbook(
                            new File(filePath)));
            var sheets = xlsFile.getSheets();
            if(sheets.length <1) return;

            var excelSheet = sheets[0];

            createData(excelSheet, newRow);

            xlsFile.write();

        }
        catch (NullPointerException ex){

            createFile(p);
            throw ex;
        }
        finally {
                xlsFile.close();
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

        //У счета фиксированная длинна
        for (int c = 1; c < 9; c++) {
            CellView cell = excelSheet.getColumnView(c);
            cell.setSize(800);
            excelSheet.setColumnView(c, cell);
        }

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

        if(parsKey.length < 2) return null;

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
     * @param sheet страница xls файла
     * @param newRow хэшмап с данными
     */
    public void createData(WritableSheet sheet, HashMap<String, String> newRow) throws WriteException {
        int rowCount = sheet.getRows();

        for (String key: newRow.keySet()) {
            String[] names= parsName(key);
            String[][] scores = parsScore(newRow.get(key));

            if(names == null || scores == null)
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

        //Для имени и даты ставим автосайз
        CellView cellName = sheet.getColumnView(0);
        cellName.setAutosize(true);
        sheet.setColumnView(0, cellName);

        CellView cellDate = sheet.getColumnView(sheet.getColumns());
        cellDate.setAutosize(true);
        sheet.setColumnView(sheet.getColumns()-1, cellDate);
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
        HashMap<String, Winrate> sportsmanScore = new HashMap<>();
        Sheet sheet = xlsFile.getSheet(0);
        int numberOfRows = sheet.getRows();

        for (int i=0; i<=numberOfRows; i++){

            if (sheet.getCell(0,i).getClass() == EmptyCell.class) continue;

            Cell sportsmanNameOne = sheet.getCell(0,i);
            Cell sportsmanNameTwo = sheet.getCell(0,i+1);

            if (sheet.getCell(1,i).getClass() == EmptyCell.class) continue;

            Cell sportsmanResultOne = sheet.getCell(1, i);
            Cell sportsmanResultTwo = sheet.getCell(1, i+1);


            if (!sportsmanScore.containsKey(sportsmanNameOne.getContents())){
                sportsmanScore.put(sportsmanNameOne.getContents(), new Winrate());
            }
            if (!sportsmanScore.containsKey(sportsmanNameTwo.getContents())){
                sportsmanScore.put(sportsmanNameTwo.getContents(), new Winrate());
            }

            int scoreOne = Integer.parseInt(sportsmanResultOne.getContents());
            int scoreTwo = Integer.parseInt(sportsmanResultTwo.getContents());

            if(scoreOne>scoreTwo) {
                sportsmanScore.get(sportsmanNameOne.getContents()).addWin(scoreOne);
                sportsmanScore.get(sportsmanNameTwo.getContents()).addLouse(scoreTwo);
            }else{
                sportsmanScore.get(sportsmanNameTwo.getContents()).addWin(scoreOne);
                sportsmanScore.get(sportsmanNameOne.getContents()).addLouse(scoreTwo);
            }


            i+=1;
        }

        int i=0;
        for (String key: sportsmanScore.keySet()) {

            Label sportsmanName = new Label(15,i,"" + key);
            Label sportsmanResultWin = new Label(16,i,"" + sportsmanScore.get(key).getWin());
            Label sportsmanResultLouse = new Label(17,i,"" + sportsmanScore.get(key).getLouse());


            try {

                excelSheet.addCell(sportsmanName);
                excelSheet.addCell(sportsmanResultWin);
                excelSheet.addCell(sportsmanResultLouse);

            } catch (WriteException e) {
                e.printStackTrace();
            }

            i++;
        }

        CellView cellName = sheet.getColumnView(15);
        cellName.setAutosize(true);
        excelSheet.setColumnView(15, cellName);

    }

}

