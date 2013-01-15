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
import mechanism.PickerUpper;
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
    private PickerUpper pickerUpper;
    private Shooter shooter;
    
    private double angularVelocity;
    private double shooterPreset1;
    private double shooterPreset2;
    private double shooterPreset3;
    
    public MechController(GRTJoystick leftJoy, GRTJoystick rightJoy,
            GRTJoystick secondaryJoy, ButtonBoard buttonBoard,
            Shooter shooter, PickerUpper pickerUpper,
            Climber climber, Belts belts) {
        super("Mechanism Controller");
        this.leftJoy = leftJoy;
        this.rightJoy = rightJoy;
        this.secondaryJoy = secondaryJoy;
        this.buttonBoard = buttonBoard;
        
        this.belts = belts;
        this.climber = climber;
        this.pickerUpper = pickerUpper;
        this.shooter = shooter;
        
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
        if (e.getSource() == secondaryJoy) {
            angularVelocity = e.getData();
             
        }
        shooter.setAngularSpeed(-angularVelocity);
    }

    public void AngleChanged(JoystickEvent e) {
    }

    
    public void buttonPressed(ButtonEvent e) {
        if (e.getSource() == rightJoy) {
            if (e.getButtonID() == GRTJoystick.KEY_BUTTON_3) {
                pickerUpper.pickUp();
            }
            else if (e.getButtonID() == GRTJoystick.KEY_BUTTON_2) {
                pickerUpper.spitOut();
            }
        }
        
        else if (e.getSource() == leftJoy) {
            if (e.getButtonID() == GRTJoystick.KEY_BUTTON_TRIGGER) {
                pickerUpper.lower();
            }
        }
        
        else if (e.getSource() == buttonBoard) {
            switch (e.getButtonID()) {
                case ButtonBoard.KEY_BUTTON1: shooter.setSpeed(shooterPreset1);
                    break;
                case ButtonBoard.KEY_BUTTON2: shooter.setSpeed(shooterPreset2);
                    break;
                case ButtonBoard.KEY_BUTTON3: shooter.setSpeed(shooterPreset3);
                    break;
                case ButtonBoard.KEY_BUTTON4: climber.popWheelie();
                    break;
                case ButtonBoard.KEY_BUTTON5: climber.climb();
                    break;
            }
        }
        
        else if (e.getSource() == secondaryJoy) {
            switch (e.getButtonID()) {
                case GRTJoystick.KEY_BUTTON_TRIGGER: shooter.shoot();
                    break;
                case GRTJoystick.KEY_BUTTON_2: belts.moveUp();
                    break;
                case GRTJoystick.KEY_BUTTON_3: belts.moveDown();
                    break;
            }
        }
    }

    public void buttonReleased(ButtonEvent e) {
        if (e.getSource() == leftJoy) {
            if (e.getButtonID() == GRTJoystick.KEY_BUTTON_TRIGGER) {
                pickerUpper.raise();
            }
        }
    }

    public void valueChanged(PotentiometerEvent e) {
        //TODO set trim for auto tracking
    }
}
