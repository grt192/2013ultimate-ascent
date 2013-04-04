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

    private Vector macros;
    private int currentIndex = -1;


    /**
     * Creates a new GRTMacroController.
     * @param macros list of macros to run
     */
    public GRTMacroController(Vector macros) {
        super("Macro controller");
        this.macros = macros;
        System.out.println("Number of macros: " + macros.size());
    }

    protected void startListening() {
        GRTLogger.logInfo("start listen");
        currentIndex = -1;
        for (Enumeration en = macros.elements(); en.hasMoreElements();) {
            GRTMacro m = (GRTMacro) en.nextElement();
            m.reset();
            m.addListener(this);
        }
        
        startNextMacro();
    }

    protected void stopListening() {
        System.out.println("Disabling macrocontroller");
        for (Enumeration en = macros.elements(); en.hasMoreElements();) {
            GRTMacro m = (GRTMacro) en.nextElement();
            System.out.println("\tKilling macro " + m);
            m.removeListener(this);    
            if (!m.isDone()){
                m.die();
            }
        }
    }   

    public void macroInitialized(MacroEvent e) {
        GRTLogger.logInfo("Initialized macro: " + e.getSource().getID());
    }

    public void macroDone(MacroEvent e) {
        GRTLogger.logInfo("Completed macro: " + e.getSource().getID());
            startNextMacro();
    }

    public void macroTimedOut(MacroEvent e) {
        GRTLogger.logError("Macro " + e.getSource().getID() +
                " timed out. Skipping macros.");
    }
    
    private void startNextMacro() {
//        if (!enabled) {
//            System.out.println("startNextMacro while unenabled");
//            return;
//        }
        
        System.out.println("Next macro up to bat!");
        if (++currentIndex < macros.size()) {
            System.out.println("Starting new Macros!");
            GRTMacro macro = (GRTMacro) macros.elementAt(currentIndex);
//            if (concurrentMacros.contains(macro)) 
//            {
//                System.out.println("\tIt's a concurrent macro! " + macro.getID());
//                (new ConcurrentMacroRunner(macro)).execute();
//                startNextMacro();
//            }
//            else{
                System.out.println("\tIt's a regular macro! " + macro.getID());
                macro.execute();
//            }
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
            if (enabled) m.execute();
        }
        
        public void execute() {
            (new Thread(this)).start();
        }
    }
}
