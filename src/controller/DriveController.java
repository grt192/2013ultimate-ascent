package controller;

import core.EventController;
import event.events.ButtonEvent;
import event.events.JoystickEvent;
import event.listeners.ButtonListener;
import event.listeners.GRTJoystickListener;
import mechanism.GRTDriveTrain;
import sensor.GRTJoystick;

/**
 * Robot base driver.
 *
 * Operates for any DriverStation.
 *
 * @author andrew, keshav
 */
public class DriveController extends EventController implements GRTJoystickListener, ButtonListener {

    //sensor
    GRTJoystick left, right;
    
    //actuator
    private final GRTDriveTrain dt;
    
    //state
    private double leftVelocity;
    private double rightVelocity;
    
    private int triggersPressed = 0;
    
    /**
     * Creates a new driving controller.
     * 
     * @param base robot base to drive
     * @param ds driver station to control with
     * @param name name of controller
     */
    public DriveController(GRTDriveTrain dt, GRTJoystick leftStick, GRTJoystick rightStick) {
        super("Driving Controller");
        this.dt = dt;
        
        this.left = leftStick;
        this.right = rightStick;
    }

    protected void startListening() {
        left.addJoystickListener(this);
        left.addButtonListener(this);
        
        right.addJoystickListener(this);
        right.addButtonListener(this);        
    }

    protected void stopListening() {
        left.removeJoystickListener(this);
        left.removeButtonListener(this);
        
        right.removeJoystickListener(this);
        right.removeButtonListener(this);        
    }

    public void XAxisMoved(JoystickEvent e) {
    }

    public void YAxisMoved(JoystickEvent e) {
        if ( e.getSource() == left ){
            leftVelocity = e.getData();
        } else if ( e.getSource() == right ){
            rightVelocity = e.getData();
        }
        
        dt.setMotorSpeeds(leftVelocity, rightVelocity);
    }

    public void AngleChanged(JoystickEvent e) {
    }

    public void buttonPressed(ButtonEvent e) {
        
        if ( e.getButtonID() == GRTJoystick.KEY_BUTTON_TRIGGER ){
            dt.shiftDown();           
            triggersPressed++;
        }
    }
    
    public void buttonReleased(ButtonEvent e) {
        if ( e.getButtonID() == GRTJoystick.KEY_BUTTON_TRIGGER ){
            triggersPressed--;
            
            //If neither trigger is still being held, then it's safe to shift back up.
            if (triggersPressed <= 0){
                dt.shiftUp();
            }            
        }
    }
}
