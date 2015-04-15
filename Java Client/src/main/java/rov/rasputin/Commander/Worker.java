package rov.rasputin.Commander;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.BitSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import rov.rasputin.Communication.ROV;

public class Worker extends Thread
{

    private final boolean debugMode;

    private final Display parent;
    private final Properties settings;

    private ROV rasputin;

    private float roll, pitch, yaw, depth;

    public Worker(Display display, Properties settings)
    {
        parent = display;

        this.settings = settings;
        debugMode = settings.getBoolean("debugMode");
    }

    @Override
    public void run()
    {
        float[] channelData = null;
        int iteration = 0;
        int valuesToSend[];

        try {
            rasputin = new ROV(settings.getProperty("ROVAddress"),
                               settings.getInt("ROVPort"),
                               settings.getInt("clientPort"),
                               settings.getInt("datawidth"),
                               settings.getInt("commPeriod"));
            rasputin.startTXRX();
        } catch(UnknownHostException ex) {
            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("cannot connect to ROV1: " + ex);
        } catch(SocketException ex) {
            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Cannot connect to ROV2: " + ex);
        }

        parent.controller = new PS3Controller();
        parent.controller.poll();

        while(true) {

            if(!parent.controller.poll()) {
                parent.controller = new PS3Controller();
                System.out.println("ERROR! PS3 Controller polling error, had to create new controller.");
                parent.controller.poll();
            }
            channelData = parent.controller.getComponentsData();

            // roll(0,1)max180 pitch(2,3)max180
            roll = (rasputin.get(0) * 256 + (rasputin.get(1) + 128)) / 32768f * 180f;
            pitch = (rasputin.get(2) * 256 + (rasputin.get(3) + 128)) / 32768f * 180f;
            yaw = (rasputin.get(4) * 256 + (rasputin.get(5) + 128)) / 32768f * 180f;
            depth = (rasputin.get(6) * 256 + (rasputin.get(7) + 128)) / 32768f * 2.5f + 2.5f;

            updateStateLabels((byte) rasputin.get(8), depth);

            if(debugMode) {
                if(Debugger.instance != null) {
                    roll = Debugger.instance.RLL.getValue();
                    pitch = Debugger.instance.PCH.getValue();
                    yaw = Debugger.instance.YAW.getValue();
                }
            }

            if(iteration % 2 == 0) {
                updateUI();
            }

            valuesToSend = getOutput(channelData);
            for(int channel = 0; channel < 32 && rasputin != null; channel++) {
                rasputin.set(channel, valuesToSend[channel]);
            }

            iteration++;
            try {
                Thread.sleep(settings.getInt("workerPeriod"));
            } catch(InterruptedException ex) {
                Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private int[] getOutput(float controllerValues[])
    {
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
        int[] intMappedValues = new int[32];

        if(controllerValues[8] > 0.5) {
            intMappedValues[0] += 128;
        }
        if(controllerValues[10] > 0.5) {
            intMappedValues[0] += 64;
        }
        if(controllerValues[11] > 0.5) {
            intMappedValues[0] += 32;
        }
        if(controllerValues[9] > 0.5) {
            intMappedValues[0] += 16;
        }
        if(controllerValues[16] > 0.5) {
            intMappedValues[0] += 8;
        }
        if(controllerValues[18] > 0.5) {
            intMappedValues[0] += 4;
        }
        if(controllerValues[19] > 0.5) {
            intMappedValues[0] += 2;
        }
        if(controllerValues[17] > 0.5) {
            intMappedValues[0] += 1;
        }
        if(intMappedValues[0] >= 128) {
            intMappedValues[0] -= 256;
        }

        if(controllerValues[14] > 0.5) {
            intMappedValues[1] += 128;
        }
        if(controllerValues[12] > 0.5) {
            intMappedValues[1] += 64;
        }
        if(controllerValues[15] > 0.5) {
            intMappedValues[1] += 32;
        }
        if(controllerValues[13] > 0.5) {
            intMappedValues[1] += 16;
        }
        if(controllerValues[4] > 0.5) {
            intMappedValues[1] += 8;
        }
        if(controllerValues[7] > 0.5) {
            intMappedValues[1] += 4;
        }
        if(controllerValues[5] > 0.5) {
            intMappedValues[1] += 2;
        }
        if(controllerValues[6] > 0.5) {
            intMappedValues[1] += 1;
        }
        if(intMappedValues[1] >= 128) {
            intMappedValues[1] -= 256;
        }

        intMappedValues[2] = (int) (controllerValues[0] * 127);
        intMappedValues[3] = (int) (controllerValues[1] * 127);
        intMappedValues[4] = (int) (controllerValues[2] * 127);
        intMappedValues[5] = (int) (controllerValues[3] * 127);

        return intMappedValues;
    }

    private static BufferedImage pfd_AI;
    private static BufferedImage pfd_AI_mask;
    private static BufferedImage pfd_AI_ptr;
    private static BufferedImage pfd_AI_yaw;

    private static BasicStroke basicStroke2, basicStroke3;

    static {
        try {
            pfd_AI = ImageIO.read(Worker.class.getResource("pfd_AI.png"));
            pfd_AI_mask = ImageIO.read(Worker.class.getResource("pfd_AI_mask.png"));
            pfd_AI_ptr = ImageIO.read(Worker.class.getResource("pfd_AI_ptr.png"));
            pfd_AI_yaw = ImageIO.read(Worker.class.getResource("pfd_yaw.png"));
        } catch(IOException ex) {
            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
        }

        basicStroke2 = new BasicStroke(2);
        basicStroke3 = new BasicStroke(3);
    }

    private void drawMeterset(Graphics2D graphics)
    {
        graphics.setColor(Color.black);
        graphics.fillRect(0, 0, 380, 380);

        BufferedImage altImage = new BufferedImage(250, 250, BufferedImage.TYPE_INT_ARGB_PRE);
        Graphics2D altGraphics = altImage.createGraphics();

        AffineTransform altTransform = AffineTransform.getRotateInstance(Math.toRadians(roll), 400, 400);
        AffineTransformOp altTransformOp = new AffineTransformOp(altTransform, AffineTransformOp.TYPE_BICUBIC);

        altGraphics.drawImage(altTransformOp.filter(pfd_AI, null), 250 / 2 - 400, (int) (-275 + (pitch * 400) / 90), null);
        altGraphics.drawImage(pfd_AI_mask, 0, 0, null);

        altTransform = AffineTransform.getRotateInstance(Math.toRadians(roll), 125, 125);
        altTransformOp = new AffineTransformOp(altTransform, AffineTransformOp.TYPE_BICUBIC);
        altGraphics.drawImage(altTransformOp.filter(pfd_AI_ptr, null), 0, 0, null);

        BufferedImage yawImage = new BufferedImage(250, 30, BufferedImage.TYPE_INT_ARGB_PRE);
        Graphics2D yawGraphics = yawImage.createGraphics();
        int yawX = (int) (yaw / 360 * 3000 + 6);
        yawGraphics.drawImage(pfd_AI_yaw, -yawX + 125, 0, null);
        yawGraphics.drawImage(pfd_AI_yaw, -yawX + 125 - 3000, 0, null);
        yawGraphics.drawImage(pfd_AI_yaw, -yawX + 125 + 3000, 0, null);

        graphics.setColor(Color.white);
        graphics.fillRect(64, 340, 252, 31);
        graphics.drawImage(yawImage, 65, 341, null);
        graphics.setColor(Color.green);
        graphics.setStroke(basicStroke2);
        graphics.drawLine(190, 315, 190, 356);
        graphics.drawImage(altImage, 65, 65, null);
        graphics.setStroke(basicStroke3);
        graphics.setColor(Color.red);
        graphics.drawRect(60, 160, 260, 60);
    }

    private void updateStateLabels(byte stateByte, float depth)
    {
        byte stateByteAsArray[] = {stateByte};
        BitSet stateByteBitSet = BitSet.valueOf(stateByteAsArray);
        if(stateByteBitSet.get(0)) {
            parent.stabilizationStateLabel.setText("Stabilization On");
            parent.stabilizationStateLabel.setForeground(Color.green);
        } else {
            parent.stabilizationStateLabel.setText("Stabilization Off");
            parent.stabilizationStateLabel.setForeground(Color.red);
        }
        if(stateByteBitSet.get(1)) {
            parent.clawStateLabel.setText("Claw Locked");
            parent.clawStateLabel.setForeground(Color.red);
        } else {
            parent.clawStateLabel.setText("Claw Unlocked");
            parent.clawStateLabel.setForeground(Color.green);
        }
        if(stateByteBitSet.get(2)) {
            parent.rasputinStateLabel.setText("ROV ready");
            parent.rasputinStateLabel.setForeground(Color.green);
        } else {
            parent.rasputinStateLabel.setText("ROV not ready");
            parent.rasputinStateLabel.setForeground(Color.red);
        }

        parent.depthLabel.setText(String.format("Depth: %.5f", depth));
    }

    private void updateUI()
    {
        {
            BufferedImage frame = new BufferedImage(380, 380, BufferedImage.TYPE_INT_ARGB_PRE);
            drawMeterset(frame.createGraphics());
            parent.meterset.getGraphics().drawImage(frame, 0, 0, null);
        }
    }
}
