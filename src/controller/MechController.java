package controller;

import core.EventController;
import event.events.ButtonEvent;
import event.events.JoystickEvent;
import event.events.PotentiometerEvent;
import event.listeners.ButtonListener;
import event.listeners.GRTJoystickListener;
import event.listeners.PotentiometerListener;
import mechanism.Belts;
import mechanism.Climber;
import mechanism.ExternalPickup;
import mechanism.Shooter;
import sensor.ButtonBoard;
import sensor.GRTJoystick;

/**
 * Controller for shooter, picker-upper, internal belts, climbing
 *
 * @author Calvin
 */
public class MechController extends EventController implements GRTJoystickListener, PotentiometerListener, ButtonListener {

    private GRTJoystick leftJoy;
    private GRTJoystick rightJoy;
    private GRTJoystick secondaryJoy;
    private ButtonBoard buttonBoard;
    private Belts belts;
    private Climber climber;
    private ExternalPickup pickerUpper;
    private Shooter shooter;
    private double shooterPreset1;
    private double shooterPreset2;
    private double shooterPreset3;

    public MechController(GRTJoystick leftJoy, GRTJoystick rightJoy,
            GRTJoystick secondaryJoy, ButtonBoard buttonBoard,
            Shooter shooter, ExternalPickup pickerUpper,
            Climber climber, Belts belts,
            double preset1, double preset2, double preset3) {
        super("Mechanism Controller");
        this.leftJoy = leftJoy;
        this.rightJoy = rightJoy;
        this.secondaryJoy = secondaryJoy;
        this.buttonBoard = buttonBoard;

        this.belts = belts;
        this.climber = climber;
        this.pickerUpper = pickerUpper;
        this.shooter = shooter;

        this.shooterPreset1 = preset1;
        this.shooterPreset2 = preset2;
        this.shooterPreset3 = preset3;
    }

    protected void startListening() {
        leftJoy.addJoystickListener(this);
        leftJoy.addButtonListener(this);

        rightJoy.addJoystickListener(this);
        rightJoy.addButtonListener(this);

        secondaryJoy.addJoystickListener(this);
        secondaryJoy.addButtonListener(this);

        buttonBoard.addButtonListener(this);
    }

    protected void stopListening() {
        leftJoy.removeJoystickListener(this);
        leftJoy.removeButtonListener(this);

        rightJoy.removeJoystickListener(this);
        rightJoy.removeButtonListener(this);

        secondaryJoy.removeJoystickListener(this);
        secondaryJoy.removeButtonListener(this);

        buttonBoard.removeButtonListener(this);
    }

    public void XAxisMoved(JoystickEvent e) {
    }

    public void YAxisMoved(JoystickEvent e) {
        if (e.getSource().equals(secondaryJoy) &&
                secondaryJoy.getState(GRTJoystick.KEY_BUTTON_TRIGGER) ==
                GRTJoystick.TRUE)
            shooter.raise(e.getData());
    }

    public void AngleChanged(JoystickEvent e) {
    }

    //commented out code is because betabot is FUBAR
    public void buttonPressed(ButtonEvent e) {
        try {
            logInfo("Button Pressed: " + e.getID());
            if (e.getSource() == rightJoy) {
                if (e.getButtonID() == GRTJoystick.KEY_BUTTON_3) {
                    pickerUpper.pickUp();
                } else if (e.getButtonID() == GRTJoystick.KEY_BUTTON_2) {
                    pickerUpper.spitOut();
                }
            } else if (e.getSource() == leftJoy) {
                if (e.getButtonID() == GRTJoystick.KEY_BUTTON_TRIGGER) {
                    logInfo("pickerupper lower");
                    pickerUpper.lower();
                }
            }

            if (e.getButtonID() == GRTJoystick.KEY_BUTTON_7) {
                shooter.setSpeed(1.0);
            } else if (e.getSource() == buttonBoard) {
                switch (e.getButtonID()) {
                    case ButtonBoard.KEY_BUTTON1:
                        shooter.setSpeed(shooterPreset1);
                        break;
                    case ButtonBoard.KEY_BUTTON2:
                        shooter.setSpeed(shooterPreset2);
                        break;
                    case ButtonBoard.KEY_BUTTON3:
                        shooter.setSpeed(shooterPreset3);
                        break;
//                case ButtonBoard.KEY_BUTTON5: climber.climb();
//                    break;
                }
            } else if (e.getSource() == secondaryJoy) {
                switch (e.getButtonID()) {
                    case GRTJoystick.KEY_BUTTON_TRIGGER:
                        shooter.shoot();
                        logInfo("shoot shooter");
                        break;
                    case GRTJoystick.KEY_BUTTON_2:
                        belts.moveUp();
                        logInfo("belts move up");
                        break;
                    case GRTJoystick.KEY_BUTTON_3:
                        belts.moveDown();
                        logInfo("belts move down");
                        break;
                }
            }
        } catch (NullPointerException _) {
            _.printStackTrace();
        }
    }

    public void buttonReleased(ButtonEvent e) {
        if (e.getSource() == leftJoy) {
            if (e.getButtonID() == GRTJoystick.KEY_BUTTON_TRIGGER) {
                logInfo("pickerupper raise");
                pickerUpper.raise();
            }
        } else if (e.getSource() == rightJoy) {
            if (e.getButtonID() == GRTJoystick.KEY_BUTTON_3) {
                logInfo("stop roller");
                pickerUpper.stopRoller();
            } else if (e.getButtonID() == GRTJoystick.KEY_BUTTON_2) {
                logInfo("stop roller");
                pickerUpper.stopRoller();
            }
        } else if (e.getSource() == secondaryJoy) {
            switch (e.getButtonID()) {
                case GRTJoystick.KEY_BUTTON_2:
                    belts.stop();
                    logInfo("belts stop");
                    break;
                case GRTJoystick.KEY_BUTTON_3:
                    belts.stop();
                    logInfo("belts stop");
                    break;
                case GRTJoystick.KEY_BUTTON_TRIGGER:
                    shooter.raise(0);
                    break;
            }
        } else if (e.getSource() == buttonBoard) {
            switch (e.getButtonID()) {
                case ButtonBoard.KEY_BUTTON1:
                    shooter.setSpeed(0.0);
                    break;
                case ButtonBoard.KEY_BUTTON2:
                    shooter.setSpeed(0.0);
                    break;
                case ButtonBoard.KEY_BUTTON3:
                    shooter.setSpeed(0.0);
                    break;
            }
        }

    }

    public void valueChanged(PotentiometerEvent e) {
        //TODO set trim for auto tracking
    }
}
