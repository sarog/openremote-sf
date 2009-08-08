package org.openremote.android.console.bindings;

import java.io.Serializable;
import java.util.List;

/**
 * This is a simple convenience interface so that you do not have to type the
 * FQN of Activity each time due to name conflicts. SimpleBinder needs the real
 * class.
 * 
 * @see org.openremote.android.console.bindings.Activity
 * @author Andrew C. Oliver <acoliver osintegrators.com>
 */
public interface ORActivity extends Serializable {

    public List<Screen> getScreens();

    public void setScreens(List<Screen> screens);

    public String getId();

    public void setId(String id);

    public void setName(String name);

    public String getName();

}
