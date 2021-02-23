import connect.Connect;
import form.ConnectionForm;
import form.core.Load;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class testWriteToXLS {
    public static void main(String[] args){
        //ExecutorService exec = Executors.newCachedThreadPool();

        ConnectionForm.setConnect(true);
        Connect load = new Connect();

        load.testLoad();
        //exec.execute();



    }
}
