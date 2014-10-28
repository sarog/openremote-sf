package org.openremote.modeler.utils.dtoconverter;

import org.openremote.modeler.domain.CommandRefItem;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.Sensor;
import org.openremote.modeler.domain.Switch;
import org.openremote.modeler.domain.SwitchSensorRef;
import org.openremote.modeler.logging.LogFacade;
import org.openremote.modeler.shared.dto.DTOReference;
import org.openremote.modeler.shared.dto.SwitchDetailsDTO;

public class SwitchDTOConverter {

  private static LogFacade persistenceLog = LogFacade.getInstance(LogFacade.Category.PERSISTENCE);

  public static SwitchDetailsDTO createSwitchDetailsDTO(Switch sw) {
    // resolve read data into data transfer objects...

    DTOReference sensor     = resolveSensor(sw);
    DTOReference onCommand  = resolveOnCommand(sw);
    DTOReference offCommand = resolveOffCommand(sw);

    String onCommandDisplayName = sw.getSwitchCommandOnRef().getDeviceCommand().getDisplayName();
    String offCommandDisplayName = sw.getSwitchCommandOffRef().getDeviceCommand().getDisplayName();

    long switchID = sw.getOid();

    String switchName = sw.getName();

    return new SwitchDetailsDTO(
        switchID, switchName,
        sensor,
        onCommand, onCommandDisplayName,
        offCommand, offCommandDisplayName
    );
  }
  
  /**
   * Attempts to resolve the associated sensor for this switch into a data transfer object
   * reference. Mainly this is used in preparation for a data transfer object graph that
   * disconnects the persistent switch entity from the client side processing.
   *
   * TODO :
   *   - if no sensor has been associated, currently still returns an empty DTOReference,
   *     should return null instead (see MODELER-313)
   *
   * @param sw  a persistent switch entity
   *
   * @return  a data transfer object reference for a persistent sensor instance that can be
   *          used to build the DTO object graph for all switch state to be sent to the client
   */
  private static DTOReference resolveSensor(Switch sw)
  {
    SwitchSensorRef ref = sw.getSwitchSensorRef();

    DTOReference sensor;

    // defensive check -- switch may not always have a sensor associated to it...

    if (ref == null)
    {
      // TODO : should return null reference directly, see MODELER-313
      
      sensor = new DTOReference();
    }

    else
    {
      Sensor switchSensor = ref.getSensor();

      sensor = new DTOReference(switchSensor.getOid());
    }

    return sensor;
  }


  /**
   * Returns a data transfer object reference for a 'off' command for the given switch. This is
   * mainly used in preparation for a data transfer object graph that disconnects the persistent
   * switch entity from the client side processing.
   *
   *
   * @param   sw    a persistent switch entity
   *
   * @return  A data transfer object reference for a persistent command instance that can be
   *          used to build the DTO object graph for all switch state to be sent to the client. <p>
   *
   *          Note that in case of an error in resolving the dependent 'off' command reference,
   *          an empty DTO reference will be returned.
   */
  private static DTOReference resolveOffCommand(Switch sw)
  {
    DTOReference ref = resolveSwitchCommandReference(sw.getSwitchCommandOffRef());

    // This is a defensive check -- switches *should* always have an off command, so if we don't
    // find one, it is most likely a database integrity/constraint issue.

    if (ref == null)
    {
      persistenceLog.error(
          "Switch ID {0} -- ''{1}'' in device ''{2}'' does not have an associated 'off' command. " +
          "(Account ID : {3}, Users : {4})",
          sw.getOid(), sw.getDisplayName(), sw.getDevice().getDisplayName(),
          sw.getAccount(), sw.getAccount().getUsers()
      );

      return new DTOReference();
    }

    return ref;
  }

  /**
   * Returns a data transfer object reference for an 'on' command for the given switch. This is
   * mainly used in preparation for a data transfer object graph that disconnects the persistent
   * switch entity from the client side processing.
   *
   *
   * @param   sw    a persistent switch entity
   *
   * @return  A data transfer object reference for a persistent command instance that can be
   *          used to build the DTO object graph for all switch state to be sent to the client. <p>
   *
   *          Note that in case of an error in resolving the dependent 'on' command reference,
   *          an empty DTO reference will be returned.
   */
  private static DTOReference resolveOnCommand(Switch sw)
  {
    DTOReference ref = resolveSwitchCommandReference(sw.getSwitchCommandOnRef());

    // Above may return 'null' in case of errors -- log the error and return an empty DTO reference
    // instead.

    if (ref == null)
    {
      persistenceLog.error(
          "BUG: Switch ID {0} -- ''{1}'' in device ''{2}'' does not have an associated 'on' " +
          "command. (Account ID : {3}, Users : {4})",
          sw.getOid(), sw.getDisplayName(), sw.getDevice().getDisplayName(),
          sw.getAccount(), sw.getAccount().getUsers()
      );

      return new DTOReference();
    }

    return ref;
  }


  /**
   * Defensively resolves associated command references for a switch. The implementation
   * assumes most worse case scenarios with regards to data integrity issues and lack of
   * database constraints (hence, defensive)
   *
   * @param ref   the switch command reference to resolve into a data transfer object reference
   *
   * @return  Returns a data transfer object reference to a switch's 'on' or 'off' command that
   *          can be used to build a DTO object graph to disconnect the persistent switch entity
   *          from client side processing.  <p>
   *
   *          Note that may return a null reference in case of database integrity or constraint
   *          errors.
   */
  private static DTOReference resolveSwitchCommandReference(CommandRefItem ref)
  {
    // defensive check -- this may indicate a database integrity issue, both 'on'
    // and 'off' commands should always be associated with a switch

    if (ref == null)
    {
      persistenceLog.error(
          "Switch command could not be resolved -- command reference not found."
      );

      return null;
    }

    DeviceCommand cmd = ref.getDeviceCommand();

    // defensive check -- this would be a DB constraint issue, there's a reference to
    // command but the command itself was not found...

    if (cmd == null)
    {
      persistenceLog.error(
          "Command for switch reference could not be resolved, command not found. " +
          "(Device : ''{0}'')", ref.getDisplayName()
      );

      return null;
    }

    return new DTOReference(cmd.getOid());
  }

}
