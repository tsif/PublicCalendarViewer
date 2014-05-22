package com.tsif.publiccalendarviewer;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

public class ClickSpan extends ClickableSpan {

	private OnClickListener _listener;

	public ClickSpan(OnClickListener listener) {
		_listener = listener;
	}

	@Override public void onClick(View widget) {
		if (_listener != null) _listener.onClick();
	}

	public interface OnClickListener {
		void onClick();
	}

	@Override
    public void updateDrawState(TextPaint ds) {
        ds.linkColor = 0xFF0645AD;
        super.updateDrawState(ds);
    }
	
	public static void clickify(TextView view, final String clickableText, final ClickSpan.OnClickListener listener) {

		CharSequence text = view.getText();
		String string = text.toString();
		ClickSpan span = new ClickSpan(listener);

		int start = string.indexOf(clickableText);
		int end = start + clickableText.length();
		if (start == -1) return;

		if (text instanceof Spannable) {
			((Spannable)text).setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			
		} else {
			
			SpannableString s = SpannableString.valueOf(text);
			s.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			view.setText(s);
		}

		MovementMethod m = view.getMovementMethod();
		if ((m == null) || !(m instanceof LinkMovementMethod)) {
			view.setMovementMethod(LinkMovementMethod.getInstance());
		}
	}
}

