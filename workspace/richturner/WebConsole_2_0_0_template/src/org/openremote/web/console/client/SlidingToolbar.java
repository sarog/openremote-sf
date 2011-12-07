package org.openremote.web.console.client;

import org.openremote.web.console.event.ConsoleUnitEventManager;
import org.openremote.web.console.event.rotate.RotationEvent;
import org.openremote.web.console.util.BrowserUtils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SlidingToolbar extends Composite implements KeyUpHandler, BlurHandler {
	private static SlidingToolbar instance;
	private VerticalPanel mainPanel;
	private Label tab;
	private boolean isHidden = true;
	private int maxMarginLeft = -50;
	private int minMarginLeft = -320;
	private Label widthLbl;
	private Label heightLbl;
	private TextBox widthInput;
	private TextBox heightInput;
	private boolean widthValid = false;
	private boolean heightValid = false;
	
	private SlidingToolbar() {
		HorizontalPanel container = new HorizontalPanel();
		this.initWidget(container);
		setWidth("350px");
		setStylePrimaryName("toolbar");
		setHeight(BrowserUtils.getWindowHeight() + "px");
		container.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		DOM.setStyleAttribute(getElement(), "marginLeft", "-320px");
		mainPanel = new VerticalPanel();
		mainPanel.setStylePrimaryName("mainToolbarPanel");
		tab = new Label(">");
		tab.setStylePrimaryName("tabToolbarPanel");
		container.add(mainPanel);
		container.add(tab);
		
		// Configure Main Panel
		mainPanel.setHeight("80%");
		mainPanel.setWidth("320px");
		mainPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		Element mainElem = mainPanel.getElement();
		DOM.setStyleAttribute(mainElem, "paddingLeft", "70px");
		DOM.setStyleAttribute(mainElem, "paddingTop", "20px");
		DOM.setStyleAttribute(mainElem, "paddingRight", "20px");
		DOM.setStyleAttribute(mainElem, "paddingBottom", "20px");
		DOM.setStyleAttribute(mainElem, "marginTop", "10%");
		
		// Configure Tab
		tab.setWidth("30px");
		tab.setHeight("50px");
		tab.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		Element tabElem = tab.getElement();
		DOM.setStyleAttribute(tabElem, "lineHeight", "50px");
		DOM.setStyleAttribute(tabElem, "marginLeft", "-20px");
		DOM.setStyleAttribute(tabElem, "paddingLeft", "20px");
		
		// Add to Window
		BrowserUtils.getConsoleContainer().add(this);
		
		addAnimation();
		
		createToolbarContent();		
	}
	
	private void addAnimation() {
		tab.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (isHidden) {
					// Animate Slide out
					DOM.setStyleAttribute(getElement(), "marginLeft", maxMarginLeft + "px");
				} else {
					// Animate Slide in
					DOM.setStyleAttribute(getElement(), "marginLeft", minMarginLeft + "px");
				}
				
				isHidden = !isHidden;
				
				// Change Tab Text
				if (isHidden) {
					tab.setText(">");
				} else {
					tab.setText("<");
				}				
			}			
		});
	}
	
	private void createToolbarContent() {
		// Create Toolbar Content
		HTML title = new HTML();
		title.setHTML("<span>OpenRemote</span><br /><span>Web Console 2.0</span>");
		title.setWidth("100%");
		title.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		title.setStylePrimaryName("title");
		title.getElement().getStyle().setMarginBottom(50, Unit.PX);
		mainPanel.add(title);
		
		Button rotateBtn = new Button();
		rotateBtn.setWidth("100px");
		rotateBtn.setHeight("50px");
		rotateBtn.setText("ROTATE");
		rotateBtn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String eventOrientation = "landscape";
				if (WebConsole.getConsoleUnit().getOrientation().equals("landscape")) {
					eventOrientation = "portrait";
				}
				ConsoleUnitEventManager.getInstance().getEventBus().fireEvent(new RotationEvent(eventOrientation, BrowserUtils.getWindowWidth(), BrowserUtils.getWindowHeight()));
			}			
		});
		
		Button resizeBtn = new Button();
		resizeBtn.setWidth("100px");
		resizeBtn.setHeight("50px");
		resizeBtn.setText("RESIZE");
		resizeBtn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (widthValid && heightValid) {
					try {
						WebConsole.getConsoleUnit().setSize(Integer.parseInt(widthInput.getValue()), Integer.parseInt(heightInput.getValue()));
					} catch (Exception e) {}
				}
			}
		});
		
		// Panel Width Input
		widthLbl = new Label("Panel Width:");
		widthLbl.setStylePrimaryName("toolbarInputLabel");
		widthLbl.setWidth("100%");
		widthLbl.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		mainPanel.add(widthLbl);
		widthInput = new TextBox();
		widthInput.setStylePrimaryName("toolbarInput");
		widthInput.setWidth("100%");
		widthInput.addDomHandler(this, KeyUpEvent.getType());
		widthInput.addDomHandler(this, BlurEvent.getType());
		mainPanel.add(widthInput);
		validateInput(widthInput);
		
		// Panel Height Input
		heightLbl = new Label("Panel Height:");
		heightLbl.setStylePrimaryName("toolbarInputLabel");
		heightLbl.setWidth("100%");
		heightLbl.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		mainPanel.add(heightLbl);
		heightInput = new TextBox();
		heightInput.setStylePrimaryName("toolbarInput");
		heightInput.setWidth("100%");		
		heightInput.addDomHandler(this, KeyUpEvent.getType());
		heightInput.addDomHandler(this, BlurEvent.getType());
		mainPanel.add(heightInput);
		validateInput(heightInput);
		
		mainPanel.add(resizeBtn);
		mainPanel.add(rotateBtn);
	}
	
	public void show() {
		setVisible(true);
	}
	
	public void hide() {
		setVisible(false);
	}
	
	public static synchronized void initialise() {
		if (instance == null) {
			instance = new SlidingToolbar();
		}
	}

	@Override
	public void onBlur(BlurEvent event) {
		validateInput((TextBox)event.getSource());
	}

	@Override
	public void onKeyUp(KeyUpEvent event) {
		validateInput((TextBox)event.getSource());		
	}
	
	private void validateInput(TextBox input) {
		String inputStr = input.getValue();
		boolean valid = inputStr.matches("^\\d+$");
		Label lbl;
		if (input == widthInput) {
			lbl = widthLbl;
			widthValid = valid;
		} else {
			lbl = heightLbl;
			heightValid = valid;
		}
		
		if (valid) {
			lbl.removeStyleName("invalidToolbarField");
		} else {
			lbl.addStyleName("invalidToolbarField");
		}
	}
}