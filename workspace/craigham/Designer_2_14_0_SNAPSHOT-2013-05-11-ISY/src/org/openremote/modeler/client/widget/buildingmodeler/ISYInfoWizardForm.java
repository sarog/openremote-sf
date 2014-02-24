package org.openremote.modeler.client.widget.buildingmodeler;

import net.customware.gwt.dispatch.client.DispatchAsync;

import org.openremote.modeler.client.ModelerGinjector;
import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.widget.CommonForm;
import org.openremote.modeler.shared.isy.CreateISYDeviceAction;
import org.openremote.modeler.shared.isy.CreateISYDeviceResult;

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ISYInfoWizardForm extends CommonForm {

	/** The Constant DEVICE_NAME. */
	public static final String HOST = "host";

	/** The Constant DEVICE_VENDOR. */
	public static final String PORT = "port";

	/** The Constant DEVICE_MODEL. */

	/** The Constant CONTROLLERS. */
	public static final String KEY2 = "key2";

	/** The wrapper. */
	final protected Component wrapper;

	TextField<String> hostField;
	TextField<String> userNameField;
	TextField<String> passwordField;

	/**
	 * Instantiates a new device info wizard form.
	 * 
	 * @param wrapper
	 *            the wrapper
	 * @param deviceBeanModel
	 *            the device bean model
	 */
	public ISYInfoWizardForm(Component parent) {
		super();
		this.wrapper = parent;
		createFields();
		addListener(Events.BeforeSubmit, new Listener<FormEvent>() {
			public void handleEvent(FormEvent be) {
				ModelerGinjector injector = GWT.create(ModelerGinjector.class);
				DispatchAsync dispatcher = injector.getDispatchAsync();

				CreateISYDeviceAction action = new CreateISYDeviceAction(
						hostField.getValue(), userNameField.getValue(), passwordField
								.getValue());

				dispatcher.execute(action,
						new AsyncCallback<CreateISYDeviceResult>() {

							@Override
							public void onFailure(Throwable caught) {
								Info.display("ERRORc", caught.getMessage());
								MessageBox.alert("ERRORc", caught.getMessage(),
										null);
								caught.printStackTrace();
								// TODO: better error reporting
							}

							@Override
							public void onSuccess(CreateISYDeviceResult result) {
								// TODO: might have an error message in result,
								// handle it
								wrapper.fireEvent(SubmitEvent.SUBMIT,
										new SubmitEvent(result.getDevices()));
							}
						});
			}
		});
	}

	/**
	 * Creates the fields.
	 */
	private void createFields() {
		hostField = new TextField<String>();
		hostField.setName(HOST);
		hostField.setFieldLabel("Host Name or IP");
		hostField.setAllowBlank(false);


		userNameField = new TextField<String>();
		// key1Field.setName(KEY1);
		userNameField.setFieldLabel("User Name");
		userNameField.setAllowBlank(false);

		passwordField = new TextField<String>();
		passwordField.setName(KEY2);
		passwordField.setFieldLabel("Password");
		passwordField.setAllowBlank(false);

		// TODO: remove this prompting
		hostField.setValue("192.168.0.17");
		userNameField.setValue("craigh");

		add(hostField);
		add(userNameField);
		add(passwordField);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openremote.modeler.client.widget.CommonForm#isNoButton()
	 */
	@Override
	public boolean isNoButton() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.extjs.gxt.ui.client.widget.Component#show()
	 */
	@Override
	public void show() {
		super.show();
		((Window) wrapper).setSize(360, 250);
	}
}