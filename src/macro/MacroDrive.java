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
 * @author keshav
 */
public class MacroDrive extends GRTMacro {

    private GRTDriveTrain dt;
    private double distance = 0;
    private double velocity = 1;
    private double leftInitialDistance;
    private double rightInitialDistance;
    private PIDController DTController;
    private PIDController straightController;
    private GRTEncoder leftEncoder;
    private GRTEncoder rightEncoder;
    private double leftSpeed, rightSpeed;
    private double leftSF = 1;
    private double rightSF = 1;
    private static final double DTP = GRTConstants.getValue("DMP");
    private static final double DTI = GRTConstants.getValue("DMI");
    private static final double DTD = GRTConstants.getValue("DMD");
    private static final double CP = GRTConstants.getValue("DMCP");
    private static final double CI = GRTConstants.getValue("DMCI");
    private static final double CD = GRTConstants.getValue("DMCD");
    private static final int POLL_TIME = 12;
    private static final double TOLERANCE = GRTConstants.getValue("DMTol");
    
    private PIDSource DTSource = new PIDSource() {
        public double pidGet() {
            System.out.println ("distance = " + (rightTraveledDistance() + leftTraveledDistance()) / 2);
            return (rightTraveledDistance() + leftTraveledDistance()) / 2;
        }
    };
    
    private PIDOutput DTOutput = new PIDOutput() {
        public void pidWrite(double output) {
            System.out.println ("distance = " + (rightTraveledDistance() + leftTraveledDistance()) / 2);
            rightSpeed = output;
            leftSpeed = output;
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
                leftSF = 1 - output; //leftSF is now low 
                rightSF = 1;
            } else {
                rightSF = 1 + output;
                leftSF = 1;
            }
            updateMotorSpeeds();
        }
    };
    
    private void updateMotorSpeeds() {
        System.out.println("Setting motor speeds to\t" + leftSpeed*leftSF + "\t" + rightSpeed*rightSF);
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

        DTController = new PIDController(DTP, DTI, DTD, DTSource, DTOutput);

        straightController = new PIDController(CP, CI, CD, straightSource, straightOutput);

        DTController.setAbsoluteTolerance(TOLERANCE);

        DTController.setSetpoint(distance);
        straightController.setSetpoint(0);

        DTController.setOutputRange(-0.4, 0.4);
        straightController.setOutputRange(-1.0, 1.0);

        DTController.enable();
        straightController.enable();
    }



    protected void perform() {
        if (DTController.onTarget()) {
//            hasCompletedExecution = true;
        }
    }

    public void die() {
        dt.setMotorSpeeds(0, 0);
        DTController.free();
        straightController.free();
    }
}
