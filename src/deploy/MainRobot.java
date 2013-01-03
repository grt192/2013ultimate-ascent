package deploy;

import actuator.GRTSolenoid;
import actuator.GRTTalon;
import actuator.GRTVictor;
import controller.ShiftingDriveController;
import edu.wpi.first.wpilibj.Compressor;
import java.util.Calendar;
import java.util.TimeZone;
import logger.GRTLogger;
import mechanism.GRTDriveTrain;
import mechanism.GRTRobotBase;
import mechanism.ShiftingDriveTrain;
import sensor.GRTAttack3Joystick;
import sensor.GRTBatterySensor;
import sensor.GRTXBoxJoystick;
import sensor.base.*;

/**
 * Constructor for the main robot. Put all robot components here.
 *
 * @author ajc
 */
public class MainRobot extends GRTRobot {

    private GRTRobotBase robotBase;
   
    /**
     * Initializer for the robot. Calls an appropriate initialization function.
     */
    public MainRobot() {
        //base2012Init();
        base2013Init();
    }
    
    public void disabled() {
        GRTLogger.logInfo("Disabling robot. Halting drivetrain");
        robotBase.tankDrive(0.0, 0.0);
    }

    /**
     * Initializer for the 2013 robot.
     */
    private void base2013Init() {

        //Init the logging files.
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        String dateStr = "" + cal.get(Calendar.YEAR) + "-" + cal.get(
                Calendar.MONTH) + 1 + "T" + cal.get(Calendar.HOUR_OF_DAY) + cal.
                get(Calendar.MINUTE) + cal.get(Calendar.SECOND);
        GRTLogger.logInfo("Date string = " + dateStr);
        String loggingFiles[] = new String[]{"/logs/" + dateStr + "_info.log",
            "/logs/" + dateStr + "_success.log", "/logs/" + dateStr
            + "_error.log", "/logs/" + dateStr + "_all.log"};
        GRTLogger.setLoggingFiles(loggingFiles);
        //GRTLogger.enableFileLogging();

        GRTLogger.logInfo("GRTFramework v6 starting up.");

        //Driver station components
        GRTAttack3Joystick primary = new GRTAttack3Joystick(1, 12, "primary");
        GRTAttack3Joystick secondary =
                new GRTAttack3Joystick(2, 12, "secondary");
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

        //Mechanisms
        ShiftingDriveTrain dt = new ShiftingDriveTrain(leftDT1, leftDT2,
                rightDT1, rightDT2, leftShifter, rightShifter);
        
        robotBase = new GRTRobotBase(dt, batterySensor);
        GRTDriverStation driverStation =
                new GRTAttack3DriverStation(primary, secondary, "driverStation");
        driverStation.enable();
        GRTLogger.logInfo("Mechanisms initialized");

        //Controllers
        ShiftingDriveController shiftingControl =
                new ShiftingDriveController(dt, driverStation, "driveControl");
        GRTLogger.logInfo("Controllers Initialized");
        driverStation.addDrivingListener(shiftingControl);
	driverStation.addShiftListener(shiftingControl);
        
        addTeleopController(shiftingControl);

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
        GRTXBoxJoystick joy = new GRTXBoxJoystick(1, 25, "Joystick");
        joy.startPolling();
        joy.enable();
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
        GRTDriveTrain dt = new GRTDriveTrain(leftDT1, leftDT2, rightDT1, rightDT2);
        dt.setScaleFactors(1, -1, -1, 1);

        robotBase = new GRTRobotBase(dt, batterySensor);
        
        GRTDriverStation driverStation =
                new GRTXboxDriverStation(joy, "Driver Station");
        driverStation.enable();
        GRTLogger.logInfo("mechanisms intialized");
    }
}
