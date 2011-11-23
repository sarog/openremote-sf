package org.openremote.web.console.widget.panel.form;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.openremote.web.console.panel.entity.GridLayout;
import org.openremote.web.console.widget.ConsoleComponent;
import org.openremote.web.console.widget.panel.PanelComponent;
import org.openremote.web.console.widget.Sensor;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

public class FormPanelComponent extends PanelComponent {
	private static final String CLASS_NAME = "formPanelComponent";
	private List<FormField> fields = new ArrayList<FormField>();
	private List<ConsoleComponent> buttons = new ArrayList<ConsoleComponent>();
	
	public FormPanelComponent() {
		Grid grid = new Grid(1,1);
		grid.setCellPadding(0);
		grid.setCellSpacing(0);
		setPanelWidget(grid);
	}
	
	public void addField(FormField field) {
		if (field != null) {
			fields.add(field);
		}
	}
	
	public ConsoleComponent getField(int row) {
		return (ConsoleComponent)(((Grid)getWidget()).getWidget(row, 0));
	}
	
	public void addButton(FormButton button) {
		if (button != null) {
			buttons.add(button);
		}
	}
	
	public List<FormField> getFields() {
		return fields;
	}

	// ---------------------------------------------------------------------------------
	//			SUPER CLASS OVERRIDES BELOW
	// ---------------------------------------------------------------------------------
	
	@Override
	public void onRender(int width, int height) {
		Grid grid = (Grid)getWidget();
		int rows = fields.size();
		grid.resizeRows(rows + 1);
		int rowHeight = (int)Math.round((double)height / (rows + 1));
		
		for (int i=0; i<rows; i++) {
			HTMLTable.CellFormatter formatter = grid.getCellFormatter();
			FormField field = fields.get(i);
			
			formatter.setHeight(i, 0, rowHeight + "px");
			formatter.setHorizontalAlignment(i, 0, HasHorizontalAlignment.ALIGN_LEFT);
			formatter.setVerticalAlignment(i, 0, HasVerticalAlignment.ALIGN_MIDDLE);
			
			grid.setWidget(i, 0, field);
			field.onAdd(width, rowHeight);
			
//			Widget widget = grid.getWidget(i, 0);
//			if (widget != null) {
//				ConsoleComponent component = (ConsoleComponent)widget;
//				if (component != null) {
//					component.onAdd(this.width, rowHeight);
//				}
//			}
		}
	
		// Force button size to be 80 x 35
		HorizontalPanel buttonPanel = new HorizontalPanel();
		for (ConsoleComponent button : buttons) {
			buttonPanel.add((Widget)button);
			button.onAdd(80, 35);
		}
		grid.setWidget(rows, 0, buttonPanel);
	}
	
	@Override
	public void onRemove() {
		Iterator<Widget> iterator = ((Grid)getWidget()).iterator();
		while(iterator.hasNext()) {
//			Widget widget = iterator.next();			
//			ConsoleComponent component = (ConsoleComponent)widget;
//			if (component != null) {
//				component.onRemove();
//			}
		}
	}
	
	@Override
	public Set<Sensor> getSensors() {
		return null;
	}

	@Override
	public Set<ConsoleComponent> getComponents() {
		return new HashSet<ConsoleComponent>(buttons);
	}
	
	@Override
	public String getClassName() {
		return CLASS_NAME;
	}
	
	// ---------------------------------------------------------------------------------
	//			BUILD METHOD BELOW HERE
	// ---------------------------------------------------------------------------------
	
	public static FormPanelComponent build(GridLayout layout) throws Exception {
		FormPanelComponent panel = new FormPanelComponent();
		if (layout == null) {
			return panel;
		}
		panel.setHeight(layout.getHeight());
		panel.setWidth(layout.getWidth());
		panel.setPosition(layout.getLeft(),layout.getTop());
		
		// Add Fields
		
		return panel;
	}
}
