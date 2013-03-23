package macro;

import core.GRTConstants;
import core.GRTMacro;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import event.listeners.ConstantUpdateListener;
import mechanism.GRTDriveTrain;
import sensor.GRTGyro;

/**
 * Macro that automatically turns the robot a certain angle.
 *
 * @author Calvin
 */
public class MacroTurn extends GRTMacro implements ConstantUpdateListener{

    private double targetAngle;
    private double startAngle;
    private final double turnAngle;
    private GRTGyro gyro;
    private GRTDriveTrain dt;
    private PIDController controller;
    private double P;
    private double I;
    private double D;

    private boolean previouslyOnTarget = false;
    
    private PIDSource pidSource = new PIDSource() {
        public double pidGet() {
            logInfo("Setpoint: " + targetAngle + " current angle: " + gyro.getAngle());
            return gyro.getAngle();
        }
    };
    private PIDOutput pidOutput = new PIDOutput() {
        public void pidWrite(double output) {
            logInfo("Drive left: " + output + " drive right: " + -output);
            System.out.println(turnAngle + "\t" + (gyro.getAngle() - startAngle) + "\t" + output);

            dt.setMotorSpeeds(output, -output);
        }
    };
    
    /**
     * Creates a new turning macro, that turns a set number of degrees.
     * 
     * @param turnAngle angle to turn, in degrees
     * @param gyro gyroscope to track robot movement
     * @param dt drivetrain to command
     */
    public MacroTurn(GRTDriveTrain dt, GRTGyro gyro, double turnAngle, int timeout) {
        super("Turn Macro", timeout, 50);
        
        this.dt = dt;
        this.turnAngle = turnAngle;
        this.gyro = gyro;
        
        GRTConstants.addListener(this);
        updateConstants();
    }

    protected void perform() {
        if (controller.onTarget()) {
            if (previouslyOnTarget)
                hasCompletedExecution = true;
            else
                previouslyOnTarget = true;
        }
    }

    public void die() {
        hasCompletedExecution = true;
        controller.disable();
        controller.free();
    }
    
    public void initialize() {
        startAngle = gyro.getAngle();
        targetAngle = startAngle + turnAngle;
        controller = new PIDController(P, I, D, pidSource, pidOutput);
        controller.setOutputRange(-1, 1);
        controller.setAbsoluteTolerance(GRTConstants.getValue("TMTol"));
        controller.setSetpoint(targetAngle);
        controller.enable();
    }

    public final void updateConstants() {
        P = GRTConstants.getValue("TMP");
        I = GRTConstants.getValue("TMI");
        D = GRTConstants.getValue("TMD");
    }
}
