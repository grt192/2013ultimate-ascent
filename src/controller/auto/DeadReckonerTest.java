/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.auto;

import controller.DeadReckoner;
import core.GRTMacroController;
import macro.MacroDrive;
import macro.MacroTurn;
import mechanism.GRTDriveTrain;
import sensor.GRTGyro;

/**
 * A simple test for the DeadReckoner
 * @author Sidd Karamcheti
 */
public class DeadReckonerTest extends GRTMacroController {
    
    public DeadReckonerTest(GRTDriveTrain dt, GRTGyro gyro) {
        DeadReckoner dr = new DeadReckoner(0, 0, gyro.getAngle());
        
        this.addMacro(new MacroDrive(dt, 10, 5000));
        this.addMacro(new MacroTurn(dt, gyro, 45, 5000));
        
        
    }
    
}
