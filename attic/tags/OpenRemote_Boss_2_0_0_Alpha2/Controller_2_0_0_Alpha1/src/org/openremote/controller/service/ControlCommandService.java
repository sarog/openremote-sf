package org.openremote.controller.service;


/**
 * The Interface ControlCommandService.
 * @author Handy.Wang
 */
public interface ControlCommandService {

    /**
     * Trigger.
     * 
     * @param buttonID the button id
     * @param commandParam the command type
     */
    void trigger(String buttonID, String commandParam);
    
}
