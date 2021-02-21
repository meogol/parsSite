package form;

import connect.Connect;

import javax.swing.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConnectionForm extends JDialog {
    private JPanel contentPane;
    private JButton buttonStart;
    private JButton buttonStop;
    private JCheckBox CheckBoxTT;
    private JCheckBox CheckBoxKap;
    private JComboBox comboBoxSelectTournament;
    private static boolean connect = true;
    ExecutorService exec = Executors.newCachedThreadPool();

    public ConnectionForm() throws IOException {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonStart);

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


    }

    public static boolean isConnect() {
        return connect;
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
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }


    private void UIComponents() {
        // TODO: place custom component creation code here
    }
}
