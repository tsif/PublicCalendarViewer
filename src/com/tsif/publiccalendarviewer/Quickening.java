package com.tsif.publiccalendarviewer;

import java.util.Random;

import android.app.Activity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ProgressBar;

public class Quickening {

	private Activity _activity;
	
	////////////////////////////
	// Constructors
	////////////////////////////
	
	public Quickening(Activity activity) {
		_activity = activity;
	}
	
	public Quickening() {}
	
	////////////////////////////
	// Animations
	////////////////////////////
	
	public void spiralAnimate(Integer viewId) {
		
		Integer duration = 500;	
		View    view     = _activity.findViewById(viewId);
		
		Animation fadeOut = new AlphaAnimation(1, 0);
		fadeOut.setInterpolator(new AccelerateInterpolator());
		fadeOut.setStartOffset(0);
		fadeOut.setDuration(duration);

		AnimationSet animation = new AnimationSet(false);
		animation.addAnimation(fadeOut);
		
		view.setAnimation(animation);
		view.postDelayed(new PrivateRunnable(viewId) {
		    @Override public void run() {
		    	ProgressBar progressBar = (ProgressBar)_activity.findViewById(_id);
		    	progressBar.setVisibility(View.INVISIBLE);
		    }
		}, duration);
	}
	
	public void fadeOut(Integer viewId) {
		
		Integer duration = 500;	
		View    view     = _activity.findViewById(viewId);
		
		Animation fadeOut = new AlphaAnimation(1, 0);
		fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
		fadeOut.setStartOffset(0);
		fadeOut.setDuration(duration);

		AnimationSet animation = new AnimationSet(false); //change to false
		animation.addAnimation(fadeOut);
		
		view.setAnimation(animation);
		view.postDelayed(new PrivateRunnable(viewId) {
		    @Override public void run() {
		    	View view = _activity.findViewById(_id);
		    	view.setVisibility(View.INVISIBLE);
		    }
		}, duration);
	}
	
	public void fadeIn(Integer viewId) {
		
		Integer duration = 500;	
		View    view     = _activity.findViewById(viewId);
		view.setVisibility(View.VISIBLE);
		
		Animation fadeOut = new AlphaAnimation(0, 1);
		fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
		fadeOut.setStartOffset(0);
		fadeOut.setDuration(duration);

		AnimationSet animation = new AnimationSet(false); //change to false
		animation.addAnimation(fadeOut);
		
		view.setAnimation(animation);
	}
	
    public void fadeIn(View view) {
		
		Integer duration = 500;	
		view.setVisibility(View.VISIBLE);
		
		Animation fadeOut = new AlphaAnimation(0, 1);
		fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
		fadeOut.setStartOffset(0);
		fadeOut.setDuration(duration);

		AnimationSet animation = new AnimationSet(false); //change to false
		animation.addAnimation(fadeOut);
		
		view.setAnimation(animation);
	}

    public void fadeOut(View view) {
		
		Integer duration = 500;	
		view.setVisibility(View.VISIBLE);
		
		Animation fadeOut = new AlphaAnimation(1, 0);
		fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
		fadeOut.setStartOffset(0);
		fadeOut.setDuration(duration);

		AnimationSet animation = new AnimationSet(false); //change to false
		animation.addAnimation(fadeOut);
		
		view.setAnimation(animation);
		final View v = view;
		view.postDelayed(new Runnable() {
		    @Override public void run() {
		    	v.setVisibility(View.INVISIBLE);
		    }
		}, duration);
	}

	public void scale(Integer viewId, Float factor) {
		
		Integer duration = 500;	
		View    view     = _activity.findViewById(viewId);
		
		Animation scale = new ScaleAnimation(1.0f, factor,  1.0f, factor, view.getWidth() / 2.0f, view.getHeight() / 2.0f);
		scale.setInterpolator(new DecelerateInterpolator(0.5f));
		scale.setStartOffset(0);
		scale.setDuration(duration);

		AnimationSet animation = new AnimationSet(false); //change to false
		animation.addAnimation(scale);
		animation.setFillAfter(true);
		
		view.setAnimation(animation);
		view.postDelayed(new PrivateRunnable(viewId) {
		    @Override public void run() {
		    	View view = _activity.findViewById(_id);
		    	view.setVisibility(View.INVISIBLE);
		    }
		}, duration);
	}
	
	public void animateListViewRow(View view) {
		
		int          duration  = 500;
		AnimationSet animation = new AnimationSet(false); //change to false

		Animation scale = new ScaleAnimation(0.5f, 1.0f,  0.5f, 1.0f);
		scale.setInterpolator(new DecelerateInterpolator(0.5f));
		scale.setStartOffset(0);
		scale.setDuration(duration);
		animation.addAnimation(scale);
				
		Animation fadeIn = new AlphaAnimation(0.25f, 1.0f);
		fadeIn.setInterpolator(new AccelerateInterpolator());
		fadeIn.setStartOffset(0);
		fadeIn.setDuration(duration);
		animation.addAnimation(fadeIn);		
		
		Animation translate = new TranslateAnimation(view.getWidth() / 4.0f, 0.0f, 0.0f, 0.0f);
		translate.setInterpolator(new DecelerateInterpolator());
		translate.setStartOffset(0);
		translate.setDuration(duration);
		animation.addAnimation(translate);		
		
		view.setAnimation(animation);
	}
	
	public void fanAnimateListViewRow(View view, Boolean direction) {
		
		int          duration  = 250;
		AnimationSet animation = new AnimationSet(false);
				
		Animation rotate = null;
		if(direction == true) {
			rotate = new RotateAnimation(45.0f, 0.0f);
		} else {
			rotate = new RotateAnimation(45.0f, 0.0f, view.getWidth(), 0.0f);
		}
		rotate.setInterpolator(new AccelerateInterpolator());
		rotate.setStartOffset(0);
		rotate.setDuration(duration);
		animation.addAnimation(rotate);	
		
		Animation fadeIn = new AlphaAnimation(0.0f, 1.0f);
		fadeIn.setInterpolator(new AccelerateInterpolator());
		fadeIn.setStartOffset(0);
		fadeIn.setDuration(duration);
		animation.addAnimation(fadeIn);	
		
		view.setAnimation(animation);
	}
	
	public void shuffleIn(View view) {
		
		view.setVisibility(View.VISIBLE);
		
		int          duration  = 250 + new Random().nextInt(1500);
		AnimationSet animation = new AnimationSet(false);
		
		Animation fadeIn = new AlphaAnimation(0.0f, 1.0f);
		fadeIn.setStartOffset(0);
		fadeIn.setDuration(duration);
		animation.addAnimation(fadeIn);	
		
		view.setAnimation(animation);
		
	}
	
	public void shuffleOut(final View view) {
		
		int          duration  = 750;
		AnimationSet animation = new AnimationSet(false);
		
		Animation fadeOut = new AlphaAnimation(1.0f, 0.1f);
		fadeOut.setInterpolator(new AccelerateInterpolator());
		fadeOut.setStartOffset(0);
		fadeOut.setDuration(duration);
		fadeOut.setFillAfter(true);
		fadeOut.setFillEnabled(true);
		animation.addAnimation(fadeOut);	
		
		view.setAnimation(animation);
		view.postDelayed(new Runnable() {
		    @Override public void run() {
		    	view.setVisibility(View.INVISIBLE);
		    }
		}, duration);
	}
	
	////////////////////////////
	// Support
	////////////////////////////
	
	private class PrivateRunnable implements Runnable {
		
		Integer _id;
		public PrivateRunnable(Integer id) {
			_id = id;
		}
		
		@Override
		public void run() {			
		}
	}
}
