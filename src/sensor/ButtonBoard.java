/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sensor;

import core.Sensor;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStationEnhancedIO;
import event.events.ButtonEvent;
import event.events.PotentiometerEvent;
import event.listeners.ButtonListener;
import event.listeners.PotentiometerListener;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Button board on the 2013 driver station.
 * 
 * @author Calvin
 */
public class ButtonBoard extends Sensor {
    
    public static final int KEY_BUTTON1 = 1;
    public static final int KEY_BUTTON2 = 2;
    public static final int KEY_BUTTON3 = 3;
    public static final int KEY_BUTTON4 = 4;
    public static final int KEY_BUTTON5 = 5;
    public static final int KEY_BUTTON6 = 6;
    public static final int KEY_POT1 = 7;
    public static final int KEY_POT2 = 8;
    private Vector buttonListeners = new Vector();
    private Vector potentiometerListeners = new Vector();
    
    private static final DriverStationEnhancedIO ioBoard =
            DriverStation.getInstance().getEnhancedIO();
    
    public ButtonBoard() {
        super("Button Board", 8);
    }

    protected void notifyListeners(int id, double newDatum) {
        
        if (id < 7) { //button event
            ButtonEvent e = new ButtonEvent(this, id, newDatum == TRUE);
            if (newDatum == TRUE)
                for (Enumeration en = buttonListeners.elements(); en.hasMoreElements();)
                    ((ButtonListener) en.nextElement()).buttonPressed(e);
            else
                for (Enumeration en = buttonListeners.elements(); en.hasMoreElements();)
                    ((ButtonListener) en.nextElement()).buttonReleased(e);
        } else { //potentiometer event
            PotentiometerEvent e = new PotentiometerEvent(this, newDatum);
            for (Enumeration en = potentiometerListeners.elements(); en.hasMoreElements();)
                ((PotentiometerListener) en.nextElement()).valueChanged(e);
        }
    }
}
