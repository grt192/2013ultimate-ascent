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
                GRTConstants.getValue("DTDistancePerPulse"), false, "leftEnc");
        GRTEncoder rightEnc = new GRTEncoder(getPinID("encoderRightA"),
                getPinID("encoderRightB"),
                GRTConstants.getValue("DTDistancePerPulse"), true, "rightEnc");
        sp.addSensor(leftEnc);
        sp.addSensor(rightEnc);
               dt = new GRTDriveTrain(leftDT1, leftDT2, rightDT1, rightDT2,
                leftEnc, rightEnc);

        dt.setScaleFactors(
                GRTConstants.getValue("leftDT1Scale"),
                GRTConstants.getValue("leftDT2Scale"),
                GRTConstants.getValue("rightDT1Scale"),
                GRTConstants.getValue("rightDT2Scale"));

        Vector macros = new Vector();
        macros.addElement(new MacroDrive(dt, GRTConstants.getValue("autoDistance"), 7000));
        macros.addElement(new MacroDelay(1000));
        macros.addElement(new MacroDrive(dt, -GRTConstants.getValue("autoDistance"), 7000));
        
        GRTMacroController macroController = new GRTMacroController(macros); 
        addAutonomousController(macroController);
        
        System.out.println("Start sensor polling!");
        
        sp.startPolling();
    }

    private int getPinID(String name) {
        return (int) GRTConstants.getValue(name);
    }
}