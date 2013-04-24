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
import macro.MacroTurn;
import macro.Shoot;
import macro.ShooterSet;
import mechanism.Belts;
import mechanism.ExternalPickup;
import mechanism.GRTDriveTrain;
import mechanism.Shooter;
import sensor.GRTGyro;

/**
 *
 * @author Andrew Duffy <gerberduffy@gmail.com>
 * @author Calvin Huang <clhuang@eneron.us>
 * @author Sidd Karamcheti <sidd.karamcheti@gmail.com>
 */
public class WingAuto extends GRTMacroController {

    private double autoShooterAngle = GRTConstants.getValue("anglePyramidBackCenter");
    private double shootingSpeed = GRTConstants.getValue("shootingRPMS");
    private double downAngle = GRTConstants.getValue("shooterDown");
    private double shooterDelay = GRTConstants.getValue("shooterDelay");

    public WingAuto(GRTDriveTrain dt, Shooter shooter, Belts belts,
            ExternalPickup ep, GRTGyro gyro) {
        /**
         * Starting in the back left corner of the pyramid, we first start by
         * firing our 3 starting frisbees. Note: We start facing straight, and
         * then turn to shoot
         */
        System.out.println("Tony's super unrealistic centerline-wing auto activated");

        //lowers pickup
        GRTMacro lowerPickup = new LowerPickup(ep);
        addMacro(lowerPickup);

        //Sets up shooter angle and flywheel speed
        System.out.println("Setting shooter up to shoot ");

        //Turns into shooting position (corner angle)
        double cornerAngle = GRTConstants.getValue("CornerAngle");
        addMacro(new MacroTurn(dt, gyro, cornerAngle, 2000));

        //Shoot our 3 frisbees (4 shots in case of a misfire)
        addMacro(new Shoot(shooter, 500, 4));

        //lowers shooter and starts up EP as it starts driving
        ShooterSet lowerShooter = new ShooterSet(downAngle, 0, shooter, 3500);
        addMacro(lowerShooter);

        AutoPickup startPickup = new AutoPickup(ep, belts, 300);
        addMacro(startPickup);

        //Important Constants
        double wingDriveAngle = GRTConstants.getValue("WingDriveAngle"); // Angle to turn towards centerline
        double wingDriveDistance = GRTConstants.getValue("WingDriveDistance"); // Distance to drive to get to centerline

        double centerPickupAngle = GRTConstants.getValue("centerPickupAngle");  //Absolute angle to center ourselves along the line
        double pickupFrisbeesDriveDistance = GRTConstants.getValue("pickupFrisbeesDriveDistance");  //How far across the center line we want to drive

        //Not really necessary, should be set to 90¼ regardless
        double centerTurnAngle = GRTConstants.getValue("CenterTurnAngle"); // Absolute angle to get to back center of pyramid
        double returnDriveDistance = GRTConstants.getValue("ReturnDriveDistance"); //Distance to return to pyramid
        //

        //Begin to attempt Wing Pickup
        System.out.println("Attempting Wing Pickup!!!");
        System.out.println("Attempting Wing Pickup!!!");
        System.out.println("Attempting Wing Pickup!!!");

        //Turn into wing angle and drive to centerline
        addMacro(new MacroTurn(dt, gyro, wingDriveAngle - cornerAngle, 2000)); //Takes care of turning back from original 3 shots, and turning into wing
        addMacro(new MacroDrive(dt, wingDriveDistance, 5000));

        //Turns into centerline, and starts to drive and pickup frisbees
        addMacro(new MacroTurn(dt, gyro, centerPickupAngle, 3000));
        addMacro(new MacroDrive(dt, pickupFrisbeesDriveDistance, 6000));

        //Turns and drives back to pyramid, while raising shooter
        addMacro(new ShooterSet(autoShooterAngle, shootingSpeed, shooter, 2500)); //start moving shooter back
        addMacro(new MacroDelay((int) shooterDelay));

        addMacro(new MacroTurn(dt, gyro, centerTurnAngle, 3000));
        addMacro(new MacroDrive(dt, returnDriveDistance, 5000));

        //Shoots 4 frisbees (5 attempts)
        addMacro(new Shoot(shooter, 500, 5));

        //Drops shooter down for teleop
        addMacro(new ShooterSet(downAngle, 0, shooter, 1000));

//        addMacro(new MacroTurn(dt, gyro, DeadReckoner.turnAngle(centerlineDriveAngle), 3000));  //Turn to the angle that gets us to the left side of the field.
//        addMacro(new MacroDrive(dt, driveToCenter, 5000)); //Drive over to the center line
//        addMacro(new MacroTurn(dt, gyro, DeadReckoner.turnAngle(centerPickupAngle), 2000));   //Turn to the frisbees
//        addMacro(new MacroDrive(dt, pickupFrisbeesDriveDistance, 3000));//Pickup some frisbees. Change distance based on frisbees on field.
//        
//        addMacro(new ShooterSet(autoShooterAngle, shootingSpeed, shooter, 2500)); //start moving shooter back
//        addMacro(new MacroDelay((int)shooterDelay));
//        
//        double backToPyramidAngle = DeadReckoner.angleFrom(startingX, startingY);
//        addMacro(new MacroTurn(dt, gyro, backToPyramidAngle, 2000));    //Turn back to the pyramid
//        
//        double backToPyramidDistance = DeadReckoner.distanceFrom(startingX, startingY);
//        addMacro(new MacroDrive(dt, backToPyramidDistance, 5000));  //Drive back to the pyramid
//        
//        double turnToShoot = DeadReckoner.turnAngle(startingAngle); //Find the angle we need to turn back to to shoot.
//        addMacro(new MacroTurn(dt, gyro, turnToShoot, 2000));

    }
}
