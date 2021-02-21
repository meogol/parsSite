package connect;

import form.ConnectionForm;
import form.core.Load;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class Connect implements Runnable{

    /**
     * Вывод результатов сбора инфы с сайта в кнсоль
     * @throws IOException
     */
    public void printMatch() throws IOException {

        Document doc = Jsoup.connect(Load.getUrl() +"ru/live/Table-Tennis/").data("query", "Java")
                .timeout(10000).userAgent("Mozilla").get();


        //https://kbepha.top/15ns?s1=cbb&p=/user/registration/

        Elements newsHeadlines = doc.getElementsByClass("sports_widget");

        for (Element elem : newsHeadlines) {
            Elements news = elem.getElementsByClass("c-events-scoreboard__item");

            for (int i = 0; i < news.size(); i++) {
                if (i % 2 == 0) {
                    Elements ne = news.get(i).getElementsByClass("n");
                    System.out.println(ne.text());

                    Elements ne1 = news.get(i).getElementsByClass("c-events-scoreboard__cell");
                    StringBuffer s = new StringBuffer(ne1.text());
                    s.replace(s.length() / 2, s.length() / 2, "\n");

                    System.out.println(s + "\n");
                }
            }
        }

    }

    @Override
    public void run() {
        while (ConnectionForm.isConnect()) {
            try {
                printMatch();
            } catch (IOException e) {
                Load.loadUrl();
            }
        }
    }
}
