/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rov;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
public class Control {
    Controller[] ca;
    Controller controller;
    Component[] components;

    public Control() {
        ca = ControllerEnvironment.getDefaultEnvironment().getControllers();
        for (int i = 0; i < ca.length; i++) {
            String cName = ca[i].getName();
            //System.out.println(cName);
            if (cName.equals("PLAYSTATION(R)3 Controller")) {
                //System.out.println(cName);
                controller = ca[i];
                //System.out.println("Type: " + ca[i].getType().toString());
                //Get this controllers components (buttons and axis)
                components = controller.getComponents();
            }
        }
    }

    public void poll() {
        controller.poll();
    }

    public float[] getComponentsData() {
        float[] vals = new float[components.length];
        for (int i = 0; i < components.length; i++) {
            vals[i] = components[i].getPollData();
        }
        return vals;
    }

    public void printComponents() {
        System.out.println("Component Count: " + components.length);
        for (int j = 0; j < components.length; j++) {
            //Get the components name
            System.out.println("Component " + j + ": " + components[j].getName());
        }
    }
}
