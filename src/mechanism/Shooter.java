package mechanism;

import actuator.GRTSolenoid;
import core.GRTConstants;
import core.GRTLoggedProcess;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Victor;
import event.events.PotentiometerEvent;
import event.listeners.PotentiometerListener;
import sensor.GRTEncoder;
import sensor.Potentiometer;

/**
 * Shooter mechanism.
 *
 * @author Calvin
 */
public class Shooter extends GRTLoggedProcess implements PotentiometerListener {

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
    private static final double MAX_ANGLE = 40;

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
    public Shooter(Victor shooterMotor1, Victor shooterMotor2,
            GRTSolenoid feeder, Victor raiser, GRTEncoder flywheelEncoder,
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

        logInfo("New Shooter");
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
    private PIDSource flywheelSource = new PIDSource() {
        public double pidGet() {
            return flywheelEncoder.getRate();
        }
    };
    private PIDOutput flywheelOutput = new PIDOutput() {
        public void pidWrite(double d) {
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
        double currentAngle = getShooterAngle();
        if ((velocity >= 0 && currentAngle < MAX_ANGLE)
                || (velocity < 0 && currentAngle > 0)) {
            raiser.set(velocity);
        }
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
        feeder.set(true);
    }

    /**
     * Retracts luna.
     */
    public void unShoot() {
        feeder.set(false);
    }

    public void valueChanged(PotentiometerEvent e) {
        if ((getShooterAngle() < 0 && raiser.get() < 0)
                || (getShooterAngle() > MAX_ANGLE && raiser.get() > 0)) {
            raiser.set(0);
        }
    }
}
