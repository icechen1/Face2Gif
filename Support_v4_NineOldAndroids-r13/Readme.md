#Android Support Library v4 with NineOldAndroids

Allows using android [Property Animations][2] with Google's Support Library v4 Fragment transitions.  Rely's on Jake Wharton's [NineOldAndroids][1].

* Object Animator API's for [Fragment Transitions](#transition)
  * [Standard Transitions](#standard)
  * [Custom Transitions](#custom)
  * [Fragment Implementation](#fragment)
  * [Style Resources](#style)
* [ViewPager PagerTransformers](#pager)
* [ActionBarToggle for Drawer Navigation](#drawer)


##Project Configuration

Your project must have [NineOldAndroids][1] in the classpath.  This can be done with Maven or putting the jar into the /libs folder. *This project is packaged as an APK Library* to support style resources.  Import it into eclipse and reference it as an Android Library. Right-click on project and `Properties->Android`.  Finally remove Google's support v4 library from your classpath.

##Usage

Read Google's documentation about [Property Animations][2].  They are different from View animations. For example:

Create a file `res/animator/fade_in.xml'

```xml
<?xml version="1.0" encoding="utf-8"?>
<set xmlns:android="http://schemas.android.com/apk/res/android"
	android:zAdjustment="top">
    <objectAnimator
        android:valueFrom="0"
        android:valueTo="1"
        android:valueType="floatType"
        android:propertyName="alpha"
        android:duration="220"/>
</set>
```

Look at the sample application for a complete example and read below for instructions.

###<a name="transition"></a>Animator Fragment Transitions

This fork allows using [NineOldAndroids][1] Object Animators for fragment transitions.  View animations will no longer work.

####<a name="standard"></a>Standard Transitions

Specify standard transitions in the transaction.

```java
tx.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
```

####<a name="custom"></a>Custom Transitions

Specify custom transition animations in the transaction

```java
tx.setCustomTransitions(R.animator.flip_left_in, R.animator.flip_left_out, R.animator.flip_right_in, R.animator.flip_right_out)
```

####<a name="fragment"></a>Fragment Implementation Specified Transitions

Specify animations in Fragment implementation

```java
public class AnimatedFragment extends Fragment {

	//class contents removed

	@Override
	public Animator onCreateAnimator(int transit, boolean enter, int nextAnim) {
	   //If transaction specifies a custom animation, use it
	   if(nextAnim>0)
		  return AnimatorInflater.loadAnimator(getActivity(), nextAnim);
	   if(enter)
		  return AnimatorInflater.loadAnimator(getActivity(), R.animator.fade_in);
	   else
		  return AnimatorInflater.loadAnimator(getActivity(), R.animator.fade_out);
	}

}
```

####<a name="style"></a>Transition style resources

Specify transition animations in a style resource.

Create a style resource `res/values/styles.xml'

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
 	<!-- Override standard Transitions with a Style -->
   	<style name="MyTransitionStyle">
	    <item name="fragmentFadeEnterAnimation">@animator/fade_enter</item>
	    <item name="fragmentFadeExitAnimation">@animator/fade_exit</item>
	    <item name="fragmentOpenEnterAnimation">@animator/flip_left_in</item>
	    <item name="fragmentOpenExitAnimation">@animator/flip_left_out</item>
	    <item name="fragmentCloseEnterAnimation">@animator/flip_right_in</item>
	    <item name="fragmentCloseExitAnimation">@animator/flip_right_out</item>
   	</style>
</resources>
```

Specify the resource and transition in the transaction

```java
tx.setTransitionStyle(R.style.MyTransitionStyle);
tx.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
```

###<a name="pager"></a>PageTransformer

The standard ViewPager ignores [PageTransformers][3] in pre-HoneyComb API levels.  This library supports implementing PageTransformers with [NineOldAndroids][1].  Just *use ViewHelper instead of the View properties*.  For example:

```java
/**
 * Implemented using NineOldAndroids ViewHelper instead of newer API View properties
 */
public class ZoomOutPageTransformer implements PageTransformer {
    private static float MIN_SCALE = 0.85f;
    private static float MIN_ALPHA = 0.5f;

    public void transformPage(View view, float position) {
        int pageWidth = view.getWidth();
        int pageHeight = view.getHeight();
        
        if (position < -1) { // [-Infinity,-1)
            // This page is way off-screen to the left.
            ViewHelper.setAlpha(view, 0);
        } else if (position <= 1) { // [-1,1]
            // Modify the default slide transition to shrink the page as well
            float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
            float vertMargin = pageHeight * (1 - scaleFactor) / 2;
            float horzMargin = pageWidth * (1 - scaleFactor) / 2;
            if (position < 0) {
                ViewHelper.setTranslationX(view, horzMargin - vertMargin / 2);
            } else {
                ViewHelper.setTranslationX(view, -horzMargin + vertMargin / 2);
            }

            // Scale the page down (between MIN_SCALE and 1)
            ViewHelper.setScaleX(view, scaleFactor);
            ViewHelper.setScaleY(view, scaleFactor);

            // Fade the page relative to its size.
            ViewHelper.setAlpha(view, MIN_ALPHA +
                    (scaleFactor - MIN_SCALE) /
                    (1 - MIN_SCALE) * (1 - MIN_ALPHA));

        } else { // (1,+Infinity]
            // This page is way off-screen to the right.
            ViewHelper.setAlpha(view, 0);
        }
    }
}
```

Set the PageTransformer as usual:

```java
viewPager.setPageTransformer(new ZoomOutPageTransformer());
```

###<a name="drawer"></a>ActionBarDrawerToggle for Drawer Navigation

Google Support Library r13 added the Navigation Drawer.  See the android docs for [Drawer Navigation][4] for details.  The ActionBarDrawerToggle may be used with ActionBarSherlock to change the *up* icon on the ActionBar on Gingerbread.

[1]: http://nineoldandroids.com "NineOldAndroids"
[2]: http://developer.android.com/guide/topics/graphics/prop-animation.html "Android Property Animations"
[3]: http://developer.android.com/training/animation/screen-slide.html#pagetransformer  "Using ViewPager for Screen Slides"
[4]: http://developer.android.com/training/implementing-navigation/nav-drawer.html "Navigation Drawer"

