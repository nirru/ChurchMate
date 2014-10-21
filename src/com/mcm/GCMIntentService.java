package com.mcm;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.mcm.appconstant.AppConstant;
import com.mcm.database.GetDataFromDatabase;
import com.mcm.database.InsertTable;
import com.mcm.database.PzDatabaseHelper;
import com.mcm.login.LoginActivity;

public class GCMIntentService extends GCMBaseIntentService {

	String message;
	private static final String TAG = "GCM Tutorial::Service";
	SharedPreferences sharedPreferences;
	boolean isLoginAlready;
	int clientid, notificationId, surveyFlag;

	String clientEmail;
	String customData, ad, df, cs;

	int push_notid_id, client_id;
	String notif_date, notif_title, notif_details, notif_status,
			notif_set_reminder, notif_recuiring_reminder_days,
			notif_survey_flag;
	// Use your PROJECT ID from Google API into SENDER_ID
	public static final String SENDER_ID = AppConstant.PROJECTS_NUMBER;
	public static String reg_ID;

	ArrayList<String> eventList;

	// public PzDatabaseHelper databaseHelper1;
	SQLiteDatabase database;

	public GCMIntentService() {
		super(SENDER_ID);

	}

	@Override
	protected void onRegistered(Context context, String registrationId) {
		reg_ID = registrationId;
		// Log.i(TAG, "onRegistered: registrationId=" + registrationId);
		// putValueInPrefs(context,registrationId);
	}

	@Override
	protected void onUnregistered(Context context, String registrationId) {

		// Log.i(TAG, "onUnregistered: registrationId=" + registrationId);
	}

	@Override
	protected void onMessage(Context context, Intent data) {

		getValueFromPrefs(context);
		// Message from PHP server
		message = data.getStringExtra("message");
		// customData = "CId=37;Id=140;SurveyFlag=0";
		customData = data.getStringExtra("custom");
		// Log.e("<<<< MESSAGE>>", "" + message);
		Log.e("<<<<Custom MESSAGE>>", "" + customData);
		seperateString(customData);
		setNotificationOnMessageArrived(context);
		//
		startAsynTask();
		Looper.loop();
	}

	@Override
	protected void onError(Context arg0, String errorId) {

		Log.e(TAG, "onError: errorId=" + errorId);
	}

	private void putValueInPrefs(Context context) {
		SharedPreferences.Editor prefs = context.getSharedPreferences(
				"com.example.app", Context.MODE_PRIVATE).edit();
		prefs.putBoolean(com.mcm.appconstant.AppConstant.PREFS_KEYS,
				isLoginAlready);
		prefs.putInt(com.mcm.appconstant.AppConstant.PREFS_KEYS_CLIENT_ID,
				clientid);
		prefs.putString(com.mcm.appconstant.AppConstant.EMAIL_ID, clientEmail
				.toString().trim());

		prefs.putInt(com.mcm.appconstant.AppConstant.NOTIFICATION_SURVEY_FLAG,
				surveyFlag);

		prefs.putInt(
				com.mcm.appconstant.AppConstant.NOTIFICATION_PUSH_NOTIFICATION_ID,
				notificationId);

		prefs.putString(com.mcm.appconstant.AppConstant.NOTIFICATION_DETAILS,
				message);
		prefs.commit();
	}

	private void getValueFromPrefs(Context context) {
		sharedPreferences = context.getSharedPreferences("com.example.app",
				Context.MODE_PRIVATE);
		isLoginAlready = sharedPreferences.getBoolean(
				com.mcm.appconstant.AppConstant.PREFS_KEYS, false);
		clientid = sharedPreferences.getInt(
				com.mcm.appconstant.AppConstant.PREFS_KEYS_CLIENT_ID, 0);
		clientEmail = sharedPreferences.getString(
				com.mcm.appconstant.AppConstant.EMAIL_ID, null);

		// Log.e("IS LOGIN ALREADY", "" + isLoginAlready);
	}

	private void setNotificationOnMessageArrived(Context context) {
		// Open a new activity called GCMMessageView
		putValueInPrefs(context);
		
		Intent intent = new Intent(context,SplashActivity.class);
		// Starts the activity on notification click
		PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		// Create the notification with a notification builder
		Notification notification = new NotificationCompat.Builder(context)
				.setSmallIcon(R.drawable.app_icon)
				.setWhen(System.currentTimeMillis())
				.setContentTitle("MyChurchMateApp").setContentText(message)
				.setContentIntent(pIntent).getNotification();

		// Remove the notification on click
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		manager.notify(R.string.app_name, notification);

		{
			// Wake Android Device when notification received
			PowerManager pm = (PowerManager) context
					.getSystemService(Context.POWER_SERVICE);
			final PowerManager.WakeLock mWakelock = pm.newWakeLock(
					PowerManager.FULL_WAKE_LOCK
							| PowerManager.ACQUIRE_CAUSES_WAKEUP, "GCM_PUSH");
			mWakelock.acquire();

			// Timer before putting Android Device to sleep mode.
			Timer timer = new Timer();
			TimerTask task = new TimerTask() {
				public void run() {
					mWakelock.release();
				}
			};
			timer.schedule(task, 5000);
		}
	}

	private void startAsynTask() {
		String urlNotification = "http://mcmwebapi.victoriatechnologies.com/api/PushNotification/GetPushNotification?ClientId="
				+ clientid + "&NofiticationId=" + notificationId;
		// Log.e("GSDD", "" + urlNotification);
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(urlNotification, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String response) {
				// Log.e("RESPONSE NOTIFICATION", "" + response);
				try {
					JSONObject jsonObject = new JSONObject(response);
					push_notid_id = jsonObject
							.getInt(AppConstant.NOTIFICATION_PUSH_NOTIFICATION_ID);
					client_id = jsonObject
							.getInt(AppConstant.NOTIFICATION_CLIENT_ID);
					notif_date = jsonObject
							.getString(AppConstant.NOTIFICATION_DATE);
					notif_title = jsonObject
							.getString(AppConstant.NOTIFICATION_TITLE);
					notif_details = jsonObject
							.getString(AppConstant.NOTIFICATION_DETAILS);
					notif_status = jsonObject
							.getString(AppConstant.NOTIFICATION_STATUS);
					notif_set_reminder = jsonObject
							.getString(AppConstant.NOTIFICATION_SET_REMINDER);
					notif_recuiring_reminder_days = jsonObject
							.getString(AppConstant.NOTIFICATION_RECURRING_REMINDER_DAYS);
					notif_survey_flag = jsonObject
							.getString(AppConstant.NOTIFICATION_SURVEY_FLAG);
					Log.e("PUSH Notification Title", "" + notif_title);
                    if (surveyFlag == 1) {
						
					} else {
						checkDuplicateEnteries(notif_title);
					}
					
				} catch (Exception e) {
					e.printStackTrace();

				}
			}
		});

	}

	private void checkDuplicateEnteries(String notificationTitle) {

		GetDataFromDatabase getDataFromDatabase = new GetDataFromDatabase();

		if (!getDataFromDatabase.checkForNotificationTables()) {
			Log.e("FALSE",
					"" + getDataFromDatabase.checkForNotificationTables());
			InsertEnteries();
		} else {
			Log.e("True",
					"" + getDataFromDatabase.checkForNotificationTables());
			removeEnteries(notificationTitle);
		}

	}

	private void removeEnteries(String notificationTitle) {

		eventList = new ArrayList<String>();
		GetDataFromDatabase getDataFromDatabase = new GetDataFromDatabase();
		ArrayList<ArrayList<String>> allNotification = getDataFromDatabase
				.getAllNotification(clientid);
		for (int i = 0; i < allNotification.size(); i++) {
			eventList.add(allNotification.get(i).get(3).toString().trim());
		}

		Log.e("MY LIST OF EVENTS", "" + eventList);
		if (eventList.contains(notificationTitle)) {
			// Do Not Insert It into Table
			Log.e("ITEM FOUND", "" + "YYESS");
		} else {
			// Insert Into Table
			Log.e("ITEM NOT FOUND", "" + "NO");
			InsertEnteries();
		}
	}

	private void InsertEnteries() {

		InsertTable insertTable = new InsertTable(
				SplashActivity.databaseHelper.getReadableDatabase());
		insertTable.addRowforNotificationTable(push_notid_id, client_id,
				notif_date, notif_title, notif_details, notif_status,
				notif_set_reminder, notif_recuiring_reminder_days,
				notif_survey_flag);

	}

	private void seperateString(String data) {
		String[] s = data.split(";");
		// Log.e("MESSAGE STRING LENNGHT", "" + s.length);
		for (String string : s)
			Log.e("STRING MESASAGE", "" + string);

		ad = s[0].substring(s[0].indexOf("=") + 1);
		clientid = Integer.parseInt(ad);
		// Log.e("FIRST VALUE", "" + ad);

		df = s[1].substring(s[1].indexOf("=") + 1);
		notificationId = Integer.parseInt(df);

		cs = s[2].substring(s[2].indexOf("=") + 1);
		surveyFlag = Integer.parseInt(cs);
		// Log.e("Last VAlue", "" + df);
	}

}