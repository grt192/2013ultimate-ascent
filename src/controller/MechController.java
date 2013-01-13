package controller;

import event.events.ButtonBoardEvent;
import event.events.JoystickEvent;
import event.listeners.ButtonBoardListener;
import event.listeners.GRTJoystickListener;
import sensor.GRTJoystick;
import mechanism.Belts;
import mechanism.Climber;
import mechanism.PickerUpper;
import mechanism.Shooter;
import sensor.ButtonBoard;

/**
 * Controller for shooter, picker-upper, internal belts, climbing
 * 
 * @author Calvin
 */
public class MechController implements GRTJoystickListener, ButtonBoardListener {
    
    private GRTJoystick leftJoy;
    private GRTJoystick rightJoy;
    private GRTJoystick secondaryJoy;
    private ButtonBoard buttonBoard;
    
    private Belts belts;
    private Climber climber;
    private PickerUpper pickerUpper;
    private Shooter shooter;
    
    public MechController(GRTJoystick leftJoy, GRTJoystick rightJoy,
            GRTJoystick secondaryJoy, ButtonBoard buttonBoard,
            Shooter shooter, PickerUpper pickerUpper,
            Climber climber, Belts belts) {
        this.leftJoy = leftJoy;
        this.rightJoy = rightJoy;
        this.secondaryJoy = secondaryJoy;
        this.buttonBoard = buttonBoard;
        
        this.belts = belts;
        this.climber = climber;
        this.pickerUpper = pickerUpper;
        this.shooter = shooter;
    }

    public void XAxisMoved(JoystickEvent e) {
    }

    public void YAxisMoved(JoystickEvent e) {
    }

    public void AngleChanged(JoystickEvent e) {
    }

    public void buttonPressed(ButtonBoardEvent e) {
    }

    public void buttonReleased(ButtonBoardEvent e) {
    }

    public void potentiometerChange(ButtonBoardEvent e) {
    }
}
