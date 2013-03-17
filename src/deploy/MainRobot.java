package deploy;

import actuator.GRTSolenoid;
import controller.DriveController;
import controller.MechController;
import core.GRTConstants;
import core.GRTMacroController;
import core.SensorPoller;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStationEnhancedIO;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Victor;
import java.util.Vector;
import logger.GRTLogger;
import macro.*;
import mechanism.Belts;
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
public class MainRobot extends GRTRobot {

    private static final int AUTO_MODE_DO_NOTHING= -1;
    private static final int AUTO_MODE_3_FRISBEE = 0;
    private static final int AUTO_MODE_7_FRISBEE = 1;
    
    private GRTDriveTrain dt;
    private int autoMode = AUTO_MODE_3_FRISBEE; //Default autonomous mode
    
    /**
     * Initializer for the robot. Calls an appropriate initialization function.
     */
    public MainRobot() {
        
        System.out.println("Robot being instantiated");
        
        if (GRTConstants.getValue("consoleOutput") == 0.0){
            GRTLogger.disableLogging();
        }
        
        double robot = GRTConstants.getValue("robot");
        if (robot == 2013.2){
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
        //TODO check motor pins
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
        System.out.println("compressorSwitch = " + getPinID("compressorSwitch"));
        Compressor compressor = new Compressor(1,1);        //They should be the same...HACK
        compressor.start();
        System.out.println("pressure switch="+compressor.getPressureSwitchValue());

        //shooter
        Talon shooter1 = new Talon(getPinID("shooter1"));
        Talon shooter2 = new Talon(getPinID("shooter2"));
        Talon shooterRaiser = new Talon(getPinID("shooterRaiser"));
        GRTSolenoid shooterFeeder = new GRTSolenoid(getPinID("shooterFeeder"));
        
        GRTEncoder shooterEncoder = new GRTEncoder(getPinID("shooterEncoderA"),
                getPinID("shooterEncoderB"), GRTConstants.getValue("shooterEncoderPulseDistance"), "shooterFlywheelEncoder");
        Potentiometer shooterPot = new Potentiometer(getPinID("shooterPotentiometer"),
                "shooter potentiometer");
        
        GRTSwitch lowerShooterLimit = new GRTSwitch(getPinID("shooterLowerLimit"),
                true, "lowerShooterLimit");
        
        Shooter shooter = new Shooter(shooter1, shooter2, shooterFeeder,
                shooterRaiser, shooterEncoder, shooterPot, lowerShooterLimit);

        sp.addSensor(shooterEncoder);
        sp.addSensor(shooterPot);
       
        //Belts
        System.out.println("belts = " + getPinID("belts"));
        System.out.println("shovelLifter = " + getPinID("shovelLifter"));
        System.out.println("rollerMotor = " + getPinID("rollerMotor"));
        System.out.println("raiserMotor = " + getPinID("raiserMotor"));
        
        Victor beltsMotor = new Victor(getPinID("belts"));
        GRTSolenoid shovelLifter = new GRTSolenoid(getPinID("shovelLifter"));

        Belts belts = new Belts(beltsMotor, shovelLifter);


        //PickerUpper
        SpeedController rollerMotor = new Victor(getPinID("rollerMotor"));
        SpeedController raiserMotor = new Victor(getPinID("raiserMotor"));
        GRTSwitch limitUp = new GRTSwitch(getPinID("pickUpUpperLimit"), false, "limitUp");
        GRTSwitch limitDown = new GRTSwitch(getPinID("pickUpLowerLimit"), false, "limitDown");
        sp.addSensor(limitUp);
        sp.addSensor(limitDown);

        ExternalPickup youTiao = new ExternalPickup(rollerMotor, raiserMotor, limitUp, limitDown);
        
        System.out.println("Mechs created");

        //Mechcontroller
        MechController mechController = new MechController(leftPrimary, rightPrimary, secondary,
                shooter, youTiao, null, belts, dt);

        addTeleopController(mechController);
        
        //Autonomous initializing
        GRTGyro gyro = new GRTGyro(1, "Turning Gyro");
        sp.addSensor(gyro);

        System.out.println("Start macro creation");
        
        autoMode = getAutonomousMode();
        
        Vector macros = new Vector();
        GRTMacroController macroController;
        
        switch (autoMode) {
            case AUTO_MODE_3_FRISBEE:
                // Macro version of autonomous
                macros.addElement(new PrimeShovel(belts, 1000));
                macros.addElement(new ShooterSet(0, 0, shooter, 5000));
                macros.addElement(new ShooterSet((int) GRTConstants.getValue("autonomousAngle"),
                        GRTConstants.getValue("shootingRPMS"), shooter, 5000));
                for (int i = 0; i < 4; i++) {
                    macros.addElement(new Shoot(shooter, 1000));
                }
                //spins down shooter and lowers it prior to teleop
                macros.addElement(new ShooterSet(0, 0, shooter, 1000));

                macroController = new GRTMacroController(macros);
                addAutonomousController(macroController);
                break;
            case AUTO_MODE_7_FRISBEE:
                // Macro version of autonomous
                macros.addElement(new PrimeShovel(belts, 1000));
                macros.addElement(new ShooterSet(0, 0, shooter, 5000));
                macros.addElement(new ShooterSet((int) GRTConstants.getValue("autonomousAngle"),
                        GRTConstants.getValue("shootingRPMS"), shooter, 5000));
                for (int i = 0; i < 4; i++) {
                    macros.addElement(new Shoot(shooter, 1000));
                }
                //todo rest
                
                macroController = new GRTMacroController(macros);
                addAutonomousController(macroController);
                break;
        }
        
        sp.startPolling();
    }
    
    private int getAutonomousMode(){
        //Check the state of the buttons that are on.
        DriverStationEnhancedIO io = DriverStation.getInstance().getEnhancedIO();
        try {
            if (io.getButton(1)){
                return AUTO_MODE_3_FRISBEE;
            }

            else if (io.getButton(2)){
                return AUTO_MODE_7_FRISBEE;
            }
            
            else {
                return AUTO_MODE_DO_NOTHING;
            }
        } catch(Exception e){
            return 0;   //Return the default 
        }
    }

    private int getPinID(String name) {
        return (int) GRTConstants.getValue(name);
    }
}
