package org.openremote.modeler.client.widget.buildingmodeler;

import org.openremote.modeler.client.lutron.importmodel.LutronImportResultOverlay;
import org.openremote.modeler.client.lutron.importmodel.ProjectOverlay;

import com.extjs.gxt.ui.client.widget.Info;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class LutronImportWizard extends DialogBox {

  private static LutronImportWizardUiBinder uiBinder = GWT.create(LutronImportWizardUiBinder.class);

  interface LutronImportWizardUiBinder extends UiBinder<Widget, LutronImportWizard> {
  }

  public LutronImportWizard() {
    setWidget(uiBinder.createAndBindUi(this));
    setSize("500px", "200px");
    
    errorMessageLabel.setVisible(false);
    
    uploadForm.setEncoding(FormPanel.ENCODING_MULTIPART);
    uploadForm.setMethod(FormPanel.METHOD_POST);
    uploadForm.setAction(GWT.getModuleBaseURL() + "fileUploadController.htm?method=importLutron");

    uploadForm.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
    
      @Override
      public void onSubmitComplete(SubmitCompleteEvent event) {
        
        LutronImportResultOverlay importResult = LutronImportResultOverlay.fromJSONString(event.getResults());
        if (importResult.getErrorMessage() != null) {
          reportError(importResult.getErrorMessage());
          return;
        }
        
        ProjectOverlay projectOverlay = importResult.getProject();
        if (projectOverlay.getAreas() == null) {
          reportError("File does not contain any information");
          return;
        }
        
        hide();
       }
    });
  }
  
  private void reportError(String errorMessage) {
    uploadForm.reset();
    errorMessageLabel.setText(errorMessage);
    errorMessageLabel.setVisible(true);
  }


  @UiField
  Label errorMessageLabel;
  
  @UiField
  Button submitButton;
  
  @UiField
  Button cancelButton;
  
  @UiField
  FormPanel uploadForm;
  

  @UiField
  FileUpload uploadField;
  
  @UiHandler("cancelButton")
  void handleClick(ClickEvent e) {
    hide();
  }

  
}
