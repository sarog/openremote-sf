/* OpenRemote, the Home of the Digital Home.
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
package org.openremote.web.console.client;

import org.openremote.web.console.event.ConsoleUnitEventManager;
import org.openremote.web.console.event.rotate.RotationEvent;
import org.openremote.web.console.panel.entity.PanelSizeInfo;
import org.openremote.web.console.service.AutoBeanService;
import org.openremote.web.console.service.LocalDataService;
import org.openremote.web.console.service.LocalDataServiceImpl;
import org.openremote.web.console.unit.ConsoleUnit;
import org.openremote.web.console.util.BrowserUtils;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Toolbar that sits on side of screen in desktop mode
 *  
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 */
public class SlidingToolbar extends Composite implements KeyUpHandler, BlurHandler {
	private static SlidingToolbar instance;
	private VerticalPanel mainPanel;
	private Label tab;
	private boolean isHidden = true;
	private int maxMarginLeft = -50;
	private int minMarginLeft = -320;
	private TextBox widthInput;
	private TextBox heightInput;
	private boolean widthValid = false;
	private boolean heightValid = false;
	private RadioButton fullscreenOpt;
	private RadioButton fixedOpt;
	private PanelSizeType currentType;
	
	private enum PanelSizeType {
		FULLSCREEN,
		FIXED;
	}
	
	private SlidingToolbar(PanelSizeInfo sizeInfo) {
		HorizontalPanel container = new HorizontalPanel();
		this.initWidget(container);
		setWidth("340px");
		setStylePrimaryName("toolbar");
		setHeight("100%");
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
		
		// Configure Tab
		tab.setWidth("20px");
		tab.setHeight("50px");
		tab.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		Element tabElem = tab.getElement();
		DOM.setStyleAttribute(tabElem, "lineHeight", "50px");
		
		// Add to Window
		RootPanel.get().add(this, 0, 0);
		
		addAnimation();
		
		createToolbarContent(sizeInfo);
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
	
	private void createToolbarContent(PanelSizeInfo sizeInfo) {
		// Create Toolbar Content
		HTML title = new HTML();
		title.setHTML("<span>OpenRemote</span><br /><span>Web Console 2.0</span>");
		title.setWidth("100%");
		title.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		title.setStylePrimaryName("title");
		mainPanel.add(title);
		
		VerticalPanel panelSizePanel = new VerticalPanel();
		panelSizePanel.setStylePrimaryName("panelSizePanel");
		panelSizePanel.setHeight("150px");
		panelSizePanel.add(new HTML("<h3>Panel Size</h3><p>If you specify a panel size then the dimensions are as if the panel was in Portrait orientation (i.e. Width&lt;=Height).<br /><br />Min Panel Size: 320px x 480px</p>"));
		fullscreenOpt = new RadioButton("panelSize","Fullscreen");
		fullscreenOpt.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				setPanelSizeType(PanelSizeType.FULLSCREEN);				
			}				
		});
		fixedOpt = new RadioButton("panelSize","Specify (W X H):");
		fixedOpt.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				setPanelSizeType(PanelSizeType.FIXED);				
			}				
		});		
		
		panelSizePanel.add(fullscreenOpt);		
		panelSizePanel.add(fixedOpt);
		
		HorizontalPanel sizeInputPanel = new HorizontalPanel();
		sizeInputPanel.setStylePrimaryName("sizeInputBoxes");
		sizeInputPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		widthInput = new TextBox();
		heightInput = new TextBox();
		widthInput.setStylePrimaryName("panelSize");
		heightInput.setStylePrimaryName("panelSize");
		widthInput.addDomHandler(this, KeyUpEvent.getType());
		widthInput.addDomHandler(this, BlurEvent.getType());
		heightInput.addDomHandler(this, KeyUpEvent.getType());
		heightInput.addDomHandler(this, BlurEvent.getType());
	
		sizeInputPanel.add(widthInput);
		sizeInputPanel.add(new Label("X"));
		sizeInputPanel.add(heightInput);
		
		panelSizePanel.add(sizeInputPanel);
		
		mainPanel.add(panelSizePanel);
		
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
		resizeBtn.getElement().setId("resizeBtn");
		resizeBtn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				int widthInt = 0;
				int heightInt = 0;
				if (currentType == PanelSizeType.FULLSCREEN) {
					WebConsole.getConsoleUnit().setSize(true);
				} else {
					if (widthValid && heightValid) {
						try {
							widthInt = Integer.parseInt(widthInput.getValue());
							heightInt = Integer.parseInt(heightInput.getValue());
							if (widthInt <= heightInt && widthInt >=320 && heightInt >= 480) {
								ConsoleUnit unit = WebConsole.getConsoleUnit();
								unit.setSize(widthInt, heightInt, false);
								unit.setPosition();
								unit.setOrientation(unit.getOrientation());
								
//								// Check if unit went to fullscreen due to large dimensions
//								if (unit.getIsFullscreen()) {
//									currentType = PanelSizeType.FULLSCREEN;
//									widthInt = 0;
//									heightInt = 0;
//								}
							} else {
								Window.alert("Width must be less than or equal to the Height.\n\nMinimum Panel Size: 320px x 480px");
								return;
							}
						} catch (Exception e) {return;}
					} else {
						Window.alert("Width and Height values must be Integers!");
						return;
					}
				}
				
				// Store new preference
				LocalDataService dataService = LocalDataServiceImpl.getInstance();
				PanelSizeInfo sizeInfo = AutoBeanService.getInstance().getFactory().create(PanelSizeInfo.class).as();
				sizeInfo.setPanelSizeType(currentType == PanelSizeType.FULLSCREEN ? "fullscreen" : "fixed");
				sizeInfo.setPanelSizeHeight(heightInt);
				sizeInfo.setPanelSizeWidth(widthInt);
				dataService.setObject("panelSizeInfo", AutoBeanService.getInstance().toJsonString(sizeInfo));
				syncUI(sizeInfo);
			}
		});
		
//		// Panel Width Input
//		widthLbl = new Label("Panel Width:");
//		widthLbl.setStylePrimaryName("toolbarInputLabel");
//		widthLbl.setWidth("100%");
//		widthLbl.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
//		mainPanel.add(widthLbl);
//		widthInput = new TextBox();
//		widthInput.setStylePrimaryName("toolbarInput");
//		widthInput.setWidth("100%");
//		widthInput.addDomHandler(this, KeyUpEvent.getType());
//		widthInput.addDomHandler(this, BlurEvent.getType());
//		mainPanel.add(widthInput);
//		validateInput(widthInput);
//		
//		// Panel Height Input
//		heightLbl = new Label("Panel Height:");
//		heightLbl.setStylePrimaryName("toolbarInputLabel");
//		heightLbl.setWidth("100%");
//		heightLbl.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
//		mainPanel.add(heightLbl);
//		heightInput = new TextBox();
//		heightInput.setStylePrimaryName("toolbarInput");
//		heightInput.setWidth("100%");		
//		heightInput.addDomHandler(this, KeyUpEvent.getType());
//		heightInput.addDomHandler(this, BlurEvent.getType());
//		mainPanel.add(heightInput);
//		validateInput(heightInput);
		
		panelSizePanel.add(resizeBtn);
		panelSizePanel.setCellHorizontalAlignment(resizeBtn, HasHorizontalAlignment.ALIGN_CENTER);
		mainPanel.add(rotateBtn);
		
		// Add Version String
		String versionStr = "Build: " + BrowserUtils.getBuildVersionString();
		mainPanel.add(new Label(versionStr));
		
		// Match UI to Panel Size Info
		syncUI(sizeInfo);
	}
	
	private void syncUI(PanelSizeInfo sizeInfo) {
		if (sizeInfo.getPanelSizeType().equals("fullscreen")) {
			setPanelSizeType(PanelSizeType.FULLSCREEN);
			widthInput.setValue("");
			heightInput.setValue("");
		} else {
			setPanelSizeType(PanelSizeType.FIXED);
			widthInput.setValue(sizeInfo.getPanelSizeWidth().toString());
			heightInput.setValue(sizeInfo.getPanelSizeHeight().toString());
			validateInput(widthInput);
			validateInput(heightInput);
		}
	}
	
	public void show() {
		setVisible(true);
	}
	
	public void hide() {
		setVisible(false);
	}
	
	public static synchronized void initialise(PanelSizeInfo sizeInfo) {
		if (instance == null) {
			instance = new SlidingToolbar(sizeInfo);
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
		
		if (valid) {
			input.getElement().removeClassName("invalid");
		} else {
			input.getElement().addClassName("invalid");
		}
		
		if (input == widthInput) {
			widthValid = valid;
		} else {
			heightValid = valid;
		}
	}
	
	private void setPanelSizeType(PanelSizeType sizeType) {
		switch(sizeType) {
			case FULLSCREEN:
				fullscreenOpt.setValue(true);
				fixedOpt.setValue(false);
				widthInput.setEnabled(false);
				heightInput.setEnabled(false);
				break;
			case FIXED:
				fullscreenOpt.setValue(false);
				fixedOpt.setValue(true);
				widthInput.setEnabled(true);
				heightInput.setEnabled(true);
				break;
		}
		currentType = sizeType;
	}
}