package org.openremote.web.console.widget.panel.form;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.openremote.web.console.client.WebConsole;
import org.openremote.web.console.controller.ControllerCredentials;
import org.openremote.web.console.event.tap.TapEvent;
import org.openremote.web.console.event.tap.TapHandler;
import org.openremote.web.console.panel.entity.DataValuePair;
import org.openremote.web.console.panel.entity.GridLayout;
import org.openremote.web.console.service.AutoBeanService;
import org.openremote.web.console.service.DataBindingService;
import org.openremote.web.console.service.LocalDataServiceImpl;
import org.openremote.web.console.widget.ConsoleComponent;
import org.openremote.web.console.widget.panel.PanelComponent;
import org.openremote.web.console.widget.panel.form.FormButton.EnumFormButtonType;
import org.openremote.web.console.widget.Sensor;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;
import com.google.web.bindery.autobean.shared.Splittable;

public class FormPanelComponent extends PanelComponent implements TapHandler {
	private static final String CLASS_NAME = "formPanelComponent";
	private List<FormField> fields = new ArrayList<FormField>();
	private List<FormButton> buttons = new ArrayList<FormButton>();
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
	
	public void addButton(FormButton button) {
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
			for (FormButton button : buttons) {
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
			dataMap = AutoBeanCodex.encode(inputObject);
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
	public void onRemove() {
		for (FormField field : fields) {
			field.onRemove();
		}
	}
	
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
	public void onTap(TapEvent event) {
		FormButton btn = (FormButton)event.getSource();
		switch (btn.getType()) {
			case SUBMIT:
				if (isValid()) {
					onSubmit();
				}
				break;
			case CANCEL:
				break;
			case CLEAR:
				
				break;
		}
	}
	
	private void onSubmit() {
		for (FormField field : fields) {
			String name = field.getName();
			if (name != null) {
				dataMap.setReified(name, field.getValue());
			}
		}
		AutoBean<ControllerCredentials> bean = AutoBeanService.getInstance().fromJsonString(ControllerCredentials.class, dataMap);
		Map<String, Object> diffMap = AutoBeanUtils.diff(inputObject, bean);
		if (diffMap.size() > 0) {
			DataBindingService.getInstance().setData(dataSource, bean);
		}
		WebConsole.getConsoleUnit().restartController();
	}
	
	// ---------------------------------------------------------------------------------
	//			BUILD METHOD BELOW HERE
	// ---------------------------------------------------------------------------------
	
	public static FormPanelComponent build(GridLayout layout) throws Exception {
		FormPanelComponent panel = new FormPanelComponent();
		if (layout == null) {
			return panel;
		}
		panel.setHeight(layout.getHeight());
		panel.setWidth(layout.getWidth());
		panel.setPosition(layout.getLeft(),layout.getTop());
		
		// Add Fields
		
		return panel;
	}
}
