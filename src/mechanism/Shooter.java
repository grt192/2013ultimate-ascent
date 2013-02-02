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
    private SpeedController raiser;
    private GRTSolenoid frisbeeHolder, feeder;
    
    private static final double SHOOT_TIME = 0.4; //TODO check
    
    public Shooter(SpeedController shooterMotor1, SpeedController shooterMotor2,
            GRTSolenoid feeder, SpeedController raiser,
            GRTSolenoid holdDown) {
        super("Shooter mech");
        this.feeder = feeder;
        this.shooterMotor1 = shooterMotor1;
        this.shooterMotor2 = shooterMotor2;
        frisbeeHolder = holdDown;
        this.raiser = raiser;
        
        logInfo("New Shooter");
    }
    
    public void setSpeed(double speed) {
        logInfo("Setting speed:" + speed);
        shooterMotor1.set(speed);
        shooterMotor2.set(speed);
        //TODO PID
    }
    
    public void raise(double velocity) {
        raiser.set(velocity);
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
