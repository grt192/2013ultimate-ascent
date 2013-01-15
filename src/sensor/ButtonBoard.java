package sensor;

import core.Sensor;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStationEnhancedIO;
import edu.wpi.first.wpilibj.DriverStationEnhancedIO.EnhancedIOException;
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
    
    public static final int KEY_BUTTON1 = 0;
    public static final int KEY_BUTTON2 = 1;
    public static final int KEY_BUTTON3 = 2;
    public static final int KEY_BUTTON4 = 3;
    public static final int KEY_BUTTON5 = 4;
    public static final int KEY_BUTTON6 = 5;
    public static final int KEY_POT1 = 6;
    public static final int KEY_POT2 = 7;
    private Vector buttonListeners = new Vector();
    private Vector potentiometerListeners = new Vector();
    
    private static final DriverStationEnhancedIO ioBoard =
            DriverStation.getInstance().getEnhancedIO();
    
    private static final ButtonBoard buttonBoard = new ButtonBoard();
    
    private ButtonBoard() {
        super("Button Board", 12, 8);
        
        try {
            //DIO 1-3 control LEDs
            ioBoard.setDigitalConfig(1, DriverStationEnhancedIO.tDigitalConfig.kOutput);
            ioBoard.setDigitalConfig(2, DriverStationEnhancedIO.tDigitalConfig.kOutput);
            ioBoard.setDigitalConfig(3, DriverStationEnhancedIO.tDigitalConfig.kOutput);
            
            //DIO 4-9 read buttons
            ioBoard.setDigitalConfig(4, DriverStationEnhancedIO.tDigitalConfig.kInputPullUp);
            ioBoard.setDigitalConfig(5, DriverStationEnhancedIO.tDigitalConfig.kInputPullUp);
            ioBoard.setDigitalConfig(6, DriverStationEnhancedIO.tDigitalConfig.kInputPullUp);
            ioBoard.setDigitalConfig(7, DriverStationEnhancedIO.tDigitalConfig.kInputPullUp);
            ioBoard.setDigitalConfig(8, DriverStationEnhancedIO.tDigitalConfig.kInputPullUp);
            ioBoard.setDigitalConfig(9, DriverStationEnhancedIO.tDigitalConfig.kInputPullUp);
            
            //AIO 1,2 read potentiometers
        } catch (EnhancedIOException ex) {
            ex.printStackTrace();
        }
    }
    
    public ButtonBoard getButtonBoard() {
        return buttonBoard;
    }

    protected void notifyListeners(int id, double newDatum) {
        
        if (id < 6) { //button event
            ButtonEvent e = new ButtonEvent(this, id, newDatum == TRUE);
            if (newDatum == TRUE)
                for (Enumeration en = buttonListeners.elements(); en.hasMoreElements();)
                    ((ButtonListener) en.nextElement()).buttonPressed(e);
            else
                for (Enumeration en = buttonListeners.elements(); en.hasMoreElements();)
                    ((ButtonListener) en.nextElement()).buttonReleased(e);
        } else { //potentiometer event
            PotentiometerEvent e = new PotentiometerEvent(this, id, newDatum);
            for (Enumeration en = potentiometerListeners.elements(); en.hasMoreElements();)
                ((PotentiometerListener) en.nextElement()).valueChanged(e);
        }
    }
    
    protected void poll() {
        for (int i = 4; i <= 9; i++) {  //iterate through button pins
            try {
                //button state IDs are offset from button pins by 4
                setState(i - 4, ioBoard.getDigital(i) ? FALSE : TRUE);
            } catch (EnhancedIOException ex) {
                ex.printStackTrace();
            }
        }
        
        for (int i = 1; i <= 2; i++) { //iterate through pot pins
            try {
                //button state IDs are offset from button pins by 5
                setState(i + 5, ioBoard.getAnalogInRatio(i));
            } catch (EnhancedIOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Set the state of an LED on the driver station.
     * @param num number of LED, from 1-3
     * @param on whether or not the LED is on
     */
    public void setLED(int num, boolean on) {
        if (num <= 3 && num >= 1) {
            try {
                ioBoard.setDigitalOutput(num, !on);
            } catch (EnhancedIOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    /**
     * Adds a button listener.
     * @param l listener to add
     */
    public void addButtonListener(ButtonListener l) {
        buttonListeners.addElement(l);
    }
    
    /**
     * Removes a button listener
     * @param l listener to remove
     */
    public void removeButtonListener(ButtonListener l) {
        buttonListeners.removeElement(l);
    }
    
    /**
     * Adds a potentiometer listener
     * @param l listener to add
     */
    public void addPotentiometerListener(PotentiometerListener l) {
        potentiometerListeners.addElement(l);
    }
    
    /**
     * Removes a potentiometer listener
     * @param l listener to add
     */
    public void removePotentiometerListener(PotentiometerListener l) {
        potentiometerListeners.removeElement(l);
    }
}
