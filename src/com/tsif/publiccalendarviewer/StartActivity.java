package com.tsif.publiccalendarviewer;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

import com.httpclienthelper.RequestNotification;
import com.tsif.publiccalendarviewer.CommunitiesParser.CommunityEntry;
import com.tsif.publiccalendarviewer.PublicCalendarParser.PublicCalendarEntry;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

public class StartActivity extends FragmentActivity implements ActionBar.TabListener  {

	protected ViewPager            _pager;
	protected SectionsPagerAdapter _adapter;
	protected Context              _context;
	
	@Override protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		_context = this;

		if(savedInstanceState!=null){
            Log.d("counts", "ec: " + savedInstanceState.getInt("eventCount") + " cc: " + savedInstanceState.getInt("communityCount"));
        }
		
		_adapter = new SectionsPagerAdapter(getSupportFragmentManager());
		_pager = (ViewPager)findViewById(R.id.pager);
		_pager.setAdapter(_adapter);

		_pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
			}
		});

		for (int i = 0; i < _adapter.getCount(); i++) {
			actionBar.addTab(actionBar.newTab().setText(_adapter.getPageTitle(i)).setTabListener(this));
		}
	}
	
	@Override public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.event_list, menu);
		return true;
	}

	@Override public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		    case R.id.refresh:
		    	
		    	ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBarCalendar);
		    	progressBar.setVisibility(View.INVISIBLE);
				new Quickening(this).fadeIn(progressBar);
				
				progressBar = (ProgressBar)findViewById(R.id.progressBarCommunities);
		    	progressBar.setVisibility(View.INVISIBLE);
				new Quickening(this).fadeIn(progressBar);
				
                _adapter.getEventsFragment().refresh();
                _adapter.getCommunitiesFragment().refresh();
		    	
			    break;
 
		    default:
			    return super.onOptionsItemSelected(item);
		}
		return true;
	}
	
	@Override protected void onSaveInstanceState(Bundle outState) {
	    
		super.onSaveInstanceState(outState);

	    try {
	    	
	    	EventsFragment ef = _adapter.getEventsFragment();
	    	CommunitiesFragment cf = _adapter.getCommunitiesFragment();
	    	
	        outState.putInt("eventCount", ef.getEntries().size());
	        outState.putInt("communityCount", cf.getEntries().size());
	    } catch (Exception e) {}
	}

	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		EventsFragment      _eventsFragment;
		CommunitiesFragment _communitiesFragmnet;
		
		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		public EventsFragment getEventsFragment() {
		    return _eventsFragment;	
		}
	
		public CommunitiesFragment getCommunitiesFragment() {
		    return _communitiesFragmnet;	
		}
		
		@Override public Fragment getItem(int position) {
			
			Bundle   args     = new Bundle();
			Fragment fragment;
			if (position == 0) {
		    
		    	fragment = new EventsFragment();
		    	args.putInt(EventsFragment.ARG_SECTION_NUMBER, position + 1);
		    	
		    	_eventsFragment = (EventsFragment)fragment;
		    	
			} else {
				
		    	fragment = new CommunitiesFragment();
		    	args.putInt(CommunitiesFragment.ARG_SECTION_NUMBER, position + 1);
		    	
		    	_communitiesFragmnet = (CommunitiesFragment)fragment;
		    }
			fragment.setArguments(args);
			return fragment;
		}

		@Override public int getCount() {
			return 2;
		}

		@Override public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			    case 0:
				    return getString(R.string.events).toUpperCase(l);
			    case 1:
				    return getString(R.string.communities).toUpperCase(l);
			}
		    return null;
		}
	}
	
	public static class CommunitiesFragment extends Fragment implements Observer {
		
		public    static final String             ARG_SECTION_NUMBER = "section_number";
		protected              View               _rootView;
		protected              DataManager        _manager;
		protected              CommunitiesAdapter _adapter;
		protected              ListView           _listview;
		protected              LayoutInflater     _inflater;
		protected              View               _footerView;
		
		@Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			
			super.onCreate(savedInstanceState);
			
			_rootView = inflater.inflate(R.layout.communities_list, container, false);
			
			_retrieveCommunities();
			
			_adapter  = new CommunitiesAdapter(getActivity());
			_listview = (ListView)_rootView.findViewById(R.id.communitiesListView);
			_listview.setAdapter(_adapter);
			
			_inflater               = LayoutInflater.from(getActivity());
			View           header   = _inflater.inflate(R.layout.community_list_header, null);
			_listview.addHeaderView(header);
			
			ProgressBar progressBar = (ProgressBar)_rootView.findViewById(R.id.progressBarCommunities);
			progressBar.setVisibility(View.INVISIBLE);
			new Quickening().fadeIn(progressBar);
			
			return _rootView;
		}
		
        protected void _retrieveCommunities() {
			
        	if(_manager != null) {
				_manager.abort();	
			}
        	
			_manager = new DataManager();
			_manager.setObserver(this);
			_manager.addObserver(DataManager.COMMUNITIES_SUCCESS);
			_manager.addObserver(DataManager.COMMUNITIES_FAIL);
			_manager.addObserver(DataManager.COMMUNITIES_BAD);
			
			_manager.retrieveCommunities(getResources().getString(R.string.communities_url));
			
			return;
		}
 
    	public List<CommunityEntry> getEntries() {
    	    return _adapter.getEntries();	
    	}
    	
        public void refresh() {
			
			_listview.removeFooterView(_footerView);
			
	    	_adapter.setEntries(null);
	    	_adapter.notifyDataSetChanged();
	    	
	    	_retrieveCommunities();
		}

		@Override public void update(Observable observable, Object object) {
			
			RequestNotification requestnotification = (RequestNotification)object;
			String              requestname         = requestnotification.getRequestName();
			
			if(requestname.equals(DataManager.COMMUNITIES_SUCCESS)) {
			    
				@SuppressWarnings("unchecked")
				ArrayList<CommunityEntry> list = (ArrayList<CommunityEntry>)requestnotification.getValues().get("entries");
				
				_adapter.setEntries(list);
	            _adapter.notifyDataSetChanged();
	            
	            View           footer   = _inflater.inflate(R.layout.event_list_footer, null);
				_listview.addFooterView(footer);
				
				_footerView = footer;
			}
			
			ProgressBar progressBar = (ProgressBar)_rootView.findViewById(R.id.progressBarCommunities);
			progressBar.setVisibility(View.INVISIBLE);
			new Quickening().fadeOut(progressBar);
		}
	}
	
	public static class EventsFragment extends Fragment implements Observer {

		public    static final String           ARG_SECTION_NUMBER = "section_number";
		protected              DataManager      _manager;
		protected              EventListAdapter _adapter;
		protected              ListView         _listview;
		protected              View             _rootView;
		protected              LayoutInflater   _inflater;
		protected              View             _footerView;
		
		public EventsFragment() {}

		@Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			
			super.onCreate(savedInstanceState);
			
			_rootView = inflater.inflate(R.layout.event_list, container, false);
			
			_retrievePublicCalendar();
			
			_adapter  = new EventListAdapter(getActivity());
			_listview = (ListView)_rootView.findViewById(R.id.eventsListView);
			_listview.setAdapter(_adapter);
			
			_inflater               = LayoutInflater.from(getActivity());
			View           header   = _inflater.inflate(R.layout.event_list_header, null);
			_listview.addHeaderView(header);
			
			ProgressBar progressBar = (ProgressBar)_rootView.findViewById(R.id.progressBarCalendar);
			progressBar.setVisibility(View.INVISIBLE);
			new Quickening().fadeIn(progressBar);
			
			Button b = (Button)header.findViewById(R.id.buttonShare);
			b.setOnClickListener(new View.OnClickListener() {
				@Override public void onClick(View v) {
					_share();
				}
				
				protected void _share() {

					String text        = getResources().getString(R.string.share_text);
					Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);

					shareIntent.setType("text/plain");
					shareIntent.putExtra(Intent.EXTRA_SUBJECT, text);
					shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, text);

					startActivity(Intent.createChooser(shareIntent, "Share via"));

				}
			});
			
			return _rootView;
		}
		 
		public List<PublicCalendarEntry> getEntries() {
		    return _adapter.getEntries();	
		}
		
		public void refresh() {
			
			_listview.removeFooterView(_footerView);
			
	    	_adapter.setEntries(null);
	    	_adapter.notifyDataSetChanged();
	    	
	    	_retrievePublicCalendar();
		}
		
		protected void _retrievePublicCalendar() {
			
			if(_manager != null) {
				_manager.abort();	
			}
			
			_manager = new DataManager();
			_manager.setObserver(this);
			_manager.addObserver(DataManager.CALENDAR_SUCCESS);
			_manager.addObserver(DataManager.CALENDAR_FAIL);
			_manager.addObserver(DataManager.CALENDAR_BAD);
			
			_manager.retrieveCalendar(getResources().getString(R.string.google_calendar_id));
			
			return;
		}

		@Override public void update(Observable observable, Object object) {
			
			RequestNotification requestnotification = (RequestNotification)object;
			String              requestname         = requestnotification.getRequestName();
			
			if(requestname.equals(DataManager.CALENDAR_SUCCESS)) {
				
				@SuppressWarnings("unchecked")
				ArrayList<PublicCalendarEntry> list = (ArrayList<PublicCalendarEntry>)requestnotification.getValues().get("entries");			
	            _adapter.setEntries(list);
	            _adapter.notifyDataSetChanged();
	            
	            View           footer   = _inflater.inflate(R.layout.event_list_footer, null);
				_listview.addFooterView(footer);
				
				_footerView = footer;
			}

			ProgressBar progressBar = (ProgressBar)_rootView.findViewById(R.id.progressBarCalendar);
			progressBar.setVisibility(View.INVISIBLE);
			new Quickening().fadeOut(progressBar);
		} 
	}

	@Override public void onTabReselected(Tab tab, FragmentTransaction ft) {}
	
	@Override public void onTabSelected(Tab tab, FragmentTransaction ft) {
		_pager.setCurrentItem(tab.getPosition(), true);
	}
	
	@Override public void onTabUnselected(Tab tab, FragmentTransaction ft) {}
	
}
	
