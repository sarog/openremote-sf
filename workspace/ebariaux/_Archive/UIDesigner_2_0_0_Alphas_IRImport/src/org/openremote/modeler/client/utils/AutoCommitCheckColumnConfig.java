package org.openremote.modeler.client.utils;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.widget.grid.CheckColumnConfig;

public class AutoCommitCheckColumnConfig extends CheckColumnConfig {


    public AutoCommitCheckColumnConfig() {
        super();
    }

    public AutoCommitCheckColumnConfig(String id, String name, int width) {
        super(id, name, width);
    }

    @Override
    protected void onMouseDown(GridEvent<ModelData> ge) {
        super.onMouseDown(ge);
        grid.getStore().commitChanges();
    }

}
