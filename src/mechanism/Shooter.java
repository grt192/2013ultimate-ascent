package mechanism;

import actuator.GRTSolenoid;
import core.GRTLoggedProcess;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Timer;

/**
 * Shooter mechanism
 * @author Calvin
 */
public class Shooter extends GRTLoggedProcess {
    private SpeedController shooterMotor1, shooterMotor2;
    private GRTSolenoid feeder, raiser1, raiser2;
    private GRTSolenoid frisbeeHolder;
    
    private static final double SHOOT_TIME = 0.4; //TODO check
    
    public Shooter(SpeedController shooterMotor1, SpeedController shooterMotor2,
            GRTSolenoid feeder, GRTSolenoid raiser1, GRTSolenoid raiser2,
            GRTSolenoid holdDown) {
        super("Shooter mech");
        this.feeder = feeder;
        this.shooterMotor1 = shooterMotor1;
        this.shooterMotor2 = shooterMotor2;
        this.raiser1 = raiser1;
        this.raiser2 = raiser2;
        frisbeeHolder = holdDown;
        
        logInfo("New Shooter");
    }
    
    public void setSpeed(double speed) {
        logInfo("Setting speed:" + speed);
        shooterMotor1.set(speed);
        shooterMotor2.set(speed);
        //TODO PID
    }
    
    public void lower() {
        raiser1.set(true);
        raiser2.set(true);
    }
    
    public void raise() {
        raiser1.set(false);
        raiser2.set(false);
    }
    
    public void shoot() {
        logInfo("Shooting!");
        feeder.set(true);
        Timer.delay(SHOOT_TIME);
        feeder.set(false);
    }
    
    public void raiseHoldDown() {
        frisbeeHolder.set(true);
    }
    
    public void lowerHoldDown() {
        frisbeeHolder.set(false);
    }
}
