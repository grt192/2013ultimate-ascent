package controller;

import core.EventController;
import event.events.VisionTrackerEvent;
import event.listeners.VisionTrackerListener;
import logger.GRTLogger;
import sensor.GRTVisionTracker;

/**
 *
 * @author Andrew Duffy <gerberduffy@gmail.com>
 */
public class TrackerController extends EventController implements VisionTrackerListener {

    private GRTVisionTracker tracker;

    public TrackerController(GRTVisionTracker track){
        super("Vision Tracker Autonomous Controller");
        this.tracker = track;
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
        if (e.getData() <= -.05){
            GRTLogger.logInfo("<< left");
        } else if(e.getData() >= .05){
                GRTLogger.logInfo("right >> ");
        } else {
            GRTLogger.logSuccess("Locked on!");
        }
    
    }

    public void centroidYNormalizedChanged(VisionTrackerEvent e) {
    }

    public void centroidDistanceChanged(VisionTrackerEvent e) {
    }
    
}
