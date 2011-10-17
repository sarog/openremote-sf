/* OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2010, OpenRemote Inc.
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

import java.util.List;

import org.openremote.modeler.client.IRCommandInfo;
import org.openremote.modeler.client.listener.FormResetListener;
import org.openremote.modeler.client.listener.FormSubmitListener;
import org.openremote.modeler.client.proxy.IrFileParserProxy;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.client.widget.FormWindow;
import org.openremote.modeler.domain.Device;
import org.openremote.modeler.irfileparser.GlobalCache;
import org.openremote.modeler.irfileparser.IRLed;
import org.openremote.modeler.irfileparser.IRTrans;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ListBox;

/**
 * The window creates or updates a deviceCommand into server.
 */
public class IRFileImportToProtocolForm extends FormWindow {


	/** The device. */
	private Device device = null;
	private VerticalPanel gCPanel;
	private TextField<String> gCip;
	private TextField<String> tcpPort;
	protected boolean hideWindow = true;

	protected LabelField info;

	private TextField<String> connector;

	private VerticalPanel iRTransPanel;

	private TextField<String> udpPort;

	private ComboBox<IRLed> iRLed;

	private TextField<String> iRTransIp;

	private List<IRCommandInfo> selectedFunctions;
	private ComboBox<IRLed> IRLed;
	protected static final String INFO_FIELD = "infoField";

	/**
	 * Instantiates a new device command window.
	 * 
	 * @param wrapper
	 * 
	 * @param device
	 *            the device
	 */
	public IRFileImportToProtocolForm(Component wrapper, Device device) {
		super();
		this.device = device;
		setHeading("New command");
		initial();
		show();
	}

	/**
	 * Initial.
	 */
	private void initial() {
		setWidth(380);
		setAutoHeight(true);
		setLayout(new FlowLayout());

		form.setWidth(370);

		Button submitBtn = new Button("Submit");
		form.addButton(submitBtn);

		submitBtn.addSelectionListener(new FormSubmitListener(form, submitBtn));

		Button resetButton = new Button("Reset");
		resetButton.addSelectionListener(new FormResetListener(form));
		form.addButton(resetButton);

		form.addListener(Events.BeforeSubmit, new Listener<FormEvent>() {

			@Override
			public void handleEvent(FormEvent be) {
				// TODO add the globalCaché/IRTrans data in a convenient way
				Window.alert(String.valueOf(selectedFunctions.size()));
				
				GlobalCache globalCache = new GlobalCache();
				IRTrans irTrans = new IRTrans();

				if (gCPanel.isEnabled()){
					globalCache = new GlobalCache(gCip.getValue(), tcpPort.getValue(), connector.getValue());
				}else{
					Window.alert(iRLed.getSelection().get(0).getCode());
					irTrans = new IRTrans(iRTransIp.getValue(), udpPort.getValue(), iRLed.getSelection().get(0).getCode());
					
				}
				IrFileParserProxy.saveCommands(device, selectedFunctions,globalCache,irTrans,
						new AsyncSuccessCallback<List<BeanModel>>() {
							@Override
							public void onSuccess(
									List<BeanModel> deviceCommandModels) {
								Window.alert("should be saved");
							}
						});
			}
		});
		createFields();
		add(form);
	}

	/**
	 * Creates the fields.
	 * 
	 * @param protocols
	 *            the protocols
	 */
	private void createFields() {
		final ListBox product = new ListBox();
		product.addItem("Please choose a product"); // addItem("Please choose a product");
		product.addItem("GlobalCaché	");
		product.addItem("IRTrans");
		form.add(product);

		// globalCaché panel
		gCPanel = new VerticalPanel();
		gCPanel.setSpacing(10);

		FieldSet gCFieldSet = new FieldSet();
		FormLayout gCLayout = new FormLayout();
		gCLayout.setLabelWidth(80);
		gCFieldSet.setLayout(gCLayout);

		gCip = new TextField<String>();
		gCip.setFieldLabel("IP address / HostName");
		gCFieldSet.add(gCip);
		tcpPort = new TextField<String>();
		tcpPort.setValue("4998");
		tcpPort.setFieldLabel("TCP Port");
		gCFieldSet.add(tcpPort);
		connector = new TextField<String>();
		connector.setValue("4:1");
		connector.setFieldLabel("Connector");
		gCFieldSet.add(connector);

		gCPanel.add(gCFieldSet);
		gCPanel.setVisible(false);
		form.add(gCPanel);

		// IRTrans Panel
		iRTransPanel = new VerticalPanel();
		iRTransPanel.setSpacing(10);

		FieldSet iRTransFieldSet = new FieldSet();
		FormLayout iRTransLayout = new FormLayout();
		iRTransLayout.setLabelWidth(80);
		iRTransFieldSet.setLayout(iRTransLayout);
		iRTransIp = new TextField<String>();
		iRTransIp.setFieldLabel(new String("Ip address Host name"));
		iRTransFieldSet.add(iRTransIp);
		udpPort = new TextField<String>();
		udpPort.setValue("21000");
		udpPort.setFieldLabel(new String("UDP Port"));
		iRTransFieldSet.add(udpPort);
		iRLed = new ComboBox<IRLed>();
		ListStore<IRLed> irStore = new ListStore<IRLed>();
		irStore.add(new IRLed("Internal", "i"));
		irStore.add(new IRLed("External", "e"));
		irStore.add(new IRLed("Both", "b"));
		iRLed.setStore(irStore);
		iRLed.setFieldLabel("IR Led");
		iRLed.setAllowBlank(false);
		iRLed.setDisplayField("value");
		iRLed.setLazyRender(false);
		iRLed.setValue(new IRLed("Internal", "i"));
		iRLed.setTriggerAction(TriggerAction.ALL);

		iRTransFieldSet.add(iRLed);

		iRTransPanel.add(iRTransFieldSet);
		iRTransPanel.setVisible(false);
		form.add(iRTransPanel);

		product.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				switch (product.getSelectedIndex()) {
				case 1:
					gCPanel.setEnabled(true);
					gCPanel.setVisible(true);
					iRTransPanel.setEnabled(false);
					iRTransPanel.setVisible(false);
					break;
				case 2:
					gCPanel.setEnabled(false);
					gCPanel.setVisible(false);
					iRTransPanel.setEnabled(true);
					iRTransPanel.setVisible(true);
					break;
				default:
					gCPanel.setEnabled(false);
					gCPanel.setVisible(false);
					iRTransPanel.setVisible(false);
					iRTransPanel.setVisible(false);
					break;
				}
			}
		});
		form.layout();
	}



	public void setSelectedFunctions(List<IRCommandInfo> selectedItems) {
		this.selectedFunctions = selectedItems;

	}
}
