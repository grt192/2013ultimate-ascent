/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.auto;

import core.GRTConstants;
import core.GRTMacro;
import core.GRTMacroController;
import macro.AutoPickup;
import macro.LowerPickup;
import macro.MacroDelay;
import macro.MacroDrive;
import macro.Shoot;
import macro.ShooterSet;
import mechanism.Belts;
import mechanism.ExternalPickup;
import mechanism.GRTDriveTrain;
import mechanism.Shooter;

/**
 *
 * @author Andrew Duffy <gerberduffy@gmail.com>
 */
public class SixFrisbeeAuto extends GRTMacroController {
    
    private double autoShooterAngle = GRTConstants.getValue("anglePyramidFrontPreset");
    private double shootingSpeed = GRTConstants.getValue("shootingRPMS");
    private double downAngle = GRTConstants.getValue("shooterDown");
    private double shooterDelay = GRTConstants.getValue("shooterDelay");

    private double autoDriveDistance = GRTConstants.getValue("auto6Distance");
    
    public SixFrisbeeAuto(GRTDriveTrain dt, Shooter shooter, Belts belts,
            ExternalPickup ep) {
        /**
         * Okay, six frisbee. Starting in the front of the pyramid,
         * we first start by firing our 2 starting frisbees.
         */
        System.out.println("6 Frisbee Centerline Autonomous Activated.");

        //lowers pickup
        GRTMacro lowerPickup = new LowerPickup(ep);
        addMacro(lowerPickup);

        //Sets up shooter angle and flywheel speed
        System.out.println("Setting shooter up to shoot ");
        addMacro(new ShooterSet(autoShooterAngle, shootingSpeed, shooter, 2500));
        addMacro(new MacroDelay((int)shooterDelay));

        //Shoot our 2 frisbees (3 shots in case of a misfire)
        addMacro(new Shoot(shooter, 500, 3));

        //lowers shooter and starts up EP as it starts driving
        ShooterSet lowerShooter = new ShooterSet(downAngle, 0, shooter, 3500);

        addMacro(lowerShooter);
        AutoPickup startPickup = new AutoPickup(ep, belts, 300);
        addMacro(startPickup);

        //drives over frisbees, comes back  
        addMacro(new MacroDrive(dt, autoDriveDistance, 4000));
        addMacro(new ShooterSet(autoShooterAngle, shootingSpeed, shooter, 3500));
        addMacro(new MacroDrive(dt, -autoDriveDistance, 4000));

        addMacro(new Shoot(shooter, 500, 3));
        
        //spins down shooter and lowers it prior to teleop
        addMacro(new ShooterSet(downAngle, 0, shooter, 1000));
    }
}
