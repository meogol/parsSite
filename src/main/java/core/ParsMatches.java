package core;

import form.ConnectionForm;
import core.load.Load;
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
        Load.loadUrl();
        try {
            matches  = Load.loadMatches(connectUrl);
        } catch (IOException e) {
            Load.loadUrl();

            LOG.info("thread "+hashKey+" "+e.toString());
        }
    }

    public ParsMatches(String fullUrl, String hashKey, String sportKey)  {
        Load.loadUrl();


        this.connectUrl = fullUrl;
        this.hashKey = hashKey;
        this.sportKey = sportKey;

        try {
            matches  = Load.loadMatches(connectUrl);

        } catch (IOException e) {
            Load.loadUrl();

            LOG.info("thread "+hashKey+" "+e.toString());

        }
    }

    public void checkMatches() throws URISyntaxException {
        HashMap<String, String> thisMatches;
        HashMap<String, String> writeMatches = new HashMap<>();

        try {
            thisMatches = Load.loadMatches(connectUrl);
        } catch (IOException e) {
            Load.loadUrl();
            LOG.info("thread "+hashKey+" "+e.toString());

            return;
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
            saveDontWriteMatches(thisMatches,writeMatches);
        }
    }

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
            }
        }
    }

    public void testLoad(){
        while (ConnectionForm.isConnect() || !isAllWrits) {
            try {
                checkMatches();
            } catch (URISyntaxException e) {
                LOG.error("thread "+hashKey+" "+e.toString());
            }
        }
    }

}
