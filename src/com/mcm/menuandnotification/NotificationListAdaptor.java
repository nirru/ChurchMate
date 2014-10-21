package com.mcm.menuandnotification;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mcm.R;
import com.mcm.SplashActivity;

public class NotificationListAdaptor extends BaseAdapter {

	Context context;
	int layoutResourceId;
	String folderName;
	ArrayList<ArrayList<String>> values;
	String dateToPass, dayOfTheWeek, month, day, year, enddateToPass,
			enddayOfTheWeek, endmonth, endday, endyear;
	int hour, min, endhour, endmin;
	String amPm, endamPm;
	Date customDate, eventStartTime, eventEndTime, eventDate,
	currentSystemDate;
	String email;

	public NotificationListAdaptor(Context context,
			ArrayList<ArrayList<String>> values, int layoutResourceId,
			String folderName, String email) {
		this.context = context;
		this.values = values;
		this.layoutResourceId = layoutResourceId;
		this.folderName = folderName;
		this.email = email;
	}

	@Override
	public int getCount() {
		if (values != null)
			return values.size();
		else
			return 0;
	}

	@Override
	public Object getItem(int arg0) {
		return values.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup parent) {

		View row = convertView;
		ItemHolder holder = null;
		final int position = pos;

		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);
			holder = new ItemHolder();
			row.setTag(holder);

		} else {
			holder = (ItemHolder) row.getTag();
		}
		holder.fg_month_tv = (TextView) row.findViewById(R.id.fg_month);
		holder.fg_date_tv = (TextView) row.findViewById(R.id.fg_date);
		holder.fg_day_tv = (TextView) row.findViewById(R.id.fg_day);
		holder.fg_time_tv = (TextView) row.findViewById(R.id.fg_time);
		holder.fg_title_tv = (TextView) row.findViewById(R.id.fg_title);
		
		holder.fg_long_desc_tv = (TextView) row.findViewById(R.id.fg_long_desc);
		holder.fg_long_desc_tv.setMovementMethod(new ScrollingMovementMethod());
		holder.fg_time_tv.setTypeface(SplashActivity.mpSemiBold);
		holder.fg_title_tv.setTypeface(SplashActivity.mpBold);
		holder.fg_long_desc_tv.setTypeface(SplashActivity.mpSemiBold);
		holder.fg_day_tv.setTypeface(SplashActivity.mpSemiBold);
		holder.fg_date_tv.setTypeface(SplashActivity.mpBold);
		holder.fg_month_tv.setTypeface(SplashActivity.mpSemiBold);

		holder.imageView = (ImageView) row.findViewById(R.id.imageView1);

		convertstringTodate(values.get(position).get(2).toString().trim());
		holder.fg_month_tv.setText(month);
		holder.fg_date_tv.setText(day);
		holder.fg_day_tv.setText(dayOfTheWeek);

		holder.fg_long_desc_tv.setText(values.get(position).get(4).toString()
				.trim());
		holder.fg_title_tv.setText(values.get(position).get(3).toString()
				.trim());
		holder.fg_time_tv.setText(convertstringTotime(values.get(position)
				.get(2).toString().trim()));
		row.setId(position);
		row.setOnClickListener(ll);
		return row;
	}

	View.OnClickListener ll = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int id =  v.getId();
//			Log.e("ID IN RECEIVED", "" + id);
			displayView(id);
		}
	};

	static class ItemHolder {
		TextView fg_month_tv;
		TextView fg_date_tv;
		TextView fg_day_tv;
		TextView fg_time_tv;
		TextView fg_title_tv;
		TextView fg_long_desc_tv;
		ImageView imageView;
		RelativeLayout relative_row;
	}

	private void displayView(int id) {
		String eventName = values.get(id).get(3).toString().trim();
		String eventDateTime = values.get(id).get(2).toString().trim();
		String eventlongDesc = values.get(id).get(4).toString().trim();
		String setReinder = values.get(id).get(6).toString().trim();
		String recurrimgReminder = values.get(id).get(7).toString().trim();
		Fragment fragment = new NotificationDetails(context, eventName,
				eventDateTime, eventlongDesc, dateToPass, day, month, year,
				hour, min, amPm, folderName, setReinder, recurrimgReminder, email);
		if (fragment != null) {
			if (context instanceof FragmentActivity) {
				// We can get the fragment manager
				FragmentActivity activity = (FragmentActivity) context;
				FragmentManager fragmentManager = activity
						.getSupportFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.frame_container, fragment)
						.addToBackStack(null).commit();
			}
		}

	}
	
	private Date getDateTime(String convertDateString) {
		SimpleDateFormat inputFormat24 = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss");
		try {
			customDate = inputFormat24.parse(convertDateString);

			// Log.e("DATE customDate", "" + customDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return customDate;
	}


	private String convertstringTodate(String date_String) {

		String dtStart = date_String;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		Date date = null;
		try {
			date = format.parse(dtStart);
			dtStart = getMonthYear(date, dtStart);
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// System.out.println(date);

		return dtStart;
	}

	private String convertstringTotime(String date_String) {
		String dtStart = "";

		SimpleDateFormat inputFormat24 = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss");
		SimpleDateFormat outputFormatAmPm = new SimpleDateFormat("KK:mm a");
		SimpleDateFormat outputFormatHour = new SimpleDateFormat("KK");
		SimpleDateFormat outputFormatMin = new SimpleDateFormat("mm");
		SimpleDateFormat outputFormatHMAmPm = new SimpleDateFormat("a");
		try {
			Date date = inputFormat24.parse(date_String);
			dtStart = outputFormatAmPm.format(date);
			hour = Integer.parseInt(outputFormatHour.format(date));
			min = Integer.parseInt(outputFormatMin.format(date));
			amPm = outputFormatHMAmPm.format(date);
			// Log.e("DATE HOUR", "" + dtStart);
			return outputFormatAmPm.format(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "";
	}

	private String getMonthYear(Date date, String dateTimeString) {

		Calendar c = Calendar.getInstance();
		c.setTime(date);
		dayOfTheWeek = (String) android.text.format.DateFormat.format("EEEE",
				date);
		month = (String) android.text.format.DateFormat.format("MMM", date);
		day = (String) android.text.format.DateFormat.format("dd", date); // 20
		year = (String) android.text.format.DateFormat.format("yyyy", date); // 20
		// hour = Integer.parseInt((String)
		// android.text.format.DateFormat.format("hh", date)); // 20
		// min = Integer.parseInt((String)
		// android.text.format.DateFormat.format("mm", date)); // 20
		// Log.e("HOUR MIN", "" + hour + "      " + min);

		String textdate = month + "\n" + day + "\n" + dayOfTheWeek;

		String time_Am_Pm = convertstringTotime(dateTimeString);
		dateToPass = time_Am_Pm + " on " + dayOfTheWeek + " " + month + " "
				+ day + "," + year;

		return textdate;
	}

	private String convertstringToEnddate(String date_String) {

		String dtStart = date_String;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		Date date = null;
		try {
			date = format.parse(dtStart);
			dtStart = getMonthYearforEndTime(date, dtStart);
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Log.e("DATE HOUR", "" + date);

		return dtStart;
	}

	private String getMonthYearforEndTime(Date date, String dateTimeString) {

		Calendar c = Calendar.getInstance();
		c.setTime(date);
		enddayOfTheWeek = (String) android.text.format.DateFormat.format(
				"EEEE", date);
		endmonth = (String) android.text.format.DateFormat.format("MMM", date);
		endday = (String) android.text.format.DateFormat.format("dd", date); // 20
		endyear = (String) android.text.format.DateFormat.format("yyyy", date); // 20
		String textdate = endmonth + "\n" + endday + "\n" + enddayOfTheWeek;

		String time_Am_Pm = convertstringTotimeforEndTime(dateTimeString);
		enddateToPass = time_Am_Pm + " on " + enddayOfTheWeek + " " + month
				+ " " + endday + "," + year;

		return textdate;
	}

	private String convertstringTotimeforEndTime(String date_String) {
		String dtStart = "";

		SimpleDateFormat inputFormat24 = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss");
		SimpleDateFormat outputFormatAmPm = new SimpleDateFormat("KK:mm a");
		SimpleDateFormat outputFormatHour = new SimpleDateFormat("KK");
		SimpleDateFormat outputFormatMin = new SimpleDateFormat("mm");
		SimpleDateFormat outputFormatHMAmPm = new SimpleDateFormat("a");
		try {
			Date date = inputFormat24.parse(date_String);
			dtStart = outputFormatAmPm.format(date);
			endhour = Integer.parseInt(outputFormatHour.format(date));
			endmin = Integer.parseInt(outputFormatMin.format(date));
			endamPm = outputFormatHMAmPm.format(date);
			// Log.e("DATE HOUR", "" + date);
			return outputFormatAmPm.format(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "";
	}

}