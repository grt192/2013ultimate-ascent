/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package macro;

import core.GRTMacro;
import event.events.EncoderEvent;
import event.listeners.EncoderListener;
import logger.GRTLogger;
import mechanism.GRTDriveTrain;

/**
 *
 * @author keshav
 */
public class MacroDrive extends GRTMacro implements EncoderListener {

    private GRTDriveTrain dt;
    private double distance = 0;
    private double velocity = 1;

    /*
     * Creates a new Driving Macro
     * 
     * @param dt GRTDriveTrain object
     * @param distance distance to travel in m (assumes travel in straight line)
     * @param timeout time in ms
     */
    public MacroDrive(GRTDriveTrain dt, double distance, int timeout) {
        super("Drive Macro", timeout);
        this.dt = dt;
        this.distance = distance;
    }

    protected void initialize() {
        dt.getLeftEncoder().addListener(this);
        dt.getRightEncoder().addListener(this);        
        dt.setMotorSpeeds(velocity, velocity);
    }

    protected void perform() {
        //TODO
    }

    public void die() {
        dt.getLeftEncoder().removeListener(this);
        dt.getRightEncoder().removeListener(this);
        dt.setMotorSpeeds(0, 0);
    }

    public void rotationStarted(EncoderEvent e) {
    }

    public void degreeChanged(EncoderEvent e) {
    }

    public void distanceChanged(EncoderEvent e) {
//        if(e.getSource() == dt.getLeftEncoder()){
//            GRTLogger.logInfo("Dist: " + e.getData());
//            if (e.getData() > distance) {
//                velocity = 0;
//                dt.setMotorSpeeds(0, 0);
//                die();
//            } else {
//                dt.setMotorSpeeds(velocity, velocity);
//            }
//        }        
    }

    public void rotationStopped(EncoderEvent e) {
    }

    public void rateChanged(EncoderEvent e) {
        GRTLogger.logInfo("[" + e.getSource() + "]: " + e.getData());
    }
}
