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
public class Shoot extends GRTMacro {
 
    private Shooter shooter;
    
    /**
     * Operate Luna for one cycle
     * @param shooter Shooter object
     * @param timeout Timeout (in ms)
     */
    public Shoot(Shooter shooter, int timeout) {
        super("Shoot Macro", timeout);
        this.shooter = shooter;
        
    }

    protected void perform() {
        shooter.shoot();
        Timer.delay(0.2);
        shooter.unShoot();
        Timer.delay(0.5);
        hasCompletedExecution = true;
    }

    public void die() {        
    }

    protected void initialize() {
    }

}
