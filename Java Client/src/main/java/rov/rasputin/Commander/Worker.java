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

    private int[] getOutput(float controllerValues[]) {
        /*
        # Commander > Rasputin Table
0 -> Controller Direction State
	bit0: up
	bit1: down
	bit2: left
	bit3: right
	bit4: triangle
	bit5: cross
	bit6: square
	bit7: circle
1 -> Controller Misc. State
	bit0: L1
	bit1: L2
	bit2: R1
	bit3: R2
	bit4: select
	bit5: start
	bit6: L3
	bit7: R3
2 -> Controller Left Stick x Value
3 -> Controller Left Stick y Value
4 -> Controller Right Stick x Value
5 -> Controller Right Stick y Value
        
     # Controller Components Mapping
0 -> Left Stick x Value
1 -> Left Stick y Value
2 -> Right Stick x Value
3 -> Right Stick y Value
4 -> Select
5 -> L3
6 -> R3
7 -> Start
8 -> Up
9 -> Right
10 -> Down
11 -> Left
12 -> L2
13 -> R2
14 -> L1
15 -> R1
16 -> Triangle
17 -> Circle
18 -> Cross
19 -> Square
20 -> PS3   
        */
        int []intMappedValues = new int[32];
        
        if(controllerValues[8]>0.5) intMappedValues[0]+=128;
        if(controllerValues[10]>0.5) intMappedValues[0]+=64;
        if(controllerValues[11]>0.5) intMappedValues[0]+=32;
        if(controllerValues[9]>0.5) intMappedValues[0]+=16;
        if(controllerValues[16]>0.5) intMappedValues[0]+=8;
        if(controllerValues[18]>0.5) intMappedValues[0]+=4;
        if(controllerValues[19]>0.5) intMappedValues[0]+=2;
        if(controllerValues[17]>0.5) intMappedValues[0]+=1;
        if(intMappedValues[0]>=128) intMappedValues[0]-=256;
        
        if(controllerValues[14]>0.5) intMappedValues[1]+=128;
        if(controllerValues[12]>0.5) intMappedValues[1]+=64;
        if(controllerValues[15]>0.5) intMappedValues[1]+=32;
        if(controllerValues[13]>0.5) intMappedValues[1]+=16;
        if(controllerValues[4]>0.5) intMappedValues[1]+=8;
        if(controllerValues[7]>0.5) intMappedValues[1]+=4;
        if(controllerValues[5]>0.5) intMappedValues[1]+=2;
        if(controllerValues[6]>0.5) intMappedValues[1]+=1;
        if(intMappedValues[1]>=128) intMappedValues[1]-=256;
        
        intMappedValues[2]=(int)(controllerValues[0]*127);
        intMappedValues[3]=(int)(controllerValues[1]*127);
        intMappedValues[4]=(int)(controllerValues[2]*127);
        intMappedValues[5]=(int)(controllerValues[3]*127);
        
        return intMappedValues;
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
