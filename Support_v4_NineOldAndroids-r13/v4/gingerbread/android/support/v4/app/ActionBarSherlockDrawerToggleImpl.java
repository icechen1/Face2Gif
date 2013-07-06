package android.support.v4.app;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.supportanimator.supportv4.R;

/**
 * 
 */
public class ActionBarSherlockDrawerToggleImpl {
	private static final String TAG = "ActionBarSherlockDrawerToggleImpl";

	private static final int[] THEME_ATTRS = new int[] {
		R.attr.homeAsUpIndicator
	};

	public static Drawable getThemeUpIndicator(Activity activity) {
		final TypedArray a = activity.obtainStyledAttributes(THEME_ATTRS);
		final Drawable result = a.getDrawable(0);
		a.recycle();
		return result;
	}

	public static Object setActionBarUpIndicator(Object info, Activity activity, Drawable drawable, int contentDescRes) {
		if (info == null) info = new SetIndicatorInfo(activity);

		final SetIndicatorInfo sii = (SetIndicatorInfo) info;
		if (sii.upIndicatorView != null) {
		    for(ImageView up : sii.upIndicatorView) {
			    up.setImageDrawable(drawable);
			    ((ViewGroup)up.getParent()).setContentDescription(activity.getResources().getString(contentDescRes));
		    }
		} else {
			Log.w(TAG, "Couldn't set home-as-up indicator");
		}
		return info;
	}

	public static Object setActionBarDescription(Object info, Activity activity, int contentDescRes) {
		if (info == null) info = new SetIndicatorInfo(activity);

		final SetIndicatorInfo sii = (SetIndicatorInfo) info;
		if (sii.upIndicatorView != null) {
		    for(ImageView up : sii.upIndicatorView) {
                ((ViewGroup)up.getParent()).setContentDescription(activity.getResources().getString(contentDescRes));
            }
		} else {
			Log.w(TAG, "Couldn't set home-as-up indicator");
		}
		return info;
	}

	private static class SetIndicatorInfo {
	    public List<ImageView> upIndicatorView = new ArrayList<ImageView>();
	    
	    SetIndicatorInfo(Activity activity) {
	        final View home = activity.findViewById(R.id.abs__up);
	        if (home instanceof ImageView) {
	            ViewGroup gp = (ViewGroup)home.getParent().getParent();
	            for(int i=0; i<gp.getChildCount(); i++) {
	                View upView = gp.getChildAt(i).findViewById(R.id.abs__up);
	                if(upView!=null) {
	                    upIndicatorView.add((ImageView) upView);
	                }
	            }
	            for(ImageView up : upIndicatorView)
	                Log.v(TAG, "Found up indicator:  " + up.getId() + "  " + (up.getVisibility()==View.VISIBLE));
	        }
	    }
	}
}
