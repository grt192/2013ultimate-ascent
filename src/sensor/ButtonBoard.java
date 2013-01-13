/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sensor;

import core.Sensor;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStationEnhancedIO;
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
    private Vector listeners = new Vector();
    
    private static final DriverStationEnhancedIO ioBoard =
            DriverStation.getInstance().getEnhancedIO();
    
    public ButtonBoard() {
        super("Button Board", 2);
    }

    protected void notifyListeners(int id, double newDatum) {
        for (Enumeration en = listeners.elements(); en.hasMoreElements();) {
            switch (id) {
                
            }
        }
    }
}
