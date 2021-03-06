package core;

import form.ConnectionForm;
import core.load.Load;
import jxl.read.biff.BiffException;
import jxl.write.WriteException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;

public class ParsMatches implements Runnable{
    String connectUrl="/live/Table-Tennis/1197285-TT-Cup/";
    String hashKey="";
    String sportKey = "";
    HashMap<String, String> matches;
    Boolean isAllWrits = true;

    private static final Logger LOG = LoggerFactory.getLogger(ParsMatches.class);

    public ParsMatches(){

        try {
            matches  = Load.loadMatches(connectUrl);
        } catch (IOException e) {
            Load.loadUrl();
            matches = new HashMap<>();
            LOG.info("thread "+hashKey+" "+e.toString());
        }
    }


    public ParsMatches(String fullUrl, String hashKey, String sportKey)  {
        this.connectUrl = fullUrl;
        this.hashKey = hashKey;
        this.sportKey = sportKey;

        try {
            matches  = Load.loadMatches(connectUrl);

        } catch (IOException e) {
            Load.loadUrl();
            matches = new HashMap<>();
            LOG.info("thread "+hashKey+" "+e.toString());

        }
    }

    /**
     * метод получает с сайта матчи и пишет в файл завершенные
     * @throws URISyntaxException
     */
    public void checkMatches() throws URISyntaxException, IOException {
        HashMap<String, String> thisMatches;
        HashMap<String, String> writeMatches = new HashMap<>();

        try {
            thisMatches = Load.loadMatches(connectUrl);
        } catch (IOException e) {
            LOG.info("thread "+hashKey+" "+e.toString());

            throw e;
        }


        for (String key :matches.keySet()) {
            if (!thisMatches.containsKey(key)){
                writeMatches.put(key, matches.get(key));
            }
        }

        try {
            if (writeMatches.size() != 0) {
                String fileName = connectUrl.split("/")[2].split("-",2)[1];

                Write write = new Write();
                write.writeToXLS(writeMatches, fileName);
            }
            matches = thisMatches;
            isAllWrits = true;

        }catch (IOException ex)
        {
            LOG.info("Задержка записи: "+ex.toString());
            saveDontWriteMatches(thisMatches,writeMatches);
        } catch (BiffException | WriteException e) {
            LOG.info(e.toString());
            saveDontWriteMatches(thisMatches,writeMatches);

        }catch (Exception ex) {
            LOG.info(ex.toString());
            saveDontWriteMatches(thisMatches,writeMatches);
        }
    }

    /**
     * метод добавляет в ассив активных
     * матчей незаписанные по какой-либо причине матчи
     * @param thisMatches список матчей, полученных с сайта
     * @param writeMatches список незаписанных матчей
     */
    private void saveDontWriteMatches(HashMap<String, String> thisMatches, HashMap<String, String> writeMatches){
        matches = thisMatches;
        isAllWrits = false;
        for (String key: writeMatches.keySet()) {
            matches.put(key, writeMatches.get(key));
        }
    }

    @Override
    public void run() {
        while ((ConnectionForm.getActiveTread().get(hashKey)) || !isAllWrits) {
            try {
                checkMatches();
            }
            catch ( URISyntaxException e){
                LOG.error("thread "+hashKey+" "+e.toString());
            } catch (IOException e) {
                LOG.error("thread "+hashKey+" "+e.toString());
                Load.loadUrl();
            }
        }
    }

}
