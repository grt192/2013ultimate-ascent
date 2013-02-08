package controller;

import core.EventController;
import event.events.VisionTrackerEvent;
import event.listeners.VisionTrackerListener;
import logger.GRTLogger;
import mechanism.GRTDriveTrain;
import sensor.GRTVisionTracker;

/**
 *
 * @author Andrew Duffy <gerberduffy@gmail.com>
 */
public class TrackerController extends EventController implements VisionTrackerListener {

    private GRTVisionTracker tracker;
    private GRTDriveTrain dt;

    private static final double X_THRESHOLD = 0.05;

    public TrackerController(GRTVisionTracker track, GRTDriveTrain dt){
        super("Vision Tracker Autonomous Controller");
        this.tracker = track;
        this.dt = dt;
    }
    protected void startListening() {
        tracker.enable();
        tracker.addVisionTrackerListener(this);
    }

    protected void stopListening() {
        tracker.remoteVisionTrackerListener(this);
    }

    public void centroidXChanged(VisionTrackerEvent e) {
    }

    public void centroidYChanged(VisionTrackerEvent e) {
    }

    public void centroidXNormalizedChanged(VisionTrackerEvent e) {
        logInfo("Normalized centroid x: " + e.getData());
        if (e.getData() <= -X_THRESHOLD) {
            GRTLogger.logInfo("<< left");
        } else if(e.getData() >= X_THRESHOLD) {
                GRTLogger.logInfo("right >> ");
        } else {
            GRTLogger.logSuccess("Locked on!");
        }
    
    }

    public void centroidYNormalizedChanged(VisionTrackerEvent e) {
    }

    public void centroidDistanceChanged(VisionTrackerEvent e) {
        logInfo("New Distance: " + e.getData());
        if (e.getData() >= 50){
            dt.setMotorSpeeds(0.3 , 0.3 );
        } else {
            dt.setMotorSpeeds(0.0, 0.0);
        }
    }
    
}
