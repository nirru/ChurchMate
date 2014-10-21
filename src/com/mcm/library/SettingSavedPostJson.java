package com.mcm.library;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.mcm.appconstant.AppConstant;

import android.util.Log;

public class SettingSavedPostJson {

	int clientId, memberId, pushNotificationId;
	boolean response;
	String url;
	ArrayList<String> savedCheckedItem;

	public SettingSavedPostJson(String url, int memberId,
			ArrayList<String> savedCheckedItem) {
		this.url = url;
		this.memberId = memberId;
		this.savedCheckedItem = savedCheckedItem;
		Log.e("SAVED CHECKED ITEM", " " + savedCheckedItem);
	}

	public String postDataToServer() {

		InputStream inputStream = null;
		String result = "";
		try {
			// 1. create HttpClient
			HttpClient httpclient = new DefaultHttpClient();

			// 2. make POST request to the given URL
			HttpPost httpPost = new HttpPost(url);

			StringBuilder ad = new StringBuilder();
			String json = "";
			// 3. build jsonObject
			for (int i = 0; i < savedCheckedItem.size(); i++) {
				JSONObject jsonObject = new JSONObject();
				;
				jsonObject.accumulate("MemberId", "" + memberId);
				jsonObject.accumulate("InterestId",
						"" + savedCheckedItem.get(i));

				if (i == savedCheckedItem.size() - 1) {
					ad.append(jsonObject.toString());
				} else {
					ad.append(jsonObject.toString() + ",");
				}
			}

			// 4. convert JSONObject to JSON to String
			json = "[" + ad.toString() + "]";
			
//			Log.e("<<Post JSON IN ARRAY>>", "" + json);
			
			// 5. set json to StringEntity
			StringEntity se = new StringEntity(json);

			// 6. set httpPost Entity
			httpPost.setEntity(se);

			// 7. Set some headers to inform server about the type of the
			// content
			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");

			// 8. Execute POST request to the given URL
			HttpResponse httpResponse = httpclient.execute(httpPost);
			Log.e("RESPONSE FROM SERVER", ""
					+ httpResponse.getStatusLine().getStatusCode());

			// 9. receive response as inputStream
			inputStream = httpResponse.getEntity().getContent();

			// 10. convert inputstream to string
			if (inputStream != null)
				result = convertInputStreamToString(inputStream);
			else
				result = "Did not work!";

		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}

		// 11. return result
		// Log.e("RESULT", "" + result);
		return result;
	}

	private String convertInputStreamToString(InputStream inputStream)
			throws IOException {
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(inputStream));
		String line = "";
		String result = "";
		while ((line = bufferedReader.readLine()) != null)
			result += line;

		inputStream.close();
		return result;
	}
}
