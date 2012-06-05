/* OpenRemote, the Home of the Digital Home*/
package org.openremote.android.console;


/**
 * 
 * for detecting the presence of an NFC Tag
 * @author Grâce Tchougbe
 * 
 */

import org.openremote.android.console.R;

import android.app.PendingIntent;

import android.content.Intent;

import android.nfc.NfcAdapter;

import android.os.Bundle;


import android.view.View;
import android.view.View.OnClickListener;

import android.widget.Button;
import android.widget.LinearLayout;



public class NfcActivity extends GenericActivity {
	/** Called when the activity is first created. */

	private NfcAdapter mAdapter;
	private PendingIntent PendingIntent;

	protected void onCreate(Bundle bundle) {

		super.onCreate(bundle); 
		
		this.setContentView(R.layout.nfcreading_view);

		mAdapter = NfcAdapter.getDefaultAdapter(this.getApplicationContext());
		Intent intent = new Intent(this, ReadingNFC_Activity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
				| Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_NO_HISTORY);

		PendingIntent = android.app.PendingIntent.getActivity(this, 0, intent,
				0);
		
		
		 Button myButton = (Button)findViewById(R.id.cancelButton);
	       
	        myButton.setOnClickListener(new OnClickListener()
	        {
				public void onClick(View arg0) {
					// TODO Cancel 		
		          finish(); 
				}

			
	        });
	       
	      
	    	
	}

	public void onResume() {
		super.onResume();
		mAdapter.enableForegroundDispatch(this, PendingIntent, null, null);

	}

	public void onPause() {
		super.onPause();
		mAdapter.disableForegroundDispatch(this);
		finish(); 
	}


	
}
