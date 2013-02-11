package mechanism;

import actuator.GRTSolenoid;
import core.GRTLoggedProcess;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Victor;

/**
 * Shooter mechanism
 * @author Calvin
 */
public class Shooter extends GRTLoggedProcess {
    private SpeedController shooterMotor1, shooterMotor2;
    private SpeedController raiser;
    private GRTSolenoid feeder;
    private boolean shooting = false;
    
    private double shootTime = 0.0; //TODO check
    
    public Shooter(Victor shooterMotor1, Victor shooterMotor2,
            GRTSolenoid feeder, double shootTime, Victor raiser) {
        super("Shooter mech");
        this.feeder = feeder;
        this.shooterMotor1 = shooterMotor1;
        this.shooterMotor2 = shooterMotor2;
        this.raiser = raiser;
        
        this.shootTime = shootTime;
        
        logInfo("New Shooter");        
    }
    
    public void setSpeed(double speed) {
        logInfo("Setting speed:" + speed);
        shooterMotor1.set(speed);
        shooterMotor2.set(speed);
        //TODO PID
    }
    
    public void adjustHeight(double velocity) {
        raiser.set(velocity);
    }
    
    public void shoot() {
        if (!shooting) {
            shooting = true;
            logInfo("Shooting!");
            logInfo("Setting feeder to ture");
            feeder.set(true);
            Timer.delay(shootTime);
            logInfo("setting feeder to false");
            feeder.set(false);
            shooting = false;
        }
    }
}
