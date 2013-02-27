package mechanism;

import actuator.GRTSolenoid;
import core.GRTConstants;
import core.GRTLoggedProcess;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.SpeedController;
import event.events.EncoderEvent;
import event.events.PotentiometerEvent;
import event.listeners.EncoderListener;
import event.listeners.PotentiometerListener;
import sensor.GRTEncoder;
import sensor.Potentiometer;

/**
 * Shooter mechanism.
 *
 * @author Calvin
 */
public class Shooter extends GRTLoggedProcess implements PotentiometerListener, EncoderListener {

    private SpeedController shooterMotor1, shooterMotor2;
    private SpeedController raiser;
    private GRTSolenoid feeder;
    private GRTEncoder flywheelEncoder;
    private Potentiometer raiserPot;
    private PIDController raiserController;
    private PIDController flywheelController;
    /**
     * PID Constants for the raiser. RAISER_TOLERANCE is the absolute error
     * allowed in the raiser angle (in degrees).
     */
    private static final double RAISER_P = GRTConstants.getValue("shooterRaiserP");
    private static final double RAISER_I = GRTConstants.getValue("shooterRaiserI");
    private static final double RAISER_D = GRTConstants.getValue("shooterRaiserD");
    private static final double RAISER_TOLERANCE =
            GRTConstants.getValue("raiserTolerance");
    /**
     * PID Constants for the flywheel. FLYWHEEL_TOLERANCe is the percent error
     * allowed by the flywheel (i.e. 5.0 -> 5 percent).
     */
    private static final double FLYWHEEL_P = GRTConstants.getValue("flywheelP");
    private static final double FLYWHEEL_I = GRTConstants.getValue("flywheelI");
    private static final double FLYWHEEL_D = GRTConstants.getValue("flywheelD");
    private static final double FLYWHEEL_TOLERANCE =
            GRTConstants.getValue("flywheelTolerance");
    /**
     * The voltage output by the pot at the lowest angle.
     */
    private static final double ZERO_V = GRTConstants.getValue("raiserZeroV");
    /**
     * The angular range of the potentiometer.
     */
    private static final double POT_RANGE = GRTConstants.getValue("raiserPotRange");
    private static final double MAX_ANGLE = 47.0;

    /**
     * Creates a new shooter.
     *
     * @param shooterMotor1
     * @param shooterMotor2
     * @param feeder
     * @param raiser
     * @param flywheelEncoder
     * @param raiserPot
     */
    public Shooter(SpeedController shooterMotor1, SpeedController shooterMotor2,
            GRTSolenoid feeder, SpeedController raiser, GRTEncoder flywheelEncoder,
            Potentiometer raiserPot) {
        super("Shooter mech");
        this.feeder = feeder;
        this.shooterMotor1 = shooterMotor1;
        this.shooterMotor2 = shooterMotor2;
        this.raiser = raiser;
        this.flywheelEncoder = flywheelEncoder;
        this.raiserPot = raiserPot;

        flywheelController = new PIDController(FLYWHEEL_P, FLYWHEEL_I, FLYWHEEL_D,
                flywheelSource, flywheelOutput);
        flywheelController.setOutputRange(0, 1);
        flywheelController.setPercentTolerance(FLYWHEEL_TOLERANCE);

        raiserController = new PIDController(RAISER_P, RAISER_I, RAISER_D,
                raiserSource, raiserOutput);
        raiserController.setOutputRange(-1, 1);
        raiserController.setAbsoluteTolerance(RAISER_TOLERANCE);

        System.out.println("New Shooter");
        raiserPot.addListener(this);
    }

    /**
     * Sets the output of the speed controllers controlling the flywheel.
     *
     * @param speed flywheel output, from -1 to 1
     */
    public void setFlywheelOutput(double speed) {
        flywheelController.disable();
        logInfo("Setting speed:" + speed);
        shooterMotor1.set(speed);
        shooterMotor2.set(speed);
    }
    
    //PID sources. This is the input gain that is read in by the controller, and is used to scale the output gains according to your PID function.
    private PIDSource flywheelSource = new PIDSource() {
        public double pidGet() {
            return flywheelEncoder.getRate();
        }
    };
    
    //Function that is called with the PID output gain. Here, it is being applied to the shooter motor speeds.
    private PIDOutput flywheelOutput = new PIDOutput() {
        public void pidWrite(double d) {
            System.out.println("Motor output: " + ((int) (d * 1000))/1000.0 + " Shooter RPM: " + (int) flywheelEncoder.getRate() + "  Desired: " + flywheelController.getSetpoint());
            shooterMotor1.set(d);
            shooterMotor2.set(d);
        }
    };

    /**
     * Sets the speed of the flywheel.
     *
     * @param speed speed of flywheel, in RPM
     */
    public void setSpeed(double speed) {
        System.out.println("PID settting flywheel speed to " + speed);
        flywheelController.setSetpoint(speed);
        flywheelController.enable();
    }

    /**
     * Sets the speed of the raiser motor.
     *
     * @param velocity motor output from -1 to 1
     */
    public void adjustHeight(double velocity) {
        raiserController.disable();
        
        if (Math.abs(velocity) < 0.1){
            raiser.set(0.0);
        } else {
            raiser.set(-velocity);
        }
        logInfo("adjusting shooter by " +velocity);
////        double currentAngle = getShooterAngle();
////        if ((velocity >= 0 && currentAngle < MAX_ANGLE)
////                || (velocity < 0 && currentAngle > 0)) {
////            raiser.set(velocity);
//        }
    }

    /**
     * Gets the current shooter angle.
     */
    public double getShooterAngle() {
        return (raiserPot.getValue() - ZERO_V) * POT_RANGE;
    }
    private PIDSource raiserSource = new PIDSource() {
        public double pidGet() {
            return getShooterAngle();
        }
    };
    private PIDOutput raiserOutput = new PIDOutput() {
        public void pidWrite(double d) {
            raiser.set(d);
        }
    };

    /**
     * Sets the angle of the shooter.
     *
     * @param angle angle of shooter, from 0 to {@value #MAX_ANGLE}
     */
    public void setAngle(double angle) {
        logInfo("Setting Angle to " + angle);
        if (angle < 0) {
            angle = 0;
        } else if (angle > MAX_ANGLE) {
            angle = MAX_ANGLE;
        }

        raiserController.setSetpoint(angle);
        raiserController.enable();
    }

    /**
     * Extends luna.
     */
    public void shoot() {
        logInfo("Shooting!");
        feeder.set(true);
    }

    /**
     * Retracts luna.
     */
    public void unShoot() {
        logInfo("Unshooting!");
        feeder.set(false);
    }

    public void valueChanged(PotentiometerEvent e) {
        logInfo(""+getShooterAngle());
        if ((getShooterAngle() < 0 && raiser.get() < 0)
                || (getShooterAngle() > MAX_ANGLE)) {
            raiser.set(0);
            System.out.println("shooter angle = " + e.getData());
        }
    }

    public void rotationStarted(EncoderEvent e) {
        logInfo("Rotation beginning");
    }

    public void degreeChanged(EncoderEvent e) {
    }

    public void distanceChanged(EncoderEvent e) {
    }

    public void rotationStopped(EncoderEvent e) {
    }

    public void rateChanged(EncoderEvent e) {
        logInfo("Current shooter RPMs: " + e.getData());
    }
}
