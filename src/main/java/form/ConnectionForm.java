package form;

import connect.Connect;
import form.core.Load;

import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.awt.event.*;
import java.io.IOException;
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
    private ExecutorService exec = Executors.newCachedThreadPool();
    private static HashMap<String, Boolean> activeTread = new HashMap<String, Boolean>();
    private HashMap<String, String> mapMenu;
    private ArrayList<String> listOfActives = new ArrayList<String>();

    public ConnectionForm(String title){
        super(title);

        setContentPane(contentPane);
        getRootPane().setDefaultButton(buttonStart);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final int SINGLE_SELECTION;
        Load connection = new Load();
        mapMenu = connection.loadMenu();
        listSportSelect.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listSportSelect.setListData(mapMenu.keySet().toArray(new String[0]));
        this.setTitle("HUMBot");


        ArrayList<String> selectedSport = new ArrayList<String>();
        ArrayList<String> selectedMatches = new ArrayList<String>();



        buttonStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                onStart(connection);

            }

        });
        buttonStop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onStop();
            }
        });

        sportSelectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedMatches.clear();
                Runnable run = ()->{
                    progressBar.setIndeterminate(true);
                    sportSelectButton.setEnabled(false);
                    buttonStart.setEnabled(false);

                    for (String key:listSportSelect.getSelectedValuesList()) {
                        selectedSport.add(key);
                        HashMap<String, String> mapTour = connection.loadTournaments(mapMenu.get(key));
                        for (String keyTwo:mapTour.keySet()){
                            selectedMatches.add(keyTwo);
                        }
                    }
                    listMatchSelect.setListData(selectedMatches.toArray(new String[0]));

                    sportSelectButton.setEnabled(true);
                    buttonStart.setEnabled(true);
                    progressBar.setIndeterminate(false);

                };

                exec.execute(run);

            }
        });
        
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

    private void onStart(Load connection) {
        Runnable run = () -> {
            progressBar.setIndeterminate(true);
            buttonStart.setEnabled(false);
            sportSelectButton.setEnabled(false);

            for (String keySport : listSportSelect.getSelectedValuesList()) {
                HashMap<String, String> mapTourMat = connection.loadTournaments(mapMenu.get(keySport));
                for (String keyMatch : listMatchSelect.getSelectedValuesList()) {
                    activeTread.put(keyMatch, true);
                    listOfActives.add(keyMatch);
                    exec.execute(new Connect(mapTourMat.get(keyMatch), keyMatch, mapMenu.get(keySport)));
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
        for (Object keySport:listActiveMatches.getSelectedValuesList()){
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

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
