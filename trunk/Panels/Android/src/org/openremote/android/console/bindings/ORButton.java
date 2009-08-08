package org.openremote.android.console.bindings;

import java.io.Serializable;

/**
 * This is a simple convenience interface so that you do not have to type the
 * FQN of Button each time due to name conflicts. SimpleBinder needs the real
 * class.
 * 
 * @see org.openremote.android.console.bindings.Button
 * @author Andrew C. Oliver <acoliver osintegrators.com>
 */
public interface ORButton extends Serializable {

    public String getId();

    public void setId(String id);

    public String getLabel();

    public void setLabel(String label);

    public int getX();

    public void setX(int x);

    public int getY();

    public void setY(int y);

    public int getWidth();

    public void setWidth(int width);

    public int getHeight();

    public void setHeight(int height);

    public String getIcon();

    public void setIcon(String icon);

}
