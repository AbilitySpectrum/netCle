package lyricom.config3.calibration;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.util.concurrent.Semaphore;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import lyricom.config3.ui.MainFrame;
import lyricom.config3.ui.ScreenInfo;
import lyricom.config3.ui.Utils;

/**
 *
 * @author Andrew
 */
public class CalibrationUI extends JDialog {
    
    private JEditorPane messageBox;
    private Calibrator calibrator;
    private final Semaphore continueSemaphore;
    private boolean waitingOnSemaphore = false;
    
    public CalibrationUI() {
        super(MainFrame.getInstance(), false);
        continueSemaphore = new Semaphore(1);
        
        setLayout(new BorderLayout());
        add(titleLine(), BorderLayout.NORTH);
        
        messageBox = new JEditorPane();
        messageBox.setContentType("text/html");   
//        descriptionText = new JTextArea(1,80);
        messageBox.setBackground(getBackground());
        messageBox.setFont(Utils.STD_FONT);
        messageBox.setEditable(false);
        messageBox.setBorder(new EmptyBorder(20,20,20,20));
        add(messageBox, BorderLayout.CENTER);
        
        add(buttons(), BorderLayout.SOUTH);
        
        pack();
        // Center on screen
        Dimension dim = new Dimension(500, 300);
        setMinimumSize(dim);
        Point center = ScreenInfo.getCenter();
        setLocation(center.x-dim.width/2, center.y-dim.height/2 - 100);
        
        setVisible(true);
    }
    
    void setCalibrator(Calibrator cal) {
        calibrator = cal;
    }
    
    private JPanel titleLine() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel t = new JLabel("Gyro Calibration");
        t.setFont(Utils.TITLE_FONT);
        p.add(t);
        return p;
    }
    
    private JPanel buttons() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
        p.add(continueBtn());
        p.add(cancelBtn());
        
        return p;        
    }
    
    private JButton continueBtn() {
        JButton b = new JButton("Continue");
        b.addActionListener((e) -> {
            continuePressed();
        });
        return b;
    }

    private JButton cancelBtn() {
        JButton b = new JButton("Cancel");
        b.addActionListener((e) -> {
            calibrator.cancel();
            if (waitingOnSemaphore)
                continueSemaphore.release();
//            closeWindow();
        });
        return b;
    }
    
    private void continuePressed() {
        if (waitingOnSemaphore)
            continueSemaphore.release();
    }
    
    private void closeWindow() {
        this.dispose();
    }

    // These routines are called by the calibration utility
    // which runs a non-swing thread.
    void presentMessage(String action) {
        SwingUtilities.invokeLater(() -> {
            messageBox.setText(action);
        });
    }
    
    void waitForContinue() {
        try {
            continueSemaphore.acquire();
            waitingOnSemaphore = true;
            continueSemaphore.acquire();  // blocks until user selects an answer.
            continueSemaphore.release();
            return;
        } catch(InterruptedException ex) {
            return;
        }
        
    }
    
    void closeDialog() {
        SwingUtilities.invokeLater(() -> {
            closeWindow();
        });
    }
    
}
