package org.openremote.android.console;

import java.util.ArrayList;

import org.openremote.android.console.net.AsyncControllerAvailabilityChecker;
import org.openremote.android.console.view.ControllerListItemLayout;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
		ControllerListItemLayout v;
		ControllerObject controller = items.get(position);
		TextView tt = null;
		
		if (convertView == null) {
			v = (ControllerListItemLayout)View.inflate(ctx, R.layout.controller_list_item, null);
		} else {
			v = (ControllerListItemLayout)convertView;
		}

	  ProgressBar pb = (ProgressBar)v.findViewById(R.id.controller_status_searching);
	  ImageView ok = (ImageView)v.findViewById(R.id.controller_status_ok);
	  ImageView nok = (ImageView)v.findViewById(R.id.controller_status_nok);
		tt = (TextView)v.findViewById(R.id.controllerURL);
		
		if (controller == null) {
			return null;
		}
		
		tt.setText(controller.getControllerName());
		
		v.setChecked(controller.isIs_Selected());
		
		if (controller.isAvailabilityCheckDone()) {
		  pb.setVisibility(View.GONE);
		  boolean result = controller.isControllerUp();
		  
		  if (result) {
		  	ok.setVisibility(View.VISIBLE);
		  	nok.setVisibility(View.GONE);
		  	v.setCheckable(true);
		  } else {
			  nok.setVisibility(View.VISIBLE);
		  	ok.setVisibility(View.GONE);
		  	v.setCheckable(false);
		  }
		} else {
			// Check Controller Availability
			AsyncControllerAvailabilityChecker checker = new AsyncControllerAvailabilityChecker((ControllerListItemLayout)v, controller);
			v.setCheckerTask(checker);
			
		  // Set progress indicator on controller item
		  ok.setVisibility(View.GONE);
		  nok.setVisibility(View.GONE);
		  pb.setVisibility(View.VISIBLE);
		  controller.setAvailabilityCheckDone();
			checker.execute(controller.getControllerName());
		}
		
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
