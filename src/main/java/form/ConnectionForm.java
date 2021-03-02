package form;

import core.ParsMatches;
import core.load.Load;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConnectionForm extends JFrame {
    private JPanel contentPane;
    private JButton buttonStart;
    private JButton buttonStop;
    private JList<String> listSportSelect;
    private JList<String> listMatchSelect;
    private JButton sportSelectButton;
    private JList listActiveMatches;
    private JPanel ContentPanel;
    private JProgressBar progressBar;
    private JScrollPane HUMBot;
    private static boolean connect = true;
    private final ExecutorService exec = Executors.newCachedThreadPool();
    private static HashMap<String, Boolean> activeTread = new HashMap<>();
    private HashMap<String, String> mapMenu;
    private ArrayList<String> listOfActives = new ArrayList<>();
    private HashMap<String, String> mapTour;
    public ConnectionForm(String title){
        super(title);

        setContentPane(contentPane);
        getRootPane().setDefaultButton(buttonStart);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        listSportSelect.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.setTitle("HUMBot");
        readUrlToProp();

        ArrayList<String> selectedSport = new ArrayList<>();
        ArrayList<String> selectedMatches = new ArrayList<>();
        mapTour = new HashMap<>();
        mapMenu = Load.loadMenu();

        listSportSelect.setListData(mapMenu.keySet().toArray(new String[0]));

        buttonStart.addActionListener(e -> onStart());
        buttonStop.addActionListener(e -> onStop());
        sportSelectButton.addActionListener(e -> onSelectSport(selectedSport, selectedMatches));

        addSavingUrlToExit();

    }
    private void addSavingUrlToExit(){
        this.addWindowListener(new WindowListener(){

                                   @Override
                                   public void windowOpened(WindowEvent e) {

                                   }

                                   @Override
                                   public void windowClosing(WindowEvent e) {
                                       writeUrlToProp();
                                       e.getWindow().setVisible(false);
                                       System.exit(0);
                                   }

                                   @Override
                                   public void windowClosed(WindowEvent e) {

                                   }

                                   @Override
                                   public void windowIconified(WindowEvent e) {

                                   }

                                   @Override
                                   public void windowDeiconified(WindowEvent e) {

                                   }

                                   @Override
                                   public void windowActivated(WindowEvent e) {

                                   }

                                   @Override
                                   public void windowDeactivated(WindowEvent e) {

                                   }
                               }

        );
    }
    public static boolean isConnect() {
        return connect;
    }

    public static void setConnect(boolean connect) {
        ConnectionForm.connect = connect;
    }

    public static HashMap<String, Boolean> getActiveTread() {
        return activeTread;
    }

    private void onSelectSport(ArrayList<String> selectedSport, ArrayList<String> selectedMatches){
        selectedMatches.clear();
        Runnable run = () -> {
            progressBar.setIndeterminate(true);
            sportSelectButton.setEnabled(false);
            buttonStart.setEnabled(false);
            mapTour.clear();

            for (String key : listSportSelect.getSelectedValuesList()) {
                selectedSport.add(key);
                mapTour = Load.loadTournaments(mapMenu.get(key));
                selectedMatches.addAll(mapTour.keySet());
            }

            listMatchSelect.setListData(selectedMatches.toArray(new String[0]));

            sportSelectButton.setEnabled(true);
            buttonStart.setEnabled(true);
            progressBar.setIndeterminate(false);

        };

        exec.execute(run);
    }

    private void onStart() {
        Runnable run = () -> {
            progressBar.setIndeterminate(true);
            buttonStart.setEnabled(false);
            sportSelectButton.setEnabled(false);

            for (String keySport : listSportSelect.getSelectedValuesList()) {
                for (String keyMatch : listMatchSelect.getSelectedValuesList()) {
                    if (activeTread.getOrDefault(keyMatch, false))
                        continue;

                    listOfActives.add(keyMatch);
                    activeTread.put(keyMatch,true);
                    exec.execute(new ParsMatches(mapTour.get(keyMatch), keyMatch, mapMenu.get(keySport)));
                }
            }
            listActiveMatches.setListData(listOfActives.toArray(new String[0]));

            buttonStart.setEnabled(true);
            sportSelectButton.setEnabled(true);
            progressBar.setIndeterminate(false);
        };

        exec.execute(run);

    }

    private void onStop() {
        int index = 0;

        for (Object keySport : listActiveMatches.getSelectedValuesList()) {
            activeTread.put((String) keySport, false);
            index = listActiveMatches.getSelectedIndex();
            listOfActives.remove(index);
        }
        listActiveMatches.setListData(listOfActives.toArray(new String[0]));
    }

    private void readUrlToProp(){
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream("res.properties"));
            Load.setUrl(prop.getProperty("connect_url"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeUrlToProp(){
        Properties prop = new Properties();
        try {
            FileOutputStream output = new FileOutputStream("res.properties");
            prop.setProperty("connect_url", Load.getUrl());
            prop.store(output, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ConnectionForm dialog = new ConnectionForm("HUMBot");
        dialog.setVisible(true);
        dialog.pack();

    }


}
