package mechanism;

import core.GRTLoggedProcess;
import edu.wpi.first.wpilibj.SpeedController;
import event.events.SwitchEvent;
import event.listeners.SwitchListener;
import sensor.GRTSwitch;

/**
 * Mechanism code for ExternalPickup
 *
 * @author Sidd and Nadia
 */
public class ExternalPickup extends GRTLoggedProcess implements SwitchListener {

    private SpeedController rollerMotor;
    private SpeedController raiserMotor;
    private GRTSwitch limitUp;
    private GRTSwitch limitDown;
    private static double ROLLER_SF = -1, RAISE_SPEED = 1, LOWER_SPEED = -.3;

    public ExternalPickup(SpeedController rollerMotor, SpeedController raiserMotor,
            GRTSwitch limitUp, GRTSwitch limitDown) {
        super("PickerUpper mech");
        this.rollerMotor = rollerMotor;
        this.raiserMotor = raiserMotor;
        this.limitUp = limitUp;
        this.limitDown = limitDown;

        limitDown.addListener(this);
        limitUp.addListener(this);
    }

    public void raise() {
        //if the chalupa is already raised it will not raise further
        if (!limitUp.isPressed()) {
            raiserMotor.set(RAISE_SPEED);
        }
    }

    public void lower() {
        //if the chalupa is already lowered it will not lower further
        if (!limitDown.isPressed()) {
            raiserMotor.set(LOWER_SPEED);
        }
    }

    public void pickUp() {
        rollerMotor.set(1 * ROLLER_SF);
    }

    public void spitOut() {
        rollerMotor.set(-1 * ROLLER_SF);
    }

    public void stopRoller() {
        rollerMotor.set(0);
    }

    public void stopRaiser() {
        raiserMotor.set(0); 
    }

    public void switchStateChanged(SwitchEvent e) {
        logInfo("limit switch pressed");
        if (e.getData() == GRTSwitch.TRUE) {
            if (e.getSource() == limitUp) {
                stopRaiser();
            } else if (e.getSource() == limitDown) {
                stopRaiser();
            }
        }
    }
}