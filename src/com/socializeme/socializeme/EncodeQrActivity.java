package com.socializeme.socializeme;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

public class EncodeQrActivity extends Activity {

	public Bitmap bm = null;
	public SocialData s;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.encode_qr);

		encodeBarcode();
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	/*
	 * @TargetApi(Build.VERSION_CODES.HONEYCOMB) private void setupActionBar() {
	 * if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
	 * getActionBar().setDisplayHomeAsUpEnabled(true); } }
	 */

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			// NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void encodeBarcode() {
		// ImageView img = (ImageView)findViewById(R.id.imageView1);
		SocialData s = new SocialData(this);

		final String message = s.GenerateRecord();

		new Thread(new Runnable() {

			// @Override
			public void run() {

				bm = loadQRCode(message);
				myHandler.sendEmptyMessage(0);
				if (bm == null) {
					Toast.makeText(EncodeQrActivity.this,
							"Problem in loading QR Code1", Toast.LENGTH_LONG)
							.show();
				}

			}

		}).start();

		// img.setImageBitmap(bm);
	}


	final Handler myHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			ImageView img = (ImageView) findViewById(R.id.imageView1);
			/* do all your ui action here to display the image () */
			img.setImageBitmap(bm);
		}
	};

	private Bitmap loadQRCode(String input) {
		Bitmap bmQR = null;
		InputStream inputStream = null;

		try {
			inputStream = OpenHttpConnection("http://chart.apis.google.com/chart?chs=100x100&cht=qr&choe=UTF-8&chl="
					+ URLEncoder.encode(input, "UTF-8"));
			bmQR = BitmapFactory.decodeStream(inputStream);
			inputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bmQR;
	}

	private InputStream OpenHttpConnection(String strURL) throws IOException {
		InputStream is = null;
		@SuppressWarnings("unused")
		String error = null;
		URL url = new URL(strURL);
		URLConnection urlConnection = url.openConnection();

		try {
			HttpURLConnection httpConn = (HttpURLConnection) urlConnection;
			httpConn.setRequestMethod("GET");
			httpConn.connect();

			// if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
			is = httpConn.getInputStream();
			// }
			error = httpConn.getResponseMessage();
		} catch (Exception ex) {

		}

		return is;
	}

}
