/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import java.util.Enumeration;
import java.util.Vector;

/**
 * A class that polls all sensors at once.
 * @author Andrew Duffy <gerberduffy@gmail.com>
 */
public class SensorPoller extends Thread {
    
    private Vector sensors;
    private int pollTime;
    public SensorPoller(){
        sensors = new Vector();
        pollTime = 10;
    }
    
    public SensorPoller(Vector sensors){
        this.sensors = sensors;
    }
    
    public SensorPoller(Vector sensors, int pollTime){
        this.sensors = sensors;
        this.pollTime = pollTime;
    }
    
    public void addSensor(Sensor s){
        sensors.addElement(s);
    }
    
    public void run(){
        while (true){
            try {
                //Update all of our sensors.
                for (Enumeration en = sensors.elements(); en.hasMoreElements();){
                    ((Sensor)en.nextElement()).poll();
                }
                
                //Sleep a little.
                Thread.sleep(pollTime);
            } catch (Exception ex){
                System.err.println("There was a thread error while polling the sensors");
                ex.printStackTrace();
            }
        }
    }
    
}
