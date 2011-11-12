package org.openremote.web.console.widget;

import java.util.ArrayList;
import java.util.List;
import org.openremote.web.console.event.ConsoleUnitEventManager;
import org.openremote.web.console.event.drag.DragCancelEvent;
import org.openremote.web.console.event.drag.DragEndEvent;
import org.openremote.web.console.event.drag.DragMoveEvent;
import org.openremote.web.console.event.drag.DragStartEvent;
import org.openremote.web.console.event.drag.Draggable;
import org.openremote.web.console.event.hold.HoldEvent;
import org.openremote.web.console.event.hold.HoldHandler;
import org.openremote.web.console.event.press.PressCancelEvent;
import org.openremote.web.console.event.press.PressCancelHandler;
import org.openremote.web.console.event.press.PressEndEvent;
import org.openremote.web.console.event.press.PressEndHandler;
import org.openremote.web.console.event.press.PressMoveEvent;
import org.openremote.web.console.event.press.PressStartEvent;
import org.openremote.web.console.event.press.PressStartHandler;
import org.openremote.web.console.event.rotate.RotationEvent;
import org.openremote.web.console.event.rotate.RotationHandler;
import org.openremote.web.console.event.swipe.SwipeEvent;
import org.openremote.web.console.event.swipe.SwipeHandler;
import org.openremote.web.console.event.tap.DoubleTapEvent;
import org.openremote.web.console.event.tap.DoubleTapHandler;
import org.openremote.web.console.event.tap.TapEvent;
import org.openremote.web.console.event.tap.TapHandler;
import org.openremote.web.console.event.ui.NavigateEvent;
import org.openremote.web.console.panel.entity.Navigate;
import org.openremote.web.console.util.BrowserUtils;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public abstract class InteractiveConsoleComponent extends ConsoleComponentImpl implements Interactive, TapHandler {
	private List<HandlerRegistration> handlerRegistrations = new ArrayList<HandlerRegistration>();
	private List<Widget> interactiveChildren = new ArrayList<Widget>();
	protected boolean handlersRegistered = false;
	PressStartEvent startEvent = null;
	protected PressMoveEvent lastMoveEvent = null;
	HandlerManager eventBus = ConsoleUnitEventManager.getInstance().getEventBus();
	protected Navigate navigate;
	protected Boolean hasControlCommand = false;
	
	protected InteractiveConsoleComponent(Widget container, String className) {
		super(container, className);
	}
	
	protected List<Widget> getInteractiveChildren() {
		return interactiveChildren;
	}
	
	@Override
	public void onTouchStart(TouchStartEvent event) {
		event.stopPropagation();
		startEvent = new PressStartEvent(event);
		eventBus.fireEvent(startEvent);
		this.fireEvent(startEvent);
	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
		event.stopPropagation();
		startEvent = new PressStartEvent(event);
		eventBus.fireEvent(startEvent);
		this.fireEvent(startEvent);
	}

	@Override
	public void onTouchEnd(TouchEndEvent event) {
		event.stopPropagation();
		PressEndEvent endEvent = null;
		if (lastMoveEvent != null) {
			endEvent = new PressEndEvent(lastMoveEvent);
		} else if (startEvent != null) {
			endEvent = new PressEndEvent(startEvent);
		}
		if (endEvent != null) {
			eventBus.fireEvent(endEvent);
			this.fireEvent(endEvent);
		}
		reset();
	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		event.stopPropagation();
		PressEndEvent endEvent = new PressEndEvent(event); 
		eventBus.fireEvent(endEvent);
		this.fireEvent(endEvent);
		reset();
	}
	
	@Override
	public void onMouseOut(MouseOutEvent event) {
		this.fireEvent(new PressCancelEvent(event));
	}

	protected void reset() {
		startEvent = null;
		lastMoveEvent = null;
	}
	
	
	/**
	 * Add Mouse and Touch Handlers to either entire console component or specified
	 * child widget
	 */
	protected void registerHandlers() {
		registerHandlers(this);
	}
	
	protected void registerHandlers(Widget component) {
		if(BrowserUtils.isMobile()) {
			storeHandler(component.addDomHandler(this, TouchStartEvent.getType()));
			storeHandler(component.addDomHandler(this, TouchEndEvent.getType()));
		} else {
			storeHandler(component.addDomHandler(this, MouseDownEvent.getType()));
			storeHandler(component.addDomHandler(this, MouseUpEvent.getType()));
			storeHandler(component.addDomHandler(this, MouseOutEvent.getType()));
		}
		
		if (component instanceof PressStartHandler) {
			PressStartHandler pressableComponent = (PressStartHandler) component;
			storeHandler(component.addHandler(pressableComponent, PressStartEvent.getType()));
		}

		if (component instanceof PressEndHandler) {
			PressEndHandler pressableComponent = (PressEndHandler) component;
			storeHandler(component.addHandler(pressableComponent, PressEndEvent.getType()));
		}

		if (component instanceof PressCancelHandler) {
			PressCancelHandler pressableComponent = (PressCancelHandler) component;
			storeHandler(component.addHandler(pressableComponent, PressCancelEvent.getType()));
		}
		
		if (component instanceof Draggable) {
			Draggable draggableComponent = (Draggable) component;
			storeHandler(component.addHandler(draggableComponent, DragStartEvent.getType()));
			storeHandler(component.addHandler(draggableComponent, DragMoveEvent.getType()));
			storeHandler(component.addHandler(draggableComponent, DragEndEvent.getType()));
			storeHandler(component.addHandler(draggableComponent, DragCancelEvent.getType()));
		}
		
		if (component instanceof TapHandler) {
			TapHandler tappableComponent = (TapHandler) component;
			storeHandler(component.addHandler(tappableComponent, TapEvent.getType()));
		}
		
		if (component instanceof DoubleTapHandler) {
			DoubleTapHandler dblTappableComponent = (DoubleTapHandler) component;
			storeHandler(component.addHandler(dblTappableComponent, DoubleTapEvent.getType()));
		}
		
		if (component instanceof HoldHandler) {
			HoldHandler holdableComponent = (HoldHandler) component;
			storeHandler(component.addHandler(holdableComponent, HoldEvent.getType()));
		}
		
		if (component instanceof SwipeHandler) {
			SwipeHandler pressableComponent = (SwipeHandler) component;
			storeHandler(component.addHandler(pressableComponent, SwipeEvent.getType()));
		}
		
		if (component instanceof RotationHandler) {
			RotationHandler pressableComponent = (RotationHandler) component;
			storeHandler(component.addHandler(pressableComponent, RotationEvent.getType()));
		}
	}
	
	protected void unRegisterHandlers() {
		for (HandlerRegistration handler : handlerRegistrations) {
			handler.removeHandler();
		}
		handlerRegistrations.clear();
		handlersRegistered = false;
	}
	
	private void storeHandler(HandlerRegistration registration) {
		handlerRegistrations.add(registration);
		handlersRegistered = true;
	}
	
	public void addInteractiveChild(Widget component) {
		interactiveChildren.add(component);
	}
	
	public Navigate getNaviate() {
		return navigate;
	}
	
	public void setNavigate(Navigate navigate) {
		this.navigate = navigate;
	}
	
	public Boolean getHasControlCommand() {
		return hasControlCommand;
	}
	
	public void setHasControlCommand(Boolean hasControlCommand) {
		this.hasControlCommand = hasControlCommand;
	}
	
	@Override
	public void onTap(TapEvent event) {
		// TODO Auto-generated method stub
		if (navigate != null) {
			eventBus.fireEvent(new NavigateEvent(navigate));
		} else if (hasControlCommand) {
			// TODO: Send Command
			Window.alert("SEND COMMAND");
			//eventBus.fireEvent(new CommandEvent(getId()));
		}
	}
}
