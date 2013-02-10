/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import java.util.Enumeration;
import java.util.Vector;

/**
 * A class that polls all sensors at once.
 *
 * @author Andrew Duffy <gerberduffy@gmail.com>
 */
public class SensorPoller extends GRTLoggedProcess{

    private Vector sensors;
    
    private final static int DEFAULT_POLLTIME = 10;

    public SensorPoller() {
        this(new Vector());
    }

    public SensorPoller(Vector sensors) {
        this(sensors, DEFAULT_POLLTIME);
    }

    public SensorPoller(Vector sensors, int pollTime) {
        super("Sensor poller", pollTime);
        this.sensors = sensors;
    }

    public void addSensor(Sensor s) {
        sensors.addElement(s);
    }
    
    public void removeSensor(Sensor s) {
        sensors.removeElement(s);
    }
    
    protected void poll() {
        for (Enumeration en = sensors.elements(); en.hasMoreElements();) {
            ((Sensor) en.nextElement()).poll();
        }
    }
}
