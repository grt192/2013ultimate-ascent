/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package macro;

import core.GRTMacro;
import edu.wpi.first.wpilibj.Timer;
import mechanism.Belts;

/**
 *
 * @author Calvin
 */
public class PrimeShovel extends GRTMacro {

    private final Belts belts;
    
    public PrimeShovel(Belts belts, int timeout) {
        super("stupid pickup", timeout);
        this.belts = belts;
    }
    
    protected void initialize() {
    }

    protected void perform() {
        belts.extendShovel();
        Timer.delay(0.2);
        belts.retractShovel();
        hasCompletedExecution = true;
    }

    public void die() {
    }
    
}
