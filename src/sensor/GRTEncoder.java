package sensor;

import core.Sensor;
import edu.wpi.first.wpilibj.Encoder;
import event.events.EncoderEvent;
import event.listeners.EncoderListener;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Wrapper class for a quadrature encoder.
 *
 * @author gerberduffy
 */
public class GRTEncoder extends Sensor {

    private Encoder rotaryEncoder;
    private double distancePerPulse;
    public static final int KEY_DISTANCE = 0;
    public static final int KEY_DEGREES = 1;
    public static final int KEY_DIRECTION = 2;
    public static final int KEY_STOPPED = 3;
    public static final int KEY_RATE = 4;
    
    public static final int NUM_DATA = 5;
    private Vector encoderListeners;

    /**
     * Instantiates an encoder on the default digital module.
     *
     * @param channelA digital channel for encoder channel A
     * @param channelB digital channel for encoder channel B
     * @param pulseDistance distance traveled for each pulse (typically 1 degree
     * of rotation per pulse)
     * @param pollTime how often to poll
     * @param name name of encoder
     */
    public GRTEncoder(int channelA, int channelB, double pulseDistance,
            int pollTime, String name) {
        this (channelA, channelB, pulseDistance, pollTime, false, name);
    }
    
    /**
     * Instantiates an encoder on the default digital module.
     *
     * @param channelA digital channel for encoder channel A
     * @param channelB digital channel for encoder channel B
     * @param pulseDistance distance traveled for each pulse (typically 1 degree
     * of rotation per pulse)
     * @param pollTime how often to poll
     * @param reversed whether or not the encoder is reversed
     * @param name name of encoder
     */
    public GRTEncoder(int channelA, int channelB,
            double pulseDistance, int pollTime, boolean reversed, String name) {
        super(name, pollTime, NUM_DATA);
        rotaryEncoder = new Encoder(channelA, channelB, reversed);
        rotaryEncoder.start();

        encoderListeners = new Vector();
        distancePerPulse = pulseDistance;
        rotaryEncoder.setDistancePerPulse(distancePerPulse);
    }
    
    /**
     * Instantiates an encoder.
     *
     * @param moduleNum number of digital module
     * @param channelA digital channel for encoder channel A
     * @param channelB digital channel for encoder channel B
     * @param pulseDistance distance traveled for each pulse (typically 1 degree
     * of rotation per pulse)
     * @param pollTime how often to poll
     * @param name name of encoder
     */
    public GRTEncoder(int moduleNum, int channelA, int channelB,
            double pulseDistance, int pollTime, String name) {
        this(moduleNum, channelA, channelB,
                pulseDistance, pollTime, false, name);
    }

    /**
     * Instantiates an encoder.
     *
     * @param moduleNum number of digital module
     * @param channelA digital channel for encoder channel A
     * @param channelB digital channel for encoder channel B
     * @param pulseDistance distance traveled for each pulse (typically 1 degree
     * of rotation per pulse)
     * @param pollTime how often to poll
     * @param reversed whether or not the encoder is reversed
     * @param name name of encoder
     */
    public GRTEncoder(int moduleNum, int channelA, int channelB,
            double pulseDistance, int pollTime, boolean reversed, String name) {
        super(name, pollTime, NUM_DATA);
        rotaryEncoder = new Encoder(moduleNum, channelA,
                moduleNum, channelB, reversed);
        rotaryEncoder.start();
        
        encoderListeners = new Vector();
        distancePerPulse = pulseDistance;
        rotaryEncoder.setDistancePerPulse(this.distancePerPulse);
    }
    
    public double getDistance() {
        return rotaryEncoder.getDistance();
    }
    
    public double getRate() {
        return rotaryEncoder.getRate();
    }
    
    public double getAngle() {
        return getDistance() / distancePerPulse;
    }

    protected void poll() {
        setState(KEY_DISTANCE, getDistance());
        setState(KEY_DEGREES, getAngle());
        setState(KEY_RATE, getRate());
        setState(KEY_DIRECTION, rotaryEncoder.getDirection() ? TRUE : FALSE);
        setState(KEY_STOPPED, rotaryEncoder.getStopped() ? TRUE : FALSE);
    }

    protected void notifyListeners(int id, double newDatum) {
        EncoderEvent e = new EncoderEvent(this, id, newDatum);
            
        switch (id) {
            case KEY_DEGREES:
                for (Enumeration en = encoderListeners.elements(); en.
                        hasMoreElements();)
                    ((EncoderListener) en.nextElement()).degreeChanged(e);
                break;
            case KEY_DISTANCE:
                for (Enumeration en = encoderListeners.elements(); en.
                        hasMoreElements();)
                    ((EncoderListener) en.nextElement()).distanceChanged(e);
                break;
            case KEY_RATE:
                for (Enumeration en = encoderListeners.elements(); en.
                        hasMoreElements();)
                    ((EncoderListener) en.nextElement()).rateChanged(e);
                break;
            case KEY_STOPPED:
                if (newDatum == TRUE)
                    for (Enumeration en = encoderListeners.elements(); en.
                            hasMoreElements();)
                        ((EncoderListener) en.nextElement()).rotationStopped(e);
                else
                    for (Enumeration en = encoderListeners.elements(); en.
                            hasMoreElements();)
                        ((EncoderListener) en.nextElement()).rotationStarted(e);
                break;
        }
    }

    public void addListener(EncoderListener l) {
        encoderListeners.addElement(l);
    }

    public void removeListener(EncoderListener l) {
        encoderListeners.removeElement(l);
    }
}
