package deploy;

import controller.DriveController;
import controller.TrackerController;
import core.GRTConstants;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.camera.AxisCamera;
import logger.GRTLogger;
import mechanism.GRTDriveTrain;
import sensor.GRTBatterySensor;
import sensor.GRTEncoder;
import sensor.GRTJoystick;
import sensor.GRTVisionTracker;

/**
 * Constructor for the main robot. Put all robot components here.
 *
 * @author andrew, dan, keshav, calvin, others
 */
public class MainRobot extends GRTRobot {

    private GRTDriveTrain dt;

    /**
     * Initializer for the robot. Calls an appropriate initialization function.
     */
    public MainRobot() {
        GRTLogger.logInfo("Robot version: " + GRTConstants.getValue("robot"));
        switch((int)GRTConstants.getValue("robot")){
            case 2012:
                GRTLogger.logInfo("Running 2012 base");
                base2012Init();
                break;
            case 2013:
                GRTLogger.logInfo("Running 2013 base");
                base2013Init();
                break;
        }

        GRTLogger.logInfo("Big G, Little O");
        GRTLogger.logInfo("Go Go Go!");
    }

    public void disabled() {
        GRTLogger.logInfo("Disabling robot. Halting drivetrain");
        dt.setMotorSpeeds(0.0, 0.0);
    }

    /**
     * Initializer for the 2013 robot.
     */
    private void base2013Init() {

        GRTLogger.logInfo("Base 2013: GRTFramework starting up.");

        //Driver station components
        GRTJoystick primary = new GRTJoystick(1, 12, "primary");
        GRTJoystick secondary =
                new GRTJoystick(2, 12, "secondary");
        primary.startPolling();
        secondary.startPolling();
        primary.enable();
        secondary.enable();
        GRTLogger.logInfo("Joysticks initialized");

        //Battery Sensor
        GRTBatterySensor batterySensor = new GRTBatterySensor(10, "battery");
        batterySensor.startPolling();
        batterySensor.enable();

        //Shifter solenoids
        Solenoid leftShifter = new Solenoid( (int)GRTConstants.getValue("leftSolenoid") );
        Solenoid rightShifter = new Solenoid( (int)GRTConstants.getValue("rightSolenoid") );

        //Compressor
        Compressor compressor = new Compressor(14, 1);
        compressor.start();

        // PWM outputs
        Victor leftDT1 = new Victor( (int)GRTConstants.getValue("leftDT1") );
        Victor leftDT2 = new Victor( (int)GRTConstants.getValue("leftDT2") );
        Victor rightDT1 = new Victor( (int)GRTConstants.getValue("rightDT1") );
        Victor rightDT2 = new Victor( (int)GRTConstants.getValue("rightDT2") );
        GRTLogger.logInfo("Motors initialized");

        // Encoders
        GRTEncoder leftEnc = new GRTEncoder( (int)GRTConstants.getValue("encoderLeftA"), 
                                             (int)GRTConstants.getValue("encoderLeftB"), 
                                             1, 50, "leftEnc");

        GRTEncoder rightEnc = new GRTEncoder( (int)GRTConstants.getValue("encoderRightA"), 
                                              (int)GRTConstants.getValue("encoderRightB"),
                                              1, 50, "rightEnc");

        
        leftEnc.enable();
        rightEnc.enable();
        leftEnc.startPolling();
        rightEnc.startPolling();

        GRTLogger.logInfo("Encoders initialized");

        //Mechanisms
        dt = new GRTDriveTrain(leftDT1, leftDT2,
                rightDT1, rightDT2, leftShifter, rightShifter, leftEnc, rightEnc);

        GRTLogger.logInfo("Mechanisms initialized");

        //Controllers
        DriveController dc =
                new DriveController(dt, primary, secondary);
        GRTLogger.logInfo("Controllers Initialized");

        addTeleopController(dc);

        GRTLogger.logSuccess("Ready to drive.");
    }

    /**
     * Initialize function for the 2012 base.
     */
    private void base2012Init() {
        GRTLogger.logInfo("2012 Base: GRTFramework starting up.");

        //Battery Sensor
        GRTBatterySensor batterySensor = new GRTBatterySensor(10, "battery");
        batterySensor.startPolling();
        batterySensor.enable();

        //Driver station components
        GRTJoystick joy1 = new GRTJoystick(1, 25, "Joystick");
        GRTJoystick joy2 = new GRTJoystick(2, 25, "Joystick");

        joy1.startPolling();
        joy1.enable();

        joy2.startPolling();
        joy2.enable();

        GRTLogger.logInfo("Joysticks initialized");

        // PWM outputs
        //TODO check motor pins
        Talon leftDT1 = new Talon(9);
        Talon leftDT2 = new Talon(10);
        Talon rightDT1 = new Talon(1);
        Talon rightDT2 = new Talon(2);
        GRTLogger.logInfo("Motors initialized");

        //Mechanisms
        dt = new GRTDriveTrain(leftDT1, leftDT2, rightDT1, rightDT2);
        dt.setScaleFactors(1, -1, -1, 1);

        AxisCamera cam = AxisCamera.getInstance();
        cam.writeResolution(AxisCamera.ResolutionT.k640x480);

        //If vision is enabled, add the Tracker autonomous controller.
        if (GRTConstants.getValue("vision") == 1.0){
            GRTVisionTracker tracker = new GRTVisionTracker(cam);
            tracker.startPolling(); //Allow it to wait for enable.

            TrackerController trackController = new TrackerController(tracker);

            addAutonomousController(trackController);
        }
        
        GRTLogger.logInfo("mechanisms intialized");
    }
}
