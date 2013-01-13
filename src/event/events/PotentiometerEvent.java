package event.events;

import core.Sensor;
import sensor.Potentiometer;

/**
 *
 * @author calvin
 */
public class PotentiometerEvent extends SensorEvent {

    public PotentiometerEvent(Sensor source, double value) {
        super(source, 0, value);
    }

    public double getAngle() {
        return getData();
    }
}
