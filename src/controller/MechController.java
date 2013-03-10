package controller;

import core.EventController;
import core.GRTConstants;
import event.events.ButtonEvent;
import event.events.JoystickEvent;
import event.events.PotentiometerEvent;
import event.events.XboxJoystickEvent;
import event.listeners.ButtonListener;
import event.listeners.GRTJoystickListener;
import event.listeners.PotentiometerListener;
import event.listeners.XboxJoystickListener;
import mechanism.Belts;
import mechanism.Climber;
import mechanism.ExternalPickup;
import mechanism.GRTDriveTrain;
import mechanism.Shooter;
import sensor.GRTJoystick;
import sensor.GRTXboxJoystick;

/**
 * Controller for shooter, picker-upper, internal belts, climbing
 *
 * @author Calvin, agd
 */
public class MechController extends EventController implements GRTJoystickListener, XboxJoystickListener, PotentiometerListener, ButtonListener {

    private GRTJoystick leftJoy;
    private GRTJoystick rightJoy;
    private GRTXboxJoystick secondary;
    private Belts belts;
    private Climber climber;
    private ExternalPickup pickerUpper;
    private Shooter shooter;
    private GRTDriveTrain dt;
    
    private double shootingSpeed = GRTConstants.getValue("shootingRPMS");

    private double shooterPresetY;
    private double shooterPresetB;

    private double turningDivider;
    private double adjustDivider;
    private double storedAngle;
    
    private boolean leftShoulderHeld = false, leftTriggerHeld = false;  //Variables useful for collection logic.

    public MechController(GRTJoystick leftJoy, GRTJoystick rightJoy,
            GRTXboxJoystick secondary,
            Shooter shooter, ExternalPickup pickerUpper,
            Climber climber, Belts belts,
            GRTDriveTrain dt) {
        super("Mechanism Controller");
        this.leftJoy = leftJoy;
        this.rightJoy = rightJoy;
        this.secondary = secondary;

        this.belts = belts;
        this.climber = climber;
        this.pickerUpper = pickerUpper;
        this.shooter = shooter;

        this.dt = dt;

        this.shooterPresetB = GRTConstants.getValue("anglePyramidFrontPreset");
        this.shooterPresetY = GRTConstants.getValue("anglePyramidRearPreset");
    }

    protected void startListening() {
        leftJoy.addJoystickListener(this);
        leftJoy.addButtonListener(this);

        rightJoy.addJoystickListener(this);
        rightJoy.addButtonListener(this);

        secondary.addJoystickListener(this);
        secondary.addButtonListener(this);

        try {
            turningDivider = GRTConstants.getValue("turningDivider");
        } catch(Exception e){
            turningDivider = 2.0;
            logError("Could not find key  `turningDivider'  in the constants file. Maybe you should add it?");
            logInfo("Setting turingDivider to default of " + turningDivider);
        }

        try {
            adjustDivider = GRTConstants.getValue("adjustDivider");
        } catch(Exception e){
            adjustDivider = 10.0;
            logError("Could not find key  `adjustDivider'  in the constants file. Maybe you should add it?");
            logInfo("Setting adjustDivider to default of " + adjustDivider);
        }
    }

    protected void stopListening() {
        leftJoy.removeJoystickListener(this);
        leftJoy.removeButtonListener(this);

        rightJoy.removeJoystickListener(this);
        rightJoy.removeButtonListener(this);

        secondary.removeJoystickListener(this);
        secondary.removeButtonListener(this);
        
        
        //Set the flywheel controller back to zero on disable. Helps prevent the I term from accumulating to quickly
        shooter.setFlywheelOutput(0.0);

    }

    public void XAxisMoved(JoystickEvent e) {
    }

    public void YAxisMoved(JoystickEvent e) {
    }

    public void AngleChanged(JoystickEvent e) {
    }

    //commented out code is because betabot is FUBAR
    public void buttonPressed(ButtonEvent e) {
        try {
            if (e.getSource() == rightJoy) {
                switch (e.getButtonID()) {
                    case GRTJoystick.KEY_BUTTON_3: 
                        pickerUpper.pickUp();
                        belts.moveUp();
//                        belts.extendShovel();
                        break;
                    case GRTJoystick.KEY_BUTTON_2: 
                        pickerUpper.spitOut();
                        belts.moveDown();
                        break;
                        
                    case GRTJoystick.KEY_BUTTON_4:
                        pickerUpper.raise();
                        break;
                    case GRTJoystick.KEY_BUTTON_5:
                        pickerUpper.lower();
                        break;
                }  
            }

            else if (e.getSource() == leftJoy) {
                switch (e.getButtonID()) {
                    case GRTJoystick.KEY_BUTTON_3: 
                        //pickerUpper.raise();
                        break;
                    case GRTJoystick.KEY_BUTTON_2: 
                        pickerUpper.lower();
                        break;
                }   
            }

            else if (e.getSource() == secondary){
                switch (e.getButtonID()){
                    case GRTXboxJoystick.KEY_BUTTON_X:
                        logError("X: Angle adjustment #X will be here.");
                        shooter.setAngle(0);
                        break;
                    case GRTXboxJoystick.KEY_BUTTON_Y:
                        logError("A: Angle adjustment #Y will be here.");
                        shooter.setAngle(shooterPresetY);
                        break;
                    case GRTXboxJoystick.KEY_BUTTON_B:
                        shooter.setAngle(shooterPresetB);
                        break;
                    case GRTXboxJoystick.KEY_BUTTON_LEFT_SHOULDER:
                        shooter.setSpeed(shootingSpeed);
                        break;

                    case GRTXboxJoystick.KEY_BUTTON_RIGHT_SHOULDER:
                        shooter.shoot();
                        System.out.println(shooter.getShooterAngle());
                        break;
                    case GRTXboxJoystick.KEY_BUTTON_BACK:
                        logInfo("Storing angle.");
                        storedAngle = shooter.getShooterAngle();
                        System.out.println(storedAngle);
                        break;
                    case GRTXboxJoystick.KEY_BUTTON_START:
                        logInfo("Going to stored angle: " + storedAngle);
                        shooter.setAngle(storedAngle);
                }
            }
        } catch (NullPointerException ex) {
            logError("Null pointer encountered when trying to categorize button events");
            ex.printStackTrace();
        }
    }

    public void buttonReleased(ButtonEvent e) {
        if (e.getSource() == leftJoy) {
            switch (e.getButtonID()) {
                case GRTJoystick.KEY_BUTTON_TRIGGER:
                    //pickerUpper.raise();
                    break;
                case GRTJoystick.KEY_BUTTON_4:
            }
        }

        else if (e.getSource() == rightJoy) {
            switch (e.getButtonID()) {
                case GRTJoystick.KEY_BUTTON_3:
                    belts.retractShovel();
                case GRTJoystick.KEY_BUTTON_2: 
                    pickerUpper.stopRoller();
                    belts.stop();
                    break;
                case GRTJoystick.KEY_BUTTON_4:
                case GRTJoystick.KEY_BUTTON_5:
                    pickerUpper.stopRaiser();
                    break;
            }
        }

        else if (e.getSource() == secondary) {
            switch (e.getButtonID()) {
                case GRTXboxJoystick.KEY_BUTTON_X:
                    shooter.setFlywheelOutput(0.0);
                    break;
                case GRTXboxJoystick.KEY_BUTTON_A:
                    shooter.setFlywheelOutput(0.0);
                    break;
                case GRTXboxJoystick.KEY_BUTTON_B:
                    shooter.setFlywheelOutput(0.0);
                    break;
                case GRTXboxJoystick.KEY_BUTTON_LEFT_SHOULDER:
                    shooter.setSpeed(0.0);
                    shooter.setFlywheelOutput(0.0);
                    break;
                case GRTXboxJoystick.KEY_BUTTON_RIGHT_SHOULDER:
                    shooter.unShoot();
                    break;
            }
        }
    }
    
    public void leftXAxisMoved(XboxJoystickEvent e) {
        //Use Xbox left axis to make fine adjustments to the robot's directional heading.
        logInfo("Left x axis moved!");
        if (e.getSource() == secondary){
            System.out.println("Slowly turning dt's");
            dt.setMotorSpeeds(-e.getData() / turningDivider , e.getData() / turningDivider );
        }
    }

    public void leftYAxisMoved(XboxJoystickEvent e) {
    }

    public void leftAngleChanged(XboxJoystickEvent e) {
    }

    public void rightXAxisMoved(XboxJoystickEvent e) {
    }

    public void rightYAxisMoved(XboxJoystickEvent e) {
        if (e.getSource() == secondary){
            shooter.adjustHeight(e.getData());
        }
    }

    public void padMoved(XboxJoystickEvent e) {
    }

    public void triggerMoved(XboxJoystickEvent e) {
        if (e.getSource() == secondary){
            if (Math.abs(e.getData()) <= 0.1){
                belts.stop();
            }
            
            else if (e.getData() > 0.0){
                belts.moveDown();
            }
            
            else {
                belts.moveUp();
            }
        }
    }
    
    public void valueChanged(PotentiometerEvent e) {
        System.out.println("potentiometer value changed: " + e.getData());
    }

}
