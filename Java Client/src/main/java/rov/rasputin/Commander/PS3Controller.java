package rov.rasputin.Commander;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

public class PS3Controller
{

    private Controller mainController;
    private Component[] components;

    public PS3Controller()
    {
        Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
        for(Controller eachController : controllers) {
            if(eachController.getName().equals("PLAYSTATION(R)3 Controller")) {
                mainController = eachController;
                components = mainController.getComponents();
            }
        }
    }

    public boolean poll()
    {
        if(!mainController.poll()) {
            return false;
        }
        components = mainController.getComponents();
        return true;
    }

    public float[] getComponentsData()
    {
        float[] values = new float[components.length];
        for(int index = 0; index < components.length; index++) {
            values[index] = components[index].getPollData();
        }
        return values;
    }
}
