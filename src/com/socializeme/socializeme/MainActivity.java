package com.socializeme.socializeme;



import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.Bundle;

import android.view.Menu;
import android.view.View;
import android.widget.Button;



public class MainActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		
		NfcManager manager = (NfcManager) this.getSystemService(Context.NFC_SERVICE);
		NfcAdapter adapter = manager.getDefaultAdapter();
		if (adapter == null ) {
			 final Button buttonNFC=(Button)findViewById(R.id.button_NFC);
			 buttonNFC.setEnabled(false);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		Button button = (Button)findViewById(R.id.QRScan);
		
		int numCameras = Camera.getNumberOfCameras();
		if (numCameras == 0)
		{
		 
		button.setEnabled(false);
		}
		return true;
	}
	
	public void settings(View view){
		Intent intent = new Intent(this, Settings.class);
		startActivity(intent);
	}
	
	public void scanQR(View view)
	{
		Intent  intent = new Intent(view.getContext(), CameraTestActivity.class);
		
        startActivity(intent);      
       
		
	
	}
	
	public void encodeQR(View view)
	{
		Intent  intent = new Intent(view.getContext(), EncodeQrActivity.class);
        startActivity(intent);      
        
	}
	
	public void NFC(View view){
		Intent  intent = new Intent(view.getContext(), NFC.class);
        startActivity(intent);    
	}
}
