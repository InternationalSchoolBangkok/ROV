/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rov;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 *
 * @author 15998
 */
class Worker extends Thread {

    boolean skipVideo = true;
    private final javax.swing.JLabel rz;
    private final javax.swing.JLabel x;
    private final javax.swing.JLabel y;
    private final javax.swing.JLabel z;
    private final javax.swing.JLabel cam1View;
    private final javax.swing.JFrame jf;
    private final javax.swing.JLabel stateLabel;
    
    private ImageIcon imageIcon;
    int cam1width, cam1height;

    public Worker(javax.swing.JFrame jf, JLabel x, JLabel y, JLabel z, JLabel rz, JLabel cam1View, JLabel stateLabel) {
        this.jf = jf;
        this.x = x;
        this.y = y;
        this.z = z;
        this.rz = rz;
        this.cam1View = cam1View;
        this.stateLabel = stateLabel;
    }

    public void setImageScale(int screenWidth, int screenHeight) {
        cam1width = screenWidth;
        cam1height = (int) ((float) cam1width * 0.65);
    }

    @Override
    public void run() {
        float[] cData = null;
        int i = 0;
        boolean connected = false;
        rov.rasputin.comm.State ROV = null;
        /*try {
            ROV = new rov.rasputin.comm.State("192.168.2.2", 6969, 6969, 32, 50);
            ROV.startTXRX();
            connected = true;
        } catch (UnknownHostException ex) {
            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("cannot connect to ROV1: " + ex);
            stateLabel.setText("Headless");
        } catch (SocketException ex) {
            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Cannot connect to ROV2: " + ex);
            stateLabel.setText("Headless");
        }*/

        while (true) {
            try {
                Control control = new Control();
                control.poll();
                cData = control.getComponentsData();
            } catch (Exception e) {
                //System.out.println("Control Exception: " + e);
                JOptionPane.showMessageDialog(jf, "Could not connect to control: " + e);
            }
            if (!skipVideo) {
                try {
                    BufferedImage myPicture = ImageIO.read(new URL("http://192.168.2.2/cam_pic.php?t=\"" + i));
                    myPicture = resize(myPicture, cam1width, cam1height);
                    imageIcon = new ImageIcon(myPicture);
                } catch (IOException ioe) {
                    //System.out.println("Video Exception: "+ioe);
                    JOptionPane.showMessageDialog(jf, "Could not get video: " + ioe);
                }
            }
            updateUI(cData);
            try {
                Thread.sleep(50);
            } catch (InterruptedException ex) {
                Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
            }
            i++;
            getOutput(cData);
            if (connected) {
                int values[];
                values = getOutput(cData);
                for (int b = 0; b < 32; b++) {
                    ROV.set(b, values[b]);
                }
            }

        }
    }

    private int[] getOutput(float cData[]) {
        //map the controller input to the actual output
        int values[] = new int[32];
        values[0] = (byte) (cData[0] * 127);
        values[1] = (byte) (cData[1] * 127);
        values[2] = (byte) (cData[2] * 127);
        values[3] = 0;
        values[4] = (cData[15]==1f)?127:(cData[13]==1f)?-127:0;
        values[5] = (cData[14]==1f)?127:(cData[12]==1f)?-127:0;
        //for debugging
        for(int i=0;i<6;i++){
            System.out.print("Values["+i+"]"+" "+values[i]+" ");
        }
        System.out.println();
        //
        return values;
                /*
         outChannel# - function - cData
         0 - straife - 0
         1 - forward/back - 1
         2 - Yaw - 3
         3 - NA - NA
         4 - UP/Down - 13/15
         5 - claw - 
         */
    }

    public static BufferedImage resize(BufferedImage image, int width, int height) {
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);
        Graphics2D g2d = (Graphics2D) bi.createGraphics();
        g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
        g2d.drawImage(image, 0, 0, width, height, null);
        g2d.dispose();
        return bi;
    }

    private void updateUI(float cData[]) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                //System.out.println("13,15 "+cData[13]+" "+cData[15]);
                x.setText("" + cData[0]);
                y.setText("" + cData[1]);
                z.setText("" + cData[2]);
                rz.setText("" + cData[3]);
                cam1View.setIcon(imageIcon);
            }
        });
    }
}
