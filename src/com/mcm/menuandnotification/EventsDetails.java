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
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
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
import android.widget.Toast;

import com.mcm.R;
import com.mcm.SplashActivity;
import com.mcm.database.AppConstant;
import com.mcm.database.InsertTable;
public class EventsDetails extends Fragment implements AlertInterface {
	TextView tv_eventName, tv_eventDateTime, tv_eventlongDesc,
			location_not_provided_tv;;
	ListView listView;
	ArrayList<ArrayList<String>> eventsList;
	Context context;
	// int clientID;
	String eventName;
	String eventDateTime;
	String eventlongDesc;
	String dateToPass;
	String datOfWeek;
	String month;
	String year;
	String folderName;
	int hour, min, endhour, endmin,starthour, startmin;

	UiTimePicker uiTimePicker;
	TextView evetnDeatilHeader;
	int monthIndex = 0, endmonthIndex = 0, startmonthIndex = 0 ;
	int dateIndex;
	String amPm, endamPm,startamPm;
	ImageView imageReminder, delete_Reminder, ev_detail_fg_bg_imageView;

	String startdateToPass,startdayOfTheWeek, startmonth, startdatOfWeek, startyear;
	String enddateToPass, enddatOfWeek, endmonth, endday, endyear;

	NotificationManager nm;
	TextView savedEvents;

	String location;

	Date eventTime, eventStartTime;

	Uri eventsUri, remainderUri;

	Cursor cursor;

	int[] calendarId;
	String[] calendarNames;

	long startCalTime;
	long endCalTime;
	Date eventDate = null, endEventDate = null;
	String calId = null;
	int customCalenderHour, endcustomCalenderHour,startcustomCalenderHour;
	String reminder_flag;
	String email_String;
	int clientID;
	long set_event_id;
	boolean isReminderSetToClientIDForDate;

	public EventsDetails(Context context, String eventName,
			String eventDateTime, String eventlongDesc, String dateToPass,
			String datOfWeek, String month, String year, int hour, int min,
			String amPm, String enddateToPass, String enddatOfWeek,
			String endmonth, String endyear, int endhour, int endmin,
			String endamPm,String startdateToPass,String startdatOfWeek, String startmonth, String startyear,
			int starthour, int startmin, String startamPm, String folderName, Date eventTime,
			Date eventStartTime, String location, String reminder_flag,
			String email_String, int clientID) {

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

		this.enddateToPass = enddateToPass;
		this.enddatOfWeek = enddatOfWeek;
		this.endmonth = endmonth;
		this.endyear = endyear;
		this.endhour = endhour;
		this.endmin = endmin;
		this.endamPm = endamPm;
		this.startdateToPass = startdateToPass;
		this.startdatOfWeek = startdatOfWeek;
		this.startmonth = startmonth;
		this.startyear = startyear;
		this.starthour = starthour;
		this.startmin = startmin;
		this.startamPm = startamPm;

		this.folderName = folderName;
		this.context = context;
		this.eventTime = eventTime;
		this.eventStartTime = eventStartTime;
		this.location = location;
		this.reminder_flag = reminder_flag;
		this.email_String = email_String;
		this.clientID = clientID;
		uiTimePicker = new UiTimePicker(context, false);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragement_detail_event,
				container, false);
		init(rootView);
		setBackground("EventsBG.png", folderName, ev_detail_fg_bg_imageView);
		setTextOnTextView();
		setReminder();
		// checkReminderIsSetToAutomaticOrNot();
		return rootView;
	}

	private void init(View rootView) {
		// Log.e("EVENTS LIST", "" + eventsList);
		imageReminder = (ImageView) rootView.findViewById(R.id.imageView1);
		delete_Reminder = (ImageView) rootView
				.findViewById(R.id.delete_reminder);
		ev_detail_fg_bg_imageView = (ImageView) rootView
				.findViewById(R.id.fg_detail_imageview);
		evetnDeatilHeader = (TextView) rootView
				.findViewById(R.id.event_detail_header);
		tv_eventName = (TextView) rootView.findViewById(R.id.textView2);
		tv_eventDateTime = (TextView) rootView.findViewById(R.id.textView4);
		location_not_provided_tv = (TextView) rootView
				.findViewById(R.id.textView5);
		tv_eventlongDesc = (TextView) rootView.findViewById(R.id.textView7);

		nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		imageReminder.setOnClickListener(l);
		delete_Reminder.setOnClickListener(dl);
		savedEvents = (TextView) rootView.findViewById(R.id.saved_reminder);

	}

	private void setReminder() {
		if (!isReminderTableEmpty()) {
			Log.e("TABLE IS EMPTY", "YES");
			addingEventsToCalender();
		} else {
			Log.e("TABLE IS NON EMPTY", "YES");
			checkReminderIsSavedtOrNotByEventname();
		}
	}

	private void checkReminderIsSavedtOrNotByEventname() {
		if (!isReminderSavedInDatabase()) {
			// reminder is not saved in database
			Log.e("<<<isReminderSavedInDatabase???", "" + " FALSE");
			addingEventsToCalender();
		} else {
			// reminder is saved in database
			Log.e("<<<isReminderSavedInDatabase???", "" + " TRUE");
			showDeleteReminderButtonAndHideReminderButton();
		}
	}

	private void showDeleteReminderButtonAndHideReminderButton() {
		delete_Reminder.setVisibility(View.VISIBLE);
		imageReminder.setVisibility(View.INVISIBLE);
	}

	private boolean isReminderTableEmpty() {
		boolean hasTables = false;
		SQLiteDatabase database = SplashActivity.databaseHelper
				.getWritableDatabase();
		String q = " SELECT * FROM " + AppConstant.CHECK_REMINDER_TABLE;
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

	private void addingEventsToCalender() {
		if (!checkReminderFlag()) {
			showReminderButtonAndHideDeleteReminderButton();
		} else {
			saveEventsAutomatically();
		}
	}

	private boolean checkReminderFlag() {
		if (reminder_flag.trim().equals("true")) {
			Log.e("YE TRUE HAI KYA", "" + reminder_flag);
			return true;
		} else {
			Log.e("YE FALSE HAI KYA", "" + reminder_flag);
			return false;
		}
	}

	private void saveEventsAutomatically() {
		if (!checkCurrentDateIsGreaterThanEventSchduledDate()) {
			// Event not saved to calender
			imageReminder.setVisibility(View.INVISIBLE);
			delete_Reminder.setVisibility(View.INVISIBLE);
			Toast.makeText(
					context,
					"Event can't be saved to calender as current date is greater than event schduled date.. ",
					Toast.LENGTH_LONG).show();
		} else {
			// insert event to calender and hide reminder button and visible
			// delete reminder button
			insertEventAndHideReminderButton();
		}
	}

	private void insertEventAndHideReminderButton() {
		imageReminder.setVisibility(View.INVISIBLE);
		delete_Reminder.setVisibility(View.VISIBLE);
		insertEventToCalendar();
	}

	private void showReminderButtonAndHideDeleteReminderButton() {
		imageReminder.setVisibility(View.VISIBLE);
		delete_Reminder.setVisibility(View.INVISIBLE);
	}

	private void setEventOnButtonClick() {
		if (!checkCurrentDateIsGreaterThanEventSchduledDate()) {
			// Event not saved to calender
			imageReminder.setVisibility(View.INVISIBLE);
			delete_Reminder.setVisibility(View.INVISIBLE);
			Toast.makeText(
					context,
					"Event can't be saved to calender as current date is greater than event schduled date.. ",
					Toast.LENGTH_LONG).show();
		} else {
			// insert event to calender and hide reminder button and visible
			// delete reminder button
			insertEventAndHideReminderButton();
			showOKAleart("Message", "Event Saved succesfully");
		}
	}

	private int deleteCalendarEntry(long entryID) {
		int iNumRowsDeleted = 0;
		Uri eventsUri = Uri.parse(getCalendarUriBase(getActivity()) + "events");
		Uri eventUri = ContentUris.withAppendedId(eventsUri, entryID);
		iNumRowsDeleted = context.getContentResolver().delete(eventUri, null,
				null);

		showOKAleart("Message", "Event deleted successfully");
		Log.e("DEBUG_TAG", "Deleted " + iNumRowsDeleted + " calendar entry."
				+ "ENTRY ID" + "===" + entryID);

		return iNumRowsDeleted;
	}

	private boolean isReminderSavedInDatabase() {

		SQLiteDatabase sqLiteDatabase = SplashActivity.databaseHelper
				.getReadableDatabase();

		String query = " SELECT * " + " FROM "
				+ com.mcm.database.AppConstant.CHECK_REMINDER_TABLE + " WHERE "
				+ com.mcm.database.AppConstant.REMINDER_CLIENT_ID + " ='"
				+ clientID + "'" + " AND "
				+ com.mcm.database.AppConstant.REMINDER_CLIENT_EMAIL + " ='"
				+ email_String + "'" + " AND "
				+ com.mcm.database.AppConstant.REMINDER_NAME + " ='"
				+ eventName + "'";

		Cursor cursor = sqLiteDatabase.rawQuery(query, null);
		Log.e("CURSOR >GET COUNT", "" + cursor.getCount());
		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {
			do {
				isReminderSetToClientIDForDate = cursor.getInt(4) > 0;
				set_event_id = cursor.getLong(3);
				Log.e("REMINDER NAME__", "--" + eventName + "---"
						+ "isReminderSetToClientIDForDate" + "---"
						+ isReminderSetToClientIDForDate + "---"
						+ "set_event_id" + "---" + set_event_id);

			} while (cursor.moveToNext());
			cursor.close();
		}

		return isReminderSetToClientIDForDate;
	}

	private void setTextOnTextView() {

		String longDesc = "0:0 am to 0:0 am  on  every " + datOfWeek
				+ ". Please come and enjoy";
		tv_eventName.setText(eventName);
		tv_eventDateTime.setText(dateToPass);
		tv_eventlongDesc.setText(eventlongDesc);
		location_not_provided_tv.setText(location);

	}

	private int getMonth() {
		try {
			Calendar cal = Calendar.getInstance();
			cal.setTime(new SimpleDateFormat("MMM").parse(month));
			monthIndex = cal.get(Calendar.MONTH);
			String monthString = new DateFormatSymbols().getMonths()[monthIndex];
			// Log.e("MONTH NAME AFTER CONVERING", "" + monthString);
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Log.e("MONTH", "" + monthIndex);
		return monthIndex;
	}

	private int endgetMonth() {
		try {
			Calendar cal = Calendar.getInstance();
			cal.setTime(new SimpleDateFormat("MMM").parse(month));
			endmonthIndex = cal.get(Calendar.MONTH);
			String monthString = new DateFormatSymbols().getMonths()[monthIndex];
			// Log.e("MONTH NAME AFTER CONVERING", "" + monthString);
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Log.e("MONTH", "" + monthIndex);
		return monthIndex;
	}
	
	private int startgetMonth() {
		try {
			Calendar cal = Calendar.getInstance();
			cal.setTime(new SimpleDateFormat("MMM").parse(month));
			startmonthIndex = cal.get(Calendar.MONTH);
			String monthString = new DateFormatSymbols().getMonths()[monthIndex];
			// Log.e("MONTH NAME AFTER CONVERING", "" + monthString);
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Log.e("MONTH", "" + monthIndex);
		return monthIndex;
	}

	private boolean checkCurrentDateIsGreaterThanEventSchduledDate() {

		boolean isCurrentDateIsGreaterThanEventSchduledDate = false;
		Calendar cal = Calendar.getInstance();
		long i = cal.getTimeInMillis();
		// Log.e("CURRENT MILLIS", "" + cal.getTimeInMillis());
		getMonth();
		endgetMonth();
		startgetMonth();
		cal.set(Integer.parseInt(year), monthIndex, Integer.parseInt(datOfWeek));
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, min);
		cal.set(Calendar.SECOND, 0);
		if (amPm.equals("AM")) {
			customCalenderHour = hour;
			cal.set(Calendar.AM_PM, Calendar.AM);
		} else {
			customCalenderHour = hour + 12;
			cal.set(Calendar.AM_PM, Calendar.PM);
		}

		if (endamPm.equals("AM")) {
			endcustomCalenderHour = endhour;
		} else {
			endcustomCalenderHour = endhour + 12;
		}
		
		if (startamPm.equals("AM")) {
			startcustomCalenderHour = starthour;
		} else {
			startcustomCalenderHour = starthour + 12;
		}
		long j = cal.getTimeInMillis();

		long diff = j - i;
		if (diff > 0) {
			isCurrentDateIsGreaterThanEventSchduledDate = true;
			Log.e("NOTIFICTION IS SET", " "
					+ isCurrentDateIsGreaterThanEventSchduledDate);
		} else {
			isCurrentDateIsGreaterThanEventSchduledDate = false;
			Log.e("NOTIFICTION IS NOT SET", "  "
					+ isCurrentDateIsGreaterThanEventSchduledDate);

		}

		return isCurrentDateIsGreaterThanEventSchduledDate;
	}

	@SuppressWarnings("deprecation")
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

	// public void setRemindarCustom(String calenderDate, String
	// endcalenderDate,
	// int customHour, int customMin, int endcustomHour, int endcustomMin,
	// String title) {
	//
	// Uri EVENTS_URI = Uri
	// .parse(getCalendarUriBase(getActivity()) + "events");
	// ContentResolver cr = context.getContentResolver();
	//
	// TimeZone timeZone = TimeZone.getDefault();
	// Calendar cal = Calendar.getInstance();
	// try {
	// eventDate = new SimpleDateFormat("MM/dd/yyyy").parse(calenderDate);
	// endEventDate = new SimpleDateFormat("MM/dd/yyyy")
	// .parse(endcalenderDate);
	//
	// cal.setTime(eventDate);
	// cal.set(Calendar.HOUR_OF_DAY, customHour);
	// cal.set(Calendar.MINUTE, customMin);
	// startCalTime = cal.getTimeInMillis();
	//
	// cal.setTime(endEventDate);
	// cal.set(Calendar.HOUR_OF_DAY, endcustomHour);
	// cal.set(Calendar.MINUTE, endcustomMin);
	// endCalTime = cal.getTimeInMillis();
	//
	// Log.e("startCalTime", "" + startCalTime);
	// Log.e("endCalTime", "" + endCalTime);
	// // event insert
	// ContentValues values = new ContentValues();
	// values.put("calendar_id", 1);
	// values.put(CalendarContract.Events.TITLE, title);
	// values.put(CalendarContract.Events.DESCRIPTION, dateToPass);
	// values.put(CalendarContract.Events.EVENT_LOCATION, location);
	// values.put(CalendarContract.Events.DTSTART, startCalTime);
	// values.put(CalendarContract.Events.DTEND, endCalTime);
	// values.put(CalendarContract.Events.ALL_DAY, true);
	// values.put(CalendarContract.Events.STATUS, 1);
	// values.put(CalendarContract.Events.HAS_ALARM, 1);
	// values.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());
	// // values.put(CalendarContract.Events.RRULE, "FREQ=" + "DAILY" +
	// ";UNTIL="
	// // + dtUntilDate(endEventDate));
	// Uri event = cr.insert(EVENTS_URI, values);
	// // reminder insert
	// Uri REMINDERS_URI = Uri.parse(getCalendarUriBase(getActivity())
	// + dtUntilDate(endEventDate));
	// values = new ContentValues();
	// values.put("event_id", Long.parseLong(event.getLastPathSegment()));
	// values.put("method", 1);
	// values.put("minutes", 5);
	// cr.insert(REMINDERS_URI, values);
	// } catch (Exception e) {
	// // TODO: handle exception
	// }
	//
	// }

	private void insertEventToCalendar() {
		long calID = 1;
		long startMillis = 0;
		long endMillis = 0;
		Uri EVENTS_URI = Uri
				.parse(getCalendarUriBase(getActivity()) + "events");
		TimeZone timeZone = TimeZone.getDefault();
		Calendar beginTime = Calendar.getInstance();
		beginTime.set(Integer.parseInt(startyear), startmonthIndex,
				Integer.parseInt(startdatOfWeek), startcustomCalenderHour, startmin);
		
		startMillis = beginTime.getTimeInMillis();
		Calendar endTime = Calendar.getInstance();
		endTime.set(Integer.parseInt(endyear), endmonthIndex,
				Integer.parseInt(enddatOfWeek), endcustomCalenderHour, endmin);
		endMillis = endTime.getTimeInMillis();

		ContentResolver cr = context.getContentResolver();
		ContentValues values = new ContentValues();
		values.put(CalendarContract.Events.DTSTART, startMillis);
		values.put(CalendarContract.Events.DTEND, endMillis);
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
		insertTable.insertValueInRemindertable(clientID, eventName,
				email_String, eventID, true);

		Log.e("EVENT ID MAI KYA HAI", "" + eventID);
	}

	View.OnClickListener l = new OnClickListener() {

		@Override
		public void onClick(View v) {
			setEventOnButtonClick();
		}
	};

	View.OnClickListener dl = new OnClickListener() {

		@Override
		public void onClick(View v) {
			deleteReminderonButtonClick();
		}
	};

	private void deleteReminderonButtonClick() {
		isReminderSavedInDatabase();
		deleteCalendarEntry(set_event_id);
		imageReminder.setVisibility(View.VISIBLE);
		delete_Reminder.setVisibility(View.INVISIBLE);
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
