/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package macro;

import core.GRTMacro;
import event.events.GyroEvent;
import event.listeners.GyroListener;
import logger.GRTLogger;
import sensor.GRTGyro;

/**
 *
 * @author student
 */
public class GRTTurnMacro extends GRTMacro implements GyroListener {

    private double desiredTurnAngle;				//The desired angle of rotation.
    private double lastAngle;
    private double deltaAngle = 0;
    private GRTGyro gyro = null;

    public GRTTurnMacro(double degrees, GRTGyro gyro, int timeout) {
        super("Turn Macro", timeout);
        this.desiredTurnAngle = degrees;
        this.gyro = gyro;
    }

    protected void perform() {
        
    }

    public void die() {
        gyro.removeListener(this);
    }
    
    protected void initialize() {
        gyro.addListener(this);
    }


    public void angleChanged(GyroEvent e) {
        deltaAngle = e.getAngle() - lastAngle;
        lastAngle = e.getAngle();

        //If we've turned far enough, then we are ready to stop performing.
        if (deltaAngle >= desiredTurnAngle) {
            GRTLogger.logInfo("Angle: " + e.getAngle());
            hasCompletedExecution = true;
        }
    }

    
}
