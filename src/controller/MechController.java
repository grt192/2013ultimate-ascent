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

    private double shooterPreset1;
    private double shooterPreset2;
    private double shooterPreset3;

    private double turningDivider;
    private double adjustDivider;

    private boolean leftShoulderHeld = false, leftTriggerHeld = false;  //Variables useful for collection logic.

    public MechController(GRTJoystick leftJoy, GRTJoystick rightJoy,
            GRTXboxJoystick secondary,
            Shooter shooter, ExternalPickup pickerUpper,
            Climber climber, Belts belts,
            GRTDriveTrain dt,
            double preset1, double preset2, double preset3) {
        super("Mechanism Controller");
        this.leftJoy = leftJoy;
        this.rightJoy = rightJoy;
        this.secondary = secondary;

        this.belts = belts;
        this.climber = climber;
        this.pickerUpper = pickerUpper;
        this.shooter = shooter;

        this.dt = dt;

        this.shooterPreset1 = preset1;
        this.shooterPreset2 = preset2;
        this.shooterPreset3 = preset3;
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
            logInfo("Button Pressed: " + e.getID());
            if (e.getSource() == rightJoy) {
                switch (e.getButtonID()) {
                    case GRTJoystick.KEY_BUTTON_3: 
                        pickerUpper.pickUp();
                        belts.moveUp();
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
                        logError("X: Angle adjustment #1 will be here.");
                        shooter.setSpeed(shooterPreset1);
                        break;
                    case GRTXboxJoystick.KEY_BUTTON_A:
                        logError("A: Angle adjustment #2 will be here.");
                        shooter.setSpeed(shooterPreset2);
                        break;
                    case GRTXboxJoystick.KEY_BUTTON_B:
                        logError("B: Angle adjustment #3 will be here.");
                        shooter.setSpeed(shooterPreset3);
                        break;
                    case GRTXboxJoystick.KEY_BUTTON_LEFT_SHOULDER:
                        leftShoulderHeld = true;
                        belts.moveUp();
                        break;

                    case GRTXboxJoystick.KEY_BUTTON_RIGHT_SHOULDER:
                        logInfo("Right shoulder preseed, shooting!");
                        shooter.shoot();
                        break;
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
            }
        }

        else if (e.getSource() == rightJoy) {
            switch (e.getButtonID()) {
                case GRTJoystick.KEY_BUTTON_3: 
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
                    shooter.setSpeed(0.0);
                    break;
                case GRTXboxJoystick.KEY_BUTTON_A:
                    shooter.setSpeed(0.0);
                    break;
                case GRTXboxJoystick.KEY_BUTTON_B:
                    shooter.setSpeed(0.0);
                    break;
                case GRTXboxJoystick.KEY_BUTTON_LEFT_SHOULDER:
                    leftShoulderHeld = false;
                    if (!leftShoulderHeld && !leftTriggerHeld){
                        belts.stop();
                    } else {
                        belts.moveDown();
                    }
                    break;
                case GRTXboxJoystick.KEY_BUTTON_RIGHT_SHOULDER:
                    logInfo("Right shoulder released!");
                    shooter.unShoot();
                    break;
            }
        }
    }

    public void valueChanged(PotentiometerEvent e) {
        //TODO set trim for auto tracking
    }

    public void leftXAxisMoved(XboxJoystickEvent e) {
        //Use Xbox left axis to make fine adjustments to the robot's directional heading.
        logInfo("Left x axis moved!");
        if (e.getSource() == secondary){
            logInfo("Slowly turning dt's");
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
            logInfo("Adjusting luna height.");
            shooter.adjustHeight(e.getData() / adjustDivider);
        }
    }

    public void padMoved(XboxJoystickEvent e) {
    }

    public void triggerMoved(XboxJoystickEvent e) {
        if (e.getSource() == secondary){
            if (e.getData() > 0.0){
                leftTriggerHeld = true;
                belts.moveDown();
            } else if (e.getData() == 0.0){
                leftTriggerHeld = false;
                if (!leftTriggerHeld && !leftShoulderHeld){
                    belts.stop();
                } else {
                    belts.moveUp();
                }
            }
        }
    }
}