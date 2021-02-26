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
        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }


    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        Runnable runLoadingForm = () ->
        {
            loading dialog = new loading();
            dialog.pack();
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
        dialogFirst.setVisible(true);




       // System.exit(0);

    }
}
