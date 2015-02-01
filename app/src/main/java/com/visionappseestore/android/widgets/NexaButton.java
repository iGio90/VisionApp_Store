package com.visionappseestore.android.widgets;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by iGio90 on 10/10/2014.
 */
public class NexaButton extends Button {
    private Typeface mNexa;
    private Typeface mNexaBold;

    public NexaButton(Context context) {
        super(context);

        setup(context);
    }

    public NexaButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        setup(context);
    }

    public NexaButton(Context context, AttributeSet attrs, int defStyle) {
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
