/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

/**
 * A Dead Reckoner class to keep track of a starting position, and to map the 
 * field during autonomous
 * 
 * @author Sidd Karamcheti <sidd.karamcheti@gmail.com>
 */
public class DeadReckoner {
    private static double initialX, initialY, initialTheta;
    private static double currentX, currentY, currentTheta;
//    private static Vector xValues = new Vector();
//    private static Vector yValues = new Vector();
//    private static Vector thetaValues = new Vector();
    
    public DeadReckoner(double initialX, double initalY, double initialTheta) {
        DeadReckoner.initialX = initialX;
        DeadReckoner.initialY = initalY;
        DeadReckoner.initialTheta = initialTheta;
    }
    
    private static double degreesToRadians(double theta) {
        return theta * Math.PI / 180.0;
    }
        /**
     * Sets the position
     * @param x
     * @param y
     * @param theta
     */
    public static void setPosition(double x, double y, double theta) {
        DeadReckoner.currentX = x;
        DeadReckoner.currentY = y;
        DeadReckoner.currentTheta = theta;
    }
    /**
     * Adds a waypoint on the deadReckoner's path
     * For example, if you need to go to a specific location before returning
     * to the start, add it as a waypoint;
     * 
     * @param x
     * @param y
     * @param theta
     */
    
    public static void dropMarker(double x, double y, double theta) {
        //TBA
    }
    
    public static double getReturnTurnAngle() {
        return DeadReckoner.initialTheta - DeadReckoner.getTheta();
    }
    
    public static double getReturnY() {
        return DeadReckoner.initialY - DeadReckoner.getY();
    }
    
    public static double getReturnX() {
        return DeadReckoner.initialX - DeadReckoner.getX();
    }
    public static double getX() {
        return DeadReckoner.currentX;
    }
    
    public static double getY() {
        return DeadReckoner.currentY;
    }
    
    public static double getTheta() {
        return DeadReckoner.currentTheta;
    }
}
