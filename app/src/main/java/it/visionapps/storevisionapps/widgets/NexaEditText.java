package it.visionapps.storevisionapps.widgets;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by iGio90 on 10/10/2014.
 */
public class NexaEditText extends EditText {
    private Typeface mNexa;
    private Typeface mNexaBold;

    public NexaEditText(Context context) {
        super(context);

        setup(context);
    }

    public NexaEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        setup(context);
    }

    public NexaEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setup(context);
    }

    private void setup(Context context) {
        mNexa = Typeface.createFromAsset(context.getAssets(), "fonts/nexalight.ttf");
        mNexaBold = Typeface.createFromAsset(context.getAssets(), "fonts/nexabold.ttf");
        setTextColor(Color.BLACK);

        setTypeface(mNexa);
    }

    public void setBold(boolean bold) {
        setTypeface(bold ? mNexaBold : mNexa);
    }
}
