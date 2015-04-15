/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rov.rasputin.Commander;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamImageTransformer;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.ds.ipcam.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import org.jdesktop.layout.GroupLayout;
import org.netbeans.lib.awtextra.AbsoluteConstraints;
import org.netbeans.lib.awtextra.AbsoluteLayout;

/**
 *
 * @author 15998
 */
public class Display extends javax.swing.JFrame {

    private Worker worker;
    private Properties settings;
    private Webcam olga, alexei, maria;
    private boolean isFullscreen = false;

    private void loadSettings() {
        Webcam.setDriver(new IpCamDriver());

        try {
            settings = new Properties();
            InputStream in = getClass().getResourceAsStream("settings.properties");
            if (in == null) {
                System.out.println("ERROR");
            }
            settings.load(in);
            in.close();
        } catch (IOException ex) {
            Logger.getLogger(Display.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void createAndStartWorker() {
        worker = new Worker(this, settings);
        worker.start();
    }

    private void createCam() {
        String user = settings.getProperty("camUser");
        String pass = settings.getProperty("camPass");
        IpCamAuth auth = new IpCamAuth(user, pass);

        IpCamDeviceRegistry.unregisterAll();
        String format = "http://%s/videostream.cgi?loginuse=%s&loginpas=%s";
        Dimension d = new Dimension(600, 400);
        try {
            IpCamDeviceRegistry.register("Olga", String.format(format, settings.getProperty("olgaAddr"), user, pass), IpCamMode.PUSH, auth).setResolution(d);
            IpCamDeviceRegistry.register("Alexei", String.format(format, settings.getProperty("alexeiAddr"), user, pass), IpCamMode.PUSH, auth).setResolution(d);
            IpCamDeviceRegistry.register("Maria", String.format(format, settings.getProperty("mariaAddr"), user, pass), IpCamMode.PUSH, auth).setResolution(d);
        } catch (MalformedURLException ex) {
            Logger.getLogger(Display.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void connectCam() {
        olga = Webcam.getWebcams().get(0);
        olga.setImageTransformer(new WebcamImageTransformer() {
            @Override
            public BufferedImage transform(BufferedImage image) {
                int w = image.getWidth();
                int h = image.getHeight();
                BufferedImage modified = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
                Graphics2D g2 = modified.createGraphics();
                g2.drawImage(image, w, h, -w, -h, null);
                g2.dispose();
                modified.flush();
                return modified;
            }
        });
        WebcamPanel subOlga = new WebcamPanel(olga);
        subOlga.setFPSDisplayed(true);
        subOlga.setDisplayDebugInfo(true);
        subOlga.setFPSLimit(60);
        olgaPanel.removeAll();
        olgaPanel.add(subOlga);
        olgaPanel.revalidate();
        olgaPanel.repaint();

        alexei = Webcam.getWebcams().get(1);
        WebcamPanel subAlexei = new WebcamPanel(alexei);
        subAlexei.setFPSDisplayed(true);
        subAlexei.setDisplayDebugInfo(true);
        subAlexei.setFPSLimit(60);
        alexeiPanel.removeAll();
        alexeiPanel.add(subAlexei);
        alexeiPanel.revalidate();
        alexeiPanel.repaint();

        maria = Webcam.getWebcams().get(2);
        WebcamPanel subMaria = new WebcamPanel(maria);
        subMaria.setFPSDisplayed(true);
        subMaria.setDisplayDebugInfo(true);
        subMaria.setFPSLimit(60);
        mariaPanel.removeAll();
        mariaPanel.add(subMaria);
        mariaPanel.revalidate();
        mariaPanel.repaint();

        pack();
    }

    private void disconnectCam() {
        olga.close();
        alexei.close();
        maria.close();
    }

    /**
     * Creates new form Interface
     */
    public Display() {
        loadSettings();
        createCam();
        initComponents();
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        /*GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment()
         .getDefaultScreenDevice();
         if(gd.isFullScreenSupported()){
         gd.setFullScreenWindow(this);
         isFullscreen = true;
         } else {
         System.err.println("Full screen not supported.");
         }
        
         if(settings.getBoolean("debugMode")){
         Debugger.main(null);
         }*/
        getContentPane().setBackground(Color.black);
        createAndStartWorker();
        connectCam();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        mariaPanel = new JPanel();
        alexeiPanel = new JPanel();
        olgaPanel = new JPanel();
        meterset0 = new JPanel();
        stabilizationStateLabel = new JLabel();
        clawStateLabel = new JLabel();
        rasputinStateLabel = new JLabel();
        fcrBtn = new JButton();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("ROV Rasputin Commander");
        setBackground(new Color(0, 0, 0));
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        setName("rovCommander"); // NOI18N
        setUndecorated(true);
        setResizable(false);
        getContentPane().setLayout(new AbsoluteLayout());

        mariaPanel.setBackground(new Color(0, 0, 255));
        mariaPanel.setAlignmentX(0.0F);
        mariaPanel.setAlignmentY(0.0F);
        mariaPanel.setMaximumSize(new Dimension(600, 400));
        mariaPanel.setMinimumSize(new Dimension(600, 400));
        mariaPanel.setPreferredSize(new Dimension(600, 400));
        mariaPanel.setLayout(new BorderLayout());
        getContentPane().add(mariaPanel, new AbsoluteConstraints(0, 400, -1, -1));

        alexeiPanel.setBackground(new Color(0, 0, 255));
        alexeiPanel.setMaximumSize(new Dimension(600, 400));
        alexeiPanel.setMinimumSize(new Dimension(600, 400));
        alexeiPanel.setPreferredSize(new Dimension(600, 400));
        alexeiPanel.setLayout(new BorderLayout());
        getContentPane().add(alexeiPanel, new AbsoluteConstraints(0, 0, -1, -1));

        olgaPanel.setBackground(new Color(0, 0, 255));
        olgaPanel.setMaximumSize(new Dimension(600, 400));
        olgaPanel.setMinimumSize(new Dimension(600, 400));
        olgaPanel.setPreferredSize(new Dimension(600, 400));
        olgaPanel.setLayout(new BorderLayout());
        getContentPane().add(olgaPanel, new AbsoluteConstraints(600, 0, -1, -1));

        meterset0.setBackground(new Color(0, 0, 0));
        meterset0.setMaximumSize(new Dimension(380, 380));
        meterset0.setMinimumSize(new Dimension(380, 380));

        GroupLayout meterset0Layout = new GroupLayout(meterset0);
        meterset0.setLayout(meterset0Layout);
        meterset0Layout.setHorizontalGroup(meterset0Layout.createParallelGroup(GroupLayout.LEADING)
            .add(0, 380, Short.MAX_VALUE)
        );
        meterset0Layout.setVerticalGroup(meterset0Layout.createParallelGroup(GroupLayout.LEADING)
            .add(0, 380, Short.MAX_VALUE)
        );

        getContentPane().add(meterset0, new AbsoluteConstraints(610, 410, 380, 380));

        stabilizationStateLabel.setForeground(new Color(255, 255, 255));
        stabilizationStateLabel.setText("Stabilization State");
        getContentPane().add(stabilizationStateLabel, new AbsoluteConstraints(1000, 410, -1, -1));

        clawStateLabel.setForeground(new Color(255, 255, 255));
        clawStateLabel.setText("Claw State");
        getContentPane().add(clawStateLabel, new AbsoluteConstraints(1000, 430, -1, -1));

        rasputinStateLabel.setForeground(new Color(255, 255, 255));
        rasputinStateLabel.setText("Rasputin State");
        getContentPane().add(rasputinStateLabel, new AbsoluteConstraints(1000, 450, -1, -1));

        fcrBtn.setText("Force Camera Reconnect");
        fcrBtn.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
                fcrBtnActionPerformed(evt);
            }
        });
        getContentPane().add(fcrBtn, new AbsoluteConstraints(1000, 770, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void fcrBtnActionPerformed(ActionEvent evt)//GEN-FIRST:event_fcrBtnActionPerformed
    {//GEN-HEADEREND:event_fcrBtnActionPerformed
        disconnectCam();
        connectCam();
    }//GEN-LAST:event_fcrBtnActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the system look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Display.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new Display().setVisible(true);
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JPanel alexeiPanel;
    public JLabel clawStateLabel;
    private JButton fcrBtn;
    private JPanel mariaPanel;
    public JPanel meterset0;
    private JPanel olgaPanel;
    public JLabel rasputinStateLabel;
    public JLabel stabilizationStateLabel;
    // End of variables declaration//GEN-END:variables
}

