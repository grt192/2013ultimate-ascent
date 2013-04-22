package macro;

import controller.DeadReckoner;
import core.GRTConstants;
import core.GRTMacro;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import event.listeners.ConstantUpdateListener;
import mechanism.GRTDriveTrain;
import sensor.GRTEncoder;

/**
 * Drives straight for a set distance.
 * 
 * @author keshav
 */
public class MacroDrive extends GRTMacro implements ConstantUpdateListener {

    private GRTDriveTrain dt;
    private double distance = 0;
    private double leftInitialDistance;
    private double rightInitialDistance;
    private PIDController DTController;
    private PIDController straightController;
    private GRTEncoder leftEncoder;
    private GRTEncoder rightEncoder;
    private double speed;
    private double leftSF = 1;
    private double rightSF = 1;
    private double DTP;
    private double DTI;
    private double DTD;
    private double CP;
    private double CI;
    private double CD;
    private double TOLERANCE;
    
    private double MAX_MOTOR_OUTPUT;
    
    private boolean previouslyOnTarget = false;
        
    private PIDSource DTSource = new PIDSource() {
        public double pidGet() {
            return (rightTraveledDistance() + leftTraveledDistance()) / 2;
        }
    };
    
    private PIDOutput DTOutput = new PIDOutput() {
        public void pidWrite(double output) {
            speed = output;
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
            double modifier = Math.abs(output);
            //concise code is better code
            rightSF = 2 - modifier - (leftSF = 1 - (speed * output < 0 ? modifier : 0)); 
            
            updateMotorSpeeds();
        }
    };
    
    private void updateMotorSpeeds() {
        dt.setMotorSpeeds(speed * leftSF, speed * rightSF);
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
        
        DTController = new PIDController(DTP, DTI, DTD, DTSource, DTOutput);
        straightController = new PIDController(CP, CI, CD, straightSource, straightOutput);
        straightController.setOutputRange(-1, 1);
        
        updateConstants();
        GRTConstants.addListener(this);
    }

    protected void initialize() {
        leftInitialDistance = leftEncoder.getDistance();
        rightInitialDistance = rightEncoder.getDistance();

        DTController.setSetpoint(distance);
        straightController.setSetpoint(0);

        DTController.enable();
        straightController.enable();
        System.out.println("MACRODRIVE is initialized");
    }

    protected void perform() {
        if (DTController.onTarget()) {
            System.out.println("On target!");
            if (previouslyOnTarget)
                notifyFinished();
            else
                previouslyOnTarget = true;
        }
    }

    protected void die() {
        dt.setMotorSpeeds(0, 0);
        DTController.disable();
        straightController.disable();
        DeadReckoner.notifyDrive(getDistanceTraveled());
    }
    
    public double getDistanceTraveled() {
        return (leftTraveledDistance() + rightTraveledDistance()) / 2;
    }

    public final void updateConstants() {
        DTP = GRTConstants.getValue("DMP");
        DTI = GRTConstants.getValue("DMI");
        DTD = GRTConstants.getValue("DMD");
        CP = GRTConstants.getValue("DMCP");
        CI = GRTConstants.getValue("DMCI");
        CD = GRTConstants.getValue("DMCD");
        TOLERANCE = GRTConstants.getValue("DMTol");
        MAX_MOTOR_OUTPUT = GRTConstants.getValue("DMMax");
        
        DTController.setPID(DTP, DTI, DTD);
        straightController.setPID(CP, CI, CD);
        DTController.setAbsoluteTolerance(TOLERANCE);
        DTController.setOutputRange(-MAX_MOTOR_OUTPUT, MAX_MOTOR_OUTPUT);
    }
}
