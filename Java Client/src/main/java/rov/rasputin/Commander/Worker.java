/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rov.rasputin.Commander;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import rov.rasputin.Communication.ROV;

/**
 *
 * @author 15998
 */
class Worker extends Thread {

    private final boolean skipController, skipCommunication, debugMode;

    private final Display parent;
    private final Properties settings;

    private ROV rasputin;

    private float roll, pitch, yaw;

    public Worker(Display display, Properties settings) {
        parent = display;

        this.settings = settings;

        skipCommunication = settings.getBoolean("workerSkipCommunication");
        skipController = settings.getBoolean("workerSkipController");
        debugMode = settings.getBoolean("debugMode");
    }

    @Override
    public void run() {
        float[] cData = null;
        int i = 0;
        boolean connected = false;
        if (!skipCommunication) {
            try {
                rasputin = new ROV(settings.getProperty("ROVAddress"),
                        settings.getInt("ROVPort"),
                        settings.getInt("clientPort"),
                        settings.getInt("datawidth"),
                        settings.getInt("commPeriod"));
                rasputin.startTXRX();
                connected = true;
            } catch (UnknownHostException ex) {
                Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("cannot connect to ROV1: " + ex);
            } catch (SocketException ex) {
                Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("Cannot connect to ROV2: " + ex);
            }
        }
        while (true) {
            if (!skipController) {
                try {
                    Control control = new Control();
                    control.poll();
                    cData = control.getComponentsData();
                } catch (Exception e) {
                    //System.out.println("Control Exception: " + e);
                    JOptionPane.showMessageDialog(parent, "Could not connect to control: " + e);
                }
            }
            if (!skipCommunication) {
                // roll(0,1)max180 pitch(2,3)max180
                roll = (rasputin.get(0) * 256 + (rasputin.get(1) + 128)) / 32768f * 180;
                pitch = (rasputin.get(2) * 256 + (rasputin.get(3) + 128)) / 32768f * 180;
            }
            if (debugMode) {
                if (Debugger.instance != null) {
                    roll = Debugger.instance.RLL.getValue();
                    pitch = Debugger.instance.PCH.getValue();
                    yaw = Debugger.instance.YAW.getValue();
                }
            }
            updateUI(cData);
            try {
                Thread.sleep(settings.getInt("workerPeriod"));
            } catch (InterruptedException ex) {
                Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
            }
            i++;
            if (connected && !skipCommunication && !skipController) {
                int values[];
                values = getOutput(cData);
                for (int b = 0; b < 32 && rasputin != null; b++) {
                    rasputin.set(b, values[b]);
                }
            }
        }
    }

    private int[] getOutput(float cData[]) {
        //map the controller input to the actual output
        int values[] = new int[32];
        int mots[] = calcMotors(cData[0], cData[1], cData[2]);
        values[0] = mots[0];
        values[1] = mots[1];
        values[2] = mots[2];
        values[3] = mots[3];
        //updown
        int vert = (cData[15] == 1f) ? 60 : (cData[13] == 1f) ? -60 : 0;//limit up down to only ±60
        values[4] = vert;
        values[5] = vert;
        values[6] = vert;
        values[7] = vert;
        //values[5] = (cData[14] == 1f) ? 127 : (cData[12] == 1f) ? -127 : 0;
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

    public int[] calcMotors(double x, double y, double yaw) {
        y *= -1; //flip y axis
        /* 
         Motor Layout
         1// \\2
         ROV
         4\\ //3           
         */
        //System.out.println("x, y: "+x+" "+y);
        double mots[]  = new double[4];
        mots[0] = (-y - x) * 63.5f;
        mots[1] = (x - y) * 63.5f;
        mots[2] = (x + y) * 63.5f;
        mots[3] = (y - x) * 63.5f;
        float yawLimit = 50;
        mots[0] += yaw*-yawLimit;
        mots[1] += yaw*yawLimit;
        mots[2] += yaw*-yawLimit;
        mots[3] += yaw*yawLimit;
        for(int i = 0;i<mots.length;i++){
            mots[i] = (mots[i]>127)?127:(mots[i]<-127)?-127:mots[i];//limit mots output
        }
        System.out.println("m1: " + mots[0] + " m2: " + mots[1] + " m3: " + mots[2] + " m4: " + mots[3]);
        int result[] = {(int) mots[0], (int) mots[1], (int) mots[2], (int) mots[3]};
        return result;
    }

    private static class WebColor {

        public static Color royalblue = new Color(0x4169e1);
    }

    private static BufferedImage pfd_AI;
    private static BufferedImage pfd_AI_mask;
    private static BufferedImage pfd_AI_ptr;
    private static BufferedImage pfd_AI_yaw;

    static {
        try {
            pfd_AI = ImageIO.read(Worker.class.getResource("pfd_AI.png"));
            pfd_AI_mask = ImageIO.read(Worker.class.getResource("pfd_AI_mask.png"));
            pfd_AI_ptr = ImageIO.read(Worker.class.getResource("pfd_AI_ptr.png"));
            pfd_AI_yaw = ImageIO.read(Worker.class.getResource("pfd_yaw.png"));
        } catch (IOException ex) {
            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void drawMeterset0(Graphics2D g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, 380, 380);

        BufferedImage AI = new BufferedImage(250, 250, BufferedImage.TYPE_INT_ARGB_PRE);
        Graphics2D aig = AI.createGraphics();

        AffineTransform aitx = AffineTransform.getRotateInstance(Math.toRadians(roll), 400, 400);
        AffineTransformOp aiop = new AffineTransformOp(aitx, AffineTransformOp.TYPE_BICUBIC);

        aig.drawImage(aiop.filter(pfd_AI, null), 250 / 2 - 400, (int) (-275 + (pitch * 400) / 90), null);
        aig.drawImage(pfd_AI_mask, 0, 0, null);

        aitx = AffineTransform.getRotateInstance(Math.toRadians(roll), 125, 125);
        aiop = new AffineTransformOp(aitx, AffineTransformOp.TYPE_BICUBIC);
        aig.drawImage(aiop.filter(pfd_AI_ptr, null), 0, 0, null);

        BufferedImage YAW = new BufferedImage(250, 30, BufferedImage.TYPE_INT_ARGB_PRE);
        Graphics2D yawg = YAW.createGraphics();
        int yawx = (int) (yaw / 360 * 3000 + 6);
        yawg.drawImage(pfd_AI_yaw, -yawx + 125, 0, null);
        yawg.drawImage(pfd_AI_yaw, -yawx + 125 - 3000, 0, null);
        yawg.drawImage(pfd_AI_yaw, -yawx + 125 + 3000, 0, null);

        g.setColor(Color.white);
        g.fillRect(64, 340, 252, 31);
        g.drawImage(YAW, 65, 341, null);
        g.setColor(Color.green);
        g.setStroke(new BasicStroke(2));
        g.drawLine(190, 315, 190, 356);
        g.drawImage(AI, 65, 65, null);
        g.setStroke(new BasicStroke(3));
        g.setColor(Color.red);
        g.drawRect(60, 160, 260, 60);
    }

    private void updateUI(float cData[]) {
        //drawing
        {
            BufferedImage frame = new BufferedImage(380, 380, BufferedImage.TYPE_INT_ARGB_PRE);
            drawMeterset0(frame.createGraphics());
            parent.meterset0.getGraphics().drawImage(frame, 0, 0, null);
        }

        if (cData != null) {

        } else {
            if (!skipController) {
                System.out.print("cData null");
            }
        }

        if (!skipCommunication) {
            StringBuilder channelDisp = new StringBuilder();
            channelDisp.append('{');
            for (int i = 0; i < settings.getInt("datawidth"); ++i) {
                channelDisp.append(rasputin.get(i)).append(", ");
                if (i % 8 == 0) {
                    channelDisp.append('\n');
                }
            }
            channelDisp.append("END}");
        }
    }
}
