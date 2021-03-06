import core.Write;
import jxl.read.biff.BiffException;
import jxl.write.WriteException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class TestWrite {
    public static void main(String[] args) throws IOException, InterruptedException, BiffException, WriteException {
        HashMap<String, String> h = new HashMap<>();
        Random random = new Random();

        while (true) {
            for (int i = 0; i < 1; i++) {
                String score = String.valueOf(random.nextInt(10))+" " +
                        String.valueOf(random.nextInt(20))+" "  +
                        String.valueOf(random.nextInt(20))+" "  +
                        String.valueOf(random.nextInt(10))+" "  +
                        String.valueOf(random.nextInt(10))+" "  +
                        String.valueOf(random.nextInt(10));

                String name = String.valueOf(random.nextInt())+"(s) "+String.valueOf(random.nextInt())+"(w)";
                h.put(name, score);
            }

            Write w = new Write();
            w.writeToXLS(h, "test");
            h.clear();
            TimeUnit.SECONDS.sleep(1);
        }

    }
}
