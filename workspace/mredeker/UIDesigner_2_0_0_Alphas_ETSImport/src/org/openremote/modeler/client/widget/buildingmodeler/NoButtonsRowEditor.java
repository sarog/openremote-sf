package org.openremote.modeler.client.widget.buildingmodeler;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.RowEditor;

public class NoButtonsRowEditor<M extends ModelData> extends RowEditor<M> {

    private ListStore<ModelData> store;

    public NoButtonsRowEditor(ListStore<ModelData> store) {
        super();
        super.renderButtons = false;
        this.store = store;
    }

    @Override
    public void stopEditing(boolean saveChanges) {
        super.stopEditing(saveChanges);
        if (isDirty()) {
            store.commitChanges();
        }
    }

}
