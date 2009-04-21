package org.openremote.irbuilder.xstream;

import org.openremote.irbuilder.domain.*;
/**
 * Created by IntelliJ IDEA.
 * User: finalist
 * Date: Mar 13, 2009
 * Time: 1:38:21 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 * @author allen.wei
 */
public class TestData {

    private static int btnNum = 1;
    private static int eventId = 1;


    public static Activity getActivity() {
        Activity activity = new Activity();
        activity.setId(1);
        activity.setName("activity");
        activity.getScreens().add(getScreen());
        return activity;
    }

    public static Screen getScreen() {
        Screen screen = new Screen();
        screen.setCol(1);
        screen.setName("screen");
        screen.setRow(1);
        screen.setId(1);
        screen.getControls().add(getCommonButton());
        screen.getControls().add(getCommonButton());
        screen.getControls().add(getMacroButton());
        screen.getControls().add(getMacroButton());
        return screen;
    }

    public static IPhoneButton getCommonButton() {
        IPhoneButton commonIPhoneButton = new IPhoneButton();
        commonIPhoneButton.setHeight(1);
        commonIPhoneButton.setWidth(1);
        commonIPhoneButton.setId(btnNum++);
        commonIPhoneButton.setX(1);
        commonIPhoneButton.setY(1);
        commonIPhoneButton.setLabel("commonIPhoneButton" + btnNum++);
        return commonIPhoneButton;
    }

    public static IPhoneButton getMacroButton() {
        IPhoneButton macroIPhoneButton = new IPhoneButton();
        macroIPhoneButton.setHeight(1);
        macroIPhoneButton.setWidth(1);
        macroIPhoneButton.setId(btnNum++);
        macroIPhoneButton.setX(1);
        macroIPhoneButton.setY(1);
        macroIPhoneButton.setLabel("macroIPhoneButton" + btnNum++);
        return macroIPhoneButton;
    }

    public static IREvent getIREvent() {
        IREvent irEvent = new IREvent();
        irEvent.setLabel("IREvent" + eventId++);
        irEvent.setName("MP8640");
        irEvent.setCommand("DVD");
        irEvent.setId(eventId);
        return irEvent;
    }

    public static KNXEvent getKNXEvent() {
        KNXEvent knxEvent = new KNXEvent();
        knxEvent.setGroupAddress("4/1/1");
        knxEvent.setLabel("KNXEvent" + eventId++);
        knxEvent.setId(eventId);
        return knxEvent;
    }

    public static X10Event getX10Event() {
        X10Event x10Event = new X10Event();
        x10Event.setAddress("A1");
        x10Event.setCommand("0x01");
        x10Event.setId(eventId++);
        x10Event.setLabel("X10Event" + eventId);
        return x10Event;
    }

    public static EventsWrapper getEventsWrapper() {
        EventsWrapper eventsWrapper = new EventsWrapper();


        eventsWrapper.getKnxEvents().add(getKNXEvent());
        eventsWrapper.getKnxEvents().add(getKNXEvent());
        eventsWrapper.getKnxEvents().add(getKNXEvent());

        eventsWrapper.getX10Events().add(getX10Event());
        eventsWrapper.getX10Events().add(getX10Event());
        eventsWrapper.getX10Events().add(getX10Event());


        eventsWrapper.getIrEvents().add(getIREvent());
        eventsWrapper.getIrEvents().add(getIREvent());
        eventsWrapper.getIrEvents().add(getIREvent());

        return eventsWrapper;
    }

    public static EventsWrapper getEmptryEventsWrapper() {
        EventsWrapper eventsWrapper = new EventsWrapper();
        return eventsWrapper;
    }

    public static ControllerButton getControllerButton() {
        ControllerButton controllerButton = new ControllerButton();
        controllerButton.getEventIds().add(1);
        controllerButton.setId(1);
        return controllerButton;
    }

    public static ControllerWrapper getControllerWrapper() {
        ControllerWrapper controllerWrapper = new ControllerWrapper();
        controllerWrapper.setEventsWrapper(getEventsWrapper());
        controllerWrapper.getButtons().add(getControllerButton());
        controllerWrapper.getButtons().add(getControllerButton());
        controllerWrapper.getButtons().add(getControllerButton());
        return controllerWrapper;
    }


    public static ControllerWrapper getEmptyControllerWrapper() {
        ControllerWrapper controllerWrapper = new ControllerWrapper();
       controllerWrapper.setEventsWrapper(getEmptryEventsWrapper());
        return controllerWrapper;
    }
}
