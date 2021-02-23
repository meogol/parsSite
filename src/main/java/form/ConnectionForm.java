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
    private JList list1;
    private static boolean connect = true;
    ExecutorService exec = Executors.newCachedThreadPool();

    public ConnectionForm(){
        setContentPane(contentPane);
        getRootPane().setDefaultButton(buttonStart);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Load connection = new Load();
        HashMap<String, String> mapMenu = connection.loadMenu();

        listSportSelect.setListData(mapMenu.keySet().toArray(new String[0]));

        buttonStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onStart();
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
                ArrayList<String> selectedSport = new ArrayList<String>();
                ArrayList<String> selectedMatches = new ArrayList<String>();
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

    private void onStart() {
        connect = true;
        buttonStart.setEnabled(false);

        exec.execute(new Connect());
    }

    private void onStop() {
        connect = false;
        buttonStart.setEnabled(true);

    }


    public static void main(String[] args) throws IOException {
        ConnectionForm dialog = new ConnectionForm();
        dialog.setVisible(true);
        dialog.pack();

    }

}
