import core.ParsMatches;
import form.ConnectionForm;

public class testWriteToXLS {
    public static void main(String[] args){
        //ExecutorService exec = Executors.newCachedThreadPool();

        ConnectionForm.setConnect(true);
        ParsMatches load = new ParsMatches();

        load.testLoad();
        //exec.execute();



    }
}
