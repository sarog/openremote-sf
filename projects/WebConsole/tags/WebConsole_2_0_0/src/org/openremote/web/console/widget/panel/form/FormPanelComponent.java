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
package org.openremote.web.console.widget.panel.form;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openremote.web.console.panel.entity.DataValuePair;
import org.openremote.web.console.panel.entity.DataValuePairContainer;
import org.openremote.web.console.panel.entity.Field;
import org.openremote.web.console.panel.entity.FormButton;
import org.openremote.web.console.panel.entity.FormLayout;
import org.openremote.web.console.service.AutoBeanService;
import org.openremote.web.console.service.DataBindingService;
import org.openremote.web.console.widget.ConsoleComponent;
import org.openremote.web.console.widget.Sensor;
import org.openremote.web.console.widget.panel.PanelComponent;
import org.openremote.web.console.widget.panel.form.FormButtonComponent.EnumFormButtonAction;
import org.openremote.web.console.widget.panel.form.FormButtonComponent.EnumFormButtonType;
import org.openremote.web.console.widget.panel.form.FormField.EnumFormInputType;

import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.Splittable;
/**
 * 
 *  
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 */
public class FormPanelComponent extends PanelComponent {
	private static final String CLASS_NAME = "formPanelComponent";
	private List<FormField> fields = new ArrayList<FormField>();
	private List<FormButtonComponent> buttons = new ArrayList<FormButtonComponent>();
	private String dataSource = null;
	private String itemBindingObject = null;
	private FormHandler handler = null;
	private AutoBean<?> inputObject = null;
	private Splittable dataMap = null;
	private Splittable objectMap = null;
	private Integer objectIndex = null;
	
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
	
	public void setItemBindingObject(String itemBindingObject) {
		this.itemBindingObject = itemBindingObject;
	}	
	
	public String getItemBindingObject() {
		return itemBindingObject;
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
	public void onRender(int width, int height, List<DataValuePairContainer> data) {
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
				
			if (dataMap != null) {
				if (itemBindingObject != null && !itemBindingObject.equals("")) {
					if (!dataMap.isUndefined(itemBindingObject)) {
						objectMap = dataMap.get(itemBindingObject);
					}
				} else {
					objectMap = dataMap;
				}
				
				// If object map is an indexed object then we need to know which index to use for binding
				// this has to be specified by a DataValuePair called bindingItem
				if (objectMap != null && objectMap.isIndexed() && data != null) {
						// Look for BindingItem dvp
						for (DataValuePairContainer dvpContainer : data) {
							DataValuePair dvp = dvpContainer.getDataValuePair();
							if (dvp.getName().equalsIgnoreCase("bindingitem")) {
								String bindingItem = dvp.getValue();
								String[] bindingItemKvp = bindingItem.split("=");
								String fieldName = null;
								String fieldValue = null;
								if (bindingItemKvp.length == 2) {
									fieldName = bindingItemKvp[0];
									fieldValue = bindingItemKvp[1];
									for (int i=0; i<objectMap.size(); i++) {
										Splittable itemMap = objectMap.get(i);
										String dataMapEntry = itemMap.get(fieldName).asString();
										if(dataMapEntry != null && dataMapEntry.equalsIgnoreCase(fieldValue)) {
											objectIndex = i;
											break;
										}
									}
								}
								break;
							}
						}
				}
			}
		}

		
		// Populate fields using binding data
		Splittable itemMap = objectMap;
		if (objectIndex != null) {
			itemMap = objectMap.get(objectIndex);
		}
		for (FormField field : fields) {	
			if (itemMap != null && field.getName() != null && !field.getName().equals("")) {
				try {
					Splittable fieldMap = itemMap.get(field.getName());
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
	
	/*
	 * This is called by the SUBMIT button before the Navigate Event is fired
	 * button is passed in to allow removal of the navigate event if desired
	 */
	public void onSubmit(FormButtonComponent submitBtn) {
		Splittable submitMap = null;
		EnumFormButtonAction action = submitBtn.getAction();
		
		// If no object map then just return
		if (objectMap == null) {
			return;
		}
		
		if (objectMap.isIndexed() && objectIndex != null) {
			submitMap = objectMap.get(objectIndex).deepCopy();
		} else {
			switch(action) {
			case ADD:
				if (itemBindingObject != null) {
					AutoBean<?> bean = AutoBeanService.getInstance().getFactory().create(DataBindingService.getInstance().getClass(itemBindingObject));
					submitMap = AutoBeanCodex.encode(bean);
				}
				break;
			case UPDATE:
				submitMap = objectMap.deepCopy();
				break;
			}
		}
		
		// Convert form fields into a splittable object
		for (FormField field : fields) {
			String name = field.getName();
			if (name != null) {
				submitMap.setReified(name, field.getValue());
			}
		}
		
		boolean updateSource = false;
		
		// Perform form action using collected data
		switch (action) {
			case DELETE:
				if (objectMap.isIndexed() && objectIndex != null) {
				    int newSize = objectMap.size() - 1;
				    for (int i = objectIndex; i < newSize; i++) {
				      objectMap.get(i + 1).assign(objectMap, i);
				    }
				    objectMap.setSize(newSize);
				} else {
					inputObject = null;
				}
				if (itemBindingObject != null && !itemBindingObject.equals("")) {
					objectMap.assign(dataMap, itemBindingObject);
				} else {
					dataMap = objectMap;
				}
				updateSource = true;
				break;
			case ADD:
			case UPDATE:
				// Convert submit map to an autobean to commit reified values
				AutoBean<?> bindingBean = null;
				if (itemBindingObject != null && !itemBindingObject.equals("")) {
					bindingBean = AutoBeanService.getInstance().fromJsonString(DataBindingService.getInstance().getClass(itemBindingObject), submitMap);
				} else {
					bindingBean = AutoBeanService.getInstance().fromJsonString(DataBindingService.getInstance().getClass(dataSource), submitMap);
				}
				if (action == EnumFormButtonAction.ADD && bindingBean != null) {
					// Can only add to an array
					if (objectMap.isIndexed()) {
						int pos = objectMap.size();
						//objectMap.setSize(pos+1);
						AutoBeanCodex.encode(bindingBean).assign(objectMap, pos);
					}					
				}
				if (action == EnumFormButtonAction.UPDATE && bindingBean != null) {
					// If objectIndex exists then we are editing an array entry so update the corresponding entry
					if (objectMap.isIndexed() && objectIndex != null) {
						AutoBeanCodex.encode(bindingBean).assign(objectMap, objectIndex);
					} else {
						objectMap = AutoBeanCodex.encode(bindingBean);
					}
				}
				if (itemBindingObject != null && !itemBindingObject.equals("")) {
					objectMap.assign(dataMap, itemBindingObject);
				} else {
					dataMap = objectMap;
				}
				updateSource = true;
				break;
		}
		
		// Update binding object source
		if (dataMap != null && updateSource) {
			AutoBean<?> bean = AutoBeanService.getInstance().fromJsonString(DataBindingService.getInstance().getClass(dataSource), dataMap);
			if (bean != null) {
				DataBindingService.getInstance().setData(dataSource, bean);
			}
		}
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
		panel.setPosition(layout.getLeft(),layout.getTop(), layout.getRight(), layout.getBottom());
		panel.setDataSource(layout.getDataSource());
		panel.setItemBindingObject(layout.getItemBindingObject());
		
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
				fieldComp.setId(field.getId());
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
					buttonComp = new FormButtonComponent(panel, EnumFormButtonType.getButtonType(button.getType()), name);
				} else {
					buttonComp = new FormButtonComponent(panel, EnumFormButtonType.getButtonType(button.getType()));
				}
				buttonComp.setNavigate(button.getNavigate());
				if (button.getHasControlCommand() != null && button.getId() != null) {
					buttonComp.setHasControlCommand(button.getHasControlCommand());
					buttonComp.setId(button.getId());
				}
				buttonComp.setAction(EnumFormButtonAction.enumValueOf(button.getAction()));
				panel.addButton(buttonComp);
			}
		}
		
		return panel;
	}
}
