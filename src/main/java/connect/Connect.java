package connect;

import form.ConnectionForm;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;

public class Connect implements Runnable{
    private static String url = "https://betwin52436.site/";

    public void printMatch() throws IOException {

        Document doc = Jsoup.connect(url+"ru/live/Table-Tennis/").data("query", "Java")
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

    public static HashMap<String, String> getMenu() throws IOException {
        HashMap<String, String> menu = new HashMap<>();

        Document doc = Jsoup.connect(url+"ru/live/").data("query", "Java")
                .timeout(10000).userAgent("Mozilla").get();

        Element sportMenu = doc.getElementsByClass("sport_menu").first();
        Elements sportsItems = sportMenu.getElementsByClass("link");

        for (Element sport:sportsItems) {
            String href = sport.attr("href");

            String name = sport.child(1).text();
            menu.put(name,href);
        }

        return menu;
    }

    public String LoadUrl()  {
        try {
            String url = "https://kbepha.top/s/15ns?s1=cbb&amp;p=%2Fuser%2Fregistration%2F&amp;fp=";

            URL obj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
            connection.setRequestMethod("GET");


            connection.getInputStream();

            String s = connection.getURL().toString();
            connection.disconnect();

            String[] ss = s.split("\\?");

            return  ss[0];
        }
        catch (ProtocolException ex){
            System.out.println(ex.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return  null;

    }

    @Override
    public void run() {
        while (ConnectionForm.isConnect()) {
            try {
                printMatch();
            } catch (IOException e) {
                url = LoadUrl();
            }
        }
    }
}
