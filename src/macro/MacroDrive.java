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
import sensor.GRTEncoder;

/**
 *
 * @author keshav
 */
public class MacroDrive extends GRTMacro {

    private GRTDriveTrain dt;
    private double distance = 0;
    private double velocity = 1;
    
    private double leftInitialDistance;
    private double rightInitialDistance;
    
    private PIDController leftDTController;
    private PIDController rightDTController;
    private GRTEncoder leftEncoder;
    private GRTEncoder rightEncoder;
    
    private double leftSpeed;
    private double rightSpeed;
    
    private static final double LP = 0.4;
    private static final double LI = 0.0;
    private static final double LD = 0.0;
    private static final double RP = 0.4;
    private static final double RI = 0.0;
    private static final double RD = 0.0;
    
    private static final int POLL_TIME = 12;
    private static final double TOLERANCE = 4; //TODO
    
    private PIDSource leftSource = new PIDSource() {
        public double pidGet() {
            return distance - (leftEncoder.getDistance() - leftInitialDistance);
        }
    };
    
    private PIDOutput leftOutput = new PIDOutput() {
        public void pidWrite(double output) {
            leftSpeed = output;
            dt.setMotorSpeeds(leftSpeed, rightSpeed);
        }
    };
    
    private PIDSource rightSource = new PIDSource() {
        public double pidGet() {
            return distance - (rightEncoder.getDistance() - rightInitialDistance);
        }
    };
    
    private PIDOutput rightOutput = new PIDOutput() {
        public void pidWrite(double output) {
            rightSpeed = output;
            dt.setMotorSpeeds(leftSpeed, rightSpeed);
        }
    };

    /*
     * Creates a new Driving Macro
     * 
     * @param dt GRTDriveTrain object
     * @param distance distance to travel in m (assumes travel in straight line)
     * @param timeout time in ms
     */
    public MacroDrive(GRTDriveTrain dt, double distance, int timeout,
            GRTEncoder leftEncoder, GRTEncoder rightEncoder) {
        super("Drive Macro", timeout);
        this.dt = dt;
        this.distance = distance;
        this.leftEncoder = leftEncoder;
        this.rightEncoder = rightEncoder;
    }

    protected void initialize() {        
        dt.setMotorSpeeds(velocity, velocity);
        
        leftInitialDistance = leftEncoder.getDistance();
        rightInitialDistance = rightEncoder.getDistance();
        
        leftDTController = new PIDController(LP, LI, LD, leftSource, leftOutput, POLL_TIME);
        rightDTController = new PIDController(RP, RI, RD, rightSource, rightOutput, POLL_TIME);
        
        leftDTController.setAbsoluteTolerance(TOLERANCE);
        rightDTController.setAbsoluteTolerance(TOLERANCE);
        
        leftDTController.setSetpoint(distance);
        rightDTController.setSetpoint(distance);
        
        leftDTController.setOutputRange(-1.0, 1.0);
        rightDTController.setOutputRange(-1.0, 1.0);
        
    }

    protected void perform() {
        if (leftDTController.onTarget() && rightDTController.onTarget())
            hasCompletedExecution = true;
    }

    public void die() {
        dt.setMotorSpeeds(0, 0);
        leftDTController.free();
        rightDTController.free();
    }
}
