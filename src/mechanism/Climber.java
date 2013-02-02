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
    
    public void engage() {
        engager.set(true);
    }
    
    public void disengage() {
        engager.set(false);
    }
    
    public void toggleTop() {
        solenoid1.set(!solenoid1.get());
    }
    
    public void toggleBottom() {
        solenoid2.set(!solenoid2.get());
    }
    
    public void winch(double speed) {
        dt.setMotorSpeeds(speed, speed);
    }
    
    public boolean isEngaged()
    {
        return engager.get();
    }
}
