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
import macro.MacroDrive;
import macro.MacroTurn;
import macro.Shoot;
import macro.ShooterSet;
import mechanism.Belts;
import mechanism.ExternalPickup;
import mechanism.GRTDriveTrain;
import mechanism.Shooter;
import sensor.GRTGyro;

/**
 * Five frisbee auto, goes to center of pyramid
 * @author Andrew Duffy <gerberduffy@gmail.com>
 */
public class FiveFrisbeeAuto extends GRTMacroController {

    private double autoShooterAngle = GRTConstants.getValue("anglePyramidCorner");
    private double shootingSpeed = GRTConstants.getValue("shootingRPMS");
    private double downAngle = GRTConstants.getValue("shooterDown");

    public FiveFrisbeeAuto(GRTDriveTrain dt, Shooter shooter, Belts belts,
            ExternalPickup ep, GRTGyro gyro) {
        /**
         * Okay, five frisbee. Starting in the back right corner of the pyramid,
         * we first start by firing our 3 starting frisbees.
         */
        System.out.println("30 Point Autonomous Activated.");

//                double autoDriveDistance = GRTConstants.getValue(10);
        double autoDriveDistance = GRTConstants.getValue("auto5Distance");    //Drive angled for 1.80m to pickup the two frisbees centered under the pyramid.
        double backupDistance = GRTConstants.getValue("backupDistance");
        double headingAngle = GRTConstants.getValue("headingAngle");
        //lowers pickup
        GRTMacro lowerPickup = new LowerPickup(ep);
        addMacro(lowerPickup);
//                concurrentMacros.addMacro(lowerPickup);

        //Sets up shooter angle and flywheel speed
        System.out.println("Setting shooter up to shoot ");
        addMacro(new ShooterSet(autoShooterAngle, shootingSpeed, shooter, 2500));

        //Shoot our 3 frisbees (4 shots in case of a misfire)
        for (int i = 0; i < 4; i++) {
            System.out.println("\tShooting a frisbee!");
            addMacro(new Shoot(shooter, 500));
        }

        //lowers shooter and starts up EP as it starts driving
        ShooterSet lowerShooter = new ShooterSet(downAngle, 0, shooter, 3500);
        addMacro(lowerShooter);
        AutoPickup startPickup = new AutoPickup(ep, belts, 300);
        addMacro(startPickup);

        //spins around, drives over frisbees, comes back  
        System.out.println("100-something degree turn");
        addMacro(new MacroDrive(dt, backupDistance, 3000));
        addMacro(new MacroTurn(dt, gyro, headingAngle, 2000));
        addMacro(new MacroDrive(dt, autoDriveDistance, 4000));
        addMacro(new MacroDrive(dt, -autoDriveDistance, 4000));
        addMacro(new MacroTurn(dt, gyro, -headingAngle, 2000));
        addMacro(new MacroDrive(dt, -backupDistance, 3000));

        for (int i = 0; i < 5; i++) {
            addMacro(new Shoot(shooter, 500));
        }
        //spins down shooter and lowers it prior to teleop
        addMacro(new ShooterSet(downAngle, 0, shooter, 1000));
    }
}
