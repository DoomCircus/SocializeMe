/**
 * 
 */
package com.socializeme.socializeme;

import java.util.ArrayList;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.widget.Toast;
/**
 * @author Matthew Anselmo
 *
 */
public class SocialData {
	private final static String PACKAGE = "com.socializeme.socializeme";
	
	public final static String KEY_NAME = "Name";
	public final static String KEY_PHONE = "Phone";
	public final static String KEY_EMAIL = "Email";
	public final static String KEY_FB = "Facebook";
	public final static String KEY_TWIT = "Twitter";
	
	public final static String TYPE_TWITTER = "com.twitter.android.auth.login";
	public final static String TYPE_EMAIL = "com.google";
	public final static String TYPE_FACEBOOK = "com.facebook.auth.login";	

	
	private SharedPreferences prefs;
	
	public Account[] accounts;
	
	public SocialData(){}
	
	public SocialData(Context c){
		prefs = c.getSharedPreferences(PACKAGE, Context.MODE_PRIVATE);
		
		GetAccounts(null, c);
		
	}
	
	private void GetAccounts(String type, Context c){
		AccountManager am = AccountManager.get(c); // "this" references the current Context
		accounts = am.getAccountsByType(type);
	}
	
	public String PullAccount(String type){
		for (Account acc : accounts){
			 if(acc.type.equals(type)){
				 return acc.name;
			 }
		}
		return "";
	}
	
	public String GetLocalPhoneNumber(Context c){
		return ((TelephonyManager)c.getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number();       
	}
	
	public String GetData(String key){
		return prefs.getString(AppendKey(key), "");
	}
	
	public boolean SaveData(String key, String data){	
		return prefs.edit().putString(AppendKey(key), data).commit();	
	}
	
	private String AppendKey(String key){
		return PACKAGE + "." + key;
	}
	
	public String GenerateRecord(){
		String[] record = new String[5];
		
		record[0] = GetData(KEY_NAME);
		record[1] = GetData(KEY_PHONE);
		record[2] = GetData(KEY_EMAIL);
		record[3] = GetData(KEY_TWIT);
		record[4] = GetData(KEY_FB);
		return EncodeData(record);
	}
	
	private String EncodeData(String[] arr){
		String temp = "";
		for (String s : arr){
			if(s.equals("")){
				s = " ";
			}
			temp += s + "|";
		}
		
		return temp;
	}
	public void addToContacts(Context c, String name, String phone, String email){
		ArrayList < ContentProviderOperation > ops = new ArrayList < ContentProviderOperation > ();

		 ops.add(ContentProviderOperation.newInsert(
		 ContactsContract.RawContacts.CONTENT_URI)
		     .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, "com.google")
		     .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, accounts[0].name)
		     .build());
		
		 if (name != null) {
		     ops.add(ContentProviderOperation.newInsert(
		     ContactsContract.Data.CONTENT_URI)
		         .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
		         .withValue(ContactsContract.Data.MIMETYPE,
		     ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
		         .withValue(
		     ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
		     name).build());
		 }
		 
		 if (phone != null) {
		     ops.add(ContentProviderOperation.
		     newInsert(ContactsContract.Data.CONTENT_URI)
		         .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
		         .withValue(ContactsContract.Data.MIMETYPE,
		     ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
		         .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
		         .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
		     ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
		         .build());
		 }
		 
		 if (email != null) {
		     ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
		         .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
		         .withValue(ContactsContract.Data.MIMETYPE,
		     ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
		         .withValue(ContactsContract.CommonDataKinds.Email.DATA, email)
		         .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
		         .build());
		 }
		 
		 try {
		     c.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
		     requestSync(c);
		 } catch (Exception e) {
		     e.printStackTrace();
		     Toast.makeText(c, "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
		 } 
		

	}
	
	private void requestSync(Context c)
	{
	    AccountManager am = AccountManager.get(c);
	    Account[] accounts = am.getAccounts();

	    for (Account account : accounts)
	    {
	        int isSyncable = ContentResolver.getIsSyncable(account, ContactsContract.AUTHORITY);

	        if (isSyncable > 0)
	        {
	            Bundle extras = new Bundle();
	            extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
	            ContentResolver.requestSync(accounts[0], ContactsContract.AUTHORITY, extras);
	        }
	    }
	}
}
