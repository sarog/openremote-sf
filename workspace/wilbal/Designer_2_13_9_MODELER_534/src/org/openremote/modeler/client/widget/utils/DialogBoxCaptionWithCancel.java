package org.openremote.modeler.client.widget.utils;


import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox.Caption;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class DialogBoxCaptionWithCancel extends Composite implements Caption,
        HasClickHandlers {

    @UiField
    HTMLPanel mainPanel;
    @UiField
    HTML captionLabel;
    @UiField
    Button cancelButton;

    private HandlerManager handlerManager = null;

    private static final Binder binder = GWT.create(Binder.class);

    interface Binder extends UiBinder<Widget, DialogBoxCaptionWithCancel> {
    }

    public DialogBoxCaptionWithCancel() {
        initWidget(binder.createAndBindUi(this));

        mainPanel.setStyleName("Caption");
        
        cancelButton.setText("X");
        cancelButton.setStylePrimaryName("none");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.user.client.ui.Widget#onLoad()
     */
    @Override
    protected void onLoad() {
        super.onLoad();

        handlerManager = new HandlerManager(this);
    }

    @UiHandler("cancelButton")
    public void cancelButtonOnClick(ClickEvent event) {
        handlerManager.fireEvent(event);
    }

    @Override
    public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
        return handlerManager.addHandler(MouseDownEvent.getType(), handler);
    }

    @Override
    public HandlerRegistration addMouseUpHandler(MouseUpHandler handler) {
        return handlerManager.addHandler(MouseUpEvent.getType(), handler);
    }

    @Override
    public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
        return handlerManager.addHandler(MouseOutEvent.getType(), handler);
    }

    @Override
    public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
        return handlerManager.addHandler(MouseOverEvent.getType(), handler);
    }

    @Override
    public HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler) {
        return handlerManager.addHandler(MouseMoveEvent.getType(), handler);
    }

    @Override
    public HandlerRegistration addMouseWheelHandler(MouseWheelHandler handler) {
        return handlerManager.addHandler(MouseWheelEvent.getType(), handler);
    }

    @Override
    public String getHTML() {
        return "";
    }

    @Override
    public void setHTML(String html) {
    }

    @Override
    public String getText() {
        return this.captionLabel.getText();
    }

    @Override
    public void setText(String text) {
        this.captionLabel.setText(text);
    }

    @Override
    public void setHTML(SafeHtml html) {
    }

    @Override
    public HandlerRegistration addClickHandler(ClickHandler handler) {
        return handlerManager.addHandler(ClickEvent.getType(), handler);
    }
}
