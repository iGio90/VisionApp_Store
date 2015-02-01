package com.visionappseestore.android.widgets;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by iGio90 on 10/10/2014.
 */
public class NexaTextView extends TextView {
    private Typeface mNexa;
    private Typeface mNexaBold;

    public NexaTextView(Context context) {
        super(context);

        setup(context);
    }

    public NexaTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setup(context);
    }

    public NexaTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setup(context);
    }

    private void setup(Context context) {
        mNexa = Typeface.createFromAsset(context.getAssets(), "fonts/nexalight.ttf");
        mNexaBold = Typeface.createFromAsset(context.getAssets(), "fonts/nexabold.ttf");

        setTypeface(mNexa);
    }

    public void setBold(boolean bold) {
        setTypeface(bold ? mNexaBold : mNexa);
    }
}
