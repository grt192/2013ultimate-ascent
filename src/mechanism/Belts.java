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
    private final GRTSolenoid shovelLifter;
    
    public Belts(SpeedController beltsMotor, GRTSolenoid shovelLifter) {
        super("Belts mech");
        this.beltsMotor = beltsMotor;
        this.shovelLifter = shovelLifter;
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

    public void extendShovel() {
        shovelLifter.set(true);
    }

    public void retractShovel( {
        shovelLifter.set(false);
    }
}
