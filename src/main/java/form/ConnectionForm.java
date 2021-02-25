package form;

import connect.Connect;
import form.core.Load;

import javax.swing.*;
import java.awt.*;
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
    private static boolean connect = true;
    private ExecutorService exec = Executors.newCachedThreadPool();
    private static HashMap<String, Boolean> activeTread = new HashMap<String, Boolean>();
    private HashMap<String, String> mapMenu;
    ArrayList<String> listOfActives = new ArrayList<String>();

    public ConnectionForm(){
        setContentPane(contentPane);
        getRootPane().setDefaultButton(buttonStart);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final int SINGLE_SELECTION;
        Load connection = new Load();
        mapMenu = connection.loadMenu();
        listSportSelect.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listSportSelect.setListData(mapMenu.keySet().toArray(new String[0]));

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
                for (String key:listSportSelect.getSelectedValuesList()) {
                    selectedSport.add(key);
                    HashMap<String, String> mapTour = connection.loadTournaments(mapMenu.get(key));
                    for (String keyTwo:mapTour.keySet()){
                        selectedMatches.add(keyTwo);
                    }
                }
                listMatchSelect.setListData(selectedMatches.toArray(new String[0]));

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
        connect = true;
        for (String keySport:listSportSelect.getSelectedValuesList()) {
            HashMap<String, String> mapTourMat = connection.loadTournaments(mapMenu.get(keySport));
            for (String keyMatch:listMatchSelect.getSelectedValuesList()) {
                activeTread.put(keyMatch, true);
                listOfActives.add(keyMatch);
                exec.execute(new Connect(mapTourMat.get(keyMatch), keyMatch));
            }
        }
        listActiveMatches.setListData(listOfActives.toArray(new String[0]));



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


    public static void main(String[] args) throws IOException {
        ConnectionForm dialog = new ConnectionForm();
        dialog.setVisible(true);
        dialog.pack();

    }

}
