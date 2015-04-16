/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.awooga.rovsurfacegimp;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamImageTransformer;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.ds.ipcam.*;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.OutputStream;
import static java.lang.Math.pow;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import org.jdesktop.layout.GroupLayout;

/**
 *
 * @author 15998
 */
public class Display extends javax.swing.JFrame {

    private Webcam olga, alexei, maria;
    int picIncrement = 0;

    private void createCam() {
        String user = "rovguest";
        String pass = "rov";
        IpCamAuth auth = new IpCamAuth(user, pass);
        IpCamDeviceRegistry.unregisterAll();
        String format = "http://%s/videostream.cgi?loginuse=%s&loginpas=%s";
        Dimension d = new Dimension(600, 400);
        Dimension d2 = new Dimension(400,300);
        try {
            IpCamDeviceRegistry.register("Olga", String.format(format, "10.69.69.74:6969", user, pass), IpCamMode.PUSH, auth).setResolution(d);
            IpCamDeviceRegistry.register("Alexei", String.format(format, "10.69.69.75:6969", user, pass), IpCamMode.PUSH, auth).setResolution(d);
            IpCamDeviceRegistry.register("Maria", String.format(format, "10.69.69.76:6969", user, pass), IpCamMode.PUSH, auth).setResolution(d2);
        } catch (MalformedURLException ex) {
            Logger.getLogger(Display.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void connectCam() {
        olga = Webcam.getWebcams().get(0);
        olga.setImageTransformer((BufferedImage image) -> {
            int w = image.getWidth();
            int h = image.getHeight();
            BufferedImage modified = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = modified.createGraphics();
            g2.drawImage(image, w, h, -w, -h, null);
            g2.dispose();
            modified.flush();
            return modified;
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
        Webcam.setDriver(new IpCamDriver());
        createCam();
        initComponents();
        getContentPane().setBackground(Color.black);
        olgaImageHolder.setVisible(false);
        olgaImageHolder.setEnabled(false);

        alexeiImageHolder.setVisible(false);
        alexeiImageHolder.setEnabled(false);
        rootPane.requestFocus();
        connectCam();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        olgaLayers = new JLayeredPane();
        olgaImagePanel = new JPanel();
        olgaImageHolder = new JLabel();
        olgaPanel = new JPanel();
        freezeButton = new JButton();
        unfreezeButton = new JButton();
        alexeiLayers = new JLayeredPane();
        alexeiImagePanel = new JPanel();
        alexeiImageHolder = new JLabel();
        alexeiPanel = new JPanel();
        outputLabel = new JLabel();
        outputLabel1 = new JLabel();
        mariaPanel = new JPanel();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("ROV Surface Gimp");
        setBackground(new Color(0, 0, 0));
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        setName("ROV Surface Gimp"); // NOI18N
        setResizable(false);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        olgaLayers.setPreferredSize(new Dimension(600, 400));

        olgaImagePanel.setPreferredSize(new Dimension(600, 400));

        olgaImageHolder.setText("THIS IS olga IMAGE HOLDER HUE");
        olgaImageHolder.setPreferredSize(new Dimension(600, 400));
        olgaImageHolder.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent evt) {
                olgaImageHolderMousePressed(evt);
            }
        });

        GroupLayout olgaImagePanelLayout = new GroupLayout(olgaImagePanel);
        olgaImagePanel.setLayout(olgaImagePanelLayout);
        olgaImagePanelLayout.setHorizontalGroup(olgaImagePanelLayout.createParallelGroup(GroupLayout.LEADING)
            .add(olgaImageHolder, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        );
        olgaImagePanelLayout.setVerticalGroup(olgaImagePanelLayout.createParallelGroup(GroupLayout.LEADING)
            .add(olgaImageHolder, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        );

        olgaPanel.setBackground(new Color(204, 204, 204));
        olgaPanel.setMaximumSize(new Dimension(600, 400));
        olgaPanel.setMinimumSize(new Dimension(600, 400));
        olgaPanel.setPreferredSize(new Dimension(600, 400));
        olgaPanel.setLayout(new BorderLayout());

        GroupLayout olgaLayersLayout = new GroupLayout(olgaLayers);
        olgaLayers.setLayout(olgaLayersLayout);
        olgaLayersLayout.setHorizontalGroup(olgaLayersLayout.createParallelGroup(GroupLayout.LEADING)
            .add(olgaLayersLayout.createSequentialGroup()
                .add(olgaPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .add(olgaLayersLayout.createParallelGroup(GroupLayout.LEADING)
                .add(olgaLayersLayout.createSequentialGroup()
                    .add(olgaImagePanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        olgaLayersLayout.setVerticalGroup(olgaLayersLayout.createParallelGroup(GroupLayout.LEADING)
            .add(olgaPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .add(olgaLayersLayout.createParallelGroup(GroupLayout.LEADING)
                .add(olgaLayersLayout.createSequentialGroup()
                    .add(olgaImagePanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .add(0, 0, Short.MAX_VALUE)))
        );
        olgaLayers.setLayer(olgaImagePanel, JLayeredPane.DEFAULT_LAYER);
        olgaLayers.setLayer(olgaPanel, JLayeredPane.DEFAULT_LAYER);

        freezeButton.setText("Freeze");
        freezeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                freezeButtonActionPerformed(evt);
            }
        });

        unfreezeButton.setText("Unfreeze");
        unfreezeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                unfreezeButtonActionPerformed(evt);
            }
        });

        alexeiLayers.setPreferredSize(new Dimension(600, 400));

        alexeiImageHolder.setText("THIS IS THE alexei IMAGE PANEL HUE");
        alexeiImageHolder.setPreferredSize(new Dimension(600, 400));
        alexeiImageHolder.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent evt) {
                alexeiImageHolderMousePressed(evt);
            }
        });

        GroupLayout alexeiImagePanelLayout = new GroupLayout(alexeiImagePanel);
        alexeiImagePanel.setLayout(alexeiImagePanelLayout);
        alexeiImagePanelLayout.setHorizontalGroup(alexeiImagePanelLayout.createParallelGroup(GroupLayout.LEADING)
            .add(alexeiImageHolder, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        );
        alexeiImagePanelLayout.setVerticalGroup(alexeiImagePanelLayout.createParallelGroup(GroupLayout.LEADING)
            .add(alexeiImageHolder, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        );

        alexeiPanel.setBackground(new Color(204, 204, 204));
        alexeiPanel.setMaximumSize(new Dimension(600, 400));
        alexeiPanel.setMinimumSize(new Dimension(600, 400));
        alexeiPanel.setPreferredSize(new Dimension(600, 400));
        alexeiPanel.setLayout(new BorderLayout());

        GroupLayout alexeiLayersLayout = new GroupLayout(alexeiLayers);
        alexeiLayers.setLayout(alexeiLayersLayout);
        alexeiLayersLayout.setHorizontalGroup(alexeiLayersLayout.createParallelGroup(GroupLayout.LEADING)
            .add(alexeiPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(alexeiLayersLayout.createParallelGroup(GroupLayout.LEADING)
                .add(GroupLayout.TRAILING, alexeiLayersLayout.createSequentialGroup()
                    .add(0, 0, Short.MAX_VALUE)
                    .add(alexeiImagePanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
        );
        alexeiLayersLayout.setVerticalGroup(alexeiLayersLayout.createParallelGroup(GroupLayout.LEADING)
            .add(alexeiPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .add(alexeiLayersLayout.createParallelGroup(GroupLayout.LEADING)
                .add(alexeiLayersLayout.createSequentialGroup()
                    .add(alexeiImagePanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .add(0, 6, Short.MAX_VALUE)))
        );
        alexeiLayers.setLayer(alexeiImagePanel, JLayeredPane.DEFAULT_LAYER);
        alexeiLayers.setLayer(alexeiPanel, JLayeredPane.DEFAULT_LAYER);

        outputLabel.setForeground(new Color(255, 255, 255));
        outputLabel.setText("HUEHEUHEHEHEUEHUE");

        outputLabel1.setForeground(new Color(255, 255, 255));
        outputLabel1.setText("HUEHEUHEHEHEUEHUE");

        mariaPanel.setBackground(new Color(153, 153, 0));
        mariaPanel.setLayout(new BorderLayout());

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(GroupLayout.LEADING)
                    .add(alexeiLayers, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(freezeButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(unfreezeButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(outputLabel)))
                .add(16, 16, 16)
                .add(layout.createParallelGroup(GroupLayout.LEADING)
                    .add(outputLabel1)
                    .add(olgaLayers, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .add(0, 0, Short.MAX_VALUE))
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(mariaPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(GroupLayout.LEADING, false)
                    .add(alexeiLayers, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(olgaLayers, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(GroupLayout.LEADING)
                    .add(layout.createParallelGroup(GroupLayout.BASELINE)
                        .add(freezeButton)
                        .add(unfreezeButton)
                        .add(outputLabel))
                    .add(outputLabel1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(mariaPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void freezeButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_freezeButtonActionPerformed
        // TODO add your handling code here:
        if (olgaPanel.isVisible() && alexeiPanel.isVisible()) {
            //olga
            olgaImage = getPanelPixies(olgaPanel, "pictures/olga" + picIncrement + ".jpg");
            olgaImageHolder.setIcon(new ImageIcon(olgaImage));
            olgaImageHolder.setVisible(true);
            olgaImageHolder.setEnabled(true);
            olgaPanel.setVisible(false);
            //alexei
            alexeiImage = getPanelPixies(alexeiPanel, "pictures/alexei" + picIncrement + ".jpg");
            alexeiImageHolder.setIcon(new ImageIcon(alexeiImage));
            alexeiImageHolder.setVisible(true);
            alexeiImageHolder.setEnabled(true);
            alexeiPanel.setVisible(false);
            picIncrement++;
        }
    }//GEN-LAST:event_freezeButtonActionPerformed

    private void unfreezeButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_unfreezeButtonActionPerformed
        // TODO add your handling code here:
        olgaImageHolder.setVisible(false);
        olgaImageHolder.setEnabled(false);
        olgaPanel.setVisible(true);

        alexeiImageHolder.setVisible(false);
        alexeiImageHolder.setEnabled(false);
        alexeiPanel.setVisible(true);
    }//GEN-LAST:event_unfreezeButtonActionPerformed

    private void formWindowClosing(WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        disconnectCam();
        dispose();
        System.exit(0);
    }//GEN-LAST:event_formWindowClosing
    BufferedImage alexeiImage;
    BufferedImage olgaImage;
    int llx, lly, lrx, lry;
    private void alexeiImageHolderMousePressed(MouseEvent evt) {//GEN-FIRST:event_alexeiImageHolderMousePressed
        System.out.printf("[left] selected (%d,%d).%n", evt.getX(), evt.getY());
        int x = evt.getX();
        int y = evt.getY();
        lx1 = lx2;
        lx2 = evt.getX();
        //nic
        ly1 = ly2;
        ly2 = evt.getY();
        //endnic
        Graphics2D g = (Graphics2D) alexeiImageHolder.getGraphics();
        g.drawImage(alexeiImage, null, 0, 0);
        g.setColor(new Color(0xaaaaff));
        int size = 5;
        g.setStroke(new BasicStroke(3));
        g.drawLine(x, y, llx, lly);
        g.setColor(new Color(0xffaaaa));
        g.fillOval(llx - size, lly - size, size * 2, size * 2);

        g.setColor(new Color(0xddddff));
        g.fillOval(x - size, y - size, size * 2, size * 2);

        calculations();
        llx = x;
        lly = y;
    }//GEN-LAST:event_alexeiImageHolderMousePressed

    private void olgaImageHolderMousePressed(MouseEvent evt) {//GEN-FIRST:event_olgaImageHolderMousePressed
        // TODO add your handling code here:
        System.out.printf("[right] selected (%d,%d).%n", evt.getX(), evt.getY());
        int x = evt.getX();
        int y = evt.getY();
        rx1 = rx2;
        rx2 = evt.getX();
        //nic
        ry1 = ry2;
        ry2 = evt.getY();
        //endnic
        Graphics2D g = (Graphics2D) olgaImageHolder.getGraphics();
        g.drawImage(olgaImage, null, 0, 0);
        g.setColor(new Color(0xaaaaff));
        int size = 5;
        g.setStroke(new BasicStroke(3));
        g.drawLine(x, y, lrx, lry);
        g.setColor(new Color(0xffaaaa));
        g.fillOval(lrx - size, lry - size, size * 2, size * 2);

        g.setColor(new Color(0xddddff));
        g.fillOval(x - size, y - size, size * 2, size * 2);
        calculations();
        lrx = x;
        lry = y;
    }//GEN-LAST:event_olgaImageHolderMousePressed

    public BufferedImage getPanelPixies(Component myComponent, String filename) {
        Dimension size = myComponent.getSize();
        BufferedImage myImage = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = myImage.createGraphics();
        myComponent.paint(g2);
        try {
            OutputStream out = new FileOutputStream(filename);
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
            encoder.encode(myImage);
            out.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return myImage;
    }

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
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new Display().setVisible(true);
        });
    }
    double pixsizeL, pixsizeR, pixsize, shift, lx1, lx2, rx1, rx2, size, A, B, C, D, E;
    double pixsizeL1, pixsizeR1, pixsize1, shift1, ly1, ly2, ry1, ry2, size1, A1, B1, C1, D1, E1;

    private void calculations() {
        double lx1, lx2, rx1, rx2, ly1, ly2, ry1, ry2;
        lx1 = this.lx1;
        lx2 = this.lx2;
        rx1 = this.rx1;
        rx2 = this.rx2;
        ly1 = this.ly1;
        ly2 = this.ly2;
        ry1 = this.ry1;
        ry2 = this.ry2;
        //incase x1>x2
        if (lx1 > lx2) {
            double tmp = lx1;
            lx1 = lx2;
            lx2 = tmp;
        }
        if (rx1 > rx2) {
            double tmp = rx1;
            rx1 = rx2;
            rx2 = tmp;
        }

        if (ly1 > ly2) {
            double tmp = ly1;
            ly1 = ly2;
            ly2 = tmp;
        }
        if (ry1 > ry2) {
            double tmp = ry1;
            ry1 = ry2;
            ry2 = tmp;
        }

        pixsizeL = lx2 - lx1;
        System.out.println("pixsizeL " + pixsizeL);

        pixsizeR = rx2 - rx1;
        System.out.println("pixsizeR " + pixsizeR);

        pixsize = (pixsizeL + pixsizeR) / 2;
        System.out.println("average size pix: " + pixsize);

        shift = lx1 - rx1;
        shift = (shift > 0) ? shift : -shift;
        System.out.println("pixel shift " + shift);

        pixsizeL1 = ly2 - ly1;
        System.out.println("pixsizeL1 " + pixsizeL1);

        pixsizeR1 = ry2 - ry1;
        System.out.println("pixsizeR1 " + pixsizeR1);

        pixsize1 = (pixsizeL1 + pixsizeR1) / 2;
        System.out.println("average size pix1: " + pixsize1);

        shift1 = ly1 - ry1;
        shift1 = (shift1 > 0) ? shift1 : -shift1;
        System.out.println("pixel shift1 " + shift1);

//        size = C*(pixsize/(A*(shift^B)))^D;       
//       pow(shift, B);     
//        size = C*(pixsize/(A*(pow(shift,B))))^D;     
        A = 0.001467;
        B = 1.119;
        E = 0.1522;
        C = 0.0007641;
        D = 1.034;

        double size1 = pow(shift, B);
        System.out.println("size1: " + size1);

        double size2 = pixsize / (A * (size1) + E);
        System.out.println("size2: " + size2);

        System.out.println("pixsize: " + pixsize);

//        double size3;
//        if(size2>0){
//        }else{
//            double temp = Math.abs(size2);
//            size3 = pow(temp ,1.5);
//            size3*=-1;
//        }
        double size3 = pow(size2, D);

        System.out.println("size3: " + size3);

        size = C * size3;

        System.out.println("size hue: " + size);

        Double.toString(size);

        String sizeString;
        if (Double.isInfinite(size)) {
            sizeString = "your mom";
        } else {
            sizeString = String.format("%.5f", size);
        }
        outputLabel.setText(String.format("left(%.0f-%.0f), right(%.0f-%.0f), size(%s)", lx1, lx2, rx1, rx2, sizeString));

        // NEW SHIT FOR Y
        A1 = 0.00002836;

        B1 = 1.812;

        E1 = 0.06488;

        C1 = 0.001247;

        D1 = 0.9617;

        double ysize1 = pow(shift1, B1);
        System.out.println("ysize1: " + ysize1);

        double ysize2 = pixsize1 / (A1 * (ysize1) + E1);
        System.out.println("ysize2: " + ysize2);

        System.out.println("pixsize1: " + pixsize1);

//        double size3;
//        if(size2>0){
//        }else{
//            double temp = Math.abs(size2);
//            size3 = pow(temp ,1.5);
//            size3*=-1;
//        }
        double ysize3 = pow(ysize2, D1);

        System.out.println("ysize3: " + ysize3);

        size1 = C1 * ysize3;

        System.out.println("size1 hue: " + size1);

        Double.toString(size1);

        String size1String;
        if (Double.isInfinite(size1)) {
            size1String = "your mom";
        } else {
            size1String = String.format("%.5f", size1);
        }
        outputLabel1.setText(String.format("left(%.0f-%.0f), right(%.0f-%.0f), size(%s)", ly1, ly2, ry1, ry2, size1String));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JLabel alexeiImageHolder;
    private JPanel alexeiImagePanel;
    private JLayeredPane alexeiLayers;
    private JPanel alexeiPanel;
    private JButton freezeButton;
    private JPanel mariaPanel;
    private JLabel olgaImageHolder;
    private JPanel olgaImagePanel;
    private JLayeredPane olgaLayers;
    private JPanel olgaPanel;
    private JLabel outputLabel;
    private JLabel outputLabel1;
    private JButton unfreezeButton;
    // End of variables declaration//GEN-END:variables
}
