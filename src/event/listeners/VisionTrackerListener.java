/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package event.listeners;

import event.events.VisionTrackerEvent;

/**
 * An interface implemented by all classes that listen to the vision tracker
 * 
 * @author agd
 */
public interface VisionTrackerListener {
    
    /**
     * Called when the X-coordinate of the centroid in the image has changed.
     * @param e The VisionTrackerEvent that captures the change
     */
    public void centroidXChanged(VisionTrackerEvent e);
    
    /**
     * Called when the Y-coordinate of the centroid in the image has changed.
     * @param e The VisionTrackerEvent that captures the change
     */
    public void centroidYChanged(VisionTrackerEvent e);
    
    /**
     * Called when the normalized X-coordinate of the centroid in the image has changed.
     * @param e The VisionTrackerEvent that captures the change
     */
    public void centroidXNormalizedChanged(VisionTrackerEvent e);
    
    /**
     * Called when the normalized Y-coordinate of the centroid in the image has changed.
     * @param e The VisionTrackerEvent that captures the change
     */
    public void centroidYNormalizedChanged(VisionTrackerEvent e);
    
    /**
     * Called when the distance from the camera to the target has changed
     * @param e The VisionTrackerEvent that captures the change.
     */
    public void centroidDistanceChanged(VisionTrackerEvent e);
    
}
