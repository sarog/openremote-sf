package org.openremote.android.console;

import java.util.ArrayList;

import org.openremote.android.console.net.AsyncControllerAvailabilityChecker;
import org.openremote.android.console.view.ControllerListItemLayout;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ControllerListAdapter extends ArrayAdapter<ControllerObject> {

	private ArrayList<ControllerObject> items;
	private Context ctx;

	public ControllerListAdapter(Context context, int textViewResourceId, ArrayList<ControllerObject> items) {
		super(context, textViewResourceId, items);
		this.items = items;
		ctx=context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final View v;
		final ControllerObject o;
		TextView tt = null;
		
		if (convertView == null) {
			v = View.inflate(ctx, R.layout.controller_list_item, null);
		} else {
			v = convertView;
		}

		o = items.get(position);
		
		if (o != null) {
			tt = (TextView)v.findViewById(R.id.controllerURL);

			if (tt != null) {
				tt.setText(o.getControllerName());
			}
		}
		
		// Check Controller Availability
		final ControllerListItemLayout itemLayout = (ControllerListItemLayout)v;
		
		AsyncControllerAvailabilityChecker checker = new AsyncControllerAvailabilityChecker() {
			@Override
			public void onPostExecute(Boolean result) {
				o.setIsControllerUp(result);
				
			  // Set availability indicator on controller item and make item checkable if available
			  ProgressBar pb = (ProgressBar)v.findViewById(R.id.controller_status_searching);
			  pb.setVisibility(View.GONE);
			  
			  if (result) {
			  	ImageView ok = (ImageView)v.findViewById(R.id.controller_status_ok);
			  	ok.setVisibility(View.VISIBLE);
			  	itemLayout.setCheckable(true);
			  } else {
				  ImageView nok = (ImageView)v.findViewById(R.id.controller_status_nok);
				  nok.setVisibility(View.VISIBLE);
			  }
			}
		};
		
	  // Set progress indicator on controller item
	  ProgressBar pb = (ProgressBar)v.findViewById(R.id.controller_status_searching);
	  ImageView ok = (ImageView)v.findViewById(R.id.controller_status_ok);
	  ImageView nok = (ImageView)v.findViewById(R.id.controller_status_nok);
	  ok.setVisibility(View.GONE);
	  nok.setVisibility(View.GONE);
	  pb.setVisibility(View.VISIBLE);
	  
		checker.execute(o.getControllerName());
		
//		if(o.isAuto()){
//			icon.setImageResource(R.drawable.auto_discovered);
//		}
//		
//		if((o.getFailoverFor().length()>0)){
//			icon.setImageResource(R.drawable.slider_thumb);
//		}
//		
//		else{
//			icon.setImageResource(R.drawable.custom_controller);
//		}
//		
//		ImageView iconUp=(ImageView)v.findViewById(R.id.iconup);         	 
//
//		if(o.isControllerUp()){
//			iconUp.setImageResource(R.drawable.icon);
//		}else{
//			iconUp.setImageResource(R.drawable.ic_notfound);
//		}

//		if(items.get(position).isIs_Selected()){
//			Log.e("IconicAdapter", "Selected state is saved blue");
//			tt.setBackgroundColor(Color.BLUE);
//		}
//		if(!(items.get(position).isIs_Selected())){
//			Log.e("IconicAdapter", "Selected state is saved black");
//			tt.setBackgroundColor(Color.BLACK);
//		}
//
//		if(v.isPressed()||v.isSelected()||tt.isChecked()||tt.isSelected()||tt.isPressed()){
//			Log.e("IconicAdapter", "Selected state is saved");
//			tt.setBackgroundColor(Color.BLUE);
//		}

		return v;
	}
}
