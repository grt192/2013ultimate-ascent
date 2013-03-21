package mechanism;

import actuator.GRTSolenoid;
import core.GRTLoggedProcess;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

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
        logInfo("Belts moving up!");
        beltsMotor.set(SPEED);
    }
    
    public void moveDown() {
        logInfo("Belts moving down!");
        beltsMotor.set(-SPEED);
    }
    
    public void stop() {
        logInfo("Belts stopping!");
        beltsMotor.set(0);
    }   
}
