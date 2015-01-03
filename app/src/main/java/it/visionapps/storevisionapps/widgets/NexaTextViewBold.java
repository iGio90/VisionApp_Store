package it.visionapps.storevisionapps.widgets;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by iGio90 on 10/10/2014.
 */
public class NexaTextViewBold extends TextView {
    private Typeface mNexaBold;

    public NexaTextViewBold(Context context) {
        super(context);

        setup(context);
    }

    public NexaTextViewBold(Context context, AttributeSet attrs) {
        super(context, attrs);

        setup(context);
    }

    public NexaTextViewBold(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setup(context);
    }

    private void setup(Context context) {
        mNexaBold = Typeface.createFromAsset(context.getAssets(), "fonts/nexabold.ttf");

        setTypeface(mNexaBold);
    }

}
