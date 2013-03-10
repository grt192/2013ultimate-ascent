/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import event.events.MacroEvent;
import event.listeners.MacroListener;
import java.util.Enumeration;
import java.util.Vector;
import logger.GRTLogger;

/**
 * Creates a new MacroController that executes macros in sequence.
 *
 * Macros can also be executed concurrently with others, if added
 * to the concurrentMacros vector.
 * 
 * @author keshav, calvin
 */
public class GRTMacroController extends EventController implements MacroListener {

    private Vector macros, concurrentMacros;
    private int currentIndex = -1;

    /**
     * Creates a new GRTMacroController.
     * @param macros list of macros to run
     */
    public GRTMacroController(Vector macros) {
        this(macros, new Vector());
    }

    /**
     * Creates a new GRTMacroController.
     * @param macros list of macros to run
     * @param concurrentMacros macros which will be run in their own thread,
     * allowing multiple macros to run simultaneously
     */
    public GRTMacroController(Vector macros, Vector concurrentMacros) {
        super("Macro controller");
        this.macros = macros;
        this.concurrentMacros = concurrentMacros;
    }

    protected void startListening() {
        GRTLogger.logInfo("start listen");
        currentIndex = 0;
        for (Enumeration en = macros.elements(); en.hasMoreElements();) {
            GRTMacro m = (GRTMacro) en.nextElement();
            m.reset();
            m.addListener(this);
        }

        startNextMacro();
    }

    protected void stopListening() {
        for (Enumeration en = macros.elements(); en.hasMoreElements();) {
            ((GRTMacro) en.nextElement()).removeListener(this);
        }
    }

    public void macroInitialized(MacroEvent e) {
        GRTLogger.logInfo("Initialized macro: " + e.getSource().getID());
    }

    public void macroDone(MacroEvent e) {
        GRTLogger.logInfo("Completed macro: " + e.getSource().getID());
        if (!concurrentMacros.contains(e.getSource()))
            startNextMacro();
    }

    public void macroTimedOut(MacroEvent e) {
        GRTLogger.logError("Macro " + e.getSource().getID() +
                " timed out. Skipping macros.");
    }
    
    private void startNextMacro() {
        if (!enabled) {
            return;
        }
        
        currentIndex++;
        if (currentIndex < macros.size()) {
            GRTMacro macro = (GRTMacro) macros.elementAt(currentIndex);
            if (concurrentMacros.contains(macros)) {
                (new ConcurrentMacroRunner(macro)).execute();
                startNextMacro();
            }
            else
                macro.execute();
        } else {
            GRTLogger.logSuccess("Completed all macros. Waiting for teleop!");
        }
    }
    
    private class ConcurrentMacroRunner implements Runnable {

        private GRTMacro m;
        
        private ConcurrentMacroRunner(GRTMacro m) {
            this.m = m;
        }
        
        public void run() {
            m.execute();
        }
        
        public void execute() {
            (new Thread(this)).start();
        }
    }
}
