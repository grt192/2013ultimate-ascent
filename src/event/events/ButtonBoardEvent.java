/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package event.events;

import core.Sensor;
import sensor.ButtonBoard;

/**
 * Button Board Event
 * @author Calvin
 */
public class ButtonBoardEvent extends SensorEvent {

    public ButtonBoardEvent(ButtonBoard source, int id, double data) {
        super(source, id, data);
    }
    
}
