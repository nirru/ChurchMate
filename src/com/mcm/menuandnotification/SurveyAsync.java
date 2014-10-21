package com.mcm.menuandnotification;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.mcm.library.SurveyNotificationPostJson;

public class SurveyAsync extends AsyncTask<String, String, String> {

	ProgressDialog mProgressDialog;
	Context context;
	String url;
	int clientID, memberId, notificationId;
	boolean isApprove;
	RelativeLayout rel_xyz;
	String message;

	public SurveyAsync(Context context, ProgressDialog mProgressDialog,
			String url, int clientID, int memberId, int notificationId,
			boolean isApprove, RelativeLayout rel_xyz) {
		this.context = context;
		this.mProgressDialog = mProgressDialog;
		this.url = url;
		this.clientID = clientID;
		this.memberId = memberId;
		this.notificationId = notificationId;
		this.isApprove = isApprove;
		this.rel_xyz = rel_xyz;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		showDialog();
	}

	@Override
	protected String doInBackground(String... aurl) {
		// Log.e("MAY AYA", "" + "do in background in sigin");
		callLogin();
		return null;
	}

	@Override
	protected void onPostExecute(String unused) {
		closeDialog();
		rel_xyz.setVisibility(View.GONE);
       showOKAleart("Message", message);
	}

	void showDialog() {
		mProgressDialog
				.setMessage("Login Successful.  Sync data from Server is in progress. Please wait patiently....");
		mProgressDialog.setCancelable(false);
		mProgressDialog.show();
	}

	void closeDialog() {
		if (mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
	}

	private void callLogin() {
		String url = "http://mcmwebapi.victoriatechnologies.com/api/PushNotification/PostSurvey";
		SurveyNotificationPostJson surveyNotificationPostJson = new SurveyNotificationPostJson(
				url, clientID, memberId, notificationId, isApprove);
		 message = surveyNotificationPostJson.postDataToServer();
		Log.e("TAGGGGGG", "" + message);
	}

	public void showOKAleart(String title, String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title).setMessage(message)
				.setNegativeButton("OK", null).show();
	}
}
