/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mechanism;

import core.GRTLoggedProcess;
import edu.wpi.first.wpilibj.SpeedController;

/**
 * Mechanism code for PickerUpper
 * @author Sidd
 */
public class PickerUpper extends GRTLoggedProcess{
    private SpeedController chalupaMotor;
    
    public PickerUpper(SpeedController chalupaMotor) {
        super("PickerUpper mech");
        this.chalupaMotor = chalupaMotor;
        
        //TODO
    }
    
    
    
    public void pickUp() {
     
    }
    
    public void spitOut() {
        //TODO
    }
    
    public void stop() {
        //TODO
    }
    
}
