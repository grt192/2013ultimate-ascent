/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package actuator;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.livewindow.LiveWindowSendable;
import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.first.wpilibj.tables.ITableListener;

/**
 * Wrapper class for solenoids. Done to allow for interchangeablity between
 * solenoids on the solenoid module, and solenoids on spikes.
 * 
 * @author Calvin
 */
public class GRTSolenoid implements LiveWindowSendable {
    
    private Solenoid solenoid;
    
    /**
     * Constructs a solenoid on the default solenoid module.
     * 
     * @param channel Channel on the module. 
     */
    public GRTSolenoid(final int channel) {
        solenoid = new Solenoid(channel);
    }
    
    /**
     * Constructs a solenoid on a solenoid module.
     * 
     * @param moduleNumber Number of solenoid module.
     * @param channel Channel on the module.
     */
    public GRTSolenoid(final int moduleNumber, final int channel) {
        solenoid = new Solenoid(moduleNumber, channel);
    }
    
    /**
     * Package-private constructor for use in GRTDoubleActuator.
     */
    GRTSolenoid() {}
    
    /**
     * Sets the state of the solenoid.
     * 
     * @param on true to activate, false to deactivate 
     */
    public void set(boolean on) {
        solenoid.set(on);
    }
    
    /**
     * Gets the state of the solenoid.
     * 
     * @return true if active, false otherwise 
     */
    public boolean get() {
        return solenoid.get();
    }

    /**
     * Free the solenoid.
     */
    public void free() {
        if (solenoid != null)
            solenoid.free();
    }
    
    //Live window code, copied verbatim from WPILibJ.
    
    /*
     * Live Window code, only does anything if live window is activated.
     */
    public String getSmartDashboardType(){
        return "Solenoid";
    }
    private ITable m_table;
    private ITableListener m_table_listener;
    
    /**
     * {@inheritDoc}
     */
    public void initTable(ITable subtable) {
        m_table = subtable;
        updateTable();
    }
    
    /**
     * {@inheritDoc}
     */
    public ITable getTable(){
        return m_table;
    }
    
    /**
     * {@inheritDoc}
     */
    public void updateTable() {
        if (m_table != null) {
            m_table.putBoolean("Value", get());
        }
    }
    

    /**
     * {@inheritDoc}
     */
    public void startLiveWindowMode() {
        set(false); // Stop for safety
        m_table_listener = new ITableListener() {
            public void valueChanged(ITable itable, String key, Object value, boolean bln) {
                System.out.println(key+": "+value);
                set(((Boolean) value).booleanValue());
            }
        };
        m_table.addTableListener("Value", m_table_listener, true);
    }
    
    /**
     * {@inheritDoc}
     */
    public void stopLiveWindowMode() {
        set(false); // Stop for safety
        // TODO: Broken, should only remove the listener from "Value" only.
        m_table.removeTableListener(m_table_listener);
    }
}
