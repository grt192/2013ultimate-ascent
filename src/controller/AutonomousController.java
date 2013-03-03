/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import core.EventController;
import core.GRTConstants;
import macro.MacroDrive;
import macro.MacroTurn;
import mechanism.Belts;
import mechanism.Climber;
import mechanism.ExternalPickup;
import mechanism.GRTDriveTrain;
import mechanism.Shooter;
import sensor.GRTGyro;

/**
 *
 * @author trevornielsen
 */
public class AutonomousController extends EventController
{

    private GRTDriveTrain dt;
    private ExternalPickup pickerUpper;
    private Climber climber;
    private Belts belts;
    private Shooter shooter;
    private GRTGyro gyro;
    
    public AutonomousController(GRTGyro gyro, Shooter shooter, ExternalPickup pickerUpper, Belts belts, GRTDriveTrain dt)
    {
        super("Autonomous Controller");
        this.dt = dt;
        this.pickerUpper = pickerUpper;
        this.climber = climber;
        this.belts = belts;
        this.shooter = shooter;
        this.gyro = gyro;
    }
    
    public void startListening()
    {
        
    }

    public void stopListening()
    {
        
    }
    
    public void start()
    {
        shoot(GRTConstants.getValue("autoShooterAngle1"));
        MacroDrive drive = new MacroDrive(dt, GRTConstants.getValue("autoDistance1"), 5000);
        
        
        MacroTurn turn = new MacroTurn(dt, gyro, GRTConstants.getValue("autoAngle1"), 5000);
    }
    
    private void shoot(double angle)
    {
        shooter.setAngle(angle);
        
        for(int i = 0; i < 5; i++)
        {
            shooter.shoot();
            try{
                Thread.sleep(500);
                shooter.unShoot();
                Thread.sleep(500);
            } catch (InterruptedException e) {
                System.out.println("Thread can't sleep. Thread should be able"
                        + "to sleep.");
            }
        }
    }
    
}
