/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package macro;

import core.GRTConstants;
import core.GRTMacro;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import mechanism.GRTDriveTrain;
import sensor.GRTEncoder;

/**
 * Drives straight for a set distance.
 * 
 * @author keshav, calvin, dan (github: dannyperson, twitter: @dannyperson. LinkedIn: Daniel Frei)
 */
public class MacroDrive extends GRTMacro {

    private GRTDriveTrain dt;
    private double distance = 0;
    private double velocity = 1;
    private double leftInitialDistance;
    private double rightInitialDistance;
    private PIDController leftDTController;
    private PIDController rightDTController;
    private PIDController straightController;
    private GRTEncoder leftEncoder;
    private GRTEncoder rightEncoder;
    private double leftSpeed, rightSpeed;
    private double leftSF = 1;
    private double rightSF = 1;
    private static final double LP = GRTConstants.getValue("DMLP");
    private static final double LI = GRTConstants.getValue("DMLI");
    private static final double LD = GRTConstants.getValue("DMLD");
    private static final double RP = GRTConstants.getValue("DMRP");
    private static final double RI = GRTConstants.getValue("DMRI");
    private static final double RD = GRTConstants.getValue("DMRD");
    private static final double CP = GRTConstants.getValue("DMCP");
    private static final double CI = GRTConstants.getValue("DMCI");
    private static final double CD = GRTConstants.getValue("DMCD");
    private static final int POLL_TIME = 12;
    private static final double TOLERANCE = GRTConstants.getValue("DMTol"); //TODO
    
    private PIDSource leftSource = new PIDSource() {
        public double pidGet() {
            System.out.println("distance = " + leftTraveledDistance());
            return leftTraveledDistance();
        }
    };
    
    private PIDOutput leftOutput = new PIDOutput() {
        public void pidWrite(double output) {
            leftSpeed = output;
            updateMotorSpeeds();
        }
    };
    
    private PIDSource rightSource = new PIDSource() {
        public double pidGet() {
            System.out.println("distance = " + rightTraveledDistance());
            return rightTraveledDistance();
        }
    };
    
    private PIDOutput rightOutput = new PIDOutput() {
        public void pidWrite(double output) {
            rightSpeed = output;
            updateMotorSpeeds();
        }
    };
    
    /**
     * Use distance difference, rather than speed difference, to keep
     * robot straight
     */
    private PIDSource straightSource = new PIDSource() {
        public double pidGet() {
            return leftTraveledDistance() - rightTraveledDistance();
        }
    };
    
    private PIDOutput straightOutput = new PIDOutput() {
        public void pidWrite(double output) {
            if (output > 0) { //if left is ahead, pidGet will correct with negative number
                leftSF = 1 + output; //leftSF is now low 
                rightSF = 1;
            } else {
                rightSF = 1 - output;
                leftSF = 1;
            }
        }
    };
    
    private void updateMotorSpeeds() {
        System.out.println("Setting motor speeds :\t" + leftSpeed  + "\t" + rightSpeed);
        dt.setMotorSpeeds(leftSpeed * leftSF, rightSpeed * rightSF);
    }
    
    private double rightTraveledDistance() {
        return rightEncoder.getDistance() - rightInitialDistance;
    }
    
    private double leftTraveledDistance() {
        return leftEncoder.getDistance() - leftInitialDistance;
    }

    /*
     * Creates a new Driving Macro
     * 
     * @param dt GRTDriveTrain object
     * @param distance distance to travel in meters (assumes travel in straight line)
     * @param timeout time in ms
     */
    public MacroDrive(GRTDriveTrain dt, double distance, int timeout) {
        super("Drive Macro", timeout);
        this.dt = dt;
        this.distance = distance;
        this.leftEncoder = dt.getLeftEncoder();
        this.rightEncoder = dt.getRightEncoder();
    }

    protected void initialize() {
        dt.setMotorSpeeds(velocity, velocity);

        leftInitialDistance = leftEncoder.getDistance();
        rightInitialDistance = rightEncoder.getDistance();

        leftDTController = new PIDController(LP, LI, LD, leftSource, leftOutput);
        rightDTController = new PIDController(RP, RI, RD, rightSource, rightOutput);

        straightController = new PIDController(CP, CI, CD, straightSource, straightOutput);

        leftDTController.setAbsoluteTolerance(TOLERANCE);
        rightDTController.setAbsoluteTolerance(TOLERANCE);

        leftDTController.setSetpoint(distance);
        rightDTController.setSetpoint(distance);
        straightController.setSetpoint(0);

        leftDTController.setOutputRange(-0.3, 0.3);
        rightDTController.setOutputRange(-0.3, 0.3);
        straightController.setOutputRange(-1.0, 1.0);

        System.out.println("Enabling PID Controllers");
        leftDTController.enable();
        rightDTController.enable();
        straightController.enable();
    }

    protected void perform() {
        if (leftDTController.onTarget() && rightDTController.onTarget()) {
            System.out.println("Execution of driving complete");
            hasCompletedExecution = true;
        }
    }

    public void die() {
        System.out.println("Killing Driving Straight Macro");
        dt.setMotorSpeeds(0, 0);
        leftDTController.free();
        rightDTController.free();
    }
}
