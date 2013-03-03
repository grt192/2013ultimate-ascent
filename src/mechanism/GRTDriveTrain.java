package mechanism;

import actuator.GRTSolenoid;
import core.GRTLoggedProcess;
import edu.wpi.first.wpilibj.SpeedController;
import logger.GRTLogger;
import sensor.GRTEncoder;

/**
 * A Drive Train that is capable of
 * shifting, and has rotary encoders.
 *
 * @author andrew, keshav
 */
public class GRTDriveTrain extends GRTLoggedProcess {

    private final SpeedController leftFront;
    private final SpeedController leftBack;
    private final SpeedController rightFront;
    private final SpeedController rightBack;
    private double leftFrontSF = 1;
    private double leftBackSF = 1;
    private double rightFrontSF = -1;
    private double rightBackSF = -1;
    
    private boolean hasShifters = false;
    private GRTSolenoid leftShifter, rightShifter;
    
    private boolean hasEncoders = false;
    private GRTEncoder leftEncoder, rightEncoder;

    double power = 1;    //State variable determining if we run at 1/2 power. 
    
    /**
     * Constructs a new drivetrain.
     *
     * @param leftFront left front motor
     * @param leftBack left back motor
     * @param rightFront right front motor
     * @param rightBack right back motor
     */
    public GRTDriveTrain(SpeedController leftFront, SpeedController leftBack,
            SpeedController rightFront, SpeedController rightBack) {
        
        this(leftFront, leftBack, rightFront, rightBack,
                null, null, null, null);
    }
    
    public GRTDriveTrain(SpeedController leftFront, SpeedController leftBack,
            SpeedController rightFront, SpeedController rightBack,
            GRTEncoder leftEncoder, GRTEncoder rightEncoder) {
        
        this(leftFront, leftBack, rightFront, rightBack,
                null, null,
                leftEncoder, rightEncoder);
    }

    public GRTDriveTrain(SpeedController leftFront, SpeedController leftBack,
            SpeedController rightFront, SpeedController rightBack,
            GRTSolenoid leftShifter, GRTSolenoid rightShifter) {

        this(leftFront, leftBack, rightFront, rightBack,
                leftShifter, rightShifter,
                null, null);
    }
    
    public GRTDriveTrain(SpeedController leftFront, SpeedController leftBack,
            SpeedController rightFront, SpeedController rightBack,
            GRTSolenoid leftShifter, GRTSolenoid rightShifter,
            GRTEncoder leftEncoder, GRTEncoder rightEncoder) {
        
        super("Drivetrain");
        
        this.leftFront = leftFront;
        this.rightFront = rightFront;
        this.leftBack = leftBack;
        this.rightBack = rightBack;
        
        if(leftShifter != null && rightShifter != null) {
            this.hasShifters = true;
            this.leftShifter = leftShifter;
            this.rightShifter = rightShifter;
        }
        
        if(leftEncoder != null && rightEncoder != null) {
            this.hasEncoders = true;
            this.leftEncoder = leftEncoder;
            this.rightEncoder = rightEncoder;
        }
        
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
     * Set the left and right side speeds of the drivetrain motors.
     *
     * @param leftVelocity left drivetrain velocity
     * @param rightVelocity right drivetrain velocity
     */
    public void setMotorSpeeds(double leftVelocity, double rightVelocity) {
        logInfo("Left: " +  leftVelocity +"\tRight: " + rightVelocity);
        
//        if (Math.abs(leftVelocity) >= 0.06){
            leftFront.set(leftVelocity * leftFrontSF * power);
            leftBack.set(leftVelocity * leftBackSF * power);
//        } else {
//            logInfo("Dead zone");
//            leftFront.set(0.0);
//            leftBack.set(0.0);
//        }
        
//        if (Math.abs(rightVelocity) >= 0.06){
            rightBack.set(rightVelocity * rightBackSF * power);
            rightFront.set(rightVelocity * rightFrontSF * power);
//        } else {
//            logInfo("Dead zone");
//            rightFront.set(0.0);
//            rightBack.set(0.0);
//        }
    }
        
    /**
     * Set the relative power output of the drivetrain
     *
     * @param power Percentage of power output (double between 0 and 1)
     */
    public void setPower(double power){
        if(power > 1) {
            this.power = 1;
        } else if (power < 0) {
            this.power = 0;
        } else {
            this.power = power;
        }
        logInfo("Power: " + this.power);
    }
    
    public void setFullPower() {
        this.power = 1;
    }
    
    public void shiftUp(){
        GRTLogger.logInfo("Shifting up");
        if(hasShifters){
            leftShifter.set(false);
            rightShifter.set(false); 
        }
    }
    
    public void shiftDown(){
        GRTLogger.logInfo("Shifting down");
        if(hasShifters){
            leftShifter.set(true);
            rightShifter.set(true);
        }
    }
    
    public GRTEncoder getLeftEncoder(){
        return rightEncoder;
    }
    
    public GRTEncoder getRightEncoder(){
        return rightEncoder;
    }
}
