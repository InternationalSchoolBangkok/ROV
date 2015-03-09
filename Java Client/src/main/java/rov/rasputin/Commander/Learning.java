/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rov.rasputin.Commander;

import javax.swing.JFrame;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.github.sarxos.webcam.ds.ipcam.IpCamAuth;
import com.github.sarxos.webcam.ds.ipcam.IpCamDevice;
import com.github.sarxos.webcam.ds.ipcam.IpCamDeviceRegistry;
import com.github.sarxos.webcam.ds.ipcam.IpCamDriver;
import com.github.sarxos.webcam.ds.ipcam.IpCamMode;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Kolatat Thangkasemvathana <kolatat.t@gmail.com>
 */
public class Learning
{
    static {
        Webcam.setDriver(new IpCamDriver());
    }
    public static void ma2in(String[] args) throws InterruptedException {

        try {
            
            IpCamAuth auth = new IpCamAuth("rovguest", "rov");
            
            IpCamDevice olga = IpCamDeviceRegistry.register("Olga", "http://172.16.65.196:6969/videostream.cgi?loginuse=rovguest&loginpas=rov", IpCamMode.PUSH, auth);
            IpCamDevice alexei = IpCamDeviceRegistry.register("Alexei", "http://172.16.65.197:6969/videostream.cgi?loginuse=rovguest&loginpas=rov", IpCamMode.PUSH, auth);
            
            olga.setResolution(WebcamResolution.VGA.getSize());
            alexei.setResolution(WebcamResolution.VGA.getSize());
            
            WebcamPanel panel0 = new WebcamPanel(Webcam.getWebcams().get(0));
            WebcamPanel panel1 = new WebcamPanel(Webcam.getWebcams().get(1));
            panel0.setFPSDisplayed(true);
            panel0.setDisplayDebugInfo(true);
            panel0.setImageSizeDisplayed(true);
            panel1.setFPSDisplayed(true);
            panel1.setDisplayDebugInfo(true);
            panel1.setImageSizeDisplayed(true);
            
            JFrame window = new JFrame("Test webcam panel");
            window.setLayout(new GridBagLayout());
            window.add(panel0);
            window.add(panel1);
            window.setResizable(true);
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.pack();
            window.setVisible(true);
        } catch(MalformedURLException ex) {
            Logger.getLogger(Learning.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
