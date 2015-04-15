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
import java.awt.Font;
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
public class Display extends javax.swing.JFrame
{

    private Worker worker;
    private Properties settings;
    private Webcam olga, alexei, maria;

    public PS3Controller controller;

    private void loadSettings()
    {
        Webcam.setDriver(new IpCamDriver());

        try {
            settings = new Properties();
            InputStream in = getClass().getResourceAsStream("settings.properties");
            if(in == null) {
                System.out.println("ERROR");
            }
            settings.load(in);
            in.close();
        } catch(IOException ex) {
            Logger.getLogger(Display.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void createAndStartWorker()
    {
        worker = new Worker(this, settings);
        worker.start();
    }

    private void createCam()
    {
        String user = settings.getProperty("camUser");
        String pass = settings.getProperty("camPass");
        IpCamAuth auth = new IpCamAuth(user, pass);

        IpCamDeviceRegistry.unregisterAll();
        String format = "http://%s/videostream.cgi?loginuse=%s&loginpas=%s";
        Dimension d = new Dimension(585, 390);
        try {
            IpCamDeviceRegistry.register("Olga", String.format(format, settings.getProperty("olgaAddr"), user, pass), IpCamMode.PUSH, auth).setResolution(d);
            IpCamDeviceRegistry.register("Alexei", String.format(format, settings.getProperty("alexeiAddr"), user, pass), IpCamMode.PUSH, auth).setResolution(d);
            IpCamDeviceRegistry.register("Maria", String.format(format, settings.getProperty("mariaAddr"), user, pass), IpCamMode.PUSH, auth).setResolution(d);
        } catch(MalformedURLException ex) {
            Logger.getLogger(Display.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void connectCam()
    {
        olga = Webcam.getWebcams().get(0);
        olga.setImageTransformer(new WebcamImageTransformer()
        {
            @Override
            public BufferedImage transform(BufferedImage image)
            {
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

    private void disconnectCam()
    {
        olga.close();
        alexei.close();
        maria.close();
    }

    /**
     * Creates new form Interface
     */
    public Display()
    {
        loadSettings();
        createCam();
        initComponents();
        //setExtendedState(JFrame.MAXIMIZED_BOTH);
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
        meterset = new JPanel();
        stabilizationStateLabel = new JLabel();
        clawStateLabel = new JLabel();
        rasputinStateLabel = new JLabel();
        reconnectCamBtn = new JButton();
        depthLabel = new JLabel();
        reconnectControllerBtn = new JButton();
        quitBtn = new JButton();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("ROV Rasputin Commander");
        setBackground(new Color(0, 0, 0));
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        setMinimumSize(new Dimension(1200, 780));
        setName("rovCommander"); // NOI18N
        setUndecorated(true);
        getContentPane().setLayout(new AbsoluteLayout());

        mariaPanel.setBackground(new Color(0, 0, 255));
        mariaPanel.setAlignmentX(0.0F);
        mariaPanel.setAlignmentY(0.0F);
        mariaPanel.setMaximumSize(new Dimension(585, 390));
        mariaPanel.setMinimumSize(new Dimension(585, 390));
        mariaPanel.setPreferredSize(new Dimension(585, 390));
        mariaPanel.setLayout(new BorderLayout());
        getContentPane().add(mariaPanel, new AbsoluteConstraints(0, 390, -1, -1));

        alexeiPanel.setBackground(new Color(0, 0, 255));
        alexeiPanel.setMaximumSize(new Dimension(585, 390));
        alexeiPanel.setMinimumSize(new Dimension(585, 390));
        alexeiPanel.setName(""); // NOI18N
        alexeiPanel.setPreferredSize(new Dimension(585, 390));
        alexeiPanel.setLayout(new BorderLayout());
        getContentPane().add(alexeiPanel, new AbsoluteConstraints(0, 0, -1, -1));

        olgaPanel.setBackground(new Color(0, 0, 255));
        olgaPanel.setMaximumSize(new Dimension(585, 390));
        olgaPanel.setMinimumSize(new Dimension(585, 390));
        olgaPanel.setPreferredSize(new Dimension(585, 390));
        olgaPanel.setLayout(new BorderLayout());
        getContentPane().add(olgaPanel, new AbsoluteConstraints(585, 0, -1, -1));

        meterset.setBackground(new Color(0, 0, 0));
        meterset.setMaximumSize(new Dimension(380, 380));
        meterset.setMinimumSize(new Dimension(380, 380));

        GroupLayout metersetLayout = new GroupLayout(meterset);
        meterset.setLayout(metersetLayout);
        metersetLayout.setHorizontalGroup(metersetLayout.createParallelGroup(GroupLayout.LEADING)
            .add(0, 380, Short.MAX_VALUE)
        );
        metersetLayout.setVerticalGroup(metersetLayout.createParallelGroup(GroupLayout.LEADING)
            .add(0, 380, Short.MAX_VALUE)
        );

        getContentPane().add(meterset, new AbsoluteConstraints(590, 395, 380, 380));

        stabilizationStateLabel.setFont(stabilizationStateLabel.getFont().deriveFont(stabilizationStateLabel.getFont().getStyle() | Font.BOLD, stabilizationStateLabel.getFont().getSize()+4));
        stabilizationStateLabel.setForeground(new Color(255, 255, 255));
        stabilizationStateLabel.setText("Stabilization State");
        getContentPane().add(stabilizationStateLabel, new AbsoluteConstraints(1000, 410, -1, -1));

        clawStateLabel.setFont(clawStateLabel.getFont().deriveFont(clawStateLabel.getFont().getStyle() | Font.BOLD, clawStateLabel.getFont().getSize()+4));
        clawStateLabel.setForeground(new Color(255, 255, 255));
        clawStateLabel.setText("Claw State");
        getContentPane().add(clawStateLabel, new AbsoluteConstraints(1000, 440, -1, -1));

        rasputinStateLabel.setFont(rasputinStateLabel.getFont().deriveFont(rasputinStateLabel.getFont().getStyle() | Font.BOLD, rasputinStateLabel.getFont().getSize()+4));
        rasputinStateLabel.setForeground(new Color(255, 255, 255));
        rasputinStateLabel.setText("Rasputin State");
        getContentPane().add(rasputinStateLabel, new AbsoluteConstraints(1000, 470, -1, -1));

        reconnectCamBtn.setText("Force Camera Reconnect");
        reconnectCamBtn.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
                reconnectCamBtnActionPerformed(evt);
            }
        });
        getContentPane().add(reconnectCamBtn, new AbsoluteConstraints(990, 710, 190, 60));

        depthLabel.setFont(depthLabel.getFont().deriveFont(depthLabel.getFont().getStyle() | Font.BOLD, depthLabel.getFont().getSize()+4));
        depthLabel.setForeground(new Color(0, 255, 0));
        depthLabel.setText("Depth");
        getContentPane().add(depthLabel, new AbsoluteConstraints(1000, 500, -1, -1));

        reconnectControllerBtn.setText("Force Controller Reconnect");
        reconnectControllerBtn.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
                reconnectControllerBtnActionPerformed(evt);
            }
        });
        getContentPane().add(reconnectControllerBtn, new AbsoluteConstraints(990, 640, 190, 60));

        quitBtn.setText("Exit Commander");
        quitBtn.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
                quitBtnActionPerformed(evt);
            }
        });
        getContentPane().add(quitBtn, new AbsoluteConstraints(990, 570, 190, 60));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void reconnectCamBtnActionPerformed(ActionEvent evt)//GEN-FIRST:event_reconnectCamBtnActionPerformed
    {//GEN-HEADEREND:event_reconnectCamBtnActionPerformed
        disconnectCam();
        connectCam();
    }//GEN-LAST:event_reconnectCamBtnActionPerformed

    private void reconnectControllerBtnActionPerformed(ActionEvent evt)//GEN-FIRST:event_reconnectControllerBtnActionPerformed
    {//GEN-HEADEREND:event_reconnectControllerBtnActionPerformed
        controller = new PS3Controller();
        controller.poll();
    }//GEN-LAST:event_reconnectControllerBtnActionPerformed

    private void quitBtnActionPerformed(ActionEvent evt)//GEN-FIRST:event_quitBtnActionPerformed
    {//GEN-HEADEREND:event_quitBtnActionPerformed
        disconnectCam();
        System.exit(0);
    }//GEN-LAST:event_quitBtnActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[])
    {
        /* Set the system look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch(ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
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
    public JLabel depthLabel;
    private JPanel mariaPanel;
    public JPanel meterset;
    private JPanel olgaPanel;
    private JButton quitBtn;
    public JLabel rasputinStateLabel;
    private JButton reconnectCamBtn;
    private JButton reconnectControllerBtn;
    public JLabel stabilizationStateLabel;
    // End of variables declaration//GEN-END:variables
}
