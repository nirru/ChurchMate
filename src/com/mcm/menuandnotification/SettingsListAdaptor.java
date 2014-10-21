package com.mcm.menuandnotification;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.internal.fo;
import com.google.android.gms.internal.hp;
import com.mcm.R;

public class SettingsListAdaptor extends BaseAdapter {

	Context context;
	int layoutResourceId;
	ArrayList<ArrayList<String>> values;
	ArrayList<ArrayList<String>> interestCheckList;
	ArrayList<Boolean> itemChecked = new ArrayList<Boolean>();
	int memberID;
	ArrayList<String> listSaved;
	ListView listView;
	ArrayList<String> allInterestList, updatedInterestList;
	ArrayList<Integer> matchedPosition;

	public SettingsListAdaptor(Context context,
			ArrayList<ArrayList<String>> values,
			ArrayList<ArrayList<String>> interestCheckList, int memberID,
			int layoutResourceId, ListView listView) {
		// super(context, layoutResourceId);
		this.context = context;
		this.values = values;
		this.interestCheckList = interestCheckList;
		this.layoutResourceId = layoutResourceId;
		this.memberID = memberID;
		this.listView = listView;
		listSaved = new ArrayList<String>();

		allInterestList = new ArrayList<String>();
		updatedInterestList = new ArrayList<String>();
		matchedPosition = new ArrayList<Integer>();

		for (int i = 0; i < values.size(); i++) {
			allInterestList.add(values.get(i).get(1).toString().trim());
		}

		for (int i = 0; i < values.size(); i++) {
			if (i <= interestCheckList.size() - 1) {
				updatedInterestList.add(interestCheckList.get(i).get(1)
						.toString().trim());
			} else {
				updatedInterestList.add("");
			}
		}

		markOldTag();
		markCheckBox();

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

		holder.text1 = (TextView) row.findViewById(R.id.textView1);
		holder.check = (CheckBox) row.findViewById(R.id.checkBox1);

		holder.text1.setText(values.get(position).get(1).toString().trim());
		holder.check.setTag(position);
		holder.check.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				CheckBox cb = (CheckBox) v.findViewById(R.id.checkBox1);
				if (cb.isChecked()) {
					cb.getId();
					itemChecked.set(position, true);
					listSaved.add("" + values.get((Integer) cb.getTag()).get(0));

				} else if (!cb.isChecked()) {
					listSaved.remove(values.get((Integer) cb.getTag()).get(0));
					itemChecked.set(position, false);
				}
				setListSaved(listSaved);
			}
		});
		// Log.e("GSDGS", "GAT");

		holder.check.setChecked(itemChecked.get(position));

		return row;
	}

	public void markOldTag() {
		for (int i = 0; i < updatedInterestList.size(); i++) {
			itemChecked.add(i, false);
		}
	}

	public void markCheckBox() {

		for (int i = 0; i < updatedInterestList.size(); i++) {
			for (int j = 0; j < allInterestList.size(); j++) {
				if (updatedInterestList.get(i).trim()
						.equals(allInterestList.get(j).trim())) {
					matchedPosition.add(j);
					itemChecked.set(j, true);
					listSaved.add("" + values.get(j).get(0));
				} else {

				}
			}
		}

	}

	static class ItemHolder {
		TextView text1;
		CheckBox check;
	}

	public ArrayList<String> getListSaved() {
		return listSaved;
	}

	public void setListSaved(ArrayList<String> listSaved) {
		this.listSaved = listSaved;
	}

}