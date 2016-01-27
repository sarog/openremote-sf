package org.openremote.web.console.widget.panel.form;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.openremote.web.console.client.WebConsole;
import org.openremote.web.console.controller.ControllerCredentials;
import org.openremote.web.console.event.ConsoleUnitEventManager;
import org.openremote.web.console.event.tap.TapEvent;
import org.openremote.web.console.event.tap.TapHandler;
import org.openremote.web.console.event.ui.NavigateEvent;
import org.openremote.web.console.panel.entity.DataValuePair;
import org.openremote.web.console.panel.entity.Field;
import org.openremote.web.console.panel.entity.FormButton;
import org.openremote.web.console.panel.entity.FormLayout;
import org.openremote.web.console.service.AutoBeanService;
import org.openremote.web.console.service.DataBindingService;
import org.openremote.web.console.widget.ConsoleComponent;
import org.openremote.web.console.widget.panel.PanelComponent;
import org.openremote.web.console.widget.panel.form.FormButtonComponent.EnumFormButtonType;
import org.openremote.web.console.widget.panel.form.FormField.EnumFormInputType;
import org.openremote.web.console.widget.Sensor;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;
import com.google.web.bindery.autobean.shared.Splittable;

public class FormPanelComponent extends PanelComponent implements TapHandler {
	private static final String CLASS_NAME = "formPanelComponent";
	private List<FormField> fields = new ArrayList<FormField>();
	private List<FormButtonComponent> buttons = new ArrayList<FormButtonComponent>();
	private String dataSource = null;
	private FormHandler handler = null;
	private AutoBean<?> inputObject = null;
	private Splittable dataMap = null;
	
	public FormPanelComponent() {
		Grid grid = new Grid(1,1);
		grid.setCellPadding(0);
		grid.setCellSpacing(0);
		setPanelWidget(grid);
	}
	
	public void addField(FormField field) {
		if (field != null) {
			fields.add(field);
		}
	}
	
	public ConsoleComponent getField(int row) {
		return (ConsoleComponent)(((Grid)getWidget()).getWidget(row, 0));
	}
	
	public void addButton(FormButtonComponent button) {
		if (button != null) {
			buttons.add(button);
		}
	}
	
	public List<FormField> getFields() {
		return fields;
	}
	
	public void setFormHandler(FormHandler handler) {
		this.handler = handler;
	}
	
	public FormHandler getFormHandler() {
		return handler;
	}
	
	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}
	
	public String getDataSource() {
		return dataSource;
	}
	
	public boolean isValid() {
		boolean valid = true;
		for (FormField field : fields) {
			if (!field.isValid()) {
				valid = false;
				break;
			}
		}
		return valid;
	}

	// ---------------------------------------------------------------------------------
	//			SUPER CLASS OVERRIDES BELOW
	// ---------------------------------------------------------------------------------
	
	@Override
	public Set<Sensor> getSensors() {
		return null;
	}

	@Override
	public Set<ConsoleComponent> getComponents() {
		return new HashSet<ConsoleComponent>(buttons);
	}
	
	@Override
	public String getClassName() {
		return CLASS_NAME;
	}
	
	@Override
	public void onRender(int width, int height, List<DataValuePair> data) {
		Grid grid = (Grid)getWidget();
		int rows = fields.size();
		grid.resizeRows(rows + 1);
		int rowHeight = (int)Math.round((double)height / (rows + 1));
		
		if (!isInitialised) {
			for (int i=0; i<rows; i++) {
				HTMLTable.CellFormatter formatter = grid.getCellFormatter();
				FormField field = fields.get(i);
				
				formatter.setHeight(i, 0, rowHeight + "px");
				formatter.setHorizontalAlignment(i, 0, HasHorizontalAlignment.ALIGN_LEFT);
				formatter.setVerticalAlignment(i, 0, HasVerticalAlignment.ALIGN_MIDDLE);
				
				grid.setWidget(i, 0, field);
			}
		
			// Force button size to be 80 x 35
			HorizontalPanel buttonPanel = new HorizontalPanel();
			buttonPanel.setWidth("100%");
			buttonPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			for (FormButtonComponent button : buttons) {
				switch (button.getType()) {
					case SUBMIT:
						button.addHandler(this, TapEvent.getType());
						button.setNavigate(null);
						button.setHasControlCommand(false);
						break;
				}
				buttonPanel.add((Widget)button);
				button.onAdd(80, 35);
			}
			grid.setWidget(rows, 0, buttonPanel);
		}
		
		// Get data source if it is defined
		if (dataSource != null && !dataSource.equals("")) {
			inputObject = DataBindingService.getInstance().getData(dataSource);
			if (inputObject != null) {
				dataMap = AutoBeanCodex.encode(inputObject);
			}
		}
		
		// Populate fields using binding data
		for (FormField field : fields) {	
			if (dataMap != null && field.getName() != null && !field.getName().equals("")) {
				try {
					Splittable fieldMap = dataMap.get(field.getName());
					field.setDefaultValue(fieldMap.asString());
				} catch (Exception e) {}
			}
			field.onAdd(width, rowHeight);
		}
	}
	
	@Override
	public void onUpdate(int width, int height) {
		for (FormField field : fields) {
			field.onUpdate(width, height);
		}
	}
	
	@Override
	public void onRemove() {
		for (FormField field : fields) {
			field.onRemove();
		}
	}
	
	@Override
	public void onTap(TapEvent event) {
		FormButtonComponent btn = (FormButtonComponent)event.getSource();
		switch (btn.getType()) {
			case SUBMIT:
				if (isValid()) {
					onSubmit();
				}
				break;
			case CANCEL:
				// This is handled by the button component superclass as a navigate action should be specified
				break;
			case CLEAR:
				break;
		}
	}
	
	private void onSubmit() {
		for (FormField field : fields) {
			String name = field.getName();
			if (dataMap != null && name != null) {
				dataMap.setReified(name, field.getValue());
			}
		}
		AutoBean<ControllerCredentials> bean = AutoBeanService.getInstance().fromJsonString(ControllerCredentials.class, dataMap);
		Map<String, Object> diffMap = AutoBeanUtils.diff(inputObject, bean);
		if (diffMap.size() > 0) {
			DataBindingService.getInstance().setData(dataSource, bean);
		}
		WebConsole.getConsoleUnit().restart();
	}
	
	// ---------------------------------------------------------------------------------
	//			BUILD METHOD BELOW HERE
	// ---------------------------------------------------------------------------------
	
	public static FormPanelComponent build(FormLayout layout) throws Exception {
		FormPanelComponent panel = new FormPanelComponent();
		if (layout == null) {
			return panel;
		}
		panel.setHeight(layout.getHeight());
		panel.setWidth(layout.getWidth());
		panel.setPosition(layout.getLeft(),layout.getTop());
		panel.setDataSource(layout.getDataSource());
		
		// Add Fields
		List<Field> fields = layout.getField();
		if (fields != null) {
			for (Field field : fields) {
				FormField fieldComp = new FormField();
				fieldComp.setLabel(field.getLabel());
				fieldComp.setInputType(EnumFormInputType.getInputType(field.getInputType()));
				fieldComp.setValidationString(field.getValidationString());
				fieldComp.setIsOptional(field.getOptional());
				fieldComp.setName(field.getName());
				panel.addField(fieldComp);
			}
		}
		
		// Add Buttons
		List<FormButton> buttons = layout.getButton();
		if (buttons != null) {
			for (FormButton button : buttons) {
				FormButtonComponent buttonComp;
				String name = button.getName();
				if (name != null && name.length() > 0) {
					buttonComp = new FormButtonComponent(EnumFormButtonType.getButtonType(button.getType()), name);
				} else {
					buttonComp = new FormButtonComponent(EnumFormButtonType.getButtonType(button.getType()));
				}
				buttonComp.setNavigate(button.getNavigate());
				panel.addButton(buttonComp);
			}
		}
		
		return panel;
	}
}
