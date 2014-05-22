package com.tsif.publiccalendarviewer;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;

@SuppressLint("DefaultLocale")
public class PublicCalendarParser {

	class PublicCalendarEntry {
		
		public String title;
		public String content;
		public String about;
		public String venue;
		public String map;
		public String info;
		public String when;
	};
	
	private JSONArray _array; 
	
	public PublicCalendarParser(String jsonInput) throws JSONException {
	
		JSONObject object = new JSONObject(jsonInput);
		JSONObject feed   = object.getJSONObject("feed");
		_array            = feed.getJSONArray("entry");
	}
	
	public ArrayList<PublicCalendarEntry> getEntries() {
		
		ArrayList<PublicCalendarEntry> list = new ArrayList<PublicCalendarEntry>();
		for(int i = 0; i < _array.length(); i++){
		 
			try {

				JSONObject          jsonObj = _array.getJSONObject(i);
				PublicCalendarEntry entry   = new PublicCalendarEntry();
				entry.title                 = jsonObj.getJSONObject("title").getString("$t");
				entry.content               = jsonObj.getJSONObject("content").getString("$t");
				entry.when                  = jsonObj.getJSONArray("gd$when").getJSONObject(0).getString("startTime");

				try {
					
					String[] separated = entry.content .split("\n"); 
					for(String s : separated) {

						StringTokenizer tokens = new StringTokenizer(s, ":");
						String          result = "";

						String first = tokens.nextToken();
						while(tokens.hasMoreTokens()) {
							
							String second =  tokens.nextToken();
							result        += second;

							if(tokens.hasMoreTokens()) {
								result += ":";
							}
						}
						result = result.trim();

						if(first.toLowerCase().equals("about")) {
							entry.about = result;
						} else if(first.toLowerCase().equals("venue")) {
							entry.venue = result;
						} else if(first.toLowerCase().equals("map")) {
							entry.map = result;
						} else if(first.toLowerCase().equals("info")) {
							entry.info = result;
						} 

						list.add(entry);
					}
				} catch (java.util.NoSuchElementException e) {
				}


			} catch (JSONException e) {}
		}
		return list;
	}
}
