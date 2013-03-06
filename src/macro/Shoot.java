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
        try {
            Thread.sleep(500);      
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        shooter.unShoot();
        hasCompletedExecution = true;
    }

    public void die() {        
    }

    protected void initialize() {
    }

}
