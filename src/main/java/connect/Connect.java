package connect;

import form.ConnectionForm;
import form.core.Load;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

public class Connect implements Runnable{
    String connectUrl="/live/Table-Tennis/1197285-TT-Cup/";
    String hashKey="";
    HashMap<String, String> matches;
    Boolean isAllWrits = true;

    public Connect(){
        Load.loadUrl();
        try {
            matches  =  getMatches();
        } catch (IOException e) {
            Load.loadUrl();
        }
    }

    public Connect(String fullUrl, String hashKey)  {
        Load.loadUrl();
        this.connectUrl = fullUrl;
        this.hashKey = hashKey;

        try {
            matches  =  getMatches();
        } catch (IOException e) {
            Load.loadUrl();
        }
    }


    /**
     * Создание хешмапа с инфой об активных матчах
     * @throws IOException
     */
    public HashMap<String, String> getMatches() throws IOException {
        Load.loadUrl();

        HashMap<String, String> thisMatches = new HashMap<>();

        Document doc = Jsoup.connect(Load.getUrl() +"ru/"+connectUrl).data("query", "Java")
                .timeout(10000).userAgent("Mozilla").get();

        Elements newsHeadlines = doc.getElementsByClass("sports_widget");

        for (Element elem : newsHeadlines) {
            Elements news = elem.getElementsByClass("c-events-scoreboard__item");

            for (int i = 0; i < news.size(); i++) {
                if (i % 2 == 0) {
                    Elements sportsmenNames = news.get(i).getElementsByClass("n");

                    Elements score = news.get(i).getElementsByClass("c-events-scoreboard__cell");
                    String item = score.text();
                    thisMatches.put(sportsmenNames.text(), item);

                }
            }
        }

        return thisMatches;
    }

    public void writeToXLS(HashMap<String, String> newRow) throws IOException {

        Path p = Paths.get(connectUrl.split("/")[2].split("-",2)[1]+".xls");
        String fileName = p.toString();

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

            for (String key: newRow.keySet()) {
                Row row = sheet.createRow(rowCount+=1);
                row = sheet.createRow(rowCount+=1);

                String score = newRow.get(key);

                String[] parsScore = score.split(" ");

                String[] parsKey = key.trim().split("\\) ");


                Cell cellName = row.createCell(0);
                cellName.setCellValue(parsKey[0]+")");

                sheet.autoSizeColumn(0);

                for (int cellIndex = 1, i=0; i < parsScore.length/2; cellIndex++, i++) {

                    Cell cell = row.createCell(cellIndex);
                    cell.setCellValue(parsScore[i]);
                }

                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Moscow"));
                Date date = calendar.getTime();

                Cell cell = row.createCell(7);
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

            try (BufferedOutputStream fio = new BufferedOutputStream(new FileOutputStream(fileName))) {
                workbook.write(fio);
            }
        }
    }

    public void checkMatches() throws URISyntaxException, IOException {
        HashMap<String, String> thisMatches = getMatches();
        HashMap<String, String> writeMatches = new HashMap<>();

        for (String key :matches.keySet()) {
            if (!thisMatches.containsKey(key)){
                writeMatches.put(key, matches.get(key));
            }
        }

        try {
            if (writeMatches.size() != 0) {
                writeToXLS(writeMatches);
            }
            matches = thisMatches;

        }catch (IOException ex)
        {
            matches = thisMatches;

            for (String key: writeMatches.keySet()) {
                matches.put(key, writeMatches.get(key));
            }
        }
    }

    @Override
    public void run() {
        while ((ConnectionForm.getActiveTread().get(hashKey)) || !isAllWrits) {
            try {
                checkMatches();

            } catch (IOException ex) {
                Load.loadUrl();
            }
            catch ( URISyntaxException e){
                System.out.println(e);
            }
        }
    }

    public void testLoad(){
        while (ConnectionForm.isConnect() || !isAllWrits) {
            try {
                checkMatches();
            } catch (IOException ex) {
                Load.loadUrl();
            }
            catch ( URISyntaxException e){
                System.out.println(e);
            }
        }
    }

}
