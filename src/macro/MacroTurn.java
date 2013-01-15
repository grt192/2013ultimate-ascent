/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package macro;

import core.GRTMacro;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import mechanism.GRTDriveTrain;
import sensor.GRTGyro;

/**
 * Macro that automatically turns the robot a
 *
 * @author Calvin
 */
public class MacroTurn extends GRTMacro {

    private double targetAngle;
    private double currentAngle;
    private final double turnAngle;
    private GRTGyro gyro;
    private GRTDriveTrain dt;
    private PIDController controller;
    private static final double P = 1.0;
    private static final double I = 0.0;
    private static final double D = 0.0;
    private static final double F = 0.0;
    private static final double POLL_TIME = 0.05;

    private PIDSource pidSource = new PIDSource() {
        public double pidGet() {
            logInfo("Setpoint: " + targetAngle + " current angle: " + gyro.getAngle());
            return gyro.getAngle();
        }
    };
    private PIDOutput pidOutput = new PIDOutput() {
        public void pidWrite(double output) {
            logInfo("Drive left: " + output + " drive right: " + -output);
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
    public MacroTurn(double turnAngle, GRTGyro gyro, GRTDriveTrain dt) {
        super("Turn Macro", 5000, 50);
        
        this.turnAngle = turnAngle;
        this.gyro = gyro;
        controller = new PIDController(P, I, D, F, pidSource, pidOutput, POLL_TIME);
        controller.setOutputRange(-1, 1);
        
        controller.setAbsoluteTolerance(3.0);
    }

    protected void perform() {
        if (controller.onTarget())
            hasCompletedExecution = true;
    }

    public void die() {
        controller.disable();
        controller.free();
    }
    
    public void initialize() {
        currentAngle = gyro.getAngle();
        targetAngle = currentAngle + turnAngle;
        controller.setSetpoint(targetAngle);
        controller.enable();
    }
}
