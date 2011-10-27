package org.openremote.android.console;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

public class IconicAdapter extends ArrayAdapter<ControllerObject> {

	private ArrayList<ControllerObject> items;
	private Context ctx;

	public IconicAdapter(Context context, int textViewResourceId, ArrayList<ControllerObject> items) {
		super(context, textViewResourceId, items);
		this.items = items;
		ctx=context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v;
		if (convertView == null) {

			v = View.inflate(ctx, R.layout.row, null);
		}
		else{
			v=convertView;
		}
		ImageView icon=(ImageView)v.findViewById(R.id.icon);

		CheckedTextView tt = null;

		ControllerObject o = items.get(position);
		if (o != null) {
			tt = (CheckedTextView) v.findViewById(R.id.toptext);

			if (tt != null) {
				tt.setText(o.getControllerName()); 
			}
		}
		
		
		if(o.isAuto()){
			icon.setImageResource(R.drawable.auto_discovered);
		}
		
		if((o.getFailoverFor().length()>0)){
			icon.setImageResource(R.drawable.slider_thumb);
		}
		
		else{
			icon.setImageResource(R.drawable.custom_controller);
		}
		
		
		
		ImageView iconUp=(ImageView)v.findViewById(R.id.iconup);         	 

		if(o.isControllerUp()){
			iconUp.setImageResource(R.drawable.icon);
		}else{
			iconUp.setImageResource(R.drawable.ic_notfound);
		}

		if(items.get(position).isIs_Selected()){
			Log.e("IconicAdapter", "Selected state is saved blue");
			tt.setBackgroundColor(Color.BLUE);
		}
		if(!(items.get(position).isIs_Selected())){
			Log.e("IconicAdapter", "Selected state is saved black");
			tt.setBackgroundColor(Color.BLACK);
		}

		if(v.isPressed()||v.isSelected()||tt.isChecked()||tt.isSelected()||tt.isPressed()){
			Log.e("IconicAdapter", "Selected state is saved");
			tt.setBackgroundColor(Color.BLUE);
		}

		return v;
	}
}
