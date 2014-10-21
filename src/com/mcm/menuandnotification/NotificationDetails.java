package com.mcm.menuandnotification;

import java.io.File;
import java.io.FileInputStream;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.mcm.R;
import com.mcm.ReminderService;
import com.mcm.SplashActivity;
import com.mcm.database.AppConstant;
import com.mcm.database.InsertTable;

//import android.util.Log;

public class NotificationDetails extends Fragment implements AlertInterface {
	TextView tv_eventName, tv_eventDateTime, tv_eventlongDesc,
			location_not_provided_tv;;
	ListView listView;
	ArrayList<ArrayList<String>> eventsList;
	Context context;
	int clientID;
	String eventName;
	String eventDateTime;
	String eventlongDesc;
	String dateToPass;
	String datOfWeek;
	String month;
	String year;
	String folderName;
	int hour, min, endhour, endmin;
	UiTimePicker uiTimePicker;
	TextView evetnDeatilHeader;
	int monthIndex = 0, endmonthIndex = 0;
	int dateIndex;
	String amPm, endamPm;

	Uri eventsUri, remainderUri;

	Cursor cursor;

	int[] calendarId;
	String[] calendarNames;
	String location;
	long startCalTime;
	long endCalTime;
	Date eventDate = null, endEventDate = null;
	String calId = null;
	int customCalenderHour, endcustomCalenderHour, reminderDays;
	Date eventTime, eventStartTime;
	ImageView imageReminder, delete_Reminder, ev_detail_fg_bg_imageView;
	String setReminder, recurrimgReminder;
	String email;
	NotificationManager nm;
	boolean isReminderSetToClientIDForDate;
	long set_event_id;
	ArrayList<Long> listOfSavedEventId;

	public NotificationDetails(Context context, String eventName,
			String eventDateTime, String eventlongDesc, String dateToPass,
			String datOfWeek, String month, String year, int hour, int min,
			String amPm, String folderName, String setReminder,
			String recurrimgReminder, String email) {

		this.eventName = eventName;
		this.eventDateTime = eventDateTime;
		this.eventlongDesc = eventlongDesc;
		this.dateToPass = dateToPass;
		this.datOfWeek = datOfWeek;
		this.month = month;
		this.year = year;
		this.hour = hour;
		this.min = min;
		this.amPm = amPm;
		this.setReminder = setReminder;
		this.recurrimgReminder = recurrimgReminder;
		this.email = email;

		this.folderName = folderName;
		this.context = context;

		uiTimePicker = new UiTimePicker(context, false);

		reminderDays = Integer.parseInt(recurrimgReminder);
		Log.e("Reminder Days", "" + reminderDays);
		Log.e("SET REMINDER", "" + setReminder);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragement_detail_event,
				container, false);
		init(rootView);
		hideReminderButton();
		setBackground("EventsBG.png", folderName, ev_detail_fg_bg_imageView);
		setTextOnTextView();
		return rootView;
	}

	private void init(View rootView) {
		// Log.e("EVENTS LIST", "" + eventsList);
		listOfSavedEventId = new ArrayList<Long>();
		imageReminder = (ImageView) rootView.findViewById(R.id.imageView1);

		delete_Reminder = (ImageView) rootView
				.findViewById(R.id.delete_reminder);
		ev_detail_fg_bg_imageView = (ImageView) rootView
				.findViewById(R.id.fg_detail_imageview);
		evetnDeatilHeader = (TextView) rootView
				.findViewById(R.id.event_detail_header);
		evetnDeatilHeader.setText("Notification Details");
		tv_eventName = (TextView) rootView.findViewById(R.id.textView2);
		tv_eventDateTime = (TextView) rootView.findViewById(R.id.textView4);
		location_not_provided_tv = (TextView) rootView
				.findViewById(R.id.textView5);
		tv_eventlongDesc = (TextView) rootView.findViewById(R.id.textView7);

		nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		imageReminder.setOnClickListener(l);
		delete_Reminder.setOnClickListener(dl);
	}

	private void hideReminderButton() {
		if (setReminder.trim().equals("false")) {
			imageReminder.setVisibility(View.INVISIBLE);
		} else {
			goForConditionCheck();
		}
	}

	private void goForConditionCheck() {
		Log.e("APPLICATION REMINDER EMPTY", ""
				+ isNotificationReminderTableEmpty());
		if (!isNotificationReminderTableEmpty()) {
			imageReminder.setVisibility(View.VISIBLE);
			delete_Reminder.setVisibility(View.INVISIBLE);
		} else {
			showDeleteReminderButtonOnSavedReminder();
		}
	}

	private void showDeleteReminderButtonOnSavedReminder() {
		if (!isReminderSavedInDatabase()) {
			imageReminder.setVisibility(View.VISIBLE);
			delete_Reminder.setVisibility(View.INVISIBLE);
		} else {
			imageReminder.setVisibility(View.INVISIBLE);
			delete_Reminder.setVisibility(View.VISIBLE);
		}
	}

	private boolean isNotificationReminderTableEmpty() {
		boolean hasTables = false;
		SQLiteDatabase database = SplashActivity.databaseHelper
				.getWritableDatabase();
		String q = " SELECT * FROM "
				+ AppConstant.CHECK_NOTIFICATION_REMINDER_TABLE;
		Cursor cursor = database.rawQuery(q, null);
		if (cursor.getCount() == 0)
			hasTables = false;
		if (cursor.getCount() > 0)
			hasTables = true;

		this.cursor = cursor;
		cursor.close();

		Log.e("HO TABLE IS EMPTY", "--" + hasTables);
		return hasTables;
	}

	private boolean isReminderSavedInDatabase() {

		SQLiteDatabase sqLiteDatabase = SplashActivity.databaseHelper
				.getReadableDatabase();

		String query = " SELECT * "
				+ " FROM "
				+ com.mcm.database.AppConstant.CHECK_NOTIFICATION_REMINDER_TABLE
				+ " WHERE "
				+ com.mcm.database.AppConstant.NOTIFICATIION_REMIONDER_CLIENT_ID
				+ " ='"
				+ clientID
				+ "'"
				+ " AND "
				+ com.mcm.database.AppConstant.NOTIFICATIION_REMIONDER_CLIENT_EMAIL
				+ " ='" + email + "'" + " AND "
				+ com.mcm.database.AppConstant.NOTIFICATIION_REMIONDER_NAME
				+ " ='" + eventName + "'";

		Cursor cursor = sqLiteDatabase.rawQuery(query, null);
		Log.e("CURSOR >GET COUNT", "" + cursor.getCount());
		if (cursor.getCount() == 0) {
			isReminderSetToClientIDForDate = false;
		} else {
			cursor.moveToFirst();
			if (!cursor.isAfterLast()) {
				do {
					isReminderSetToClientIDForDate = cursor.getInt(4) > 0;
					set_event_id = cursor.getLong(3);
					Log.e("set_event_id_", "--" + "---" + set_event_id
							+ "-----" + isReminderSetToClientIDForDate);
					listOfSavedEventId.add(set_event_id);

				} while (cursor.moveToNext());
				cursor.close();
			}
			Log.e("LIST OF EVENT ID", "" + listOfSavedEventId);
		}

		return isReminderSetToClientIDForDate;
	}

	private void setTextOnTextView() {

		String longDesc = "0:0 am to 0:0 am  on  every " + datOfWeek
				+ ". Please come and enjoy";
		tv_eventName.setText(eventName);
		tv_eventDateTime.setText(dateToPass);
		tv_eventlongDesc.setText(eventlongDesc);

	}

	private int getMonth() {
		try {
			Calendar cal = Calendar.getInstance();
			cal.setTime(new SimpleDateFormat("MMM").parse(month));
			monthIndex = cal.get(Calendar.MONTH);
			String monthString = new DateFormatSymbols().getMonths()[monthIndex];
			Log.e("MONTH NAME AFTER CONVERING", "" + monthString);
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.e("MONTH", "" + monthIndex);
		return monthIndex;
	}

	private void setOtherReminder() {
		int id = 1;
		Calendar cal = Calendar.getInstance();
		long i = cal.getTimeInMillis();
		Log.e("CURRENT MILLIS", "" + cal.getTimeInMillis());
		getMonth();

		Log.e("YEAR",
				"" + Integer.parseInt(year) + " -- " + "MONTH" + "---"
						+ monthIndex + "-----" + "DAY OF WEEK " + "---"
						+ Integer.parseInt(datOfWeek));

		Log.e("HOUR", "" + hour + " -- " + "MINUTE" + "---" + min + "-----"
				+ "SECOND " + "---" + amPm);
		cal.set(Integer.parseInt(year), monthIndex, Integer.parseInt(datOfWeek));
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, min);
		cal.set(Calendar.SECOND, 0);
		if (amPm.equals("AM")) {
			Log.e("IN IF ENTERTED", "YES");
			customCalenderHour = hour;
			cal.set(Calendar.AM_PM, Calendar.AM);
		} else {
			Log.e("IN ELSE ENTERTED", "YES");
			customCalenderHour = hour + 12;
			cal.set(Calendar.AM_PM, Calendar.PM);
		}
		long j = cal.getTimeInMillis();

		long diff = j - i;
		Log.e("DIFF", "" + diff);
		// if (diff > 0) {
		// Log.e("NOTIFICTION IS SET", "YES");
		// // NotificationReceiver.setNotificationOnDateTime(getActivity(), cal
		// // .getTimeInMillis(), tv_eventlongDesc.getText().toString(),
		// // id);
		// String customCalenderAlertDate = (monthIndex + 1) + "/"
		// + Integer.parseInt(datOfWeek) + "/"
		// + Integer.parseInt(year);
		//
		// setRemindarCustom(customCalenderAlertDate, customCalenderHour, min,
		// eventName);
		// showOKAleart("Reminder", "Reminder is saved successfully");
		// } else {
		// Log.e("NOTIFICTION IS NOT SET", "YES");
		// }

		insertEventToCalendar(reminderDays);

	}

	private void insertEventToCalendar(int recuringDays) {
		long calID = 1;
		long startMillis = 0;
		long endMillis = 0;
		Uri EVENTS_URI = Uri
				.parse(getCalendarUriBase(getActivity()) + "events");
		TimeZone timeZone = TimeZone.getDefault();
		Calendar beginTime = Calendar.getInstance();

		if (recuringDays > 1) {

			for (int i = 0; i < recuringDays; i++) {

				int startDateOfWeek = Integer.parseInt(datOfWeek) + i;

				beginTime.set(Integer.parseInt(year), monthIndex,
						startDateOfWeek, customCalenderHour, min);
				startMillis = beginTime.getTimeInMillis();

				ContentResolver cr = context.getContentResolver();
				ContentValues values = new ContentValues();
				values.put(CalendarContract.Events.DTSTART, startMillis);
				values.put(CalendarContract.Events.DTEND, startMillis);
				values.put(CalendarContract.Events.TITLE, eventName);
				values.put(CalendarContract.Events.DESCRIPTION, dateToPass);
				values.put(CalendarContract.Events.HAS_ALARM, 1);
				values.put(CalendarContract.Events.CALENDAR_ID, calID);
				values.put(CalendarContract.Events.EVENT_TIMEZONE,
						timeZone.getID());

				Uri uri = cr.insert(EVENTS_URI, values);

				// get the event ID that is the last element in the Uri
				long eventID = Long.parseLong(uri.getLastPathSegment());

				// reminder insert
				Uri REMINDERS_URI = Uri.parse(getCalendarUriBase(getActivity())
						+ "reminders");
				values = new ContentValues();
				values.put("event_id", eventID);
				values.put("method", 1);
				values.put("minutes", 5);
				context.getContentResolver().insert(REMINDERS_URI, values);

				InsertTable insertTable = new InsertTable(
						SplashActivity.databaseHelper.getReadableDatabase());
				insertTable.insertValueInNotificationRemindertable(clientID,
						eventName, email, eventID, true);

				Log.e("EVENT ID MAI KYA HAI", "" + eventID);
			}

		} else {
			beginTime.set(Integer.parseInt(year), monthIndex,
					Integer.parseInt(datOfWeek), customCalenderHour, min);
			startMillis = beginTime.getTimeInMillis();
			ContentResolver cr = context.getContentResolver();
			ContentValues values = new ContentValues();
			values.put(CalendarContract.Events.DTSTART, startMillis);
			values.put(CalendarContract.Events.DTEND, startMillis);
			values.put(CalendarContract.Events.TITLE, eventName);
			values.put(CalendarContract.Events.DESCRIPTION, dateToPass);
			values.put(CalendarContract.Events.HAS_ALARM, 1);
			values.put(CalendarContract.Events.CALENDAR_ID, calID);
			values.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());

			Uri uri = cr.insert(EVENTS_URI, values);

			// get the event ID that is the last element in the Uri
			long eventID = Long.parseLong(uri.getLastPathSegment());

			// reminder insert
			Uri REMINDERS_URI = Uri.parse(getCalendarUriBase(getActivity())
					+ "reminders");
			values = new ContentValues();
			values.put("event_id", eventID);
			values.put("method", 1);
			values.put("minutes", 5);
			context.getContentResolver().insert(REMINDERS_URI, values);

			InsertTable insertTable = new InsertTable(
					SplashActivity.databaseHelper.getReadableDatabase());
			insertTable.insertValueInNotificationRemindertable(clientID,
					eventName, email, eventID, true);

			Log.e("EVENT ID MAI KYA HAI", "" + eventID);
		}

		imageReminder.setVisibility(View.INVISIBLE);
		delete_Reminder.setVisibility(View.VISIBLE);

	}

	private String getCalendarUriBase(Activity act) {
		String calendarUriBase = null;
		Uri calendars = Uri.parse("content://calendar/calendars");
		Cursor managedCursor = null;
		try {
			managedCursor = act.managedQuery(calendars, null, null, null, null);
		} catch (Exception e) {
		}
		if (managedCursor != null) {
			calendarUriBase = "content://calendar/";
		} else {
			calendars = Uri.parse("content://com.android.calendar/calendars");
			try {
				managedCursor = act.managedQuery(calendars, null, null, null,
						null);
			} catch (Exception e) {
			}
			if (managedCursor != null) {
				calendarUriBase = "content://com.android.calendar/";
			}
		}
		return calendarUriBase;
	}

	View.OnClickListener l = new OnClickListener() {

		@Override
		public void onClick(View v) {
			IsReminderSet();
		}
	};

	private void IsReminderSet() {
		if (setReminder.trim().equals("true")) {
			setOtherReminder();
			showOKAleart("Message", "Event Saved succesfully");
		}
	}

	View.OnClickListener dl = new OnClickListener() {

		@Override
		public void onClick(View v) {
			deleteReminderonButtonClick();
		}
	};

	private void deleteReminderonButtonClick() {
		isReminderSavedInDatabase();
		InsertTable insertTable = new InsertTable(
				SplashActivity.databaseHelper.getReadableDatabase());
		for (int i = 0; i < listOfSavedEventId.size(); i++) {
			deleteCalendarEntry(listOfSavedEventId.get(i));
			insertTable.updateValueInNotificationRemindertable(clientID,
					eventName, email, listOfSavedEventId.get(i), false);
		}

		imageReminder.setVisibility(View.VISIBLE);
		delete_Reminder.setVisibility(View.INVISIBLE);
		showOKAleart("Message", "Event deleted successfully");
	}

	private int deleteCalendarEntry(long entryID) {
		int iNumRowsDeleted = 0;
		Uri eventsUri = Uri.parse(getCalendarUriBase(getActivity()) + "events");
		Uri eventUri = ContentUris.withAppendedId(eventsUri, entryID);
		iNumRowsDeleted = context.getContentResolver().delete(eventUri, null,
				null);
		return iNumRowsDeleted;
	}

	public void showOKAleart(String title, String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title).setMessage(message)
				.setNegativeButton("OK", null).show();
	}

	@Override
	public void alertDialog() {
		showOKAleart("Reminder", "Set");
	}

	public Bitmap setBackground(String filename, String folder,
			ImageView imageView) {
		Bitmap thumbnail = null;
		try {
			FileInputStream fi = new FileInputStream(new File(
					context.getFilesDir(), "/Images/" + folder + "/"
							+ "ThemeImages/" + filename));
			thumbnail = BitmapFactory.decodeStream(fi);
			Drawable d = new BitmapDrawable(getResources(), thumbnail);
			imageView.setImageBitmap(thumbnail);
		} catch (Exception ex) {
			Log.e("getThumbnail() on internal storage", ex.getMessage());
		}
		return thumbnail;
	}
}
