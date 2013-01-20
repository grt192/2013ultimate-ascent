package event.events;

import sensor.GRTVisionTracker;

/**
 * The subclass of SensorEvent that captures all the necessary information
 * to pass to VisionTrackerListeners when there is a change in state of
 * a GRTVisionTracker.
 * 
 * @author andrew
 */
public class VisionTrackerEvent extends SensorEvent {
    
    public VisionTrackerEvent(GRTVisionTracker src, int id, double newDatum){
        super(src, id, newDatum);
    }
}
