package connect;

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
import java.util.HashMap;

public class Connect implements Runnable{
    String connectUrl="/live/Table-Tennis/";
    HashMap<String, String> matches = new HashMap<>();

    public Connect(){}
    public Connect(String connectUrl){
        this.connectUrl = connectUrl;
    }


    /**
     * Создание хешмапа с инфой об активных матчах
     * @throws IOException
     */
    public HashMap<String, String> getMatches() throws IOException {
        HashMap<String, String> thisMatches = new HashMap<>();

        Document doc = Jsoup.connect(Load.getUrl() +"ru"+connectUrl).data("query", "Java")
                .timeout(10000).userAgent("Mozilla").get();

        Elements newsHeadlines = doc.getElementsByClass("sports_widget");

        for (Element elem : newsHeadlines) {
            Elements news = elem.getElementsByClass("c-events-scoreboard__item");

            for (int i = 0; i < news.size(); i++) {
                if (i % 2 == 0) {
                    Elements sportsmenNames = news.get(i).getElementsByClass("n");


                    Elements score = news.get(i).getElementsByClass("c-events-scoreboard__cell");
                    String[] parsScore = {
                            score.text().substring(0, score.text().length()/2),
                            score.text().substring(score.text().length()/2),
                    };

                    System.out.println(sportsmenNames.text());
                    for (String scoreItem: parsScore) {
                        thisMatches.put(sportsmenNames.text(),scoreItem);
                    }

                }
            }
        }

        return thisMatches;
    }

    private static synchronized void writeToXLS(HashMap<String, String> newRow) throws IOException, URISyntaxException {
        Path p = Paths.get("gdfg.xls");
        String fileName = p.toString();

        if (!Files.exists( p)) {
            Files.createFile(p);
            try (BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(new File(fileName)))) {
                Workbook workbook = new HSSFWorkbook();
                workbook.createSheet("Ready");
                workbook.write(fos);
                workbook.close();
            }
        }

        try (BufferedInputStream fis = new BufferedInputStream(new FileInputStream(new File(fileName)))) {

            Workbook workbook = new HSSFWorkbook(fis);
            Sheet sheet = workbook.getSheetAt(0);
            int rowCount = sheet.getPhysicalNumberOfRows();

            for (String key: newRow.keySet()) {

                Row row = sheet.createRow(rowCount += 1);

       //         key
                int index = 0;
                for (int cellIndex = 0; cellIndex < newRow.size(); cellIndex++) {
                    Cell cell = row.createCell(cellIndex);
                  //  cell.setCellValue();
                }
            }

//            for (int cellIndex = 0; cellIndex < newRow.size(); cellIndex++) {
//                Cell cell = row.createCell(cellIndex);
//                cell.setCellValue(newRow.get(cellIndex));
//            }


            try (BufferedOutputStream fio = new BufferedOutputStream(new FileOutputStream(fileName))) {
                workbook.write(fio);
            }
        }
    }


    @Override
    public void run() {
       // while (ConnectionForm.isConnect()) {
            try {
                writeToXLS(getMatches());
            } catch (IOException | URISyntaxException e) {
                Load.loadUrl();
            }
        //}
    }
}
