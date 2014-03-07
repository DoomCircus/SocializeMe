package com.socializeme.socializeme;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcAdapter.OnNdefPushCompleteCallback;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import java.nio.charset.Charset;

@SuppressLint("HandlerLeak")
public class NFC extends Activity implements CreateNdefMessageCallback,
OnNdefPushCompleteCallback {
	private NfcAdapter mNfcAdapter;
	private Intent nfcIntent;
	private PendingIntent pendingIntent;
	private EditText name;
	private EditText phone;
	private EditText email;
	private EditText twitter;
	
	private static final int MESSAGE_SENT = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nfc);
		SocialData s = new SocialData(this);
		name = (EditText) findViewById(R.id.editText_Name);
		phone = (EditText) findViewById(R.id.editText_Number);
		email = (EditText) findViewById(R.id.editText_Email);
		twitter = (EditText) findViewById(R.id.editText_Twitter);
		
		
		name.setText(s.GetData(SocialData.KEY_NAME));
		phone.setText(s.GetData(SocialData.KEY_PHONE));
		email.setText(s.GetData(SocialData.KEY_EMAIL));
		twitter.setText(s.GetData(SocialData.KEY_TWIT));
		
		//Keeps user from editing account fields, this must be done in settings
		name.setKeyListener(null);
		phone.setKeyListener(null);
		email.setKeyListener(null);
		twitter.setKeyListener(null);
		
		
		// Check for available NFC Adapter
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		pendingIntent = PendingIntent.getActivity(
				  this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		if (mNfcAdapter == null) {
			//mInfoText = (EditText) findViewById(R.id.test);
			//mInfoText.setText("NFC is not available on this device.");
		}
		// Register callback to set NDEF message
		mNfcAdapter.setNdefPushMessageCallback(this, this);
		// Register callback to listen for message-sent success
		mNfcAdapter.setOnNdefPushCompleteCallback(this, this);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (mNfcAdapter != null) {
			mNfcAdapter.disableForegroundDispatch(this);
            mNfcAdapter.disableForegroundNdefPush(this);
        }
		nfcIntent = getIntent();
		setIntent(nfcIntent);
	}

	/**
	 * Implementation for the CreateNdefMessageCallback interface
	 */
	@Override
	public NdefMessage createNdefMessage(NfcEvent event) {
		SocialData s = new SocialData(this);

		String text = s.GenerateRecord();
		NdefMessage msg = new NdefMessage(
				new NdefRecord[] { createMimeRecord(
						"application/com.socializeme.socializeme", text.getBytes())
				});
		return msg;
	}
	
	

	/**
	 * Implementation for the OnNdefPushCompleteCallback interface
	 */
	@Override
	public void onNdefPushComplete(NfcEvent arg0) {
		// A handler is needed to send messages to the activity when this
		// callback occurs, because it happens from a binder thread
		mHandler.obtainMessage(MESSAGE_SENT).sendToTarget();
	}

	/** This handler receives a message from onNdefPushComplete */
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_SENT:
				Toast.makeText(getApplicationContext(), "Message sent!", Toast.LENGTH_LONG).show();
				break;
			}
		}
	};

	@Override
	public void onResume() {
		super.onResume();
		// Check to see that the Activity started due to an Android Beam
		mNfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
			processIntent(nfcIntent);
		}
	}

	@Override
	public void onNewIntent(Intent intent) {
		// onResume gets called after this to handle the intent
		nfcIntent = intent;
		setIntent(intent);
	}

	/**
	 * Parses the NDEF Message from the intent and prints to the TextView
	 */
	void processIntent(Intent intent) {
		Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
		// only one message sent during the beam
		NdefMessage msg = (NdefMessage) rawMsgs[0];
		// record 0 contains the MIME type, record 1 is the AAR, if present
		//mInfoText.setText(new String(msg.getRecords()[0].getPayload()));
		
		String data = new String(msg.getRecords()[0].getPayload());
		String delims = "[|]+";
    	String[] tokens = data.split(delims);
    
    	SocialData s = new SocialData(this);
    	
    	boolean saveContact = true;
    	for(int i = 0; i < 3; i++){
    		if(tokens[i].equals(" ")){
    			saveContact = false;
    		}
    	}
    	
    	if(saveContact){
    		s.addToContacts(this,tokens[0], tokens[1], tokens[2]);
    	}
    	if(!tokens[3].equals(" ")){
    		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://twitter.com/"+ tokens[3]));
    		startActivity(browserIntent);
    		finish();
    	}
    	
	}

	/**
	 * Creates a custom MIME type encapsulated in an NDEF record
	 *
	 * @param mimeType
	 */
	public NdefRecord createMimeRecord(String mimeType, byte[] payload) {
		byte[] mimeBytes = mimeType.getBytes(Charset.forName("US-ASCII"));
		NdefRecord mimeRecord = new NdefRecord(
				NdefRecord.TNF_MIME_MEDIA, mimeBytes, new byte[0], payload);
		return mimeRecord;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// If NFC is not available, we won't be needing this menu
		if (mNfcAdapter == null) {
			return super.onCreateOptionsMenu(menu);
		}
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.nfc, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			Intent intent = new Intent(Settings.ACTION_NFCSHARING_SETTINGS);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}