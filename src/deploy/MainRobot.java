package deploy;

import actuator.GRTSolenoid;
import controller.DriveController;
import controller.MechController;
import core.GRTConstants;
import core.GRTMacroController;
import core.SensorPoller;
import edu.wpi.first.wpilibj.Compressor;
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
        if (robot == 2013.2){
            System.out.println("Starting up 2013 OmegaBot");
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
                50, true, "leftEnc");
        GRTEncoder rightEnc = new GRTEncoder(getPinID("encoderRightA"),
                getPinID("encoderRightB"),
                50, false, "rightEnc");
        sp.addSensor(leftEnc);
        sp.addSensor(rightEnc);
        
        GRTSolenoid leftShifter = new GRTSolenoid(getPinID("leftShifter"));
        GRTSolenoid rightShifter = new GRTSolenoid(getPinID("rightShifter"));
        
        
        
        dt = new GRTDriveTrain(leftDT1, leftDT2, rightDT1, rightDT2, leftShifter, rightShifter,
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
                false, "lowerShooterLimit");
        
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

        //Mechcontroller
        MechController mechController = new MechController(leftPrimary, rightPrimary, secondary,
                shooter, youTiao, null, belts, dt,
                GRTConstants.getValue("shooterPresetX"),
                GRTConstants.getValue("shooterPresetY"),
                GRTConstants.getValue("shooterPresetB"));

        addTeleopController(mechController);
        
//        Potentiometer p = new Potentiometer(3, "Shooter Pot");
//        sp.addSensor(p);
        
        //Autonomous initializing
        GRTGyro gyro = new GRTGyro(1, "Turning Gyro");
        sp.addSensor(gyro);
        
//        AutonomousController controller = new AutonomousController(gyro, shooter, youTiao, belts, dt);
//        addAutonomousController(controller);
        
        // Macro version of autonomous
        Vector macros = new Vector();
        macros.addElement(new ShooterSpeed(GRTConstants.getValue("shootingRPMS"), shooter, 2000));
        macros.addElement(new ShooterAngle((int)GRTConstants.getValue("autoShooterAngle1"), shooter, 1000));
        macros.addElement(new Shoot(shooter, 1000));
        macros.addElement(new Shoot(shooter, 1000));
        macros.addElement(new Shoot(shooter, 1000));
        macros.addElement(new Shoot(shooter, 1000));
        macros.addElement(new Shoot(shooter, 1000));
        macros.addElement(new Shoot(shooter, 1000));
        macros.addElement(new ShooterAngle(0, shooter, 1000));
        macros.addElement(new ShooterSpeed(0, shooter, 2000));
      
 	GRTMacroController macroController = new GRTMacroController(macros); 
        addAutonomousController(macroController);
        
        sp.startPolling();
    }

    private int getPinID(String name) {
        return (int) GRTConstants.getValue(name);
    }
}
