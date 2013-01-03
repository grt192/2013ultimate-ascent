package actuator;

/**
 * Talon wrapper class. Behaves exactly like the Victor class.
 * 
 * @author calvin
 */
public class GRTTalon extends GRTVictor {

    /**
     * Instantiates a Talon controller on the default digital module.
     *
     * @param channel number of PWM output this Talon is attached to
     * @param name name of motor
     */
    public GRTTalon(int channel, String name) {
        super(channel, name);
    }

    /**
     * Instantiates a Talon controller.
     *
     * @param moduleNum digital module number
     * @param channel number of PWM output this Victor is attached to
     * @param name name of motor
     */
    public GRTTalon(int moduleNum, int channel, String name) {
        super(moduleNum, channel, name);
    }
    
}
