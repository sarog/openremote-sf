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
package org.openremote.modeler.client.widget.uidesigner;

import org.openremote.modeler.client.icon.IconResources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;

public class UIDesignerToolbarImpl extends Composite implements UIDesignerToolbar {

  private static UIDesignerToolbarUiBinder uiBinder = GWT.create(UIDesignerToolbarUiBinder.class);

  @UiTemplate("UIDesignerToolbar.ui.xml")
  interface UIDesignerToolbarUiBinder extends UiBinder<Widget, UIDesignerToolbarImpl> {
  }

  public UIDesignerToolbarImpl() {
    initWidget(uiBinder.createAndBindUi(this));
  }

  private Presenter presenter;
  
  @UiField(provided=true)
  final IconResources resources = IconResources.INSTANCE;
  
  @UiField
  TextButton horizontalLeftAlignButton;
  @UiField
  TextButton horizontalCenterAlignButton;
  @UiField
  TextButton horizontalRightAlignButton;
  
  @UiField
  TextButton verticalTopAlignButton;
  @UiField
  TextButton verticalCenterAlignButton;
  @UiField
  TextButton verticalBottomAlignButton;
  
  @UiField
  TextButton sameSizeButton;
  
  @UiField
  TextButton horizontalSpreadButton;
  @UiField
  TextButton verticalSpreadButton;
  
  @UiField
  TextButton horizontalCenterButton;
  @UiField
  TextButton verticalCenterButton;

  public UIDesignerToolbarImpl(String firstName) {
    initWidget(uiBinder.createAndBindUi(this));
  }

  @UiHandler("horizontalLeftAlignButton")
  void onLeftAlignSelected(SelectEvent e) {
    if (presenter != null) {
      presenter.onHorizontalLeftAlignButtonClicked();
    }
  }
  
  @UiHandler("horizontalCenterAlignButton")
  void onMiddleAlignSelected(SelectEvent e) {
    if (presenter != null) {
      presenter.onHorizontalCenterAlignButtonClicked();
    }
  }

  @UiHandler("horizontalRightAlignButton")
  void onRightAlignSelected(SelectEvent e) {
    if (presenter != null) {
      presenter.onHorizontalRightAlignButtonClicked();
    }
  }

  @UiHandler("verticalTopAlignButton")
  void onVerticalTopAlignSelected(SelectEvent e) {
    if (presenter != null) {
      presenter.onVerticalTopAlignButtonClicked();
    }
  }
  
  @UiHandler("verticalCenterAlignButton")
  void onVerticalCenterAlignSelected(SelectEvent e) {
    if (presenter != null) {
      presenter.onVerticalCenterAlignButtonClicked();
    }
  }
  
  @UiHandler("verticalBottomAlignButton")
  void onVerticalBottomAlignSelected(SelectEvent e) {
    if (presenter != null) {
      presenter.onVerticalBottomAlignButtonClicked();
    }
  }
  
  @UiHandler("sameSizeButton")
  void onSameSizeButtonSelected(SelectEvent e) {
    if (presenter != null) {
      presenter.onSameSizeButtonClicked();
    }
  }

  @UiHandler("horizontalSpreadButton")
  void onHorizontalSpreadButtonSelected(SelectEvent e) {
    if (presenter != null) {
      presenter.onHorizontalSpreadButtonClicked();
    }
  }
  
  @UiHandler("verticalSpreadButton")
  void onVerticalSpreadButtonSelected(SelectEvent e) {
    if (presenter != null) {
      presenter.onVerticalSpreadButtonClicked();
    }
  }

  @UiHandler("horizontalCenterButton")
  void onHorizontalCenterButtonSelected(SelectEvent e) {
    if (presenter != null) {
      presenter.onHorizontalCenterButtonClicked();
    }
  }
  
  @UiHandler("verticalCenterButton")
  void onVerticalCenterButtonSelected(SelectEvent e) {
    if (presenter != null) {
      presenter.onVerticalCenterButtonClicked();
    }
  }

  public void setPresenter(Presenter presenter) {
    this.presenter = presenter;
  }

  public TextButton getHorizontalLeftAlignButton() {
    return horizontalLeftAlignButton;
  }

  public TextButton getHorizontalCenterAlignButton() {
    return horizontalCenterAlignButton;
  }

  public TextButton getHorizontalRightAlignButton() {
    return horizontalRightAlignButton;
  }

  public TextButton getVerticalTopAlignButton() {
    return verticalTopAlignButton;
  }

  public TextButton getVerticalCenterAlignButton() {
    return verticalCenterAlignButton;
  }

  public TextButton getVerticalBottomAlignButton() {
    return verticalBottomAlignButton;
  }

  public TextButton getSameSizeButton() {
    return sameSizeButton;
  }

  public TextButton getHorizontalSpreadButton() {
    return horizontalSpreadButton;
  }

  public TextButton getVerticalSpreadButton() {
    return verticalSpreadButton;
  }

  public TextButton getHorizontalCenterButton() {
    return horizontalCenterButton;
  }

  public TextButton getVerticalCenterButton() {
    return verticalCenterButton;
  }

}
