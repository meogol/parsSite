package form;

import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;


public class loading extends JDialog {
    private JPanel contentPane;
    public JProgressBar progressBar;

    public loading() {
        setContentPane(contentPane);
        setModal(true);
        progressBar.setIndeterminate(true);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setUndecorated(true);
        setLocationRelativeTo(null);
    }


    public static void main(String[] args) {
        Runnable runLoadingForm = () ->
        {
            loading dialog = new loading();
            dialog.pack();
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);

            while(!Thread.interrupted())  // Clears interrupted status!
                try {
                    throw new InterruptedException();
                } catch (InterruptedException e) {
                    dialog.dispose();
                    Thread.currentThread().interrupt();
                }
        };

        Thread threadLoadingForm = new Thread(runLoadingForm);
        threadLoadingForm.start();

        ConnectionForm dialogFirst = new ConnectionForm("HUMBot");

        threadLoadingForm.interrupt();

        dialogFirst.pack();
        dialogFirst.setLocationRelativeTo(null);
        dialogFirst.setVisible(true);




       // System.exit(0);

    }

}
