package mechanism;

import actuator.GRTSolenoid;
import actuator.Motor;
import event.events.EncoderEvent;
import event.listeners.EncoderListener;
import logger.GRTLogger;
import sensor.GRTEncoder;

/**
 * A Drive Train that is capable of
 * shifting, and has rotary encoders.
 *
 * @author andrew, keshav
 */
public class GRTDriveTrain {

    private final Motor leftFront;
    private final Motor leftBack;
    private final Motor rightFront;
    private final Motor rightBack;
    private double leftFrontSF = 1;
    private double leftBackSF = 1;
    private double rightFrontSF = -1;
    private double rightBackSF = -1;
    
    private boolean shifting;
    private GRTSolenoid leftShifter, rightShifter;
    
    private boolean hasEncoders;
    private GRTEncoder leftEncoder, rightEncoder;

    boolean halfPowered = false;    //State variable determining if we run at 1/2 power. 
    
    /**
     * Constructs a new drivetrain.
     *
     * @param leftFront left front motor
     * @param leftBack left back motor
     * @param rightFront right front motor
     * @param rightBack right back motor
     */
    public GRTDriveTrain(Motor leftFront, Motor leftBack,
            Motor rightFront, Motor rightBack) {

        this.leftFront = leftFront;
        this.leftBack = leftBack;
        this.rightFront = rightFront;
        this.rightBack = rightBack;
        
        this.shifting = false;
        this.leftShifter = null;
        this.rightShifter = null;
        
        this.hasEncoders = false;
        this.leftEncoder = this.rightEncoder = null;
    }
    
    public GRTDriveTrain(Motor leftFront, Motor leftBack,
            Motor rightFront, Motor rightBack,
            GRTEncoder leftEncoder, GRTEncoder rightEncoder) {

        this.leftFront = leftFront;
        this.leftBack = leftBack;
        this.rightFront = rightFront;
        this.rightBack = rightBack;
        
        this.shifting = false;
        this.leftShifter = null;
        this.rightShifter = null;
        
        this.hasEncoders = true;
        this.leftEncoder = leftEncoder;
        this.rightEncoder = rightEncoder;
    }

    public GRTDriveTrain(Motor leftFront, Motor leftBack,
            Motor rightFront, Motor rightBack,
            GRTSolenoid leftShifter, GRTSolenoid rightShifter) {

        this.leftFront = leftFront;
        this.leftBack = leftBack;
        this.rightFront = rightFront;
        this.rightBack = rightBack;
        
        this.shifting = true;
        this.leftShifter = leftShifter;
        this.rightShifter = rightShifter;
        
        this.hasEncoders = false;
        this.leftEncoder = this.rightEncoder = null;
    }
    
    public GRTDriveTrain(Motor leftFront, Motor leftBack,
            Motor rightFront, Motor rightBack,
            GRTSolenoid leftShift, GRTSolenoid rightShift,
            GRTEncoder leftEncoder, GRTEncoder rightEncoder) {
        
        this(leftFront, leftBack, rightFront, rightBack);
        
        this.shifting = true;
        this.leftShifter = leftShift;
        this.rightShifter = rightShift;
        
        this.hasEncoders = true;
        this.leftEncoder = leftEncoder;
        this.rightEncoder = rightEncoder;
    }
    
    
    
    

    /**
     * Depending on robot orientation, drivetrain configuration, controller
     * configuration, motors on different parts of the drivetrain may need to be
     * driven in differing directions. These "scale factor" numbers change the
     * magnitude and/or direction of the different motors; they are multipliers
     * for the speed fed to the motors.
     *
     * @param leftFrontSF left front scale factor.
     * @param leftBackSF left back scale factor.
     * @param rightFrontSF right front scale factor.
     * @param rightBackSF right back scale factor.
     */
    public void setScaleFactors(double leftFrontSF, double leftBackSF,
            double rightFrontSF, double rightBackSF) {
        this.leftFrontSF = leftFrontSF;
        this.leftBackSF = leftBackSF;
        this.rightFrontSF = rightFrontSF;
        this.rightBackSF = rightBackSF;
    }
    
    /**
     * Set the left and right side speeds of the drivetrain motors
     *
     * @param leftVelocity left drivetrain velocity
     * @param rightVelocity right drivetrain velocity
     */
    public void setMotorSpeeds(double leftVelocity, double rightVelocity) {
        GRTLogger.logInfo("Left: " +  leftVelocity +"\tRight: " + rightVelocity);
        leftFront.setSpeed(leftVelocity * leftFrontSF);
        rightFront.setSpeed(rightVelocity * rightFrontSF);
        
        //If we're full powered, then command the other two motors as well.
        if (!halfPowered){
            leftBack.setSpeed(leftVelocity * leftBackSF);
            rightBack.setSpeed(rightVelocity * rightBackSF);
        }
    }
    
    public void setHalfPowered(){
        halfPowered = true;
    }
    
    public void setFullPowered(){
        halfPowered = false;
    }
    
    public void shiftUp(){
        GRTLogger.logInfo("Shifting down? " + shifting);
        if(shifting){
            leftShifter.engage(false);
            rightShifter.engage(false); 
        }
    }
    
    public void shiftDown(){
        GRTLogger.logInfo("Shifting down? " + shifting);
        if(shifting){
            leftShifter.engage(true);
            rightShifter.engage(true);
        }
    }
    
    public GRTEncoder getLeftEncoder(){
        return leftEncoder;
    }
    
    public GRTEncoder getRightEncoder(){
        return rightEncoder;
    }
}
