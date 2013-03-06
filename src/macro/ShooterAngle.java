/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package macro;

import core.GRTMacro;
import mechanism.Shooter;

/**
 *
 * @author keshav
 */
public class ShooterAngle extends GRTMacro {
 
    private int angle = 0;
    private Shooter shooter;
    
    /**
     * Shooter angle
     * @param angle Desired angle
     * @param shooter Shooter object
     * @param timeout Timeout (in ms)
     */
    public ShooterAngle(int angle, Shooter shooter, int timeout) {
        super("Shooter Angle Macro", timeout);
        this.shooter = shooter;
        this.angle = angle;
    }

    protected void perform() {
       try {
            Thread.sleep(2000);      
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        hasCompletedExecution = true;        
    }

    public void die() {
    }

    protected void initialize() {
        shooter.setAngle(angle);
    }

}
