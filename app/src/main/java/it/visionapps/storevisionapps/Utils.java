package it.visionapps.storevisionapps;

import android.content.res.Resources;
import android.util.TypedValue;

/**
 * Created by iGio90 on 03/01/15.
 */
public class Utils {
    public static int dpToPx(float dp, Resources resources){
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
        return (int) px;
    }
}
