package com.socializeme.socializeme;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.EditText;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;

public class Settings extends Activity {
	SocialData s;
	EditText TextName;
	EditText TextNumber;
	EditText TextEmail;
	EditText TextTwitter;
	EditText TextFacebook;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_settings);
		// Show the Up button in the action bar.
		setupActionBar();

		s = new SocialData(this);

		TextName = (EditText) findViewById(R.id.editText_Name);
		TextNumber = (EditText) findViewById(R.id.editText_Number);
		TextEmail = (EditText) findViewById(R.id.editText_Email);
		TextTwitter = (EditText) findViewById(R.id.editText_Twitter);
		TextFacebook = (EditText) findViewById(R.id.editText_Facebook);
		setFields();
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	public void Pull(View view) {
		
	
		if(TextNumber.getText().toString().equals("")){
			TextNumber.setText(s.GetLocalPhoneNumber(this));
		}
		if(TextEmail.getText().toString().equals("")){
			TextEmail.setText(s.PullAccount(SocialData.TYPE_EMAIL));
		}
		if(TextTwitter.getText().toString().equals("")){
			TextTwitter.setText(s.PullAccount(SocialData.TYPE_TWITTER));
		}
		if(TextFacebook.getText().toString().equals("")){
			TextFacebook.setText(s.PullAccount(SocialData.TYPE_FACEBOOK));
		}
	}

	public void save(View view) {
		s.SaveData(SocialData.KEY_NAME, TextName.getText().toString());
		s.SaveData(SocialData.KEY_PHONE, TextNumber.getText().toString());
		s.SaveData(SocialData.KEY_EMAIL, TextEmail.getText().toString());
		s.SaveData(SocialData.KEY_TWIT, TextTwitter.getText().toString());
		s.SaveData(SocialData.KEY_FB, TextFacebook.getText().toString());
		
		Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
	}

	private void setFields() {
		
		TextName.setText(s.GetData(SocialData.KEY_NAME));
		TextNumber.setText(s.GetData(SocialData.KEY_PHONE));
		TextEmail.setText(s.GetData(SocialData.KEY_EMAIL));
		TextTwitter.setText(s.GetData(SocialData.KEY_TWIT));	
		TextFacebook.setText(s.GetData(SocialData.KEY_FB));
	}
	
	
}
