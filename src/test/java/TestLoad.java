import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;

public class TestLoad {
    public static void main(String[] args) {
        HashMap<String, String> menu = new HashMap<>();

        Document doc = null;
        try {
            doc = Jsoup.connect("https://betwin45717.site/ru/live/TwentyOne/2092323-21-Classics/287304291-Player-Dealer/").data("query", "Java")
                    .userAgent("Mozilla").get();
            doc=doc;

        } catch (IOException e) {

        }
    }
}
