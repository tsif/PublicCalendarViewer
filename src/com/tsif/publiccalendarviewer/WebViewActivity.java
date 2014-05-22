package com.tsif.publiccalendarviewer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewActivity extends Activity {
	 
	private WebView webView;
 
	@SuppressLint("SetJavaScriptEnabled")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_web);
 
		if(getIntent().hasExtra("url")) {
		    String url = getIntent().getExtras().getString("url");
		    
		    webView = (WebView) findViewById(R.id.webView1);
			webView.getSettings().setJavaScriptEnabled(true);
			webView.loadUrl(url);
			webView.setWebViewClient(new PrivateBrowser(this));
			
			setProgressBarIndeterminateVisibility(true);
		    overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
		
		} else {
			finish();
		}
	}
 
    @Override public void onBackPressed() {
		
		super.onBackPressed();
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}

	private class PrivateBrowser extends WebViewClient {
	
		protected Activity _activity;
		
	    public PrivateBrowser(Activity a) {
		   super();
		   
		   _activity = a;
	    }
	    @Override public boolean shouldOverrideUrlLoading(WebView view, String url) {
	    	
	    	_activity.setProgressBarIndeterminateVisibility(true);
            
	        view.loadUrl(url);
		    return true;
	    }
	   
	    @Override public void onPageFinished(WebView view, String url) {
		   
	    	_activity.setProgressBarIndeterminateVisibility(false);
	    }
	}
}
