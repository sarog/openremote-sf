package org.openremote.modeler.client.widget.buildingmodeler;

import org.openremote.modeler.client.widget.CommonForm;
import org.openremote.modeler.client.widget.WizardWindow;

public class ISYImportWizard extends WizardWindow {


	   /** The Step index DEVICE_INFO_STEP. */
	   public static final int DEVICE_INFO_STEP = 0;
	   
	   
	   /**
	    * Instantiates a new device wizard window.
	    * 
	    * @param deviceBeanModel
	    *           the device bean model
	    */
	   public ISYImportWizard() {
	      super(null);
	      setHeading("Add ISY Insteon Devices");
	      show();
	   }

	   /**
	    * {@inheritDoc}
	    */
	   @Override
	   protected void initForms() {
	      forms = new CommonForm[]{
	            new ISYInfoWizardForm(this)
	       };
	   }

//	   /**
//	    * {@inheritDoc}
//	    */
//	   @Override
//	   protected void postProcess(int step, FormPanel currentForm) {
//	      switch (step) {
//	      case DEVICE_INFO_STEP:
//	         break;
//	      default:
//	         break;
//	      }
//	   }


	}