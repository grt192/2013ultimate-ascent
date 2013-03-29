package deploy;

import actuator.GRTSolenoid;
import controller.DriveController;
import controller.MechController;
import core.GRTConstants;
import core.GRTMacro;
import core.GRTMacroController;
import core.SensorPoller;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Victor;
import event.listeners.ConstantUpdateListener;
import java.util.Vector;
import logger.GRTLogger;
import macro.*;
import mechanism.Belts;
import mechanism.Climber;
import mechanism.ExternalPickup;
import mechanism.GRTDriveTrain;
import mechanism.Shooter;
import sensor.GRTBatterySensor;
import sensor.GRTEncoder;
import sensor.GRTGyro;
import sensor.GRTJoystick;
import sensor.GRTSwitch;
import sensor.GRTXboxJoystick;
import sensor.Potentiometer;

/**
 * Constructor for the main robot. Put all robot components here.
 *
 * @author ajc
 */
public class MainRobot extends GRTRobot implements ConstantUpdateListener {

    //Autonomous mode constants
    private static final int AUTO_MODE_DO_NOTHING = -1;
    private static final int AUTO_MODE_3_FRISBEE = 0;
    private static final int AUTO_MODE_5_FRISBEE = 1;
    private static final int AUTO_MODE_7_FRISBEE = 2;
    private static final int AUTO_MODE_DRIVE_CENTER_LEFT = 3;
    
    //Private i-vars.
    private GRTDriveTrain dt;
    private Belts belts;
    private Shooter shooter;
    private ExternalPickup ep;
    private Climber climber;
    private GRTGyro gyro;
    private GRTMacroController macroController;
    private int autoMode = AUTO_MODE_3_FRISBEE; //Default autonomous mode

    /**
     * Initializer for the robot. Calls an appropriate initialization function.
     */
    public MainRobot() {

        System.out.println("Robot being instantiated");

        if (GRTConstants.getValue("consoleOutput") == 0.0) {
            GRTLogger.disableLogging();
        }

        double robot = GRTConstants.getValue("robot");
        if (robot == 2013.2) {
            System.out.println("Starting up 2013 OmegaBot");
            omegaInit();
        }

    }

    public void disabled() {
        super.disabled();
        GRTLogger.logInfo("Disabling robot. Halting drivetrain");
        dt.setMotorSpeeds(0.0, 0.0);
    }

    /**
     * Initializer for omega bot.
     */
    private void omegaInit() {

        SensorPoller sp = new SensorPoller(10);     //Thread that polls all sensors every 10ms.

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
        GRTSolenoid leftShifter = new GRTSolenoid(getPinID("leftShifter"));
        GRTSolenoid rightShifter = new GRTSolenoid(getPinID("rightShifter"));

        // PWM outputs
        Talon leftDT1 = new Talon(getPinID("leftDT1"));
        Talon leftDT2 = new Talon(getPinID("leftDT2"));
        Talon rightDT1 = new Talon(getPinID("rightDT1"));
        Talon rightDT2 = new Talon(getPinID("rightDT2"));
        GRTLogger.logInfo("Motors initialized");

        double dtDistancePerPulse = GRTConstants.getValue("DTDistancePerPulse");
        //Mechanisms
        GRTEncoder leftEnc = new GRTEncoder(getPinID("encoderLeftA"),
                getPinID("encoderLeftB"),
                dtDistancePerPulse, true, "leftEnc");
        GRTEncoder rightEnc = new GRTEncoder(getPinID("encoderRightA"),
                getPinID("encoderRightB"),
                dtDistancePerPulse, false, "rightEnc");
        sp.addSensor(leftEnc);
        sp.addSensor(rightEnc);

        dt = new GRTDriveTrain(leftDT1, leftDT2, rightDT1, rightDT2,
                leftShifter, rightShifter,
                leftEnc, rightEnc);

        dt.setScaleFactors(
                GRTConstants.getValue("leftDT1Scale"),
                GRTConstants.getValue("leftDT2Scale"),
                GRTConstants.getValue("rightDT1Scale"),
                GRTConstants.getValue("rightDT2Scale"));

        DriveController dc = new DriveController(dt, leftPrimary, rightPrimary);

        addTeleopController(dc);

        //Compressor
        Compressor compressor = new Compressor(getPinID("compressorSwitch"),
                getPinID("compressorRelay"));
        compressor.start();
        System.out.println("pressure switch=" + compressor.getPressureSwitchValue());

        //shooter
        Talon shooter1 = new Talon(getPinID("shooter1"));
        Talon shooter2 = new Talon(getPinID("shooter2"));
        Talon shooterRaiser = new Talon(getPinID("shooterRaiser"));
        GRTSolenoid shooterFeeder = new GRTSolenoid(getPinID("shooterFeeder"));

        GRTEncoder shooterEncoder = new GRTEncoder(getPinID("shooterEncoderA"),
                getPinID("shooterEncoderB"),
                GRTConstants.getValue("shooterEncoderPulseDistance"),
                "shooterFlywheelEncoder");
        Potentiometer shooterPot = new Potentiometer(
                getPinID("shooterPotentiometer"),
                "shooter potentiometer");
        GRTSwitch lowerShooterLimit = new GRTSwitch(
                getPinID("shooterLowerLimit"),
                true, "lowerShooterLimit");

        shooter = new Shooter(shooter1, shooter2, shooterFeeder,
                shooterRaiser, shooterEncoder, shooterPot, lowerShooterLimit);

        sp.addSensor(shooterEncoder);
        sp.addSensor(shooterPot);

        //Belts
        System.out.println("belts = " + getPinID("belts"));
        System.out.println("rollerMotor = " + getPinID("rollerMotor"));
        System.out.println("raiserMotor = " + getPinID("raiserMotor"));

        Victor beltsMotor = new Victor(getPinID("belts"));

        belts = new Belts(beltsMotor);


        //PickerUpper
        SpeedController rollerMotor = new Victor(getPinID("rollerMotor"));
        SpeedController raiserMotor = new Victor(getPinID("raiserMotor"));
        GRTSwitch limitUp = new GRTSwitch(getPinID("pickUpUpperLimit"), false, "limitUp");
        GRTSwitch limitDown = new GRTSwitch(getPinID("pickUpLowerLimit"), false, "limitDown");
        sp.addSensor(limitUp);
        sp.addSensor(limitDown);

        ep = new ExternalPickup(rollerMotor, raiserMotor, limitUp, limitDown);

        //Climber
        GRTSolenoid climberSolenoid = new GRTSolenoid(getPinID("climberSolenoid"));
        climber = new Climber(climberSolenoid);

        System.out.println("Mechs created");

        //Mechcontroller
        MechController mechController = new MechController(leftPrimary, rightPrimary, secondary,
                shooter, ep, climber, belts, dt);

        addTeleopController(mechController);

        //Autonomous initializing
        gyro = new GRTGyro(1, "Turning Gyro");
        sp.addSensor(gyro);

        System.out.println("Start macro creation");
        defineAutoMacros();

        GRTConstants.addListener(this);

        sp.startPolling();
    }

    private int getAutonomousMode() {
        System.out.print("Auto mode: ");
        //Check the state of the buttons that are on.
        switch (getPinID("autoMode")) {
            case AUTO_MODE_3_FRISBEE:
                System.out.println("3 frisbee auto");
                return AUTO_MODE_3_FRISBEE;
            case AUTO_MODE_5_FRISBEE:
                System.out.println("5 frisbee auto");
                return AUTO_MODE_5_FRISBEE;
            case AUTO_MODE_7_FRISBEE:
                System.out.println("7 Frisbee auto");
                return AUTO_MODE_DRIVE_CENTER_LEFT;
            default:
                //We do nothing
                System.out.println("Auto Strategy: Do nothing");
                return AUTO_MODE_DO_NOTHING;
        }
    }

    private int getPinID(String name) {
        return (int) GRTConstants.getValue(name);
    }

    /**
     * Lays out definitions of each auto macro routine.
     * Based on the type of autonomous mode
     */
    private void defineAutoMacros() {
        clearAutoControllers();

        autoMode = getAutonomousMode(); //Get our autonomous mode

        Vector macros = new Vector();
        Vector concurrentMacros = new Vector();

        double autoShooterAngle = GRTConstants.getValue("autonomousAngle");
        double shootingSpeed = GRTConstants.getValue("shootingRPMS");
        double downAngle = GRTConstants.getValue("shooterDown");

        GRTLogger.logInfo("autoMode = " + autoMode);
        GRTLogger.logInfo("autoMode = " + autoMode);
        GRTLogger.logInfo("autoMode = " + autoMode);
        switch (autoMode) {
            case 10:
                GRTLogger.logInfo("Straight driving stuff");
                macros.addElement(new MacroDrive(dt, 3, 3000));
                macros.addElement(new MacroDrive(dt, -3, 3000));
                break;
            case AUTO_MODE_3_FRISBEE:
                // Macro version of autonomous
                macros.addElement(new ShooterSet(autoShooterAngle,
                        shootingSpeed, shooter, 5000));
                //Shoot our 3 frisbees. 
                //shoots 4 times in case hopper gets jammed
                for (int i = 0; i < 4; i++) {
                    macros.addElement(new Shoot(shooter, 1000));
                }
                //spins down shooter and lowers it prior to teleop
                macros.addElement(new ShooterSet(downAngle, 0, shooter, 1000));
                break;
            case AUTO_MODE_7_FRISBEE:
                double autoDriveDistance = GRTConstants.getValue("autoDistance");

                //lowers pickup
                GRTMacro lowerPickup = new LowerPickup(ep);
                macros.addElement(lowerPickup);
                concurrentMacros.addElement(lowerPickup);

                //primes shovel, spins up shooter and shoots 4x
                macros.addElement(new ShooterSet(autoShooterAngle,
                        shootingSpeed, shooter, 2500));
                for (int i = 0; i < 4; i++) {
                    macros.addElement(new Shoot(shooter, 500));
                }

                //lowers shooter and starts up EP as it starts driving
                ShooterSet lowerShooter = new ShooterSet(downAngle, 0, shooter, 3500);
                macros.addElement(lowerShooter);
                concurrentMacros.addElement(lowerShooter);
                AutoPickup startPickup = new AutoPickup(ep, belts, 300);
                macros.addElement(startPickup);
                concurrentMacros.addElement(startPickup);

                //spins around, drives over frisbees
                System.out.println("180 turn");
                macros.addElement(new MacroTurn(dt, gyro, 180, 2000));
                macros.addElement(new MacroDrive(dt, autoDriveDistance, 4000));

                //spins around and drives back, all while preparing shooter
                ShooterSet prepareSecondVolley =
                        new ShooterSet(autoShooterAngle,
                        shootingSpeed, shooter, 3500);
                macros.addElement(prepareSecondVolley);
                concurrentMacros.addElement(prepareSecondVolley);
                macros.addElement(new MacroTurn(dt, gyro, -180, 2000));
                macros.addElement(new MacroDrive(dt, autoDriveDistance, 4000));

                for (int i = 0; i < 5; i++) {
                    macros.addElement(new Shoot(shooter, 500));
                }
                //spins down shooter and lowers it prior to teleop
                macros.addElement(new ShooterSet(downAngle, 0, shooter, 1000));
                break;
            case AUTO_MODE_5_FRISBEE:    
                double autoDistance = 1.27;
                //lowers pickup
                GRTMacro lower = new LowerPickup(ep);
                macros.addElement(lower);
                concurrentMacros.addElement(lower);

                //primes shovel, spins up shooter and shoots 4x
                macros.addElement(new ShooterSet(autoShooterAngle,
                        shootingSpeed, shooter, 2500));
                for (int i = 0; i < 4; i++) {
                    macros.addElement(new Shoot(shooter, 500));
                }

                //lowers shooter and starts up EP as it starts driving
                ShooterSet lShooter = new ShooterSet(downAngle, 0, shooter, 3500);
                macros.addElement(lShooter);
                concurrentMacros.addElement(lShooter);
                AutoPickup pickup = new AutoPickup(ep, belts, 300);
                macros.addElement(pickup);
                concurrentMacros.addElement(pickup);

                //spins around, drives over frisbees
                System.out.println("180 turn");
                macros.addElement(new MacroTurn(dt, gyro, 180, 2000));
                macros.addElement(new MacroDrive(dt, autoDistance, 4000));

                //spins around and drives back, all while preparing shooter
                ShooterSet prepareVolley =
                        new ShooterSet(autoShooterAngle,
                        shootingSpeed, shooter, 3500);
                macros.addElement(prepareVolley);
                concurrentMacros.addElement(prepareVolley);
                macros.addElement(new MacroTurn(dt, gyro, -180, 2000));
                macros.addElement(new MacroDrive(dt, autoDistance, 4000));

                for (int i = 0; i < 5; i++) {
                    macros.addElement(new Shoot(shooter, 500));
                }
                //spins down shooter and lowers it prior to teleop
                macros.addElement(new ShooterSet(downAngle, 0, shooter, 1000));
                break;
                
            case AUTO_MODE_DRIVE_CENTER_LEFT:
                //Set the shooter angle
                macros.addElement(new ShooterSet(autoShooterAngle,
                        shootingSpeed, shooter, 5000));
                //Shoot our 3 frisbees.
                //shoots 4 times in case hopper gets jammed
                for (int i = 0; i < 4; i++) {
                    macros.addElement(new Shoot(shooter, 1000));
                }
                
                macros.addElement(new ShooterSet(downAngle, 0, shooter, 1000)); //spins down shooter and lowers it prior to teleop
                macros.addElement(new MacroDrive(dt, 2.54, 5000));      //Drive towards the driver 2.54m (100 inches. almost to the other side of the lines).
                macros.addElement(new MacroTurn(dt, gyro, 90, 3000));   //Turn to the right (the driver's left).
                macros.addElement((new MacroDrive(dt, 1.00, 2000)));    //Drive towards the leftmost edge of the field.
                break;
        }
        
        macroController = new GRTMacroController(macros);
        addAutonomousController(macroController);
    }

    public final void updateConstants() {
        defineAutoMacros();
    }
}
