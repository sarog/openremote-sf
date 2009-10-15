package org.openremote.controller.service;

import org.openremote.controller.command.CommandType;

/**
 * The Interface ControlCommandService.
 * @author Handy.Wang
 */
public interface ControlCommandService {

    /**
     * Trigger.
     * 
     * @param buttonID the button id
     * @param commandType the command type
     */
    void trigger(String buttonID, CommandType commandType);
    
    /**
     * Trigger.
     * 
     * @param buttonID the button id
     */
    void trigger(String buttonID);
}
