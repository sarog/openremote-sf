/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2012, OpenRemote Inc.
 *
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.openremote.modeler.service.impl;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.hibernate.ObjectNotFoundException;
import org.openremote.modeler.beehive.Beehive30API;
import org.openremote.modeler.beehive.BeehiveService;
import org.openremote.modeler.cache.CacheOperationException;
import org.openremote.modeler.cache.LocalFileCache;
import org.openremote.modeler.client.Configuration;
import org.openremote.modeler.client.utils.PanelsAndMaxOid;
import org.openremote.modeler.configuration.PathConfig;
import org.openremote.modeler.domain.Absolute;
import org.openremote.modeler.domain.Account;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.DeviceCommandRef;
import org.openremote.modeler.domain.DeviceMacro;
import org.openremote.modeler.domain.DeviceMacroRef;
import org.openremote.modeler.domain.GroupRef;
import org.openremote.modeler.domain.Panel;
import org.openremote.modeler.domain.Panel.UIComponentOperation;
import org.openremote.modeler.domain.ScreenPairRef;
import org.openremote.modeler.domain.Sensor;
import org.openremote.modeler.domain.Slider;
import org.openremote.modeler.domain.Switch;
import org.openremote.modeler.domain.UICommand;
import org.openremote.modeler.domain.User;
import org.openremote.modeler.domain.component.ColorPicker;
import org.openremote.modeler.domain.component.Gesture;
import org.openremote.modeler.domain.component.SensorLinkOwner;
import org.openremote.modeler.domain.component.SensorOwner;
import org.openremote.modeler.domain.component.UIButton;
import org.openremote.modeler.domain.component.UIComponent;
import org.openremote.modeler.domain.component.UISlider;
import org.openremote.modeler.domain.component.UISwitch;
import org.openremote.modeler.exception.ConfigurationException;
import org.openremote.modeler.exception.NetworkException;
import org.openremote.modeler.exception.UIRestoreException;
import org.openremote.modeler.logging.AdministratorAlert;
import org.openremote.modeler.logging.LogFacade;
import org.openremote.modeler.server.SensorController;
import org.openremote.modeler.server.SliderController;
import org.openremote.modeler.server.SwitchController;
import org.openremote.modeler.service.DeviceCommandService;
import org.openremote.modeler.service.DeviceMacroService;
import org.openremote.modeler.service.SensorService;
import org.openremote.modeler.service.SliderService;
import org.openremote.modeler.service.SwitchService;
import org.openremote.modeler.shared.dto.DeviceCommandDTO;
import org.openremote.modeler.shared.dto.MacroDTO;
import org.openremote.modeler.shared.dto.UICommandDTO;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;


/**
 * TODO
 *
 *   This is a temporary implementation. It only exists to support the
 *   existing legacy Java serialization for designer UI state.
 *
 *   Fundamentally the original mechanism for state persistence is
 *   too brittle and unsuitable, and should be replaced. Once that
 *   is complete, this implementation can be mostly or completely
 *   removed.
 *
 *   The issues with current serialization-based state persistence
 *   are many:
 *
 *     - In its current state it is too brittle, using a binary
 *       format that is difficult to debug and correct when issues
 *       arise. A temporary remedy to this is to serialize the
 *       designer state in an alternative XML format which allows
 *       easier manipulation when corrections must be made.
 *
 *     - There's no recovery -- it is purely based on file I/O
 *       with no recovery options. File I/O *does* fail on occasions
 *       (for example in case of out-of-memory errors or forced
 *       shut-downs) so recovery is a necessity.
 *
 *     - It is based on regular file I/O -- it is somewhat redundant
 *       to implement recovery options for file I/O when a database
 *       is available which has lot of the ground-work for reliable
 *       storage on disk. Instead of using regular filesystem, the
 *       data could be moved to a DB with reliable disk management,
 *       reducing the need for recovery handling on regular file I/O
 *       which will always be inferior to the more dedicated disk
 *       management by a DB. This also enables hosting on cloud
 *       services that do not allow direct file system access.
 *
 *     - Current implementation is not restricted to merely persisting
 *       designer UI state. Large parts of the current serialization
 *       graph include data that is redundant and stored more reliably
 *       in the controller.xml and panel.xml documents (but these are
 *       not being used). Some of the data would clearly belong to the
 *       schemas for controller.xml and panel.xml but is not supported
 *       yet. Only a very minority of the data being persisted is
 *       actual UI state data. This makes the persistence a much more
 *       critical part of the implementation (with more urgent need
 *       for features such as recovery) than it would be otherwise.
 *       These very poor implementation choices must be corrected.
 *
 *     - State that is defined within controller.xml and panel.xml
 *       documents should be directly parsed and restored from those
 *       files. The implementation for this will be similar to that
 *       of an account import feature. The same object-to-xml mapping
 *       implementations should be reusable in designer, beehive and
 *       controller applications.
 *
 *   Ultimately a web-front end UI state persistence should be supported
 *   via Beehive REST API. This removes the need for localized
 *   UI state persistence, and enables UI state storage for all client
 *   applications, not merely this designer implementation.
 *
 *   Once the above is complete, this implementation can be removed.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
class DesignerState
{

  // TODO :
  //
  //    - replace throwing runtime exceptions to report issues back to user in UI with a
  //      more robust messaging implementation


  // Class Members --------------------------------------------------------------------------------


  /**
   * Logging for save operation.
   */
  private final static LogFacade saveLog =
      LogFacade.getInstance(LogFacade.Category.STATE_SAVE);

  /**
   * Logging for restore operation.
   */
  private final static LogFacade restoreLog =
      LogFacade.getInstance(LogFacade.Category.STATE_RECOVERY);

  /**
   * Admin alerts for critical errors.
   */
  private final static AdministratorAlert admin =
      AdministratorAlert.getInstance(AdministratorAlert.Type.DESIGNER_STATE);

  /**
   * Class-wide safety valve on account data save. If any errors on restore are detected, halt
   * data saves based on the concern of potential data corruption. <p>
   *
   * This set contains account IDs that have been flagged and should not attempt to save data
   * from the current in-memory domain model.  <p>
   *
   * Multiple thread-access is synchronized via copy-on-write implementation. Assumption is that
   * writes are rare (only occur in case of critical errors) and mostly access is read-only to
   * check existence of account IDs.
   */
  private final static Set<Long> haltAccountSave = new CopyOnWriteArraySet<Long>();

  // Services required to access DB building elements during configuration restore

  private DeviceCommandService deviceCommandService;
  private DeviceMacroService deviceMacroService;

  private SensorService sensorService;
  private SliderService sliderService;
  private SwitchService switchService;

  /**
   * TODO : should come through resource cache interface
   *
   * Opens a deserialization stream to the legacy Designer UI state object. Attempts
   * to read the serialization header and initialize the stream ready to deserialize
   * panels from the file.
   *
   * @param legacyPanelsObjFile   file path to the legacy binary panels.obj serialization
   *                              file
   *
   * @return  object input stream positioned to read the first object from the
   *          legacy binary file
   *
   * @throws RestoreFailureException
   *            if opening a stream to the serialization file or verifying the header
   *            fails for any reason
   */
  private static ObjectInputStream openSerializationStream(File legacyPanelsObjFile)
      throws RestoreFailureException
  {
    BufferedInputStream bis;

    try
    {
      bis = new BufferedInputStream(new FileInputStream(legacyPanelsObjFile));
    }

    catch (FileNotFoundException e)
    {
      // Something strange happened... throw restore failure exception which should
      // allow for further recovery attempts.

      throw new RestoreFailureException(
          "Previously checked " + legacyPanelsObjFile.getAbsoluteFile() +
          " can no longer be found, or was not a proper file : " + e.getMessage(), e
      );
    }

    catch (SecurityException e)
    {
      // It's really an configuration error but throw restore failure exception instead...
      // it's unlikely to work but lets us proceed with attempting recovery options.

      throw new RestoreFailureException(
          "Security manager has denied read access to " + legacyPanelsObjFile.getAbsoluteFile() +
          ". Read/write access to designer temporary directory is required.", e
      );
    }


    try
    {
      return new ObjectInputStream(bis);
    }

    catch (SecurityException e)
    {
      //  Could potentially occur if untrusted subclass illegally overrides
      //  security-sensitive methods.
      //
      //  This is really an configuration error but throw restore failure exception instead...
      //  this will allow recovery options to proceed.

      throw new RestoreFailureException(
          "Error in creating object input stream : " + e.getMessage(), e
      );
    }

    catch (StreamCorruptedException e)
    {
      // we've got corrupt .ser file, can't even read the header...

      throw new RestoreFailureException(
          "Serialization header is corrupted : " + e.getMessage(), e
      );
    }

    catch (IOException e)
    {
      // Corrupt file, can't read the header...

      throw new RestoreFailureException(
          "I/O Error when attempting to read serialization header : " + e.getMessage(), e
      );
    }
  }

  protected static String uglyImageSourcePathHack(User user, String str)
  {
    // TODO :
    //
    //   Certain image sources still include path in their names (although they should
    //   not anymore) based on the runtime error logs. Unclear whether this is caused
    //   by pre-existing serialization data from older versions that included paths or
    //   if the API to set image source names still includes path somewhere.
    //
    //   This ugly hack is a stop-gap measure for the runtime errors caused by the paths
    //   in image source names and based on the file pattern revealed by runtime logs
    //   (/[cache folder]/[account id]/[image name])
    //
    //   It would be better placed into the domain model to strip incorrect path names
    //   at the source (e.g. in Panel.getallImageNames() method) but before moving there
    //   should add sufficient unit test coverage to ensure there's no regression on
    //   this issue.
    //                                                                            [JPL]


    if (str.startsWith(PathConfig.RESOURCEFOLDER + "/" + user.getAccount().getOid() + "/"))
    {
      saveLog.warn("Found ''{0}'' -- there should be no path included...", str);

      str = str.substring(str.lastIndexOf("/") + 1, str.length());

      saveLog.warn("Truncated image name to ''{0}''.", str);
    }

    return str;
  }




  // Private Instance Fields ----------------------------------------------------------------------

  private Long maxOID = 0L;
  private Collection<Panel> panels = new ArrayList<Panel>();

  /**
   * The current user executing the operations.
   */
  private User user;

  /**
   * Designer configuration.
   */
  private Configuration configuration;

  /**
   * Local cache.
   */
  private LocalFileCache cache;
  
  // TODO EBR : should really be ResourceCache<File> but using some methods that are not public on the interface at this stage of refactoring

  // Constructors ---------------------------------------------------------------------------------


  /**
   * Constructs a new designer state management instance for the given user.
   *
   * @param config    Designer configuration
   * @param user      current user associated with the incoming HTTP request thread
   * @param cache	  LocalFileCache associated with the user
   */
  protected DesignerState(Configuration config, User user, LocalFileCache cache)
  {
    this.configuration = config;
    this.user = user;
    this.cache = cache;
  }



  // Object Overrides -----------------------------------------------------------------------------

  @Override public String toString()
  {
    return "Panels : " + panels.size() + ", max OID : " + maxOID;
  }



  // Protected Instance Methods -------------------------------------------------------------------


  /**
   * Restores designer UI state to match the data found from the local cache or Beehive (if update is requested). <p>
   *
   * Note the various problems with this current implementation described in this class'
   * documentation.  <p>
   *
   * @param updateLocalCache indicates local cache should be updated from Beehive before restore
   *
   * @throws NetworkException
   *            If any errors occur with the network connection when updating the cache
   *            from Beehive server -- the basic assumption here is that network exceptions
   *            are recoverable (within a certain time period) and the method call can
   *            optionally be re-attempted at later time. Do note that the exception class
   *            provides a {@link NetworkException.Severity severity level} which can be used
   *            to indicate the likelyhood that the network error can be recovered from.
   */
  protected void restore(boolean updateLocalCache) throws NetworkException
  {

    // collect some performance stats...

    PerformanceLog perf = new PerformanceLog(PerformanceLog.Category.RESTORE_PERFORMANCE);
    perf.startTimer();


    try
    {
      // keep thread-local user and account information in log4j context...

      addContextLog();

      restoreLog.info(
          "Attempting to restore designer panel UI state for user {0} (Acct ID : {1})",
          user.getUsername(), user.getAccount().getOid()
      );


      // synchronize user data from Beehive server...
      if (updateLocalCache) {
    	  cache.update();
      }

      boolean hasLegacyDesignerUIState = cache.hasLegacyDesignerUIState();
      boolean hasCachedState = cache.hasState();
      boolean hasXMLUIState = cache.hasXMLUIState();


      // If we can't find any serialization binary (panels.obj), XML serialization file and
      // no backups, we must assume it was a new account (hopefully, otherwise there are more
      // serious issues if all these files have been wiped out).

      if (!hasLegacyDesignerUIState && !hasXMLUIState && !hasCachedState)
      {
        restoreLog.info(
            "There was no serialized panels.obj file, no serialized XML stream and no local " +
            "cache backups found in local user cache {0}. No user state was found in Beehive. " +
            "Assuming a new user with no saved design.", printUserAccountLog(user)
        );

        LogFacade.getInstance(LogFacade.Category.USER).info(
            "NEW USER: {0}", printUserAccountLog(user)
        );

        return;
      }

      // Restore XML state first, as this is now the format used for saving
      if (hasXMLUIState)
      {
        
        restoreXMLUIState(cache.getXMLUIFile());
                
        restoreLog.info("Restored UI state from XML : {0}", this);

        // TODO, should we delete legacy file if it exists ?
        // Otherwise we'll just have outdated legacy files laying around

        try {
          // The UI state does store full definition (e.g. including names) of the modeler elements it references         
          // The current UI does not properly update those linked DTOs, it only updates properly the database.
          // See MODELER-322
          // Calling this here ensures the DTOs are recreated from the DB objects when loading a configuration.
          refreshLinkedDTOReferences(panels);
        } catch (Throwable t) {
          // This exception type and message will propagate to the user...

          throw new UIRestoreException(
              "Restoring your account data has failed. Please contact an administrator for " +
              "assistance. Avoid making further changes to your account and design to prevent " +
              "any potential data corruption issues: " + t.getMessage(), t
          );
        }

        return;
      }

      
      if (hasLegacyDesignerUIState)
      {
        try
        {
          restoreLegacyDesignerUIState(cache.getLegacyPanelObjFile());

          restoreLog.info("Restored UI state : {0}", this);
          
          // TODO :
          //    this should be pushed deeper into the call stack, either into the designer state
          //    implementation (which can implement a translation to DTOs, similar to the current
          //    hack of transformToPanelsAndMaxOid() as seen above) or better yet into the domain
          //    package as was done in the case of Panel.getAllImageNames() method implementation --
          //    same comment applies, the processing is part of the domain object which should also
          //    help reduce the very brittle instanceof semantics seen in the populate and resolve
          //    DTO references here.
          //
          //    Also the error handling needs to be pushed to new DesignerState implementation
          //    so that errors in the implementation below are correctly handled and potentially
          //    preventing user data corruption.
          //                                                                            [JPL]
          //
          //    UPDATE 2012-09-13: Have not accomplished the task above yet (pushing call down
          //    to DesignerState implementation which would have more robust error handling
          //    *and* better error reporting due to user and account references that are carried
          //    in it. Duplicating some error handling here until have time to reorganize the
          //    code better, at which point the duplicate error handling can probably be removed. [JPL]
          //
          //    UPDATE 2013-02-11: Already pushed down to this level. Don't seem to change much with
          //    regards to how errors are reported.

          try
          {
            populateDTOReferences(this.panels);
          }

          catch (Throwable t)
          {
            // This exception type and message will propagate to the user...

            throw new UIRestoreException(
                "Restoring your account data has failed. Please contact an administrator for " +
                "assistance. Avoid making further changes to your account and design to prevent " +
                "any potential data corruption issues: " + t.getMessage(), t
            );
          }

          return;
        }

        catch (RestoreFailureException e)
        {
          admin.alert(
              "There was a state restoration error from panels.obj file in account {0} ({1}) : {2}",
              e, user.getAccount().getOid(), printUserAccountLog(user), e.getMessage()
          );
        }
      }

      // TODO :
      //
      //  If no designer serialization state is found, we assume that the account is ok,
      //  just that nothing has been saved in the designer yet.
      //
      //  This assumption is brittle, it would be better to save even an empty designer
      //  serialization data (for example, in the XML format once it is added) leaving it
      //  clear that complete lack of any serialization information indicates a system error.

      return;
    }

    catch (ConfigurationException e)
    {
      //   Critical error caused by server configuration issues

      haltAccount(
          MessageFormat.format(
              "CRITICAL CONFIGURATION ERROR ({0}) : {1}", printUserAccountLog(user), e.getMessage()
          ),

          MessageFormat.format(
              "There's an issue with the server configuration that has prevented restoring your " +
              "account data from Beehive. The administrator has been notified of this issue. As a " +
              "safety precaution, all changes to your account data has been disabled until the " +
              "issue has been resolved. Do not make changes to your designs or configuration " +
              "during this time, as the changes may get lost. For further assistance, contact support." +
              "(Error Message : {0})", e.getMessage()
          ), e
      );
    }

    catch (CacheOperationException e)
    {
      // Critical error caused by unanticipated runtime I/O errors

      haltAccount(
          MessageFormat.format(
              "RUNTIME I/O ERROR : {0} ({1}).", e.getMessage(), printUserAccountLog(user)
          ),

          MessageFormat.format(
              "There has been an I/O error in reading or saving a cached copy of your " +
              "account data stored in Beehive. The system administrators have been notified of " +
              "this error. To prevent any potential damage, further modifications of your data " +
              "has been disabled until the admins have cleared the issue. Do not make changes to " +
              "your designs or configuration during this period, as these changes may get lost. " +
              "For further assistance, contact support. (Error Message : {0})", e.getMessage()
          ), e
      );
    }

    catch (Throwable t)
    {
      // Catch-all for implementation errors

      haltAccount(
          MessageFormat.format(
              "IMPLEMENTATION ERROR : {0} ({1}).", t.getMessage(), printUserAccountLog(user)
          ),

          MessageFormat.format(
              "There was an implementation error in Designer while restoring your account data. " +
              "The system administrators have been notified of this issue. To prevent potential " +
              "damage to your data, further modifications have been disabled until the admins " +
              "have cleared the issue. Do not make changes to your designs or configuration " +
              "during this period, as these changes may get lost. For further assistance, " +
              "please contact support. (Error Message : {0})", t.getMessage()
          ), t
      );
    }

    finally
    {
      perf.stopTimer("Restore time " + printUserAccountLog(user) + " : {0} seconds.");

      removeContextLog();
    }
  }

  /**
   * Prepares the objects to be sent to client side by replacing references to entity beans with references to DTOs.
   * The inverse operation is performed by the resolveDTOReferences method before using the objects on the server side.
   *
   * @param panels
   */
  private void populateDTOReferences(Collection<Panel> panels) {
    Panel.walkAllUIComponents(panels, new UIComponentOperation() {

      @Override
      public void execute(UIComponent component) {
        if (component instanceof SensorOwner) {
          SensorOwner owner = (SensorOwner) component;
          if (owner.getSensorDTO() == null && owner.getSensor() != null) {
            Sensor sensor = sensorService.loadById(owner.getSensor().getOid());

            if (sensor != null)
            {
              owner.setSensorDTO(sensor.getSensorWithInfoDTO());
            }

            owner.setSensor(null);

            if (owner instanceof SensorLinkOwner) {
              ((SensorLinkOwner) owner).getSensorLink().setSensor(null);
              ((SensorLinkOwner) owner).getSensorLink().setSensorDTO(owner.getSensorDTO());
            }
          }
        }
        if (component instanceof UISlider) {
          UISlider uiSlider = (UISlider)component;
          if (uiSlider.getSliderDTO() == null && uiSlider.getSlider() != null) {
            // We must load slider because referenced sensor / command are not serialized, this reloads from DB
            Slider slider = sliderService.loadById(uiSlider.getSlider().getOid());
            if (slider != null) { // Just in case we have a dangling pointer
              uiSlider.setSliderDTO(slider.getSliderWithInfoDTO());
            }
            uiSlider.setSlider(null);
          }
        }
        if (component instanceof UISwitch) {
          UISwitch uiSwitch = (UISwitch)component;
          if (uiSwitch.getSwitchDTO() == null && uiSwitch.getSwitchCommand() != null) {
            Switch switchBean = switchService.loadById(uiSwitch.getSwitchCommand().getOid());
            if (switchBean != null) { // Just in case we have a dangling pointer
              uiSwitch.setSwitchDTO(switchBean.getSwitchWithInfoDTO());
            }
            uiSwitch.setSwitchCommand(null);
          }
        }
        if (component instanceof UIButton) {
          UIButton uiButton = (UIButton)component;
          if (uiButton.getUiCommandDTO() == null && uiButton.getUiCommand() != null) {
            uiButton.setUiCommandDTO(createUiCommandDTO(uiButton.getUiCommand()));
            uiButton.setUiCommand(null);
          }
        }
        if (component instanceof ColorPicker) {
          ColorPicker colorPicker = (ColorPicker)component;
          if (colorPicker.getUiCommandDTO() == null && colorPicker.getUiCommand() != null) {
            colorPicker.setUiCommandDTO(createUiCommandDTO(colorPicker.getUiCommand()));
            colorPicker.setUiCommand(null);
          }
        }
        if (component instanceof Gesture) {
          Gesture gesture = (Gesture)component;
          if (gesture.getUiCommandDTO() == null && gesture.getUiCommand() != null) {
            gesture.setUiCommandDTO(createUiCommandDTO(gesture.getUiCommand()));
            gesture.setUiCommand(null);
          }
        }
      }

      private UICommandDTO createUiCommandDTO(UICommand uiCommand)
      {
        if (uiCommand instanceof DeviceCommandRef)
        {
          try
          {
            DeviceCommand dc = deviceCommandService.loadById(((DeviceCommandRef)uiCommand).getDeviceCommand().getOid());
            return (dc != null)?new DeviceCommandDTO(dc.getOid(), dc.getDisplayName(), dc.getFullyQualifiedName(), dc.getProtocol().getType()):null;
          }
          catch (ObjectNotFoundException e)
          {
            restoreLog.warn("Button is referencing inexistent command with id " + ((DeviceCommandRef)uiCommand).getDeviceCommand().getOid(), e);
            return null;
          }
        }

        else if (uiCommand instanceof DeviceMacroRef)
        {
          try
          {
            DeviceMacro targetMacro = ((DeviceMacroRef)uiCommand).getTargetDeviceMacro();

            if (targetMacro != null)
            {
              long oid = targetMacro.getOid();

              DeviceMacro dm = deviceMacroService.loadById(oid);

              return (dm != null) ? new MacroDTO(dm.getOid(), dm.getDisplayName()) : null;
            }

            else
            {
              restoreLog.error("DeviceMacroRef had a null target device macro reference");

              return null;
            }
          }

          catch (ObjectNotFoundException e)
          {
            restoreLog.warn("Button is referencing inexistent macro with id " + ((DeviceMacroRef)uiCommand).getTargetDeviceMacro().getOid(), e);
            return null;
          }
        }

        throw new RuntimeException("We don't expect any other type of UICommand"); // TODO : review that exception type
      }

    });
  }
  
  /**
   * Walks through all the UI elements of the given panels and refresh the DTOs of the building modeler objects linked to them.
   * The refresh is done by looking up, by id, the corresponding entity objects in the database,
   * recreating the DTOs and using those new DTOs in the UI elements.
   *
   * @param panels
   */
  private void refreshLinkedDTOReferences(Collection<Panel> panels) {
    Panel.walkAllUIComponents(panels, new UIComponentOperation() {

      @Override
      public void execute(UIComponent component) {
        if (component instanceof SensorOwner) {
          SensorOwner owner = (SensorOwner) component;
          if (owner.getSensorDTO() != null && owner.getSensorDTO().getOid() != null) {
            Sensor sensor = sensorService.loadById(owner.getSensorDTO().getOid());

            if (sensor != null) {
              owner.setSensorDTO(sensor.getSensorWithInfoDTO());
            }

            if (owner instanceof SensorLinkOwner) {
              ((SensorLinkOwner) owner).getSensorLink().setSensorDTO(owner.getSensorDTO());
            }
          }
        }
        if (component instanceof UISlider) {
          UISlider uiSlider = (UISlider)component;
          if (uiSlider.getSliderDTO() != null && uiSlider.getSliderDTO().getOid() != null) {
            // We must load slider because referenced sensor / command are not serialized, this reloads from DB
            Slider slider = sliderService.loadById(uiSlider.getSliderDTO().getOid());
            if (slider != null) { // Just in case we have a dangling pointer
              uiSlider.setSliderDTO(slider.getSliderWithInfoDTO());
            }
          }
        }
        if (component instanceof UISwitch) {
          UISwitch uiSwitch = (UISwitch)component;
          if (uiSwitch.getSwitchDTO() != null && uiSwitch.getSwitchDTO().getOid() != null) {
            Switch switchBean = switchService.loadById(uiSwitch.getSwitchDTO().getOid());
            if (switchBean != null) { // Just in case we have a dangling pointer
              uiSwitch.setSwitchDTO(switchBean.getSwitchWithInfoDTO());
            }
          }
        }
        if (component instanceof UIButton) {
          UIButton uiButton = (UIButton)component;
          if (uiButton.getUiCommandDTO() != null && uiButton.getUiCommandDTO().getOid() != null) {
            uiButton.setUiCommandDTO(createUiCommandDTO(uiButton.getUiCommandDTO()));
          }
        }
        if (component instanceof ColorPicker) {
          ColorPicker colorPicker = (ColorPicker)component;
          if (colorPicker.getUiCommandDTO() != null && colorPicker.getUiCommandDTO().getOid() != null) {
            colorPicker.setUiCommandDTO(createUiCommandDTO(colorPicker.getUiCommandDTO()));
          }
        }
        if (component instanceof Gesture) {
          Gesture gesture = (Gesture)component;
          if (gesture.getUiCommandDTO() != null && gesture.getUiCommandDTO().getOid() != null) {
            gesture.setUiCommandDTO(createUiCommandDTO(gesture.getUiCommandDTO()));
          }
        }
      }

      private UICommandDTO createUiCommandDTO(UICommandDTO uiCommandDTO)
      {
        if (uiCommandDTO instanceof DeviceCommandDTO)
        {
          try
          {
            DeviceCommand dc = deviceCommandService.loadById(uiCommandDTO.getOid());
            return (dc != null)?new DeviceCommandDTO(dc.getOid(), dc.getDisplayName(), dc.getFullyQualifiedName(), dc.getProtocol().getType()):null;
          }
          catch (ObjectNotFoundException e)
          {
            restoreLog.warn("Button is referencing inexistent command with id " + uiCommandDTO.getOid(), e);
            return null;
          }
        }

        else if (uiCommandDTO instanceof MacroDTO)
        {
          try
          {
            DeviceMacro dm = deviceMacroService.loadById(uiCommandDTO.getOid());

            return (dm != null) ? new MacroDTO(dm.getOid(), dm.getDisplayName()) : null;

          }

          catch (ObjectNotFoundException e)
          {
            restoreLog.warn("Button is referencing inexistent macro with id " + uiCommandDTO.getOid(), e);
            return null;
          }
        }

        throw new RuntimeException("We don't expect any other type of UICommandDTO"); // TODO : review that exception type
      }

    });
  }
  
  /**
   * Saves the account artifacts from the current in-memory domain model to Beehive server.
   *
   * @param panels    set of panels in the domain model -- the panels aggregate all other
   *                  objects and artifacts to be saved
   *
   * @throws UIRestoreException   TODO : should use appropriate checked exception type or return value
   */
  protected void save(Set<Panel> panels, long maxOid)
  {

    PerformanceLog perf = new PerformanceLog(PerformanceLog.Category.SAVE_PERFORMANCE);

    try
    {

      // add thread-local logging contexts...

      addContextLog();

      // Pushes in-memory model to our local cache, as this is used as the source to save to beehive
      cache.replace(panels, maxOid);

      Account acct = user.getAccount();

      // This is a safety lock -- if there has been previous errors then the save operation
      // may have been disabled. Prevents autosave from corrupting data...

      if (haltAccountSave.contains(acct.getOid()))
      {
        saveLog.error("Did not save to Beehive due to earlier restore failure");

        // TODO : could save a recovery copy

        return;
      }


      // collect some performance stats...

      perf.startTimer();

      // Collect all image file names (without path references to this account's
      // local cache) included in panel definitions and components in panels...

      Set<File> imageFiles = new HashSet<File>();

      if (panels == null)
      {
        saveLog.warn(
            "getAllImageNames(panels) was called with null argument (Account : {0})",
            acct.getOid()
       );
      }

      else
      {
        for (Panel panel : panels)
        {
          Set<String> imageNames = Panel.getAllImageNames(panel);

          for (String imageName : imageNames)
          {
            imageName = uglyImageSourcePathHack(user, imageName);

            imageFiles.add(new File(imageName));

            saveLog.debug(
                "Added image resource ''{0}'' from panel ''{1}''", imageName, panel.getDisplayName()
            );
          }
        }
      }


      // Get Beehive Client API...

      BeehiveService beehive = new Beehive30API(configuration);


      // Fetch the required resource files from the user's cache...
       
      try
      {
        // Will only save images that are included in the current in-memory domain model...

        cache.markInUseImages(imageFiles);

        // Upload ZIP to Beehive server...

        beehive.uploadResources(cache.openReadStream(), user);

        saveLog.info("Saved resources for {0}", printUserAccountLog(user));
      }

      catch (NetworkException e)
      {
        // TODO : log and throw because unclear if runtime exceptions thrown to client will log...

        saveLog.error("Save failed due to network error : {0}", e, e.getMessage());

        throw new UIRestoreException(
            "Save failed due to network error to Beehive server. You may try again later. " +
            "If the issue persists, contact support (Error : " + e.getMessage() + ").", e
        );
      }

      catch (CacheOperationException e)
      {
        admin.alert(
            "Can't save account data due to cache error. Account ID : {0}, User : {1}. " +
            "Error: {2}", e, user.getAccount().getOid(), printUserAccountLog(user), e.getMessage()
        );

        throw new UIRestoreException(
            "Saving your design failed due to a cache error. Administrators have been notified " +
            "of this issue."
        );
      }

      catch (ConfigurationException e)
      {
        admin.alert(
            "Save failed for account {0} due to configuration error : {1}",
            e, user.getAccount().getOid(), e.getMessage()
        );

        throw new UIRestoreException(
            "Unable to save your data due to Designer configuration error. " +
            "Administrators have been notified of this issue. For further assistance, " +
            "please contact support. (Error : " + e.getMessage() + ")", e
        );
      }

      catch (Throwable t)
      {
        // Catch-all for implementation errors...

        admin.alert("IMPLEMENTATION ERROR : {0}", t, t.getMessage());

        throw new UIRestoreException(
            "Save failed due to Designer implementation error. Administrators have been notified " +
            "of this issue. For further assistance, please contact support. Error : " +
            t.getMessage(), t
        );
      }
    }

    finally
    {
      perf.stopTimer("Save time " + printUserAccountLog(user) + " : {0} seconds.");

      removeContextLog();
    }
  }


  /**
   * TODO :
   *
   *   - translates restored state to PanelsAndMaxOid instance that is currently required by
   *     the interface API design. Should be modified later (also to include more robust
   *     mechanism of passing error messages back to UI other than runtime exceptions).
   *
   */
  protected PanelsAndMaxOid transformToPanelsAndMaxOid()
  {
    return new PanelsAndMaxOid(panels, maxOID);
  }



  // Private Instance Methods ---------------------------------------------------------------------


  /**
   * Attempts to deserialize a legacy binary panels.obj designer UI state serialization file.
   *
   * @param legacyPanelsObjFile   file path to legacy panels.obj serialization file
   *
   * @throws RestoreFailureException
   *            if deserialization fails for any reason
   */
  private void restoreLegacyDesignerUIState(File legacyPanelsObjFile) throws RestoreFailureException
  {
    ObjectInputStream ois = null;

    try
    {
      ois = openSerializationStream(legacyPanelsObjFile);

      this.panels = deserializePanels(ois);

      this.maxOID = deserializeMaxOID(ois);
    }

    finally
    {
      try
      {
        if (ois != null)
        {
          ois.close();
        }
      }

      catch (IOException e)
      {
        restoreLog.warn(
            "Failed to close input stream to " + legacyPanelsObjFile.getAbsolutePath() +
            " : " + e.getMessage(), e
        );
      }
    }
  }
  
  private void restoreXMLUIState(File xmlUIFile) throws RestoreFailureException
  {
    XStream xstream = new XStream(new StaxDriver());
    xstream.alias("panel", Panel.class);
    xstream.alias("group", GroupRef.class);
    xstream.alias("screenPair", ScreenPairRef.class);
    xstream.alias("absolute", Absolute.class);
    InputStreamReader isr = null;
    try {
      // Going through a StreamReader to enforce UTF-8 encoding
      isr = new InputStreamReader(new FileInputStream(xmlUIFile), "UTF-8");
      PanelsAndMaxOid panelsAndMaxOid = (PanelsAndMaxOid)xstream.fromXML(isr);
      panels = panelsAndMaxOid.getPanels();
      maxOID = panelsAndMaxOid.getMaxOid();
    } catch (UnsupportedEncodingException e) {
      throw new RestoreFailureException("Issue reading file " + xmlUIFile.getAbsoluteFile() + " in UTF-8 : " + e.getMessage(), e);
    } catch (FileNotFoundException e) {
      throw new RestoreFailureException("Previously checked " + xmlUIFile.getAbsoluteFile() +
          " can no longer be found, or was not a proper file : " + e.getMessage(), e
      );
    } finally {
      if (isr != null) {
        try {
          isr.close();
        } catch (IOException e) {
          restoreLog.warn("Failed to close reader to " + xmlUIFile.getAbsolutePath() + " : " + e.getMessage(), e);
        }
      }
    }
  }


  /**
   * Deserializes a Java collection of panel instances from the legacy designer
   * serialization file.
   *
   * @param ois     object input stream for the file to deserialize -- assumes
   *                the stream is positioned for reading the collection object
   *
   * @return        deserialized instance of a collection with panel objects
   *
   * @throws RestoreFailureException
   *                if deserializing the collection fails for any reason
   */
  @SuppressWarnings("unchecked")
  private Collection<Panel> deserializePanels(ObjectInputStream ois) throws RestoreFailureException
  {
    try
    {
      return (Collection<Panel>)ois.readObject();
    }

    catch (ClassNotFoundException e)
    {
      // This is really a deployment error, but could be recoverable via XML...

      throw new RestoreFailureException(
          "Deployment Error -- Cannot restore panel collection, class not found : "
          + e.getMessage(), e
      );
    }

    catch (InvalidClassException e)
    {
      // Thrown when the Serialization runtime detects one of the following problems with a Class.
      //
      //   - The serial version of the class does not match that of the class descriptor read from
      //     the stream
      //   - The class contains unknown datatypes
      //   - The class does not have an accessible no-arg constructor
      //

      throw new RestoreFailureException(
          "Deployment Error -- Cannot restore panel collection, invalid class : "
          + e.classname, e
      );
    }

    catch (StreamCorruptedException e)
    {
      // the .ser file is basically broken...

      throw new RestoreFailureException(
          "Corrupt panel collection serialization stream : " + e.getMessage(), e
      );
    }

    catch (OptionalDataException e)
    {
      throw new RestoreFailureException(
          "Optional Data Exception : " + e.getMessage(), e
      );
    }

    catch (EOFException e)
    {
      // unexpected EOF -- probably didn't get saved correctly...

      throw new RestoreFailureException(
          "Corrupted serialization file, reached end-of-file prematurely : " + e.getMessage(), e
      );
    }

    catch (IOException e)
    {
      // Any other I/O error...

      throw new RestoreFailureException(
          "I/O Exception while reading panel collection serialization stream : " +
          e.getMessage(), e
      );
    }
  }


  /**
   * Deserialized a primitive long value from Java serialization stream.
   *
   * @param ois   object input stream for the file to deserialize -- assumes
   *              the stream is positioned for reading a long value.
   *
   * @return      the long value representing the max oid value in designer
   *              legacy serialization format
   *
   * @throws RestoreFailureException
   *              if deserializing the value fails for any reason
   */
  private Long deserializeMaxOID(ObjectInputStream ois) throws RestoreFailureException
  {
    try
    {
      return ois.readLong();
    }

    catch (EOFException e)
    {
      throw new RestoreFailureException(
          "Attempt to read past end-of-file restoring max_oid value : " +
          e.getMessage(), e
      );
    }

    catch (IOException e)
    {
      throw new RestoreFailureException(
          "I/O Exception while reading max_oid from serialization stream : " +
          e.getMessage(), e
      );
    }
  }


  /**
   * Adds thread context variables username and account ID to log statements.
   *
   * @see LogFacade#addUserName(String)
   * @see LogFacade#addAccountID(Long)
   */
  private void addContextLog()
  {
    LogFacade.addUserName(user.getUsername());
    LogFacade.addAccountID(user.getAccount().getOid());
  }

  /**
   * Removes thread context variables username and account ID from log statements.
   *
   * @see org.openremote.modeler.logging.LogFacade#removeUserName()
   * @see org.openremote.modeler.logging.LogFacade#removeAccountID()
   */
  private void removeContextLog()
  {
    LogFacade.removeUserName();
    LogFacade.removeAccountID();
  }


  private boolean hasXMLUIState()
  {
    return false; // TODO
  }


  /**
   * Halts account save/restore operations in case of critical errors.
   *
   * @param adminMessage    Message to log to admins
   * @param userMessage     Message to display to user
   */
  private void haltAccount(String adminMessage, String userMessage)
  {
    haltAccount(adminMessage, userMessage, null);
  }


  /**
   * Halts account save/restore operations in case of critical errors
   * 
   * @param adminMessage    Message to log to admins
   * @param userMessage     Message to display to user
   * @param exception       The exception that caused the halt operation
   */
  private void haltAccount(String adminMessage, String userMessage, Throwable exception)
  {

    //   - stop save/restore operations on this account
    //   - notify admins
    //   - inform user

    haltAccountSave.add(user.getAccount().getOid());

    if (exception != null)
    {
      admin.alert(adminMessage, exception);

      throw new UIRestoreException(userMessage, exception);
    }

    else
    {
      admin.alert(adminMessage);

      throw new UIRestoreException(userMessage);
    }
  }

  /**
   * Helper for logging user information.
   *
   * TODO : should be reused via User domain object
   *
   * @param currentUser   current logged in user (as per the http session associated with this
   *                      thread)
   *
   * @return    string with user name, email, role and account id information
   */
  private String printUserAccountLog(User currentUser)
  {
    return "(User: " + currentUser.getUsername() +
           ", Email: " + currentUser.getEmail() +
           ", Roles: " + currentUser.getRole() +
           ", Account ID: " + currentUser.getAccount().getOid() +
           ")";
  }




  // Nested Classes -------------------------------------------------------------------------------


  /**
   * TODO : move to top level class
   */
  private static class PerformanceLog
  {

    // Enums ----------------------------------------------------------------------------------------

    /**
     * Typesafe performance logging categories. These are specific log categories that
     * record resource, execution and network performance
     */
    public enum Category implements LogFacade.Hierarchy
    {
      /**
       * State restore performance logs.
       *
       * The canonical log category name is defined in
       * {@link PerformanceLog#RESTORE_PERFORMANCE_LOG_CATEGORY}
       *
       * @see org.openremote.modeler.service.impl.DesignerState
       */
      RESTORE_PERFORMANCE(RESTORE_PERFORMANCE_LOG_CATEGORY, "State Restore Performance Log"),

      /**
       * Designer server-side save performance logs.
       *
       * The canonical log category name is defined in
       * {@link PerformanceLog#SAVE_PERFORMANCE_LOG_CATEGORY}
       *
       * @see org.openremote.modeler.service.impl.DesignerState
       */
      SAVE_PERFORMANCE(SAVE_PERFORMANCE_LOG_CATEGORY, "Save Performance Log"),

      ;



      // Instance Fields ----------------------------------------------------------------------------

      /**
       * Stores canonical log hierarchy name with a string dot-notation as defined for Java util
       * logging (JUL) and Log4j frameworks.
       */
      private String canonicalLogCategoryName;
      private String displayName;


      // Constructors -------------------------------------------------------------------------------

      private Category(String canonicalLogCategoryName, String displayName)
      {
        this.canonicalLogCategoryName = canonicalLogCategoryName;
        this.displayName = displayName;
      }


      // Implements LogFacade.Hierarchy -------------------------------------------------------------

      @Override public String getCanonicalLogCategoryName()
      {
        return canonicalLogCategoryName;
      }
    }



    // Constants ------------------------------------------------------------------------------------

    /**
     * Common log subcategory name for performance logs.
     */
    public final static String PERFORMANCE_LOG_CATEGORY = ".Performance";

    /**
     * Common log subcategory name for execution performance logs.
     *
     * This should always be a child category of {@link #PERFORMANCE_LOG_CATEGORY}.
     */
    public final static String EXECUTION_PERFORMANCE_LOG_CATEGORY = ".Execution";


    /**
     * Canonical log hierarchy name for Designer restore operation performance. <p>
     *
     * @see Category#RESTORE_PERFORMANCE
     */
    public final static String RESTORE_PERFORMANCE_LOG_CATEGORY =
        LogFacade.RECOVERY_LOG_CATEGORY + PERFORMANCE_LOG_CATEGORY;

    /**
     * Canonical log hierarchy name for Designer save operation performance. <p>
     *
     * @see Category#SAVE_PERFORMANCE
     */
    public final static String SAVE_PERFORMANCE_LOG_CATEGORY =
        LogFacade.STATE_SAVE_LOG_CATEGORY + PERFORMANCE_LOG_CATEGORY;



    // Instance Fields ----------------------------------------------------------------------------


    /**
     * Stores the start time for measuring execution performance.
     *
     * @see #startTimer()
     */
    private long startTime = 0;

    /**
     * The log category for performance logging. Depending on the type of measurement, actual
     * logs may be written to a sub-category of this category, such as
     * {@link #EXECUTION_PERFORMANCE_LOG_CATEGORY}.
     */
    private Category performanceCategory;



    // Constructors -------------------------------------------------------------------------------


    /**
     * Constructs a new performance log facade with a given category. The category is a top
     * level performance related category where logs are added to specific sub-categories
     * depending on their type (execution, network, resource use, etc.).
     *
     * @see #EXECUTION_PERFORMANCE_LOG_CATEGORY
     *
     * @param category    See {@link PerformanceLog.Category}
     */
    public PerformanceLog(Category category)
    {
       this.performanceCategory = category;
    }



    // Public Instance Methods --------------------------------------------------------------------


    /**
     * Start measuring execution performance with this performance log instance. Notice that
     * the measurements are not designed or intended for fine-grained performance measurements
     * but for coarse-grained granularity at a tenth of a second or higher.
     */
    public void startTimer()
    {
      startTime = System.currentTimeMillis();
    }


    /**
     * Stop measuring execution performance and log a related message to a
     * {@link #EXECUTION_PERFORMANCE_LOG_CATEGORY} subcategory of the configured logging
     * category of this instance.
     *
     * @see #performanceCategory
     *
     * @param logMessage    The message to log when stopping the timer. The first argument
     *                      (<tt>{0}</tt>) will be used to store the measured execution time
     *                      in seconds.
     */
    public void stopTimer(String logMessage)
    {
      float time = (float)(System.currentTimeMillis() - startTime) / 1000;

      LogFacade logger = LogFacade.getInstance(new LogFacade.Hierarchy()
        {
          @Override public String getCanonicalLogCategoryName()
          {
            return performanceCategory.getCanonicalLogCategoryName() + EXECUTION_PERFORMANCE_LOG_CATEGORY;
          }
        }
      );

      logger.info(logMessage, new DecimalFormat("######00.000").format(time));
    }
  }


  private static class RestoreFailureException extends Exception
  {
    RestoreFailureException(String msg)
    {
      super(msg);
    }

    RestoreFailureException(String msg, Throwable cause)
    {
      super(msg, cause);
    }
  }
  
  public void setDeviceCommandService(DeviceCommandService deviceCommandService) {
    this.deviceCommandService = deviceCommandService;
  }

  public void setDeviceMacroService(DeviceMacroService deviceMacroService) {
    this.deviceMacroService = deviceMacroService;
  }

  public void setSensorService(SensorService sensorService) {
    this.sensorService = sensorService;
  }

  public void setSliderService(SliderService sliderService) {
    this.sliderService = sliderService;
  }

  public void setSwitchService(SwitchService switchService) {
    this.switchService = switchService;
  }

}
