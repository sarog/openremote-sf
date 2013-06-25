/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2013, OpenRemote Inc.
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

package org.openremote.modeler.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;

import junit.framework.Assert;

import org.openremote.modeler.client.utils.IDUtil;
import org.openremote.modeler.domain.component.ColorPicker;
import org.openremote.modeler.domain.component.Gesture;
import org.openremote.modeler.domain.component.Gesture.GestureType;
import org.openremote.modeler.domain.component.ImageSource;
import org.openremote.modeler.domain.component.UIButton;
import org.openremote.modeler.domain.component.UIComponent;
import org.openremote.modeler.domain.component.UIGrid;
import org.openremote.modeler.domain.component.UIImage;
import org.openremote.modeler.domain.component.UILabel;
import org.openremote.modeler.domain.component.UISlider;
import org.openremote.modeler.domain.component.UISwitch;
import org.openremote.modeler.domain.component.UIWebView;
import org.testng.annotations.Test;

/**
 * @author <a href = "mailto:eric@openremote.org">Eric Bariaux</a>
 */
public class PanelTest {

  @Test void testNullOperation() {
    List<Panel> panels = new ArrayList<Panel>();
    panels.add(buildPanelWithAbsoluteComponents());
    Panel.walkAllUIComponents(panels, null); 
  }
  
  @Test
  public void testWalkNoPanels() {
    List<Panel> panels = new ArrayList<Panel>();
    CountingOperation op = new CountingOperation();
    Panel.walkAllUIComponents(panels, op);
    Assert.assertEquals("No operation should have been called", 0, op.getTotal());
  }
  
  @Test
  public void testWalkOnePanelWithAbsoluteComponents() {
    List<Panel> panels = new ArrayList<Panel>();
    panels.add(buildPanelWithAbsoluteComponents());
    CountingOperation op = new CountingOperation();
    Panel.walkAllUIComponents(panels, op);
    Assert.assertEquals("Panels contains 7 components", 7, op.getTotal());
    Collection<UIComponent> visitedComponents = op.getVisitedComponents();
    for (UIComponent component : visitedComponents) {
      Assert.assertEquals("Component should only be visited once", 1, op.getTotal(component));
    }
  }
  
  @Test
  public void testWalkOnePanelWithCellComponents() {
    List<Panel> panels = new ArrayList<Panel>();
    panels.add(buildPanelWithCellComponents());
    CountingOperation op = new CountingOperation();
    Panel.walkAllUIComponents(panels, op);
    Assert.assertEquals("Panels contains 7 components", 7, op.getTotal());
    Collection<UIComponent> visitedComponents = op.getVisitedComponents();
    for (UIComponent component : visitedComponents) {
      Assert.assertEquals("Component should only be visited once", 1, op.getTotal(component));
    }
  }

  @Test
  public void testWalkOnePanelWithGesture() {
    List<Panel> panels = new ArrayList<Panel>();
    panels.add(buildPanelWithGesture());
    CountingOperation op = new CountingOperation();
    Panel.walkAllUIComponents(panels, op);
    Assert.assertEquals("Panels contains 1 gesture", 1, op.getTotal());
    Collection<UIComponent> visitedComponents = op.getVisitedComponents();
    for (UIComponent component : visitedComponents) {
      Assert.assertEquals("Component should only be visited once", 1, op.getTotal(component));
    }
  }

  @Test
  public void testWalkTwoGroupsReferencingSameScreenWithAbsoluteComponents() {
    List<Panel> panels = new ArrayList<Panel>();

    Panel p = new Panel();
    p.setOid(IDUtil.nextID());
    p.setName("panel");
    
    Screen screen1 = buildScreenWithAbsoluteComponents();
    
    Group group1 = new Group();
    group1.setOid(IDUtil.nextID());
    group1.setName("group1");

    ScreenPair screenPair1 = new ScreenPair();
    screenPair1.setOid(IDUtil.nextID());
    screenPair1.setPortraitScreen(screen1);
    List<ScreenPairRef> screenRefs = new ArrayList<ScreenPairRef>();
    screenRefs.add(new ScreenPairRef(screenPair1));
    group1.setScreenRefs(screenRefs);
    
    Group group2 = new Group();
    group1.setOid(IDUtil.nextID());
    group2.setName("group2");
    
    ScreenPair screenPair2 = new ScreenPair();
    screenPair2.setOid(IDUtil.nextID());
    screenPair2.setPortraitScreen(screen1);
    List<ScreenPairRef> screenRefs2 = new ArrayList<ScreenPairRef>();
    screenRefs2.add(new ScreenPairRef(screenPair2));
    group2.setScreenRefs(screenRefs2);
    
    List<GroupRef> groupRefs = new ArrayList<GroupRef>();
    groupRefs.add(new GroupRef(group1));
    groupRefs.add(new GroupRef(group2));
    p.setGroupRefs(groupRefs);

    panels.add(p);
    
    CountingOperation op = new CountingOperation();
    Panel.walkAllUIComponents(panels, op);
    Assert.assertEquals("Panels contains 7 components", 7, op.getTotal());
    Collection<UIComponent> visitedComponents = op.getVisitedComponents();
    for (UIComponent component : visitedComponents) {
      Assert.assertEquals("Component should only be visited once", 1, op.getTotal(component));
    }
  }

  @Test
  public void testWalkTwoGroupsReferencingSameScreenWithCellComponents() {
    List<Panel> panels = new ArrayList<Panel>();

    Panel p = new Panel();
    p.setOid(IDUtil.nextID());
    p.setName("panel");
    
    Screen screen1 = buildScreenWithCellComponents();
    
    Group group1 = new Group();
    group1.setOid(IDUtil.nextID());
    group1.setName("group1");

    ScreenPair screenPair1 = new ScreenPair();
    screenPair1.setOid(IDUtil.nextID());
    screenPair1.setPortraitScreen(screen1);
    List<ScreenPairRef> screenRefs = new ArrayList<ScreenPairRef>();
    screenRefs.add(new ScreenPairRef(screenPair1));
    group1.setScreenRefs(screenRefs);
    
    Group group2 = new Group();
    group1.setOid(IDUtil.nextID());
    group2.setName("group2");
    
    ScreenPair screenPair2 = new ScreenPair();
    screenPair2.setOid(IDUtil.nextID());
    screenPair2.setPortraitScreen(screen1);
    List<ScreenPairRef> screenRefs2 = new ArrayList<ScreenPairRef>();
    screenRefs2.add(new ScreenPairRef(screenPair2));
    group2.setScreenRefs(screenRefs2);
    
    List<GroupRef> groupRefs = new ArrayList<GroupRef>();
    groupRefs.add(new GroupRef(group1));
    groupRefs.add(new GroupRef(group2));
    p.setGroupRefs(groupRefs);

    panels.add(p);
    
    CountingOperation op = new CountingOperation();
    Panel.walkAllUIComponents(panels, op);
    Assert.assertEquals("Panels contains 7 components", 7, op.getTotal());
    Collection<UIComponent> visitedComponents = op.getVisitedComponents();
    for (UIComponent component : visitedComponents) {
      Assert.assertEquals("Component should only be visited once", 1, op.getTotal(component));
    }
  }
  
  @Test
  public void testWalkTwoGroupsReferencingSameScreenWithGesture() {
    List<Panel> panels = new ArrayList<Panel>();

    Panel p = new Panel();
    p.setOid(IDUtil.nextID());
    p.setName("panel");
    
    Screen screen1 = new Screen();
    screen1.setOid(IDUtil.nextID());
    screen1.setName("screen1");

    Gesture gesture = new Gesture(GestureType.swipe_bottom_to_top);
    gesture.setOid(IDUtil.nextID());
    screen1.addGesture(gesture);
    
    Group group1 = new Group();
    group1.setOid(IDUtil.nextID());
    group1.setName("group1");

    ScreenPair screenPair1 = new ScreenPair();
    screenPair1.setOid(IDUtil.nextID());
    screenPair1.setPortraitScreen(screen1);
    List<ScreenPairRef> screenRefs = new ArrayList<ScreenPairRef>();
    screenRefs.add(new ScreenPairRef(screenPair1));
    group1.setScreenRefs(screenRefs);
    
    Group group2 = new Group();
    group1.setOid(IDUtil.nextID());
    group2.setName("group2");
    
    ScreenPair screenPair2 = new ScreenPair();
    screenPair2.setOid(IDUtil.nextID());
    screenPair2.setPortraitScreen(screen1);
    List<ScreenPairRef> screenRefs2 = new ArrayList<ScreenPairRef>();
    screenRefs2.add(new ScreenPairRef(screenPair2));
    group2.setScreenRefs(screenRefs2);
    
    List<GroupRef> groupRefs = new ArrayList<GroupRef>();
    groupRefs.add(new GroupRef(group1));
    groupRefs.add(new GroupRef(group2));
    p.setGroupRefs(groupRefs);

    panels.add(p);
    
    CountingOperation op = new CountingOperation();
    Panel.walkAllUIComponents(panels, op);
    Assert.assertEquals("Panels contains 1 gesture", 1, op.getTotal());
    Collection<UIComponent> visitedComponents = op.getVisitedComponents();
    for (UIComponent component : visitedComponents) {
      Assert.assertEquals("Component should only be visited once", 1, op.getTotal(component));
    }
  }
  
  private Panel buildPanelWithAbsoluteComponents() {
    List<ScreenPairRef> screenRefs = new ArrayList<ScreenPairRef>();
    List<GroupRef> groupRefs = new ArrayList<GroupRef>();

    Panel p = new Panel();
    p.setOid(IDUtil.nextID());
    p.setName("panel");
    
    Screen screen1 = buildScreenWithAbsoluteComponents();
    
    ScreenPair screenPair = new ScreenPair();
    screenPair.setOid(IDUtil.nextID());
    screenPair.setPortraitScreen(screen1);
    screenRefs.add(new ScreenPairRef(screenPair));
    
    Group group1 = new Group();
    group1.setOid(IDUtil.nextID());
    group1.setName("group1");
    group1.setScreenRefs(screenRefs);
    
    groupRefs.add(new GroupRef(group1));
    p.setGroupRefs(groupRefs);

    return p;
  }

  private Screen buildScreenWithAbsoluteComponents() {
    Screen screen1 = new Screen();
    screen1.setOid(IDUtil.nextID());
    screen1.setName("screen1");
    
    UIButton button = new UIButton(IDUtil.nextID());
    button.setName("Button 1");

    Absolute buttonAbsolute = ModelerDomainTestHelper.createAbsolute(10,  button);
    screen1.addAbsolute(buttonAbsolute);
    
    UISwitch aSwitch = new UISwitch(IDUtil.nextID());
    aSwitch.setOnImage(new ImageSource("On image"));
    aSwitch.setOffImage(new ImageSource("Off image"));
    
    Absolute switchAbsolute = ModelerDomainTestHelper.createAbsolute(20,  aSwitch);
    screen1.addAbsolute(switchAbsolute);
    
    UISlider slider = new UISlider(IDUtil.nextID());
    slider.setVertical(true);
    
    Absolute sliderAbsolute = ModelerDomainTestHelper.createAbsolute(30, slider);
    screen1.addAbsolute(sliderAbsolute);
    
    UILabel label = new UILabel(IDUtil.nextID());
    label.setText("Label");
    
    Absolute labelAbsolute = ModelerDomainTestHelper.createAbsolute(40, label);
    screen1.addAbsolute(labelAbsolute);
    
    UIImage image = new UIImage(IDUtil.nextID());
    image.setImageSource(new ImageSource("Image"));
    
    Absolute imageAbsolute = ModelerDomainTestHelper.createAbsolute(50, image);
    screen1.addAbsolute(imageAbsolute);
    
    UIWebView webView = new UIWebView(IDUtil.nextID());
    webView.setURL("http://www.openremote.org");
    
    Absolute webAbsolute = ModelerDomainTestHelper.createAbsolute(60, webView);
    screen1.addAbsolute(webAbsolute);
    
    ColorPicker colorPicker = new ColorPicker();
    colorPicker.setOid(IDUtil.nextID());
    
    Absolute colorPickerAbsolute = ModelerDomainTestHelper.createAbsolute(70, colorPicker);
    screen1.addAbsolute(colorPickerAbsolute);
    return screen1;
  }
  
  private Panel buildPanelWithCellComponents() {
    List<ScreenPairRef> screenRefs = new ArrayList<ScreenPairRef>();
    List<GroupRef> groupRefs = new ArrayList<GroupRef>();

    Panel p = new Panel();
    p.setOid(IDUtil.nextID());
    p.setName("panel");
    
    Screen screen1 = buildScreenWithCellComponents();
    
    ScreenPair screenPair = new ScreenPair();
    screenPair.setOid(IDUtil.nextID());
    screenPair.setPortraitScreen(screen1);
    screenRefs.add(new ScreenPairRef(screenPair));
    
    Group group1 = new Group();
    group1.setOid(IDUtil.nextID());
    group1.setName("group1");
    group1.setScreenRefs(screenRefs);
    
    groupRefs.add(new GroupRef(group1));
    p.setGroupRefs(groupRefs);

    return p;
  }
  
  private Screen buildScreenWithCellComponents() {
    Screen screen1 = new Screen();
    screen1.setOid(IDUtil.nextID());
    screen1.setName("screen1");
    
    UIGrid grid = new UIGrid(10, 11, 12, 13, 4, 2);
    screen1.addGrid(grid);
    
    UIButton button = new UIButton(IDUtil.nextID());
    button.setName("Button 1");

    Cell buttonCell = ModelerDomainTestHelper.createCell(0,  0, button);
    grid.addCell(buttonCell);
    
    UISwitch aSwitch = new UISwitch(IDUtil.nextID());
    aSwitch.setOnImage(new ImageSource("On image"));
    aSwitch.setOffImage(new ImageSource("Off image"));

    Cell switchCell = ModelerDomainTestHelper.createCell(1, 0, aSwitch);
    grid.addCell(switchCell);
    
    UISlider slider = new UISlider(IDUtil.nextID());
    slider.setVertical(true);
    
    Cell sliderCell = ModelerDomainTestHelper.createCell(2, 0, slider);
    grid.addCell(sliderCell);
    
    UILabel label = new UILabel(IDUtil.nextID());
    label.setText("Label");
    
    Cell labelCell = ModelerDomainTestHelper.createCell(3, 0, label);
    grid.addCell(labelCell);
    
    UIImage image = new UIImage(IDUtil.nextID());
    image.setImageSource(new ImageSource("Image"));
    
    Cell imageCell = ModelerDomainTestHelper.createCell(0, 1, image);
    grid.addCell(imageCell);
    
    UIWebView webView = new UIWebView(IDUtil.nextID());
    webView.setURL("http://www.openremote.org");

    Cell webCell = ModelerDomainTestHelper.createCell(1, 1, webView);
    grid.addCell(webCell);
    
    ColorPicker colorPicker = new ColorPicker();
    colorPicker.setOid(IDUtil.nextID());
    
    Cell colorPickerCell = ModelerDomainTestHelper.createCell(2, 1, colorPicker);
    grid.addCell(colorPickerCell);
    
    return screen1;
  }
  
  private Panel buildPanelWithGesture() {
    List<ScreenPairRef> screenRefs = new ArrayList<ScreenPairRef>();
    List<GroupRef> groupRefs = new ArrayList<GroupRef>();

    Panel p = new Panel();
    p.setOid(IDUtil.nextID());
    p.setName("panel");
    
    Screen screen1 = new Screen();
    screen1.setOid(IDUtil.nextID());
    screen1.setName("screen1");

    Gesture gesture = new Gesture(GestureType.swipe_bottom_to_top);
    gesture.setOid(IDUtil.nextID());
    screen1.addGesture(gesture);
    
    ScreenPair screenPair = new ScreenPair();
    screenPair.setOid(IDUtil.nextID());
    screenPair.setPortraitScreen(screen1);
    screenRefs.add(new ScreenPairRef(screenPair));
    
    Group group1 = new Group();
    group1.setOid(IDUtil.nextID());
    group1.setName("group1");
    group1.setScreenRefs(screenRefs);
    
    groupRefs.add(new GroupRef(group1));
    p.setGroupRefs(groupRefs);

    return p;
  }

  private class CountingOperation implements Panel.UIComponentOperation {
    
    private IdentityHashMap<UIComponent, Long> counters =  new IdentityHashMap<UIComponent, Long>();
    private long total = 0;

    @Override
    public void execute(UIComponent component) {
      Long counter = counters.get(component);
      if (counter == null) {
        counter = new Long(0);
      }
      counters.put(component, ++counter);
      total++;
    }
    
    public long getTotal() {
      return total;
    }
    
    public Collection<UIComponent> getVisitedComponents() {
      return counters.keySet();
    }
    
    public long getTotal(UIComponent component) {
      return counters.get(component);
    }
  }

}
