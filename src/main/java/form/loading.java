package form;

import form.core.Load;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;


public class loading extends JDialog {
    private JPanel contentPane;
    public JProgressBar progressBar;
    private static final Logger LOG = LoggerFactory.getLogger(Load.class);

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

        try {
            ConnectionForm dialogFirst = new ConnectionForm("HUMBot");
            dialogFirst.pack();
            dialogFirst.setLocationRelativeTo(null);
            dialogFirst.setVisible(true);
        }catch (Exception ex){
            LOG.error(ex.toString());
        }

        threadLoadingForm.interrupt();

    }

}
