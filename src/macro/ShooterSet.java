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
public class ShooterSet extends GRTMacro {
 
    private final int angle;
    private final double speed;
    private Shooter shooter;
    
    /**
     * Shooter angle
     * @param angle Desired angle
     * @param speed
     * @param shooter Shooter object
     * @param timeout Timeout (in ms)
     */
    public ShooterSet(int angle, double speed, Shooter shooter, int timeout) {
        super("Shooter Angle Macro", timeout);
        this.shooter = shooter;
        this.angle = angle;
        this.speed = speed;
    }

    protected void perform() {
//        if (Math.abs(shooter.getShooterAngle() - angle) < 2)
        Timer.delay(3.5);
        hasCompletedExecution = true;        
    }

    public void die() {
        shooter.adjustHeight(0);
    }

    protected void initialize() {
        shooter.setAngle(angle);
        shooter.setSpeed(speed);
    }

}
