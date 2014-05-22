package com.tsif.publiccalendarviewer;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.tsif.publiccalendarviewer.PublicCalendarParser.PublicCalendarEntry;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class EventListAdapter extends BaseAdapter {

	private LayoutInflater            _inflater;
	private List<PublicCalendarEntry> _entries;
	private Context                   _context;
	
	public static class ViewHolder {
		TextView title;
		TextView about;
		TextView venue;
		TextView map;
		TextView info;
		TextView when;
	}
	
	public EventListAdapter(Context context) {

		_inflater = LayoutInflater.from(context);
		_entries  = new ArrayList<PublicCalendarEntry>();
		_context  = context;
	}
	
	public List<PublicCalendarEntry> getEntries() {
	    return _entries;	
	}
	
	public void setEntries(List<PublicCalendarEntry> entries) {
		if(entries == null) {
			_entries = new ArrayList<PublicCalendarEntry>();
		} else {
		    _entries = entries;
		}
	}
	
	@Override public int getCount() {
		return _entries.size();
	}

	@Override public PublicCalendarEntry getItem(int position) {
		return (PublicCalendarEntry)_entries.get(position);
	}

	@Override public long getItemId(int position) {
		return position;
	}

	@Override public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder          holder;
		PublicCalendarEntry entry;
		
		entry = (PublicCalendarEntry)_entries.get(position);
		
		if (convertView == null) {
			
			convertView  = _inflater.inflate(R.layout.event_list_item, null);
			holder       = new ViewHolder();
			holder.title = (TextView)convertView.findViewById(R.id.textViewTitle);
			holder.about = (TextView)convertView.findViewById(R.id.textViewAbout);
			holder.info  = (TextView)convertView.findViewById(R.id.textViewInfo);
			holder.map   = (TextView)convertView.findViewById(R.id.textViewMap);
			holder.venue = (TextView)convertView.findViewById(R.id.textViewVenue);
			holder.when  = (TextView)convertView.findViewById(R.id.textViewWhen);
			
			convertView.setTag(holder);
			
		} else {

			holder = (ViewHolder) convertView.getTag();
		}
		
		String bullet = _context.getResources().getString(R.string.bullet) + " ";
		
		holder.title.setText(entry.title);
		
		if(entry.when == null) {
			holder.when.setVisibility(View.GONE);
		} else {
			holder.when.setVisibility(View.VISIBLE);
		    holder.when.setText(bullet + _context.getResources().getString(R.string.str_when) + ": " + getFormattedDate(entry.when));
		}
		
		if(entry.about == null) {
			holder.about.setVisibility(View.GONE);
		} else {
			holder.about.setVisibility(View.VISIBLE);
		    holder.about.setText(bullet +  _context.getResources().getString(R.string.str_about) + ": " + entry.about);
		    
		    ArrayList<String> links = pullLinks(entry.about);
		    for(String link : links) {
		    	final String lnk = link;
		        ClickSpan.clickify(holder.about, link, new ClickSpan.OnClickListener() {
	            @Override public void onClick() {
	        	    Intent intent = new Intent(_context, WebViewActivity.class);
	        	    intent.putExtra("url", lnk);
	        	    _context.startActivity(intent);
	            }
	        });
		}
		}
		
		if(entry.info == null) {
			holder.info.setVisibility(View.GONE);
		} else {
			holder.info.setVisibility(View.VISIBLE);
			holder.info.setText(bullet +  _context.getResources().getString(R.string.str_info) + ": " + entry.info);
			
			ArrayList<String> links = pullLinks(entry.info);
		    for(String link : links) {
		    	final String lnk = link;
		        ClickSpan.clickify(holder.info, link, new ClickSpan.OnClickListener() {
	            @Override public void onClick() {
	        	    Intent intent = new Intent(_context, WebViewActivity.class);
	        	    intent.putExtra("url", lnk);
	        	    _context.startActivity(intent);
	            }
	        });
		}
		}
		
		if(entry.map == null) {
			holder.map.setVisibility(View.GONE);
		} else {
			holder.map.setVisibility(View.VISIBLE);
			holder.map.setText(bullet +  _context.getResources().getString(R.string.str_map) + ": " + entry.map);
			
			ArrayList<String> links = pullLinks(entry.map);
			    for(String link : links) {
			    	final String lnk = link;
			    	ClickSpan.clickify(holder.map, link, new ClickSpan.OnClickListener() {
		            @Override public void onClick() {
		        	    Intent intent = new Intent(_context, WebViewActivity.class);
		        	    intent.putExtra("url", lnk);
		        	    _context.startActivity(intent);
		            }
		        });
			}
		}
	
		if(entry.venue == null) {
			holder.venue.setVisibility(View.GONE);
		} else {
			holder.venue.setVisibility(View.VISIBLE);
			holder.venue.setText(bullet +  _context.getResources().getString(R.string.str_venue) + ": " + entry.venue);
			
			ArrayList<String> links = pullLinks(entry.venue);
		    for(String link : links) {
		    	final String lnk = link;
		    	ClickSpan.clickify(holder.venue, link, new ClickSpan.OnClickListener() {
	            @Override public void onClick() {
	        	    Intent intent = new Intent(_context, WebViewActivity.class);
	        	    intent.putExtra("url", lnk);
	        	    _context.startActivity(intent);
	            }
	        });
		}
		}
		
		return convertView;
	}

	//Pull all links from the body for easy retrieval
	public static ArrayList<String> pullLinks(String text) { ArrayList<String> links = new ArrayList<String>();
	 
	    String regex = "\\(?\\b(http|https)://(www[.])?[-A-Za-z0-9+&amp;@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&amp;@#/%=~_()|]";
   	    Pattern p = Pattern.compile(regex);
	    Matcher m = p.matcher(text);
	
	    while(m.find()) {
	        String urlStr = m.group();
	        if (urlStr.startsWith("(") && urlStr.endsWith(")")) {
	            urlStr = urlStr.substring(1, urlStr.length() - 1);
	        }
	        links.add(urlStr);
	    }
	    return links;
	}
	
	public  static String getFormattedDate(String dateString) {

		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		f.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date             d1     = null;
		try {
			d1 = f.parse(dateString);
		} catch (ParseException e) {
			
			/* one more chance */
			f = new SimpleDateFormat("yyyy-MM-dd");
			f.setTimeZone(TimeZone.getTimeZone("UTC"));
			
			try {
				d1 = f.parse(dateString);
				
				DateFormat dateFormat = new SimpleDateFormat("'On' dd MMMM yyyy");
				return dateFormat.format(d1);
				
			} catch (ParseException ep) {
				return null;
			}
		}

		DateFormat dateFormat = new SimpleDateFormat("'On' dd MMMM yyyy 'at' HH:mm");
		return dateFormat.format(d1);
	}
}
