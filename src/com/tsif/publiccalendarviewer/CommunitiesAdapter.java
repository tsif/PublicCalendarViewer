package com.tsif.publiccalendarviewer;

import java.util.ArrayList;
import java.util.List;

import com.tsif.publiccalendarviewer.CommunitiesParser.CommunityEntry;
import com.tsif.publiccalendarviewer.CommunitiesParser.Maintainer;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CommunitiesAdapter extends BaseAdapter {

	private LayoutInflater       _inflater;
	private List<CommunityEntry> _entries;
	private Context            	 _context;
	
	public static class ViewHolder {
		TextView community;
		TextView period;
		TextView at;
		TextView web;
		TextView description;
		TextView maintainers;
	}
	
	public CommunitiesAdapter(Context context) {

		_inflater = LayoutInflater.from(context);
		_entries  = new ArrayList<CommunityEntry>();
		_context  = context;
	}
	
	public List<CommunityEntry> getEntries() {
	    return _entries;	
	}
	
	public void setEntries(List<CommunityEntry> entries) {
		if(entries == null) {
			_entries = new ArrayList<CommunityEntry>();
		} else {
		    _entries = entries;
		}
	}
	
	@Override public int getCount() {
		return _entries.size();
	}

	@Override public CommunityEntry getItem(int position) {
		return (CommunityEntry)_entries.get(position);
	}

	@Override public long getItemId(int position) {
		return position;
	}
	
	@Override public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder     holder;
		CommunityEntry entry;
		
		entry = (CommunityEntry)_entries.get(position);
		
		if (convertView == null) {
			
			convertView  = _inflater.inflate(R.layout.community_list_item, null);
			holder       = new ViewHolder();
			
			holder.community   = (TextView)convertView.findViewById(R.id.textViewCommunity);
			holder.period      = (TextView)convertView.findViewById(R.id.textViewPeriod);
			holder.at          = (TextView)convertView.findViewById(R.id.textViewAt);
			holder.web         = (TextView)convertView.findViewById(R.id.textViewWeb);
			holder.description = (TextView)convertView.findViewById(R.id.textViewDescription);
			holder.maintainers = (TextView)convertView.findViewById(R.id.textViewMaintainers);
			
			convertView.setTag(holder);
			
		} else {

			holder = (ViewHolder) convertView.getTag();
		}
		
		String bullet = _context.getResources().getString(R.string.bullet) + " ";
		
		if(entry.name == null) {
			holder.community.setVisibility(View.GONE);
		} else {
			holder.community.setVisibility(View.VISIBLE);
		    holder.community.setText(entry.name);
		}
		
		if(entry.period == null) {
			holder.period.setVisibility(View.GONE);
		} else {
			holder.period.setVisibility(View.VISIBLE);
			holder.period.setText(bullet + _context.getResources().getString(R.string.str_period) + ": " + entry.period);
		}
		
		if(entry.happensAt == null) {
			holder.at.setVisibility(View.GONE);
		} else {
			holder.at.setVisibility(View.VISIBLE);
			holder.at.setText(bullet + _context.getResources().getString(R.string.str_at) + ": " + entry.happensAt);
		}
		
		if(entry.website == null) {
			holder.web.setVisibility(View.GONE);
		} else {
			holder.web.setVisibility(View.VISIBLE);
			holder.web.setText(bullet + _context.getResources().getString(R.string.str_web) + ": " + entry.website);
			
			ArrayList<String> links = EventListAdapter.pullLinks(entry.website);
		    for(String link : links) {
		    	final String lnk = link;
		        ClickSpan.clickify(holder.web, link, new ClickSpan.OnClickListener() {
	            @Override public void onClick() {
	        	    Intent intent = new Intent(_context, WebViewActivity.class);
	        	    intent.putExtra("url", lnk);
	        	    _context.startActivity(intent);
	            }
	        });
		    }
		}
		
		if(entry.description == null) {
			holder.description.setVisibility(View.GONE);
		} else {
			holder.description.setVisibility(View.VISIBLE);
			holder.description.setText(bullet + _context.getResources().getString(R.string.str_description) + ": " + entry.description);
		}
		
		holder.maintainers.setText(_context.getResources().getString(R.string.str_maintainers) + "\n");
		ArrayList<CommunitiesParser.Maintainer> maintainers = (ArrayList<Maintainer>) entry.maintainers;
		for(Maintainer m : maintainers) {
			String ms = (String) holder.maintainers.getText();
			holder.maintainers.setText(ms +"\n\t" + bullet + m.name + "\n");
		}
		
		return convertView;
	}

}
