package actuator;

import edu.wpi.first.wpilibj.Relay;

/**
 * Controls two actuators (e.g. solenoids)
 * independently with a single spike relay.
 * 
 * Actuator 1 is connected to the M+ pin on the spike (and ground),
 * Actuator 2 is connected to the M- pin.
 * 
 * @author Calvin Huang <smartycalvin@gmail.com>
 */
public class GRTDoubleActuator {
    
    private Relay relay;
    private boolean act1State = false;
    private boolean act2State = false;
    
    /**
     * Creates a new GRTDoubleActuator.
     * @param channel Relay channel to which the relay is connected.
     */
    public GRTDoubleActuator(int channel) {
        relay = new Relay(channel);
    }
    
    /**
     * Creates a new GRTDoubleActuator.
     * @param slot Digital module slot to which the relay is connected.
     * @param channel Relay channel to which the relay is connected.
     */
    public GRTDoubleActuator(int slot, int channel) {
        relay = new Relay(slot, channel);
    }
    
    /**
     * Gets the state of actuator 1.
     * @return true if the actuator is energized, false otherwise
     */
    public boolean getFirstActuatorState() {
        return act1State;
    }
    
    /**
     * Gets the state of actuator 2.
     * @return true if the actuator is energized, false otherwise
     */
    public boolean getSecondActuatorState() {
        return act2State;
    }
    
    /**
     * Set the state of actuator 1.
     * @param state true to energize the actuator, false to deactivate it
     */
    public void setFirstActuatorState(boolean state) {
        act1State = state;
        updateState();
    }
    
    /**
     * Set the state of actuator 2.
     * @param state true to energize the actuator, false to deactivate it
     */
    public void setSecondActuatorState(boolean state) {
        act2State = state;
        updateState();
    }
    
    private void updateState() {
        Relay.Value value;
        
        if (act1State) {
            if (act2State) {
                value = Relay.Value.kOn;
            }
            else {
                value = Relay.Value.kForward;
            }
        } else {
            if (act2State) {
                value = Relay.Value.kReverse;
            }
            else {
                value = Relay.Value.kOff;
            }
        }
        
        relay.set(value);
    }
}
