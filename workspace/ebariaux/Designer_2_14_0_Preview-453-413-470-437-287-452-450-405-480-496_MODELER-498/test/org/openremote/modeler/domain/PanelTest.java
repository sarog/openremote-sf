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
import org.openremote.modeler.domain.component.ImageSource;
import org.openremote.modeler.domain.component.UIButton;
import org.openremote.modeler.domain.component.UIComponent;
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
    panels.add(buildPanel());
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
  public void testWalkOnePanel() {
    List<Panel> panels = new ArrayList<Panel>();
    panels.add(buildPanel());
    CountingOperation op = new CountingOperation();
    Panel.walkAllUIComponents(panels, op);
    Assert.assertEquals("Panels contains 7 components", 7, op.getTotal());
    Collection<UIComponent> visitedComponents = op.getVisitedComponents();
    for (UIComponent component : visitedComponents) {
      Assert.assertEquals("Component should only be visited once", 1, op.getTotal(component));
    }
  }
  
  @Test
  public void testWalkTwoGroupsReferencingSameScreen() {
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

  private Panel buildPanel() {
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

    Absolute buttonAbsolute = createAbsolute(10,  button);
    screen1.addAbsolute(buttonAbsolute);
    
    UISwitch aSwitch = new UISwitch(IDUtil.nextID());
    aSwitch.setOnImage(new ImageSource("On image"));
    aSwitch.setOffImage(new ImageSource("Off image"));
    
    Absolute switchAbsolute = createAbsolute(20,  aSwitch);
    screen1.addAbsolute(switchAbsolute);
    
    UISlider slider = new UISlider(IDUtil.nextID());
    slider.setVertical(true);
    
    Absolute sliderAbsolute = createAbsolute(30, slider);
    screen1.addAbsolute(sliderAbsolute);
    
    UILabel label = new UILabel(IDUtil.nextID());
    label.setText("Label");
    
    Absolute labelAbsolute = createAbsolute(40, label);
    screen1.addAbsolute(labelAbsolute);
    
    UIImage image = new UIImage(IDUtil.nextID());
    image.setImageSource(new ImageSource("Image"));
    
    Absolute imageAbsolute = createAbsolute(50, image);
    screen1.addAbsolute(imageAbsolute);
    
    UIWebView webView = new UIWebView(IDUtil.nextID());
    webView.setURL("http://www.openremote.org");
    
    Absolute webAbsolute = createAbsolute(60, webView);
    screen1.addAbsolute(webAbsolute);
    
    ColorPicker colorPicker = new ColorPicker();
    colorPicker.setOid(IDUtil.nextID());
    
    Absolute colorPickerAbsolute = createAbsolute(70, colorPicker);
    screen1.addAbsolute(colorPickerAbsolute);
    return screen1;
  }
  
  // Copied from ResourceServiceTest -> move to utility class
  private Absolute createAbsolute(int baseValue, UIComponent uiComponent) {
    Absolute abs = new Absolute(IDUtil.nextID());
    abs.setPosition(baseValue, baseValue + 1);
    abs.setSize(baseValue + 2, baseValue + 3);
    abs.setUiComponent(uiComponent);
    return abs;
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
