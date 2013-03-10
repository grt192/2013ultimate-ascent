package deploy;

import actuator.GRTSolenoid;
import controller.DriveController;
import controller.MechController;
import core.GRTConstants;
import core.SensorPoller;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import logger.GRTLogger;
import mechanism.Belts;
import mechanism.ExternalPickup;
import mechanism.GRTDriveTrain;
import mechanism.Shooter;
import sensor.GRTBatterySensor;
import sensor.GRTEncoder;
import sensor.GRTJoystick;
import sensor.GRTSwitch;
import sensor.GRTXboxJoystick;

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
        
        System.out.println("Robot being instantiated");
        
        boolean consoleOut;
        try {
            consoleOut = ( GRTConstants.getValue("consoleOutput") == 1.0 );
        } catch (Exception ex){
            GRTLogger.logError("consoleOutput key not found in constants file. Maybe you'd like to add it?");
            consoleOut = true;
            GRTLogger.disableLogging();
        }
        
        if (!consoleOut){
            GRTLogger.disableLogging();
        }
        
        double robot = GRTConstants.getValue("robot");
        if (robot == 2012.0){
            GRTLogger.logInfo("Starting up 2012 Test Base");
            base2012Init();
        }
        if (robot == 2013.0){
            GRTLogger.logInfo("Starting up 2013 Test Base");
            base2013Init();
        }
        
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
        SensorPoller sp = new SensorPoller();

        //Driver station components
        GRTJoystick primary = new GRTJoystick(1, "primary");
        GRTJoystick secondary =
                new GRTJoystick(2, "secondary");
        sp.addSensor(primary);
        sp.addSensor(secondary);
 
        GRTLogger.logInfo("Joysticks initialized");

        //Battery Sensor
        GRTBatterySensor batterySensor = new GRTBatterySensor("battery");
        sp.addSensor(batterySensor);

        //Shifter solenoids
        //GRTSolenoid leftShifter = new GRTSolenoid((int) GRTConstants.getValue("leftSolenoid"));
        //GRTSolenoid rightShifter = new GRTSolenoid((int) GRTConstants.getValue("rightSolenoid"));

        //Compressor
        Compressor compressor = new Compressor((int) GRTConstants.getValue("compressor"), 1);
        compressor.start();

        // PWM outputs
        Victor leftDT1 = new Victor((int) GRTConstants.getValue("leftDT1"));
        Victor leftDT2 = new Victor((int) GRTConstants.getValue("leftDT2"));
        Victor rightDT1 = new Victor((int) GRTConstants.getValue("rightDT1"));
        Victor rightDT2 = new Victor((int) GRTConstants.getValue("rightDT2"));
        GRTLogger.logInfo("Motors initialized");

        //Add to Test Mode
        LiveWindow.addActuator("DT", "leftDT1", leftDT1);
        LiveWindow.addActuator("DT", "leftDT2", leftDT2);
        LiveWindow.addActuator("DT", "rightDT1", rightDT1);
        LiveWindow.addActuator("DT", "rightDT2", rightDT2);

        // Encoders
//        GRTEncoder leftEnc = new GRTEncoder((int) GRTConstants.getValue("encoderLeftA"),
//                (int) GRTConstants.getValue("encoderLeftB"),
//                1, 50, "leftEnc");
//        GRTEncoder rightEnc = new GRTEncoder((int) GRTConstants.getValue("encoderRightA"),
//                (int) GRTConstants.getValue("encoderRightB"),
//                1, 50, "rightEnc");
//        sp.addSensor(leftEnc);
//        sp.addSensor(rightEnc);

        GRTLogger.logInfo("Encoders initialized");

        //Mechanisms
        dt = new GRTDriveTrain(leftDT1, leftDT2,
                rightDT1, rightDT2);

        GRTLogger.logInfo("Setting the scale factors!");
        dt.setScaleFactors(GRTConstants.getValue("leftDT1Scale"),
                GRTConstants.getValue("leftDT2Scale"),
                GRTConstants.getValue("rightDT1Scale"),
                GRTConstants.getValue("rightDT2Scale"));

        GRTLogger.logInfo("Mechanisms initialized");

        //Controllers
//        MacroDriveTrapezoidal trap = new MacroDriveTrapezoidal(dt, -5, 10000, leftEnc, rightEnc, 2000, 2000);
//        
//        Vector macros = new Vector();
//        macros.addElement(trap);
//        EventController ac = new GRTMacroController(macros);
//        addAutonomousController(ac);


        DriveController dc =
                new DriveController(dt, primary, secondary);

        GRTLogger.logInfo("Controllers Initialized");

        addTeleopController(dc);
        sp.startPolling();

        GRTLogger.logSuccess("Ready to drive.");
    }

    /**
     * Initialize function for the 2012 base.
     */
    private void base2012Init() {
        GRTLogger.logInfo("2012 Base: GRTFramework starting up.");
        SensorPoller sp = new SensorPoller();

        //Battery Sensor
        GRTBatterySensor batterySensor = new GRTBatterySensor("battery");
        sp.addSensor(batterySensor);

        //Driver station components
        GRTJoystick joy1 = new GRTJoystick(1, "Joystick");
        GRTJoystick joy2 = new GRTJoystick(2, "Joystick");
        sp.addSensor(joy1);
        sp.addSensor(joy2);
        
        GRTLogger.logInfo("Joysticks initialized");

        // PWM outputs
        //TODO check motor pins
        Talon leftDT1 = new Talon((int) GRTConstants.getValue("leftDT1"));
        Talon leftDT2 = new Talon((int) GRTConstants.getValue("leftDT2"));
        Talon rightDT1 = new Talon((int) GRTConstants.getValue("rightDT1"));
        Talon rightDT2 = new Talon((int) GRTConstants.getValue("rightDT2"));
        GRTLogger.logInfo("Motors initialized");

        //Mechanisms
        dt = new GRTDriveTrain(leftDT1, leftDT2, rightDT1, rightDT2);
        dt.setScaleFactors(1, -1, -1, 1);


        DriveController dc = new DriveController(dt, joy1, joy2);

        addTeleopController(dc);
        sp.startPolling();

        GRTLogger.logInfo("Big G, Litte O");
        Timer.delay(.2);
        GRTLogger.logInfo("Go");
        Timer.delay(.4);
        GRTLogger.logInfo("Go");
        Timer.delay(.4);
        GRTLogger.logInfo("Go!");
    }

    public void test() {
        while (isTest() && isEnabled()) {
            LiveWindow.run();
            Timer.delay(.1);
        }
    }
}
