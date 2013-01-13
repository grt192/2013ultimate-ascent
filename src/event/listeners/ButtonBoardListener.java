/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package event.listeners;

import event.events.ButtonBoardEvent;

/**
 *
 * @author Calvin
 */
public interface ButtonBoardListener {
    public void buttonPressed(ButtonBoardEvent e);
    public void buttonReleased(ButtonBoardEvent e);
    public void potentiometerChange(ButtonBoardEvent e);
}
