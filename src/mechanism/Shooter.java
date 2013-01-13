/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mechanism;

import core.GRTLoggedProcess;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Timer;

/**
 * Shooter mechanism
 * @author Calvin
 */
public class Shooter extends GRTLoggedProcess {
    private SpeedController shooterMotor;
    private SpeedController angleMotor;
    private Solenoid feeder;
    
    private static final double SHOOT_TIME = 0.4; //TODO check
    
    public Shooter(SpeedController shooterMotor, SpeedController angleMotor,
            Solenoid feeder) {
        super("Shooter mech");
        this.shooterMotor = shooterMotor;
        this.angleMotor = angleMotor;
        this.feeder = feeder;
        //TODO
    }
    
    public void setSpeed(double speed) {
        //TODO
    }
    
    public void setAngle(double speed) {
        //TODO
    }
    
    public void shoot() {
        feeder.set(true);
        Timer.delay(SHOOT_TIME);
        feeder.set(false);
    }
}
