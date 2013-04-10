/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package macro;

import core.GRTMacro;
import edu.wpi.first.wpilibj.Timer;
import mechanism.Belts;
import mechanism.ExternalPickup;

/**
 *
 * @author Calvin
 */
public class AutoPickup extends GRTMacro {
    
    private ExternalPickup ep;
    private Belts belts;
    
    public AutoPickup(ExternalPickup ep, Belts belts, int timeout) {
        super("start auto pickup", timeout);
        this.ep = ep;
        this.belts = belts;
    }

    protected void initialize() {
    }

    protected void perform() {
        belts.moveUp();
        ep.pickUp();
        hasCompletedExecution = true;
    }

    protected void die() {
    }
    
}
