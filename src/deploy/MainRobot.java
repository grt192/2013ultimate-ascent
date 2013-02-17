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
import sensor.Potentiometer;

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
        
        if (GRTConstants.getValue("consoleOutput") == 0.0){
            GRTLogger.disableLogging();
        }
        
        double robot = GRTConstants.getValue("robot");
        if (robot == 2012.0){
            System.out.println("Starting up 2012 Test Base");
            base2012Init();
        }
        if (robot == 2013.0){
            System.out.println("Starting up 2013 Test Base");
            base2013Init();
        }
        if (robot == 2013.1){
            System.out.println("Starting up 2013 BetaBot");
            omegaInit();
        }
        
    }

    public void disabled() {
        GRTLogger.logInfo("Disabling robot. Halting drivetrain");
        dt.setMotorSpeeds(0.0, 0.0);
    }

    /**
     * Initializer for omega bot.
     */
    private void omegaInit() {

        SensorPoller sp = new SensorPoller();
        
        GRTJoystick leftPrimary = new GRTJoystick(1, "left primary joy");
        GRTJoystick rightPrimary = new GRTJoystick(2, "right primary joy");
        GRTXboxJoystick secondary = new GRTXboxJoystick(3, "xbox mech joy");
        sp.addSensor(leftPrimary);
        sp.addSensor(rightPrimary);
        sp.addSensor(secondary);

        GRTLogger.logInfo("Joysticks initialized");

        //Battery Sensor
        GRTBatterySensor batterySensor = new GRTBatterySensor("battery");
        sp.addSensor(batterySensor);
        
        //Shifter solenoids
//        GRTSolenoid leftShifter = new GRTSolenoid(getPinID("leftSolenoid"));
//        GRTSolenoid rightShifter = new GRTSolenoid(getPinID("rightSolenoid"));

        // PWM outputs
        //TODO check motor pins
        Talon leftDT1 = new Talon(getPinID("leftDT1"));
        Talon leftDT2 = new Talon(getPinID("leftDT2"));
        Talon rightDT1 = new Talon(getPinID("rightDT1"));
        Talon rightDT2 = new Talon(getPinID("rightDT2"));
        GRTLogger.logInfo("Motors initialized");

        //Mechanisms
        GRTEncoder leftEnc = new GRTEncoder(getPinID("encoderLeftA"),
                getPinID("encoderLeftB"),
                1, 50, "leftEnc");
        GRTEncoder rightEnc = new GRTEncoder(getPinID("encoderRightA"),
                getPinID("encoderRightB"),
                1, 50, "rightEnc");
        sp.addSensor(leftEnc);
        sp.addSensor(rightEnc);
        
        GRTSolenoid leftShifter = new GRTSolenoid(getPinID("leftShifter"));
        GRTSolenoid rightShifter = new GRTSolenoid(getPinID("rightShifter"));
        
        
        
        dt = new GRTDriveTrain(leftDT1, leftDT2, rightDT1, rightDT2, leftShifter, rightShifter,
                leftEnc, rightEnc);

        dt.setScaleFactors(GRTConstants.getValue("leftDT1Scale"),
                GRTConstants.getValue("leftDT2Scale"),
                GRTConstants.getValue("rightDT1Scale"),
                GRTConstants.getValue("rightDT2Scale"));

        DriveController dc = new DriveController(dt, leftPrimary, rightPrimary);

        addTeleopController(dc);

        //Compressor
        Compressor compressor = new Compressor(getPinID("compressorSwitch"),
                getPinID("compressorRelay"));
        compressor.start();

        //shooter
        Victor shooter1 = new Victor(getPinID("shooter1"));
        Victor shooter2 = new Victor(getPinID("shooter2"));
        Victor shooterRaiser = new Victor(getPinID("shooterRaiser"));
        GRTSolenoid shooterFeeder = new GRTSolenoid(getPinID("shooterFeeder"));

        GRTEncoder shooterEncoder = new GRTEncoder(getPinID("shooterEncoderA"),
                getPinID("shooterEncoderB"), getPinID("shooterEncoderPulseDistance"), "shooterFlywheelEncoder");
        Potentiometer shooterPot = new Potentiometer(getPinID("shooterPotentiometer"),
                "shooter potentiometer");
        Shooter shooter = new Shooter(shooter1, shooter2, shooterFeeder,
                shooterRaiser, shooterEncoder, shooterPot);

        //Belts
        Victor beltsMotor = new Victor(getPinID("belts"));
        GRTSolenoid shovelLifter = new GRTSolenoid(getPinID("shovelLifter"));

        Belts belts = new Belts(beltsMotor, shovelLifter);



        //PickerUpper


        SpeedController rollerMotor = new Victor(getPinID("rollerMotor"));
        SpeedController raiserMotor = new Victor(getPinID("raiserMotor"));
        GRTSwitch limitUp = new GRTSwitch(getPinID("pickUpUpperLimit"), 50, false, "limitUp");
        GRTSwitch limitDown = new GRTSwitch(getPinID("pickUpLowerLimit"), 50, false, "limitDown");
        sp.addSensor(limitUp);
        sp.addSensor(limitDown);

        ExternalPickup youTiao = new ExternalPickup(rollerMotor, raiserMotor, limitUp, limitDown);

        //Climber


//        GRTSolenoid solenoid1 = new GRTSolenoid(getPinID("climberSolenoid1"));
//        GRTSolenoid solenoid2 = new GRTSolenoid(getPinID("climberSolenoid2"));
//        GRTSolenoid engager = doubleSolenoid.getSecondSolenoid();

//        Climber climber = new Climber(dt, solenoid1, solenoid2, engager);

        //Mechcontroller
        MechController mechController = new MechController(leftPrimary, rightPrimary, secondary,
                shooter, youTiao, null, belts, dt,
                GRTConstants.getValue("shooterPreset1"),
                GRTConstants.getValue("shooterPreset2"),
                GRTConstants.getValue("shooterPreset3"));

        addTeleopController(mechController);
        sp.startPolling();
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
        //GRTSolenoid leftShifter = new GRTSolenoid(getPinID("leftSolenoid"));
        //GRTSolenoid rightShifter = new GRTSolenoid(getPinID("rightSolenoid"));

        //Compressor
        Compressor compressor = new Compressor(getPinID("compressor"), 1);
        compressor.start();

        // PWM outputs
        Victor leftDT1 = new Victor(getPinID("leftDT1"));
        Victor leftDT2 = new Victor(getPinID("leftDT2"));
        Victor rightDT1 = new Victor(getPinID("rightDT1"));
        Victor rightDT2 = new Victor(getPinID("rightDT2"));
        GRTLogger.logInfo("Motors initialized");

        //Add to Test Mode
        LiveWindow.addActuator("DT", "leftDT1", leftDT1);
        LiveWindow.addActuator("DT", "leftDT2", leftDT2);
        LiveWindow.addActuator("DT", "rightDT1", rightDT1);
        LiveWindow.addActuator("DT", "rightDT2", rightDT2);

        // Encoders
//        GRTEncoder leftEnc = new GRTEncoder(getPinID("encoderLeftA"),
//                getPinID("encoderLeftB"),
//                1, 50, "leftEnc");
//        GRTEncoder rightEnc = new GRTEncoder(getPinID("encoderRightA"),
//                getPinID("encoderRightB"),
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
        
        DriveController dc = new DriveController(dt, primary, secondary);
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
        Talon leftDT1 = new Talon(getPinID("leftDT1"));
        Talon leftDT2 = new Talon(getPinID("leftDT2"));
        Talon rightDT1 = new Talon(getPinID("rightDT1"));
        Talon rightDT2 = new Talon(getPinID("rightDT2"));
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

    private int getPinID(String name) {
        return (int) GRTConstants.getValue(name);
    }
}
