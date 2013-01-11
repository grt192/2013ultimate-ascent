/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import deploy.GRTRobot;
import event.events.MacroEvent;
import event.listeners.MacroListener;
import java.util.Enumeration;
import java.util.Vector;
import logger.GRTLogger;


    /**
 *
 * @author andrew, keshav
 */
public abstract class GRTMacro {

    protected boolean hasCompletedExecution = false;
    protected boolean hasTimedOut = false;
    protected boolean hasInitialized = false;
    
    private Vector macroListeners;
    private boolean hasStarted = false;
    private double timeout = 500;
    private double startTime = 0;
    private String name;
    
    private static final int NOTIFY_INITIALIZE = 0;
    private static final int NOTIFY_COMPLETED = 1;
    private static final int NOTIFY_TIMEDOUT = 2;
            

   
    /**
     * A GRTMacro specifies code to complete one discrete motion (ie. Turn a specified angle, drive a specific distance). Most useful for autonomous mode
     *
     * @param name Name of macro
     * @param timeout Time in ms after which macro will automatically stop execution
     */
    public GRTMacro(String name, double timeout) {
        this.name = name;
        this.timeout = timeout;
        macroListeners = new Vector();
    }
    
    public void execute() {        
        if(!hasStarted){
            hasStarted = true;
            initialize();
            notifyListeners(NOTIFY_INITIALIZE);            
            this.startTime = System.currentTimeMillis();            
            while (!hasCompletedExecution && !hasTimedOut) {
                if((System.currentTimeMillis() - startTime) > timeout){                    
                    hasTimedOut = true;
                    die();
                    notifyListeners(NOTIFY_TIMEDOUT);
                    return;
                }
                perform();

                try {
                    Thread.sleep(50);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
            die();
            notifyListeners(NOTIFY_COMPLETED);
        }       
    }
    
    /**
     * 
     * @return state of macro initialization
     */
    public boolean isInitialized() {
        return hasInitialized;
    }
     
    public boolean isDone() {
        return hasCompletedExecution;
    }

    public boolean isTimedOut() {
        return hasTimedOut;
    }
        
    
    public void reset() {
        hasCompletedExecution = hasStarted = hasTimedOut = false;
    }
    
    /**
     * Implemented on a per-macro basis
     */
    protected abstract void initialize();

    /**
     * Implemented on a per-macro basis
     */
    protected abstract void perform();

    /**
     * After executing
     */
    public abstract void die();
    
 
    public void addListener(MacroListener l) {
        macroListeners.addElement(l);
    }

    public void removeListener(MacroListener l) {
        macroListeners.removeElement(l);
    }
    
    private void notifyListeners(int id){
         switch (id) {
            case NOTIFY_COMPLETED:
                for (Enumeration en = macroListeners.elements(); en.
                        hasMoreElements();)
                    ((MacroListener) en.nextElement()).macroDone(new MacroEvent(name));
                break;
            case NOTIFY_TIMEDOUT:
                for (Enumeration en = macroListeners.elements(); en.
                        hasMoreElements();)
                    ((MacroListener) en.nextElement()).macroTimedOut(new MacroEvent(name));
                break;
            case NOTIFY_INITIALIZE:
                for (Enumeration en = macroListeners.elements(); en.
                        hasMoreElements();)
                    ((MacroListener) en.nextElement()).macroInitialized(new MacroEvent(name));
                break;
         }
    
    }
}
