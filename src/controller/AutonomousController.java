/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import core.EventController;
import core.GRTConstants;
import event.events.ButtonEvent;
import event.events.PotentiometerEvent;
import event.listeners.ButtonListener;
import event.listeners.PotentiometerListener;
import macro.MacroDrive;
import mechanism.Belts;
import mechanism.Climber;
import mechanism.ExternalPickup;
import mechanism.GRTDriveTrain;
import mechanism.Shooter;

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
    
    public AutonomousController(Shooter shooter, ExternalPickup pickerUpper,
            Climber climber, Belts belts, GRTDriveTrain dt)
    {
        super("Autonomous Controller");
        this.dt = dt;
        this.pickerUpper = pickerUpper;
        this.climber = climber;
        this.belts = belts;
        this.shooter = shooter;
    }
    
    public void startListening()
    {
        
    }

    public void stopListening()
    {
        
    }
    
    public void start()
    {
        shooter.setAngle(GRTConstants.getValue("autoShooterHeight1"));
        shooter.shoot();
        try{
        Thread.sleep(500);
        } catch (InterruptedException e){
            System.out.println("The shooter sleeping got interrupted. This"
                    + "shouldn't be a thing. ");
        }
        shooter.unShoot();
        MacroDrive drive = new MacroDrive(dt, GRTConstants.getValue("autoDistance1"), GRTConstants.getValue("autoTimeout1"));
    }
    
}
