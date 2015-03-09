/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rov.rasputin.Commander;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import rov.rasputin.Communication.ROV;

/**
 *
 * @author 15998
 */
class Worker extends Thread {

    private final boolean skipController;
    private final boolean skipCommunication;
    
    private final Display parent;
    private final Properties settings;

    public Worker(Display display, Properties settings) {
        parent = display;
        
        this.settings=settings;
        
        skipCommunication = settings.getBoolean("workerSkipCommunication");
        skipController = settings.getBoolean("workerSkipController");
    }

    @Override
    public void run() {
        float[] cData = null;
        int i = 0;
        boolean connected = false;
        ROV rasputin = null;
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
                parent.stateLabel.setText("Headless");
            } catch (SocketException ex) {
                Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("Cannot connect to ROV2: " + ex);
                parent.stateLabel.setText("Headless");
            }
        }
        while (true) {
            if (!skipController) {
                try {
                    Control control = new Control();
                    control.poll();
                    cData = control.getComponentsData();
                    System.out.println("cdat0: "+cData[0]);
                } catch (Exception e) {
                    //System.out.println("Control Exception: " + e);
                    JOptionPane.showMessageDialog(parent, "Could not connect to control: " + e);
                }
            }
            updateUI(cData);
            try {
                Thread.sleep(settings.getInt("workerPeriod"));
            } catch (InterruptedException ex) {
                Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
            }
            i++;
            getOutput(cData);
            if (connected) {
                int values[];
                values = getOutput(cData);
                for (int b = 0; b < 32 && rasputin!=null; b++) {
                    rasputin.set(b, values[b]);
                   System.out.print("Values["+b+"]"+" "+rasputin.get(b)+" ");
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
        values[4] = (cData[15] == 1f) ? 127 : (cData[13] == 1f) ? -127 : 0;
        values[5] = (cData[14] == 1f) ? 127 : (cData[12] == 1f) ? -127 : 0;
        //for debugging
        /*for(int i=0;i<6;i++){
         System.out.print("Values["+i+"]"+" "+values[i]+" ");
         }
         System.out.println();*/
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

    private void updateUI(float cData[]) {
        if (cData != null) {
            SwingUtilities.invokeLater(() -> {
                //System.out.println("13,15 "+cData[13]+" "+cData[15]);
                parent.xLabel.setText("" + cData[0]);
                parent.yLabel.setText("" + cData[1]);
                parent.zLabel.setText("" + cData[2]);
                parent.rzLabel.setText("" + cData[3]);
            });
        } else {
            if (!skipController) {
                System.out.print("cData null");
            }
        }
    }
}
