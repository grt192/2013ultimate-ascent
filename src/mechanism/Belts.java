package mechanism;

import actuator.GRTSolenoid;
import core.GRTLoggedProcess;
import edu.wpi.first.wpilibj.SpeedController;

/**
 * Mechanism code for the Belts past the flipper. (i.e not PickUp Belts)
 * @author Sidd
 */
public class Belts extends GRTLoggedProcess{
    private static final double SPEED = -1.0;
    private final SpeedController beltsMotor;
    
    public Belts(SpeedController beltsMotor) {
        super("Belts mech");
        this.beltsMotor = beltsMotor;
    }
    
    public void moveUp() {
        beltsMotor.set(SPEED);
    }
    
    public void moveDown() {
        beltsMotor.set(-SPEED);
    }
    
    public void stop() {
        beltsMotor.set(0);
    }   
}