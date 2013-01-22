/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mechanism;

import actuator.GRTSolenoid;
import core.GRTLoggedProcess;

/**
 * Mechanism Code for Climber
 * @author Sidd
 */
public class Climber extends GRTLoggedProcess {
    
    private GRTDriveTrain dt;
    private GRTSolenoid solenoid1;
    private GRTSolenoid solenoid2;
    private GRTSolenoid engager;
    
    public Climber(GRTDriveTrain dt, GRTSolenoid solenoid1, GRTSolenoid solenoid2,
            GRTSolenoid engager) { //TODO motors
        super("Climber mech");
        this.dt = dt;
        this.solenoid1 = solenoid1;
        this.solenoid2 = solenoid2;
        this.engager = engager;
    }
    
    public void climb() {
        //TODO
    }
}
