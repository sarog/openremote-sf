/* OpenRemote, the Home of the Digital Home*/
package org.openremote.android.console;


/**
 * 
 * for read and save content of an NFC-Tag
 * @author Grâce Tchougbe
 * 
 */
import java.io.UnsupportedEncodingException;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ReadingNFC_Activity extends Activity {
	/** Called when the activity is first created. */

	NfcAdapter mAdapter = null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		resolveIntent(intent);	
		intent.setClass(ReadingNFC_Activity.this, GroupActivity.class);
		startActivity(intent);	
	}

	@Override
	public void onNewIntent(Intent intent) {
		resolveIntent(intent);
	}

	public void resolveIntent(Intent intent) {
		String action = intent.getAction();

		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

			NdefMessage[] messages = null;
			
			Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
			if (rawMsgs != null) {

				messages = new NdefMessage[1];
                //put in "messages" information that is contained in the Tag
				messages[0] = (NdefMessage) rawMsgs[0];

			} else {
				// Unknown type of tag
				byte[] empty = new byte[] {};
				NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN,
						empty, empty, empty);
				NdefMessage msg = new NdefMessage(new NdefRecord[] { record });
				messages = new NdefMessage[] { msg };
			}

			intent.putExtra("NFCValue",getTextData(messages[0].getRecords()[0].getPayload()));
		

		} else {
			// Can't read the tag
			Dialog d = new Dialog(this);
			LinearLayout ll = new LinearLayout(this);
			TextView tv = new TextView(this);
			tv.setText("Can't read the tag! Try Again");
			ll.addView(tv);
			d.setContentView(ll);
			d.show();
		}

	}

	
	/** Constructs a new String by decoding the specified array of bytes. */
	public String getTextData(byte[] payload) {
		try {
			String texteCode = ((payload[0] & 0200) == 0) ? "UTF-8" : "UTF-16";
			int langageCodeTaille = payload[0] & 0077;
			return new String(payload, langageCodeTaille + 1, payload.length
					- langageCodeTaille - 1, texteCode);
		} catch (UnsupportedEncodingException e) {
			Log.e("NfcReaderActivity", e.getMessage());
			return null;
		}
	}

}