package form;

import core.ParsMatches;
import core.load.Load;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
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
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        listSportSelect.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.setTitle("HUMBot");

        ArrayList<String> selectedSport = new ArrayList<>();
        ArrayList<String> selectedMatches = new ArrayList<>();
        mapTour = new HashMap<>();
        mapMenu = Load.loadMenu();

        listSportSelect.setListData(mapMenu.keySet().toArray(new String[0]));

        buttonStart.addActionListener(e -> onStart());
        buttonStop.addActionListener(e -> onStop());
        sportSelectButton.addActionListener(e -> onSelectSport(selectedSport, selectedMatches));

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
                    if (!activeTread.getOrDefault(keyMatch, false)) {
                        listOfActives.add(keyMatch);
                        activeTread.put(keyMatch,true);
                        exec.execute(new ParsMatches(mapTour.get(keyMatch), keyMatch, mapMenu.get(keySport)));
                    }
                    else {
                        continue;
                    }
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


    public static void main(String[] args) {
        ConnectionForm dialog = new ConnectionForm("HUMBot");
        dialog.setVisible(true);
        dialog.pack();

    }


}
