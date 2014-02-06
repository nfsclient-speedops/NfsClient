/*
 * Copyright (c) 2013 SpeedOps
 * All rights reserved.
 *
 * SpeedOps is not responsible for any use or misuse of this product.
 * In using this software you agree to hold harmless mux.sms@gmail.com and any other
 * contributors to this project from any damages or liabilities which might result 
 * from its use.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.app.nfsclient.generic;

import com.app.nfsclient.AppState;
import com.app.nfsclient.Background;
import com.app.nfsclient.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.widget.CompoundButton;

public class GenericOnOffSwitch extends CompoundButton {
    private static final String TAG = "GenericOnOffSwitch";
    
    private static final int TOUCH_MODE_IDLE = 0;
    private static final int TOUCH_MODE_DOWN = 1;
    private static final int TOUCH_MODE_DRAGGING = 2;
    
    // Enum for the "typeface" XML parameter.
    private static final int SANS = 1;
    private static final int SERIF = 2;
    private static final int MONOSPACE = 3;

    private static final int VERTICAL=0;
   	private static final int HORIZONTAL=1;
    
   	private Context context;
   	
    private int mOrientation = HORIZONTAL;
    private OnChangeAttemptListener mOnChangeAttemptListener;
    private boolean mPushStyle;
    private boolean mTextOnThumb;
    private int mThumbExtraMovement;
    private Drawable mLeftBackground;
    private Drawable mRightBackground;
    private Drawable mMaskDrawable;
    private Drawable mThumbDrawable;
    private Drawable mTrackDrawable;
    private int mThumbTextPadding;
    private int mTrackTextPadding;
    private int mSwitchMinWidth;
    private int mSwitchMinHeight;
    private int mSwitchPadding;
    private CharSequence mTextOn;
    private CharSequence mTextOff;
    private int mTextColorId;
    private Drawable mDrawableOn;
    private Drawable mDrawableOff;
    private boolean fixed=false;
    private boolean clickDisabled=false;
    private boolean onOrOff=true;
    private Bitmap pushBitmap;
    private Bitmap maskBitmap;
    private Bitmap tempBitmap;
    private Canvas backingLayer;

    private int mTouchMode;
    private int mTouchSlop;
    private float mTouchX;
    private float mTouchY;
    private VelocityTracker mVelocityTracker = VelocityTracker.obtain();
    private int mMinFlingVelocity;

    private float mThumbPosition = 0;
    private int mSwitchWidth;
    private int mSwitchHeight;
    private int mThumbWidth;
    private int mThumbHeight;

    private int mSwitchLeft;
    private int mSwitchTop;
    private int mSwitchRight;
    private int mSwitchBottom;
    
    private TextPaint mTextPaint;
    private ColorStateList mTextColors;
    private Layout mOnLayout;
    private Layout mOffLayout;

    private Paint xferPaint;
    private Bitmap leftBitmap, rightBitmap;
    
    private final Rect mTrackPaddingRect = new Rect();
    private final Rect mThPad = new Rect();
	private final Rect canvasClipBounds = new Rect();

    private static final int[] CHECKED_STATE_SET = {
        android.R.attr.state_checked
    };

    /**
     * Construct a new GenericOnOffSwitch with default styling.
     *
     * @param context The Context that will determine this widget's theming.
     */
    public GenericOnOffSwitch(Context context) {
        this(context, null);
        AppState.logZ(TAG, "constructor 1");
    }

    /**
     * Construct a new GenericOnOffSwitch with default styling, overriding specific style
     * attributes as requested.
     *
     * @param context The Context that will determine this widget's theming.
     * @param attrs Specification of attributes that should deviate from default styling.
     */
    public GenericOnOffSwitch(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.genericOnOffSwitchStyleAttr);
        AppState.logZ(TAG, "constructor 2");
    }

    /**
     * Construct a new GenericOnOffSwitch with a default style determined by the given theme attribute,
     * overriding specific style attributes as requested.
     *
     * @param context The Context that will determine this widget's theming.
     * @param attrs Specification of attributes that should deviate from the default styling.
     * @param defStyle An attribute ID within the active theme containing a reference to the
     *                 default style for this widget. e.g. android.R.attr.switchStyle.
     */
    public GenericOnOffSwitch(Context context, AttributeSet attrs, int defStyle) {
    	super(context, attrs, defStyle);
        AppState.logZ(TAG, "constructor 3");
        
    	this.context = context;
    	
    	mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    	Resources res = getResources();
    	mTextPaint.density = res.getDisplayMetrics().density;
    	mTextPaint.setShadowLayer(0.5f, 1.0f, 1.0f, Color.BLACK);

    	TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GenericOnOffSwitch, defStyle, 0);

    	mLeftBackground = a.getDrawable(R.styleable.GenericOnOffSwitch_leftBackground);
    	mRightBackground = a.getDrawable(R.styleable.GenericOnOffSwitch_rightBackground);
    	mOrientation = a.getInteger(R.styleable.GenericOnOffSwitch_orientation, HORIZONTAL);

//      mThumbDrawable = a.getDrawable(R.styleable.GenericOnOffSwitch_thumb);
//    	mTrackDrawable = a.getDrawable(R.styleable.GenericOnOffSwitch_track);
//      mThumbDrawable = this.context.getResources().getDrawable(R.drawable.switch_thumb);
//      mThumbDrawable = this.context.getResources().getDrawable(R.drawable.track_bg);
        mThumbDrawable = Background.switchThumbTrackGet(context);
    	mTrackDrawable = generateThumbTrack(Background.listItemSelectionGet(context));

    	mTextOn = a.getText(R.styleable.GenericOnOffSwitch_textOn);
    	mTextOff = a.getText(R.styleable.GenericOnOffSwitch_textOff);
    	mDrawableOn = a.getDrawable(R.styleable.GenericOnOffSwitch_drawableOn);
    	mDrawableOff = a.getDrawable(R.styleable.GenericOnOffSwitch_drawableOff);
    	mPushStyle = a.getBoolean(R.styleable.GenericOnOffSwitch_pushStyle, false);
    	mTextOnThumb = a.getBoolean(R.styleable.GenericOnOffSwitch_textOnThumb, false);
    	mThumbExtraMovement = a.getDimensionPixelSize(R.styleable.GenericOnOffSwitch_thumbExtraMovement, 0);
    	mThumbTextPadding = a.getDimensionPixelSize( R.styleable.GenericOnOffSwitch_thumbTextPadding, 0);
    	mTrackTextPadding = a.getDimensionPixelSize( R.styleable.GenericOnOffSwitch_trackTextPadding, 0);
    	mSwitchMinWidth = a.getDimensionPixelSize( R.styleable.GenericOnOffSwitch_switchMinWidth, 0);
    	mSwitchMinHeight = a.getDimensionPixelSize( R.styleable.GenericOnOffSwitch_switchMinHeight, 0);
    	mSwitchPadding =  a.getDimensionPixelSize( R.styleable.GenericOnOffSwitch_switchPadding, 0);

    	mTrackDrawable.getPadding(mTrackPaddingRect);
    	AppState.log(TAG, "mTrackPaddingRect=" + mTrackPaddingRect);
    	mThumbDrawable.getPadding(mThPad);
    	AppState.log(TAG, "mThPad=" + mThPad);

    	mMaskDrawable = a.getDrawable(R.styleable.GenericOnOffSwitch_backgroundMask);

    	RuntimeException e = null;
    	if ((mLeftBackground != null) || (mRightBackground != null)) {
    		if (mMaskDrawable == null) {
    			e = new IllegalArgumentException(a.getPositionDescription()
    				+ " if left/right background is given, then a mask has to be there");
    		}
    	}
    	if ((mLeftBackground != null) ^ (mRightBackground != null)) {
    		if (mMaskDrawable == null) {
    			e = new IllegalArgumentException(a.getPositionDescription()
    				+ " left and right background both should be there. only one is not allowed ");
    		}
    	}

    	if ((mTextOnThumb) && (mPushStyle)) {
    		e = new IllegalArgumentException(a.getPositionDescription()
    			+ " Text On Thumb and Push Stype are mutually exclusive. Only one can be present ");
    	}

    	xferPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    	//xferPaint.setColor(Color.TRANSPARENT);
    	xferPaint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));

    	int appearance = a.getResourceId(R.styleable.GenericOnOffSwitch_switchTextAppearanceAttrib, 0);
    	if (appearance != 0) {
    		setSwitchTextAppearance(context, appearance);
    	}
    	a.recycle();
    	if (e != null) {
    		throw e;
    	}
    	ViewConfiguration config = ViewConfiguration.get(context);
    	mTouchSlop = config.getScaledTouchSlop();
    	//AppState.log(TAG, "mTouchSlop="+mTouchSlop);
    	mMinFlingVelocity = config.getScaledMinimumFlingVelocity();
    	//AppState.log(TAG, "mMinFlingVelocity="+mMinFlingVelocity);

    	// Refresh display with current params
    	refreshDrawableState();
    	setChecked(isChecked());
    	this.setClickable(true);
    	//this.setOnClickListener(clickListener);
    }

    /**
     * Sets the switch text color, size, style, hint color, and highlight color
     * from the specified TextAppearance resource.
     */
    public void setSwitchTextAppearance(Context context, int resid) {
        TypedArray appearance =
            context.obtainStyledAttributes(resid, R.styleable.genericOnOffSwitchTextAppearanceAttrib);

        ColorStateList colors;
        int ts;

        colors = appearance.getColorStateList(R.styleable.genericOnOffSwitchTextAppearanceAttrib_textColor);
        if (colors != null) {
            mTextColors = colors;
        } else {
            // If no color set in TextAppearance, default to the view's textColor
            mTextColors = getTextColors();
        }
        AppState.logZ(TAG, String.format("setSwitchTextAppearance: black resid = %d, colors = %s", 
        	R.color.black, colors));
        
        ts = appearance.getDimensionPixelSize(R.styleable.genericOnOffSwitchTextAppearanceAttrib_textSize, 0);
        if (ts != 0) {
            if (ts != mTextPaint.getTextSize()) {
                mTextPaint.setTextSize(ts);
                requestLayout();
            }
        }

        int typefaceIndex, styleIndex;

        typefaceIndex = appearance.getInt(R.styleable.genericOnOffSwitchTextAppearanceAttrib_typeface, -1);
        styleIndex = appearance.getInt(R.styleable.genericOnOffSwitchTextAppearanceAttrib_textStyle, -1);

        setSwitchTypefaceByIndex(typefaceIndex, styleIndex);

        appearance.recycle();
    }

    private void setSwitchTypefaceByIndex(int typefaceIndex, int styleIndex) {
        Typeface tf = null;
        switch (typefaceIndex) {
            case SANS:
                tf = Typeface.SANS_SERIF;
                break;

            case SERIF:
                tf = Typeface.SERIF;
                break;

            case MONOSPACE:
                tf = Typeface.MONOSPACE;
                break;
        }

        setSwitchTypeface(tf, styleIndex);
    }

    /**
     * Sets the typeface and style in which the text should be displayed on the
     * switch, and turns on the fake bold and italic bits in the Paint if the
     * Typeface that you provided does not have all the bits in the
     * style that you specified.
     */
    public void setSwitchTypeface(Typeface tf, int style) {
        if (style > 0) {
            if (tf == null) {
                tf = Typeface.defaultFromStyle(style);
            } else {
                tf = Typeface.create(tf, style);
            }

            setSwitchTypeface(tf);
            // now compute what (if any) algorithmic styling is needed
            int typefaceStyle = tf != null ? tf.getStyle() : 0;
            int need = style & ~typefaceStyle;
            mTextPaint.setFakeBoldText((need & Typeface.BOLD) != 0);
            mTextPaint.setTextSkewX((need & Typeface.ITALIC) != 0 ? -0.25f : 0);
        } else {
            mTextPaint.setFakeBoldText(false);
            mTextPaint.setTextSkewX(0);
            setSwitchTypeface(tf);
        }
    }

    /**
     * Sets the typeface in which the text should be displayed on the switch.
     * Note that not all Typeface families actually have bold and italic
     * variants, so you may need to use
     * {@link #setSwitchTypeface(Typeface, int)} to get the appearance
     * that you actually want.
     *
     * @attr ref android.R.styleable#TextView_typeface
     * @attr ref android.R.styleable#TextView_textStyle
     */
    public void setSwitchTypeface(Typeface tf) {
        if (mTextPaint.getTypeface() != tf) {
            mTextPaint.setTypeface(tf);

            requestLayout();
            invalidate();
        }
    }

    /**
     * Returns the text displayed when the button is in the checked state.
     */
    public CharSequence getTextOn() {
        return mTextOn;
    }

    /**
     * Sets the text displayed when the button is in the checked state.
     */
    public void setTextOn(CharSequence textOn) {
        mTextOn = textOn;
        this.mOnLayout = null;
        requestLayout();
    }

    /**
     * Sets the color of the text displayed when the button is in the checked state.
     */
    public void setTextOnColor(int colorId) {
        mTextColorId = colorId;
        mTextColors = new ColorStateList (
        	new int [] [] {
        		new int [] {android.R.attr.state_pressed},
        		new int [] {android.R.attr.state_focused},
        		new int [] {}
        	},
        	new int [] {
        		mTextColorId,
        		mTextColorId,
        		mTextColorId
        	}
        );
        this.mOnLayout = null;
        requestLayout();
    }
    
    /**
     * Returns the text displayed when the button is not in the checked state.
     */
    public CharSequence getTextOff() {
        return mTextOff;
    }

    /**
     * Sets the text displayed when the button is not in the checked state.
     */
    public void setTextOff(CharSequence textOff) {
        mTextOff = textOff;
        this.mOffLayout = null;
        requestLayout();
    }
    
    /**
     * Interface definition for a callback to be invoked when the switch is
     * in a fixed state and there was an attempt to change its state either
     * via a click or drag
     */
    public static interface OnChangeAttemptListener {
        /**
         * Called when an attempt was made to change the checked state of the 
         * switch while the switch was in a fixed state.
         *
         * @param isChecked  The current state of switch.
         */
        void onChangeAttempted(boolean isChecked);
    }

    /**
     * Register a callback to be invoked when there is an attempt to change the
     * state of the switch when its in fixated
     *
     * @param listener the callback to call on checked state change
     */
    public void setOnChangeAttemptListener(OnChangeAttemptListener listener) {
        mOnChangeAttemptListener = listener;
    }
    
    /**
     * fixates the switch on one of the positions ON or OFF.
     * if the switch is fixated, then it cannot be switched to the other position
     * @param fixed   If true, sets the switch to fixed mode. 
     *                If false, sets the switch to switched mode. 
     * @param onOrOff The switch position to which it will be fixed.
     *                If it is true then the switch is fixed on ON.
     *                If it is false then the switch is fixed on OFF
     * @Note The position is only fixed from the user interface. It can still be
     *       changed through program by using {@link #setChecked(boolean) setChecked} 
     */
    public void fixate(boolean fixed, boolean onOrOff) {
        fixate(fixed);
    	this.onOrOff = onOrOff;
    	if (onOrOff)
    		this.setChecked(true);
    }

    /**
     * fixates the switch on one of the positions ON or OFF.
     * if the switch is fixated, then it cannot be switched to the other position
     * @param fixed   if true, sets the switch to fixed mode. 
     *                if false, sets the switch to switched mode. 
     */
    public void fixate(boolean fixed) {
    	this.fixed = fixed;
    }

    /**
     * returns if the switch is fixed to one of its positions
     */
    public boolean isFixed() {
    	return fixed;
    }

    private Layout makeLayout(CharSequence text) {
        return new StaticLayout(text, mTextPaint,
            (int) java.lang.Math.ceil(Layout.getDesiredWidth(text, mTextPaint)),
            Layout.Alignment.ALIGN_NORMAL, 1.f, 0, true);
    }

    /**
     * @return true if (x, y) is within the target area of the switch thumb
     */
    private boolean hitThumb(float x, float y) {
    	if (mOrientation == HORIZONTAL) {
    		final int thumbTop = mSwitchTop - mTouchSlop;
    		final int thumbLeft = mSwitchLeft + (int) (mThumbPosition + 0.5f) - mTouchSlop;
    		final int thumbRight = thumbLeft + mThumbWidth + mTouchSlop;// + mThPad.left + mThPad.right
    		final int thumbBottom = mSwitchBottom + mTouchSlop;
    		return x > thumbLeft && x < thumbRight && y > thumbTop && y < thumbBottom;
    	}

    	//if (mOrientation == VERTICAL)
    	if (mSwitchHeight >150) {
    	    final int thumbLeft = mSwitchLeft - mTouchSlop;
    	    final int thumbTop = mSwitchTop + (int) (mThumbPosition + 0.5f) - mTouchSlop;
    	    final int thumbBottom = thumbTop + mThumbHeight + mTouchSlop;// + mThPad.top + mThPad.bottom
    	    final int thumbRight = mSwitchRight + mTouchSlop;
    	    AppState.log(TAG, "returning " +  (x > thumbLeft && x < thumbRight && y > thumbTop &&
    	    	y < thumbBottom));
    	    return x > thumbLeft && x < thumbRight && y > thumbTop && y < thumbBottom;
    	}
    	else  {
    	    return x > mSwitchLeft && x < mSwitchRight && y > mSwitchTop && y < mSwitchBottom;
    	}
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
    	//if (fixed) {
    		//AppState.log(TAG, "the switch position is fixed to " + (onOrOff ? "On":"Off") + "position.");
    		//return true;
    	//}
        mVelocityTracker.addMovement(ev);
        //AppState.log(TAG, "onTouchEvent(ev="+ev.toString()+")");
        //AppState.log(TAG, "mTouchMode="+mTouchMode);
        final int action = ev.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                final float x = ev.getX();
                final float y = ev.getY();
                if (isEnabled() && hitThumb(x, y)) {
                    mTouchMode = TOUCH_MODE_DOWN;
                    mTouchX = x;
                    mTouchY = y;
                }
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                switch (mTouchMode) {
                    case TOUCH_MODE_IDLE:
                        // Didn't target the thumb, treat normally.
                        break;

                    case TOUCH_MODE_DOWN: {
                        final float x = ev.getX();
                        final float y = ev.getY();
                        if (Math.abs(x - mTouchX) > mTouchSlop/2 ||
                                Math.abs(y - mTouchY) > mTouchSlop/2) {
                            mTouchMode = TOUCH_MODE_DRAGGING;
            			    if (getParent() != null) {
            				    getParent().requestDisallowInterceptTouchEvent(true);
            			    }
                            mTouchX = x;
                            mTouchY = y;
                            return true;
                        }
                        break;
                    }

                    case TOUCH_MODE_DRAGGING: {
                        final float x = ev.getX();
                        final float dx = x - mTouchX;
                        final float y = ev.getY();
                        final float dy = y - mTouchY;
                    	if (mOrientation == HORIZONTAL) {
                            float newPos = Math.max(0,
                                    Math.min(mThumbPosition + dx, getThumbScrollRange()));
                            if (newPos != mThumbPosition) {
                                mThumbPosition = newPos;
                                mTouchX = x;
                                invalidate();
                            }
                            return true;
                    	}
                    	if (mOrientation == VERTICAL) {
                            float newPos = Math.max(0,
                                    Math.min(mThumbPosition + dy, getThumbScrollRange()));
                            if (newPos != mThumbPosition) {
                                mThumbPosition = newPos;
                                mTouchY = y;
                                invalidate();
                            }
                            return true;
                    	}
                    }
                }
                break;
            }

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                if (mTouchMode == TOUCH_MODE_DRAGGING) {
                    stopDrag(ev);
                    return true;
                }
                mTouchMode = TOUCH_MODE_IDLE;
                mVelocityTracker.clear();
                break;
            }
        }

        AppState.log(TAG, "mThumbPosition="+mThumbPosition);
        boolean flag = super.onTouchEvent(ev);
        //AppState.log(TAG, "super.onTouchEvent(ev) returned="+flag);
        return flag;
    }
    
    /*   
	OnClickListener clickListener = new OnClickListener() {
		public void onClick(View v) {
            AppState.log(TAG, "onClick()");
            animateThumbToCheckedState(isChecked());
		}
	}
	*/
	
	@Override
	public boolean performClick() {
		if (!clickDisabled) {
			// AppState.log(TAG, "performClick(). current Value="+isChecked());
			if (!fixed) {
				boolean flag = super.performClick();
				// AppState.log(TAG,
				// "after super.performClick().  Value="+isChecked());
				return flag;
			} else {
				if (this.mOnChangeAttemptListener != null)
					this.mOnChangeAttemptListener .onChangeAttempted(isChecked());
				return false;
			}
		} else {
			return false;
		}
	}

    public void disableClick() {
    	clickDisabled=true;
    }


    
    public void enableClick() {
    	clickDisabled=false;
    }

    /*
        public  void toggleWithAnimation(boolean animate) {
    	toggle();
    	if (animate) {
            animateThumbToCheckedState(isChecked());
    	}
    }
    
    //@Override
    public boolean isChecked1() {
    	AppState.log(TAG, "isChecked()-mTextOnThumb="+mTextOnThumb);
        if (mTextOnThumb) {
    	    AppState.log(TAG, "returning = "+super.isChecked());
        	return super.isChecked();
        }
   	    AppState.log(TAG, "returning="+(!(super.isChecked())));
       	return !(super.isChecked());
    }
    */
    
    public CharSequence getCurrentText() {
    	if (isChecked())
    		return mTextOn;
    	
    	return mTextOff;
    }
    
    public CharSequence getText(boolean checkedState) {
    	if (checkedState) 
    		return mTextOn;
    	else
    		return mTextOff;
    }
    
	

    private void cancelSuperTouch(MotionEvent ev) {
        MotionEvent cancel = MotionEvent.obtain(ev);
        cancel.setAction(MotionEvent.ACTION_CANCEL);
        super.onTouchEvent(cancel);
        cancel.recycle();
    }

    /**
     * Called from onTouchEvent to end a drag operation.
     *
     * @param ev Event that triggered the end of drag mode - ACTION_UP or ACTION_CANCEL
     */
    private void stopDrag(MotionEvent ev) {
        mTouchMode = TOUCH_MODE_IDLE;
        // Up and not canceled, also checks the switch has not been disabled during the drag
        boolean commitChange = ev.getAction() == MotionEvent.ACTION_UP && isEnabled();
        
        //check if the swtich is fixed to a position
        commitChange = commitChange && (!fixed);
        //AppState.log(TAG,"commitChange="+commitChange);

        cancelSuperTouch(ev);

        if (commitChange) {
            boolean newState;
            mVelocityTracker.computeCurrentVelocity(1000);
            if (mOrientation == HORIZONTAL) {
                float xvel = mVelocityTracker.getXVelocity();
                if (Math.abs(xvel) > mMinFlingVelocity) {
                    newState = xvel > 0;
                } else {
                    newState = getTargetCheckedState();
                }
            } else {
                float yvel = mVelocityTracker.getYVelocity();
                if (Math.abs(yvel) > mMinFlingVelocity) {
                    newState = yvel > 0;
                } else {
                    newState = getTargetCheckedState();
                }
            }
 
            if (mTextOnThumb)
                animateThumbToCheckedState(newState);
            else
                animateThumbToCheckedState(!newState);
        } else {
            animateThumbToCheckedState(isChecked());
            if (fixed) 
			if (this.mOnChangeAttemptListener != null)
				this.mOnChangeAttemptListener.onChangeAttempted(isChecked());
        }
    }

    private void animateThumbToCheckedState(boolean newCheckedState) {
        // TODO animate!
        //float targetPos = newCheckedState ? 0 : getThumbScrollRange();
        //mThumbPosition = targetPos;
        setChecked(newCheckedState);
    }

    private boolean getTargetCheckedState() {
        return mThumbPosition >= getThumbScrollRange() / 2;
    }

    @Override
    public void setChecked(boolean checked) {
        //AppState.log(TAG, "setChecked("+checked+")");
    	boolean lc = checked;
        if (!mTextOnThumb) {
    	    lc = !checked;
        }
        super.setChecked(checked);
        mThumbPosition = lc ? getThumbScrollRange() : 0;
        invalidate();
    }
    @SuppressLint("WrongCall")
	protected void onLayout_orig(boolean changed, int left, int top, int right, int bottom) {
		//AppState.log(TAG, "left=" + left + ",top="+top+",right="+right+",bottom="+bottom);
        super.onLayout(changed, left, top, right, bottom);

        mThumbPosition = isChecked() ? getThumbScrollRange() : 0;

        int switchRight = getWidth() - getPaddingRight();
        int switchLeft = switchRight - mSwitchWidth;
        int switchTop = 0;
        int switchBottom = 0;
        switch (getGravity() & Gravity.VERTICAL_GRAVITY_MASK) {
            default:
            case Gravity.TOP:
                switchTop = getPaddingTop();
                switchBottom = switchTop + mSwitchHeight;
                break;

            case Gravity.CENTER_VERTICAL:
                switchTop = (getPaddingTop() + getHeight() - getPaddingBottom()) / 2 -
                        mSwitchHeight / 2;
                switchBottom = switchTop + mSwitchHeight;
                break;

            case Gravity.BOTTOM:
                switchBottom = getHeight() - getPaddingBottom();
                switchTop = switchBottom - mSwitchHeight;
                break;
        }

        mSwitchLeft = switchLeft;
        mSwitchTop = switchTop;
        mSwitchBottom = switchBottom;
        mSwitchRight = switchRight;
        //AppState.log(TAG, "mSwitchLeft="+mSwitchLeft+" mSwitchRight="+mSwitchRight);
        //AppState.log(TAG, "mSwitchTop="+mSwitchTop+" mSwitchBottom="+mSwitchBottom);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (mOnLayout == null) {
            mOnLayout = makeLayout(mTextOn);
        }
        if (mOffLayout == null) {
            mOffLayout = makeLayout(mTextOff);
        }

        final int maxTextWidth = Math.max(mOnLayout.getWidth(), mOffLayout.getWidth());
        final int maxTextHeight = Math.max(mOnLayout.getHeight(), mOffLayout.getHeight());
        mThumbWidth = maxTextWidth + mThumbTextPadding * 2 + mThPad.left + mThPad.right;
        mThumbWidth = Math.max(mThumbWidth, mThumbDrawable.getIntrinsicWidth());
        if (mTextOnThumb == false) {
            mThumbWidth = mThumbDrawable.getIntrinsicWidth() ;
            if (mThumbWidth <15) {
                //TODO: change this to something guessed based on the other parameters.
            	mThumbWidth = 15;
            }
        }

        mThumbHeight = maxTextHeight + mThumbTextPadding * 2 + mThPad.bottom + mThPad.top;
        mThumbHeight = Math.max(mThumbHeight, mThumbDrawable.getIntrinsicHeight());
        if (mTextOnThumb == false) {
            mThumbHeight = mThumbDrawable.getIntrinsicHeight() ;
            if (mThumbHeight <15) {
                //TODO: change this to something guessed based on the other parameters.
            	mThumbHeight = 15;
            }
        }
        AppState.log(TAG, "mThumbWidth="+mThumbWidth);
        AppState.log(TAG, "mThumbHeight="+mThumbHeight);

        int switchWidth;
        if (mOrientation == HORIZONTAL) {
        	switchWidth = Math.max(mSwitchMinWidth, maxTextWidth * 2 +
        			mThumbTextPadding * 2 + mTrackTextPadding * 2 +
        			mTrackPaddingRect.left + mTrackPaddingRect.right);
        	if (mTextOnThumb == false) {
        		switchWidth = Math.max(  maxTextWidth
        				+ mThumbWidth + mTrackTextPadding * 2
        				+ (mTrackPaddingRect.right + mTrackPaddingRect.left)/2
        				, mSwitchMinWidth);        	                      
        	}

        	if (this.mPushStyle) {
        		switchWidth = Math.max(mSwitchMinWidth, maxTextWidth+ mThumbWidth +
        				mTrackTextPadding +
        				(mTrackPaddingRect.left + mTrackPaddingRect.right)/2);
        	}
        } else {
        	switchWidth = Math.max(maxTextWidth +
        			mThumbTextPadding * 2 + mThPad.left + mThPad.right,
        			mThumbWidth);
        	if ((this.mPushStyle) ||(mTextOnThumb == false)) {
        	    switchWidth = Math.max(maxTextWidth +
        			    mTrackTextPadding * 2 +
        			    mTrackPaddingRect.left + mTrackPaddingRect.right,
        			    mThumbWidth);
        	}
        }
        switchWidth = Math.max(mSwitchMinWidth, switchWidth);
        	
        final int trackHeight = mTrackDrawable.getIntrinsicHeight();
        final int thumbHeight = mThumbDrawable.getIntrinsicHeight();
        int switchHeight = Math.max(mSwitchMinHeight, maxTextHeight);
            switchHeight = Math.max(trackHeight, switchHeight);
            switchHeight = Math.max(switchHeight, thumbHeight);

        if (mOrientation == VERTICAL) {
        	switchHeight = mOnLayout.getHeight() + mOffLayout.getHeight() +
        			mThumbTextPadding * 2 + mThPad.top + mThPad.bottom +
       			    mTrackPaddingRect.bottom + mTrackPaddingRect.top+
        			mTrackTextPadding * 2 ;
      	    if (mTextOnThumb == false) {
      	    	switchHeight = Math.max(mThumbHeight + maxTextHeight +
        			    (mTrackPaddingRect.bottom + mTrackPaddingRect.top)/2 +
        			    mTrackTextPadding * 2, mSwitchMinHeight) ;
      	    }
      	    if (this.mPushStyle) {
        		switchHeight = Math.max(mSwitchMinHeight, maxTextHeight+ mThumbHeight +
        				mTrackTextPadding +
        				(mTrackPaddingRect.top + mTrackPaddingRect.bottom)/2);
        	}
        }

        switch (widthMode) {
            case MeasureSpec.AT_MOST:
                widthSize = Math.min(widthSize, switchWidth);
                break;

            case MeasureSpec.UNSPECIFIED:
                widthSize = switchWidth;
                break;

            case MeasureSpec.EXACTLY:
                // Just use what we were given
                break;
        }

        switch (heightMode) {
            case MeasureSpec.AT_MOST:
                heightSize = Math.min(heightSize, switchHeight);
                break;

            case MeasureSpec.UNSPECIFIED:
                heightSize = switchHeight;
                break;

            case MeasureSpec.EXACTLY:
                // Just use what we were given
                break;
        }

        mSwitchWidth = switchWidth;
        mSwitchHeight = switchHeight;

        AppState.log(TAG, "onMeasure():mSwitchWidth="+mSwitchWidth+" mSwitchHeight="+mSwitchHeight);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int measuredHeight = getMeasuredHeight();
        final int measuredWidth = getMeasuredWidth();
        if (measuredHeight < switchHeight) {
            setMeasuredDimension(getMeasuredWidth(), switchHeight);
        }
        if (measuredWidth < switchWidth) {
            setMeasuredDimension(switchWidth, getMeasuredHeight());
        } 
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		AppState.log(TAG, "onLayout()-left=" + left + ",top="+top+",right="+right+",bottom="+bottom);
        super.onLayout(changed, left, top, right, bottom);

        int switchTop = 0;
        int switchBottom = 0;
        switch (getGravity() & Gravity.VERTICAL_GRAVITY_MASK) {
            default:
            case Gravity.TOP:
                switchTop = getPaddingTop();
                switchBottom = switchTop + mSwitchHeight;
                break;

            case Gravity.CENTER_VERTICAL:
                switchTop = (getPaddingTop() + getHeight() - getPaddingBottom()) / 2 - mSwitchHeight / 2;
                switchBottom = switchTop + mSwitchHeight;
                break;

            case Gravity.BOTTOM:
                switchBottom = getHeight() - getPaddingBottom();
                switchTop = switchBottom - mSwitchHeight;
                break;
        }

        mSwitchBottom = mSwitchHeight - getPaddingBottom();
        mSwitchTop = mSwitchBottom-mSwitchHeight;
        mSwitchRight = mSwitchWidth-getPaddingRight();
        mSwitchLeft = mSwitchRight - mSwitchWidth;
        if (this.mTextOnThumb) {
            mThumbPosition = isChecked() ? getThumbScrollRange() : 0;
        } else  {
            mThumbPosition = isChecked() ? 0 : getThumbScrollRange();
        }
        
        AppState.log(TAG, "getWidth()="+getWidth()+" getHeight()="+getHeight());
        AppState.log(TAG, "getPaddingLeft()="+getPaddingLeft()+" getPaddingRight()="+getPaddingRight());
        AppState.log(TAG, "getPaddingTop()="+getPaddingTop()+" getPaddingBottom()="+getPaddingBottom());
        
        AppState.log(TAG, "mSwitchWidth="+mSwitchWidth+" mSwitchHeight="+mSwitchHeight);
        AppState.log(TAG, "mSwitchLeft="+mSwitchLeft+" mSwitchRight="+mSwitchRight);
        AppState.log(TAG, "mSwitchTop="+mSwitchTop+" mSwitchBottom="+mSwitchBottom);
        
        //now that the layout is known, prepare the drawables
        mTrackDrawable.setBounds(mSwitchLeft, mSwitchTop, mSwitchRight, mSwitchBottom);
        if (mDrawableOn != null) mDrawableOn.setBounds(0, 0, mDrawableOn.getIntrinsicWidth(),
        	mDrawableOn.getIntrinsicHeight());
        if (mDrawableOff != null) mDrawableOff.setBounds(0, 0, mDrawableOff.getIntrinsicWidth(),
        	mDrawableOff.getIntrinsicHeight());
        if (mLeftBackground != null)
            mLeftBackground.setBounds(mSwitchLeft, mSwitchTop, mSwitchRight, mSwitchBottom);
        if (mRightBackground != null)
            mRightBackground.setBounds(mSwitchLeft, mSwitchTop, mSwitchRight, mSwitchBottom);
        
        if (mMaskDrawable != null) {
            tempBitmap = Bitmap.createBitmap(mSwitchRight - mSwitchLeft, mSwitchBottom - mSwitchTop,
            	Config.ARGB_8888);
            backingLayer = new Canvas(tempBitmap);
            mMaskDrawable.setBounds(mSwitchLeft, mSwitchTop, mSwitchRight, mSwitchBottom);
            mMaskDrawable.draw(backingLayer);
            maskBitmap = tempBitmap.extractAlpha();
        
            if (mLeftBackground != null) {
                mLeftBackground.draw(backingLayer);
                backingLayer.drawBitmap(maskBitmap, 0, 0, xferPaint);
                leftBitmap = tempBitmap.copy(tempBitmap.getConfig(), true);
            }
        
            if (mRightBackground != null) {
                mRightBackground.draw(backingLayer);
                backingLayer.drawBitmap(maskBitmap, 0, 0, xferPaint);
                rightBitmap = tempBitmap.copy(tempBitmap.getConfig(), true);
            }
        }
        if (mPushStyle) {
            final int switchInnerTop = mSwitchTop + mTrackPaddingRect.top;
            final int switchInnerBottom = mSwitchBottom - mTrackPaddingRect.bottom;
            final int switchVerticalMid = (switchInnerTop + switchInnerBottom) / 2;
            final int maxTextWidth = Math.max(mOnLayout.getWidth(), mOffLayout.getWidth());
            final int maxTextHeight = Math.max(mOnLayout.getHeight(), mOffLayout.getHeight());
            int width = maxTextWidth * 2 +
            		    mTrackPaddingRect.left + mTrackPaddingRect.right +
            		    mThumbWidth + mTrackTextPadding * 4 ;
            int height = mSwitchBottom - mSwitchTop;
            
            if (mOrientation == VERTICAL) {
                height =
            		    mTrackPaddingRect.top +
            		    mTrackTextPadding +
                		maxTextHeight +
            		    mTrackTextPadding +
            		    mThumbHeight +
            		    mTrackTextPadding +
                		maxTextHeight +
            		    mTrackTextPadding +
            		    mTrackPaddingRect.bottom ;
                width = mSwitchRight - mSwitchLeft;
            }
            
            AppState.log(TAG,"pushBitmap width="+width+" height="+height);
            pushBitmap = Bitmap.createBitmap(width, height,  Config.ARGB_8888);
            Canvas backingLayer = new Canvas(pushBitmap);
            mTextPaint.drawableState = getDrawableState();
            
            // mTextColors should not be null, but just in case
            if (mTextColors != null) {
                mTextPaint.setColor(mTextColors.getColorForState(getDrawableState(),
                		            mTextColors.getDefaultColor()));
            }
            
            //for vertical orientation leftBitmap is used as top bitmap
            if (leftBitmap != null) {
                backingLayer.save();
                if (backingLayer.getClipBounds(canvasClipBounds)) {
                	if (mOrientation == HORIZONTAL) {
                        canvasClipBounds.right -= width/2;
                	}
                	if (mOrientation == VERTICAL) {
                        canvasClipBounds.bottom -= height/2;
                	}
           	        backingLayer.clipRect(canvasClipBounds);
                }
                backingLayer.drawBitmap(leftBitmap, 0, 0, null);
                backingLayer.restore();
            }
            
            if (rightBitmap != null) {
           	    backingLayer.save();
                if (backingLayer.getClipBounds(canvasClipBounds)) {
                	if (mOrientation == HORIZONTAL) {
                        canvasClipBounds.left += (width)/2;
                	}
                	if (mOrientation == VERTICAL) {
                        canvasClipBounds.top += (height)/2;
                	}
           	        backingLayer.clipRect(canvasClipBounds);
                }
               	if (mOrientation == HORIZONTAL) {
                    backingLayer.translate(width/2-mTrackPaddingRect.right, 0);
               	}
               	if (mOrientation == VERTICAL) {
                    backingLayer.translate(0, height/2-mTrackPaddingRect.bottom);
               	}
                backingLayer.drawBitmap(rightBitmap, 0, 0, null);
                backingLayer.restore();
            }
        }
    }

    // Draw the switch
	@Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);
        int switchInnerLeft = mSwitchLeft + mTrackPaddingRect.left;
        int switchInnerTop = mSwitchTop + mTrackPaddingRect.top;
        int switchInnerRight = mSwitchRight - mTrackPaddingRect.right;
        int switchInnerBottom = mSwitchBottom - mTrackPaddingRect.bottom;
        int thumbRange = getThumbScrollRange();
       	
       	int thumbPos = (int) (mThumbPosition + 0.5f);
       	int alpha = mTextPaint.getAlpha();
       	mTextPaint.drawableState = getDrawableState();
   		AppState.logX(TAG, String.format("mOrientation = %s, mPushStype = %s", mOrientation, mPushStyle));
       	
        if (mOrientation == VERTICAL) {
        	int switchHorizontalMid = (switchInnerLeft + switchInnerRight) / 2;
        	int thumbBoundR = mSwitchRight;
       	    int thumbBoundT = switchInnerTop + 1 * this.getThumbScrollRange() - mThumbExtraMovement;
       	    int thumbBoundB = thumbBoundT  + mThumbHeight;
       	    
        	if (mPushStyle) {
        		final int maxTextHeight = Math.max(mOnLayout.getHeight(), mOffLayout.getHeight());
        		backingLayer.save();
        		backingLayer.translate(0,-thumbRange+thumbPos);
        		backingLayer.drawBitmap(pushBitmap, 0, 0, null);
        		backingLayer.restore();
        		backingLayer.drawBitmap(maskBitmap, 0, 0, xferPaint);
        		canvas.drawBitmap(tempBitmap, 0, 0, null);
        		
        		mTrackDrawable.draw(canvas);
        		
        		backingLayer.drawColor(0xff000000, Mode.DST_IN);
        		backingLayer.save();
        		backingLayer.translate(0,-thumbRange+thumbPos);
        		backingLayer.translate(0,mTrackPaddingRect.top);
        		backingLayer.save();
        		backingLayer.translate(0, (maxTextHeight - mOffLayout.getHeight())/2);
        		if (mDrawableOff != null) mDrawableOff.draw(backingLayer);
        		backingLayer.translate(switchHorizontalMid - mOffLayout.getWidth() / 2, 0);
        		mOffLayout.draw(backingLayer);
        		backingLayer.restore();
        		
        		backingLayer.translate(0,
        				maxTextHeight+ mTrackTextPadding * 2+
        				(maxTextHeight - mOnLayout.getHeight())/2 +
        				mThumbHeight);
        		if (mDrawableOn != null)
        			mDrawableOn.draw(backingLayer);
        		backingLayer.translate(switchHorizontalMid - mOnLayout.getWidth() / 2, 0);
        		mOnLayout.draw(backingLayer); 
        		backingLayer.restore();    
        		backingLayer.drawBitmap(maskBitmap, 0, 0, xferPaint);
        		canvas.drawBitmap(tempBitmap, 0, 0, null);	
        	} else {
        		if (rightBitmap != null) {
        			canvas.save();
        			if (canvas.getClipBounds(canvasClipBounds)) {
        				if (this.mOrientation == HORIZONTAL) {
        					canvasClipBounds.left += (thumbPos+ mThumbWidth/2);
        				}
        				if (this.mOrientation == VERTICAL) {
        					canvasClipBounds.top += (thumbPos+ mThumbHeight/2);
        				}
        				canvas.clipRect(canvasClipBounds);
        			}
        			canvas.drawBitmap(rightBitmap, 0, 0, null);
        			canvas.restore();
        		}

        		if (leftBitmap != null) {
        			canvas.save();
        			if (canvas.getClipBounds(canvasClipBounds)) {
        				if (this.mOrientation == HORIZONTAL) {
        					canvasClipBounds.right -= (thumbRange- thumbPos + mThumbWidth/2);
        				}
        				if (this.mOrientation == VERTICAL) {
        					canvasClipBounds.bottom = (canvasClipBounds.top+ thumbPos + mThumbHeight/2);
        				}
        				canvas.clipRect(canvasClipBounds);
        			}
        			canvas.drawBitmap(leftBitmap, 0, 0, null);
        			canvas.restore();
        		}

        		//draw the track
        		mTrackDrawable.draw(canvas);

        		canvas.save();
        		// evaluate the coordinates for drawing the Thumb and Text
        		canvas.clipRect(switchInnerLeft, mSwitchTop, switchInnerRight, mSwitchBottom);

        		// mTextColors should not be null, but just in case
        		if (mTextColors != null) {
        			mTextPaint.setColor(mTextColors.getColorForState(getDrawableState(),
        					mTextColors.getDefaultColor()));
        		}
        		// draw the texts for On/Off in reduced alpha mode.
        		if (this.getTargetCheckedState() ^ (mTextOnThumb))
        		    mTextPaint.setAlpha(alpha/4);
        		else 
        		    mTextPaint.setAlpha(alpha);
        		
       	        thumbBoundT = switchInnerTop + 1 * this.getThumbScrollRange() - mThumbExtraMovement;
       	        thumbBoundB = thumbBoundT  + mThumbHeight;
        		canvas.save();
        		canvas.translate(0, (thumbBoundT + thumbBoundB) / 2 - mOnLayout.getHeight()/2 );
                if ((mDrawableOn != null) && (mTextPaint.getAlpha() == alpha)) mDrawableOn.draw(canvas);
        		canvas.translate((mSwitchLeft + mSwitchRight) / 2 - mOnLayout.getWidth() / 2, 0);
        		mOnLayout.draw(canvas);
        		
        		canvas.restore();
 
        		// mTextColors should not be null, but just in case
        		if (mTextColors != null) {
        			mTextPaint.setColor(mTextColors.getColorForState(getDrawableState(),
        					mTextColors.getDefaultColor()));
        		}

        		if (this.getTargetCheckedState() ^ (mTextOnThumb))
        		    mTextPaint.setAlpha(alpha);
        		else 
        		    mTextPaint.setAlpha(alpha/4);
        		
       	        thumbBoundT = switchInnerTop + 0 * this.getThumbScrollRange() - mThumbExtraMovement;
       	        thumbBoundB = thumbBoundT  + mThumbHeight;
        		canvas.save();
        		canvas.translate(0, (thumbBoundT + thumbBoundB) / 2 - mOffLayout.getHeight()/2 );
        		if ((mDrawableOff != null) && (mTextPaint.getAlpha() == alpha))  mDrawableOff.draw(canvas);
        		canvas.translate((mSwitchLeft + mSwitchRight) / 2 - mOffLayout.getWidth() / 2, 0);
        		mOffLayout.draw(canvas);
        		canvas.restore();
        		canvas.restore();
        	}

        	
       	    thumbBoundT = switchInnerTop + thumbPos - mThumbExtraMovement;
       	    thumbBoundB = switchInnerTop + thumbPos - mThumbExtraMovement + mThumbHeight;
        	//Draw the Thumb
        	AppState.log(TAG, "thumbBoundT, thumbBoundB=("+thumbBoundT+","+thumbBoundB+")");
        	AppState.log(TAG, "mSwitchLeft, mSwitchRight=("+mSwitchLeft+","+mSwitchRight+")");
        	mThumbDrawable.setBounds(mSwitchLeft, thumbBoundT, mSwitchRight, thumbBoundB);
        	mThumbDrawable.draw(canvas);

       		mTextPaint.setAlpha(alpha);
        	//Draw the text on the Thumb
        	if (mTextOnThumb) {
        		Layout offSwitchText = getTargetCheckedState() ? mOnLayout : mOffLayout;
        		canvas.save();
        		canvas.translate((mSwitchLeft + mSwitchRight) / 2 - offSwitchText.getWidth() / 2,
        				(thumbBoundT + thumbBoundB) / 2 - offSwitchText.getHeight()/2 );
        				//(switchInnerTop + switchInnerBottom) / 2 - onSwitchText.getHeight()
        		        //- this.mThumbTextPadding);
        		offSwitchText.draw(canvas);
        		canvas.restore();
        	}
        }
        if (mOrientation == HORIZONTAL) {
        	int thumbL = switchInnerLeft;// + mThPad.left;
        	int thumbR = switchInnerLeft + mThumbWidth;// - mThPad.right;
        	int dxOffText = mTextOnThumb ? (thumbL + thumbR)/2
        			- mOffLayout.getWidth()/2 + mTrackTextPadding
        			- mThumbTextPadding //(thumbL+thumbR)/2 already has 2*mThumbTextPadding
        			// so we have to subtract it
        			: switchInnerLeft + mTrackTextPadding;

        	thumbL = thumbL + thumbRange;
        	thumbR = thumbR + thumbRange;
        	int dxOnText = mTextOnThumb ? (thumbL + thumbR)/2 - mOnLayout.getWidth()/2
        			//(thumbL + thumbR)/2 already has the ThumbTextPadding
        			//so we dont have to add it
        			: switchInnerRight - mOnLayout.getWidth() - mTrackTextPadding;

        	int switchVerticalMid = (switchInnerTop + switchInnerBottom) / 2;

        	int thumbBoundL = switchInnerLeft + thumbPos - mThumbExtraMovement;// + mThPad.left 
        	int thumbBoundR = switchInnerLeft + thumbPos + mThumbWidth -mThumbExtraMovement;// - mThPad.right

        	if (mPushStyle) {
        		final int maxTextWidth = Math.max(mOnLayout.getWidth(), mOffLayout.getWidth());
        		//tempBitmap = Bitmap.createBitmap(mSwitchRight - mSwitchLeft, mSwitchBottom - mSwitchTop,
        		//Config.ARGB_8888);
        		//backingLayer = new Canvas(tempBitmap);
        		backingLayer.save();
        		backingLayer.translate(-thumbRange+thumbPos,0);
        		backingLayer.drawBitmap(pushBitmap, 0, 0, null);
        		backingLayer.restore();
        		backingLayer.drawBitmap(maskBitmap, 0, 0, xferPaint);
        		canvas.drawBitmap(tempBitmap, 0, 0, null);
        		mTrackDrawable.draw(canvas);

        		backingLayer.drawColor(0xff000000, Mode.DST_IN);
        		backingLayer.save();
        		backingLayer.translate(-thumbRange+thumbPos,0);
        		backingLayer.translate(mTrackPaddingRect.left, 0);
        		backingLayer.save();
        		backingLayer.translate((maxTextWidth - mOffLayout.getWidth())/2, switchVerticalMid -
        		    mOffLayout.getHeight() / 2);
        		mOffLayout.draw(backingLayer);
        		if (mDrawableOff != null) mDrawableOff.draw(backingLayer);
        		backingLayer.restore();
        		backingLayer.translate(maxTextWidth+ mTrackTextPadding * 2+
        				(maxTextWidth - mOnLayout.getWidth())/2 +
        				mThumbWidth,//+ mThPad.left + mThPad.right,
        				switchVerticalMid - mOnLayout.getHeight() / 2);
        		mOnLayout.draw(backingLayer); 
                if (mDrawableOn != null) mDrawableOn.draw(backingLayer);       		
        		backingLayer.restore();    
        		backingLayer.drawBitmap(maskBitmap, 0, 0, xferPaint);
        		canvas.drawBitmap(tempBitmap, 0, 0, null);

        	} else {
        		if (rightBitmap != null) {
        			canvas.save();
        			if (canvas.getClipBounds(canvasClipBounds)) {
        				canvasClipBounds.left += (mThumbPosition+ mThumbWidth/2);
        				canvas.clipRect(canvasClipBounds);
        			}
        			canvas.drawBitmap(rightBitmap, 0, 0, null);
        			canvas.restore();
        		}

        		if (leftBitmap != null) {
        			canvas.save();
        			if (canvas.getClipBounds(canvasClipBounds)) {
        				canvasClipBounds.right -= (thumbRange- mThumbPosition + mThumbWidth/2);
        				canvas.clipRect(canvasClipBounds);
        			}
        			canvas.drawBitmap(leftBitmap, 0, 0, null);
        			canvas.restore();
        		}

        		//draw the track
        		mTrackDrawable.draw(canvas);

        		// evaluate the coordinates for drawing the Thumb and Text
        		canvas.save();
        		canvas.clipRect(switchInnerLeft, mSwitchTop, switchInnerRight, mSwitchBottom);

        		// mTextColors should not be null, but just in case
        		if (mTextColors != null) {
        			mTextPaint.setColor(mTextColors.getColorForState(getDrawableState(),
        					mTextColors.getDefaultColor()));
        		}

        		// draw the texts for On/Off in reduced alpha mode.
        		mTextPaint.setAlpha(alpha/4);

        		if (getTargetCheckedState()) {
        			canvas.save();
        			canvas.translate(dxOnText, switchVerticalMid - mOnLayout.getHeight() / 2);
        			if (canvas.getClipBounds(canvasClipBounds)) {
        				canvasClipBounds.left += (mThumbPosition+ mThumbWidth/2);
        				canvas.clipRect(canvasClipBounds);
        			}
        			mOnLayout.draw(canvas);
        			if (mDrawableOn != null) mDrawableOn.draw(canvas);
        			canvas.restore();

        			if (mTextOnThumb == false)
        				mTextPaint.setAlpha(alpha);
        			canvas.save();
        			canvas.translate(dxOffText, switchVerticalMid - mOffLayout.getHeight() / 2);
        			if (canvas.getClipBounds(canvasClipBounds)) {
        				canvasClipBounds.right -= (thumbRange- mThumbPosition + mThumbWidth/2);
        				canvas.clipRect(canvasClipBounds);
        			}
        			mOffLayout.draw(canvas);
        			if (mDrawableOff != null) mDrawableOff.draw(canvas);
        			canvas.restore();
        		}
        		else {
        			canvas.save();
        			canvas.translate(dxOffText, switchVerticalMid - mOffLayout.getHeight() / 2);
        			if (canvas.getClipBounds(canvasClipBounds)) {
        				canvasClipBounds.right -= (thumbRange - mThumbPosition + mThumbWidth/2);
        				canvas.clipRect(canvasClipBounds);
        			}
        			mOffLayout.draw(canvas);
        			if (mDrawableOff != null) mDrawableOff.draw(canvas);
        			canvas.restore();

        			if (mTextOnThumb == false)
        				mTextPaint.setAlpha(alpha);
        			canvas.save();
        			canvas.translate(dxOnText, switchVerticalMid - mOnLayout.getHeight() / 2);
        			if (canvas.getClipBounds(canvasClipBounds)) {
        				canvasClipBounds.left += (mThumbPosition+ mThumbWidth/2);
        				canvas.clipRect(canvasClipBounds);
        			}
        			mOnLayout.draw(canvas);
        			if (mDrawableOn != null) mDrawableOn.draw(canvas);
        			canvas.restore();
        		}
        		canvas.restore();
        	}

        	//Draw the Thumb
        	//AppState.log(TAG, "thumbBoundL, thumbBoundR=("+thumbBoundL+","+thumbBoundR+")");
        	mThumbDrawable.setBounds(thumbBoundL, mSwitchTop, thumbBoundR, mSwitchBottom);
        	mThumbDrawable.draw(canvas);

        	//Draw the text on the Thumb
        	if (mTextOnThumb) {
        		mTextPaint.setAlpha(alpha);
        		Layout onSwitchText = getTargetCheckedState() ? mOnLayout : mOffLayout;
        		canvas.save();
        		canvas.translate((thumbBoundL + thumbBoundR) / 2 - onSwitchText.getWidth() / 2,
        				(switchInnerTop + switchInnerBottom) / 2 - onSwitchText.getHeight() / 2);
        		onSwitchText.draw(canvas);
        		canvas.restore();
        	}
        }
    }

    @Override
    public int getCompoundPaddingRight() {
        int padding = super.getCompoundPaddingRight() + mSwitchWidth;
        if (!TextUtils.isEmpty(getText())) {
            padding += mSwitchPadding;
        }
        return padding;
    }

    @Override
    public int getCompoundPaddingTop() {
        int padding = super.getCompoundPaddingTop() + mSwitchHeight;
        if (!TextUtils.isEmpty(getText())) {
            padding += mSwitchPadding;
        }
        return padding;
    }

    private int getThumbScrollRange() {
        if (mTrackDrawable == null) {
            return 0;
        }
        
        int range=0;
        if (mOrientation == VERTICAL)
            range = mSwitchHeight - mThumbHeight - mTrackPaddingRect.top - mTrackPaddingRect.bottom +
            mThumbExtraMovement* 2;
        if (mOrientation == HORIZONTAL) 
            range = mSwitchWidth - mThumbWidth - mTrackPaddingRect.left - mTrackPaddingRect.right +
            mThumbExtraMovement* 2;
        if (this.mPushStyle)
          	range += this.mTrackTextPadding*2;
        //AppState.log(TAG,"getThumbScrollRange() = "+ range);
        return range;
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();

        int[] myDrawableState = getDrawableState();

        // Set the state of the Drawable
        // Drawable may be null when checked state is set from XML, from super constructor
        if (mThumbDrawable != null) mThumbDrawable.setState(myDrawableState);
        if (mTrackDrawable != null) mTrackDrawable.setState(myDrawableState);

        invalidate();
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        return super.verifyDrawable(who) || who == mThumbDrawable || who == mTrackDrawable;
    }
    
    /**
     * Set the background color for the thumb track.
     *
     * @param colorId The color id
     */
    protected void setThumbTrackBackgroundColor(int colorId) {
    	AppState.logX(TAG, String.format("setThumbTrackBackgroundColor colorId = %x", colorId));
    	
    	mTrackDrawable = generateThumbTrack(colorId);
        requestLayout();
        invalidate();
    }
    
    private static final float THUMB_TRACK_CORNER_RADIUS = 1f;
    protected Drawable generateThumbTrack(int colorId) {
    	GradientDrawable thumbTrack = new GradientDrawable();
    	float[] radii = {
    	    THUMB_TRACK_CORNER_RADIUS, THUMB_TRACK_CORNER_RADIUS,
    	    THUMB_TRACK_CORNER_RADIUS, THUMB_TRACK_CORNER_RADIUS,
    	    THUMB_TRACK_CORNER_RADIUS, THUMB_TRACK_CORNER_RADIUS,
    	    THUMB_TRACK_CORNER_RADIUS, THUMB_TRACK_CORNER_RADIUS };
    	
    	thumbTrack.setColor(colorId);
    	thumbTrack.setGradientType(GradientDrawable.RECTANGLE);
    	thumbTrack.setDither(true);
    	thumbTrack.setGradientCenter(0.0f, 0.45f);
    	thumbTrack.setCornerRadii(radii);
    	
    	return thumbTrack;
    }
}