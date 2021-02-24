package form.core;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;

public class Load {
    private static String url = "https://betwin52436.site/";


    /**
     * Возвращает HashMap с меню видов спорта
     * Ключ- название значение- ссылка
     * @return
     * @throws IOException
     */
    public HashMap<String, String> loadMenu() {
        loadUrl();

        HashMap<String, String> menu = new HashMap<>();

        Document doc = null;
        try {
            doc = Jsoup.connect(url + "ru/live/").data("query", "Java")
                    .timeout(10000).userAgent("Mozilla").get();

            Element sportMenu = doc.getElementsByClass("sport_menu").first();
            Elements sportsItems = sportMenu.getElementsByClass("link");

            for (Element sport : sportsItems) {
                String href = sport.attr("href");

                String name = sport.child(1).text();
                menu.put(name, href);
            }

            return menu;

        } catch (IOException e) {
            System.out.println(e.toString());
        }

        return null;
    }

    /**
     * Обновляет урл сйта. Вызывается автоматически при ошибке подключения
     */
    public static void loadUrl()  {
        try {
            String url = "https://kbepha.top/s/15ns?s1=cbb&amp;p=%2Fuser%2Fregistration%2F&amp;fp=";

            URL obj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
            connection.setRequestMethod("GET");


            connection.getInputStream();

            String s = connection.getURL().toString();

            connection.disconnect();

            String[] ss = s.split("\\?");

            Load.url = ss[0];

        }
        catch (ProtocolException ex){
            System.out.println(ex.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * метод возвращает список турниров для выбранного вида спорта
     * @param sport url(value) из HasMap menu
     * @return HasMap с ключем-названи значением- url
     * @throws IOException
     */
    public HashMap<String,String> loadTournaments(String sport) {
        loadUrl();

        HashMap<String, String> tournaments = new HashMap<>();
            
        Document doc = null;

        try {
            doc = Jsoup.connect(url + "ru/" + sport).data("query", "Java")
                    .timeout(10000).userAgent("Mozilla").get();


            Elements ligaMenu = doc.getElementsByClass("imp");

            for (Element tournament : ligaMenu) {
                String hrefTournament = tournament.attr("href");
                String nameTournament = tournament.text();

                tournaments.put(nameTournament, hrefTournament);
            }

            return tournaments;

        } catch (IOException e) {
            System.out.println(e.toString());
        }

        return null;
    }

    public static String getUrl() {
        return url;
    }
}
