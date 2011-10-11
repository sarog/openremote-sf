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

import java.util.ArrayList;
import java.util.List;

import org.openremote.modeler.client.BrandInfo;
import org.openremote.modeler.client.CodeSetInfo;
import org.openremote.modeler.client.DeviceInfo;
import org.openremote.modeler.client.IRCommandInfo;
import org.openremote.modeler.client.proxy.DeviceBeanModelProxy;
import org.openremote.modeler.client.proxy.IrFileParserProxy;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.client.rpc.ConfigurationRPCService;
import org.openremote.modeler.client.rpc.ConfigurationRPCServiceAsync;
import org.openremote.modeler.client.rpc.IRFileParserRPCService;
import org.openremote.modeler.client.rpc.IRFileParserRPCServiceAsync;
import org.openremote.modeler.client.widget.CommonForm;
import org.openremote.modeler.domain.Device;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.DataField;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.ListLoader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelType;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Padding;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridView;
import com.extjs.gxt.ui.client.widget.grid.GridViewConfig;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.menu.Item;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * IR File Command Import Form.
 * 
 */
public class IRFileImportForm extends CommonForm {

	/** The configuration service. */
	private IRFileParserRPCServiceAsync iRFileParserService = (IRFileParserRPCServiceAsync) GWT
			.create(IRFileParserRPCService.class);

	/** The device. */
	protected Device device = null;

	/** The select container. */
	private LayoutContainer selectContainer = new LayoutContainer();

	/** The command container. */
	private LayoutContainer commandContainer = new LayoutContainer();

	/** The import button. */
	protected Button importButton;

	/** The code grid. */
	protected Grid<IRCommandInfo> codeGrid = null;

	private ColumnModel cm = null;
	
	protected ListStore<BrandInfo> brandInfos = null;
	protected ComboBox<BrandInfo> brandInfoList = null;
	
	protected ListStore<DeviceInfo> deviceInfos = null;
	protected ComboBox<DeviceInfo> deviceInfoList = null;
	
	protected ListStore<CodeSetInfo> codeSetInfos = null;
	protected ComboBox<CodeSetInfo> codeSetInfoList = null;
	
	ListStore<IRCommandInfo> listStore;
	
	protected Component wrapper;

	private ModelType codeType;



	/**
	 * Instantiates a new iR command import form.
	 * 
	 * @param wrapper
	 *            the wrapper
	 * @param deviceBeanModel
	 *            the device bean model
	 */
	public IRFileImportForm(final Component wrapper, BeanModel deviceBeanModel) {
		super();
		setHeight(500);
		this.wrapper = wrapper;
		setLayout(new RowLayout(Orientation.VERTICAL));
		HBoxLayout selectContainerLayout = new HBoxLayout();
		selectContainerLayout.setPadding(new Padding(5));
		selectContainerLayout.setHBoxLayoutAlign(HBoxLayoutAlign.TOP);

		selectContainer.setLayout(selectContainerLayout);
		selectContainer.setLayoutOnChange(true);
		add(selectContainer, new RowData(1, 35));

		commandContainer.setLayout(new CenterLayout());
		commandContainer.setLayoutOnChange(true);
		add(commandContainer, new RowData(1, 1));
		onSubmit(wrapper);
	}
	
	/**
	 * On submit.
	 * 
	 * @param wrapper
	 *            the wrapper
	 */
	protected void onSubmit(final Component wrapper) {
		addListener(Events.BeforeSubmit, new Listener<FormEvent>() {
			public void handleEvent(FormEvent be) {

			}
		});
	}

	@Override
	protected void addButtons() {
		importButton = new Button("Import");
		// importButton.setEnabled(false);
		importButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				submit();
			}
		});
		addButton(importButton);
	}

	public void showBrands() {
		IrFileParserProxy
				.loadBrands(new AsyncSuccessCallback<List<BrandInfo>>() {

					@Override
					public void onSuccess(final List<BrandInfo> brands) {
						// TODO now that we can retrieve brands from server,
						// add them into a drop-down list, where a selection
						// will trigger the next one
						// with the devices where a selection in it will trigger
						// display of a last drop-down list
						// or display a list of available codes.

						/*
						 * final RpcProxy<List<BeanModel>> loadBrandRPCProxy =
						 * new RpcProxy<List<BeanModel>>() {
						 * 
						 * @Override protected void load(Object o, final
						 * AsyncCallback<List<BeanModel>> listAsyncCallback) {
						 * IrFileParserProxy.loadBrands(new
						 * AsyncSuccessCallback<List<BrandInfo>>() {
						 * 
						 * @Override public void onSuccess(List<BrandInfo>
						 * result) {
						 * 
						 * 
						 * 
						 * } }); } };
						 * 
						 * ModelType brandType = new ModelType();
						 * brandType.setRoot("brands.brand"); DataField idField
						 * = new DataField("id"); idField.setType(Long.class);
						 * brandType.addField(idField); final String emptyText =
						 * "Please select Brand...";
						 * ListLoader<ListLoadResult<Item>> loader = new
						 * BaseListLoader
						 * <ListLoadResult<Item>>(loadBrandRPCProxy);
						 * 
						 * ListStore<ModelData> brandStore = new
						 * ListStore<ModelData>(loader); brandStore.add(result);
						 * ComboBox<ModelData> brandList = new
						 * ComboBox<ModelData>();
						 * brandList.setStore(brandStore);
						 * selectContainer.add(brandList);
						 */

						if (brandInfos == null) {
							brandInfos = new ListStore<BrandInfo>();
							brandInfoList = new ComboBox<BrandInfo>();
							brandInfoList
									.setEmptyText("Please select Brand...");
							brandInfoList.setDisplayField("brandName");
							brandInfoList.setWidth(150);
							brandInfoList.setStore(brandInfos);
							brandInfoList.setTriggerAction(TriggerAction.ALL);
							brandInfoList.setEditable(false);
							selectContainer.add(brandInfoList);
							brandInfos.add(brands);

							brandInfoList
									.addSelectionChangedListener(new SelectionChangedListener<BrandInfo>() {

										@Override
										public void selectionChanged(
												SelectionChangedEvent<BrandInfo> se) {
											showDevices(se.getSelectedItem());

										}

									});

						} else {
							codeSetInfos.removeAll();
							codeSetInfoList.clearSelections();
							codeSetInfoList.getStore().removeAll();
							deviceInfos.removeAll();
							deviceInfoList.clearSelections();
							deviceInfoList.getStore().removeAll();
							brandInfoList.clearSelections();
							brandInfos.removeAll();
							brandInfos.add(brands);
						}

					}

				});
	}

	private void showDevices(BrandInfo brandInfo) {
		IrFileParserProxy.loadModels(brandInfo,
				new AsyncSuccessCallback<List<DeviceInfo>>() {

					@Override
					public void onSuccess(List<DeviceInfo> devices) {
						if (deviceInfos == null) {
							deviceInfos = new ListStore<DeviceInfo>();

							deviceInfoList = new ComboBox<DeviceInfo>();
							deviceInfoList
									.setEmptyText("Please select Device...");
							deviceInfoList.setDisplayField("modelName");
							deviceInfoList.setWidth(150);
							deviceInfoList.setStore(deviceInfos);
							deviceInfoList.setTriggerAction(TriggerAction.ALL);
							deviceInfoList.setEditable(false);
							selectContainer.add(deviceInfoList);
							deviceInfos.add(devices);

							deviceInfoList
									.addSelectionChangedListener(new SelectionChangedListener<DeviceInfo>() {

										@Override
										public void selectionChanged(
												SelectionChangedEvent<DeviceInfo> se) {
											showCodeSets(se.getSelectedItem());

										}

										

									});
						} else {
							codeSetInfos.removeAll();
							codeSetInfoList.clearSelections();
							codeSetInfoList.getStore().removeAll();
							deviceInfos.removeAll();
							deviceInfoList.clearSelections();
							deviceInfoList.getStore().removeAll();
							deviceInfos.add(devices);
							
							
						}

					}
				});

	}
	
	private void showCodeSets(
			DeviceInfo device) {
		IrFileParserProxy.loadCodeSets(device, new AsyncSuccessCallback<List<CodeSetInfo>>() {

			@Override
			public void onSuccess(List<CodeSetInfo> codeSets) {
				if (codeSetInfos==null){
					codeSetInfos = new ListStore<CodeSetInfo>();
					codeSetInfoList = new ComboBox<CodeSetInfo>();
					
					deviceInfoList
					.setEmptyText("Please select CodeSet...");
//					Window.alert(String.valueOf(codeSets.size()));
					codeSetInfoList.setDisplayField("category");
					codeSetInfoList.setWidth(150);
					codeSetInfoList.setStore(codeSetInfos);
					codeSetInfoList.setTriggerAction(TriggerAction.ALL);
					codeSetInfoList.setEditable(false);
			selectContainer.add(codeSetInfoList);
			codeSetInfos.add(codeSets);

			codeSetInfoList.addSelectionChangedListener(new SelectionChangedListener<CodeSetInfo>() {

						@Override
						public void selectionChanged(
								SelectionChangedEvent<CodeSetInfo> se) {
							//Window.alert(se.getSelectedItem().getCategory()+" "+se.getSelectedItem().getDescription()+" "+se.getSelectedItem().getCategory().toString());
							showGrid(se.getSelectedItem());
						}

						
					});
					
				}else{
					codeSetInfos.removeAll();
					codeSetInfoList.clearSelections();
					codeSetInfoList.getStore().removeAll();
					codeSetInfos.add(codeSets);
				}
				
			}
		});
		
	}
	private void showGrid(CodeSetInfo selectedItem) {
		
		IrFileParserProxy.loadIRCommands(selectedItem, new AsyncSuccessCallback<List<IRCommandInfo>>() {

			@Override
			public void onSuccess(List<IRCommandInfo> iRCommands) {
				if (importButton != null) {
			         importButton.setEnabled(true);
			      }
				if (listStore==null){
				 listStore = new ListStore<IRCommandInfo>();}else{
					 listStore.removeAll();
				 }
				for (IRCommandInfo irCommandInfo : iRCommands) {
					listStore.add(irCommandInfo);
				}
			      if (cm == null) {
			          List<ColumnConfig> codeGridColumns = new ArrayList<ColumnConfig>();
			          codeGridColumns.add(new ColumnConfig("name", "Name", 120));
			          codeGridColumns.add(new ColumnConfig("code", "Code", 250));
			          codeGridColumns.add(new ColumnConfig("originalCode", "Original Code", 250));
			          codeGridColumns.add(new ColumnConfig("comment", "Comment", 250));
			          cm = new ColumnModel(codeGridColumns);
			       }
			      if (codeGrid==null){
			      codeGrid = new Grid<IRCommandInfo>(listStore, cm);}
			      GridView gv = new GridView();
			      codeGrid.setView(gv);
			      gv.setViewConfig(new GridViewConfig(){
			    	 @Override
			    	public String getRowStyle(ModelData model, int rowIndex,
			    			ListStore<ModelData> ds) {
			    		 	if (model!=null){
			    		if (model.get("code")==null){
			    			return "background-color:red;";
			    		}else{
			    			return "";
			    		}}else{
			    			return "";
			    		}
			    	} 
			      });
			      codeGrid.setLoadMask(true);
			      codeGrid.setHeight(400);
			      commandContainer.add(codeGrid);
			}
		});
		
		
	}
}
