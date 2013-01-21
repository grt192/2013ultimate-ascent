package deploy;

import controller.DriveController;
import core.EventController;
import core.GRTMacroController;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.camera.AxisCamera;
import java.util.Vector;
import logger.GRTLogger;
import macro.MacroDriveTrapezoidal;
import mechanism.GRTDriveTrain;
import sensor.GRTBatterySensor;
import sensor.GRTEncoder;
import sensor.GRTJoystick;

/**
 * Constructor for the main robot. Put all robot components here.
 *
 * @author ajc
 */
public class MainRobot extends GRTRobot {

    private GRTDriveTrain dt;

    /**
     * Initializer for the robot. Calls an appropriate initialization function.
     */
    public MainRobot() {

//        base2012Init();
        base2013Init();

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
        Solenoid leftShifter = new Solenoid(1);
        Solenoid rightShifter = new Solenoid(2);

        //Compressor
        Compressor compressor = new Compressor(14, 1);
        compressor.start();

        // PWM outputs
        Victor leftDT1 = new Victor(9);
        Victor leftDT2 = new Victor(10);
        Victor rightDT1 = new Victor(1);
        Victor rightDT2 = new Victor(2);
        GRTLogger.logInfo("Motors initialized");

        // Encoders
        GRTEncoder leftEnc = new GRTEncoder(1, 2, (Math.PI *3.5)/(12*360*2), 50, "leftEnc");
        GRTEncoder rightEnc = new GRTEncoder(3, 4, (Math.PI *3.5)/(12*360*2), 50, "rightEnc");

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
        MacroDriveTrapezoidal trap = new MacroDriveTrapezoidal(dt, -5, 10000, leftEnc, rightEnc, 2000, 2000);
        
        Vector macros = new Vector();
        macros.addElement(trap);
        EventController ac = new GRTMacroController(macros);
        addAutonomousController(ac);
        

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

        GRTLogger.logInfo("mechanisms intialized");
    }
}
