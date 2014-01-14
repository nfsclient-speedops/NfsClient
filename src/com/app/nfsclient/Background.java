/*
 * Copyright (c) 2014 SpeedOps
 * All rights reserved.
 *
 * SpeedOps is not responsible for any use or misuse of this product.
 * In using this software you agree to hold harmless SpeedOps and any other
 * contributors to this project from any damages or liabilities which might result 
 * from its use.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.app.nfsclient;

import java.util.Arrays;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;

public class Background {
	private static final String TAG = "Background";
	
    private static final int LIST_DIVIDER_GRADIENTS = 6;
    private static final int BACKGROUND_GRADIENTS = 20;
    
	private static int windowBackground = R.color.black;
	private static int actionBarBackground = R.color.black;
	private static int listHeader = R.color.black;
	private static int listItemSelection = R.color.snow4;
	private static int listItemUnselection = R.color.transparent;
	private static int textColor = R.color.white;
	private static int iconResid = R.drawable.ic_launcher;
	private static int switchThumbTrack = R.drawable.track_bg_black;
	private static int preferencesTheme = R.style.PreferencesTheme_black2;

	private static int[] solidListDividerGradient = new int[LIST_DIVIDER_GRADIENTS];
	
	private static int[] actionBarBackgroundGradient = new int[BACKGROUND_GRADIENTS];
	private static GradientDrawable actionBarBackgroundGradientDrawable;
	
    private static StateListDrawable positiveButtonSelector;
    private static StateListDrawable negativeButtonSelector;
    private static StateListDrawable neutralButtonSelector;
  
    public static void colorThemeInit(Context context) {
    	AppState.logX(TAG, "backgroundInit");
    	
    	solidListDividerGradient = gradientInit(context, LIST_DIVIDER_GRADIENTS, textColor);

    	actionBarBackgroundGradient = gradientInit(context, BACKGROUND_GRADIENTS, actionBarBackground);
    	actionBarBackgroundGradientDrawable = new GradientDrawable(Orientation.TOP_BOTTOM,
    		actionBarBackgroundGradient);
    	actionBarBackgroundGradientDrawable.setShape(GradientDrawable.RECTANGLE);

    	positiveButtonSelector = new StateListDrawable();
    	positiveButtonSelector.addState(new int[] { android.R.attr.state_pressed }, 
    		context.getResources().getDrawable(listHeader));
    	positiveButtonSelector.addState(new int[] { android.R.attr.state_focused },
    		context.getResources().getDrawable(listHeader));
    	positiveButtonSelector.addState(new int[] { },
    		context.getResources().getDrawable(android.R.color.transparent));

    	negativeButtonSelector = new StateListDrawable();
    	negativeButtonSelector.addState(new int[] { android.R.attr.state_pressed }, 
    		context.getResources().getDrawable(listHeader));
    	negativeButtonSelector.addState(new int[] { android.R.attr.state_focused },
    		context.getResources().getDrawable(listHeader));
    	negativeButtonSelector.addState(new int[] { },
    		context.getResources().getDrawable(android.R.color.transparent));

    	neutralButtonSelector = new StateListDrawable();
    	neutralButtonSelector.addState(new int[] { android.R.attr.state_pressed }, 
    		context.getResources().getDrawable(listHeader));
    	neutralButtonSelector.addState(new int[] { android.R.attr.state_focused },
    		context.getResources().getDrawable(listHeader));
    	neutralButtonSelector.addState(new int[] { },
    		context.getResources().getDrawable(android.R.color.transparent));
    }
    
	public static int windowBackgroundGet(Context context) {
		return context.getResources().getColor(windowBackground);
	}
	
	public static int actionBarBackgroundGet() {
		return actionBarBackground;
	}
	
	public static int actionBarBackgroundGet(Context context) {
		return context.getResources().getColor(actionBarBackground);
	}
	
	public static int listHeaderGet(Context context) {
		return context.getResources().getColor(listHeader);
	}
	
	public static int listItemSelectionGet(Context context) {
		return context.getResources().getColor(listItemSelection);
	}
	
	public static int listItemUnselectionGet(Context context) {
		return context.getResources().getColor(listItemUnselection);
	}
	
	public static int textColorGet(Context context) {
		return context.getResources().getColor(textColor);
	}
	
	public static int iconResourceIdGet() {
		return iconResid;
	}
	
	public static Drawable switchThumbTrackGet(Context context) {
		return context.getResources().getDrawable(switchThumbTrack);
	}
	
	public static int preferencesThemeGet() {
		return preferencesTheme;
	}
	
	public static StateListDrawable selectorGet(Context context) {
		StateListDrawable selector = new StateListDrawable();
		selector.addState(new int[] { android.R.attr.state_pressed, android.R.attr.state_enabled },
    	    context.getResources().getDrawable(listItemSelection));
		selector.addState(new int[] { android.R.attr.state_focused, android.R.attr.state_enabled },
    		context.getResources().getDrawable(listItemSelection));
		selector.addState(new int[] { },
    		context.getResources().getDrawable(windowBackground));
		
		return selector;
	}
	public static StateListDrawable positiveButtonSelectorGet(Context context) {
		return selectorGet(context);
	}
	
	public static StateListDrawable negativeButtonSelectorGet(Context context) {
		return selectorGet(context);
	}
	
	public static StateListDrawable neutralButtonSelectorGet(Context context) {
		return selectorGet(context);
	}
	
	public static  int[] solidListDividerGet() {
		return solidListDividerGradient;
	}
	public static GradientDrawable solidListDividerDrawableGet() {
		return new GradientDrawable(Orientation.RIGHT_LEFT, solidListDividerGradient);
	}
	public void solidListDividerSet(int[] solidListDivider) {
		solidListDividerGradient = solidListDivider;
	}
	
	public static GradientDrawable actionBarBackgroundDrawableGet() {
		return new GradientDrawable(Orientation.RIGHT_LEFT, actionBarBackgroundGradient);
	}
	
    private static int[] gradientInit(Context context, int size, int colorResId) {
    	int color = context.getResources().getColor(colorResId);
    	int[] divider = new int[size];

    	Arrays.fill(divider, color);
    	return divider;
    }
}