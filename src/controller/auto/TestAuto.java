/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.auto;

import core.GRTConstants;
import core.GRTMacroController;
import macro.MacroDrive;
import macro.MacroTurn;
import mechanism.GRTDriveTrain;
import sensor.GRTGyro;

/**
 *
 * @author Andrew Duffy <gerberduffy@gmail.com>
 */
public class TestAuto extends GRTMacroController {
    
    public TestAuto(GRTDriveTrain dt, GRTGyro g){
        addMacro( new MacroDrive(dt, GRTConstants.getValue("testShitFax"), 3500));
//        addMacro( new MacroTurn(dt, g, 10.0, 1000));
    }
    
}
