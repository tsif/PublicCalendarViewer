package com.tsif.publiccalendarviewer;

import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.json.JSONException;

import com.httpclienthelper.HttpClientBuilder;
import com.httpclienthelper.HttpClientManager;
import com.httpclienthelper.QueryStringBuilder;
import com.httpclienthelper.RequestAttributes;
import com.httpclienthelper.RequestNotification;
import com.httpclienthelper.Utilities;

public class DataManager extends HttpClientManager {

	public static final String CALENDAR_TAG        = "calendarTag";
	public static final String CALENDAR_SUCCESS    = "calendarSuccess";
	public static final String CALENDAR_FAIL       = "calendarFail";
	public static final String CALENDAR_BAD        = "calendarBad";

	public static final String COMMUNITIES_TAG     = "communitiesTag";
	public static final String COMMUNITIES_SUCCESS = "communitiesSuccess";
	public static final String COMMUNITIES_FAIL    = "communitiesFail";
	public static final String COMMUNITIES_BAD     = "communitiesBad";
	
	public void retrieveCalendar(String id) { 

		try {
			
			QueryStringBuilder builder = new QueryStringBuilder();
			builder.add("alt", "json");
			builder.add("orderby", "starttime");
		    builder.add("results", 30);
		    builder.add("singleevents", true);
		    builder.add("sortorder", "ascending"); 
		    builder.add("futureevents", true); 
		
		    String             url        = "http://www.google.com/calendar/feeds/" + id + "/public/full?" + builder.toString();
			RequestAttributes  attributes = new RequestAttributes(url, CALENDAR_TAG);
			attributes.addHeader("Content-Type", "application/json");
			attributes.setMethod(HttpClientBuilder.HTTP_GET_METHOD);
			attributes.addValue("format", "json");
			
			execute(attributes); 
			
		} catch(UnsupportedEncodingException e) {}
	}
	
	public void retrieveCommunities(String url) { 
		
		RequestAttributes  attributes = new RequestAttributes(url, COMMUNITIES_TAG);
		attributes.addHeader("Content-Type", "application/json");
		attributes.setMethod(HttpClientBuilder.HTTP_GET_METHOD);
		attributes.addValue("format", "yaml");
			
		execute(attributes); 
	}
	
	@Override public RequestNotification parseResult(HttpResponse result, RequestAttributes attributes) {

		RequestNotification requestnotification = new RequestNotification();
		
		if(!isRunning()) {
			return null;
			
		} else if(result == null) {
			
			requestnotification.setStatusCode(HttpClientBuilder.STATUS_CODE_ERROR);
			assignNotificationName(HttpClientBuilder.STATUS_CODE_ERROR, attributes.getTag(), requestnotification);
			return requestnotification;
		}
		
		int statuscode = result.getStatusLine().getStatusCode();
		
		requestnotification.setStatusCode(statuscode);
		assignNotificationName(statuscode, attributes.getTag(), requestnotification);

		if(HttpClientBuilder.statusGood(statuscode)) {
			
			String format = (String) attributes.getValue("format");
			if(format.equals("yaml")) {
				
				try {
				    CommunitiesParser parser     = new CommunitiesParser(Utilities.extractJsonAsString(result.getEntity()));
				    requestnotification.addValueForKey("entries", parser.getEntries());
			    } catch (JSONException e) { e.printStackTrace();}
				
			} else if(format.equals("json")) {
			
				try {
				    PublicCalendarParser parser     = new PublicCalendarParser(Utilities.extractJsonAsString(result.getEntity()));
				    requestnotification.addValueForKey("entries", parser.getEntries());
			    } catch (JSONException e) {}
			}
		}
        
		return requestnotification;	
	}
	
	@Override public String successNotification(String tag) {
		
		if(tag.equals(CALENDAR_TAG)) {
			return CALENDAR_SUCCESS;
		} else if(tag.equals(COMMUNITIES_TAG)) {
			return COMMUNITIES_SUCCESS;
		}
		return EMPTY_STRING;
	}

	@Override public String failNotification(String tag) {		
		 
        if(tag.equals(CALENDAR_TAG)) {
        	return CALENDAR_FAIL;
		} else if(tag.equals(COMMUNITIES_TAG)) {
			return COMMUNITIES_FAIL;
		}
		return EMPTY_STRING;
	}

	@Override public String badNotification(String tag) {
		
        if(tag.equals(CALENDAR_TAG)) {
        	return CALENDAR_BAD;
		} else if(tag.equals(COMMUNITIES_TAG)) {
			return COMMUNITIES_BAD;
		}
		return EMPTY_STRING;
	}
}
