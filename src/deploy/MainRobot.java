package deploy;

import actuator.GRTSolenoid;
import actuator.GRTTalon;
import actuator.GRTVictor;
import com.sun.squawk.microedition.io.FileConnection;
import controller.DriveController;
import core.GRTMacroController;
import core.GRTMacro;
import edu.wpi.first.wpilibj.Compressor;
import java.io.IOException;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.Vector;
import javax.microedition.io.Connector;
import logger.GRTLogger;
import macro.MacroDelay;
import macro.MacroDrive;
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
        
        //base2012Init();
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

        GRTLogger.logInfo("GRTFramework v6 starting up.");

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
	GRTSolenoid leftShifter = new GRTSolenoid(1, "leftShifter");
	GRTSolenoid rightShifter = new GRTSolenoid(2, "rightShifter");
	
	leftShifter.enable(); rightShifter.enable();
	
	//Compressor
	Compressor compressor = new Compressor(14, 1);
	compressor.start();
	
        // PWM outputs
        GRTVictor leftDT1 = new GRTVictor(9, "leftDT1");
        GRTVictor leftDT2 = new GRTVictor(10, "leftDT2");
        GRTVictor rightDT1 = new GRTVictor(1, "rightDT1");
        GRTVictor rightDT2 = new GRTVictor(2, "rightDT2");
        leftDT1.enable();
        leftDT2.enable();
        rightDT1.enable();
        rightDT2.enable();
        GRTLogger.logInfo("Motors initialized");
        
        // Encoders
        GRTEncoder leftEnc = new GRTEncoder(1, 2, 1, 50, "leftEnc");
        GRTEncoder rightEnc = new GRTEncoder(3, 4, 1, 50, "rightEnc");
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
        
        Vector macros = new Vector();
        
        
        macros.addElement(new MacroDelay(3000));
        macros.addElement(new MacroDelay(3000));
        macros.addElement(new MacroDrive(dt, 100, 10000));
        GRTMacroController mc = new GRTMacroController(macros);
        
        
        addTeleopController(dc);
        addAutonomousController(mc);
        
        GRTLogger.logSuccess("Ready to drive.");
    }

    /**
     * Initialize function for the 2012 base.
     */
    private void base2012Init(){
        GRTLogger.logInfo("GRTFramework v6 starting up.");
        
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
        GRTTalon leftDT1 = new GRTTalon(9, "leftDT1");
        GRTTalon leftDT2 = new GRTTalon(10, "leftDT2");
        GRTTalon rightDT1 = new GRTTalon(1, "rightDT1");
        GRTTalon rightDT2 = new GRTTalon(2, "rightDT2");
        leftDT1.enable();
        leftDT2.enable();
        rightDT1.enable();
        rightDT2.enable();
        GRTLogger.logInfo("Motors initialized");

        //Mechanisms
        dt = new GRTDriveTrain(leftDT1, leftDT2, rightDT1, rightDT2);
        dt.setScaleFactors(1, -1, -1, 1);
        
        GRTLogger.logInfo("mechanisms intialized");
    }
}
