package org.openremote.modeler.client.widget.utils;

import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class CustomDialogBox extends DialogBox implements ClickHandler{
    
    @UiField
    HorizontalPanel panel;
    private static DialogBoxCaptionWithCancel caption = new DialogBoxCaptionWithCancel();
    interface MyUiBinder extends UiBinder<Widget, CustomDialogBox> {
    }
    private static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
    
    public CustomDialogBox() {
        super(false, true, caption);
        setWidget(uiBinder.createAndBindUi(this));
        this.center();
    }
    
    @Override
    protected void onLoad() {
        super.onLoad();
        caption.addClickHandler(this);
        TextBox delayField = new TextBox();
        delayField.addFocusHandler(new FocusHandler() {
            
            @Override
            public void onFocus(FocusEvent event) {
                GWT.log("focus");
                
            }
        });
        panel.add(delayField);
        
    }
    public void setHeading(String heading) {
        caption.setText(heading);
    }

    @Override
    public void onClick(ClickEvent arg0) {
        this.hide(true);      
    }
    
}
