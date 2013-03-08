/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package macro;

import core.GRTMacro;
import edu.wpi.first.wpilibj.Timer;
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
        //in the future, instead of delaying, check to see if speed is correct
        Timer.delay(1.5);
        hasCompletedExecution = true;
    }

    public void die() {        
    }

    protected void initialize() {
        shooter.setSpeed(speed);
    }

}
