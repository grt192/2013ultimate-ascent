/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package macro;

import core.GRTMacro;
import mechanism.GRTDriveTrain;
import sensor.GRTEncoder;
import logger.GRTLogger;

/**
 *
 * @author Darshan
 */
public class MacroDriveTrapezoidal extends GRTMacro {
    private GRTDriveTrain dt;
    private double distance = 0;
    private double velocity = 1;
    
    private double leftInitialDistance;
    private double rightInitialDistance;
    
    private GRTEncoder leftEncoder;
    private GRTEncoder rightEncoder;
    
    private double leftSpeed;
    private double rightSpeed;
    
    private int accTime; //TODO
    private int decTime;
    private int driveTime;
    
    private double vMax = 1.0;
    
    // TODO
    private double accDistance;
    private double decDistance;
    
    private double updateTime = .050;
    
    private double accStep;
    private double decStep;
    private double speedStep = 0.0;
    private double speedDecStep = 0.0;
    private boolean accelerating = true;
    private boolean driving = false;
    double time = 0;
    int totTime = 0;
    int startTime;
    
    public MacroDriveTrapezoidal(GRTDriveTrain dt, double distance, int timeout,
            GRTEncoder leftEncoder, GRTEncoder rightEncoder, int accTime, 
            int decTime) {
        super("Drive Macro", timeout);
        this.dt = dt;
        this.distance = distance;
        this.leftEncoder = leftEncoder;
        this.rightEncoder = rightEncoder;
        this.accTime = accTime;
        this.decTime = decTime;
    }

    protected void initialize() {
        accDistance = .5 * vMax * accTime/1000;
        decDistance = .5 * vMax * decTime/1000;
        accStep = vMax/(accTime/(updateTime * 1000));
        decStep = vMax/(decTime/(updateTime * 1000));
        startTime = (int) System.currentTimeMillis();
        
        //setSleepTime(updateTime);
        if(distance < 0){
            accDistance = -accDistance;
            decDistance = -decDistance;
            accStep = -accStep;
            decStep = -decStep;
        }
        driveTime = (int) ((distance - accDistance - decDistance)/vMax);
        setSleepTime(50);
    }

    protected void perform() {

//        GRTLogger.logInfo("Encoder distance = " + (leftEncoder.getRate()));
//        GRTLogger.logInfo("Encoder distance = " + (rightEncoder.getDistance()));
//        GRTLogger.logInfo("Encoder distance = " + ((leftEncoder.getDistance() + rightEncoder.getDistance())/2));
        GRTLogger.logInfo("" + accDistance + "  " + decDistance);
        GRTLogger.logInfo("" + distance);
        double distanceDriven = (leftEncoder.getDistance() + rightEncoder.getDistance())/2;
        time = System.currentTimeMillis() - startTime;
        totTime += 50;
        GRTLogger.logInfo("Time = " + totTime);
        GRTLogger.logInfo("Current Time " + time);
        GRTLogger.logInfo("Distance = " + distanceDriven);

        if(Math.abs(accDistance + decDistance) < Math.abs(distance)){
            GRTLogger.logInfo("In main method");
            if(accelerating){
                GRTLogger.logInfo("Accelerating");
//                if((speedStep > 1) || (speedStep < -1)){
                if(time > accTime){
                    GRTLogger.logInfo("Reached full speed");
                    accelerating = false;
                    driving = true;
                    dt.setMotorSpeeds(1.0, 1.0);
                    startTime = (int) System.currentTimeMillis();
                }
                else{
//                    double speed = vFunction(speedStep);
                    GRTLogger.logInfo("Current Time " + time);

                    double speed = vFunction(time/accTime);
                    GRTLogger.logInfo("Speed Step = " + speedStep);
                    dt.setMotorSpeeds(speed, speed);
                    speedStep += accStep;
                    GRTLogger.logInfo("Speed = " + speed);
                    
                }
            }
            else if(driving){
                GRTLogger.logInfo("Driving");
                GRTLogger.logInfo("Distance = " + distanceDriven);

//                if(Math.abs(distance - distanceDriven) < Math.abs(decDistance)){
                if(driveTime < time){
                    driving = false;
                    startTime = (int) System.currentTimeMillis();
                }
            }
            else{
                GRTLogger.logInfo("Decelerating");                
//                if((speedDecStep > 1) || (speedDecStep < -1)){
                if(decTime < time){
                    dt.setMotorSpeeds(0.0, 0.0);
                }
                else{
                    double speed;
                    if(distance > 0){
                        speed = 1 - vFunction(time/decTime);
                    }
                    else{
                        speed = -1 + vFunction(time/decTime);
                    }
                    dt.setMotorSpeeds(speed, speed);
                    speedStep -= decStep;
                    speedDecStep -= decStep;
                }
            }
        }
        else{
            if(accelerating){
                GRTLogger.logInfo("Accelerating");
                if((2 * distanceDriven) > distance){
                    accelerating = false;
                    dt.setMotorSpeeds(1.0, 1.0);

                }
                else{
                    double speed = vFunction(speedStep);
                    dt.setMotorSpeeds(speed, speed);
                    speedStep += accStep;
                }
            }
            else{
                GRTLogger.logInfo("Decelerating");                              
                if(distanceDriven > distance){
                    dt.setMotorSpeeds(0.0, 0.0);
                }
                else{
                    double speed = vFunction(speedStep);
                    dt.setMotorSpeeds(speed, speed);
                    speedStep -= decStep;
                }
            }

        }
        //        double drivenDistance = (rightEncoder.getDistance() + leftEncoder.getDistance()) / 2;
//        
//        if((accDistance + decDistance) > distance){
//            if(drivenDistance < distance/2){
//                double speed = vFunction(drivenDistance/accDistance);
//                dt.setMotorSpeeds(speed, speed);
//            }
//            else{
//                double speed = 1 - vFunction((distance - drivenDistance)/decDistance);
//                dt.setMotorSpeeds(speed, speed);            
//            }
//        }
//        else{
//            if(drivenDistance < accDistance){
//                double speed = vFunction(drivenDistance/accDistance);
//                dt.setMotorSpeeds(speed, speed);
//            }
//            else if((distance - drivenDistance) < decDistance){
//                double speed = 1 - vFunction((distance - drivenDistance)/decDistance);
//                dt.setMotorSpeeds(speed, speed);
//            }
//        }
        
//        time = drivenDistance / distance;
//        vel = vFunction(time);
//        pos += vel;
//        
//        double vStep = vMax / (accTime/updateTime);
//        int steps = 1/(accTime/updateTime);
//        double motorSpeed = 0.0;
//        if((accDistance + decDistance) > distance){
//            
//            if(drivenDistance < distance/2){
//                double speed = vFunction(drivenDistance/accDistance);
//                dt.setMotorSpeeds(speed, speed);
//            }
//            else{
//                double speed = 1 - vFunction((distance - drivenDistance)/decDistance);
//                dt.setMotorSpeeds(speed, speed);            
//            }
//        }
//        else{
//            if(drivenDistance < accDistance){
//                double speed = vFunction(drivenDistance/accDistance);
//                dt.setMotorSpeeds(speed, speed);
//            }
//            else if((distance - drivenDistance) < decDistance){
//                double speed = 1 - vFunction((distance - drivenDistance)/decDistance);
//                dt.setMotorSpeeds(speed, speed);
//            }
//        }
        
    }

    public void die() {
        dt.setMotorSpeeds(0.0, 0.0);
    }

    private void accelerate(int time) {
        for(int i = 0; i < time - updateTime; i += updateTime){
            double speed = vFunction(i);
            dt.setMotorSpeeds(speed, speed);
        }
        dt.setMotorSpeeds(1.0, 1.0);
    }

    private void decelerate(int time) {
        for(int i = 0; i < time; i += updateTime){
            double speed = 1 - vFunction(i);
            dt.setMotorSpeeds(speed, speed);
            
        }
        dt.setMotorSpeeds(0.0, 0.0);

    }

    private void drive(int time) {
        
    }

    private double vFunction(double i) {
        return i;
    }
}
