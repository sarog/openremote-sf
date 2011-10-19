/* OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2011, OpenRemote Inc.
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
package org.openremote.modeler.client.widget.buildingmodeler;

import org.openremote.modeler.client.Constants;
import org.openremote.modeler.client.widget.FormWindow;
import org.openremote.modeler.domain.Device;
import org.openremote.modeler.selenium.DebugId;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Encoding;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Method;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

/**
 * The window allows user to import Infrared Command file.
 * 
 * @author Dan 2009-8-21
 */
public class IRFileImportWindow extends FormWindow {

	@SuppressWarnings("unused")
	private Device device;
	private final IRFileImportWindow importWindow;
	private FileUploadField fileUploadField;
	private IRFileImportForm importForm;
	private FlowPanel deviceChooser = new FlowPanel();
	private VerticalPanel fileUploadPanel = new VerticalPanel();
	private LabelField errorLabel;
	private Button loadBtn;
	private Button resetBtn;

	public IRFileImportWindow(BeanModel deviceBeanModel) {
		super();
		importWindow = this;
		setSize(800, 600);
		initial("Import IR Command from file");
		this.ensureDebugId(DebugId.IMPORT_WINDOW);
		this.device = (Device) deviceBeanModel.getBean();
		importForm = new IRFileImportForm(this, deviceBeanModel);
		deviceChooser.add(importForm);
		deviceChooser.setLayoutData(new FillLayout());
		add(deviceChooser);
		importForm.disable();
		show();
	}

	private void initial(String heading) {
		setHeading(heading);
		form.setAction(GWT.getModuleBaseURL()
				+ "fileUploadController.htm?method=importIRFile");
		form.setEncoding(Encoding.MULTIPART);
		form.setMethod(Method.POST);

		createFileUploadField();
		createLoadResetButton();
		errorLabel = new LabelField();
		errorLabel.setVisible(false);
		errorLabel.setStyleName("importErrorMessage");
		errorLabel.setAutoWidth(true);

		form.setWidth(800);
		
		form.add(fileUploadPanel);
		HorizontalPanel buttonPanel = new HorizontalPanel();
		buttonPanel.add(loadBtn);
		buttonPanel.add(resetBtn);
		form.add(buttonPanel);
		form.add(errorLabel);
		deviceChooser.add(form);
		addListenersToForm();
	}

	private void addListenersToForm() {
		form.addListener(Events.Submit, new Listener<FormEvent>() {
			public void handleEvent(FormEvent be) {
				importForm.hideComboBoxes();
				if (be.getResultHtml().contains(Constants.IRFILE_UPLOAD_ERROR)) {
					reportError(be.getResultHtml());
				} else {
					
					errorLabel.setVisible(false);
					importForm.setVisible(true);
					importWindow.unmask();
					importForm.enable();
					importForm.showBrands();
				}

			}
		});
	}

	private void reportError(String errorMessage) {
		unmask();
		form.clear();
		form.clearState();
		loadBtn.enable();
		errorLabel.setText(errorMessage);
		errorLabel.setVisible(true);
	}

	private void createLoadResetButton() {
		loadBtn = new Button("Load");
		loadBtn.ensureDebugId(DebugId.IRFILE_IMPORT_WINDOW_LOAD_BTN);
		loadBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				if (form.isValid()) {
					importWindow
							.mask("Please wait while file is loaded and processed");
					form.submit();
				}

			}
		});
		
		resetBtn = new Button("Clear");
        resetBtn.ensureDebugId(DebugId.KNX_IMPORT_WINDOW_CLEAR_BTN);
        resetBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
				errorLabel.setVisible(false);
                importForm.setVisible(false);
                importForm.hideComboBoxes();
                fileUploadField.clear();
            }
        });

		

	}

	private void createFileUploadField() {
		fileUploadField = new FileUploadField();
		fileUploadField.setName("file");
		fileUploadField.setAllowBlank(false);
		fileUploadField.setFieldLabel("File");
		fileUploadField.setStyleAttribute("overflow", "hidden");
		fileUploadPanel.add(fileUploadField);

	}

}
