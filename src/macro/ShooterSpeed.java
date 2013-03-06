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
public class ShooterSpeed extends GRTMacro {
 
    private Shooter shooter;
    private double speed;
    
    /**
     * Delay execution for specified time
     * @param shooter Shooter object
     * @param timeout Timeout (in ms)
     */
    public ShooterSpeed(double speed, Shooter shooter, int timeout) {
        super("Shooter Speed Macro", timeout);
        this.shooter = shooter;
        this.speed = speed;
    }

    protected void perform() {
        shooter.setSpeed(speed);
        try {
            Thread.sleep(1000);      
            hasCompletedExecution = true;
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public void die() {        
    }

    protected void initialize() {
    }

}
