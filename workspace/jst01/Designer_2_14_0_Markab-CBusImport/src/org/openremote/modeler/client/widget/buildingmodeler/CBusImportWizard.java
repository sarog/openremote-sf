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
package org.openremote.modeler.client.widget.buildingmodeler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import net.customware.gwt.dispatch.client.DispatchAsync;

import org.openremote.modeler.client.ModelerGinjector;
import org.openremote.modeler.client.cbus.importmodel.ApplicationOverlay;
import org.openremote.modeler.client.cbus.importmodel.CBusImportResultOverlay;
import org.openremote.modeler.client.cbus.importmodel.GroupOverlay;
import org.openremote.modeler.client.cbus.importmodel.NetworkOverlay;
import org.openremote.modeler.client.cbus.importmodel.ProjectOverlay;
import org.openremote.modeler.client.event.DeviceUpdatedEvent;

import org.openremote.modeler.client.utils.ArrayOverlay;
import org.openremote.modeler.client.utils.CheckboxCellHeader;
import org.openremote.modeler.client.utils.CheckboxCellHeader.ChangeValue;

import org.openremote.modeler.shared.cbus.GroupAddressImportConfig;
import org.openremote.modeler.shared.cbus.ImportCBusConfigAction;
import org.openremote.modeler.shared.cbus.ImportCBusConfigResult;
import org.openremote.modeler.shared.cbus.ImportConfig;
import org.openremote.modeler.shared.dto.DeviceDTO;
import org.springframework.web.util.HtmlUtils;


import com.extjs.gxt.ui.client.widget.Info;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
/**
 * Imports CBus project files
 *  
 * @author Jamie Turner
 */
public class CBusImportWizard extends DialogBox
{
    
    private static CBusImportWizardUiBinder uiBinder = GWT.create(CBusImportWizardUiBinder.class);

    interface CBusImportWizardUiBinder extends UiBinder<Widget, CBusImportWizard> 
    {
    }

    private EventBus eventBus;

    private DeviceDTO deviceDTO;

    private final MultiSelectionModel<GroupAddressImportConfig> selectionModel = new MultiSelectionModel<GroupAddressImportConfig>();

    final String NoScene = null;
    final String NoLevel = null;
    final String NoKey = null;

    @UiFactory
    DialogBox itself() 
    {
	return this;
    }

    public CBusImportWizard(final DeviceDTO deviceDTO, final EventBus eventBus) 
    {
	this.eventBus = eventBus;
	this.deviceDTO = deviceDTO;

	uiBinder.createAndBindUi(this);
	importButton.setEnabled(false);
	mainLayout.setSize("70em", "30em");
	center();

	final CheckboxCellHeader selectionHeader = new CheckboxCellHeader(new CheckboxCell());
	selectionHeader.setChangeValue(new ChangeValue() 
	{      
	    @Override
	    public void changedValue(int columnIndex, Boolean value) 
	    {
		if (value) 
		{
		    for (GroupAddressImportConfig oic : table.getVisibleItems()) 
		    {
			selectionModel.setSelected(oic, true);
		    }
		} else 
		{
		    selectionModel.clear();          
		}
	    }
	});

	TextColumn<GroupAddressImportConfig> networkNameColumn = new TextColumn<GroupAddressImportConfig>() 
	{
	    @Override
	    public String getValue(GroupAddressImportConfig outputConfig) 
	    {
		return outputConfig.getNetworkName();
	    }
	};
	
	TextColumn<GroupAddressImportConfig> applicationNameColumn = new TextColumn<GroupAddressImportConfig>() 
	{
	    @Override
	    public String getValue(GroupAddressImportConfig outputConfig) 
	    {
		return outputConfig.getApplicationName();
	    }
	};
	
	TextColumn<GroupAddressImportConfig> groupNameColumn = new TextColumn<GroupAddressImportConfig>() 
	{
	    @Override
	    public String getValue(GroupAddressImportConfig outputConfig) 
	    {
		return outputConfig.getGroupAddressName();
	    }
	};
	
	TextColumn<GroupAddressImportConfig> groupAddressColumn = new TextColumn<GroupAddressImportConfig>() 
	{
	    @Override
	    public String getValue(GroupAddressImportConfig outputConfig) 
	    {
		return outputConfig.getGroupAddress();
	    }
	};
	
	Column<GroupAddressImportConfig, Boolean> switchColumn = new Column<GroupAddressImportConfig, Boolean>(new CheckboxCell(false, false)) 
		{
	    @Override
	    public Boolean getValue(GroupAddressImportConfig outputConfig)
	    {
		return outputConfig.isSwitchCompatible();
	    }
		};
		
	Column<GroupAddressImportConfig, Boolean> dimmableColumn = new Column<GroupAddressImportConfig, Boolean>(new CheckboxCell(false, false)) 
	{
	    @Override
	    public Boolean getValue(GroupAddressImportConfig outputConfig)
	    {
		return outputConfig.isDimmable();
	    }
	};

	selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() 
	{      
	    @Override
	    public void onSelectionChange(SelectionChangeEvent event) 
	    {
		// Have import button only enabled if user has selected items to import
		importButton.setEnabled(!selectionModel.getSelectedSet().isEmpty());

		// And manage the select all/deselect all header based on individual selection
		if (selectionModel.getSelectedSet().isEmpty()) 
		{
		    selectionHeader.setValue(false);
		}
		if (selectionModel.getSelectedSet().size() == table.getVisibleItemCount()) 
		{
		    selectionHeader.setValue(true);
		}
	    }
	});

	table.setSelectionModel(selectionModel, DefaultSelectionEventManager.<GroupAddressImportConfig> createCheckboxManager());

	// Add the columns.
	Column<GroupAddressImportConfig, Boolean> checkColumn = new Column<GroupAddressImportConfig, Boolean>(new CheckboxCell(false, false)) 
	{
	    @Override
	    public Boolean getValue(GroupAddressImportConfig object) 
	    {
		return selectionModel.isSelected(object);
	    }
	};
	
	table.addColumn(checkColumn, selectionHeader);
	table.addColumn(networkNameColumn, "Network");
	table.addColumn(applicationNameColumn, "Application");
	table.addColumn(groupNameColumn, "Group Name");
	table.addColumn(groupAddressColumn, "Group Address");
	table.addColumn(switchColumn, "Switch?");
	table.addColumn(dimmableColumn, "Slider?");
	table.setRowCount(0); // No rows for now, otherwise loading indicator is displayed

	errorMessageLabel.setText("");

	uploadForm.setEncoding(FormPanel.ENCODING_MULTIPART);
	uploadForm.setMethod(FormPanel.METHOD_POST);
	uploadForm.setAction(GWT.getModuleBaseURL() + "fileUploadController.htm?method=importCBus");

	uploadForm.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() 
	{

	    @Override
	    public void onSubmitComplete(SubmitCompleteEvent event) 
	    {
		table.setRowCount(0); // No rows for now, otherwise loading indicator is displayed

		try
		{
		    CBusImportResultOverlay importResult = CBusImportResultOverlay.fromJSONString(event.getResults());

		    if (importResult.getErrorMessage() != null) 
		    {		    
			reportError(importResult.getErrorMessage());
			return;
		    }

		ProjectOverlay projectOverlay = importResult.getProject();
		if (projectOverlay.getNetworks() == null) 
		{
		    reportError("File does not contain any information");
		    return;
		}
		
		List<GroupAddressImportConfig> outputs = new ArrayList<GroupAddressImportConfig>();
		ArrayOverlay<NetworkOverlay> networks = projectOverlay.getNetworks();
		for (int i = 0; i < networks.length(); i++) 
		{
		    NetworkOverlay networkOverlay = networks.get(i);
		    
		    if (networkOverlay.getApplications() != null) 
		    {
			for (int j = 0; j < networkOverlay.getApplications().length(); j++) 
			{
			    ApplicationOverlay applicationOverlay = networkOverlay.getApplications().get(j);
			    if (applicationOverlay.getGroupAddresses() != null) 
			    {
				for (int k = 0; k < applicationOverlay.getGroupAddresses().length(); k++) 
				{
				    GroupOverlay groupOverlay = applicationOverlay.getGroupAddresses().get(k);
				    outputs.add(new GroupAddressImportConfig(decode(networkOverlay.getName()), decode(applicationOverlay.getName()), decode(groupOverlay.getName()), decode(groupOverlay.getAddress()),
					    applicationOverlay.getName().equals("Lighting"), applicationOverlay.getName().equals("Lighting")));
				}
			    }
			}
		    }
		}        
		
		table.setRowData(outputs);
		} catch (Exception ex)
		{
		    Info.display("Error", ex.toString());
		    reportError(ex.toString());
		}
	    }
	    
	});
	
    }

    private void reportError(String errorMessage) 
    {
	uploadForm.reset();
	errorMessageLabel.setText(errorMessage);
    }

    @UiField
    CellTable<GroupAddressImportConfig> table;

    @UiField
    DockLayoutPanel mainLayout;

    @UiField
    Label errorMessageLabel;

    @UiField
    Button loadButton;

    @UiField
    Button cancelButton;

    @UiField
    Button importButton;

    @UiField
    FormPanel uploadForm;


    @UiField
    FileUpload uploadField;

    @UiHandler("loadButton")
    void handleSubmit(ClickEvent e) 
    {
	// TODO: this is not really working because GUI is not updated while file uploads, only afterwards
	selectionModel.clear(); // Must clear selection, otherwise keeps previous selection
	table.setVisibleRangeAndClearData(table.getVisibleRange(), false);
	errorMessageLabel.setText("");
    }

    @UiHandler("cancelButton")
    void handleClick(ClickEvent e) 
    {
	hide();
    }

    @UiHandler("importButton")
    void handleImportClick(ClickEvent e) 
    {
	ModelerGinjector injector = GWT.create(ModelerGinjector.class);
	DispatchAsync dispatcher = injector.getDispatchAsync();

	ImportConfig importConfig = new ImportConfig();
	importConfig.setAddresses(new HashSet<GroupAddressImportConfig>(selectionModel.getSelectedSet()));

	ImportCBusConfigAction action = new ImportCBusConfigAction(importConfig);
	action.setDevice(this.deviceDTO);

	dispatcher.execute(action, new AsyncCallback<ImportCBusConfigResult>() {

	    @Override
	    public void onFailure(Throwable caught) 
	    {
		reportError(caught.getMessage());
	    }

	    @Override
	    public void onSuccess(ImportCBusConfigResult result) {
		eventBus.fireEvent(new DeviceUpdatedEvent(CBusImportWizard.this.deviceDTO));
		hide();
	    }

	});
    }
    
    /**
     * Decodes any encoded HTML in the JSON string
     * @param encodedData
     * @return
     */
    private String decode(String encodedData)
    {
	return new HTML(encodedData).getText();
	
    }
}

